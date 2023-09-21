#!/bin/bash
set -euo pipefail

apply_terraform_and_set_variables(){
  terraform apply -auto-approve
  AWS_ECR_PUBLIC_URI=$(terraform output -raw ecr_public_repository_uri)
  AWS_ECR_PUBLIC_NAME=$(terraform output -raw ecr_public_repository_name)
  BUILD_TAG=latest
}

build_and_push_docker_image(){
  aws ecr-public get-login-password --region us-east-1 --profile "$AWS_PROFILE" | docker login --username AWS --password-stdin "$AWS_ECR_PUBLIC_URI"
  docker build -t "$AWS_ECR_PUBLIC_NAME" -f Dockerfile --target run-app .
  docker tag "$AWS_ECR_PUBLIC_NAME":$BUILD_TAG "$AWS_ECR_PUBLIC_URI":$BUILD_TAG
  docker push "$AWS_ECR_PUBLIC_URI":$BUILD_TAG
}

BASEDIR=$(dirname "$0")
source "${BASEDIR}"/config.sh

pushd "${BASEDIR}"/../base_environment
apply_terraform_and_set_variables
pushd ../../
build_and_push_docker_image
popd
popd