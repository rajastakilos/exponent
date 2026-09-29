#!/bin/sh
set -eu

PROJECT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$PROJECT_DIR"
mkdir -p build/test-classes
javac -d build/test-classes src/*.java tests/*.java

for test_file in tests/*Test.java; do
    test_class=$(basename "$test_file" .java)
    java -cp build/test-classes "$test_class"
done
