package net.mega2223.aguaengine3d.physics;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.objects.PhysicsSystem;
import net.mega2223.aguaengine3d.physics.objects.RigidBodySystem;

public class CollisionResolver {

    public static final float DEF_RESTITUTION = .5F;

    private CollisionResolver(){} //TODO deltaT variable

    private static final float[] buffer1 = new float[3];
    private static final float[] buffer2 = new float[3];

    public static void resolveConflict(PhysicsSystem obj1, float pX, float pY, float pZ, final float confDepth){
        if(confDepth <= 0){return;}
        float invMass = obj1.getInverseMass();
        if(invMass <= 0){return;}
        float vx1 = obj1.getVelocityX(), vy1 = obj1.getVelocityY(), vz1 = obj1.getVelocityZ(),
                cx1 = obj1.getCoordX(), cy1 = obj1.getCoordY(), cz1 = obj1.getCoordZ();

        PhysicsManager.getContactNormal(cx1,cy1,cz1,pX,pY,pZ, buffer1);
        VectorTranslator.scaleVec3(buffer1, -confDepth/invMass);
        VectorTranslator.scaleVec3(buffer1,obj1.getInverseMass());
        VectorTranslator.flipVector(buffer1);
        obj1.applyTransformation(buffer1);
    }

    public static void resolveConflict(PhysicsSystem obj1, PhysicsSystem obj2, final float confDepth){
        if(confDepth <= 0){return;}
        float invMass = obj1.getInverseMass() + obj2.getInverseMass();
        if(invMass <= 0){return;}
        float vx1 = obj1.getVelocityX(), vy1 = obj1.getVelocityY(), vz1 = obj1.getVelocityZ(),
                cx1 = obj1.getCoordX(), cy1 = obj1.getCoordY(), cz1 = obj1.getCoordZ(),
                vx2 = obj2.getVelocityX(), vy2 = obj2.getVelocityY(), vz2 = obj2.getVelocityZ(),
                cx2 = obj2.getCoordX(), cy2 = obj2.getCoordY(), cz2 = obj2.getCoordZ();

        PhysicsManager.getContactNormal(cx1,cy1,cz1,cx2,cy2,cz2, buffer1);
        VectorTranslator.scaleVec3(buffer1, -confDepth/invMass);
        System.arraycopy(buffer1,0,buffer2,0,3);
        VectorTranslator.scaleVec3(buffer2,obj1.getInverseMass());
        VectorTranslator.flipVector(buffer2);
        obj1.applyTransformation(buffer2);
        System.arraycopy(buffer1,0,buffer2,0,3);
        VectorTranslator.scaleVec3(buffer2,obj2.getInverseMass());
        obj2.applyTransformation(buffer2);
    }

    public static void resolveCollision(PhysicsSystem obj1, float px, float py, float pz, final float restitution){
        float vx1 = obj1.getVelocityX(), vy1 = obj1.getVelocityY(), vz1 = obj1.getVelocityZ(),
                cx1 = obj1.getCoordX(), cy1 = obj1.getCoordY(), cz1 = obj1.getCoordZ();

        float separatingVelocity = - PhysicsManager.getClosingVelocity(cx1,cy1,cz1,vx1,vy1,vz1,px,py,pz,0,0,0);
        if(separatingVelocity >0 ){return;}
        float sep = separatingVelocity * restitution;
        float deltaV = sep - separatingVelocity;
        float inverseMass = obj1.getInverseMass();
        if(inverseMass <= 0){return;}
        float impulse = deltaV/inverseMass;
        PhysicsManager.getContactNormal(cx1,cy1,cz1,px,py,pz, buffer1);
        VectorTranslator.scaleVec3(buffer1,impulse);
        VectorTranslator.scaleVec3(buffer1,obj1.getInverseMass());
        obj1.applyImpulse(buffer1);
    }

    public static void resolveCollision(PhysicsSystem obj1, PhysicsSystem obj2){
        resolveCollision(obj1, obj2, DEF_RESTITUTION);
    }

    public static void resolveCollision(PhysicsSystem obj1, PhysicsSystem obj2, final float restitution){
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
        VectorTranslator.scaleVec3(buffer1,impulse);
        System.arraycopy(buffer1,0,buffer2,0,3);
        VectorTranslator.scaleVec3(buffer2,obj1.getInverseMass());
        obj1.applyImpulse(buffer2);
        VectorTranslator.flipVector(buffer1);
        System.arraycopy(buffer1,0,buffer2,0,3);
        VectorTranslator.scaleVec3(buffer2,obj2.getInverseMass());
        obj2.applyImpulse(buffer2);
    }

    public static void resolveCollision(RigidBodySystem obj1, RigidBodySystem obj2, final float restitution){
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
        VectorTranslator.scaleVec3(buffer1,impulse);
        System.arraycopy(buffer1,0,buffer2,0,3);
        VectorTranslator.scaleVec3(buffer2,obj1.getInverseMass());
        obj1.applyImpulse(buffer2[0],buffer2[1], buffer2[2],cx1,cy1,cz1,false);
        VectorTranslator.flipVector(buffer1);
        System.arraycopy(buffer1,0,buffer2,0,3);
        VectorTranslator.scaleVec3(buffer2,obj2.getInverseMass());
        obj2.applyImpulse(buffer2[0],buffer2[1], buffer2[2],cx2,cy2,cz2,false);
    }

    public static void resolveCollision(RigidBodySystem obj1, float px, float py, float pz, final float restitution){
        float vx1 = obj1.getVelocityX(), vy1 = obj1.getVelocityY(), vz1 = obj1.getVelocityZ(),
                cx1 = obj1.getCoordX(), cy1 = obj1.getCoordY(), cz1 = obj1.getCoordZ();

        float separatingVelocity = - PhysicsManager.getClosingVelocity(cx1,cy1,cz1,vx1,vy1,vz1,px,py,pz,0,0,0);
        if(separatingVelocity >0 ){return;}
        float sep = separatingVelocity * restitution;
        float deltaV = sep - separatingVelocity;
        float inverseMass = obj1.getInverseMass();
        if(inverseMass <= 0){return;}
        float impulse = deltaV/inverseMass;
        PhysicsManager.getContactNormal(cx1,cy1,cz1,px,py,pz, buffer1);
        VectorTranslator.scaleVec3(buffer1,impulse);
        VectorTranslator.scaleVec3(buffer1,obj1.getInverseMass());
        obj1.applyImpulse(buffer1[0],buffer1[1],buffer1[2], px,py,pz,false);
    }

    public static boolean checkIfSpheresCollide(float c1x, float c1y, float c1z, float c1r, float c2x, float c2y, float c2z, float c2r){
        float x = c2x - c1x;
        float y = c2y - c1y;
        float z = c2z - c1z;
        return Math.sqrt(x*x+y*y+z*z) <= c1r + c2r;
    }
}
