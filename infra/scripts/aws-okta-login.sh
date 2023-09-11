#!/usr/bin/env bash

set -euo pipefail

BASEDIR=$(dirname "$0")
source "${BASEDIR}"/set-aws-environment.sh
aws sso login --profile "${AWS_PROFILE}"