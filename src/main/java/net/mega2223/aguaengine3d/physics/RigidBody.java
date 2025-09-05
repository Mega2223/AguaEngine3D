package net.mega2223.aguaengine3d.physics;

import net.mega2223.aguaengine3d.physics.objects.Particle;

public class RigidBody extends Particle {

    protected float[] rotationQuaternions = new float[4];
    //protected float[] angularVelocity = new float[4];

    public RigidBody(float mass) {
        super(mass);
    }
}
