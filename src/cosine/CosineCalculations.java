/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cosine;

import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author C. Levallois
 */
public class CosineCalculations {
    private Matrix sourceDoc;
    private Matrix targetDoc;
    private final int i;
    private final int j;
    
    
    CosineCalculations(Matrix sourceDoc, Matrix targetDoc,int i,int j) {
        this.sourceDoc = sourceDoc;
        this.targetDoc = targetDoc;
        this.i = i;
        this.j = j;
    
    }



}