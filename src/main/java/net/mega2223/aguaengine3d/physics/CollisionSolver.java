package net.mega2223.aguaengine3d.physics;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.objects.PhysicsSystem;
import net.mega2223.aguaengine3d.physics.objects.RigidBodySystem;

import java.util.Arrays;

public class CollisionSolver {

    public static final float DEF_RESTITUTION = .5F;

    private CollisionSolver(){}

    private static final float[] bufferVec1 = new float[4];
    private static final float[] bufferVec2 = new float[4];

    private static final float[] bufferMatrix4 = new float[16];
    private static final float[] bufferMatrix3 = new float[9];
    private static final float[] bufferCollRes = new float[6];

    // The below methods are the ones responsible for solving contact equations.
    // The template for collision the resolution vector is:
    // [0,1,2] = contact transformation, [3,4,5] = contact rotation

    public static void resolveConflict(float totalInvMass, float localInvMass, float pX, float pY, float pZ, float cnX, float cnY, float cnZ, final float confDepth,float tX, float tY, float tZ, float[] result){
        //BASE METHOD FOR RESOLVING PARTICLE COLLISIONS
        if(confDepth <= 0){return;}
        if(totalInvMass <= 0){return;}
        bufferVec1[0] = cnX; bufferVec1[1] = cnY; bufferVec1[2] = cnZ;
        VectorTranslator.scaleVector(bufferVec1, -confDepth/totalInvMass);
        VectorTranslator.scaleVector(bufferVec1,totalInvMass);
        VectorTranslator.flipVector(bufferVec1);
        result[0] += bufferVec1[0]*localInvMass; result[1] += bufferVec1[1]*localInvMass; result[2] += bufferVec1[2]*localInvMass;
    }
    // transformations > position
    public static void resolveConflict(float totalInvMass, float localInvMass, float[] localInverseInertiaTensor, float pX, float pY, float pZ, float cnX, float cnY, float cnZ, final float depth, float tX, float tY, float tZ, float[] rotationMatrix, float[] result){
        //BASE METHOD FOR RESOLVING RIGIDBODY COLLISIONS

        //do not ask me how the code below works
        if(depth <= 0||totalInvMass<=0){return;}
        final float linearInertia = totalInvMass;
        bufferVec2[0] = pX + tX; bufferVec2[1] = pY + tY; bufferVec2[2] = pZ + tZ;
        bufferVec1[0] = cnX; bufferVec1[1] = cnY; bufferVec1[2] = cnZ;
        VectorTranslator.getCrossProduct(bufferVec2,bufferVec1);
        MatrixTranslator.multiplyVec3Mat3(bufferVec2,localInverseInertiaTensor);
        //bufferVec2 now stores the angular inertia in worldspace coords
        bufferVec1[0] = cnX; bufferVec1[1] = cnY; bufferVec1[2] = cnZ;
        VectorTranslator.getCrossProduct(bufferVec1,bufferVec2);
        VectorTranslator.flipVector(bufferVec1);
        bufferVec2[0] = cnX; bufferVec2[1] = cnY; bufferVec2[2] = cnZ;
        float angularInertia = VectorTranslator.getDotProduct(bufferVec1,bufferVec2);
        float totalInertia = angularInertia + linearInertia;
        float linearTransform = (depth * linearInertia)/totalInertia;
        float angularTransform = (depth * angularInertia)/totalInertia;
        VectorTranslator.scaleVector(bufferVec2,linearTransform);
        result[0] += bufferVec2[0]; result[1] += bufferVec2[1]; result[2] += bufferVec2[2];
        bufferVec2[0] = pX + tX; bufferVec2[1] = pY + tY; bufferVec2[2] = pZ + tZ;
        //VectorTranslator.getCrossProduct();
        System.out.println(angularInertia);
    }

    public static void resolveConflict(RigidBodySystem obj, float totalInvMass, float pX, float pY, float pZ, float cnX, float cnY, float cnZ, float depth, float[] result){
        obj.getRotationMatrix(bufferMatrix4);
        obj.getInverseInertiaTensor(bufferMatrix3);
        resolveConflict(totalInvMass, obj.getInverseMass(), bufferMatrix3, pX, pY, pZ, cnX, cnY, cnZ, depth, obj.getCoordX(), obj.getCoordY(), obj.getCoordZ(), bufferMatrix4, result);
    }

    public static void resolveConflict(PhysicsSystem obj, float totalInvMass,float pX, float pY, float pZ, float cnX, float cnY, float cnZ, final float depth) {
        Arrays.fill(bufferCollRes,0);
        resolveConflict(totalInvMass, obj.getInverseMass(),pX,pY,pZ,cnX,cnY,cnZ,obj.getCoordX(),obj.getCoordY(),obj.getCoordZ(),depth, bufferCollRes);
        obj.applyTransformation(bufferCollRes[0], bufferCollRes[1], bufferCollRes[2]);
    }

    public static void resolveConflict(PhysicsSystem obj, float totalInvMass, float pX, float pY, float pZ, float depth){
        Arrays.fill(bufferCollRes,0);
        float cx1 = obj.getCoordX(), cy1 = obj.getCoordY(), cz1 = obj.getCoordZ();
        PhysicsManager.getContactNormal(cx1,cy1,cz1,pX,pY,pZ, bufferVec1);
        resolveConflict(totalInvMass, obj.getInverseMass(),pX,pY,pZ, bufferVec1[0], bufferVec1[1], bufferVec1[2],obj.getCoordX(),obj.getCoordY(),obj.getCoordZ(),depth, bufferCollRes);
        obj.applyTransformation(bufferCollRes[0], bufferCollRes[1], bufferCollRes[2]);
    }

    public static void resolveConflict(PhysicsSystem obj1, PhysicsSystem obj2, float pX, float pY, float pZ, float cnX, float cnY, float cnZ, final float depth){
        final float totalInvMass = obj1.getInverseMass() + obj2.getInverseMass();
        Arrays.fill(bufferCollRes,0);
        float cx = obj1.getCoordX(), cy = obj1.getCoordY(), cz = obj1.getCoordZ();
        PhysicsManager.getContactNormal(cx,cy,cz,pX,pY,pZ, bufferVec1);
        resolveConflict(totalInvMass, obj1.getInverseMass(),pX,pY,pZ, bufferVec1[0], bufferVec1[1], bufferVec1[2],obj1.getCoordX(),obj1.getCoordY(),obj1.getCoordZ(),depth, bufferCollRes);
        obj1.applyTransformation(bufferCollRes[0], bufferCollRes[1], bufferCollRes[2]);

        Arrays.fill(bufferCollRes,0);
        cx = obj2.getCoordX(); cy = obj2.getCoordY(); cz = obj2.getCoordZ();
        PhysicsManager.getContactNormal(cx,cy,cz,pX,pY,pZ, bufferVec1);
        resolveConflict(totalInvMass, obj2.getInverseMass(),pX,pY,pZ, bufferVec1[0], bufferVec1[1], bufferVec1[2],obj2.getCoordX(),obj2.getCoordY(),obj2.getCoordZ(),depth, bufferCollRes);
        obj2.applyTransformation(bufferCollRes[0], bufferCollRes[1], bufferCollRes[2]);
    }

    public static void resolveConflict(RigidBodySystem obj, float totalInvMass, float pX, float pY, float pZ, float cnX, float cnY, float cnZ, final float depth) {
        Arrays.fill(bufferCollRes,0);
        obj.getRotationMatrix(bufferMatrix4);
        obj.getInverseInertiaTensor(bufferMatrix3);
        resolveConflict(totalInvMass, obj.getInverseMass(), bufferMatrix3, pX, pY, pZ, cnX, cnY, cnZ, depth, obj.getCoordX(), obj.getCoordY(), obj.getCoordZ(), bufferMatrix4, bufferCollRes);
        obj.applyTransformation(bufferCollRes[0], bufferCollRes[1], bufferCollRes[2]);
        obj.applyOrientationTransform(bufferCollRes[3], bufferCollRes[4], bufferCollRes[5]);
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
        VectorTranslator.scaleVector(bufferVec1,impulse);
        VectorTranslator.scaleVector(bufferVec1,obj1.getInverseMass());
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
        VectorTranslator.scaleVector(bufferVec1,impulse);
        System.arraycopy(bufferVec1,0, bufferVec2,0,3);
        VectorTranslator.scaleVector(bufferVec2,obj1.getInverseMass());
        obj1.applyImpulse(bufferVec2);
        VectorTranslator.flipVector(bufferVec1);
        System.arraycopy(bufferVec1,0, bufferVec2,0,3);
        VectorTranslator.scaleVector(bufferVec2,obj2.getInverseMass());
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
        VectorTranslator.scaleVector(bufferVec1,impulse);
        System.arraycopy(bufferVec1,0, bufferVec2,0,3);
        VectorTranslator.scaleVector(bufferVec2,obj1.getInverseMass());
        obj1.applyImpulse(bufferVec2[0], bufferVec2[1], bufferVec2[2],cx1,cy1,cz1);
        VectorTranslator.flipVector(bufferVec1);
        System.arraycopy(bufferVec1,0, bufferVec2,0,3);
        VectorTranslator.scaleVector(bufferVec2,obj2.getInverseMass());
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
        VectorTranslator.scaleVector(bufferVec1,impulse);
        VectorTranslator.scaleVector(bufferVec1,obj.getInverseMass());
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
        VectorTranslator.scaleVector(bufferVec1,impulse);
        VectorTranslator.scaleVector(bufferVec1,obj.getInverseMass());
        obj.applyImpulse(bufferVec1[0], bufferVec1[1], bufferVec1[2],px,py,pz);
    }

    public static boolean checkIfSpheresCollide(float c1x, float c1y, float c1z, float c1r, float c2x, float c2y, float c2z, float c2r){
        float x = c2x - c1x;
        float y = c2y - c1y;
        float z = c2z - c1z;
        return Math.sqrt(x*x+y*y+z*z) <= c1r + c2r;
    }
}
