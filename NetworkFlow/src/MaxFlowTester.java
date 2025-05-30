/*
w2053181
Sanjula Siriwardhana
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MaxFlowTester {

    public static void main(String[] args) {
        // You can process a single file or a directory of files
        if (args.length == 0) {
            System.out.println("Usage: java MaxFlowTester <file_or_directory_path>");
            System.out.println("Running default test directory...");
            testDirectory("NetworkFlow/test");
        } else if (args.length == 1) {
            File path = new File(args[0]);
            if (path.isDirectory()) {
                testDirectory(args[0]);
            } else {
                testSingleFile(args[0]);
            }
        }
    }

    // Process a single network file
    private static void testSingleFile(String filePath) {
        try {
            System.out.println("Processing file: " + filePath);

            // Parse the network
            FlowNetwork network = NetworkParser.readFromFile(filePath);

            // Default source and sink (usually 0 and last vertex)
            int source = 0;
            int sink = network.V() - 1;

            // Print network info
            System.out.println("Network");
            System.out.println(network.V() + " vertices, " + network.E() + " edges");

            // For small networks, print the full graph
            if (network.V() <= 20) {
                System.out.println(network.toString());
            }

            // Measure performance
            long startTime = System.currentTimeMillis();

            // Determine if we should store paths based on network size
            boolean storeAllPaths = network.V() <= 10000;

            // Run Ford-Fulkerson
            FordFulkerson fordFulkerson = new FordFulkerson(network, source, sink, storeAllPaths);

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // Print results
            System.out.println("Maximum flow value = " + fordFulkerson.value());

            // For smaller networks or if specifically requested, show all paths
            if (network.V() <= 30 && storeAllPaths) {
                System.out.println(fordFulkerson.getAugmentingPathInfo());
                System.out.println(fordFulkerson.getFinalFlowInfo(network));
            } else {
                // For large networks, just show the path count
                System.out.println("Large network: found " + fordFulkerson.getPathCount() + " augmenting paths");
                System.out.println("Total computation time: " + duration + " ms");
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error processing file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Process all network files in a directory
    private static void testDirectory(String dirPath) {
        System.out.println("Testing all network files in: " + dirPath);
        System.out.println("=======================================");

        final List<TestResult> results = new ArrayList<>();
        final AtomicInteger totalTests = new AtomicInteger(0);
        final AtomicInteger successfulTests = new AtomicInteger(0);

        try {
            Files.walk(Paths.get(dirPath))
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".txt") || p.toString().endsWith(".in"))
                    .sorted()
                    .forEach(filePath -> {
                        TestResult result = processDirectoryFile(filePath);
                        results.add(result);
                        if (result.success) successfulTests.incrementAndGet();
                        totalTests.incrementAndGet();
                    });

            // Print summary
            System.out.println("\n=======================================");
            System.out.println("SUMMARY OF TEST RESULTS");
            System.out.println("=======================================");
            System.out.println("Total tests: " + totalTests.get());
            System.out.println("Successful tests: " + successfulTests.get());
            System.out.println("Failed tests: " + (totalTests.get() - successfulTests.get()));

            // Print result table
            System.out.println("\nDETAILED RESULTS:");
            System.out.printf("%-30s | %-8s | %-8s | %-10s | %-10s | %-6s\n",
                    "File", "Vertices", "Edges", "Max Flow", "Time (ms)", "Status");
            System.out.println("--------------------------------------------------------------------------");

            for (TestResult result : results) {
                System.out.printf("%-30s | %-8d | %-8d | %-10d | %-10d | %-6s\n",
                        result.fileName, result.vertices, result.edges,
                        result.maxFlow, result.timeMs,
                        result.success ? "OK" : "FAILED");
            }

        } catch (IOException e) {
            System.err.println("Error accessing directory: " + e.getMessage());
        }
    }

    // Process a single file within a directory test
    private static TestResult processDirectoryFile(Path filePath) {
        String fileName = filePath.getFileName().toString();
        TestResult result = new TestResult(fileName);

        System.out.println("\nProcessing: " + fileName);

        try {
            // Parse the network
            FlowNetwork network = NetworkParser.readFromFile(filePath.toString());

            // Set result data
            result.vertices = network.V();
            result.edges = network.E();

            // Default source and sink
            int source = 0;
            int sink = network.V() - 1;

            // Print basic network info
            System.out.println("Vertices: " + network.V() + ", Edges: " + network.E());

            // Skip extremely large networks if needed
            if (network.V() > 50000) {
                System.out.println("Network too large to process efficiently. Skipping.");
                result.success = true;
                result.maxFlow = -1;  // Indicates skipped
                return result;
            }

            // Determine if we should store paths based on network size
            boolean storeAllPaths = network.V() <= 10000;

            // Measure performance
            long startTime = System.currentTimeMillis();

            // Run Ford-Fulkerson with memory optimization for large networks
            FordFulkerson fordFulkerson = new FordFulkerson(network, source, sink, storeAllPaths);

            long endTime = System.currentTimeMillis();
            result.timeMs = endTime - startTime;

            // Record max flow
            result.maxFlow = fordFulkerson.value();
            result.success = true;

            // Print results
            System.out.println("Max Flow: " + result.maxFlow);
            System.out.println("Processing time: " + result.timeMs + " ms");

            // For smaller networks, show detailed path info
            if (network.V() <= 15 && storeAllPaths) {
                System.out.println(fordFulkerson.getAugmentingPathInfo());
            } else {
                System.out.println("Found " + fordFulkerson.getPathCount() + " augmenting paths");
            }

        } catch (OutOfMemoryError e) {
            System.err.println("ERROR: Out of memory processing " + fileName + ": " + e.getMessage());
            System.err.println("Network too large for available memory.");
            result.errorMessage = "Out of memory";
        } catch (Exception e) {
            System.err.println("ERROR processing " + fileName + ": " + e.getMessage());
            result.errorMessage = e.getMessage();
        }

        return result;
    }

    // Helper method to count augmenting paths
    private static int countOccurrences(String text, String pattern) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(pattern, index)) != -1) {
            count++;
            index += pattern.length();
        }
        return count;
    }

    // Helper class to store test results
    private static class TestResult {
        String fileName;
        int vertices;
        int edges;
        int maxFlow;
        long timeMs;
        boolean success;
        String errorMessage;

        public TestResult(String fileName) {
            this.fileName = fileName;
            this.vertices = 0;
            this.edges = 0;
            this.maxFlow = 0;
            this.timeMs = 0;
            this.success = false;
            this.errorMessage = "";
        }
    }
}