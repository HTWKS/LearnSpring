#!/bin/bash

set -euo pipefail

if [[ -z "${1}" ]]; then
  echo "Usage: $0 <env>"
  exit 1
fi

BASEDIR=$(dirname $0)
SCRIPTS_DIR="${BASEDIR}/.."
source "${SCRIPTS_DIR}"/config.sh
source "${SCRIPTS_DIR}"/shared.sh

ENVIRONMENT=$1

pushd "${BASEDIR}"/../../infra/base_environment

step "Deleting base_environment for '$(terraform_workspace)'"
terraform_init_and_select_workspace "$(terraform_workspace)"
terraform destroy -auto-approve
terraform_delete_workspace "$(terraform_workspace)"

popd