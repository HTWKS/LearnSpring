#!/bin/bash

set -euo pipefail

BASEDIR=$(dirname $0)
SCRIPTS_DIR="${BASEDIR}/.."

source "${SCRIPTS_DIR}"/config.sh
source "${SCRIPTS_DIR}"/shared.sh # functions

question "You're about to create environments for Launch batch '${BATCH_NUMBER}', and deploy the current codebase"
for ENVIRONMENT in ${ENVIRONMENTS}; do
	echo "  - ${ENVIRONMENT}"
done
wait_for_user_confirmation

aws_ecr_login
success "All auth'ed up!"

step "Build and push docker images *if necessary*"
docker_build_if_necessary client initial-image
docker_build_if_necessary server initial-image
success "Docker images are available"

for ENVIRONMENT in ${ENVIRONMENTS}; do
    "$BASEDIR"/apply-base-environment.sh "${ENVIRONMENT}"
    "$SCRIPTS_DIR"/deploy.sh "${ENVIRONMENT}" initial-image initial-image
done

success "Done!"
