#!/bin/bash

set -euo pipefail

BASEDIR=$(dirname $0)
SCRIPTS_DIR="${BASEDIR}/.."

source "${SCRIPTS_DIR}"/config.sh
source "${SCRIPTS_DIR}"/shared.sh # functions

question "You're about to create environments for learn-spring, and deploy the current codebase"
for ENVIRONMENT in ${ENVIRONMENTS}; do
	echo "  - ${ENVIRONMENT}"
done
wait_for_user_confirmation

for ENVIRONMENT in ${ENVIRONMENTS}; do
    "$BASEDIR"/apply-base-environment.sh "${ENVIRONMENT}"
    wait_for_user_confirmation
done

success "Done!"
