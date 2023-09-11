#!/usr/bin/env bash

set -euo pipefail
BASEDIR=$(dirname "$0")
source "${BASEDIR}"/set-aws-environment.sh
aws configure set --profile "${AWS_PROFILE}" sso_start_url "https://thoughtworks-sso.awsapps.com/start"
aws configure set --profile "${AWS_PROFILE}" sso_region "eu-central-1"
aws configure set --profile "${AWS_PROFILE}" sso_account_id "160071257600"
aws configure set --profile "${AWS_PROFILE}" sso_role_name "PowerUserPlusRole"
