/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cosine;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.JCublas;
import levallois.clement.utils.Clock;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.sparse.FlexCompColMatrix;
import no.uib.cipr.matrix.sparse.SparseVector;

/**
 *
 * @author C. Levallois
 */
public class Cosine2loops implements Runnable {
    public static long cellTime;

    public ArrayList<Thread> listThreads = new ArrayList();
    private final SparseVector[] listVectors;
    public static Clock matrixClock;
    static public IntArrayList nonZeroIndexListSource = new IntArrayList();
    static public IntArrayList nonZeroIndexListTarget = new IntArrayList();
    DoubleArrayList nonZeroValueListSource = new DoubleArrayList();
    DoubleArrayList nonZeroValueListTarget = new DoubleArrayList();
    static public int numSources;
    static Pointer d_A;
    static Pointer d_B;
    private int numTargets;
    static Pointer d_C;
    static Pointer d_D;
    static double normSource;
    cern.jet.math.Functions F = cern.jet.math.Functions.functions;
    private SparseDoubleMatrix2D batchMatrix;
    static public ArrayList<Double> norms;
    ExecutorService pool = Executors.newFixedThreadPool(Main.nbThreads);
    private int countBatches = 0;
    static public int sizeVector;
    static public Pointer pointerToA;
    static public ArrayList<Pointer> listPointers;
    private double numCalculations;
    String logText = "";
    String newLine = "\n";
    String interval = "--------------------------\n";
    
    int[] sourceIndexes;
    int[] targetIndexes;
    List<Integer> listSourceIndex;
    List<Integer> listTargetIndex;

    Cosine2loops(SparseVector[] listVectors) {

        this.listVectors = listVectors;
    }

    @Override
    public void run() {
        try {
            SparseDoubleMatrix1D sourceDocMatrix = new SparseDoubleMatrix1D(1);
            SparseDoubleMatrix1D targetDocMatrix = new SparseDoubleMatrix1D(1);
            SparseVector svSource = new SparseVector(1);
            SparseVector svTarget = new SparseVector(1);


            Clock computeSimilarityClock = new Clock("computing similarity with\nsparseVectors: " + Main.useSparseVectors + "\ndotProduct: " + Main.dotProductChoice + "\neuclidianDist: " + Main.euclidianDistChoice + "\nnb of threads: " + Main.nbThreads);

            numSources = listVectors.length;
//            System.out.println("numSources: " + numSources);
            numTargets = listVectors[0].size();
            numCalculations = Math.pow(numSources, 2) / 2;
//            System.out.println("numTargets: " + numTargets);
//            batchMatrix = new SparseDoubleMatrix2D(numTargets, Main.batchSize);


  



            Main.similarityMatrix = new FlexCompColMatrix(numSources, numSources);

            System.out.println("size of the similiarity matrix: " + numSources + "x " + numSources);

            //1. iteration through all rows (targets) of the termDocumentMatrix
            JCublas.cublasInit();
            JCublas.setExceptionsEnabled(true);

            d_A = new Pointer();
            d_B = new Pointer();
            d_C = new Pointer();
            d_D = new Pointer();
            JCublas.cublasAlloc(numTargets, Sizeof.DOUBLE, d_A);
            JCublas.cublasAlloc(numTargets, Sizeof.DOUBLE, d_B);
            JCublas.cublasAlloc(numTargets, Sizeof.DOUBLE, d_C);
            JCublas.cublasAlloc(numTargets, Sizeof.DOUBLE, d_D);


//            sizeVector = batchMatrix.rows();

            //pointer to the sourceVector
//            Pointer pointerToA = new Pointer();
//
//            //creation of an array of pointers to targetVectors
//            listPointers = new ArrayList();
//
//            for (int i = 1; i < batchMatrix.columns(); i++) {
//
//                listPointers.add(new Pointer());
//
//            }
//
//            //creation of memory space for the targetVectors
//            for (int i = 0; i < listPointers.size(); i++) {
//                JCublas.cublasAlloc(sizeVector, Sizeof.DOUBLE, listPointers.get(i));
//            }
//
//            //creation of the memory space on the GPU for the sourceVector
//            JCublas.cublasAlloc(sizeVector, Sizeof.DOUBLE, pointerToA);



            norms = new ArrayList();
            
            matrixClock = new Clock("clocking the first " + Main.testruns + " calculus");
            
            for (int i = 0; i < numSources; i++) {
                //System.out.println("matrix treatment... " + ((float) i / (float) numSources * 100) + "%");
                if (Main.useSparseVectors) {
                    //svSource = new SparseVector(new DenseVector(termDocumentMatrix.viewColumn(i).toArray()));
                    svSource = Transformer.listVectors[i];
//                    System.out.println("first element of the first vector: "+listVectors[0].get(1));
                    norms.add(svSource.norm(Vector.Norm.Two));
//                    System.out.println("norm of vector "+ i+ ": "+svSource.norm(Vector.Norm.Two));


                } else {
//                    sourceDocMatrix = (SparseDoubleMatrix1D) termDocumentMatrix.viewColumn(i);
//                    norms.add(sourceDocMatrix.aggregate(Functions.plus, Functions.square));

                    //PrintColtMatrix.printSparseDoubleMatrix1D(sourceDocMatrix);
                }
                //



                for (int j = 0; j < numSources; j++) {
                    if (j < i) {

                        int countBuffer = 0;
                        Main.countCalculus++;
                        //System.out.println(Main.countCalculus);
                        if (Main.countCalculus % 100000 == 0) {
                            long elapsedtimeInSeconds = computeSimilarityClock.getElapsedTime();
                            //double currPercentAdvance = (Main.countCalculus / numCalculations) * 100;
                            double remainingTime = (elapsedtimeInSeconds * numCalculations / Main.countCalculus)-elapsedtimeInSeconds;
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
                        if (Main.countCalculus == Main.testruns) {
                            matrixClock.closeAndPrintClock();
                            System.out.println("Time in Cell:"+ cellTime);
                        }
                        if (Main.parallelCUDA) {

                            if (countBuffer == batchMatrix.columns()) {
                                countBatches++;
                                Double[] listDotProducts = new parallelCUDA(batchMatrix, sourceDocMatrix).call();
                                for (int k = 0; k < listDotProducts.length; k++) {
                                    Main.similarityMatrix.set(i, countBatches + k - 1, (listDotProducts[i] / (norms.get(i) * norms.get(countBatches + k - 1))));
                                    int newBatchSize = Math.min(Main.batchSize, (numSources - Main.batchSize * countBatches));
                                    batchMatrix = new SparseDoubleMatrix2D(numTargets, newBatchSize);
                                    break;
                                }

                            } else {
//                                countBuffer++;
//                                batchMatrix.viewColumn(countBuffer).assign(termDocumentMatrix.viewColumn(j));
                            }
                        }

                        if (Main.useSparseVectors) {
                            //svTarget = new SparseVector(new DenseVector(termDocumentMatrix.viewColumn(j).toArray()));
                            svTarget = Transformer.listVectors[j];
                        } else {
//                            targetDocMatrix = (SparseDoubleMatrix1D) termDocumentMatrix.viewColumn(j);
                            //PrintColtMatrix.printSparseDoubleMatrix1D(targetDocMatrix);
                        }


                        synchronized (Main.similarityMatrix) {
                            if (Main.useSparseVectors) {

                                if (Main.useThreads) {
//                                    Callable<Triple<Integer, Integer, Double>> worker = new CellIterator(svSource, svTarget, i, j);
//                                    Future<Triple<Integer, Integer, Double>> submit = pool.submit(worker);
//                                    list.add(submit);
                                    //System.out.println("in the inner loop, using threads and sparse vectors!");
                                    sourceIndexes = svSource.getIndex();
                                    listSourceIndex = new ArrayList();
                                    for (int s = 0;s<sourceIndexes.length;s++){
                                        
                                        listSourceIndex.add(sourceIndexes[s]);
                                        
                                    }
                                    //System.out.println(listSourceIndex.size());
                                    targetIndexes = svTarget.getIndex();
                                    listTargetIndex = new ArrayList();
                                    for (int s = 0;s<targetIndexes.length;s++){
                                        
                                        listTargetIndex.add(targetIndexes[s]);
                                        
                                    }
                                    //System.out.println(listTargetIndex.size());

                                    listSourceIndex.retainAll(listTargetIndex);
                                    if (!listSourceIndex.isEmpty()){
                                      //System.out.println("intersect btw sets: "+setSource.size());

                                        //pool.execute(new CellIteratorRunnable(svSource, svTarget, i, j));
                                        doCalculus(svSource,svTarget,i,j);
                                    }

                                } else {
                                    Main.similarityMatrix.set(i, j, ((svSource.dot(svTarget)) / (norms.get(i) * norms.get(j))));

                                }
                            } else {
                                Main.similarityMatrix.set(i, j, computeSimilarity(sourceDocMatrix, targetDocMatrix));
                            }
                        }

                    } else {
                        synchronized (Main.similarityMatrix) {
                            Main.similarityMatrix.set(i, j, 0);
                        }

                    }

                }
            }
            if (Main.useThreads) {
//                    for (Future<Triple<Integer, Integer, Double>> future : list) {
//                        try {
//                            Main.similarityMatrix.set(future.get().getLeft(), future.get().getMiddle(), future.get().getRight());
//                        } catch (InterruptedException e) {
//                        } catch (ExecutionException e) {
//                        }
//                    }

                // Clean up
                JCublas.cublasFree(d_A);
                JCublas.cublasFree(d_B);
                JCublas.cublasFree(d_C);
                JCublas.cublasFree(d_D);

                JCublas.cublasShutdown();
            }
            pool.shutdown();
            pool.awaitTermination(1, TimeUnit.DAYS);
            computeSimilarityClock.closeAndPrintClock();


        } catch (InterruptedException ex) {
            Logger.getLogger(Cosine2loops.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static double dotProduct(double A[], double B[]) {

        int n = A.length;
        //System.out.println(n);

        double result = 0;


        if ("Java".equals(Main.dotProductChoice)) {
            result = SDotJava(A, B);
        } else {
            if ("Cuda".equals(Main.dotProductChoice)) {
                result = SDotCuda(A, B, n);
            }
        }

        return result;
    }

    public static double euclidianDist(SparseDoubleMatrix1D A, SparseDoubleMatrix1D B) {

        double h_A[] = A.toArray();
        double h_B[] = B.toArray();
        int n = h_A.length;
        //System.out.println(n);
        double result = 0;

        if ("Java".equals(Main.euclidianDistChoice)) {
            result = euclidianDistJava(A, B);
        } else {
            if ("Cuda".equals(Main.euclidianDistChoice)) {

                result = euclidianDistCuda(h_A, h_B, n);
            }
        }

        return result;
    }

    private static double SDotCuda(double A[], double B[], int n) {

        //JCublas.cublasAlloc(n, Sizeof.DOUBLE, d_A);
        //JCublas.cublasAlloc(n, Sizeof.DOUBLE, d_B);

        // Copy the memory from the host to the device
        JCublas.cublasSetVector(n, Sizeof.DOUBLE, Pointer.to(A), 1, d_A, 1);
        JCublas.cublasSetVector(n, Sizeof.DOUBLE, Pointer.to(B), 1, d_B, 1);

        // Execute SDot
        double dotProductCuda = JCublas.cublasDdot(
                n, d_A, 1, d_B, 1);

        // Clean up
        //JCublas.cublasFree(d_A);
        //JCublas.cublasFree(d_B);

        //System.out.println("dotProduct with Cuda: " + dotProductCuda);

        return dotProductCuda;
    }

    private static double euclidianDistCuda(double A[], double B[], int n) {

        // Allocate memory on the device
//        Pointer d_C = new Pointer();
//        Pointer d_D = new Pointer();
//        JCublas.cublasAlloc(n, Sizeof.DOUBLE, d_C);
//        JCublas.cublasAlloc(n, Sizeof.DOUBLE, d_D);

        // Copy the memory from the host to the device
        JCublas.cublasSetVector(n, Sizeof.DOUBLE, Pointer.to(A), 1, d_C, 1);
        JCublas.cublasSetVector(n, Sizeof.DOUBLE, Pointer.to(B), 1, d_D, 1);


        // Execute SDot
        double AeuclidianCUDA = JCublas.cublasDnrm2(n, d_C, 1);
//        double AeuclidianCUDA = normSource;
        double BeuclidianCUDA = JCublas.cublasDnrm2(n, d_D, 1);


        // Clean up
//        JCublas.cublasFree(d_C);
//        JCublas.cublasFree(d_D);

        double euclidianDistCuda = AeuclidianCUDA * BeuclidianCUDA;
        //System.out.println("euclidianDist with Cuda: " + euclidianDistCuda);
        return euclidianDistCuda;

    }

    private static double euclidianDistJava(SparseDoubleMatrix1D A, SparseDoubleMatrix1D B) {


        //double euclidianDistJava = Statistic.EUCLID.apply(A, B);
        double euclidianDistJava = Math.sqrt(A.zDotProduct(A) * B.zDotProduct(B));
        //System.out.println("euclidianDist with Java: " + euclidianDistJava);

        return euclidianDistJava;


    }

    // his function is useful when the vectors have a length of 1000 or less
    private static double SDotJava(double A[], double B[]) {

        DoubleMatrix1D sourceDoc = new SparseDoubleMatrix1D(A);
        DoubleMatrix1D targetDoc = new SparseDoubleMatrix1D(B);

        double dotProductJava = sourceDoc.zDotProduct(targetDoc);
        //System.out.println("dotProduct with Java: " + dotProductJava);

        return dotProductJava;

    }

    double computeSimilarity(SparseDoubleMatrix1D sourceDoc, SparseDoubleMatrix1D targetDoc) {


        double dotProduct = dotProduct(sourceDoc.toArray(), targetDoc.toArray());
        //System.out.println(dotProduct);

        double euclidianDist = euclidianDist(sourceDoc, targetDoc);
        //double euclidianDist = Statistic.EUCLID.apply(sourceDoc, targetDoc);
//        double euclidianDist = euclidianDist(sourceDoc.toArray(), targetDoc.toArray());
        //System.out.println(eucledianDist);

        return dotProduct / euclidianDist;
//        return dotProduct;
    }
    
        static void doCalculus(SparseVector source, SparseVector target,int i, int j) {

        long currentTime = System.currentTimeMillis();

        double result = source.dot(target) / (Cosine2loops.norms.get(i) * Cosine2loops.norms.get(j));
        //System.out.println("result in the runnable: " + result);
//    Triple similarityResult = new Triple(i,j,result);
        synchronized (Main.similarityMatrix) {
            Main.similarityMatrix.set(i, j, result);
        }
    long endTime = System.currentTimeMillis();
    Cosine2loops.cellTime = Cosine2loops.cellTime + endTime-currentTime; 
    }

}
