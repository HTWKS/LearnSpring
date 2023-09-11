#!/usr/bin/env bash

set -euo pipefail

BASEDIR=$(dirname "$0")
. "${BASEDIR}"/constants.env
aws sso login --profile AWS_PROFILE