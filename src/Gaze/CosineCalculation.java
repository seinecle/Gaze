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
    static public int numNodes;
    static double normSource;
    static public ArrayList<Double> norms;
    static public int sizeVector;
    private int numCalculations;
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

        //to clarify: sources and targets refer to the 2 elements of a pair of nodes, that's all
        //without a reference to which node was actually a source in the initial edge list, and which was a target
        SparseVector svSource;
        SparseVector svTarget;


        Clock computeSimilarityClock = new Clock("computing similarity with\nsparseVectors: " + Controller.useSparseVectors);

        //the number of nodes which will appear in the final similarity network
        //corresponds to the number of vectors contained in the list created in AdjacencyMtrixBuilder
        numNodes = listVectors.length;
//      numTargets = Amb.setTargetsShort.size();


        //this looks complicated but is simply the number of elements in the networks and all their combinations
        numCalculations = (int)Math.pow(numNodes, 2) / 2;


        //this is where the adjacency matrix for the final network is built
        Controller.similarityMatrix = new FlexCompColMatrix(numNodes, numNodes);

        System.out.println("size of the similarity matrix: " + numNodes + " x " + numNodes);

        //1. iteration through all nodes of the similarityMatrix

        norms = new ArrayList();

        matrixClock = new Clock("clocking the first " + Controller.testruns + " calculus");

        for (int i = 0; i < numNodes; i++) {

            //in the case of undirected networks, the vector can be empty
            //because there...???
            if (Amb.listVectors[i] == null) {
                continue;
            }
            svSource = Amb.listVectors[i];
            norms.add(svSource.norm(Vector.Norm.Two));
//            System.out.println("index source: " + i);

            for (int j = 0; j < numNodes; j++) {
                if (Amb.listVectors[j] == null) {
                    continue;
                }

                if (j < i) {
//                    System.out.println("index target: " + j);

                    Controller.countCalculus++;
                    int index = Math.round(((float)Controller.countCalculus * 100 / numCalculations));
                    Screen_1.pb.setValue(index);

                    //System.out.println(Controller.countCalculus);
//                    if (Controller.countCalculus % 100000 == 0) {
//                        long elapsedtimeInSeconds = computeSimilarityClock.getElapsedTime();
//                        //double currPercentAdvance = (Controller.countCalculus / numCalculations) * 100;
//                        double remainingTime = (elapsedtimeInSeconds * numCalculations / Controller.countCalculus) - elapsedtimeInSeconds;
//                        if (remainingTime
//                                < 1000) {
//                            logText = "time remaining: " + remainingTime + " milliseconds" + newLine + interval;
//                            System.out.print(logText);
//                        } else if (remainingTime < 60000) {
//                            logText = "time remaining: " + Math.round(remainingTime / 1000) + " seconds" + newLine + interval;
//                            System.out.print(logText);
//                        } else {
//                            logText = "time remaining: " + Math.round(remainingTime / 60000) + " minutes " + Math.round((remainingTime % 60000) / 1000) + " seconds" + newLine + interval;
//                            System.out.print(logText);
//
//                        }
//                    }

                    svTarget = Amb.listVectors[j];


                    synchronized (Controller.similarityMatrix) {
                        sourceIndexes = svSource.getIndex();
//                        System.out.println("svSource.getIndex().size: " + sourceIndexes.length);
                        listSourceIndex = new ArrayList();
                        for (int s = 0; s < sourceIndexes.length; s++) {
                            listSourceIndex.add(sourceIndexes[s]);
                        }
//                        System.out.println(listSourceIndex.size());
                        targetIndexes = svTarget.getIndex();
//                        System.out.println("svTarget.getIndex().size: " + targetIndexes.length);

                        listTargetIndex = new ArrayList();
                        for (int s = 0; s < targetIndexes.length; s++) {
                            listTargetIndex.add(targetIndexes[s]);
                        }
//                        System.out.println(listTargetIndex.size());

                        listSourceIndex.retainAll(listTargetIndex);
                        if (!listSourceIndex.isEmpty()) {

                            doCalculus(svSource, svTarget, i, j);
                        }



                    }

                } else {

                    Controller.similarityMatrix.set(i, j, 0);

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
//        if ("Java".equals(Controller.dotProductChoice)) {
//            result = SDotJava(A, B);
//        } else {
//            if ("Cuda".equals(Controller.dotProductChoice)) {
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
//        if ("Java".equals(Controller.euclidianDistChoice)) {
//            result = euclidianDistJava(A, B);
//        } else {
//            if ("Cuda".equals(Controller.euclidianDistChoice)) {
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
        Controller.similarityMatrix.set(i, j, result);
//    long endTime = System.currentTimeMillis();
//    CosineCalculation.cellTime = CosineCalculation.cellTime + endTime-currentTime; 
    }
}
