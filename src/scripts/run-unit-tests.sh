set -euo pipefail

BASEDIR=$(dirname $0)
pushd ${BASEDIR}/../../

printf "\360\237\215\272\t Running unit tests...  \n"
./gradlew test --tests '**thoughtworks.main**'
./gradlew test --tests '**thoughtworks.unit**'