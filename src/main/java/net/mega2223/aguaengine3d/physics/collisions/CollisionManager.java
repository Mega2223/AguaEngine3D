package net.mega2223.aguaengine3d.physics.collisions;

import net.mega2223.aguaengine3d.physics.PhysicsContext;

public abstract class CollisionManager {
    protected final PhysicsContext physContext;

    public CollisionManager(PhysicsContext context){
        physContext = context;
    }

    public void managerCollisions(float deltaT){
        manageCollisions(deltaT,1);
    }

    public void manageCollisions(float deltaT, int passes){
        for (int i = 0; i < passes; i++) {
            doSinglePass(deltaT);
        }
    }

    abstract void doSinglePass(float deltaT);
}
