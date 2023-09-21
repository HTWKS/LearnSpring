package lib

import (
	"fmt"
	"os"
	"path/filepath"
	"strings"
	"testing"
)

const envKeyEnvironments = "ENVIRONMENTS"
const terraformWorkspaceNamingPattern = "learn-spring-%s"

func ResolveRelativePathStartingFromProjectRoot(t *testing.T, absolutePath string) string {
	rootPath, err := filepath.Abs(projectRootDir)
	relativePath, err := filepath.Rel(rootPath, absolutePath)
	if err != nil {
		t.FailNow()
	}
	return relativePath
}

func ResolveFromEnvVariables(t *testing.T, supportedTestEnv string) (string, string) {
	environment := resolveEnvironments(t, supportedTestEnv)
	terraformWorkspaceName := fmt.Sprintf(terraformWorkspaceNamingPattern, environment)

	return environment, terraformWorkspaceName
}

func resolveEnvironments(t *testing.T, supportedTestEnv string) string {
	environments, set := os.LookupEnv(envKeyEnvironments)
	testEnv := ""

	if set {
		envArr := strings.Split(environments, " ")
		testEnv = envArr[0]
		if len(envArr) != 1 {
			println(fmt.Sprintf("More than 1 environment is given, %s. Currently this test is only supporting environment %s", envArr, testEnv))
		}
	} else {
		println(fmt.Sprintf("using default environment %s as ENVIRONMENTS is not set!", supportedTestEnv))
		testEnv = supportedTestEnv                             // FIXME: used to passed as first param to deploy.sh, so we can deploy app to the first environment only.
		err := os.Setenv(envKeyEnvironments, supportedTestEnv) // use by most of the scripts.sh through shared.sh
		if err != nil {
			t.Fatal("ENVIRONMENTS is not set and program couldn't set it successfully. Exit as pre-requisite is not met for scripts.")
		}
	}

	if testEnv != supportedTestEnv {
		t.Fatalf("%s is not a supported test environment. Please check usages in the script shared.sh and config.env.", testEnv)
	}
	return testEnv
}
