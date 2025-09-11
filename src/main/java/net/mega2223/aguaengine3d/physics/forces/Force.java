package net.mega2223.aguaengine3d.physics.forces;

import net.mega2223.aguaengine3d.physics.PhysicsObject;

public interface Force {
    void apply(PhysicsObject object, float deltaT);
}
