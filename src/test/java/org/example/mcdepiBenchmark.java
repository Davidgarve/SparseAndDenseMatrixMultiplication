package org.example;

import org.openjdk.jmh.annotations.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@State(Scope.Thread)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.MILLISECONDS)
public class mcdepiBenchmark {
    private static final String MATRIX_FILE_PATH = "mc2depi/mc2depi/mc2depi.mtx";
    private List<Long> memoryUsages;
    private SparseMatrixCSRMul.CSRMatrix csrMatrixA;
    private SparseMatrixCSRMul.CSRMatrix csrMatrixB;

    @Setup(Level.Trial)
    public void setupTrial() {
        memoryUsages = new ArrayList<>();

        try {
            csrMatrixA = MatrixMarketLoader.loadCSRMatrixFromMTX(MATRIX_FILE_PATH);
            csrMatrixB = csrMatrixA;
        } catch (IOException e) {
            System.err.println("Error loading the matrix file: " + e.getMessage());
        }
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        System.gc();
    }

    @Benchmark
    public void sparseCSRMulMultiplication() {
        measureMemoryUsage(() -> csrMatrixA.multiply(csrMatrixB));
    }

    private void measureMemoryUsage(Runnable multiplicationTask) {
        Runtime runtime = Runtime.getRuntime();
        long beforeMemory = runtime.totalMemory() - runtime.freeMemory();
        multiplicationTask.run();
        long afterMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = afterMemory - beforeMemory;
        memoryUsages.add(memoryUsed);
    }

    @TearDown(Level.Trial)
    public void tearDownTrial() {
        long totalMemoryUsed = memoryUsages.stream().mapToLong(Long::longValue).sum();
        double averageMemoryUsed = (double) totalMemoryUsed / memoryUsages.size();
        System.out.printf("\nTotal memory used during trial: %.2f MB%n", (double) totalMemoryUsed / (1024 * 1024));
        System.out.printf("Average memory used during trial: %.2f MB%n", averageMemoryUsed / (1024 * 1024));
    }
}

