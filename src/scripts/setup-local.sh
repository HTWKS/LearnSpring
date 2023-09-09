#!/bin/bash

set -euo pipefail

BASEDIR=$(dirname "$0")
pushd "${BASEDIR}"/../../

printf "\360\237\215\272\t Running npm install...  \n"
npm install
npm audit --fix

printf "\360\237\215\272\t Running npm prepare...  \n"
npm run-script prepare
