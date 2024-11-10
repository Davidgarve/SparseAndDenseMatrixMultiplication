package org.example;

import org.openjdk.jmh.annotations.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@State(Scope.Thread)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.MILLISECONDS)
public class SparseCSCMatrixMultiplicationBenchmarking {

    @Param({"100", "500", "1000", "2000", "3000"})
    private int n;

    @Param({"0","0.2","0.5","0.7","0.9"})
    private double sparsityLevel;

    private List<Long> memoryUsages;

    private SparseMatrixCSCMul.CSCMatrix sparseMatrixA;
    private SparseMatrixCSCMul.CSCMatrix sparseMatrixB;

    @Setup(Level.Trial)
    public void setupTrial() {
        memoryUsages = new ArrayList<>();
        double[][] denseMatrixA = generateSparseMatrix(n, sparsityLevel);
        double[][] denseMatrixB = generateSparseMatrix(n, sparsityLevel);

        sparseMatrixA = SparseMatrixCSCMul.convertToCSC(denseMatrixA);
        sparseMatrixB = SparseMatrixCSCMul.convertToCSC(denseMatrixB);
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        System.gc();
    }

    @Benchmark
    public void sparseMultiplication() {
        measureMemoryUsage(() -> sparseMatrixA.multiply(sparseMatrixB));
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

    private double[][] generateSparseMatrix(int size, double sparsity) {
        Random random = new Random();
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = random.nextDouble() < sparsity ? 0.0 : random.nextDouble();
            }
        }
        return matrix;
    }
}
