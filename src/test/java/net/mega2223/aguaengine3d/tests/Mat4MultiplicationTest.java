package net.mega2223.aguaengine3d.tests;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;

public class Mat4MultiplicationTest {
    public static void main(String[] args) {
        float[] m1 = {
                0,1,2,3,
                4,5,6,7,
                8,9,10,11,
                12,13,14,15
        };
        float[] m2 = {
                0,1,2,3,
                4,5,6,7,
                8,9,10,11,
                12,13,14,15
        };
        MatrixTranslator.multiply4x4Matrices(m1,m2);
        MatrixTranslator.debugMatrix4x4(m1);

    }
}
