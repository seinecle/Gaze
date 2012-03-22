/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cosine;

import com.google.common.collect.BiMap;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import levallois.clement.utils.Clock;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.sparse.FlexCompColMatrix;
import no.uib.cipr.matrix.sparse.SparseVector;

/**
 *
 * @author C. Levallois
 */
public class Main implements Runnable{
    //
    //  ##### source files
    //
    Main(String wk,String fileName,String isUndirected,String isUnWeighted){
        
   this.wk = wk+"\\";
   this.file = fileName;
   this.directedNetwork = !Boolean.valueOf(isUndirected);
 
    
    }
    
    
    //

    public static String wk = "D:\\Docs Pro Clement\\NESSHI\\Project bibliometrics\\All Journals\\";
    //    static final String wk = "D:\\Docs Pro Clement\\Writing\\HET map\\";
   public static String file = "GEPHImap2010.dl";
//    static final String file = "sample_for_test.txt";
    //static final String file = "twitter.dl";
    private  String DLoutputFile;
    private  String DLNodesList;
    private String GEXFoutputFile;
    //static final String file = "edges_list.csv";
//    static final String file = "test.csv";
    static private String fieldSeparator = ",";
    //
    // ##### parameters
    //
    //
    public static boolean directedNetwork = true;
    private double cosineThreshold = 0.05;
    public static int maxNbTargetsPerSourceConsidered4CosineCalc = 1000;
    public static int minNbofCitationsASourceShouldMake = 5;
    public static int minNbofTimesASourceShouldBeCited = 5;
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

    public static FlexCompColMatrix similarityMatrix;
    static public int countFinishedThreads = 0;
    static BufferedWriter bw;
    static String currLine;
    public static int countCalculus = 0;

    @Override
    public void run()  {
        try {
            Transformer tr = new Transformer(fieldSeparator);
            SparseVector[] listVectors = tr.EdgeListToMatrix();

    //        CosineSimilarity cs = new CosineSimilarity();
    //        cs.transform(matrixSource);

            Thread t = new Thread(new Cosine2loops(listVectors));
            t.start();
            t.join();

            DLoutputFile = file.replaceAll("\\....", "") + "_cosine_version.dl";
            DLNodesList = file.replaceAll("\\....", "") + "_nodes_list.dl";
            GEXFoutputFile = file.replaceAll("\\....", "") + "_cosine_version.gexf";

            System.out.println("Cosine calculated!");

    //        This invert operation symply inversts keys and values in the map for ease of retrieval - nothing more!


            BiMap inverseMapNodes = Transformer.mapNodes.inverse();
            BiMap inverseMapSources = Transformer.mapSources.inverse();
            
            Clock printEdgesGexf = new Clock ("printing a gexf file for the edges"); 
            
            bw = new BufferedWriter(new FileWriter(wk + GEXFoutputFile));
            StringBuilder toBeWritten = new StringBuilder();

            Iterator<MatrixEntry> itSM = similarityMatrix.iterator();

            while (itSM.hasNext()) {

                MatrixEntry currElement = itSM.next();
                double csCoeff = currElement.get();
//                System.out.println("curr Cosine coeff is: "+csCoeff);

                if (directedNetwork) {
//                    System.out.println("before the condition");
//                    System.out.println("currElement.column(): "+inverseMapSources.get((short) currElement.column())+ "  "+currElement.column());
//                    System.out.println("currElement.row(): "+inverseMapSources.get((short) currElement.row())+ "  "+currElement.row());

                    int targetPerSourceColumn = Transformer.map.get((short) currElement.column()).size();
                    int targetPerSourceRow = Transformer.map.get((short) currElement.row()).size();

//                    System.out.println("number of targets corresponding to this source (col): "+targetPerSourceColumn);
//                    System.out.println("number of targets corresponding to this source (row): "+targetPerSourceRow);                
                    if ((csCoeff > cosineThreshold)
                            & (targetPerSourceColumn >= minNbofCitationsASourceShouldMake) & (targetPerSourceRow >= minNbofCitationsASourceShouldMake)
                            ) {

//                        System.out.println("past the condition");
                        toBeWritten.append("<edge source = \"").append(inverseMapSources.get((short) currElement.column())).append("\" target = \"").append(inverseMapSources.get((short) currElement.row())).append("\" weight = \"").append(csCoeff).append("\"/>").append("\n");

                    }

                } else {


                    if (csCoeff > cosineThreshold) {

                        toBeWritten.append("<edge source = \"").append(inverseMapNodes.get((short) currElement.column())).append("\" target = \"").append(inverseMapNodes.get((short) currElement.row())).append("\" weight = \"").append(csCoeff).append("\"/>").append("\n");
                    }
                }
            }
            
            printEdgesGexf.closeAndPrintClock();
            //iterate through edges and write them in a gexf file

            bw.write(toBeWritten.toString());
            bw.close();

            
            Clock printEdgesDL = new Clock ("printing a DL file for the edges"); 
            bw = new BufferedWriter(new FileWriter(wk + DLoutputFile));
            toBeWritten = new StringBuilder();
            toBeWritten.append("source,target,Weight\n");



            itSM = similarityMatrix.iterator();

            while (itSM.hasNext()) {

                MatrixEntry currElement = itSM.next();
                double csCoeff = currElement.get();

                if (directedNetwork) {
//                    System.out.println("before the condition");
//                    System.out.println("currElement.column(): "+inverseMapSources.get((short) currElement.column())+ "  "+currElement.column());
//                    System.out.println("currElement.row(): "+inverseMapSources.get((short) currElement.row())+ "  "+currElement.row());

                    int targetPerSourceColumn = Transformer.mapUndirected.get((short) currElement.column()).size();
                    int targetPerSourceRow = Transformer.mapUndirected.get((short) currElement.row()).size();
                    int nbTimesSourceCitedInTargetsColumn = Transformer.multisetTargets.count((short)currElement.column());
                    int nbTimesSourceCitedInTargetsRow = Transformer.multisetTargets.count((short)currElement.row());
//                    System.out.println("number of targets corresponding to this source (col): "+targetPerSourceColumn);
//                    System.out.println("number of targets corresponding to this source (row): "+targetPerSourceRow);                
//                    System.out.println("this source (column) is also mentioned this nb of times as target: "+nbTimesSourceCitedInTargetsColumn);
//                    System.out.println("this source (row) is also mentioned this nb of times as target: "+nbTimesSourceCitedInTargetsRow);                
//                    System.out.println("cosine for this pair is: "+csCoeff);
                    
                    if ((csCoeff > cosineThreshold)
                            & (targetPerSourceColumn > minNbofCitationsASourceShouldMake) & (targetPerSourceRow > minNbofCitationsASourceShouldMake)
                            & (nbTimesSourceCitedInTargetsColumn > minNbofTimesASourceShouldBeCited) & (nbTimesSourceCitedInTargetsRow > minNbofTimesASourceShouldBeCited)
                            ) {

//                        System.out.println("past the condition");
                        toBeWritten.append(inverseMapSources.get((short) currElement.column())).append(",").append(inverseMapSources.get((short) currElement.row())).append(",").append(csCoeff).append("\n");

                    }

                } else {


                    if (csCoeff > cosineThreshold) {

                       toBeWritten.append(inverseMapSources.get((short) currElement.column())).append(",").append(inverseMapSources.get((short) currElement.row())).append(",").append(csCoeff).append("\n");                    }
                }
            }


            bw.write(toBeWritten.toString());
            bw.close();
            printEdgesDL.closeAndPrintClock();
            
            Clock printNodesListCSV = new Clock ("printing a list of nodes with attributes in a csv form"); 
            bw = new BufferedWriter(new FileWriter(wk + DLNodesList));
            toBeWritten = new StringBuilder();
            toBeWritten.append("id,centrality\n");

            Iterator<Entry<String,Short>> ITMapNodes = Transformer.mapNodes.entrySet().iterator();
            
            while (ITMapNodes.hasNext()){
                Entry<String,Short> currEntry = ITMapNodes.next();
//                System.out.println("currEntry name: "+currEntry.getKey());
//                System.out.println("currEntry count: "+Transformer.mapUndirected.keys().count(currEntry.getValue()));
//                System.out.println("currEntry times referenced: "+Collections.frequency(Transformer.mapUndirected.values(),currEntry.getValue()));
                
                if(Transformer.multisetTargets.count(currEntry.getValue())<=minNbofTimesASourceShouldBeCited | Transformer.mapUndirected.keys().count(currEntry.getValue())<= minNbofCitationsASourceShouldMake)
                        continue;
                int currCentrality = Transformer.mapBetweenness.get(currEntry.getValue());
                toBeWritten.append(currEntry.getKey()).append(",").append(currCentrality).append("\n");
            }
            
            bw.write(toBeWritten.toString());
            bw.close();
            printNodesListCSV.closeAndPrintClock();
            
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
