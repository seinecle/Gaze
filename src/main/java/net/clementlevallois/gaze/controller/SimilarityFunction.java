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

    public String createSimilarityGraph(Map<String, Set<String>> sourcesAndTargets) {
        try {
            Graph graphResult;
            GraphModel gm;
            Workspace workspace;
            boolean applyPMI = false;

            double cosineThreshold = 0.05;
            int maxNbTargetsPerSourceConsidered4CosineCalc = 1000;

            SparseDoubleMatrix2D similarityMatrixColt;

            MatrixBuilder matrixBuilder = new MatrixBuilder(sourcesAndTargets, maxNbTargetsPerSourceConsidered4CosineCalc);

            SparseDoubleMatrix1D[] listVectors = matrixBuilder.createListOfSparseVectorsFromEdgeList();

            similarityMatrixColt = new SparseDoubleMatrix2D(listVectors.length, listVectors.length);

            Thread t = new Thread(new CosineCalculation(listVectors, similarityMatrixColt));
            t.start();
            t.join();

            IntArrayList rowList = new IntArrayList();
            IntArrayList columnList = new IntArrayList();
            DoubleArrayList valueList = new DoubleArrayList();
            similarityMatrixColt.getNonZeros(rowList, columnList, valueList);

            int nonZeroCell = 0;
            int nbOfNonZeroCells = rowList.size();
            Set<String> nodesString = new HashSet();
            Map<UnDirectedPair, Double> mapUnDirectedPairsToTheirWeight = new HashMap();
            for (nonZeroCell = 0; nonZeroCell < nbOfNonZeroCells; nonZeroCell++) {
                int rowIndex = rowList.get(nonZeroCell);
                int colIndex = columnList.get(nonZeroCell);
                double cellValue = valueList.get(nonZeroCell);

                String sourceLabel = matrixBuilder.getMapSourcesIndexToLabel().get(rowIndex);
                String targetLabel = matrixBuilder.getMapSourcesIndexToLabel().get(colIndex);

                nodesString.add(sourceLabel);
                nodesString.add(targetLabel);

                UnDirectedPair newPair = new UnDirectedPair(sourceLabel, targetLabel);
                mapUnDirectedPairsToTheirWeight.put(newPair, cellValue);
            }

            ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
            pc.newProject();
            workspace = pc.getCurrentWorkspace();

            //Get a graph model - it exists because we have a workspace
            gm = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);

            GraphFactory factory = gm.factory();
            graphResult = gm.getGraph();

            Set<Node> nodes = new HashSet();
            Node node;
            for (String nodeString : nodesString) {
                node = factory.newNode(nodeString);
                node.setLabel(nodeString);
                nodes.add(node);
            }
            graphResult.addAllNodes(nodes);

            Map<Edge, Double> mapEdgesToTheirWeight = new HashMap();

            Set<Edge> edgesForGraph = new HashSet();
            Edge edge;
            Iterator<Map.Entry<UnDirectedPair, Double>> iteratorEdgesToCreate = mapUnDirectedPairsToTheirWeight.entrySet().iterator();
            while (iteratorEdgesToCreate.hasNext()) {
                Map.Entry<UnDirectedPair, Double> entry = iteratorEdgesToCreate.next();
                Node nodeSource = graphResult.getNode(entry.getKey().getLeft());
                Node nodeTarget = graphResult.getNode(entry.getKey().getRight());
                edge = factory.newEdge(nodeSource, nodeTarget, 0, entry.getValue(), false);
                edgesForGraph.add(edge);
                mapEdgesToTheirWeight.put(edge, entry.getValue());
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
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }

        return "";

    }

}
