#!/bin/bash

set -euo pipefail

BASEDIR=$(dirname $0)
SCRIPTS_DIR="${BASEDIR}/.."
source "${SCRIPTS_DIR}"/config.sh
source "${SCRIPTS_DIR}"/shared.sh

question "You're about to DELETE ALL environments for Launch batch '${BATCH_NUMBER}'"
for ENVIRONMENT in ${ENVIRONMENTS}; do
	echo "  - ${ENVIRONMENT}"
done
wait_for_user_confirmation

for ENVIRONMENT in ${ENVIRONMENTS}; do
  "${SCRIPTS_DIR}"/un-deploy.sh "${ENVIRONMENT}"
  "${BASEDIR}"/destroy-base-environment.sh "${ENVIRONMENT}"
done

success "Done!"