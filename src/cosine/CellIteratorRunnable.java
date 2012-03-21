/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cosine;

import no.uib.cipr.matrix.sparse.SparseVector;

/**
 *
 * @author C. Levallois
 */
public class CellIteratorRunnable implements Runnable {

    private final SparseVector source;
    private final SparseVector target;
    private final int i;
    private final int j;

    CellIteratorRunnable(SparseVector svSource, SparseVector svTarget, int i, int j) {

        this.source = svSource;
        this.target = svTarget;
        this.i = i;
        this.j = j;


    }

    @Override
    public void run() {

        long currentTime = System.currentTimeMillis();

        double result = source.dot(target) / (Cosine2loops.norms.get(i) * Cosine2loops.norms.get(j));
        //System.out.println("result in the runnable: " + result);
//    Triple similarityResult = new Triple(i,j,result);
        synchronized (Main.similarityMatrix) {
            Main.similarityMatrix.set(i, j, result);
        }
    long endTime = System.currentTimeMillis();
    Cosine2loops.cellTime = Cosine2loops.cellTime + endTime-currentTime; 
    }
}
