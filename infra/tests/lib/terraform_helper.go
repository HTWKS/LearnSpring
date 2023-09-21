package lib

import (
	"github.com/gruntwork-io/terratest/modules/terraform"
	"path/filepath"
	"testing"
)

func InitTerraformStack(t *testing.T, workspaceName string, terraformDirPathRelativeFromProjectRoot string) *terraform.Options {
	rootPath, _ := filepath.Abs(projectRootDir)
	terraformOptionsEnvironment := terraform.WithDefaultRetryableErrors(t, &terraform.Options{
		TerraformDir: filepath.Join(rootPath, terraformDirPathRelativeFromProjectRoot),
		Vars:         map[string]interface{}{},
		NoColor:      true,
	})

	terraform.Init(t, terraformOptionsEnvironment)
	terraform.WorkspaceSelectOrNew(t, terraformOptionsEnvironment, workspaceName)
	return terraformOptionsEnvironment
}

func GetTerraformOutput(t *testing.T, workspaceName string, terraformDirPathRelativeFromProjectRoot string, key string) string {
	terraformOptions := InitTerraformStack(t, workspaceName, terraformDirPathRelativeFromProjectRoot)
	return terraform.Output(t, terraformOptions, key)
}

func DestroyTerraformStack(t *testing.T, workspaceName string, terraformDirPathRelativeFromProjectRoot string) {
	terraformOptions := InitTerraformStack(t, workspaceName, terraformDirPathRelativeFromProjectRoot)

	terraform.Destroy(t, terraformOptions)
	terraform.WorkspaceDelete(t, terraformOptions, workspaceName)
}
