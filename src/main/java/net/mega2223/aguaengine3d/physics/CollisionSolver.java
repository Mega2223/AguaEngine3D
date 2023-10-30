package net.mega2223.aguaengine3d.physics;

import net.mega2223.aguaengine3d.Gaem3D;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.objects.PhysicsSystem;
import net.mega2223.aguaengine3d.physics.objects.RigidBodySystem;

public class CollisionSolver {

    public static final float DEF_RESTITUTION = .5F;

    private CollisionSolver(){} //TODO deltaT variable

    private static final float[] buffer1 = new float[4];
    private static final float[] buffer2 = new float[4];

    public static void resolveConflict(PhysicsSystem obj1, float pX, float pY, float pZ, final float confDepth){
        if(confDepth <= 0){return;}
        float invMass = obj1.getInverseMass();
        if(invMass <= 0){return;}
        float vx1 = obj1.getVelocityX(), vy1 = obj1.getVelocityY(), vz1 = obj1.getVelocityZ(),
                cx1 = obj1.getCoordX(), cy1 = obj1.getCoordY(), cz1 = obj1.getCoordZ();

        PhysicsManager.getContactNormal(cx1,cy1,cz1,pX,pY,pZ, buffer1);
        VectorTranslator.scaleVec3(buffer1, -confDepth/invMass);
        VectorTranslator.scaleVec3(buffer1,obj1.getInverseMass());
        obj1.applyTransformation(buffer1);
    }

    public static void resolveConflict(PhysicsSystem obj1, float pX, float pY, float pZ, float cnX, float cnY, float cnZ, final float confDepth){
        if(confDepth <= 0){return;}
        float invMass = obj1.getInverseMass();
        if(invMass <= 0){return;}
        buffer1[0] = cnX;buffer1[1] = cnY;buffer1[2] = cnZ;
        VectorTranslator.scaleVec3(buffer1, -confDepth/invMass);
        VectorTranslator.scaleVec3(buffer1,obj1.getInverseMass());
        VectorTranslator.flipVector(buffer1);
        obj1.applyTransformation(buffer1);
    }

    public static void resolveConflict(RigidBodySystem obj1, float pX, float pY, float pZ, float cnX, float cnY, float cnZ, final float confDepth){
        if(confDepth <= 0){return;}
        float invMass = obj1.getInverseMass();
        if(invMass <= 0){return;}
        buffer1[0] = cnX;buffer1[1] = cnY;buffer1[2] = cnZ;
        float linearInertia = -confDepth / invMass;
        VectorTranslator.scaleVec3(buffer1, linearInertia*0.75F);
        VectorTranslator.scaleVec3(buffer1,obj1.getInverseMass());
        VectorTranslator.flipVector(buffer1);
        obj1.applyTransformation(buffer1);
        VectorTranslator.getCrossProduct(buffer2,pX,pY,pZ,cnX,cnY,cnZ);
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

    public static void resolveConflict(RigidBodySystem obj1, float pX, float pY, float pZ, float cnX, float cnY, float cnZ, final float confDepth,float[] dest){
        if(confDepth <= 0){return;}
        final float invMass = obj1.getInverseMass();
        if(invMass <= 0){return;}
        buffer1[0] = cnX;buffer1[1] = cnY;buffer1[2] = cnZ;
        float linearInertia = -confDepth / invMass * .95F;
        VectorTranslator.scaleVec3(buffer1, linearInertia);
        VectorTranslator.scaleVec3(buffer1,invMass);
        VectorTranslator.flipVector(buffer1);
        dest[0] +=  buffer1[0]; dest[1] += buffer1[1]; dest[2] += buffer2[2];
        float angularInertia = 1F-linearInertia;
        buffer1[0] = pX; buffer1[1] = pY; buffer1[2] = pZ;
        obj1.toLocalCoords(buffer1);
        VectorTranslator.scaleVec3(buffer1,angularInertia);
        dest[3] += buffer1[0]; dest[4] += buffer1[1]; dest[5] += buffer1[2];
    }

    public static void resolveConflict(float invMass, float cnX, float cnY, float cnZ, final float depth, float[] transformationMatrix, float[] dest){
        //TODO
    }

    public static void resolveConflict(float invMass, float pX, float pY, float pZ, float cnX, float cnY, float cnZ, final float depth, float crX, float crY, float crZ, float[] rotationMatrix, float[] dest){
        if(depth <= 0){return;}
        if(invMass <= 0){return;}
        buffer1[0] = cnX;buffer1[1] = cnY;buffer1[2] = cnZ;
        float linearInertia = -depth / invMass * .95F;
        VectorTranslator.scaleVec3(buffer1, linearInertia);
        VectorTranslator.scaleVec3(buffer1,invMass);
        VectorTranslator.flipVector(buffer1);
        dest[0] +=  buffer1[0]; dest[1] += buffer1[1]; dest[2] += buffer2[2];
        float angularInertia = 1F-linearInertia;
        buffer1[0] = pX; buffer1[1] = pY;

        for (int i = 0; i < 3; i++) {buffer1[i]-=coordinateBuffer[i];}
        //the inverse of a rotation matrix is it's transpose
        MatrixTranslator.getTransposeMatrix4(rotationMatrix,bufferM4);
        MatrixTranslator.multiplyVec4Mat4(localCoords,bufferM4);

        VectorTranslator.scaleVec3(buffer1,angularInertia);
        dest[3] += buffer1[0]; dest[4] += buffer1[1]; dest[5] += buffer1[2];
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
        obj1.applyImpulse(buffer2[0],buffer2[1], buffer2[2],cx1,cy1,cz1);
        VectorTranslator.flipVector(buffer1);
        System.arraycopy(buffer1,0,buffer2,0,3);
        VectorTranslator.scaleVec3(buffer2,obj2.getInverseMass());
        obj2.applyImpulse(buffer2[0],buffer2[1], buffer2[2],cx2,cy2,cz2);
    }

    public static void resolveCollision(RigidBodySystem obj, float px, float py, float pz, final float restitution){
        float vx1 = obj.getVelocityX(), vy1 = obj.getVelocityY(), vz1 = obj.getVelocityZ(),
                cx1 = obj.getCoordX(), cy1 = obj.getCoordY(), cz1 = obj.getCoordZ();

        float separatingVelocity = - PhysicsManager.getClosingVelocity(cx1,cy1,cz1,vx1,vy1,vz1,px,py,pz,0,0,0);
        if(separatingVelocity >0 ){return;}
        float sep = separatingVelocity * restitution;
        float deltaV = sep - separatingVelocity;
        float inverseMass = obj.getInverseMass();
        if(inverseMass <= 0){return;}
        float impulse = deltaV/inverseMass;
        PhysicsManager.getContactNormal(cx1,cy1,cz1,px,py,pz, buffer1);
        VectorTranslator.scaleVec3(buffer1,impulse);
        VectorTranslator.scaleVec3(buffer1,obj.getInverseMass());
        obj.applyImpulse(buffer1[0],buffer1[1],buffer1[2], px,py,pz);
    }

    public static void resolveCollision(RigidBodySystem obj, float px, float py, float pz, float cnX, float cnY, float cnZ,final float restitution){
        float vx1 = obj.getVelocityX(), vy1 = obj.getVelocityY(), vz1 = obj.getVelocityZ();
        resolveCollision(obj,px,py,pz,vx1,vy1,vz1,cnX,cnY,cnZ,restitution);
    }
    public static void resolveCollision(RigidBodySystem obj, float px, float py, float pz, float vx1, float vy1, float vz1, float cnX, float cnY, float cnZ,final float restitution) {
        resolveCollision(obj, px, py, pz, vx1, vy1, vz1,0,0,0, cnX, cnY, cnZ, restitution);
    }

    public static void resolveCollision(RigidBodySystem obj, float px, float py, float pz, float vx1, float vy1, float vz1, float vx2, float vy2, float vz2, float cnX, float cnY, float cnZ,final float restitution){
        float cx1 = obj.getCoordX(), cy1 = obj.getCoordY(), cz1 = obj.getCoordZ();
        float separatingVelocity = - PhysicsManager.getClosingVelocity(cx1,cy1,cz1,vx1,vy1,vz1,px,py,pz,vx2,vy2,vz2);
        if(separatingVelocity > 0){return;}
        float sep = separatingVelocity * restitution;
        float deltaV = sep - separatingVelocity;
        float inverseMass = obj.getInverseMass();
        if(inverseMass <= 0){return;}
        float impulse = deltaV/inverseMass;
        buffer1[0] = cnX;buffer1[1] = cnY;buffer1[2] = cnZ;
        VectorTranslator.scaleVec3(buffer1,impulse);
        VectorTranslator.scaleVec3(buffer1,obj.getInverseMass());
        obj.applyImpulse(buffer1[0],buffer1[1],buffer1[2],px,py,pz);
    }

    public static boolean checkIfSpheresCollide(float c1x, float c1y, float c1z, float c1r, float c2x, float c2y, float c2z, float c2r){
        float x = c2x - c1x;
        float y = c2y - c1y;
        float z = c2z - c1z;
        return Math.sqrt(x*x+y*y+z*z) <= c1r + c2r;
    }
}
