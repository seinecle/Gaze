/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cosine;

import com.google.common.collect.BiMap;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.sparse.FlexCompColMatrix;
import no.uib.cipr.matrix.sparse.SparseVector;

/**
 *
 * @author C. Levallois
 */
public class Main {
    //
    //  ##### source files
    //
    //

    public static final String wk = "D:\\Docs Pro Clement\\NESSHI\\Project bibliometrics\\All Journals\\";
    //    static final String wk = "D:\\Docs Pro Clement\\Writing\\HET map\\";
        public static final String file = "GEPHImap2010.dl";
//    static final String file = "sample_for_test.txt";
    //static final String file = "twitter.dl";
    private static String DLoutputFile = file.replaceAll("\\....", "") + "_cosine_version.dl";
    private static String GEXFoutputFile = file.replaceAll("\\....", "") + "_cosine_version.gexf";
    //static final String file = "edges_list.csv";
//    static final String file = "test.csv";
    static private String fieldSeparator = ",";
    //
    // ##### parameters
    //
    //
    public static boolean directedNetwork = true;
    private static double cosineThreshold = 0.50;
    public static int thresholdTargets = 15;
    public static int thresholdTimesCited = 15;
    public static int nbThreads = 8;
    public static int batchSize = 1;
    public static int testruns = 25000;
    public static boolean useSparseVectors = true;
    public static boolean useThreads = true;
    static boolean parallelCUDA = false;
    public static String euclidianDistChoice = "Cuda";
    public static String dotProductChoice = "Cuda";
    //
    // ##### objects and variables
    //
    //
    static HashMap mapJournals = new HashMap();
    public static FlexCompColMatrix similarityMatrix;
    static public int countFinishedThreads = 0;
    static BufferedWriter bw;
    static String currLine;
    public static int countCalculus = 0;

    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException, ExecutionException {


        Transformer tr = new Transformer(fieldSeparator);
        SparseVector[] listVectors = tr.EdgeListToMatrix();

//        CosineSimilarity cs = new CosineSimilarity();
//        cs.transform(matrixSource);

        Thread t = new Thread(new Cosine2loops(listVectors));
        t.start();
        t.join();



        System.out.println("Cosine calculated!");

//        This invert operation symply inversts keys and values in the map for ease of retrieval - nothing more!
//        BiMap<Integer, String> invertedMap = tr.mapSources.inverse();

        BiMap inverseMapNodes = Transformer.mapNodes.inverse();
        BiMap inverseMapSources = Transformer.mapSources.inverse();

        bw = new BufferedWriter(new FileWriter(wk + GEXFoutputFile));
        StringBuilder toBeWritten = new StringBuilder();

        Iterator<MatrixEntry> itSM = similarityMatrix.iterator();

        while (itSM.hasNext()) {

            MatrixEntry currElement = itSM.next();
            double csCoeff = currElement.get();

            if (directedNetwork) {
//                System.out.println("before the condition");
//                System.out.println("currElement.column(): "+inverseMapSources.get((short) currElement.column())+ "  "+currElement.column());
//                System.out.println("currElement.row(): "+inverseMapSources.get((short) currElement.row())+ "  "+currElement.row());

                int targetPerSourceColumn = Transformer.map.get((short) currElement.column()).size();
                int targetPerSourceRow = Transformer.map.get((short) currElement.row()).size();

//                System.out.println("number of targets corresponding to this source (col): "+targetPerSourceColumn);
//                System.out.println("number of targets corresponding to this source (row): "+targetPerSourceRow);                
                if ((csCoeff > cosineThreshold)
                        & (targetPerSourceColumn >= thresholdTimesCited) & (targetPerSourceRow >= thresholdTimesCited)
                        ) {

//                    System.out.println("past the condition");
                    toBeWritten.append("<edge source = \"").append(inverseMapSources.get((short) currElement.column())).append("\" target = \"").append(inverseMapSources.get((short) currElement.row())).append("\" weight = \"").append(csCoeff).append("\"/>").append("\n");

                }

            } else {


                if (csCoeff > cosineThreshold) {

                    toBeWritten.append("<edge source = \"").append(inverseMapNodes.get((short) currElement.column())).append("\" target = \"").append(inverseMapNodes.get((short) currElement.row())).append("\" weight = \"").append(csCoeff).append("\"/>").append("\n");
                }
            }
        }

        //load the index of journals
//        br = new BufferedReader(new FileReader(wk+"indexJournals.txt"));
//        while ((currLine = br.readLine())!=null){
//            String []fields = currLine.split(",");
//            mapJournals.put(fields[0], fields[1]);}

        //iterate through edges and write them in a gexf file

        bw.write(toBeWritten.toString());
        bw.close();

        bw = new BufferedWriter(new FileWriter(wk + DLoutputFile));
        toBeWritten = new StringBuilder();

        Iterator<MatrixEntry> itSMDL = similarityMatrix.iterator();

        while (itSMDL.hasNext()) {

            MatrixEntry currElement = itSMDL.next();
            double csCoeff = currElement.get();
            if (csCoeff > cosineThreshold & Transformer.multisetTargets.count(inverseMapNodes.get(currElement.column())) > thresholdTimesCited & Transformer.multisetTargets.count(inverseMapNodes.get(currElement.row())) > thresholdTimesCited) {
//                        System.out.println(i);
//                        System.out.println(j);
//                        System.out.println(csCoeff);
                toBeWritten.append(inverseMapNodes.get(currElement.column())).append(",").append(inverseMapNodes.get(currElement.row())).append(",").append(csCoeff).append("\n");

            }
        }

        //load the index of journals
//        br = new BufferedReader(new FileReader(wk+"indexJournals.txt"));
//        while ((currLine = br.readLine())!=null){
//            String []fields = currLine.split(",");
//            mapJournals.put(fields[0], fields[1]);}

        //iterate through edges and write them in a gexf file

        bw.write(toBeWritten.toString());
        bw.close();
    }
}
