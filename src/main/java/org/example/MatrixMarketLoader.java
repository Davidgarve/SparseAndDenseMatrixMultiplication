package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MatrixMarketLoader {

    public static SparseMatrixCSRMul.CSRMatrix loadCSRMatrixFromMTX(String filePath) throws IOException {
        List<Double> valuesList = new ArrayList<>();
        List<Integer> columnIndicesList = new ArrayList<>();
        List<Integer> rowPointersList = new ArrayList<>();

        int rows = 0, cols = 0, nonZeros = 0;
        rowPointersList.add(0);

        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("%")) continue;
                String[] parts = line.split("\\s+");
                if (rows == 0) {
                    rows = Integer.parseInt(parts[0]);
                    cols = Integer.parseInt(parts[1]);
                    nonZeros = Integer.parseInt(parts[2]);
                    break;
                }
            }

            int[] rowCounts = new int[rows];
            List<List<Integer>> tempColumnIndices = new ArrayList<>(rows);
            List<List<Double>> tempValues = new ArrayList<>(rows);
            for (int i = 0; i < rows; i++) {
                tempColumnIndices.add(new ArrayList<>());
                tempValues.add(new ArrayList<>());
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\s+");
                int row = Integer.parseInt(parts[0]) - 1;
                int col = Integer.parseInt(parts[1]) - 1;
                double value = Double.parseDouble(parts[2]);

                tempColumnIndices.get(row).add(col);
                tempValues.get(row).add(value);
                rowCounts[row]++;
            }

            for (int i = 0; i < rows; i++) {
                columnIndicesList.addAll(tempColumnIndices.get(i));
                for (double val : tempValues.get(i)) {
                    valuesList.add(val);
                }
                rowPointersList.add(rowPointersList.get(rowPointersList.size() - 1) + rowCounts[i]);
            }
        }

        // Convert lists to arrays
        double[] values = valuesList.stream().mapToDouble(Double::doubleValue).toArray();
        int[] columnIndices = columnIndicesList.stream().mapToInt(Integer::intValue).toArray();
        int[] rowPointers = rowPointersList.stream().mapToInt(Integer::intValue).toArray();

        return new SparseMatrixCSRMul.CSRMatrix(values, columnIndices, rowPointers, rows, cols);
    }

    public static void main(String[] args) {
        try {
            SparseMatrixCSRMul.CSRMatrix matrix = loadCSRMatrixFromMTX("mc2depi/mc2depi/mc2depi.mtx");
            System.out.println("Matrix loaded successfully in CSR format.");
        } catch (IOException e) {
            System.err.println("Failed to load matrix: " + e.getMessage());
        }
    }
}
