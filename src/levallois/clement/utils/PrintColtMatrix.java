/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levallois.clement.utils;

import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

/**
 *
 * @author C. Levallois
 */
public class PrintColtMatrix {

    public static void printSparseDoubleMatrix2D(SparseDoubleMatrix2D similarityMatrix) {

        for (int i = 0; i < similarityMatrix.rows(); i++) {
            for (int j = 0; j < similarityMatrix.columns(); j++) {
                System.out.print(similarityMatrix.get(i, j) + "  ");
            }
            System.out.println();
        }

    }

    public static void printSparseDoubleMatrix1D(SparseDoubleMatrix1D similarityMatrix) {

        for (int i = 0; i < similarityMatrix.size(); i++) {
            System.out.print(similarityMatrix.get(i) + "  ");
        }
        System.out.println();

    }
}
