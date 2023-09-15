package net.mega2223.aguaengine3d.physics;

public class PhysicsManager {
    private PhysicsManager(){}

    public static float getAcceleration(float mass, float force, boolean isInverseMass){
        return isInverseMass ? force * mass : force * (1/mass);
    }

    public static float getAcceleration(float time,float firstMeasurement,float secondMeasurement){
        return (firstMeasurement - secondMeasurement)/time;
    }

    public static float getSpeed(float time,float firstMeasurement,float secondMeasurement){
        return (firstMeasurement - secondMeasurement)/time;
    }

    public static float getSpeed(float x1, float y1, float z1, float x2, float y2, float z2, float dT){
        return getSpeed(dT,x1,x2)+getSpeed(dT,y1,y2)+getSpeed(dT,z1,z2);
    }
}
