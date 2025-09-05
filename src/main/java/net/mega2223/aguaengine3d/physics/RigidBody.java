package net.mega2223.aguaengine3d.physics;

public class RigidBody extends PhysicsObject {

    protected float[] rotationQuaternions = new float[4];
    //protected float[] angularVelocity = new float[4];

    public RigidBody(float mass) {
        super(mass);
    }
}
