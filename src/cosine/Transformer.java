/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cosine;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.TreeMultimap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.*;
import levallois.clement.utils.Clock;
import no.uib.cipr.matrix.sparse.SparseVector;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 *
 * @author C. Levallois
 */
public class Transformer {

    private BufferedReader br;
    private final String str;
    private String currLine;
    private HashSet<String> setNodes = new HashSet();
    private HashSet<String> setSources = new HashSet();
    private HashSet<String> setTargets = new HashSet();
    private TreeSet<Short> setSourcesShort = new TreeSet();
    public static TreeSet<Short> setTargetsShort = new TreeSet();
    public static HashMultiset<Short> multisetTargets = HashMultiset.create();
    public static HashMultiset<Short> multisetSources = HashMultiset.create();
    public static HashBiMap<String, Short> mapNodes = HashBiMap.create();
    public static HashBiMap<String, Short> mapSources = HashBiMap.create();
    public static HashBiMap<String, Short> mapTargets = HashBiMap.create();
    public static TreeMultimap<Short, Short> map = TreeMultimap.create();
    public static TreeMultimap<Short, Short> mapUndirected = TreeMultimap.create();
    public static TreeMultimap<Short, Short> mapInverse = TreeMultimap.create();
    private String sourceNode;
    private String targetNode;
    private Float weight;
    private HashMap<Pair<Short, Short>, Float> mapEdgeToWeight = new HashMap();
    private int countLines = 0;
    public static SparseVector[] listVectors;
    private static Iterator<Short> nodesIt;
    private static SparseVector vectorMJT;
    public static HashMap<Short,Integer> mapBetweenness;
    
    
    Transformer(String str) {

        this.str = str;

    }

    SparseVector[] EdgeListToMatrix() throws IOException {

        // i - Row index: Targets
        // j - Column index: Sources



        short n = 0;
        short s = 0;
        short t = 0;
        Clock readingFile = new Clock("reading input file");
        br = new BufferedReader(new FileReader(Main.wk + Main.file));
        currLine = br.readLine();
        while (currLine != null) {
            countLines++;
            if ("stop".equals(currLine)) {
                break;
            }
            String[] fields = currLine.split(str);
            sourceNode = fields[0];
            targetNode = fields[1];
            weight = Float.valueOf(fields[2]);

            boolean newNode1 = setNodes.add(sourceNode);
            boolean newNode2 = setNodes.add(targetNode);

            if (newNode1) {
                mapNodes.put(sourceNode, n);
                n++;
            }

            if (newNode2) {
                mapNodes.put(targetNode, n);

                n++;
            }


            if (Main.directedNetwork) {
                boolean newSource = setSources.add(sourceNode);
                boolean newTarget = setTargets.add(targetNode);

                if (newSource) {
                    mapSources.put(sourceNode, s);
                    setSourcesShort.add(s);
                    s++;

                }
                if (newTarget) {
                    mapTargets.put(targetNode, t);
                    setTargetsShort.add(t);
                    
                    t++;
                }
                multisetTargets.add(mapNodes.get(targetNode));

            }

//            if (weight < 0.0001) {
//                weight = 0.00;
//            }


            if (Main.directedNetwork) {
                map.put(mapSources.get(sourceNode), mapTargets.get(targetNode));
                mapUndirected.put(mapNodes.get(sourceNode), mapNodes.get(targetNode));
//                System.out.println("put in the map: --key: "+mapSources.get(sourceNode)+" value: "+mapTargets.get(targetNode));
                mapEdgeToWeight.put(new Pair(mapSources.get(sourceNode), mapTargets.get(targetNode)), weight);


            } else {
                map.put(mapNodes.get(sourceNode), mapNodes.get(targetNode));
                mapInverse.put(mapNodes.get(targetNode), mapNodes.get(sourceNode));
                mapEdgeToWeight.put(new Pair(mapNodes.get(sourceNode), mapNodes.get(targetNode)), weight);

            }

            currLine = br.readLine();
        }
        br.close();

        System.out.println("Number of edges Source // Target treated: " + countLines);
        System.out.println("Size of the list of vectors: " + setNodes.size());


        readingFile.closeAndPrintClock();
        
        
        
        Clock matrixCreation = new Clock("creating the adjacency matrix from the file");
        //this creates a list of vectors equal to the number of nodes, or just number of sources,
        //depending on whether the network is directed or not
        if (Main.directedNetwork) {
            listVectors = new SparseVector[setSourcesShort.size()];
        } else {
            listVectors = new SparseVector[setNodes.size()];
        }
        setNodes.clear();
        setTargets.clear();

        //this loops through all nodes, or just the sources, to create the similarity matrix
        //depending on whether the network is directed or not
        if (Main.directedNetwork) {

            nodesIt = setSourcesShort.iterator();
        } else {

            nodesIt = mapNodes.values().iterator();
        }

        
        while (nodesIt.hasNext()) {
            
            

            Short currNode = nodesIt.next();
//            System.out.println("number of targets associated with source "+currNode+": "+Transformer.map.get(currNode).size());

            SortedSet<Short> targets = new TreeSet();



            if (Main.directedNetwork) {
//                System.out.println("currNode: " + mapSources.inverse().get(currNode)+" (index ="+currNode+")");
                targets = map.get(currNode);
//                System.out.println("Size of the set of connected nodes for node " + mapSources.inverse().get(currNode) + ": " + targets.size());
//                System.out.println("list of connected nodes:" +targets);

            } else {
//                System.out.println(mapNodes.inverse().get(currNode));

                targets.addAll(map.get(currNode));
                targets.addAll(mapInverse.get(currNode));
//                System.out.println("Size of the set of connected nodes for node " + mapNodes.inverse().get(currNode) + ": " + targets.size());

            }


            
            Iterator<Short> targetsIt = targets.iterator();
            TreeMap<Float, Short> setCurrWeights = new TreeMap();
            while (targetsIt.hasNext()) {

                Short currTarget = targetsIt.next();
//                System.out.println("current connected Node: " + currTarget);

                Float currWeight = mapEdgeToWeight.get(new Pair(currNode, currTarget));
                if (currWeight == null) {

                    continue;
                }
//                    currWeight = mapEdgeToWeight.get(new Pair(currTarget, currNode));
//                System.out.println("currWeight: " + currWeight);
                setCurrWeights.put(currWeight, currTarget);
            }


            if (!Main.directedNetwork) {
                targetsIt = targets.iterator();
                while (targetsIt.hasNext()) {

                    Short currTarget = targetsIt.next();
//                    System.out.println("current connected Node (in undirected mode): " + currTarget);

                    Float currWeight = mapEdgeToWeight.get(new Pair(currTarget, currNode));
                    if (currWeight == null) {
                        continue;
                    }
//                    currWeight = mapEdgeToWeight.get(new Pair(currTarget, currNode));
//                    System.out.println("currWeight: " + currWeight);
                    setCurrWeights.put(currWeight,currTarget);
                }
            }


            int countTargets = 0;
            NavigableMap descMap = setCurrWeights.descendingMap();
            Iterator<Entry<Float, Short>> ITsetCurrWeights = descMap.entrySet().iterator();

//            System.out.println("nb of targets: " + targets.size());

            if (Main.directedNetwork) {
                vectorMJT = new SparseVector(setTargetsShort.size());

            } else {
                vectorMJT = new SparseVector(mapNodes.size());
            }

            //this is where the threshold of how many targets are considered for the calculus of the cosine.
            while (ITsetCurrWeights.hasNext()) {
                Entry <Float, Short> currEntry = ITsetCurrWeights.next();
                Short currTarget = currEntry.getValue();
//                System.out.println("current connected node in the loop: " + currTarget);
                Float currWeight = currEntry.getKey();
//                System.out.println("to which the current weight considered for inclusion is: "+currWeight);
                if (Main.directedNetwork & countTargets >= Main.maxNbTargetsPerSourceConsidered4CosineCalc) {
//                    System.out.println("breaking on " + currWeight);
                    break;
                }

                countTargets++;


                int vectorPos = (int) currTarget;
//                System.out.println("vectorPos: " + vectorPos);
//                System.out.println("currWeight: " + currWeight);

                vectorMJT.set(vectorPos, (double) currWeight);

            }
//            System.out.println("count targets: " + countTargets);
            listVectors[currNode] = vectorMJT;
            //System.out.println(vectorMJT.getIndex().length);
        }
        System.out.println("adjacency matrix created!");
        System.out.println("Number of sources (vectors): " + listVectors.length);
        System.out.println("Number of targets (size of a vector): " + listVectors[0].size());



        mapEdgeToWeight.clear();
        matrixCreation.closeAndPrintClock();

        InitialGraphCentrality IGC = new InitialGraphCentrality(mapUndirected);
        DirectedGraph<Short, DefaultEdge> g = IGC.getGraph();
        mapBetweenness = new HashMap();
        
//        System.out.println("size of mapNodes: "+mapNodes.size());
 
        for (short vertex = 0;vertex<mapNodes.size();vertex++){
        
            if(Main.directedNetwork){
//                System.out.println(mapNodes.inverse().get(vertex)+" "+g.inDegreeOf(vertex));    
            mapBetweenness.put(vertex,g.inDegreeOf(vertex));
            }
            else{
                        mapBetweenness.put(vertex,g.inDegreeOf(vertex)+g.outDegreeOf(vertex));
            }
        }
        return listVectors;




    }
}
