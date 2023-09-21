#!/usr/bin/env bash

set -euo pipefail

BASEDIR=$(dirname "$0")
source "${BASEDIR}"/config.sh
aws sso login --profile "${AWS_PROFILE}"