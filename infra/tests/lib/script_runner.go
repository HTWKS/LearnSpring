package lib

import (
	"github.com/gruntwork-io/terratest/modules/shell"
	"path/filepath"
	"testing"
)

func RunScript(t *testing.T, filePathRelativeFromProjectRoot string, args []string) {
	rootPath, _ := filepath.Abs(projectRootDir)
	shell.RunCommand(t, shell.Command{
		Command: filepath.Join(rootPath, filePathRelativeFromProjectRoot),
		Args:    args,
	})
}

func RunScriptWithoutArgs(t *testing.T, filePathRelativeFromProjectRoot string) {
	rootPath, _ := filepath.Abs(projectRootDir)
	shell.RunCommand(t, shell.Command{
		Command: filepath.Join(rootPath, filePathRelativeFromProjectRoot),
		Args:    []string{},
	})
}
