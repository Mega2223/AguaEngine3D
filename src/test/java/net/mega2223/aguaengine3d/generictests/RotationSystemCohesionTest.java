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
        QuaternionTranslator.axisAngleToQuaternion(rotationAxis,rotationQuaternion);

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

        float[] aQ4 = new float[4], bQ4 = new float[4], axisQ = new float[4], result = new float[4];
        QuaternionTranslator.axisAngleToQuaternion(vecA,aQ4);
        QuaternionTranslator.axisAngleToQuaternion(vecB,bQ4);
        QuaternionTranslator.axisAngleToQuaternion(rotationAxis,axisQ);

        QuaternionTranslator.rotateQuaternion(aQ4,axisQ,result);
        QuaternionTranslator.quaternionToAxisAngle(result,buffer);

        error = VectorTranslator.getAngleBetweenVectors(vecB,buffer);
        if(error > ANGULAR_ERROR_TOLERANCE){
            String message = "Quaternion rotation is not cohesive\n" +
                    String.format("error = %f\n",error);
            throw new RuntimeException(message);
        } else {
            System.out.println("Quaternion rotation test is cohesive :)");
        }

        QuaternionTranslator.quaternionToAxisAngle(rotationQuaternion,buffer);
        error = VectorTranslator.getAngleBetweenVectors(rotationAxis,buffer);
        if(error > ANGULAR_ERROR_TOLERANCE){
            String message = "Quaternion to axis and axis to quaternion are not inverse from one another\n" +
                    String.format("error = %f\n",error);
            throw new RuntimeException(message);
        } else {
            System.out.println("Axis to Quaternion test is cohesive :)");
        }

        System.out.println();
    }
}
