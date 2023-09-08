set -euo pipefail

BASEDIR=$(dirname $0)
pushd ${BASEDIR}/../../

printf "\360\237\215\272\t Running e2e tests...  \n"
./gradlew test --tests '**thoughtworks.e2e**'