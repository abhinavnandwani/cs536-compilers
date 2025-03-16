#!/bin/bash

# Define paths
TEST_DIR="$(pwd)"                  # Current directory where test files exist
PARENT_DIR="$(dirname "$TEST_DIR")" # One level up where the Makefile is
OUTPUT_DIR="$TEST_DIR/tests/tests-out"
RESULTS_DIR="$TEST_DIR/test_results"

# Create directories for results
mkdir -p "$OUTPUT_DIR"
mkdir -p "$RESULTS_DIR"

# Run make once before running the tests
echo "Compiling..."
(cd "$PARENT_DIR" && make > "$RESULTS_DIR/make.log" 2>&1)

# Loop through all .bach test files, ensuring we don't process .out files
for test_file in "$TEST_DIR"/*.bach; do
    [[ "$test_file" == *".out.bach" ]] && continue  # Skip output files

    test_name=$(basename "$test_file" .bach)  # Extract test number (e.g., 1, 2, 3)
    # Copy test file to test.bach in parent directory
    cp "$test_file" "$PARENT_DIR/test.bach"

    # Run make test from the parent directory and save output
    (cd "$PARENT_DIR" && make test > "$RESULTS_DIR/$test_name.bach.log" 2>&1)

    # Move test output if produced
    if [ -f "$PARENT_DIR/test.out" ]; then
        mv "$PARENT_DIR/test.out" "$OUTPUT_DIR/$test_name.out"
        echo "$test_name.bach: Test completed, output saved."
    else
        echo "$test_name.bach: ❌ Test failed (check logs: $RESULTS_DIR/$test_name.bach.log)."
    fi
done

# Run make clean only once at the end
echo "Cleaning up..."
(cd "$PARENT_DIR" && make clean > /dev/null 2>&1)

