package net.mega2223.aguaengine3d.physics.collisions;

import net.mega2223.aguaengine3d.physics.PhysicsContext;
import net.mega2223.aguaengine3d.physics.PhysicsObject;

import java.util.List;

public class SimpleCollisionManager extends CollisionManager{
    private static final float[] buffer = new float[4];

    public SimpleCollisionManager(PhysicsContext context) {
        super(context);
    }

    @Override
    public void manageCollisions(float deltaT) {
        List<PhysicsObject> objects = physContext.getObjects(); // fixme maybe too resource intensive
        for(PhysicsObject o1 : objects){
            o1 = o1.getActor();
            if(!(o1 instanceof Collideable)){continue;}
            for(PhysicsObject o2 : objects){
                o2 = o2.getActor();
                if(o1 == o2 || !(o2 instanceof Collideable)){continue;}
                float depth = ((Collideable) o1).getCollision((Collideable) o2,buffer);
                if (depth > 0){
                    CollisionMath.solveContact(o1, o2, buffer, depth);
			        CollisionMath.solveCollision(o1, o2, 1.0F);
                }
            }
        }
    }
}
