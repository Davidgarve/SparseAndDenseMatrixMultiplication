package org.example;

public class LoopUnrolledMatrixMultiplier {

    public double[][] LoopUnrolledMultiply(double[][] a, double[][] b) {
        int n = a.length;
        double[][] c = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double r = a[i][j];
                for (int k = 0; k < n; k += 4) {
                    c[i][j] += r * b[k][j];
                    if (j + 1 < n) c[i][j+1] += r * b[k][j+1];
                    if (j + 2 < n) c[i][j+2] += r * b[k][j+2];
                    if (j + 3 < n) c[i][j+3] += r * b[k][j+3];
                }
            }
        }
        return c;
    }
}
