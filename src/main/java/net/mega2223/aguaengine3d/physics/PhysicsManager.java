package net.mega2223.aguaengine3d.physics;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;

public class PhysicsManager {
    private PhysicsManager(){}

    private static final float[] buffer1 = new float[3];
    private static final float[] buffer2 = new float[3];

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
        VectorTranslator.normalizeVector(buffer2);
        return VectorTranslator.getScalarProduct(buffer1,buffer2);
    }
    public static float getClosingVelocity(float x1, float y1, float z1, float vx1, float vy1, float vz1, float x2, float y2, float z2, float vx2, float vy2, float vz2){
        VectorTranslator.subtractFromVector(vx1,vy1,vz1,vx2,vy2,vz2, buffer1);
        VectorTranslator.subtractFromVector(x1,y1,z1,x2,y2,z2, buffer2);
        VectorTranslator.normalizeVector(buffer2);
        return VectorTranslator.getScalarProduct(buffer1,buffer2);
    }

    public static void getContactNormal(float x1, float y1, float z1, float x2, float y2, float z2, float[] dest){
        VectorTranslator.subtractFromVector(x1,y1,z1,x2,y2,z2, dest);
        VectorTranslator.normalizeVector(dest);
    }
    public static void getContactNormal(float[] v1, float[] v2, float[] dest){
        getContactNormal(v1[0],v1[1],v1[2],v2[0],v2[1],v2[2],dest);
    }
    public static void genInnertiaTensor(float[] vertices, float verticeLenght, float[] dest){

    }

}
