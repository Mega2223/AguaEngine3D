package net.mega2223.aguaengine3d.physics;

import net.mega2223.aguaengine3d.mathematics.MathUtils;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;

public class PhysicsManager {
    private PhysicsManager(){}

    //too much?
    private static final float[] buffer1 = new float[3];
    private static final float[] buffer2 = new float[3];
    private static final float[] bufferQuaternion = new float[4];
    private static final float[] bufferQuaternion2 = new float[4];
    private static final float[] bufferQuaternion3 = new float[4];


    public static float getAcceleration(float mass, float force, boolean isInverseMass){
        return isInverseMass ? force * mass : force * (1/mass);
    }

    public static float getAcceleration(float time,float firstMeasurement,float secondMeasurement){
        return (firstMeasurement - secondMeasurement)/time;
    }

    public static float getVelocity(float time, float firstMeasurement, float secondMeasurement){
        return (firstMeasurement - secondMeasurement)/time;
    }

    public static float getVelocity(float x1, float y1, float z1, float x2, float y2, float z2, float dT){
        return getVelocity(dT,x1,x2)+ getVelocity(dT,y1,y2)+ getVelocity(dT,z1,z2);
    }

    public static float getClosingVelocity(float[] p1, float[] p2, float[] v1, float[] v2){
        VectorTranslator.subtractFromVector(v1,v2,buffer1);
        VectorTranslator.subtractFromVector(p1,p2,buffer2);
        VectorTranslator.normalizeVec3(buffer2);
        return VectorTranslator.getScalarProduct(buffer1,buffer2);
    }
    public static float getClosingVelocity(float x1, float y1, float z1, float vx1, float vy1, float vz1, float x2, float y2, float z2, float vx2, float vy2, float vz2){
        VectorTranslator.subtractFromVector(vx1,vy1,vz1,vx2,vy2,vz2, buffer1);
        VectorTranslator.subtractFromVector(x1,y1,z1,x2,y2,z2, buffer2);
        VectorTranslator.normalizeVec3(buffer2);
        return VectorTranslator.getScalarProduct(buffer1,buffer2);
    }

    public static void getContactNormal(float x1, float y1, float z1, float x2, float y2, float z2, float[] dest){
        VectorTranslator.subtractFromVector(x1,y1,z1,x2,y2,z2, dest);
        VectorTranslator.normalizeVec3(dest);
    }
    public static void getContactNormal(float[] v1, float[] v2, float[] dest){
        getContactNormal(v1[0],v1[1],v1[2],v2[0],v2[1],v2[2],dest);
    }
    public static void genInnertiaTensor(float[] vertices, float verticeLenght, float[] dest){
        //todo
    }
    public static void addScaledQuaternions(float[] q1, float[] q2, float scale){
        addScaledQuaternions(q1,q2,scale,bufferQuaternion);
        System.arraycopy(bufferQuaternion,0,q1,0,4);
    }
    public static void addScaledQuaternions(float[] q1, float[] q2, float scale, float[] dest){
        System.arraycopy(q2,0,bufferQuaternion3,0,4);
        MathUtils.scaleAllElements(bufferQuaternion3,scale);
        PhysicsUtils.multiplyQuaternions(bufferQuaternion3,q1,bufferQuaternion2);
        dest[0]=q1[0]+bufferQuaternion2[0]*.5F;
        dest[1]=q1[1]+bufferQuaternion2[1]*.5F;
        dest[2]=q1[2]+bufferQuaternion2[2]*.5F;
        dest[3]=q1[3]+bufferQuaternion2[3]*.5F;
    }

}
