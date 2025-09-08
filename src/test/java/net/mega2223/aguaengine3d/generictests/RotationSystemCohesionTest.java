package net.mega2223.aguaengine3d.generictests;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.QuaternionTranslator;

import java.util.Locale;

public class RotationSystemCohesionTest {
    /**
     * Testa se os sistemas de rotação fazem operações equivalentes
     * */

    public static final int TESTS = 10;
    public static final float ANGULAR_ERROR_TOLERANCE = (float) Math.toRadians(.25); // 1/4 graus pra mim é bem bom já

    public static void main(String[] args) {
        // TODO essa classe deve medir se as rotações de matriz, axis angle e quaternion são equivalentes
        // TODO talvez calcular o erro máximo, mínimo e médio de cada medida
        int failures = 0;
        for (int i = 0; i < TESTS; i++) {
            performTest();
            try{

            } catch (RuntimeException e){
                System.out.println("Failed test " + (i+1));
                failures++;
                e.printStackTrace();
            }
        }
        if(failures > 0){
            String message = String.format("Failed (%d/%d) of tests", failures, TESTS);
            System.out.println(message);
            throw new RuntimeException(message);
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

        float[] resultBufferV3 = new float[4];

        VectorTranslator.normalize(vecA);
        VectorTranslator.normalize(vecB);

        VectorTranslator.getRotationAxis(vecA,vecB,rotationAxis);
        MatrixTranslator.rotationMatrixFromAxisAngle(rotationAxis,rotationMatrix);
        QuaternionTranslator.axisAngleToQuaternion(rotationAxis,rotationQuaternion);

        VectorTranslator.rotateAlongAxis(vecA,rotationAxis,resultBufferV3);
        float error = VectorTranslator.getAngleBetweenVectors(resultBufferV3, vecB);
        if(Float.isNaN(error) || error > ANGULAR_ERROR_TOLERANCE){
            String message = "Vectors B and (A [[x]] RotationAxis) are not the same\n" +
                    String.format("A = v(%.4f,%.4f,%.4f)\n", vecA[0], vecA[1], vecA[2]) +
                    String.format("B = v(%.4f,%.4f,%.4f)\n", vecB[0], vecB[1], vecB[2]) +
                    String.format("axis = v(%.4f,%.4f,%.4f)\n", rotationAxis[0], rotationAxis[1], rotationAxis[2]) +
                    String.format("rotated = v(%.4f,%.4f,%.4f)\n", resultBufferV3[0], resultBufferV3[1], resultBufferV3[2]) +
                    String.format("error = %f\n",error);
            throw new RuntimeException(message);
        } else {
            System.out.println("Rotation axis test is cohesive :)");
        }

        MatrixTranslator.multiplyVec4Mat4(vecA,rotationMatrix,resultBufferV3);
        error = VectorTranslator.getAngleBetweenVectors(resultBufferV3, vecB);
        if(Float.isNaN(error) || error > ANGULAR_ERROR_TOLERANCE){
            String message = "Vectors B and (A x RotationMatrix) are not the same\n" +
                    String.format("A = v(%.4f,%.4f,%.4f)\n", vecA[0], vecA[1], vecA[2]) +
                    String.format("B = v(%.4f,%.4f,%.4f)\n", vecB[0], vecB[1], vecB[2]) +
                    String.format("M = \n[ %.4f, %.4f, %.4f, %.4f ]\n[ %.4f, %.4f, %.4f, %.4f ]\n[ %.4f, %.4f, %.4f, %.4f ]\n[ %.4f, %.4f, %.4f, %.4f ]\n",
                            rotationMatrix[0],rotationMatrix[1],rotationMatrix[2],rotationMatrix[3],
                            rotationMatrix[4],rotationMatrix[5],rotationMatrix[6],rotationMatrix[7],
                            rotationMatrix[8],rotationMatrix[9],rotationMatrix[10],rotationMatrix[11],
                            rotationMatrix[12],rotationMatrix[13],rotationMatrix[14],rotationMatrix[15]) +
                    String.format("rotated = v(%.4f,%.4f,%.4f)\n", resultBufferV3[0], resultBufferV3[1], resultBufferV3[2]) +
                    String.format("error = %f\n",error);
            throw new RuntimeException(message);
        } else {
            System.out.println("Matrix rotation test is cohesive :)");
        }

        QuaternionTranslator.rotateVectorByQuaternion(vecA,rotationQuaternion,resultBufferV3);
        error = VectorTranslator.getAngleBetweenVectors(resultBufferV3, vecB);
        if(Float.isNaN(error) || error > ANGULAR_ERROR_TOLERANCE){
            String message = "Vector times Quaternion rotation is not cohesive\n" +
                    String.format("A = v(%.4f,%.4f,%.4f)\n", vecA[0], vecA[1], vecA[2]) +
                    String.format("B = v(%.4f,%.4f,%.4f)\n", vecB[0], vecB[1], vecB[2]) +
                    String.format("rotQ = v(%.4f,%.4f,%.4f,%.4f)\n", rotationQuaternion[0], rotationQuaternion[1], rotationQuaternion[2],rotationQuaternion[3]) +
                    String.format("rotated = v(%.4f,%.4f,%.4f)\n", resultBufferV3[0], resultBufferV3[1], resultBufferV3[2]) +
                    String.format("error = %f\n",error);
            throw new RuntimeException(message);
        } else {
            System.out.println("Vector times Quaternion rotation is cohesive :)");
        }

        float[] quatA = new float[4], quatB = new float[4], bufferQ = new float[4];
        QuaternionTranslator.axisAngleToQuaternion(vecA,quatA);
        QuaternionTranslator.axisAngleToQuaternion(vecB,quatB);

        QuaternionTranslator.rotateQuaternionByAxis(quatA,rotationAxis,bufferQ);
        QuaternionTranslator.quaternionToAxisAngle(bufferQ,resultBufferV3);

        error = VectorTranslator.getAngleBetweenVectors(resultBufferV3, vecB);

        if(Float.isNaN(error) || error > ANGULAR_ERROR_TOLERANCE){
            float[] gottenAxisBuffer = new float[4];
            VectorTranslator.getRotationAxis(vecA,resultBufferV3,gottenAxisBuffer);
            String message = "Quaternion by axis rotation is not cohesive\n" +
                    String.format("A = v(%.4f,%.4f,%.4f)\n", vecA[0], vecA[1], vecA[2]) +
                    String.format("B = v(%.4f,%.4f,%.4f)\n", vecB[0], vecB[1], vecB[2]) +
                    String.format("axis (expected) = v(%.4f,%.4f,%.4f)\n", rotationAxis[0], rotationAxis[1], rotationAxis[2]) +
                    String.format("axis (gotten) = v(%.4f,%.4f,%.4f)\n", gottenAxisBuffer[0], gottenAxisBuffer[1], gottenAxisBuffer[2]) +
                    String.format("rotQ = v(%.4f,%.4f,%.4f,%.4f)\n", rotationQuaternion[0], rotationQuaternion[1], rotationQuaternion[2],rotationQuaternion[3]) +
                    String.format("qB (gotten) = v(%.4f,%.4f,%.4f,%.4f)\n", bufferQ[0], bufferQ[1], bufferQ[2],bufferQ[3]) +
                    String.format("qB (expected) = v(%.4f,%.4f,%.4f,%.4f)\n", quatB[0], quatB[1], quatB[2],quatB[3]) +
                    String.format("rotated = v(%.4f,%.4f,%.4f)\n", resultBufferV3[0], resultBufferV3[1], resultBufferV3[2]) +
                    String.format("error = %f\n",error);
            throw new RuntimeException(message);
        } else {
            System.out.println("Quaternion by axis rotation is cohesive :)");
        }

//        QuaternionTranslator.axisAngleToQuaternion(vecA,quatA);
//        QuaternionTranslator.rotateQuaternion(quatA,rotationQuaternion,bufferQ);
//        QuaternionTranslator.quaternionToAxisAngle(bufferQ,resultBufferV3);
//
//        error = VectorTranslator.getAngleBetweenVectors(resultBufferV3, vecB);
//
//        if(Float.isNaN(error) || error > ANGULAR_ERROR_TOLERANCE){
//            String message = "Quaternion times Quaternion rotation is not cohesive\n" +
//                    String.format("A = v(%.4f,%.4f,%.4f)\n", vecA[0], vecA[1], vecA[2]) +
//                    String.format("B = v(%.4f,%.4f,%.4f)\n", vecB[0], vecB[1], vecB[2]) +
//                    String.format("axis = v(%.4f,%.4f,%.4f)\n", rotationAxis[0], rotationAxis[1], rotationAxis[2]) +
//                    String.format("rotQ = v(%.4f,%.4f,%.4f,%.4f)\n", rotationQuaternion[0], rotationQuaternion[1], rotationQuaternion[2],rotationQuaternion[3]) +
//                    String.format("rotated = v(%.4f,%.4f,%.4f)\n", resultBufferV3[0], resultBufferV3[1], resultBufferV3[2]) +
//                    String.format("error = %f\n",error);
//            throw new RuntimeException(message);
//        } else {
//            System.out.println("Quaternion by quaternion rotation is cohesive :)");
//        }

        System.out.println();
    }
}
