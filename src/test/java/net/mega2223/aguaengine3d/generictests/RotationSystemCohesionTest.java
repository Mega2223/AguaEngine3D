package net.mega2223.aguaengine3d.generictests;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.QuaternionTranslator;

import java.util.Locale;
import java.util.Random;

public class RotationSystemCohesionTest {
    /**
     * Testa se os sistemas de rotação fazem operações equivalentes
     * */

    public static final int TESTS = 10;
    public static final float ANGULAR_ERROR_TOLERANCE = (float) Math.toRadians(.25); // 1/4 graus pra mim é bem bom já

    public static final Random r = new Random(2223 + System.currentTimeMillis());

    public static void main(String[] args) {
        // TODO essa classe deve medir se as rotações de matriz, axis angle e quaternion são equivalentes
        // TODO talvez calcular o erro máximo, mínimo e médio de cada medida
        int failures = 0;
        for (int i = 0; i < TESTS; i++) {
            testRotations();
            testRotationSequence();
        }
        if(failures > 0){
            String message = String.format("Failed (%d/%d) of tests", failures, TESTS);
            System.out.println(message);
            throw new RuntimeException(message);
        }
    }

    public static void testRotations(){

        float[] vecA = new float[4];
        float[] vecB = new float[4];

        for(int i = 0; i < 3; i++){
            vecA[i] = r.nextFloat();
            vecB[i] = r.nextFloat();
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

    public static void testRotationSequence(){

        System.out.println("\nStarting sequential rotation test sequence");

        float[] vecA = new float[4], vecB = new float[4];
        float[][] buffers = new float[6][4];

        for(int i = 0; i < 3; i++){
            vecA[i] = (float) Math.random();
            vecB[i] = vecA[i];
        }
        VectorTranslator.normalize(vecA);
        VectorTranslator.normalize(vecB);

        final int N = 7;
        float[][] rotations = new float[N][4];
        for (int i = 0; i < N; i++) {
            for (int v = 0; v < 3; v++) {
                rotations[i][v] = (float) Math.random() * 2 - 1;
            }
            VectorTranslator.normalize(rotations[i]);
            VectorTranslator.scaleVector(rotations[i], (float) (Math.random()*10F));
        }

        for (int i = 0; i < N; i++) {
            VectorTranslator.rotateAlongAxis(vecB,rotations[i],buffers[0]);
//            System.out.printf("(%.3f %.3f %.3f) =>[%.3f %.3f %.3f]=> (%.3f %.3f %.3f) [mag = %.4f]\n",
//                    vecB[0],vecB[1],vecB[2],
//                    rotations[i][0],rotations[i][1],rotations[i][2],
//                    buffers[0][0],buffers[0][1], buffers[0][2],
//                    VectorTranslator.magnitude(buffers[0]));
            VectorTranslator.copy(buffers[0],vecB);
        }

        System.out.printf("\nA(%.3f %.3f %.3f) -> (N=%d) -> B(%.3f %.3f %.3f)\n",
                vecA[0],vecA[1],vecA[2],N,vecB[0],vecB[1],vecB[2]
        );

        // TESTS

        VectorTranslator.copy(vecA,buffers[0]);

        float[][] rotationMatrices = new float[N][16];
        for (int i = 0; i < N; i++) {
            MatrixTranslator.rotationMatrixFromAxisAngle(rotations[i],rotationMatrices[i]);
            MatrixTranslator.multiplyVec4Mat4(buffers[0],rotationMatrices[i]);
        }

        float error = VectorTranslator.getAngleBetweenVectors(buffers[0],vecB);
        if(error >ANGULAR_ERROR_TOLERANCE){
            throw new RuntimeException("Sequential matrix rotation is incoherent");
        }

        VectorTranslator.copy(vecA,buffers[0]);
        float[][] rotationQuaternions = new float[N][4];
        for (int i = 0; i < N; i++) {
            QuaternionTranslator.axisAngleToQuaternion(rotations[i],rotationQuaternions[i]);
            QuaternionTranslator.rotateVectorByQuaternion(buffers[0],rotationQuaternions[i],buffers[1]);
            QuaternionTranslator.copy(buffers[1],buffers[0]);
        }

        error = VectorTranslator.getAngleBetweenVectors(buffers[0],vecB);
        if(error >ANGULAR_ERROR_TOLERANCE){
            throw new RuntimeException("Sequential quaternion rotation is incoherent");
        }

        VectorTranslator.copy(vecA,buffers[0]);
        QuaternionTranslator.copy(rotationQuaternions[0], buffers[1]);
        for (int i = 1; i < N; i++) {
            QuaternionTranslator.quaternionProduct(rotationQuaternions[i], buffers[1], buffers[2]);
            QuaternionTranslator.copy(buffers[2],buffers[1]);
        }
        QuaternionTranslator.rotateVectorByQuaternion(vecA,buffers[1],buffers[0]);

        error = VectorTranslator.getAngleBetweenVectors(buffers[0],vecB);
        if(error >ANGULAR_ERROR_TOLERANCE){
            throw new RuntimeException("Sequential quaternion multiplication is incoherent");
        }
    }
}
