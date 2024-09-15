package net.mega2223.aguaengine3d.generictests;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;

public class Mat4Vec4MultiplicationTest {
    public static void main(String[] args) {
        float[] m4 = new float[16], vec4 = {1,2,3,1};
        VectorTranslator.debugVector(vec4);
        MatrixTranslator.generateTranslationMatrix(0, 1, 0, m4);
        MatrixTranslator.debugMatrix4x4(m4);
        MatrixTranslator.multiplyVec4Mat4(vec4,m4);
        VectorTranslator.debugVector(vec4);
    }
}
