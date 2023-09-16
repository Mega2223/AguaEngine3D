package net.mega2223.aguaengine3d.physics;

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
}
