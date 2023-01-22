/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.clementlevallois.gaze.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author LEVALLOIS
 */
public class Test {
    
    static int minTargetsInCommon = 1;

    public static void main(String[] args) throws IOException {
        
        Set<String> targets1 = Set.of("A", "B", "C");
        Set<String> targets2 = Set.of("D", "E", "F");
        Set<String> targets3 = Set.of("A");
        Set<String> targets4 = Set.of("A", "B");
        
        Map<String, Set<String>> sourcesAndTargets = new HashMap();
        sourcesAndTargets.put("1", targets1);
        sourcesAndTargets.put("2", targets2);
        sourcesAndTargets.put("3", targets3);
        sourcesAndTargets.put("4", targets4);
        Controller controller = new Controller(sourcesAndTargets);
        controller.run(minTargetsInCommon);
        
        SimilarityFunction simFunction = new SimilarityFunction();
        String gexf = simFunction.createSimilarityGraph(sourcesAndTargets,minTargetsInCommon);
        System.out.println("");
        System.out.println("----");
        System.out.println("");
        System.out.println("gexf:");
        System.out.println("");
        System.out.println(gexf);
    }

}
