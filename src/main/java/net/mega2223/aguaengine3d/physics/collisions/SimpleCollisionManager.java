package net.mega2223.aguaengine3d.physics.collisions;

import net.mega2223.aguaengine3d.physics.PhysicsContext;
import net.mega2223.aguaengine3d.physics.objects.Particle;

import java.util.List;

public class SimpleCollisionManager extends CollisionManager{
    public SimpleCollisionManager(PhysicsContext context) {
        super(context);
    }

    @Override
    public void manageCollisions(float deltaT) {
        List<Particle> objects = physContext.getObjects(); // fixme maybe too resource intensive
        for(Particle o1 : objects){
            if(!(o1 instanceof Collideable)){continue;}
            for(Particle o2 : objects){
                if(o1 == o2 || !(o2 instanceof Collideable)){continue;}
                if (((Collideable) o1).collidesWith((Collideable) o2)){

                }
            }
        }
    }
}
