package net.mega2223.aguaengine3d.physics.collisions;

import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.physics.PhysicsContext;
import net.mega2223.aguaengine3d.physics.PhysicsObject;

import java.util.List;

public class SimpleCollisionManager extends CollisionManager{

    public long iteration = 0;

    private static final float[] buffer = new float[4];

    public SimpleCollisionManager(PhysicsContext context) {
        super(context);
    }

    @Override
    public void doSinglePass(float deltaT) {
        List<PhysicsObject> objects = physContext.getObjects(); // fixme maybe too resource intensive
        for(PhysicsObject o1 : objects){
            o1 = o1.getActor();
            if(!(o1 instanceof Collideable)){continue;}
            //System.out.println("h");
            for(PhysicsObject o2 : objects){
                o2 = o2.getActor();
                if(o1 == o2 || !(o2 instanceof Collideable)){continue;}
                if (((Collideable) o1).collidesWith((Collideable) o2)){
			//System.out.println("COLLISION RAAARRRHHHH");
			CollisionMath.solveCollision(o1, o2, 1.0F);
                float depth = ((Collideable) o1).solveCollision((Collideable) o2,buffer);
                if (depth <= 0){
                     ((Collideable) o2).solveCollision((Collideable) o1,buffer);
                }
            }
        }
        iteration++;
    }
}
