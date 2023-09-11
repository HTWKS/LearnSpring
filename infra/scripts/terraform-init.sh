#!/bin/bash
set -euo pipefail

BASEDIR=$(dirname "$0")
. "${BASEDIR}"/constants.env
pushd "${BASEDIR}"/../
AWS_PROFILE=AWS_PROFILE terraform apply