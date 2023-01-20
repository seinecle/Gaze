/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.clementlevallois.gaze.controller;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.clementlevallois.utils.Clock;

/**
 *
 * @author C. Levallois
 */
public class Controller {

    //
    //  ##### source files
    //
    //
    private Map<String,Set<String>> sourceAndTargets;
    //
    // ##### parameters
    //
    //
    private double cosineThreshold = 0.05;
    private int maxNbTargetsPerSourceConsidered4CosineCalc = 1000;
    //
    // ##### objects and variables
    //
    //
    private SparseDoubleMatrix2D similarityMatrixColt;
    private BufferedWriter bw;
    int minTargetsInCommon;

    public Controller(Map<String,Set<String>> sourceAndTargets) {
        this.sourceAndTargets = sourceAndTargets;
    }

    public String run(int minTargetsInCommon) {
        try {

            MatrixBuilder matrixBuilder = new MatrixBuilder(sourceAndTargets, maxNbTargetsPerSourceConsidered4CosineCalc);
            SparseDoubleMatrix1D[] listVectors = matrixBuilder.createListOfSparseVectorsFromEdgeList();
            similarityMatrixColt = new SparseDoubleMatrix2D(listVectors.length, listVectors.length);

            Thread t = new Thread(new CosineCalculation(listVectors, similarityMatrixColt, minTargetsInCommon));
            t.start();
            t.join();
            System.out.println("Cosine calculated!");

            Clock printEdgesDL = new Clock("printing a DL file for the edges");
            StringBuilder sb = new StringBuilder();
            sb.append("source,target,Weight,type" + System.lineSeparator());

            IntArrayList rowList = new IntArrayList();
            IntArrayList columnList = new IntArrayList();
            DoubleArrayList valueList = new DoubleArrayList();
            similarityMatrixColt.getNonZeros(rowList, columnList, valueList);

            int nonZeroCell = 0;
            int nbOfNonZeroCells = rowList.size();
            for (nonZeroCell = 0; nonZeroCell < nbOfNonZeroCells; nonZeroCell++) {
                int rowIndex = rowList.get(nonZeroCell);
                int colIndex = columnList.get(nonZeroCell);
                double cellValue = valueList.get(nonZeroCell);
                sb.append(matrixBuilder.getMapSourcesIndexToLabel().get(rowIndex)).append(",").append(matrixBuilder.getMapSourcesIndexToLabel().get(colIndex)).append(",").append(cellValue).append(",").append("undirected").append(System.lineSeparator());
            }
            printEdgesDL.closeAndPrintClock();
            System.out.println("result: "+ sb.toString());
            return sb.toString();

        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            return "error";

        }
    }

    public SparseDoubleMatrix2D getSimilarityMatrixColt() {
        return similarityMatrixColt;
    }
}
