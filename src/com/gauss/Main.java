package com.gauss;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    private static double EPSILON = 0.000000001d;

    private static void printMatrix(double[][] a) {
        for (double[] row : a) {
            for (double c : row) {
                System.out.print(c + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static double[][] forwardGauss(double[][] a) {
        for (int i = 0; i < a.length - 1; i++) {
            System.out.println("Step " + (i + 1) + ":");
            double main = a[i][i];
            System.out.println(main);

            if (main == 0) {
                System.out.println("Swap rows");
                for (int j = i + 1; j < a.length; j++) {
                    if (a[j][i] != 0) {
                        swapRows(a, i, j);
                        printMatrix(a);
                        break;
                    }
                }
            }
            for (int j = i + 1; j < a.length; j++) {
                if (a[j][i] != 0) {
                    double multiplier = a[j][i] / main;
                    for (int k = 0; k < a[i].length; k++) {
                        a[j][k] -= multiplier * a[i][k];
                    }
                }
            }
            printMatrix(a);
        }
        return a;
    }

    private static double[] solve(double[][] a) {
        System.out.println("Solution:");
        double[] solution = new double[a.length];
        for (int i = a.length - 1; i >= 0; i--) {
//            System.out.println("Row " + (i + 1));
            double b = a[i][a[i].length - 1];
            for (int j = i + 1; j < a.length; j++) {
                b -= a[i][j];
            }
            solution[i] = b / a[i][i];
            for (int j = 0; j < a.length; j++) {
                a[j][i] *= solution[i];
            }
//            printMatrix(a);
            System.out.println("x" + (i + 1) + " = " + solution[i]);
        }
        return solution;
    }

    private static boolean checkSolution(double[][] a, double[] solution) {
        System.out.println("Checking...");
        for (int i = 0; i < a.length; i++) {
            double rowSum = 0;
            for (int j = 0; j < a.length; j++) {
                rowSum += a[i][j] * solution[i];
            }
            System.out.println("Row " + (i + 1) + ": " + rowSum + " = " + a[i][a[i].length - 1]);
            if (Math.abs(rowSum - a[i][a[i].length - 1]) > EPSILON) return false;
        }
        return true;
    }

    private static double determinant(double[][] a) {
        double det = 1;
        for (int i = 0; i < a.length; i++) {
            det *= a[i][i];
        }
        return det;
    }

    private static int rank(double[][] a) {
        int rank = 0;
        for (double[] row : a) {
            boolean zero = true;
            for (double c : row) {
                if (c != 0) {
                    zero = false;
                    break;
                }
            }
            if (!zero) rank++;
        }
        return rank;
    }

    private static void swapRows(double[][] a, int i, int j) {
        double[] temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Do you want to use console (c) or file (f) for input?");
        // TODO: add file input
        System.out.println();

        System.out.println("Your matrix size (N x N):");
        int size = 0;
        while (size == 0) {
            try {
                size = Integer.parseInt(reader.readLine());
                System.out.println("Size: " + size);
            } catch (Exception e) {
                System.out.println("Enter correct integer number, please.");
            }
        }
        System.out.println();

        double[][] matrix = new double[size][size + 1];
        System.out.println("Enter all matrix coefficients row by row:");
        System.out.print("\t\t");
        for (int i = 1; i < size + 1; i++) {
            System.out.print("x" + i + "\t");
        }
        System.out.println("b");
        boolean nextRow;
        for (int i = 0; i < size; i++) {
            nextRow = false;
            while (!nextRow) {
                try {
                    System.out.print("Row " + (i + 1) + ": ");
                    String[] row = reader.readLine().trim().replaceAll("[ ,]+", " ").split(" ");
                    for (int j = 0; j < size + 1; j++) {
                        matrix[i][j] = Double.parseDouble(row[j]);
                    }
                    nextRow = true;
                } catch (Exception e) {
                    System.out.println("Invalid input, enter last row again");
                }
            }
        }
        System.out.println();

        System.out.println("Your matrix:");
        printMatrix(matrix);

        double[][] checkMatrix = matrix.clone();

        forwardGauss(matrix);

        double det = determinant(matrix);
        System.out.println("Matrix determinant: " + det);

        if (det == 0) {
            System.out.println("System does not have the solution");
        } else {
            double[] solution = solve(matrix);
            System.out.println();

            if (checkSolution(checkMatrix, solution)) System.out.println("Solution is correct :)");
            else System.out.println("Solution is wrong :(");
        }

    }
}
