package net.mega2223.aguaengine3d.tests;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;

public class MatrixTransposeAlgorithmTest {
    public static void main(String[] args) {
        float[] m4 = {1,2,3,4,5,6,7,8,8,10,11,12,13,14,15,16};
        MatrixTranslator.debugMatrix4x4(m4);
        System.out.println(" vvv ");
        MatrixTranslator.getTransposeMatrix4(m4);
        MatrixTranslator.debugMatrix4x4(m4);
    }
}
