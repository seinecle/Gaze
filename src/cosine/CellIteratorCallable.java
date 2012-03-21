/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cosine;

import java.util.concurrent.Callable;
import levallois.clement.utils.Triple;
import no.uib.cipr.matrix.sparse.SparseVector;

/**
 *
 * @author C. Levallois
 */
public class CellIteratorCallable implements Callable<Triple<Integer,Integer,Double>> {
    private final SparseVector source;
    private final SparseVector target;
    private final int i;
    private final int j;


    CellIteratorCallable(SparseVector svSource,SparseVector svTarget, int i,int j){
        
        this.source = svSource;
        this.target = svTarget;
        this.i = i;
        this.j = j;
        
    
    }
    
    @Override
    public Triple<Integer,Integer,Double> call() throws Exception  {
        
        
    double result = source.dot(target) / (Cosine2loops.norms.get(i) *Cosine2loops.norms.get(j));
    //System.out.println("result in the callable: " + result);
    Triple similarityResult = new Triple(i,j,result);
    return similarityResult;
    }


}
