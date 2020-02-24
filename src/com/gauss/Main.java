package com.gauss;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Main {

    private static double EPSILON = 0.000000001d;
    private static int swaps = 0;
    private static Random randomizer = new Random(new Date().getTime());
    private static DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

    private static void printMatrix(double[][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                if (i > j && a[i][j] == 0) System.out.print(String.format("%15s", " "));
                else System.out.print(String.format("%15.8f", a[i][j]));
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void forwardGauss(double[][] a) {
        for (int i = 0; i < a.length - 1; i++) {
            System.out.println("Step " + (i + 1) + ":");
            double main = a[i][i];

            if (main == 0) {
                for (int j = i + 1; j < a.length; j++)
                    if (a[j][i] != 0) {
                        swapRows(a, i, j);
                        swaps++;
                        break;
                    }
            }
            for (int j = i + 1; j < a.length; j++) {
                if (a[j][i] != 0) {
                    double multiplier = a[j][i] / main;
                    for (int k = 0; k < a[i].length; k++)
                        a[j][k] -= multiplier * a[i][k];
                }
            }
            printMatrix(a);
        }
    }

    private static double[] solve(double[][] a) {
        System.out.println("Solution:");
        double[] solution = new double[a.length];
        for (int i = a.length - 1; i >= 0; i--) {
            double b = a[i][a[i].length - 1];
            for (int j = i + 1; j < a.length; j++)
                b -= a[i][j];
            solution[i] = b / a[i][i];
            for (int j = 0; j < a.length; j++)
                a[j][i] *= solution[i];
            System.out.println(String.format("x%d = %-20s", (i + 1), df.format(solution[i])));
        }
        return solution;
    }

    private static boolean checkSolution(double[][] a, double[] solution) {
        double diff;
        boolean correct = true;
        System.out.println("Checking...");
        System.out.println("Calculation errors:");
        for (int i = 0; i < a.length; i++) {
            double rowSum = 0;
            for (int j = 0; j < a.length; j++)
                rowSum += a[i][j] * solution[j];
            diff = Math.abs(a[i][a[i].length - 1] - rowSum);
            System.out.println(String.format("x%d: %-20s", (i + 1), df.format(diff)));
            if (diff > EPSILON) correct = false;
        }
        System.out.println();
        return correct;
    }

    private static double determinant(double[][] a) {
        double det = 1;
        for (int i = 0; i < a.length; i++)
            det *= a[i][i];
        return det * (swaps % 2 == 0 ? 1 : -1);
    }

    private static int rankTriangular(double[][] a) {
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

    private static double[][] randomMatrix(int size) {
        double[][] matrix = new double[size][size + 1];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size + 1; j++)
                matrix[i][j] = randomizer.nextDouble() * 100;
        }
        return matrix;
    }

    private static double[][] copyMatrix(double[][] matrix) {
        int size = matrix.length;
        double[][] copy = new double[size][size + 1];
        for (int i = 0; i < size; i++)
            copy[i] = matrix[i].clone();
        return copy;
    }

    public static void main(String[] args) {
        df.setMaximumFractionDigits(340);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        int size = 0;

        System.out.println("Do you want to use console (c) or file (f) for input?");
        boolean useFile = false;
        boolean inputSource = false;
        boolean random = false;
        while (!inputSource) {
            try {
                String answer = reader.readLine().trim();
                if (!(answer.equals("c") || answer.equals("f") || answer.equals("r")))
                    System.out.println("Enter c to use console, f - to use file");
                else {
                    useFile = answer.equals("f");
                    random = answer.equals("r");
                    inputSource = true;
                }
            } catch (IOException e) {
                System.out.println("Enter c to use console, f - to use file");
            }
        }
        System.out.println();

        double[][] matrix = new double[size][size + 1];

        if (useFile) {
            BufferedReader fileReader;
            boolean gotFile = false;
            try {
                while (!gotFile) {
                    System.out.println("Enter path to your file:");
                    String path = reader.readLine().trim();
                    File file = new File(path);
                    if (file.exists() && file.canRead()) {
                        fileReader = new BufferedReader(new FileReader(file));
                        try {
                            size = Integer.parseInt(fileReader.readLine().trim());
                            matrix = new double[size][size + 1];
                            System.out.println("Matrix size: " + size + " x " + size);
                            for (int i = 0; i < size; i++) {
                                String[] row = fileReader.readLine().trim().replaceAll("[,]+", ".")
                                        .replaceAll("[ ]+", " ").split(" ");
                                for (int j = 0; j < size + 1; j++) {
                                    matrix[i][j] = Double.parseDouble(row[j]);
                                }
                            }
                            gotFile = true;
                        } catch (Exception e) {
                            System.out.println("Invalid file, check its contents");
                        }
                    } else System.out.println("No such file");
                }
                System.out.println();
            } catch (IOException e) {
                System.out.println("Invalid file path, try again");
            }

        } else if (random) {
            size = randomizer.nextInt(10);
            matrix = randomMatrix(size);
        } else {
            System.out.println("Enter your matrix size (N x N):");
            while (size <= 0) {
                try {
                    size = Integer.parseInt(reader.readLine());
                    if (size <= 0 || size > 20) throw new NumberFormatException();
                    System.out.println("Size: " + size + " x " + size);
                } catch (Exception e) {
                    System.out.println("Enter correct positive integer number less or equals 20");
                }
            }
            System.out.println();
            matrix = new double[size][size + 1];

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
                        String[] row = reader.readLine().trim().replaceAll("[,]+", ".")
                                .replaceAll("[ ]+", " ").split(" ");
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
        }

        System.out.println("Your matrix:");
        printMatrix(matrix);

        double[][] checkMatrix = copyMatrix(matrix);

        forwardGauss(matrix);

        double det = determinant(matrix);
        System.out.println(String.format("Matrix determinant: %s", df.format(det)));
        System.out.println();

        if (det == 0) {
            System.out.println("System does not have the unique solution");
        } else {
            double[] solution = solve(matrix);
            System.out.println();

            if (checkSolution(checkMatrix, solution)) System.out.println("Solution is correct :)");
            else System.out.println("Solution is wrong :(");
        }

    }
}
