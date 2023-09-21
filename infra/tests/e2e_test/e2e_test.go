package test

import (
	"fmt"
	testHelper "infra-test/lib"
	"strings"
	"testing"
	"time"

	http_helper "github.com/gruntwork-io/terratest/modules/http-helper"
	"github.com/gruntwork-io/terratest/modules/random"
	test_structure "github.com/gruntwork-io/terratest/modules/test-structure"
)

const supportedTestEnv = "e2e" // see: shared.sh

/*
Terraform was only used to extract the ALB URL to confirm it is reachable, and to do testing with
simple nginx service (mock App).

Here we will be mainly blackbox testing the scripts, functional testing the infra codebase as a unit.
  - Terraform `base_environment` is defining:
  - the networking resources and necessary IAM role resources for ECS task
  - configuring ALB with ACM (https ready) and getting ECS cluster ready to be used
  - Terraform `app_deployment` is defining ecs service and task definition.

We will be testing scripts:
  - create-all-base-environments.sh
  - build-and-push-image.sh (app deployment)
  - deploy.sh (app deployment)
*/
func TestE2eWithAppEnvironmentReadiness(t *testing.T) {
	appEnvironment, terraformWorkspaceName := testHelper.ResolveFromEnvVariables(t, supportedTestEnv)

	defer test_structure.RunTestStage(t, "destroy_base_environment", func() {
		testHelper.DestroyBaseEnvironment(t)
	})

	test_structure.RunTestStage(t, "create_all_base_environments", func() {
		testHelper.CreateAllBaseEnvironmentsAndConfirmAlbIsReachable(t, terraformWorkspaceName)
	})

	test_structure.RunTestStage(t, "run_and_check_build_and_deploy", func() {
		runAppBuildAndDeployApp(t, appEnvironment, terraformWorkspaceName)
		verifyAppDeployment(t, terraformWorkspaceName)
	})
}

func runAppBuildAndDeployApp(t *testing.T, appEnvironmentToDeploy string, ecrImageTagPrefix string) {
	ecrImageTagWithBuildId := fmt.Sprintf("%s-%s", ecrImageTagPrefix, random.UniqueId())

	testHelper.RunScript(t, "scripts/build-and-push-image.sh",
		[]string{"client", ecrImageTagWithBuildId})
	testHelper.RunScript(t, "scripts/build-and-push-image.sh",
		[]string{"server", ecrImageTagWithBuildId})

	testHelper.RunScript(t, "scripts/deploy.sh",
		[]string{appEnvironmentToDeploy, ecrImageTagWithBuildId, ecrImageTagWithBuildId})
}

func verifyAppDeployment(t *testing.T, terraformWorkspaceName string) {
	albBaseUrl := testHelper.GetTerraformOutput(t, terraformWorkspaceName, "infra/app_deployment", "base_url")

	http_helper.HttpGetWithRetryWithCustomValidation(t, albBaseUrl+"/", nil, 5, 5*time.Second, func(statusCode int, body string) bool {
		return statusCode == 200 && strings.Contains(body, "react")
	})
	http_helper.HttpGetWithRetryWithCustomValidation(t, albBaseUrl+"/api/v1/about", nil, 5, 5*time.Second, func(statusCode int, body string) bool {
		return statusCode == 200 && strings.Contains(body, "book store")
	})
}
