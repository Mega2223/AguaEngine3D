package net.mega2223.aguaengine3d.physics;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.objects.PhysicsSystem;
import net.mega2223.aguaengine3d.physics.objects.RigidBodySystem;

import java.util.Arrays;

public class CollisionSolver {

    public static final float DEF_RESTITUTION = .5F;

    private CollisionSolver(){} //TODO deltaT variable

    private static final float[] bufferVec1 = new float[4];
    private static final float[] bufferVec2 = new float[4];
    private static final float[] bufferMatrix = new float[16];
    private static final float[] bufferCollRes = new float[6];

    // The below methods are the ones responsible for solving contact equations.
    // The template for collision resolution vectors is:
    // [0,1,2] = contact transformation, [3,4,5] = relative point to apply the transformation (0 if not RigidBody)

    public static void resolveConflict(float invMass, float pX, float pY, float pZ, float cnX, float cnY, float cnZ, final float confDepth,float tX, float tY, float tZ, float[] dest){
        //BASE METHOD FOR RESOLVING PARTICLE COLLISIONS
        //TODO not using local coords
        //also FIXME lmao
        if(confDepth <= 0){return;}
        if(invMass <= 0){return;}
        bufferVec1[0] = cnX;
        bufferVec1[1] = cnY;
        bufferVec1[2] = cnZ;
        VectorTranslator.scaleVec3(bufferVec1, -confDepth/invMass);
        VectorTranslator.scaleVec3(bufferVec1,invMass);
        VectorTranslator.flipVector(bufferVec1);
        dest[0] += bufferVec1[0]; dest[1] += bufferVec1[1]; dest[2] += bufferVec1[2];
    }

    public static void resolveConflict(float invMass, float pX, float pY, float pZ, float cnX, float cnY, float cnZ, final float depth, float tX, float tY, float tZ, float[] transposeRotationMatrix, float[] dest){
        //BASE METHOD FOR RESOLVING RIGIDBODY COLLISIONS
        //Todo: INNERTIA TENSORS
        if(depth <= 0||invMass<=0){return;}
        bufferVec1[0] = cnX; bufferVec1[1] = cnY; bufferVec1[2] = cnZ;
        float linearInertia = -depth / invMass; //TODO?: partial enfim
        VectorTranslator.scaleVec3(bufferVec1, linearInertia);
        VectorTranslator.scaleVec3(bufferVec1,invMass);
        VectorTranslator.flipVector(bufferVec1);
        dest[0] +=  bufferVec1[0]; dest[1] += bufferVec1[1]; dest[2] += bufferVec1[2];
        float angularInertia = 1;
        bufferVec1[0] = pX; bufferVec1[1] = pY; bufferVec1[2] = pZ;
        bufferVec1[0]-=tX; bufferVec1[1]-=tY; bufferVec1[2]-=tZ;
        MatrixTranslator.multiplyVec4Mat4(bufferVec1,transposeRotationMatrix);
        VectorTranslator.scaleVec3(bufferVec1,angularInertia);
        dest[3] += bufferVec1[0]; dest[4] += bufferVec1[1]; dest[5] += bufferVec1[2];
    }

    public static void resolveConflict(RigidBodySystem obj, float pX, float pY, float pZ, float cnX, float cnY, float cnZ, float depth, float[] dest){
        obj.getRotationMatrix(bufferMatrix);
        MatrixTranslator.getTransposeMatrix4(bufferMatrix);
        resolveConflict(obj.getInverseMass(), pX, pY, pZ, cnX, cnY, cnZ, depth, obj.getCoordX(), obj.getCoordY(), obj.getCoordZ(), bufferMatrix, dest);
    }

    public static void resolveConflict(PhysicsSystem obj, float pX, float pY, float pZ, float cnX, float cnY, float cnZ, final float depth) {
        Arrays.fill(bufferCollRes,0);
        resolveConflict(obj.getInverseMass(),pX,pY,pZ,cnX,cnY,cnZ,obj.getCoordX(),obj.getCoordY(),obj.getCoordZ(),depth, bufferCollRes);
        obj.applyTransformation(bufferCollRes[0], bufferCollRes[1], bufferCollRes[2]);
    }

    public static void resolveConflict(PhysicsSystem obj, float pX, float pY, float pZ, float depth){
        Arrays.fill(bufferCollRes,0);
        float vx1 = obj.getVelocityX(), vy1 = obj.getVelocityY(), vz1 = obj.getVelocityZ(),
                cx1 = obj.getCoordX(), cy1 = obj.getCoordY(), cz1 = obj.getCoordZ();
        PhysicsManager.getContactNormal(cx1,cy1,cz1,pX,pY,pZ, bufferVec1);
        resolveConflict(obj.getInverseMass(),pX,pY,pZ, bufferVec1[0], bufferVec1[1], bufferVec1[2],obj.getCoordX(),obj.getCoordY(),obj.getCoordZ(),depth, bufferCollRes);
        obj.applyTransformation(bufferCollRes[0], bufferCollRes[1], bufferCollRes[2]);
    }

    public static void resolveConflict(RigidBodySystem obj, float pX, float pY, float pZ, float cnX, float cnY, float cnZ, final float depth) {
        Arrays.fill(bufferCollRes,0);
        obj.getRotationMatrix(bufferMatrix);
        resolveConflict(obj.getInverseMass(), pX, pY, pZ, cnX, cnY, cnZ, depth, obj.getCoordX(), obj.getCoordY(), obj.getCoordZ(), bufferMatrix, bufferCollRes);
        obj.applyRotationalTransformation(bufferCollRes[3], bufferCollRes[4], bufferCollRes[5], bufferCollRes[0], bufferCollRes[1], bufferCollRes[2]);
    }

    //COLLISION RESOLVING METHODS

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
        PhysicsManager.getContactNormal(cx1,cy1,cz1,px,py,pz, bufferVec1);
        VectorTranslator.scaleVec3(bufferVec1,impulse);
        VectorTranslator.scaleVec3(bufferVec1,obj1.getInverseMass());
        obj1.applyImpulse(bufferVec1);
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
        PhysicsManager.getContactNormal(cx1,cy1,cz1,cx2,cy2,cz2, bufferVec1);
        VectorTranslator.scaleVec3(bufferVec1,impulse);
        System.arraycopy(bufferVec1,0, bufferVec2,0,3);
        VectorTranslator.scaleVec3(bufferVec2,obj1.getInverseMass());
        obj1.applyImpulse(bufferVec2);
        VectorTranslator.flipVector(bufferVec1);
        System.arraycopy(bufferVec1,0, bufferVec2,0,3);
        VectorTranslator.scaleVec3(bufferVec2,obj2.getInverseMass());
        obj2.applyImpulse(bufferVec2);
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
        PhysicsManager.getContactNormal(cx1,cy1,cz1,cx2,cy2,cz2, bufferVec1);
        VectorTranslator.scaleVec3(bufferVec1,impulse);
        System.arraycopy(bufferVec1,0, bufferVec2,0,3);
        VectorTranslator.scaleVec3(bufferVec2,obj1.getInverseMass());
        obj1.applyImpulse(bufferVec2[0], bufferVec2[1], bufferVec2[2],cx1,cy1,cz1);
        VectorTranslator.flipVector(bufferVec1);
        System.arraycopy(bufferVec1,0, bufferVec2,0,3);
        VectorTranslator.scaleVec3(bufferVec2,obj2.getInverseMass());
        obj2.applyImpulse(bufferVec2[0], bufferVec2[1], bufferVec2[2],cx2,cy2,cz2);
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
        PhysicsManager.getContactNormal(cx1,cy1,cz1,px,py,pz, bufferVec1);
        VectorTranslator.scaleVec3(bufferVec1,impulse);
        VectorTranslator.scaleVec3(bufferVec1,obj.getInverseMass());
        obj.applyImpulse(bufferVec1[0], bufferVec1[1], bufferVec1[2], px,py,pz);
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
        bufferVec1[0] = cnX;
        bufferVec1[1] = cnY;
        bufferVec1[2] = cnZ;
        VectorTranslator.scaleVec3(bufferVec1,impulse);
        VectorTranslator.scaleVec3(bufferVec1,obj.getInverseMass());
        obj.applyImpulse(bufferVec1[0], bufferVec1[1], bufferVec1[2],px,py,pz);
    }

    public static boolean checkIfSpheresCollide(float c1x, float c1y, float c1z, float c1r, float c2x, float c2y, float c2z, float c2r){
        float x = c2x - c1x;
        float y = c2y - c1y;
        float z = c2z - c1z;
        return Math.sqrt(x*x+y*y+z*z) <= c1r + c2r;
    }
}
