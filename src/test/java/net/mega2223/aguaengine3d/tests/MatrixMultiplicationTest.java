package net.mega2223.aguaengine3d.tests;

import net.mega2223.lwjgltest.aguaengine3d.mathematics.MatrixTranslator;

public class MatrixMultiplicationTest {

    public static void main(String[] args) {
        float[][] m1 = {{1,2999,1},{0,1,0},{2,3,4}};
        float[][] m2 = {{2,5},{6,7},{1,8}};
        MatrixTranslator.debugMatrix(m1);
        MatrixTranslator.debugMatrix(m2);
        MatrixTranslator.debugMatrix(MatrixTranslator.multiplyMatrix(m1,m2));

    }

}
