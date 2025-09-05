package net.mega2223.aguaengine3d.physics.collisions;

import net.mega2223.aguaengine3d.physics.PhysicsContext;

public abstract class CollisionManager {
    protected final PhysicsContext physContext;

    public CollisionManager(PhysicsContext context){
        physContext = context;
    }

    public abstract void manageCollisions(float deltaT);
}
