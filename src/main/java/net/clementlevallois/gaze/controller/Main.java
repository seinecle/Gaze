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
public class Main {

    public static void main(String[] args) throws IOException {
        Set<String> targets1 = Set.of("A", "B", "C");
        Set<String> targets2 = Set.of("D", "E", "F");
        Set<String> targets3 = Set.of("A");
        
        Map<String, Set<String>> sourcesAndTargets = new HashMap();
        sourcesAndTargets.put("1", targets1);
        sourcesAndTargets.put("2", targets2);
        sourcesAndTargets.put("3", targets3);
        Controller controller = new Controller(sourcesAndTargets);
        controller.run();

    }

}
