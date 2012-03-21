/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cosine;

import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import java.util.ArrayList;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.JCublas;

/**
 *
 * @author C. Levallois
 */
public class parallelCUDA {

    private final SparseDoubleMatrix2D batchMatrix;
    private final double[] A;

    parallelCUDA(SparseDoubleMatrix2D batchMatrix, SparseDoubleMatrix1D sourceVector) {

        this.batchMatrix = batchMatrix;
        this.A = sourceVector.toArray();
        call();
    }

    public Double [] call()  {
//        int sizeVector = batchMatrix.rows();
//        
//        //pointer to the sourceVector
//        Pointer pointerToA = new Pointer();
//        
//        //creation of an array of pointers to targetVectors
//        ArrayList<Pointer> listPointers = new ArrayList();
//
//        for (int i = 1; i < batchMatrix.columns(); i++) {
//
//            listPointers.add(new Pointer());
//
//        }
//
//        //creation of memory space for the targetVectors
//        for (int i = 0; i < listPointers.size(); i++) {
//            JCublas.cublasAlloc(sizeVector, Sizeof.DOUBLE, listPointers.get(i));
//        }
//        
//        //creation of the memory space on the GPU for the sourceVector
//        JCublas.cublasAlloc(sizeVector, Sizeof.DOUBLE, pointerToA);
        
        
        //copy of the targetVectors on the GPU
        for (int i = 1; i < batchMatrix.columns(); i++) {

            double[] B = batchMatrix.viewColumn(i).toArray();
            JCublas.cublasSetVector(Cosine2loops.sizeVector, Sizeof.DOUBLE, Pointer.to(B), 1, Cosine2loops.listPointers.get(i+1), 1);

        }
        
        //copy of the sourceVector on the GPU
        JCublas.cublasSetVector(Cosine2loops.sizeVector, Sizeof.DOUBLE, Pointer.to(A), 1,Cosine2loops.pointerToA, 1);

        ArrayList<Double> batchResults = new ArrayList();
        for (int i = 1; i < batchMatrix.columns(); i++) {
        
        batchResults.add(JCublas.cublasDdot(
                Cosine2loops.sizeVector, Cosine2loops.listPointers.get(i-1), 1, Cosine2loops.pointerToA, 1));

        }
        Double [] results = (Double[]) batchResults.toArray();
        return results;
    }
        
        
}
