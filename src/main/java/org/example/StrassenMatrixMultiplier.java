package org.example;

public class StrassenMatrixMultiplier {

    private static final int THRESHOLD = 32;

    public double[][] strassenMultiply(double[][] A, double[][] B) {
        int n = A.length;

        if (n <= THRESHOLD) {
            return new MatrixMultiplication().execute(A, B);
        }

        if (n == 1) {
            double[][] C = new double[1][1];
            C[0][0] = A[0][0] * B[0][0];
            return C;
        }

        int mid = n / 2;
        double[][] A11 = new double[mid][mid];
        double[][] A12 = new double[mid][mid];
        double[][] A21 = new double[mid][mid];
        double[][] A22 = new double[mid][mid];

        double[][] B11 = new double[mid][mid];
        double[][] B12 = new double[mid][mid];
        double[][] B21 = new double[mid][mid];
        double[][] B22 = new double[mid][mid];

        for (int i = 0; i < mid; i++) {
            for (int j = 0; j < mid; j++) {
                A11[i][j] = A[i][j];
                A12[i][j] = A[i][j + mid];
                A21[i][j] = A[i + mid][j];
                A22[i][j] = A[i + mid][j + mid];

                B11[i][j] = B[i][j];
                B12[i][j] = B[i][j + mid];
                B21[i][j] = B[i + mid][j];
                B22[i][j] = B[i + mid][j + mid];
            }
        }

        double[][] M1 = strassenMultiply(addMatrices(A11, A22), addMatrices(B11, B22));
        double[][] M2 = strassenMultiply(addMatrices(A21, A22), B11);
        double[][] M3 = strassenMultiply(A11, subtractMatrices(B12, B22));
        double[][] M4 = strassenMultiply(A22, subtractMatrices(B21, B11));
        double[][] M5 = strassenMultiply(addMatrices(A11, A12), B22);
        double[][] M6 = strassenMultiply(subtractMatrices(A21, A11), addMatrices(B11, B12));
        double[][] M7 = strassenMultiply(subtractMatrices(A12, A22), addMatrices(B21, B22));

        double[][] C11 = addMatrices(subtractMatrices(addMatrices(M1, M4), M5), M7);
        double[][] C12 = addMatrices(M3, M5);
        double[][] C21 = addMatrices(M2, M4);
        double[][] C22 = addMatrices(subtractMatrices(addMatrices(M1, M3), M2), M6);

        double[][] C = new double[n][n];
        for (int i = 0; i < mid; i++) {
            for (int j = 0; j < mid; j++) {
                C[i][j] = C11[i][j];
                C[i][j + mid] = C12[i][j];
                C[i + mid][j] = C21[i][j];
                C[i + mid][j + mid] = C22[i][j];
            }
        }

        return C;
    }

    private double[][] addMatrices(double[][] A, double[][] B) {
        int n = A.length;
        double[][] result = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = A[i][j] + B[i][j];
            }
        }
        return result;
    }

    private double[][] subtractMatrices(double[][] A, double[][] B) {
        int n = A.length;
        double[][] result = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = A[i][j] - B[i][j];
            }
        }
        return result;
    }
}
