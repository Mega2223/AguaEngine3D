package net.mega2223.aguaengine3d.generictests;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;

import java.util.Locale;
import java.util.Random;

public class IsMatrixMatrixMultiplicationAssociative {

    public static final float ERROR_MARGIN = 0.01F;

    public static void main(String[] args) {
        float[] A = new float[16];
        float[] B = new float[16];
        float[] C = new float[16];

        float[] buffer = new float[16];

        Random r = new Random(System.currentTimeMillis()/2);

        for (int i = 0; i < 16; i++) {
            A[i] = r.nextFloat() * 3;
            B[i] = r.nextFloat() * 3;
            C[i] = r.nextFloat() * 3;
        }

        // r1 = (A * B) * C
        // r2 = A * (B * C)

        float[] r1 = new float[16];
        float[] r2 = new float[16];

        MatrixTranslator.multiply4x4Matrices(A,B,buffer);
        MatrixTranslator.multiply4x4Matrices(buffer,C,r1);

        MatrixTranslator.multiply4x4Matrices(B,C,buffer);
        MatrixTranslator.multiply4x4Matrices(A,buffer,r2);

        boolean throwErr = false;
        for (int i = 0; i < 16; i++) {
            boolean acceptable = Math.abs(r1[i]-r2[i]) < ERROR_MARGIN;
            System.out.printf(Locale.US,"%2d [ %.5f ] [ %.5f ] %s\n",i,r1[i],r2[i], acceptable ? "OK" : "FAIL");
            if(!acceptable){
                throwErr = true;
            }
        }

        if(throwErr){
            throw new RuntimeException("Matrix Associativity Test failed :(");
        }
    }
}
