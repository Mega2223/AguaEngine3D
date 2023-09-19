package net.mega2223.aguaengine3d.physics;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;

public class PhysicsUtils {
    private PhysicsUtils(){}

    public static void generateTensorForRect(float vx, float vy, float vz, float mass,float[] dest){
        generateInertiaTensor(
                1/12F * mass * (vy*vy+vz*vz),
                1/12F * mass * (vx*vx + vz*vz),
                1/12F * mass * (vx*vx + vy*vy),dest
                );
    }

    public static void generateInertiaTensor(float mx, float my, float mz, float[] dest){
        dest[0] = mx;
        dest[4] = my;
        dest[8] = mz;
    }

    public static void transformInertiaTensorQuaternion(float[] inn3, float[] rotationMat, float[] dest){
        float t4 = rotationMat[0] * inn3[0] + rotationMat[1] * inn3[3] + rotationMat[2] * inn3[6];
        float t9 = rotationMat[0] * inn3[1] + rotationMat[1] * inn3[4] + rotationMat[2] * inn3[7];
        float t14 = rotationMat[0] * inn3[2] + rotationMat[1] * inn3[5] + rotationMat[2] * inn3[8];
        float t28 = rotationMat[4] * inn3[0] + rotationMat[5] * inn3[3] + rotationMat[6] * inn3[6];
        float t33 = rotationMat[4] * inn3[1] + rotationMat[5] * inn3[4] + rotationMat[6] * inn3[7];
        float t38 = rotationMat[4] * inn3[2] + rotationMat[5] * inn3[5] + rotationMat[6] * inn3[8];
        float t52 = rotationMat[8] * inn3[0] + rotationMat[9] * inn3[3] + rotationMat[10] * inn3[6];
        float t57 = rotationMat[8] * inn3[1] + rotationMat[9] * inn3[4] + rotationMat[10] * inn3[7];
        float t62 = rotationMat[8] * inn3[2] + rotationMat[9] * inn3[5] + rotationMat[10] * inn3[8];
        dest[0] = t4 * rotationMat[0] + t9 * rotationMat[1] + t14 * rotationMat[2];
        dest[1] = t4 * rotationMat[4] + t9 * rotationMat[5] + t14 * rotationMat[6];
        dest[2] = t4 * rotationMat[8] + t9 * rotationMat[9] + t14 * rotationMat[10];
        dest[3] = t28 * rotationMat[0] + t33 * rotationMat[1] + t38 * rotationMat[2];
        dest[4] = t28 * rotationMat[4] + t33 * rotationMat[5] + t38 * rotationMat[6];
        dest[5] = t28 * rotationMat[8] + t33 * rotationMat[9] + t38 * rotationMat[10];
        dest[6] = t52 * rotationMat[0] + t57 * rotationMat[1] + t62 * rotationMat[2];
        dest[7] = t52 * rotationMat[4] + t57 * rotationMat[5] + t62 * rotationMat[6];
        dest[8] = t52 * rotationMat[8] + t57 * rotationMat[9] + t62 * rotationMat[10];
    }

    public static void genQuaternionFromAxis(float angleRadians, float x, float y, float z, float[] dest){
        dest[0] = (float) Math.cos(angleRadians/2);
        dest[1] = (float) (x*Math.sin(angleRadians/2));
        dest[2] = (float) (y*Math.sin(angleRadians/2));
        dest[3] = (float) (z*Math.sin(angleRadians/2));
    }
    static float[] bufferQuaternion = new float[4];
    public static void multiplyQuaternions(float[] q1, float[] q2){
        multiplyQuaternions(q1,q2,bufferQuaternion);
        System.arraycopy(bufferQuaternion,0,q1,0,4);
    }

    public static void multiplyQuaternions(float[] q1, float[] q2, float[] dest){
        dest[0] = (q1[0]*q2[0] - q1[1]*q2[2] - q1[2]*q2[2] - q1[3]*q2[3]);
        dest[1] = (q1[0]*q2[1] + q1[1]*q2[0] - q1[2]*q2[3] - q1[3]*q2[2]);
        dest[2] = (q1[0]*q2[2] - q1[1]*q2[3] + q1[2]*q2[0] - q1[3]*q2[1]);
        dest[3] = (q1[0]*q2[3] + q1[1]*q2[2] - q1[2]*q2[1] - q1[3]*q2[0]);
    }
}