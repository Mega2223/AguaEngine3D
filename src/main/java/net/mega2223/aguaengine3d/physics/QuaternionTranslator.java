package net.mega2223.aguaengine3d.physics;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.misc.annotations.Modified;

public class QuaternionTranslator {
    private QuaternionTranslator(){}

    public static final int W = 0, X = 1, Y = 2, Z = 3;

    public static void rotationMatrixToQuaternion(float[] rotationMat4, @Modified float[] result){
        // TODO womp womp
        throw new RuntimeException("Fala pro Julio que ele esqueceu de implementar isso :p");
    }

    public static void axisAngleToQuaternion(float[] axisAngle, @Modified float[] result){
        axisAngleToQuaternion(axisAngle[0], axisAngle[1], axisAngle[2], result);
    }

    public static void axisAngleToQuaternion(float x, float y, float z, @Modified float[] result) {
        //w + xi + yj + yk
        final float angle = VectorTranslator.magnitude(x,y,z);
        if(angle == 0){ x = y = z = 0; }
        else { x /= angle; y /= angle; z /= angle; }
        result[W] = (float) Math.cos(angle / 2);
        float s = (float) Math.sin(angle / 2);
        result[X] = x * s; result[Y] = y * s; result[Z] = z * s;
    }

    public static void rotateAlongQuaternion(float[] vec4, float[] rotationQ4, @Modified float[] result){
        // TODO
    }

    public static void multiplyQuaternions(float[] q4A, float[] q4B, @Modified float[] result){
        result[W] = q4A[W] * q4B[W] - q4A[X] * q4B[X] - q4A[Y] * q4B[Y] - q4A[Z] * q4B[Z];
        result[X] = q4A[W] * q4B[X] + q4A[X] * q4B[W] + q4A[Y] * q4B[Z] - q4A[Z] * q4B[Y];
        result[Y] = q4A[W] * q4B[Y] - q4A[X] * q4B[Z] + q4A[Y] * q4B[W] + q4A[Z] * q4B[X];
        result[Z] = q4A[W] * q4B[Z] + q4A[X] * q4B[Y] - q4A[Y] * q4B[X] + q4A[Z] * q4B[W];
    }

    public static void getInverseRotation(float[] rotationQ4, @Modified float[] result){
        // TODO
    }

    public static float getMagnitude(float[] q4){
        return (float) Math.sqrt(q4[W] * q4[W] + q4[X] * q4[X] + q4[Y] * q4[Y] +  q4[Z] * q4[Z]);
    }

    public static void normalize(@Modified float[] q4){
        float mag = getMagnitude(q4);
        if(mag == 0){
            q4[W] = 1; mag = 1;
        }
        q4[W]/=mag; q4[X]/=mag; q4[Y]/=mag; q4[Z]/=mag;
    }

    public static void scalarMultiplication(@Modified float[] q4,float scalar){
        q4[0]*=scalar; q4[1]*=scalar; q4[2]*=scalar; q4[3]*=scalar;
    }

    public static void simpleAddition(@Modified float[] q4A, float[] q4B){
        q4A[0] += q4B[0];
        q4A[1] += q4B[1];
        q4A[2] += q4B[2];
        q4A[3] += q4B[3];
    }

    public static void conjugate(float[] q4, @Modified float[] dest){
        dest[W] = q4[W];
        dest[X] = -q4[X];
        dest[Y] = -q4[Y];
        dest[Z] = -q4[Z];
    }

    static final float[] conjugateBuffer = new float[4], vectorBuffer = new float[4];
    public static void rotateQuaternion(float[] q4, float[] amountQ4, @Modified float[] dest){
        multiplyQuaternions(amountQ4,q4,dest);
//        conjugate(q4,conjugateBuffer);
//        vectorBuffer[0] = 0; vectorBuffer[1] = amountQ4[0]; vectorBuffer[2] = amountQ4[1]; vectorBuffer[3] = amountQ4[2];

    }

    public static void rotateVectorByQuaternion(float[] vec3, float[] q4, @Modified float[] dest){
        conjugate(q4,conjugateBuffer);
        vectorBuffer[0] = 0; vectorBuffer[1] = vec3[0]; vectorBuffer[2] = vec3[1]; vectorBuffer[3] = vec3[2];
        multiplyQuaternions(vectorBuffer,conjugateBuffer,dest);
        multiplyQuaternions(q4,dest,vectorBuffer);
        dest[0] = vectorBuffer[1]; dest[1] = vectorBuffer[2]; dest[2] = vectorBuffer[3]; dest[3] = 0;
    }

    public static void rotateQuaternionAndScale(float[] q4, float[] amountQ4, float scalar, @Modified float[] dest){
//        multiplyQuaternions(q4,amountQ4,dest);
//        scalarMultiplication(dest,.5F * scalar); // TODO isso funciona?
//        simpleAddition(dest,q4); //provavelmente nao
    }

    private static final float[] rotationQ = new float[4];
    private static final float[] bufferQ4 = new float[4];
    public static void applyRotation(@Modified float[] q4, float[] rotation){
//        rotation[0] = 0;
//        rotation[1] = 0;
//        rotation[2] = 0;
//        rotation[3] = 0;
//        axisAngleToQuaternion(rotation, rotationQ);
//        rotateQuaternion(q4,rotation,bufferQ4);
//        copy(bufferQ4,q4);
    }

    public static void quaternionToAxisAngle(float[] q4, @Modified float[] dest){
        final float wS = q4[W] * q4[W];
        final float wSm = 1 - wS;
        final float ang = (float) (2 * Math.acos(q4[W]));
        dest[0] = ang * (float) (q4[X] / Math.sqrt(wSm));
        dest[1] = ang * (float) (q4[Y] / Math.sqrt(wSm));
        dest[2] = ang * (float) (q4[Z] / Math.sqrt(wSm));
    }

    public static void copy(float[] q4, float[] dest){
        dest[0] = q4[0]; dest[1] = q4[1];
        dest[2] = q4[2]; dest[3] = q4[3];
    }
}
