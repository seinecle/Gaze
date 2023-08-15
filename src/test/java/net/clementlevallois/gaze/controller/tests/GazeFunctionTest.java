/*
 * Copyright Clement Levallois 2021-2023. License Attribution 4.0 Intertnational (CC BY 4.0)
 */
package net.clementlevallois.gaze.controller.tests;

import java.io.File;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.clementlevallois.gaze.controller.SimilarityFunction;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ContainerUnloader;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.importer.plugin.file.ImporterGEXF;
import org.gephi.io.importer.spi.FileImporter;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.junit.Assert;
import org.junit.Test;
import org.openide.util.Lookup;

/**
 *
 * @author LEVALLOIS
 */
public class GazeFunctionTest {
    
    @Test
    public void similarityFunction(){
        SimilarityFunction sim = new SimilarityFunction();
        Set<String> targets1 = Set.of("1", "2", "3");
        Set<String> targets2 = Set.of("1", "2", "3");
        Set<String> targets3 = Set.of("4", "5", "6");
        
        Map<String,Set<String>> sourcesAndTargets = new HashMap();
        sourcesAndTargets.put("A", targets1);
        sourcesAndTargets.put("B", targets2);
        sourcesAndTargets.put("C", targets3);
        
        String gexf = sim.createSimilarityGraph(sourcesAndTargets, 1);

        ProjectController projectController = Lookup.getDefault().lookup(ProjectController.class);
        GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        projectController.newProject();
        Container container = null;
            FileImporter fi = new ImporterGEXF();
            container = importController.importFile(new StringReader(gexf), fi);
            container.closeLoader();
        DefaultProcessor processor = new DefaultProcessor();
        processor.setWorkspace(projectController.getCurrentWorkspace());
        processor.setContainers(new ContainerUnloader[]{container.getUnloader()});
        processor.process();
        GraphModel gm = graphController.getGraphModel();
        
        Node nodeA = gm.getGraph().getNode("A");
        Node nodeB = gm.getGraph().getNode("B");
        Node nodeC = gm.getGraph().getNode("C");
        
        Edge edgeAB = gm.getGraph().getEdge(nodeA, nodeB);
        Edge edgeAC = gm.getGraph().getEdge(nodeA, nodeC);
        
        Assert.assertEquals(1d, edgeAB.getWeight(),0d);
        Assert.assertNotNull(nodeC);
        Assert.assertNull(edgeAC);

    }
}
