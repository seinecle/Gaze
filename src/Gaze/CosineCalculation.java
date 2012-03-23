/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gaze;

import java.util.ArrayList;
import java.util.List;
import levallois.clement.utils.Clock;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.sparse.FlexCompColMatrix;
import no.uib.cipr.matrix.sparse.SparseVector;

/**
 *
 * @author C. Levallois
 */
public class CosineCalculation implements Runnable {

    public static long cellTime;
    private final SparseVector[] listVectors;
    public static Clock matrixClock;
    static public ArrayList<Integer> nonZeroIndexListSource = new ArrayList();
    static public ArrayList<Integer> nonZeroIndexListTarget = new ArrayList();
    static public int numSources;
    static double normSource;
    static public ArrayList<Double> norms;
    static public int sizeVector;
    private double numCalculations;
    String logText = "";
    String newLine = "\n";
    String interval = "--------------------------\n";
    int[] sourceIndexes;
    int[] targetIndexes;
    List<Integer> listSourceIndex;
    List<Integer> listTargetIndex;

    CosineCalculation(SparseVector[] listVectors) {

        this.listVectors = listVectors;
    }

    @Override
    public void run() {

        SparseVector svSource = new SparseVector(1);
        SparseVector svTarget = new SparseVector(1);


        Clock computeSimilarityClock = new Clock("computing similarity with\nsparseVectors: " + Main.useSparseVectors);

        numSources = listVectors.length;
//            numTargets = AdjacencyMatrixBuilder.setTargetsShort.size();
        numCalculations = Math.pow(numSources, 2) / 2;







        Main.similarityMatrix = new FlexCompColMatrix(numSources, numSources);

        System.out.println("size of the similiarity matrix: " + numSources + "x " + numSources);

        //1. iteration through all rows (targets) of the termDocumentMatrix

        norms = new ArrayList();

        matrixClock = new Clock("clocking the first " + Main.testruns + " calculus");

        for (int i = 0; i < numSources; i++) {
            if (AdjacencyMatrixBuilder.listVectors[i] == null) {
                continue;
            }
            svSource = AdjacencyMatrixBuilder.listVectors[i];
            norms.add(svSource.norm(Vector.Norm.Two));

            for (int j = 0; j < numSources; j++) {
                if (AdjacencyMatrixBuilder.listVectors[j] == null) {
                    continue;
                }

                if (j < i) {

                    Main.countCalculus++;
                    //System.out.println(Main.countCalculus);
                    if (Main.countCalculus % 100000 == 0) {
                        long elapsedtimeInSeconds = computeSimilarityClock.getElapsedTime();
                        //double currPercentAdvance = (Main.countCalculus / numCalculations) * 100;
                        double remainingTime = (elapsedtimeInSeconds * numCalculations / Main.countCalculus) - elapsedtimeInSeconds;
                        if (remainingTime
                                < 1000) {
                            logText = "time remaining: " + remainingTime + " milliseconds" + newLine + interval;
                            System.out.print(logText);
                        } else if (remainingTime < 60000) {
                            logText = "time remaining: " + Math.round(remainingTime / 1000) + " seconds" + newLine + interval;
                            System.out.print(logText);
                        } else {
                            logText = "time remaining: " + Math.round(remainingTime / 60000) + " minutes " + Math.round((remainingTime % 60000) / 1000) + " seconds" + newLine + interval;
                            System.out.print(logText);

                        }
                    }

                    svTarget = AdjacencyMatrixBuilder.listVectors[j];


                    synchronized (Main.similarityMatrix) {



                        sourceIndexes = svSource.getIndex();
                        listSourceIndex = new ArrayList();
                        for (int s = 0; s < sourceIndexes.length; s++) {

                            listSourceIndex.add(sourceIndexes[s]);

                        }
                        //System.out.println(listSourceIndex.size());
                        targetIndexes = svTarget.getIndex();
                        listTargetIndex = new ArrayList();
                        for (int s = 0; s < targetIndexes.length; s++) {

                            listTargetIndex.add(targetIndexes[s]);

                        }
                        //System.out.println(listTargetIndex.size());

                        listSourceIndex.retainAll(listTargetIndex);
                        if (!listSourceIndex.isEmpty()) {
                            //System.out.println("intersect btw sets: "+setSource.size());

                            doCalculus(svSource, svTarget, i, j);
                        }



                    }

                } else {
                    synchronized (Main.similarityMatrix) {
                        Main.similarityMatrix.set(i, j, 0);
                    }

                }

            }
        }

        computeSimilarityClock.closeAndPrintClock();




    }

//    public static double dotProduct(double A[], double B[]) {
//
//        int n = A.length;
//        //System.out.println(n);
//
//        double result = 0;
//
//
//        if ("Java".equals(Main.dotProductChoice)) {
//            result = SDotJava(A, B);
//        } else {
//            if ("Cuda".equals(Main.dotProductChoice)) {
//                result = SDotCuda(A, B, n);
//            }
//        }
//
//        return result;
//    }
//    public static double euclidianDist(SparseDoubleMatrix1D A, SparseDoubleMatrix1D B) {
//
//        double h_A[] = A.toArray();
//        double h_B[] = B.toArray();
//        int n = h_A.length;
//        //System.out.println(n);
//        double result = 0;
//
//        if ("Java".equals(Main.euclidianDistChoice)) {
//            result = euclidianDistJava(A, B);
//        } else {
//            if ("Cuda".equals(Main.euclidianDistChoice)) {
//
//                result = euclidianDistCuda(h_A, h_B, n);
//            }
//        }
//
//        return result;
//    }
//    private static double SDotCuda(double A[], double B[], int n) {
//
//        //JCublas.cublasAlloc(n, Sizeof.DOUBLE, d_A);
//        //JCublas.cublasAlloc(n, Sizeof.DOUBLE, d_B);
//
//        // Copy the memory from the host to the device
//        JCublas.cublasSetVector(n, Sizeof.DOUBLE, Pointer.to(A), 1, d_A, 1);
//        JCublas.cublasSetVector(n, Sizeof.DOUBLE, Pointer.to(B), 1, d_B, 1);
//
//        // Execute SDot
//        double dotProductCuda = JCublas.cublasDdot(
//                n, d_A, 1, d_B, 1);
//
//        // Clean up
//        //JCublas.cublasFree(d_A);
//        //JCublas.cublasFree(d_B);
//
//        //System.out.println("dotProduct with Cuda: " + dotProductCuda);
//
//        return dotProductCuda;
//    }
//    private static double euclidianDistCuda(double A[], double B[], int n) {
//
//        // Allocate memory on the device
////        Pointer d_C = new Pointer();
////        Pointer d_D = new Pointer();
////        JCublas.cublasAlloc(n, Sizeof.DOUBLE, d_C);
////        JCublas.cublasAlloc(n, Sizeof.DOUBLE, d_D);
//
//        // Copy the memory from the host to the device
//        JCublas.cublasSetVector(n, Sizeof.DOUBLE, Pointer.to(A), 1, d_C, 1);
//        JCublas.cublasSetVector(n, Sizeof.DOUBLE, Pointer.to(B), 1, d_D, 1);
//
//
//        // Execute SDot
//        double AeuclidianCUDA = JCublas.cublasDnrm2(n, d_C, 1);
////        double AeuclidianCUDA = normSource;
//        double BeuclidianCUDA = JCublas.cublasDnrm2(n, d_D, 1);
//
//
//        // Clean up
////        JCublas.cublasFree(d_C);
////        JCublas.cublasFree(d_D);
//
//        double euclidianDistCuda = AeuclidianCUDA * BeuclidianCUDA;
//        //System.out.println("euclidianDist with Cuda: " + euclidianDistCuda);
//        return euclidianDistCuda;
//
//    }
//    private static double euclidianDistJava(SparseDoubleMatrix1D A, SparseDoubleMatrix1D B) {
//
//
//        //double euclidianDistJava = Statistic.EUCLID.apply(A, B);
//        double euclidianDistJava = Math.sqrt(A.zDotProduct(A) * B.zDotProduct(B));
//        //System.out.println("euclidianDist with Java: " + euclidianDistJava);
//
//        return euclidianDistJava;
//
//
//    }
//
//    // his function is useful when the vectors have a length of 1000 or less
//    private static double SDotJava(double A[], double B[]) {
//
//        DoubleMatrix1D sourceDoc = new SparseDoubleMatrix1D(A);
//        DoubleMatrix1D targetDoc = new SparseDoubleMatrix1D(B);
//
//        double dotProductJava = sourceDoc.zDotProduct(targetDoc);
//        //System.out.println("dotProduct with Java: " + dotProductJava);
//
//        return dotProductJava;
//
//    }
//
//    double computeSimilarity(SparseDoubleMatrix1D sourceDoc, SparseDoubleMatrix1D targetDoc) {
//
//
//        double dotProduct = dotProduct(sourceDoc.toArray(), targetDoc.toArray());
//        //System.out.println(dotProduct);
//
//        double euclidianDist = euclidianDist(sourceDoc, targetDoc);
//        //double euclidianDist = Statistic.EUCLID.apply(sourceDoc, targetDoc);
////        double euclidianDist = euclidianDist(sourceDoc.toArray(), targetDoc.toArray());
//        //System.out.println(eucledianDist);
//
//        return dotProduct / euclidianDist;
////        return dotProduct;
//    }
    static void doCalculus(SparseVector source, SparseVector target, int i, int j) {

        double result = source.dot(target) / (CosineCalculation.norms.get(i) * CosineCalculation.norms.get(j));
        //System.out.println("result in the runnable: " + result);
//    Triple similarityResult = new Triple(i,j,result);
        synchronized (Main.similarityMatrix) {
            Main.similarityMatrix.set(i, j, result);
        }
//    long endTime = System.currentTimeMillis();
//    CosineCalculation.cellTime = CosineCalculation.cellTime + endTime-currentTime; 
    }
}
