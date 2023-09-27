package lib

import (
	"testing"
	"time"

	http_helper "github.com/gruntwork-io/terratest/modules/http-helper"
)

const projectRootDir = "../../"

func CreateAllBaseEnvironmentsAndConfirmAlbIsReachable(t *testing.T, terraformWorkspaceName string) {
	RunScriptWithoutArgs(t, "scripts/infra-setup/create-all-base-environments.sh")

	albBaseUrl := GetTerraformOutput(t, terraformWorkspaceName, "base_environment", "alb_base_url")

	http_helper.HttpGetWithRetryWithCustomValidation(t, albBaseUrl, nil, 12, 5*time.Second, func(statusCode int, body string) bool {
		return statusCode == 200
	})
}

func DestroyBaseEnvironment(t *testing.T) {
	RunScriptWithoutArgs(t, "scripts/infra-setup/undeploy-and-destroy-all-infra.sh")
}
