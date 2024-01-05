package net.mega2223.aguaengine3d.physics.utils.objects.forces;

import net.mega2223.aguaengine3d.physics.objects.PhysicsForce;
import net.mega2223.aguaengine3d.physics.objects.PhysicsSystem;

public class ConstantForce implements PhysicsForce {
    final float aX, aY, aZ;
    public ConstantForce(float aX, float aY, float aZ){
        this.aX = aX; this.aY = aY; this.aZ = aZ;
    }

    @Override
    public void update(PhysicsSystem system, float time) {
        system.applyAcceleration(aX*time,aY*time,aZ*time);
    }
}
