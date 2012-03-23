/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gaze;

import com.google.common.collect.BiMap;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
    Main(String wk,String fileName,String isUndirected,String isUnWeighted, String cosineMin, String maxTargets4Calc, String minOccAsTarget, String minOccAsSource){
        
   this.wk = wk+"\\";
   this.file = fileName;
   this.directedNetwork = !Boolean.valueOf(isUndirected);
        System.out.println("directedNetwork: "+directedNetwork);
        
   this.weightedNetwork = !Boolean.valueOf(isUnWeighted);
        System.out.println("weightedNetwork: "+weightedNetwork);

   this.cosineThreshold = Float.valueOf(cosineMin);
        System.out.println("cosineThreshold: "+cosineThreshold);

   this.maxNbTargetsPerSourceConsidered4CosineCalc = Integer.valueOf(maxTargets4Calc);
        System.out.println("maxNbTargetsPerSourceConsidered4CosineCalc: "+maxNbTargetsPerSourceConsidered4CosineCalc);

   this.minNbofTimesASourceShouldBeCited = Integer.valueOf(minOccAsTarget);
        System.out.println("minNbofTimesASourceShouldBeCited: "+minNbofTimesASourceShouldBeCited);
 
   this.minNbofCitationsASourceShouldMake = Integer.valueOf(minOccAsSource);
        System.out.println("minNbofCitationsASourceShouldMake: "+minNbofCitationsASourceShouldMake);
 
    
    }
    
    
    //

    public static String wk;
    static String file;
    private  String DLoutputFile;
    private  String DLNodesList;
    private String GEXFoutputFile;
    static private String fieldSeparator = ",";
    //
    // ##### parameters
    //
    //
    public static boolean directedNetwork;
    public static boolean weightedNetwork;
    private static double cosineThreshold = 0.05;
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
            AdjacencyMatrixBuilder tr = new AdjacencyMatrixBuilder(fieldSeparator);
            SparseVector[] listVectors = tr.EdgeListToMatrix();

    //        CosineSimilarity cs = new CosineSimilarity();
    //        cs.transform(matrixSource);

            Thread t = new Thread(new CosineCalculation(listVectors));
            t.start();
            t.join();
            System.out.println("Cosine calculated!");

            DLoutputFile = file.replaceAll("\\..*", "") + "_edges_list.dl";
            DLNodesList = file.replaceAll("\\..*", "") + "_nodes_list.dl";
            GEXFoutputFile = file.replaceAll("\\....", "") + "_cosine_version.gexf";



    //        This invert operation symply inversts keys and values in the map for ease of retrieval - nothing more!


            BiMap inverseMapNodes = AdjacencyMatrixBuilder.mapNodes.inverse();
            StringBuilder toBeWritten = new StringBuilder();
            Iterator<MatrixEntry> itSM = similarityMatrix.iterator();
            
//            Clock printEdgesGexf = new Clock ("printing a gexf file for the edges"); 
//            
//            bw = new BufferedWriter(new FileWriter(wk + GEXFoutputFile));
//            toBeWritten = new StringBuilder();
//
//            itSM = similarityMatrix.iterator();
//
//            while (itSM.hasNext()) {
//
//                MatrixEntry currElement = itSM.next();
//                double csCoeff = currElement.get();
////                System.out.println("curr Cosine coeff is: "+csCoeff);
//
//                if (directedNetwork) {
////                    System.out.println("before the condition");
////                    System.out.println("currElement.column(): "+inverseMapSources.get((short) currElement.column())+ "  "+currElement.column());
////                    System.out.println("currElement.row(): "+inverseMapSources.get((short) currElement.row())+ "  "+currElement.row());
//
//                    int targetPerSourceColumn = AdjacencyMatrixBuilder.map.get((short) currElement.column()).size();
//                    int targetPerSourceRow = AdjacencyMatrixBuilder.map.get((short) currElement.row()).size();
//
////                    System.out.println("number of targets corresponding to this source (col): "+targetPerSourceColumn);
////                    System.out.println("number of targets corresponding to this source (row): "+targetPerSourceRow);                
//                    if ((csCoeff > cosineThreshold)
//                            & (targetPerSourceColumn >= minNbofCitationsASourceShouldMake) & (targetPerSourceRow >= minNbofCitationsASourceShouldMake)
//                            ) {
//
////                        System.out.println("past the condition");
//                        toBeWritten.append("<edge source = \"").append(inverseMapSources.get((short) currElement.column())).append("\" target = \"").append(inverseMapSources.get((short) currElement.row())).append("\" weight = \"").append(csCoeff).append("\"/>").append("\n");
//
//                    }
//
//                } else {
//
//
//                    if (csCoeff > cosineThreshold) {
//
//                        toBeWritten.append("<edge source = \"").append(inverseMapNodes.get((short) currElement.column())).append("\" target = \"").append(inverseMapNodes.get((short) currElement.row())).append("\" weight = \"").append(csCoeff).append("\"/>").append("\n");
//                    }
//                }
//            }
//            
//            printEdgesGexf.closeAndPrintClock();
            //iterate through edges and write them in a gexf file

//            bw.write(toBeWritten.toString());
//            bw.close();

            
            Clock printEdgesDL = new Clock ("printing a DL file for the edges"); 
            bw = new BufferedWriter(new FileWriter(wk + DLoutputFile));
            toBeWritten = new StringBuilder();
            toBeWritten.append("source,target,Weight,type\n");



            itSM = similarityMatrix.iterator();

            while (itSM.hasNext()) {

                MatrixEntry currElement = itSM.next();
                double csCoeff = currElement.get();

                if (directedNetwork) {
//                    System.out.println("before the condition");
//                    System.out.println("currElement.column(): "+inverseMapNodes.get((short) currElement.column())+ "  "+currElement.column());
//                    System.out.println("currElement.row(): "+inverseMapNodes.get((short) currElement.row())+ "  "+currElement.row());

                    int nbOccAsSourceColumn = AdjacencyMatrixBuilder.mapUndirected.get((short) currElement.column()).size();
                    int nbOccAsSourceRow = AdjacencyMatrixBuilder.mapUndirected.get((short) currElement.row()).size();
                    int nbOccAsTargetColumn = AdjacencyMatrixBuilder.multisetTargets.count((short)currElement.column());
                    int nbOccAsTargetRow = AdjacencyMatrixBuilder.multisetTargets.count((short)currElement.row());
//                    System.out.println("occurrences as source (col): "+nbOccAsSourceColumn);
//                    System.out.println("occurrences as source (row): "+nbOccAsSourceRow);                
//                    System.out.println("occurrences as target (col): "+nbOccAsTargetColumn);
//                    System.out.println("occurrences as target (row): "+nbOccAsTargetRow);                
//                    System.out.println("cosine for this pair is: "+csCoeff);
                    
                    if ((csCoeff > cosineThreshold)
                            & (nbOccAsSourceColumn > minNbofCitationsASourceShouldMake) & (nbOccAsSourceRow > minNbofCitationsASourceShouldMake)
                            & (nbOccAsTargetColumn > minNbofTimesASourceShouldBeCited) & (nbOccAsTargetRow > minNbofTimesASourceShouldBeCited)
                            ) {

//                        System.out.println("past the condition");
                        toBeWritten.append(inverseMapNodes.get((short) currElement.column())).append(",").append(inverseMapNodes.get((short) currElement.row())).append(",").append(csCoeff).append(",").append("undirected").append("\n");

                    }

                } else {


                    if (csCoeff > cosineThreshold) {

                       toBeWritten.append(inverseMapNodes.get((short) currElement.column())).append(",").append(inverseMapNodes.get((short) currElement.row())).append(",").append(csCoeff).append(",").append("undirected").append("\n");                    }
                }
            }


            bw.write(toBeWritten.toString());
            bw.close();
            printEdgesDL.closeAndPrintClock();
            
            Clock printNodesListCSV = new Clock ("printing a list of nodes with attributes in a csv form"); 
            bw = new BufferedWriter(new FileWriter(wk + DLNodesList));
            toBeWritten = new StringBuilder();
            toBeWritten.append("id,centrality\n");

            Iterator<Entry<String,Short>> ITMapNodes = AdjacencyMatrixBuilder.mapNodes.entrySet().iterator();
            
            while (ITMapNodes.hasNext()){
                Entry<String,Short> currEntry = ITMapNodes.next();
//                System.out.println("currEntry name: "+currEntry.getKey());
//                System.out.println("currEntry count: "+AdjacencyMatrixBuilder.mapUndirected.keys().count(currEntry.getValue()));
//                System.out.println("currEntry times referenced: "+Collections.frequency(AdjacencyMatrixBuilder.mapUndirected.values(),currEntry.getValue()));
                
                if(AdjacencyMatrixBuilder.multisetTargets.count(currEntry.getValue())<=minNbofTimesASourceShouldBeCited | AdjacencyMatrixBuilder.mapUndirected.keys().count(currEntry.getValue())<= minNbofCitationsASourceShouldMake)
                        continue;
                int currCentrality = AdjacencyMatrixBuilder.mapBetweenness.get(currEntry.getValue());
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
