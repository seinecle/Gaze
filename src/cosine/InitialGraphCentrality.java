/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cosine;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.TreeMultimap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 *
 * @author C. Levallois
 */
public class InitialGraphCentrality {

    static DirectedGraph<Short, DefaultEdge> g =
            new DefaultDirectedGraph<Short, DefaultEdge>(DefaultEdge.class);
    HashSet<Short> vertices = new HashSet();

    InitialGraphCentrality(TreeMultimap<Short, Short> mapUndirected) {



        Iterator<Entry<Short, Short>> ITmap = mapUndirected.entries().iterator();

        while (ITmap.hasNext()) {
            Entry<Short, Short> currEntry = ITmap.next();

            g.addVertex(currEntry.getKey());
            g.addVertex(currEntry.getValue());

            g.addEdge(currEntry.getKey(), currEntry.getValue());




        }

    }

    DirectedGraph<Short, DefaultEdge> getGraph(){
        
        return g;
    }
    
    
}
