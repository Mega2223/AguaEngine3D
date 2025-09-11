package net.mega2223.aguaengine3d.physics;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.misc.annotations.Modified;

import java.util.Arrays;

import static net.mega2223.aguaengine3d.mathematics.MatrixTranslator.M4.*;

public class QuaternionTranslator {
    private QuaternionTranslator(){}

    public static final int W = 0, X = 1, Y = 2, Z = 3;

    public static void rotationMatrixFromQuaternion(float[] q, @Modified float[] resultM4){
        float q0_2 = q[0] * q[0]; float q1_2 = q[1] * q[1];
        float q2_2 = q[2] * q[2]; float q3_2 = q[3] * q[3];
        resultM4[0] = q0_2 + q1_2 - q2_2 - q3_2;
        resultM4[1] = 2 * q[1] * q[2] - 2 * q[0] * q[3];
        resultM4[2] = 2 * q[1] * q[3] + 2 * q[0] * q[2];
        resultM4[3] = 0;
        resultM4[4] = 2 * q[1] * q[2] + 2 * q[0] * q[3]; // l2
        resultM4[5] = q0_2 - q1_2 + q2_2 - q3_2;
        resultM4[6] = 2 * q[2] * q[3] - 2 * q[0] *q [1];
        resultM4[7] = 0;
        resultM4[8] = 2 * q[1] * q[3] - 2 * q[0] * q[2]; // l3
        resultM4[9] = 2 * q[2] * q[3] + 2 * q[0] * q[1];
        resultM4[10] = q0_2 - q1_2 - q2_2 + q3_2;
        resultM4[11] = 0;
        resultM4[12] = 0; //l4
        resultM4[13] = 0;
        resultM4[14] = 0;
        resultM4[15] = 1;
    }

    public static void quaternionFromRotationMatrix(float[] rotationMat4, @Modified float[] resultQ4){
        float[] m = rotationMat4; float[] q = resultQ4;
        q[0] = (float) Math.sqrt((1 + m[0] + m[5] + m[10])/4F);
        q[1] = (float) Math.sqrt((1 + m[0] - m[5] - m[10])/4F);
        q[2] = (float) Math.sqrt((1 - m[0] + m[5] - m[10])/4F);
        q[3] = (float) Math.sqrt((1 - m[0] - m[5] + m[10])/4F);
        int greater = // I love programming :)
                q[0] > q[1] ?
                    q[0] > q[2] ?
                        q[0] > q[3] ? 0 : 3 :
                        q[2] > q[3] ? 2 : 3 :
                    q[1] > q[2] ?
                        q[1] > q[3] ? 1 : 3 :
                        q[2] > q[3] ? 2 : 3 ;
        switch (greater){
            case 0:
                q[1] = (m[M_32.i] - m[M_23.i])/(4*q[0]);
                q[2] = (m[M_13.i] - m[M_31.i])/(4*q[0]);
                q[3] = (m[M_21.i] - m[M_12.i])/(4*q[0]);
                return;
            case 1:
                q[0] = (m[M_32.i] - m[M_23.i])/(4*q[1]);
                q[2] = (m[M_12.i] + m[M_21.i])/(4*q[1]);
                q[3] = (m[M_13.i] + m[M_31.i])/(4*q[1]);
                return;
            case 2:
                q[0] = (m[M_13.i] - m[M_31.i])/(4*q[2]);
                q[1] = (m[M_12.i] + m[M_21.i])/(4*q[2]);
                q[3] = (m[M_23.i] + m[M_32.i])/(4*q[2]);
                return;
            case 3:
                q[0] = (m[M_21.i] - m[M_12.i])/(4*q[3]);
                q[1] = (m[M_13.i] + m[M_31.i])/(4*q[3]);
                q[2] = (m[M_23.i] + m[M_32.i])/(4*q[3]);
                return;
        }
    }

    public static void axisAngleToQuaternion(float[] axisAngle, @Modified float[] result){
        axisAngleToQuaternion(axisAngle[0], axisAngle[1], axisAngle[2], result);
    }

    private static final float PI2 = (float) (Math.PI * 2);

    public static void axisAngleToQuaternion(float x, float y, float z, @Modified float[] result) {
        //w + xi + yj + yk
        float angle = VectorTranslator.magnitude(x,y,z);
        if(angle == 0){ x = y = z = 0; }
        else { x /= angle; y /= angle; z /= angle; }
        angle %= PI2;
        result[W] = (float) Math.cos(angle / 2);
        float s = (float) Math.sin(angle / 2);
        result[X] = x * s; result[Y] = y * s; result[Z] = z * s;
    }

    //funciona
    public static void quaternionProduct(float[] q4A, float[] q4B, @Modified float[] result){
        result[W] = q4A[W] * q4B[W] - q4A[X] * q4B[X] - q4A[Y] * q4B[Y] - q4A[Z] * q4B[Z];
        result[X] = q4A[W] * q4B[X] + q4A[X] * q4B[W] + q4A[Y] * q4B[Z] - q4A[Z] * q4B[Y];
        result[Y] = q4A[W] * q4B[Y] - q4A[X] * q4B[Z] + q4A[Y] * q4B[W] + q4A[Z] * q4B[X];
        result[Z] = q4A[W] * q4B[Z] + q4A[X] * q4B[Y] - q4A[Y] * q4B[X] + q4A[Z] * q4B[W];
    }

    public static void getInverseRotation(float[] rotationQ4, @Modified float[] result){
        conjugate(rotationQ4,result);//TODO testar
    }

    public static float getMagnitude(float[] q4){
        return (float) Math.sqrt(q4[W] * q4[W] + q4[X] * q4[X] + q4[Y] * q4[Y] +  q4[Z] * q4[Z]);
    }

    public static void normalize(@Modified float[] q4){
        float mag = getMagnitude(q4);
        if(mag == 0){q4[W] = 1; mag = 1;}
        q4[W]/=mag; q4[X]/=mag; q4[Y]/=mag; q4[Z]/=mag;
    }

    public static void scalarProduct(@Modified float[] q4, float scalar){
        q4[0]*=scalar; q4[1]*=scalar; q4[2]*=scalar; q4[3]*=scalar;
    }

    public static void simpleAddition(@Modified float[] q4A, float[] q4B){
        q4A[0] += q4B[0]; q4A[1] += q4B[1];
        q4A[2] += q4B[2]; q4A[3] += q4B[3];
    }

    public static void simpleAddition(float[] q4A, float[] q4B, @Modified float[] dest){
        dest[0] = q4A[0] + q4B[0]; dest[1] = q4A[1] + q4B[1];
        dest[2] = q4A[2] + q4B[2]; dest[3] = q4A[3] + q4B[3];
    }

    public static void conjugate(float[] q4, @Modified float[] dest){
        dest[W] = q4[W];
        dest[X] = -q4[X];
        dest[Y] = -q4[Y];
        dest[Z] = -q4[Z];
    }

    private static final float[] rotationBuffer = new float[4];
    public static void addRotations(float[] q4a, float[] q4b, @Modified float[] dest){
        quaternionProduct(q4a,q4b,dest); // Passa os testes
    }

    static final float[] qRotationBuffer = new float[4];
    public static void rotateQuaternionByAxis(float[] q4, float[] axisVec3, @Modified float[] dest){
        Arrays.fill(dest,0); // FIXME KKKKKKKKKKKKKKK isso pelo menos funciona
        quaternionToAxisAngle(q4,qRotationBuffer);
        VectorTranslator.rotateAlongAxis(qRotationBuffer,axisVec3,dest);
        copy(dest,qRotationBuffer);
        axisAngleToQuaternion(qRotationBuffer,dest);
    }

    private static void vecToImaginary(float[] vec3, @Modified float[] dest){
        dest[0] = 0; dest[1] = vec3[0]; dest[2] = vec3[1]; dest[3] = vec3[2];
    }

    // Isso 100% funciona
    static final float[] conjugateBuffer = new float[4], vectorBuffer = new float[4];
    public static void rotateVectorByQuaternion(float[] vec3, float[] q4, @Modified float[] dest){
        conjugate(q4,conjugateBuffer);
        vecToImaginary(vec3,vectorBuffer);
        quaternionProduct(vectorBuffer,conjugateBuffer,dest);
        quaternionProduct(q4,dest,vectorBuffer);
        dest[0] = vectorBuffer[1]; dest[1] = vectorBuffer[2]; dest[2] = vectorBuffer[3]; dest[3] = 0;
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

    public static void flip(@Modified float[] q4){
        q4[0] = -q4[0]; q4[1] = -q4[1];
        q4[2] = -q4[2]; q4[3] = -q4[3];
    }

    private static final float[] angularVelocityQ4Buffer = new float[4];
    public static void addAngularVelocity(float[] rotationQ4, float[] angVelVec3, float deltaT, @Modified float[] dest){
        vecToImaginary(angVelVec3, angularVelocityQ4Buffer);
        quaternionProduct(angularVelocityQ4Buffer,rotationQ4,qRotationBuffer);
        scalarProduct(qRotationBuffer,deltaT/2);
        //addRotations(rotationQ4,qRotationBuffer,dest); // TODO veja
        simpleAddition(rotationQ4,qRotationBuffer,dest);
    }
}
