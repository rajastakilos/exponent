#!/bin/sh
set -eu

PROJECT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
mkdir -p "$PROJECT_DIR/build/classes"
javac -d "$PROJECT_DIR/build/classes" "$PROJECT_DIR"/src/*.java
exec java -cp "$PROJECT_DIR/build/classes" Main "$@"
