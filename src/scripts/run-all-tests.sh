set -euo pipefail
BASEDIR=$(dirname $0)
pushd ${BASEDIR}
./run-unit-tests.sh
./run-e2e-tests.sh
