/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.clementlevallois.gaze.controller;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.clementlevallois.utils.UnDirectedPair;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphFactory;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.plugin.ExporterGEXF;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author LEVALLOIS
 */
public class SimilarityFunction {

    public String createSimilarityGraph(Map<String, Set<String>> sourcesAndTargets, int minTargetsInCommon) {
        try {
            Graph graphResult;
            GraphModel gm;
            Workspace workspace;
            boolean applyPMI = false;

            double cosineThreshold = 0.05;
            int maxNbTargetsPerSourceConsidered4CosineCalc = 1000;

            MatrixBuilder matrixBuilder = new MatrixBuilder(sourcesAndTargets, maxNbTargetsPerSourceConsidered4CosineCalc);

            SparseDoubleMatrix1D[] listVectors = matrixBuilder.createListOfSparseVectorsFromEdgeList();

            CosineCalculation cosineCalculation = new CosineCalculation(listVectors, minTargetsInCommon);
            cosineCalculation.run();

            SparseDoubleMatrix2D similarityMatrixColt = cosineCalculation.getSimilarityMatrixColt();
            SparseDoubleMatrix2D sharedTargetsMatrixColt = cosineCalculation.getSharedTargetsMatrixColt();

            IntArrayList rowListSimMatrix = new IntArrayList();
            IntArrayList columnListSimMatrix = new IntArrayList();
            DoubleArrayList valueListSimMatrix = new DoubleArrayList();
            IntArrayList rowListNumberSharedTargets = new IntArrayList();
            IntArrayList columnListNumberSharedTargets = new IntArrayList();
            DoubleArrayList valueListNumberSharedTargets = new DoubleArrayList();
            similarityMatrixColt.getNonZeros(rowListSimMatrix, columnListSimMatrix, valueListSimMatrix);
            sharedTargetsMatrixColt.getNonZeros(rowListNumberSharedTargets, columnListNumberSharedTargets, valueListNumberSharedTargets);

            int nonZeroCell = 0;
            int nbOfNonZeroCells = rowListSimMatrix.size();
            Set<String> nodesString = new HashSet();
            Map<UnDirectedPair, Double> mapUnDirectedPairsToTheirWeight = new HashMap();
            Map<UnDirectedPair, Double> mapUnDirectedPairsToTheirNumberOfSharedTargets = new HashMap();
            for (nonZeroCell = 0; nonZeroCell < nbOfNonZeroCells; nonZeroCell++) {
                int rowIndex = rowListSimMatrix.get(nonZeroCell);
                int colIndex = columnListSimMatrix.get(nonZeroCell);
                double cellValueSim = valueListSimMatrix.get(nonZeroCell);
                double cellValueNumberOfSharedTargets = valueListNumberSharedTargets.get(nonZeroCell);

                String sourceLabel = matrixBuilder.getMapSourcesIndexToLabel().get(rowIndex);
                String targetLabel = matrixBuilder.getMapSourcesIndexToLabel().get(colIndex);

                nodesString.add(sourceLabel);
                nodesString.add(targetLabel);

                UnDirectedPair newPair = new UnDirectedPair(sourceLabel, targetLabel);
                mapUnDirectedPairsToTheirWeight.put(newPair, cellValueSim);
                mapUnDirectedPairsToTheirNumberOfSharedTargets.put(newPair, cellValueNumberOfSharedTargets);
            }

            ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
            pc.newProject();
            workspace = pc.getCurrentWorkspace();

            //Get a graph model - it exists because we have a workspace
            gm = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);

            Column sharedTargetsColumn = gm.getEdgeTable().addColumn("shared targets", Integer.class);

            GraphFactory factory = gm.factory();
            graphResult = gm.getGraph();

            Set<Node> nodes = new HashSet();
            Node node;
            Map<String, String> nodeLabelToId = new HashMap();
            int i = 0;
            for (String nodeString : nodesString) {
                String id = String.valueOf(i);
                nodeLabelToId.put(nodeString, id);
                node = factory.newNode(id);
                node.setLabel(nodeString);
                nodes.add(node);
                i++;
            }

            Set<Map.Entry<String, Set<String>>> entrySet = sourcesAndTargets.entrySet();
            Iterator<Map.Entry<String, Set<String>>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Set<String>> nextEntry = iterator.next();
                String source = nextEntry.getKey();
                if (!nodesString.contains(source)) {
                    String id = String.valueOf(i);
                    nodeLabelToId.put(source, id);
                    node = factory.newNode(id);
                    node.setLabel(source);
                    nodes.add(node);
                    i++;
                }
            }

            graphResult.addAllNodes(nodes);

            Set<Edge> edgesForGraph = new HashSet();
            Edge edge;
            Iterator<Map.Entry<UnDirectedPair, Double>> iteratorEdgesToCreate = mapUnDirectedPairsToTheirWeight.entrySet().iterator();
            while (iteratorEdgesToCreate.hasNext()) {
                Map.Entry<UnDirectedPair, Double> entry = iteratorEdgesToCreate.next();
                String nodeSourceLabel = (String) entry.getKey().getLeft();
                String nodeTargetLabel = (String) entry.getKey().getRight();
                Node nodeSource = graphResult.getNode(nodeLabelToId.get(nodeSourceLabel));
                Node nodeTarget = graphResult.getNode(nodeLabelToId.get(nodeTargetLabel));
                edge = factory.newEdge(nodeSource, nodeTarget, 0, entry.getValue(), false);
                int numberOfSharedTargets = mapUnDirectedPairsToTheirNumberOfSharedTargets.get(entry.getKey()).intValue();
                edge.setAttribute(sharedTargetsColumn, numberOfSharedTargets);
                edgesForGraph.add(edge);
            }

            graphResult.addAllEdges(edgesForGraph);

            ExportController ec = Lookup.getDefault().lookup(ExportController.class);
            ExporterGEXF exporterGexf = (ExporterGEXF) ec.getExporter("gexf");
            exporterGexf.setWorkspace(workspace);
            exporterGexf.setExportDynamic(false);

            StringWriter stringWriter = new StringWriter();
            ec.exportWriter(stringWriter, exporterGexf);
            stringWriter.close();
            return stringWriter.toString();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return "";

    }

}
