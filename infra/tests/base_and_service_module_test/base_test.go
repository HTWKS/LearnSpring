package test

import (
	testHelper "infra-test/lib"
	"path/filepath"
	"sort"
	"strings"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go/service/cloudwatchlogs"
	"github.com/aws/aws-sdk-go/service/ecs"
	"github.com/gruntwork-io/terratest/modules/aws"
	http_helper "github.com/gruntwork-io/terratest/modules/http-helper"
	"github.com/gruntwork-io/terratest/modules/logger"
	"github.com/gruntwork-io/terratest/modules/terraform"
	test_structure "github.com/gruntwork-io/terratest/modules/test-structure"
	"github.com/stretchr/testify/require"
)

const supportedTestEnv = "itest" // see: shared.sh

/*
Terraform was only used to extract the ALB URL to confirm it is reachable, and to do testing with
simple nginx service (mock App).

Here we will be mainly blackbox testing the scripts, functional testing the infra codebase as a unit.
- `base_environment` is defining:
  - the networking resources and necessary IAM role resources for ECS task
  - configuring ALB with ACM (https ready) and getting ECS cluster ready to be used

- `app_environment` is defining:
  - ecs service and task definition
*/
func TestBaseEnvironmentAndServiceModule(t *testing.T) {
	_, terraformWorkspaceName := testHelper.ResolveFromEnvVariables(t, supportedTestEnv)

	defer test_structure.RunTestStage(t, "destroy_base_environment", func() {
		testHelper.DestroyBaseEnvironment(t)
	})

	test_structure.RunTestStage(t, "create_all_base_environments", func() {
		testHelper.CreateAllBaseEnvironmentsAndConfirmAlbIsReachable(t, terraformWorkspaceName)
	})

	t.Run("ServiceModuleFunctionalTesting", func(t *testing.T) {
		absolutePath, _ := filepath.Abs("./service_module_example")
		terraformDirRelativePath := testHelper.ResolveRelativePathStartingFromProjectRoot(t, absolutePath)

		defer test_structure.RunTestStage(t, "destroy_service_module", func() {
			testHelper.DestroyTerraformStack(t, terraformWorkspaceName, terraformDirRelativePath)
			deregisterTaskDefinitions(t, terraformWorkspaceName)
		})

		test_structure.RunTestStage(t, "apply_and_check_service_module", func() {
			applyAndCheckServiceModule(t, terraformWorkspaceName, terraformDirRelativePath)
		})
	})
}

func applyAndCheckServiceModule(t *testing.T, terraformWorkspaceName string, serviceModuleTerraformDir string) {
	terraformOptions := testHelper.InitTerraformStack(t, terraformWorkspaceName, serviceModuleTerraformDir)
	terraform.Apply(t, terraformOptions)

	serviceUrl := terraform.Output(t, terraformOptions, "some_service_url")
	baseUrl := terraform.Output(t, terraformOptions, "base_url")
	logGroupName := terraform.Output(t, terraformOptions, "some_service_log_group_name")

	http_helper.HttpGetWithRetryWithCustomValidation(t, serviceUrl, nil, 20, 5*time.Second, func(statusCode int, body string) bool {
		return statusCode == 404 && strings.Contains(body, "nginx")
	})
	http_helper.HttpGetWithRetry(t, baseUrl+"/bar", nil, 200, "hello from some service under base path", 20, 5*time.Second)

	latestLogStream := latestCloudwatchLogStream(t, logGroupName)
	entries := aws.GetCloudWatchLogEntries(t, "ap-southeast-1", *latestLogStream.LogStreamName, logGroupName)
	require.Contains(t, strings.Join(entries, "\n"), "nginx")

	latestEvent := latestCloudwatchLogEvent(t, logGroupName, latestLogStream)

	require.WithinDurationf(t, time.Now(), time.Unix(*latestEvent.Timestamp/1000, 0), 5*time.Minute,
		"Expecting last log line to be not more than 5min ago")
}

func deregisterTaskDefinitions(t *testing.T, environmentName string) {
	// Task definitions should normally be cleaned up by terraform,
	// however, sometimes they get left over so adding this to clean up after ourselves

	ecsClient := aws.NewEcsClient(t, "ap-southeast-1")
	taskDefinitions, err := ecsClient.ListTaskDefinitions(&ecs.ListTaskDefinitionsInput{
		FamilyPrefix: &environmentName,
	})
	require.NoError(t, err)
	for _, taskDefinitionArn := range taskDefinitions.TaskDefinitionArns {
		logger.Log(t, "Cleaning up task definition %s", taskDefinitionArn)
		_, err := ecsClient.DeregisterTaskDefinition(&ecs.DeregisterTaskDefinitionInput{
			TaskDefinition: taskDefinitionArn,
		})
		require.NoError(t, err)
	}
}

func latestCloudwatchLogEvent(t *testing.T, logGroupName string, latestLogStream *cloudwatchlogs.LogStream) *cloudwatchlogs.OutputLogEvent {
	startFromHead := false
	cloudWatchLogsClient2 := aws.NewCloudWatchLogsClient(t, "ap-southeast-1")
	events, err := cloudWatchLogsClient2.GetLogEvents(&cloudwatchlogs.GetLogEventsInput{
		LogGroupName:  &logGroupName,
		LogStreamName: latestLogStream.LogStreamName,
		StartFromHead: &startFromHead,
	})
	require.NoError(t, err)
	require.Greater(t, len(events.Events), 0)
	return events.Events[0]
}

func latestCloudwatchLogStream(t *testing.T, logGroupName string) *cloudwatchlogs.LogStream {
	cloudWatchLogsClient := aws.NewCloudWatchLogsClient(t, "ap-southeast-1")
	streams, err := cloudWatchLogsClient.DescribeLogStreams(&cloudwatchlogs.DescribeLogStreamsInput{
		LogGroupName: &logGroupName,
	})
	require.NoError(t, err)
	require.Greater(t, len(streams.LogStreams), 0)
	sort.Slice(streams.LogStreams, func(i, j int) bool {
		return *streams.LogStreams[i].LastEventTimestamp > *streams.LogStreams[j].LastEventTimestamp
	})
	latestLogStream := streams.LogStreams[0]
	return latestLogStream
}
