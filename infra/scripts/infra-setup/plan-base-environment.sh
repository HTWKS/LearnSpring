#!/usr/bin/env bash

set -euo pipefail

BASEDIR=$(dirname $0)
SCRIPTS_DIR="${BASEDIR}/.."

source "${SCRIPTS_DIR}"/config.sh
source "${SCRIPTS_DIR}"/shared.sh # functions

ENVIRONMENT="${1}"

if [[ -z "${ENVIRONMENT}" ]]; then
  echo "Usage: $0 <environment>"
  exit 1
fi


pushd "$SCRIPTS_DIR"/../infra/base_environment/
terraform_init_and_select_workspace "$(terraform_workspace)"
terraform plan
success "Planned base_environment for $(terraform_workspace)"
popd
