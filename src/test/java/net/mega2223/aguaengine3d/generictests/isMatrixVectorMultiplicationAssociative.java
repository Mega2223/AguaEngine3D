package net.mega2223.aguaengine3d.generictests;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;

import java.util.Random;

public class isMatrixVectorMultiplicationAssociative {
    public static void main(String[] args) {
        float[] vecV = {0, 0, 1, 0};
        float[] vecW = {0, 0, 1, 0};

        float[] matA = new float[16];
        float[] matB = new float[16];

        Random r = new Random(System.currentTimeMillis());

        MatrixTranslator.generateRotationMatrix(r.nextFloat(),r.nextFloat(),r.nextFloat(),matA);
        MatrixTranslator.generateRotationMatrix(r.nextFloat(),r.nextFloat(),r.nextFloat(),matB);

        float[] matC = new float[16];

        // C = A * B
        MatrixTranslator.multiply4x4Matrices(matA,matB,matC);

        // r1 = C * V
        // r2 = A * (B * V)

        float[] r1 = new float[4];
        float[] r2 = new float[4];

        MatrixTranslator.multiplyVec4Mat4(vecV,matC,r1);
        MatrixTranslator.multiplyVec4Mat4(vecV,matB,vecW);
        MatrixTranslator.multiplyVec4Mat4(vecW,matA,r2);

        boolean throwErr = false;
        for (int i = 0; i < 3; i++) {
            System.out.printf("%d [ %.4f ] [ %.4f ]\n",i,r1[i],r2[i]);
            if(r1[i] != r2[i]){
                throwErr = true;
            }
        }

        if(throwErr){
            throw new RuntimeException("Matrix Associativity Test failed :(");
        }
    }
}
