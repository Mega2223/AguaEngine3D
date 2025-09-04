package net.mega2223.aguaengine3d.generictests;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;

import java.util.Locale;

public class RotationSystemCohesionTest {
    /**
     * Testa se os sistemas de rotação fazem operações equivalentes
     * */

    public static final int TESTS = 10;
    public static final float ANGULAR_ERROR_TOLERANCE = (float) Math.toRadians(1);

    public static void main(String[] args) {
        // TODO essa classe deve medir se as rotações de matriz, axis angle e quaternion são equivalentes
        for (int i = 0; i < TESTS; i++) {
            performTest();
        }
    }

    public static void performTest(){

        float[] vecA = new float[4];
        float[] vecB = new float[4];

        for(int i = 0; i < 4; i++){
            vecA[i] = (float) Math.random();
            vecB[i] = (float) Math.random();
        }

        System.out.printf(Locale.US,"Starting angular transform cohesion test -> " +
                        "a = v(%.4f,%.4f,%.4f) b = v(%.4f,%.4f,%.4f)\n",
                vecA[0],vecA[1],vecA[2],vecB[0],vecB[1],vecB[2]);

        float[] rotationAxis = new float[4];
        float[] rotationMatrix = new float[16];
        float[] rotationQuaternion = new float[4];

        float[] buffer = new float[4];

        VectorTranslator.normalize(vecA);
        VectorTranslator.normalize(vecB);

        VectorTranslator.getRotationAxis(vecA,vecB,rotationAxis);
        MatrixTranslator.rotationMatrixFromAxisAngle(rotationAxis,rotationMatrix);
        // TODO quaternion

        VectorTranslator.rotateAlongAxis(vecA,rotationAxis,buffer);
        float error = VectorTranslator.getAngleBetweenVectors(buffer, vecB);
        if(error > ANGULAR_ERROR_TOLERANCE){
            String message = "Vectors B and (A [[x]] RotationAxis) are not the same\n" +
                    String.format("A = v(%.4f,%.4f,%.4f)\n", vecA[0], vecA[1], vecA[2]) +
                    String.format("B = v(%.4f,%.4f,%.4f)\n", vecB[0], vecB[1], vecB[2]) +
                    String.format("axis = v(%.4f,%.4f,%.4f)\n", rotationAxis[0], rotationAxis[1], rotationAxis[2]) +
                    String.format("rotated = v(%.4f,%.4f,%.4f)\n", buffer[0], buffer[1], buffer[2]) +
                    String.format("error = %f\n",error);
            throw new RuntimeException(message);
        } else {
            System.out.println("Rotation axis test is cohesive :)");
        }

        MatrixTranslator.multiplyVec4Mat4(vecA,rotationMatrix,buffer);
        error = VectorTranslator.getAngleBetweenVectors(buffer, vecB);
        if(error > ANGULAR_ERROR_TOLERANCE){
            String message = "Vectors B and (A x RotationMatrix) are not the same\n" +
                    String.format("A = v(%.4f,%.4f,%.4f)\n", vecA[0], vecA[1], vecA[2]) +
                    String.format("B = v(%.4f,%.4f,%.4f)\n", vecB[0], vecB[1], vecB[2]) +
                    //TODO função de pegar a matrix como string :3 String.format("matrix = v(%.4f,%.4f,%.4f)\n", rotationAxis[0], rotationAxis[1], rotationAxis[2]) +
                    String.format("rotated = v(%.4f,%.4f,%.4f)\n", buffer[0], buffer[1], buffer[2]) +
                    String.format("error = %f\n",error);
            throw new RuntimeException(message);
        } else {
            System.out.println("Matrix rotation test is cohesive :)");
        }
    }
}
