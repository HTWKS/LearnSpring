#!/usr/bin/env bash

set -euo pipefail

step () {
  printf "🔷 $1 \n"
}

question () {
  printf "❓ $1 \n"
}

warn () {
  printf "⛔ $1 \n"
}

success () {
  printf "\n🍺 $1 \n\n"
}

wait_for_user_confirmation() {
  for E_TEST in $TEST_ENVIRONMENTS; do
    if [[ "$E_TEST" == "${ENVIRONMENT}" ]]; then
      return 0
    fi
  done

  echo "Press [ENTER] to continue"
  read -r _
}

environment_exists () {
  for E_TEST in $ENVIRONMENTS; do
    if [[ "$E_TEST" == "$1" ]]; then
      return 0
    fi
  done

  return 1
}

terraform_workspace () {
  echo "learn-spring-${ENVIRONMENT}"
}

aws_account_number () {
  echo "$(aws sts get-caller-identity --query 'Account' --output text)"
}

aws_ecr_login () {
  step "Docker AWS/ECR Login"
  aws ecr get-login-password | docker login --username AWS --password-stdin "$(aws_account_number).dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com"
}

terraform_init_and_select_workspace () {
  WORKSPACE="${1}"

  if [[ -z "${WORKSPACE}" ]]; then
    echo "Usage: $0 <workspace>"
    return 1
  fi

  terraform init
  if [[ $(terraform workspace show) != "${WORKSPACE}" ]]; then
    # select doesnt actually need quotes.
    # vim syntax highlighting was screwing up
    step "select|create workspace '${WORKSPACE}'"
    terraform workspace "select" "${WORKSPACE}" > /dev/null || terraform workspace new "${WORKSPACE}" > /dev/null
  else
    step "workspace '${WORKSPACE}' already selected"
  fi
}

terraform_delete_workspace() {
    WORKSPACE="${1}"

    if [[ -z "${WORKSPACE}" ]]; then
      echo "Usage: $0 <workspace>"
      return 1
    fi

    question "Going to delete the terraform workspaces for '${WORKSPACE}'."
    echo "Hit Ctrl-C to cancel"
    wait_for_user_confirmation

    step "deleting workspace '${WORKSPACE}"
    terraform workspace "select" default
    terraform workspace delete ${WORKSPACE}
}

docker_image () {
  SERVER_OR_CLIENT="${1}"
  TAG="${2}"
  echo "$(aws_account_number).dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${PROJECT_NAME}-${SERVER_OR_CLIENT}:${TAG}"
}

ecr_docker_image_exists_remotely () {
  SERVER_OR_CLIENT="${1}"
  TAG="${2}"

  echo "$(aws ecr describe-images --region $AWS_DEFAULT_REGION --repository-name $PROJECT_NAME-$SERVER_OR_CLIENT --image-ids imageTag=$TAG)"
}

docker_build_if_necessary () {
  SERVER_OR_CLIENT="${1}"
  TAG="${2}"

  if [[ -z "${SERVER_OR_CLIENT}" ]] && [[ -z "${TAG}" ]]; then
    echo "Usage: $0 <server|client> <tag>"
    return 1
  fi

  set +e
  ecr_docker_image_exists_remotely $SERVER_OR_CLIENT $TAG
	set -e
  if [[ "$?" != "0" ]]; then
    step "Build & push $SERVER_OR_CLIENT image"
    ./scripts/build-and-push-image.sh $SERVER_OR_CLIENT $TAG
	else
		step "Not necessary. skip."
  fi
}
