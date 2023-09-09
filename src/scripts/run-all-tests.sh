#!/bin/bash

set -euo pipefail
BASEDIR=$(dirname "$0")

printf "\360\237\215\272\t Running all tests...  \n"
pushd "${BASEDIR}"/../../
docker-compose -f "docker-compose-e2e.yml" up --build --abort-on-container-exit && docker-compose -f "docker-compose-e2e.yml" down
