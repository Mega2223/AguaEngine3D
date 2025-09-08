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
            q4[0] = 1; mag = 1;
        }
        q4[0]/=mag; q4[1]/=mag; q4[2]/=mag; q4[3]/=mag;
    }
}
