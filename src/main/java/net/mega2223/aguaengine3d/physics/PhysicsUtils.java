package net.mega2223.aguaengine3d.physics;

public class PhysicsUtils {
    private PhysicsUtils(){}

    public static void generateTensorForRect(float vx, float vy, float vz, float mass,float[] result){
        genericInertiaTensor(
                (1/12F) * mass * (vy*vy + vz*vz),
                (1/12F) * mass * (vx*vx + vz*vz),
                (1/12F) * mass * (vx*vx + vy*vy),result
                );
    }

    public static void genericInertiaTensor(float mx, float my, float mz, float[] result){
        result[0] = mx;
        result[4] = my;
        result[8] = mz;
    }
    //not sure this works lmao
    public static void rotateInertiaTensor(float[] inn3, float[] rotationMat, float[] result){
        float t4 = rotationMat[0] * inn3[0] + rotationMat[1] * inn3[3] + rotationMat[2] * inn3[6];
        float t9 = rotationMat[0] * inn3[1] + rotationMat[1] * inn3[4] + rotationMat[2] * inn3[7];
        float t14 = rotationMat[0] * inn3[2] + rotationMat[1] * inn3[5] + rotationMat[2] * inn3[8];
        float t28 = rotationMat[4] * inn3[0] + rotationMat[5] * inn3[3] + rotationMat[6] * inn3[6];
        float t33 = rotationMat[4] * inn3[1] + rotationMat[5] * inn3[4] + rotationMat[6] * inn3[7];
        float t38 = rotationMat[4] * inn3[2] + rotationMat[5] * inn3[5] + rotationMat[6] * inn3[8];
        float t52 = rotationMat[8] * inn3[0] + rotationMat[9] * inn3[3] + rotationMat[10] * inn3[6];
        float t57 = rotationMat[8] * inn3[1] + rotationMat[9] * inn3[4] + rotationMat[10] * inn3[7];
        float t62 = rotationMat[8] * inn3[2] + rotationMat[9] * inn3[5] + rotationMat[10] * inn3[8];
        result[0] = t4 * rotationMat[0] + t9 * rotationMat[1] + t14 * rotationMat[2];
        result[1] = t4 * rotationMat[4] + t9 * rotationMat[5] + t14 * rotationMat[6];
        result[2] = t4 * rotationMat[8] + t9 * rotationMat[9] + t14 * rotationMat[10];
        result[3] = t28 * rotationMat[0] + t33 * rotationMat[1] + t38 * rotationMat[2];
        result[4] = t28 * rotationMat[4] + t33 * rotationMat[5] + t38 * rotationMat[6];
        result[5] = t28 * rotationMat[8] + t33 * rotationMat[9] + t38 * rotationMat[10];
        result[6] = t52 * rotationMat[0] + t57 * rotationMat[1] + t62 * rotationMat[2];
        result[7] = t52 * rotationMat[4] + t57 * rotationMat[5] + t62 * rotationMat[6];
        result[8] = t52 * rotationMat[8] + t57 * rotationMat[9] + t62 * rotationMat[10];
        //this assumes a 3x3 innertia tensor with array lenght of 9
        //may produce math problems in the future
    }

    public static void genQuaternionFromAxis(float angleRadians, float x, float y, float z, float[] result){
        result[0] = (float) Math.cos(angleRadians/2);
        result[1] = (float) (x*Math.sin(angleRadians/2));
        result[2] = (float) (y*Math.sin(angleRadians/2));
        result[3] = (float) (z*Math.sin(angleRadians/2));
    }
    static float[] bufferQuaternion = new float[4];
    public static void multiplyQuaternions(float[] q1, float[] q2){
        multiplyQuaternions(q1,q2,bufferQuaternion);
        System.arraycopy(bufferQuaternion,0,q1,0,4);
    }

    public static void multiplyQuaternions(float[] q1, float[] q2, float[] result){
        result[0] = (q2[0]*q1[0] - q2[1]*q1[1] - q2[2]*q1[2] - q2[3]*q1[3]);
        result[1] = (q2[0]*q1[1] + q2[1]*q1[0] - q2[2]*q1[3] + q2[3]*q1[2]);
        result[2] = (q2[0]*q1[2] + q2[1]*q1[3] + q2[2]*q1[0] - q2[3]*q1[1]);
        result[3] = (q2[0]*q1[3] - q2[1]*q1[2] + q2[2]*q1[1] + q2[3]*q1[0]);
    }
    public static void multiplyQuaternions(float[] result, float q1w, float q1x, float q1y, float q1z, float q2w, float q2x, float q2y, float q2z){
        result[0] = (q2w * q1w - q2x * q1x - q2y * q1y - q2z * q1z);
        result[1] = (q2w * q1x + q2x * q1w - q2y * q1z + q2z * q1y);
        result[2] = (q2w * q1y + q2x * q1z + q2y * q1w - q2z * q1x);
        result[3] = (q2w * q1z - q2x * q1y + q2y * q1x + q2z * q1w);
    }
    /**Returns at ZXY order*/
    public static void radiansToQuaternion(float x, float y, float z, float[] result) {
        double cr = Math.cos(x* 0.5);
        double sr = Math.sin(x * 0.5);
        double cp = Math.cos(y * 0.5);
        double sp = Math.sin(y * 0.5);
        double cy = Math.cos(z * 0.5);
        double sy = Math.sin(z * 0.5);
        result[0] = (float) (cr * cp * cy + sr * sp * sy);
        result[1] = (float) (sr * cp * cy - cr * sp * sy);
        result[2] = (float) (cr * sp * cy + sr * cp * sy);
        result[3] = (float) (cr * cp * sy - sr * sp * cy);
    }
}
