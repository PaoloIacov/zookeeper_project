#!/bin/bash
PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
EVOSUITE_JAR="${PROJECT_ROOT}/evosuite/evosuite-1.1.0.jar"
CLASSES_DIR="${PROJECT_ROOT}/zookeeper-server/target/classes"
TEST_CLASSES_DIR="${PROJECT_ROOT}/zookeeper-server/target/test-classes"
OUTPUT_DIR="${PROJECT_ROOT}/zookeeper-server/src/test-evosuite/java"

cd "$PROJECT_ROOT"
# Filter out ALL problematic jars
DEPS_CP=$(mvn -pl zookeeper-server dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q 2>/dev/null | tr ':' '\n' | grep -v 'logback' | grep -v 'jetty' | grep -v 'jackson' | grep -v 'bouncycastle' | grep -v 'slf4j' | grep -v 'spotbugs' | grep -v 'netty' | grep -v 'jline' | tr '\n' ':')
CLASSPATH="${CLASSES_DIR}:${TEST_CLASSES_DIR}:${DEPS_CP}"

JAVA_CMD="${PROJECT_ROOT}/zulu8.78.0.19-ca-jdk8.0.412-macosx_aarch64/bin/java"

$JAVA_CMD -jar "$EVOSUITE_JAR" \
    -class org.apache.zookeeper.ClientCnxn \
    -projectCP "$CLASSPATH" \
    -Dsearch_budget=30 \
    -base_dir "${OUTPUT_DIR}"
