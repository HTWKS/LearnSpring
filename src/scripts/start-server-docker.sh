#!/bin/bash

set -euo pipefail

java -jar -Dserver.port="$SERVER_PORT" app.jar