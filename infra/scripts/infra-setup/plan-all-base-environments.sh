#!/bin/bash

set -euo pipefail

BASEDIR=$(dirname $0)
SCRIPTS_DIR="${BASEDIR}/.."

source "${SCRIPTS_DIR}"/config.sh
source "${SCRIPTS_DIR}"/shared.sh # functions

question "You're about to review the terraform plan for all environments for Launch batch '${BATCH_NUMBER}'"
for ENVIRONMENT in ${ENVIRONMENTS}; do
	echo "  - ${ENVIRONMENT}"
done
wait_for_user_confirmation

for ENVIRONMENT in ${ENVIRONMENTS}; do
    "$BASEDIR"/plan-base-environment.sh "${ENVIRONMENT}"
    wait_for_user_confirmation
done

success "Done!"