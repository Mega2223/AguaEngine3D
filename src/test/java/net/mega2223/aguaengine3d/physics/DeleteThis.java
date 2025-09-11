package net.mega2223.aguaengine3d.physics;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;

import java.util.Random;

public class DeleteThis {
    static Random r = new Random("among us".hashCode() + System.currentTimeMillis());
    public static void main(String[] args) {
        float[] axis = new float[4];
        for (int i = 0; i < 3; i++) {
            axis[i] = r.nextInt();
        }

//        VectorTranslator.normalize(axis);
//        VectorTranslator.scaleVector(axis, (float) (Math.PI*r.nextInt()));

        float[] mat3 = new float[9];
        float[] mat4 = new float[16];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                float r = DeleteThis.r.nextFloat() * 2 - 1;
                mat4[i*4+j%4]= r*10;
            }
        }
        mat4[15] = 1;
//        MatrixTranslator.rotationMatrixFromAxisAngle(axis,mat4);
        mat3[0] = mat4[0]; mat3[1] = mat4[1]; mat3[2] = mat4[2];
        mat3[3] = mat4[4]; mat3[4] = mat4[5]; mat3[5] = mat4[6];
        mat3[6] = mat4[8]; mat3[7] = mat4[9]; mat3[8] = mat4[10];

        float determinantMatrix3 = MatrixTranslator.getDeterminantMatrix3(mat3);
        System.out.println(determinantMatrix3);

        float determinantMatrix4 = MatrixTranslator.getDeterminantMatrix4(mat4);
        System.out.println(determinantMatrix4);

        if(Math.abs(determinantMatrix3-determinantMatrix4) > 1){
            throw new RuntimeException("what the");
        }

        float[] invMat4 = new float[16], buffer = new float[16];
        MatrixTranslator.getInverseMatrix4(mat4,invMat4);
        MatrixTranslator.multiply4x4Matrices(mat4,invMat4,buffer);
        MatrixTranslator.debugMatrix4x4(mat4);
        MatrixTranslator.debugMatrix4x4(invMat4);
        MatrixTranslator.debugMatrix4x4(buffer);
    }
}
