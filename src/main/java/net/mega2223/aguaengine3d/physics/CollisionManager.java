package net.mega2223.aguaengine3d.physics;

public abstract class CollisionManager {
    protected final PhysicsContext physContext;

    public CollisionManager(PhysicsContext context){
        physContext = context;
    }

    public abstract void manageCollisions(float deltaT);
}
