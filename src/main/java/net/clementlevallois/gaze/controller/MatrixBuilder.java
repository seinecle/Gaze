/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.clementlevallois.gaze.controller;

import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.*;
import net.clementlevallois.utils.Clock;
import net.clementlevallois.utils.MultiMap;
import net.clementlevallois.utils.Multiset;

/**
 *
 * @author C. Levallois
 */
public class MatrixBuilder {

    private Set<String> setSources = new HashSet();
    private Set<String> setTargets = new HashSet();
    private Set<Integer> setSourcesInteger = new TreeSet();
    private Multiset<Integer> multisetTargets = new Multiset();
    private Map<String, Integer> mapSourcesLabelToIndex = new HashMap();
    private Map<Integer, String> mapSourcesIndexToLabel = new HashMap();
    private Map<String, Integer> mapTargetsLabelToIndex = new HashMap();
    private Map<Integer, String> mapTargetsIndexToLabel = new HashMap();
    private MultiMap<Integer, Integer> mapSourceIndexToTargetIndex = new MultiMap();
    private MultiMap<Integer, Integer> mapTargetIndexToSourceIndex = new MultiMap();

    private String sourceNode;
    private Set<String> targetNodes;
    private int countLines = 0;
    public SparseDoubleMatrix1D[] listVectorsColt;
    private Iterator<Integer> sourcesIt;
    private SparseDoubleMatrix1D vectorColt;
    private final int maxNbTargetsPerSourceConsidered4CosineCalc;
    private Map<String, Set<String>> sourceAndTargets;

    public MatrixBuilder(Map<String, Set<String>> sourceAndTargets, int maxNbTargetsPerSourceConsidered4CosineCalc) {
        this.maxNbTargetsPerSourceConsidered4CosineCalc = maxNbTargetsPerSourceConsidered4CosineCalc;
        this.sourceAndTargets = sourceAndTargets;
    }

    public SparseDoubleMatrix1D[] createListOfSparseVectorsFromEdgeList() throws IOException {
        Integer s = 0;
        Integer t = 0;

        //***
        //
        //#### 1. reading the map of sources and targets and creating indexes and maps from it
        //
        //***
//        Clock readingFile = new Clock("reading input file");
        Iterator<Entry<String, Set<String>>> iteratorInput = sourceAndTargets.entrySet().iterator();
        while (iteratorInput.hasNext()) {
            Entry<String, Set<String>> entry = iteratorInput.next();

            sourceNode = entry.getKey();
            targetNodes = entry.getValue();

            boolean newSource = setSources.add(sourceNode);
            if (newSource) {
                mapSourcesLabelToIndex.put(sourceNode, s);
                mapSourcesIndexToLabel.put(s, sourceNode);
                setSourcesInteger.add(s);
                s++;
            }

            for (String target : targetNodes) {
                boolean newTarget = setTargets.add(target);

                if (newTarget) {
                    mapTargetsLabelToIndex.put(target, t);
                    mapTargetsIndexToLabel.put(t, target);
                    t++;
                }
                //this last line is for the specific purpose of being able to easily count the number of times a node appears as a target in the network
                multisetTargets.addOne(mapTargetsLabelToIndex.get(target));
                mapSourceIndexToTargetIndex.put(mapSourcesLabelToIndex.get(sourceNode), mapTargetsLabelToIndex.get(target));
            }
        }
//        System.out.println("Number of different targets: " + multisetTargets.getElementSet().size());
//        readingFile.closeAndPrintClock();

        //***
        //
        //#### 2. creating a matrix for pairwise comparisons
        //
        //***
//        Clock matrixCreation = new Clock("creating the adjacency matrix from the file");
        //this creates a list of vectors equal to the number of nodes, or just number of sources,
        //depending on whether the network is directed or not
        // a vector is a list of elements which are going to be the stuff of the similarity calculation.
        listVectorsColt = new SparseDoubleMatrix1D[mapSourcesLabelToIndex.size()];

        //this loops through the sources to create the similarity matrix
        sourcesIt = setSourcesInteger.iterator();

        while (sourcesIt.hasNext()) {
            Integer currSource = sourcesIt.next();
            int countTargets = 0;

            Set<Integer> targetsForThisSource = (Set<Integer>) mapSourceIndexToTargetIndex.get(currSource);
            // the user can have a file where a source had zero target
            if (targetsForThisSource == null){
                targetsForThisSource = new HashSet();
            }
            vectorColt = new SparseDoubleMatrix1D(multisetTargets.getElementSet().size());
            Iterator<Integer> targetsIt = targetsForThisSource.iterator();
            while (targetsIt.hasNext()) {
                Integer nextTarget = targetsIt.next();
                vectorColt.set((int) nextTarget, 1.00);
                if (countTargets++ >= maxNbTargetsPerSourceConsidered4CosineCalc) {
                    break;
                }
            }

            //finally, the treatment fot the current node is over and we can put the the vector of its targets/ weights in a list of vectors,
            //over which the cosine calculation will take place (see the CosineCalculation class)
            listVectorsColt[currSource] = vectorColt;
        }
//        System.out.println("adjacency matrix created!");
//        System.out.println("Number of sources (nb of vectors): " + listVectorsColt.length);
//        System.out.println("Number of targets (size of a given vector ): " + listVectorsColt[0].size());
//
//        matrixCreation.closeAndPrintClock();
        return listVectorsColt;
    }

    public Map<Integer, String> getMapSourcesIndexToLabel() {
        return mapSourcesIndexToLabel;
    }
}
