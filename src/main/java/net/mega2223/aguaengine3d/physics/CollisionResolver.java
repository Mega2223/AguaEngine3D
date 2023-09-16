package net.mega2223.aguaengine3d.physics;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.objects.PhysicsSystem;

import java.util.Arrays;

public class CollisionResolver {
    private CollisionResolver(){}

    private static final float[] buffer1 = new float[3];
    private static final float[] buffer2 = new float[3];

    public static void resolve(PhysicsSystem obj1, PhysicsSystem obj2){
        resolve(obj1, obj2,.5F);
    }

    public static void resolve(PhysicsSystem obj1, PhysicsSystem obj2, final float restitution){
        float vx1 = obj1.getVelocityX(), vy1 = obj1.getVelocityY(), vz1 = obj1.getVelocityZ(),
              cx1 = obj1.getCoordX(), cy1 = obj1.getCoordY(), cz1 = obj1.getCoordZ(),
              vx2 = obj2.getVelocityX(), vy2 = obj2.getVelocityY(), vz2 = obj2.getVelocityZ(),
              cx2 = obj2.getCoordX(), cy2 = obj2.getCoordY(), cz2 = obj2.getCoordZ();

        float separatingVelocity = - PhysicsManager.getClosingVelocity(cx1,cy1,cz1,vx1,vy1,vz1,cx2,cy2,cz2,vx2,vy2,vz2);
        if(separatingVelocity >0 ){return;}
        float sep = separatingVelocity * restitution;
        float deltaV = sep - separatingVelocity;
        float inverseMass = obj1.getInverseMass() + obj2.getInverseMass();
        if(inverseMass <= 0){return;}
        float impulse = deltaV/inverseMass;
        PhysicsManager.getContactNormal(cx1,cy1,cz1,cx2,cy2,cz2, buffer1);
        VectorTranslator.scaleVector(buffer1,impulse);
        System.arraycopy(buffer1,0,buffer2,0,3);
        VectorTranslator.scaleVector(buffer2,obj1.getInverseMass());
        obj1.applyImpulse(buffer2);
        VectorTranslator.flipVector(buffer1);
        System.arraycopy(buffer1,0,buffer2,0,3);
        VectorTranslator.scaleVector(buffer2,obj2.getInverseMass());
        obj2.applyImpulse(buffer2);
    }
}
