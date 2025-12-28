package net.clementlevallois.gaze.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.clementlevallois.utils.FindAllPairs;
import net.clementlevallois.utils.Multiset;
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
public class CoocFunction {

    public String createGraphFromCooccurrences(Map<Integer, Multiset<String>> inputLines, boolean applyPMI, int minInclusiveCooccurrences) {
        try {
            Iterator<Map.Entry<Integer, Multiset<String>>> iterator = inputLines.entrySet().iterator();
            FindAllPairs pairFinder;
            Multiset<UnDirectedPair<String>> allUndirectedPairs = new Multiset();
            Multiset<String> nodesAsString = new Multiset();

            while (iterator.hasNext()) {
                Map.Entry<Integer, Multiset<String>> entry = iterator.next();
                pairFinder = new FindAllPairs();
                Multiset<String> itemsOnOneLine = entry.getValue();
                for (String item : itemsOnOneLine.toListOfAllOccurrences()) {
                    nodesAsString.addOne(item);
                }
                Set<UnDirectedPair<String>> undirectedPairsAsListOneLine = pairFinder.getAllUndirectedPairsFromList(itemsOnOneLine.toListOfAllOccurrences());
                allUndirectedPairs.addAllFromListOrSet(undirectedPairsAsListOneLine);
            }

            if (minInclusiveCooccurrences > 1) {
                Multiset<UnDirectedPair<String>> temp = new Multiset();
                for (UnDirectedPair<String> edgeLoop : allUndirectedPairs.getElementSet()) {
                    Integer count = allUndirectedPairs.getCount(edgeLoop);
                    if (count >= minInclusiveCooccurrences) {
                        temp.addSeveral(edgeLoop, count);
                    }
                }
                allUndirectedPairs = temp;
            }

            ProjectController pc = null;
            Workspace workspace = null;
            try {
                pc = Lookup.getDefault().lookup(ProjectController.class);
                workspace = pc.newWorkspace(pc.newProject());
                pc.openWorkspace(workspace);  // Open this specific workspace            

                // Get graph model for THIS workspace specifically
                GraphModel gm = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);

                GraphFactory factory = gm.factory();
                Graph graphResult = gm.getGraph();

                gm.getNodeTable().addColumn("countTerms", Integer.TYPE);
                if (applyPMI) {
                    gm.getEdgeTable().addColumn("countEdge", Integer.TYPE);
                }

                Set<Node> nodes = new HashSet();
                Node node;
                for (String nodeString : nodesAsString.toListOfAllOccurrences()) {
                    node = factory.newNode(nodeString);
                    node.setLabel(nodeString);
                    node.setAttribute("countTerms", nodesAsString.getCount(nodeString));
                    nodes.add(node);
                }
                graphResult.addAllNodes(nodes);

                Set<Edge> edgesForGraph = new HashSet();
                Edge edge;
                for (UnDirectedPair<String> edgeToCreate : allUndirectedPairs.getElementSet()) {
                    Node nodeSource = graphResult.getNode(edgeToCreate.getLeft());
                    Node nodeTarget = graphResult.getNode(edgeToCreate.getRight());
                    if (applyPMI) {
                        int sourceCount = (Integer) nodeSource.getAttribute("countTerms");
                        int targetCount = (Integer) nodeTarget.getAttribute("countTerms");
                        float edgePMIWeight = (float) allUndirectedPairs.getCount(edgeToCreate) / (sourceCount * targetCount);
                        edge = factory.newEdge(nodeSource, nodeTarget, 0, edgePMIWeight, false);
                        edge.setAttribute("countEdge", allUndirectedPairs.getCount(edgeToCreate));
                    } else {
                        edge = factory.newEdge(nodeSource, nodeTarget, 0, allUndirectedPairs.getCount(edgeToCreate), false);
                    }
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
            } finally {
                if (pc != null) {
                    pc.closeCurrentWorkspace();
                    pc.closeCurrentProject();
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return "";
        }

    }

}
