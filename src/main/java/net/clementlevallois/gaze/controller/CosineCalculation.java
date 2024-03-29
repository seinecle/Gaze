/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.clementlevallois.gaze.controller;

import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author C. Levallois
 */
public class CosineCalculation {

    private final SparseDoubleMatrix1D[] listVectorsColt;
    private SparseDoubleMatrix2D similarityMatrixColt;
    private SparseDoubleMatrix2D sharedTargetsMatrixColt;
    private ArrayList<Double> normsColt;
    private int[] arrayOfTargetIndicesForSourceAColt;
    private int[] arrayOfTargetIndicesForSourceBColt;
    private List<Integer> listOfTargetIndicesForSourceAColt;
    private List<Integer> listOfTargetIndicesForSourceBColt;
    private int minTargetsInCommon = 1;

    public CosineCalculation(SparseDoubleMatrix1D[] listVectors, int minTargetsInCommon) {
        this.listVectorsColt = listVectors;
        this.minTargetsInCommon = minTargetsInCommon;
    }

    public void run() {

        similarityMatrixColt = new SparseDoubleMatrix2D(listVectorsColt.length, listVectorsColt.length);
        sharedTargetsMatrixColt = new SparseDoubleMatrix2D(listVectorsColt.length, listVectorsColt.length);

        SparseDoubleMatrix1D vectorOfTargetsForSourceAColt;
        SparseDoubleMatrix1D vectorOfTargetsForSourceBColt;

        normsColt = new ArrayList();
        //1. iteration through all vectors

        for (int i = 0; i < listVectorsColt.length; i++) {

            if (listVectorsColt[i] == null) {
                continue;
            }
            vectorOfTargetsForSourceAColt = listVectorsColt[i];

            Algebra algebraColt = new Algebra();
            double norm2Colt = Math.sqrt(algebraColt.norm2(vectorOfTargetsForSourceAColt));
            normsColt.add(norm2Colt);

            for (int j = 0; j < listVectorsColt.length; j++) {
                if (listVectorsColt[j] == null) {
                    continue;
                }

                if (j < i) {
                    vectorOfTargetsForSourceBColt = listVectorsColt[j];
                    arrayOfTargetIndicesForSourceAColt = new int[vectorOfTargetsForSourceAColt.cardinality()];
                    int indices = 0;
                    for (int row = vectorOfTargetsForSourceAColt.size(); --row >= 0;) {
                        double targetValue = vectorOfTargetsForSourceAColt.getQuick(row);
                        if (targetValue != 0) {
                            arrayOfTargetIndicesForSourceAColt[indices++] = row;
                        }
                    }
//                        System.out.println("svSource.getIndex().size: " + sourceIndexes.length);
                    listOfTargetIndicesForSourceAColt = new ArrayList();
                    for (int s = 0; s < arrayOfTargetIndicesForSourceAColt.length; s++) {
                        listOfTargetIndicesForSourceAColt.add(arrayOfTargetIndicesForSourceAColt[s]);
                    }
                    arrayOfTargetIndicesForSourceBColt = new int[vectorOfTargetsForSourceBColt.cardinality()];
                    indices = 0;
                    for (int row = vectorOfTargetsForSourceBColt.size(); --row >= 0;) {
                        double targetValue = vectorOfTargetsForSourceBColt.getQuick(row);
                        if (targetValue != 0) {
                            arrayOfTargetIndicesForSourceBColt[indices++] = row;
                        }
                    }
//                        System.out.println("svSource.getIndex().size: " + sourceIndexes.length);
                    listOfTargetIndicesForSourceBColt = new ArrayList();
                    for (int s = 0; s < arrayOfTargetIndicesForSourceBColt.length; s++) {
                        listOfTargetIndicesForSourceBColt.add(arrayOfTargetIndicesForSourceBColt[s]);
                    }

                    listOfTargetIndicesForSourceAColt.retainAll(listOfTargetIndicesForSourceBColt);
                    double nbOfShareTargets = (double) listOfTargetIndicesForSourceAColt.size();
                    if (nbOfShareTargets >= minTargetsInCommon) {
                    sharedTargetsMatrixColt.setQuick(i, j, nbOfShareTargets);
                        doCalculus(vectorOfTargetsForSourceAColt, vectorOfTargetsForSourceBColt, i, j);
                    }
                }
                // I think it is not necessary to set zero values
//                else {
//                    similarityMatrixColt.set(i, j, 0d);
//                    sharedTargetsMatrixColt.set(i, j, 0d);
//                }

            }
        }
    }

    private void doCalculus(SparseDoubleMatrix1D source, SparseDoubleMatrix1D target, int i, int j) {
        double normI = normsColt.get(i);
        double normJ = normsColt.get(j);
        double dotProduct = source.zDotProduct(target);
        double result = dotProduct / (normI * normJ);
        similarityMatrixColt.set(i, j, result);
    }

    public SparseDoubleMatrix2D getSimilarityMatrixColt() {
        return similarityMatrixColt;
    }

    public SparseDoubleMatrix2D getSharedTargetsMatrixColt() {
        return sharedTargetsMatrixColt;
    }

}
