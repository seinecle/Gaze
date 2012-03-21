package cosine;

/*
 * JCublas - Java bindings for CUBLAS, the NVIDIA CUDA BLAS library,
 * to be used with JCuda <br />
 * http://www.jcuda.org
 *
 * Copyright 2009 Marco Hutter - http://www.jcuda.org
 * class sample modified by Clement Levallois
 */

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.JCublas;


public class JCublaDotProduct

{

static double dotProductCUDA;
static double dotProductJava;
static int n;         
static double result;


    public static double dotProduct(double A[],double B[])
    {

        double h_A[] = A;
        double h_B[] = B;
        n = A.length;
       
        
//        Clock javaClock = new Clock("Performing Sgemm with Java");
        SDotJava(h_A, h_B);
//        javaClock.closeAndPrintClock();
//        Clock JCublasClock = new Clock("Performing SDot with JCublas");
        result = SdotCuda(h_A, h_B);
//        JCublasClock.closeAndPrintClock();
//        System.out.println("CUDA: "+dotProductCUDA);
//        System.out.println("JAVA: "+dotProductJava);
        return result;
//        System.out.println("dot Product with Java is "+ dotProductJava);
//        System.out.println("dot Product with CUDA is "+ dotProductCUDA);
    }



     private static double SdotCuda(double A[], double B[])

             
    {


        // Initialize JCublas
        JCublas.cublasInit();
        JCublas.setExceptionsEnabled(true);
        

        // Allocate memory on the device
        Pointer d_A = new Pointer();
        Pointer d_B = new Pointer();
        JCublas.cublasAlloc(n, Sizeof.DOUBLE, d_A);
        JCublas.cublasAlloc(n, Sizeof.DOUBLE, d_B);

        // Copy the memory from the host to the device
        JCublas.cublasSetVector(n, Sizeof.DOUBLE, Pointer.to(A), 1, d_A, 1);
        JCublas.cublasSetVector(n, Sizeof.DOUBLE, Pointer.to(B), 1, d_B, 1);


        // Execute SDot
        dotProductCUDA = JCublas.cublasDdot(
            n, d_A,1, d_B,1);

        

        // Clean up
        JCublas.cublasFree(d_A);
        JCublas.cublasFree(d_B);

        JCublas.cublasShutdown();
        return dotProductCUDA;
    }




     // this function is useful when the vectors have a length of 1000 or less
     private static void SDotJava(double A[], double B[]){
         

//    double[] A2 = new double[A.length];
//    double[] B2 = new double[B.length];
//    for (int i = 0; i < A.length; i++)
//    {
//        A2[i] = A[i];
//        B2[i] = B[i];
//    }

         DoubleMatrix1D sourceDoc = new SparseDoubleMatrix1D(A);
         //sourceDoc.assign(A2);
         DoubleMatrix1D targetDoc = new SparseDoubleMatrix1D(B);
         //targetDoc.assign(B2);
         
         dotProductJava = sourceDoc.zDotProduct(targetDoc);
         
         
     }

}