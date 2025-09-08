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

        float[] buffer = new float[4];

        VectorTranslator.normalize(vecA);
        VectorTranslator.normalize(vecB);

        VectorTranslator.getRotationAxis(vecA,vecB,rotationAxis);
        MatrixTranslator.rotationMatrixFromAxisAngle(rotationAxis,rotationMatrix);
        QuaternionTranslator.axisAngleToQuaternion(rotationAxis,rotationQuaternion);

        VectorTranslator.rotateAlongAxis(vecA,rotationAxis,buffer);
        float error = VectorTranslator.getAngleBetweenVectors(buffer, vecB);
        if(Float.isNaN(error) || error > ANGULAR_ERROR_TOLERANCE){
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
        if(Float.isNaN(error) || error > ANGULAR_ERROR_TOLERANCE){
            String message = "Vectors B and (A x RotationMatrix) are not the same\n" +
                    String.format("A = v(%.4f,%.4f,%.4f)\n", vecA[0], vecA[1], vecA[2]) +
                    String.format("B = v(%.4f,%.4f,%.4f)\n", vecB[0], vecB[1], vecB[2]) +
                    String.format("M = \n[ %.4f, %.4f, %.4f, %.4f ]\n[ %.4f, %.4f, %.4f, %.4f ]\n[ %.4f, %.4f, %.4f, %.4f ]\n[ %.4f, %.4f, %.4f, %.4f ]\n",
                            rotationMatrix[0],rotationMatrix[1],rotationMatrix[2],rotationMatrix[3],
                            rotationMatrix[4],rotationMatrix[5],rotationMatrix[6],rotationMatrix[7],
                            rotationMatrix[8],rotationMatrix[9],rotationMatrix[10],rotationMatrix[11],
                            rotationMatrix[12],rotationMatrix[13],rotationMatrix[14],rotationMatrix[15]) +
                    String.format("rotated = v(%.4f,%.4f,%.4f)\n", buffer[0], buffer[1], buffer[2]) +
                    String.format("error = %f\n",error);
            throw new RuntimeException(message);
        } else {
            System.out.println("Matrix rotation test is cohesive :)");
        }

//        float[] pvrEuTeImploro = new float[4];
//        QuaternionTranslator.multiplyQuaternions(
//                new float[]{.1F,.2F,.3F,.4F},
//                new float[]{.5F,.6F,.7F,.8F},
//                pvrEuTeImploro
//        );
//        VectorTranslator.debugVector(pvrEuTeImploro);
//        System.exit(0); ok isso faz sentido

        QuaternionTranslator.rotateVectorByQuaternion(vecA,rotationQuaternion,buffer);
        error = VectorTranslator.getAngleBetweenVectors(buffer, vecB);
        if(Float.isNaN(error) || error > ANGULAR_ERROR_TOLERANCE){
            String message = "Vector times Quaternion rotation is not cohesive\n" +
                    String.format("A = v(%.4f,%.4f,%.4f)\n", vecA[0], vecA[1], vecA[2]) +
                    String.format("B = v(%.4f,%.4f,%.4f)\n", vecB[0], vecB[1], vecB[2]) +
                    String.format("rotQ = v(%.4f,%.4f,%.4f,%.4f)\n", rotationQuaternion[0], rotationQuaternion[1], rotationQuaternion[2],rotationQuaternion[3]) +
                    String.format("rotated = v(%.4f,%.4f,%.4f)\n", buffer[0], buffer[1], buffer[2]) +
                    String.format("error = %f\n",error);
            throw new RuntimeException(message);
        } else {
            System.out.println("Vector times Quaternion rotation is cohesive :)");
        }

        //asdksalçkd
//        float[] tatatata = new float[4];
//        float[] coma2 = {0, -1, 0, 0};
//        float[] cima = coma2.clone();
//        QuaternionTranslator.axisAngleToQuaternion(cima, tatatata);
//
//        VectorTranslator.debugVector(tatatata);
//        System.exit(0);
//        //saçl~çsald~ça
//
//        QuaternionTranslator.quaternionToAxisAngle(rotationQuaternion,buffer);
//        error = VectorTranslator.getAngleBetweenVectors(rotationAxis,buffer);
//
//        if(error > ANGULAR_ERROR_TOLERANCE){
//            String message = "Quaternion to axis and axis to quaternion are not inverse from one another\n" +
//                    String.format("error = %f\n",error);
//            throw new RuntimeException(message);
//        } else {
//            System.out.println("Axis to Quaternion test is cohesive :)");
//        }
//
//        float[] aQ4 = new float[4], bQ4 = new float[4], bufferQ4 = new float[4];
//        QuaternionTranslator.axisAngleToQuaternion(vecA,aQ4);
//        QuaternionTranslator.axisAngleToQuaternion(vecB,bQ4);
//
//        QuaternionTranslator.rotateQuaternion(aQ4,rotationQuaternion,bufferQ4);
//        QuaternionTranslator.quaternionToAxisAngle(bufferQ4,buffer);
//
//        error = VectorTranslator.getAngleBetweenVectors(vecB,buffer);
//        if(Float.isNaN(error) || error > ANGULAR_ERROR_TOLERANCE){
//            String message = "Quaternion rotation is not cohesive\n" +
//                    String.format("A = v(%.4f,%.4f,%.4f)\n", vecA[0], vecA[1], vecA[2]) +
//                    String.format("B = v(%.4f,%.4f,%.4f)\n", vecB[0], vecB[1], vecB[2]) +
//                    String.format("Aq = v(%.4f,%.4f,%.4f,%.4f)\n", aQ4[0], aQ4[1], aQ4[2], aQ4[3]) +
//                    String.format("Bq = v(%.4f,%.4f,%.4f,%.4f)\n", bQ4[0], bQ4[1], bQ4[2], bQ4[3]) +
//                    String.format("axis = v(%.4f,%.4f,%.4f)\n", rotationAxis[0], rotationAxis[1], rotationAxis[2]) +
//                    String.format("rotated = v(%.4f,%.4f,%.4f)\n", buffer[0], buffer[1], buffer[2]) +
//                    String.format("error = %f\n",error);
//            throw new RuntimeException(message);
//        } else {
//            System.out.println("Quaternion rotation test is cohesive :)");
//        }
//
//        QuaternionTranslator.copy(aQ4,bufferQ4);
//        QuaternionTranslator.applyRotation(bufferQ4,rotationAxis);
//        QuaternionTranslator.quaternionToAxisAngle(bufferQ4,buffer);
//
//        error = VectorTranslator.getAngleBetweenVectors(vecB,buffer);
//        if(error > ANGULAR_ERROR_TOLERANCE){
//            String message = "Quaternion rotation with axis angle is not cohesive\n" +
//                    String.format("error = %f\n",error);
//            throw new RuntimeException(message);
//        } else {
//            System.out.println("Quaternion rotation with axis angle test is cohesive :)");
//        }

        System.out.println();
    }
}
