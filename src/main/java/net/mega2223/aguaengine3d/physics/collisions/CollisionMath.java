package net.mega2223.aguaengine3d.physics.collisions;

import net.mega2223.aguaengine3d.computing.BufferManager;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.misc.annotations.Modified;
import net.mega2223.aguaengine3d.physics.PhysicsObject;
import net.mega2223.aguaengine3d.physics.advanced.RigidBody;
import net.mega2223.aguaengine3d.physics.objects.collideable.FixedPlane;

public class CollisionMath {
    private CollisionMath(){}

    private static final float[][] buffers = new float[5][4];

    /**
     * Calculates closing velocity between two points,
     * less than zero if they are moving apart
     * */
    public static float closingVelocity(float[] posA, float[] velA, float[] posB, float[] velB){
        float[] bufferA = BufferManager.allocateVec4(), bufferB = BufferManager.allocateVec4();

        VectorTranslator.subtractFromVector(posA,posB, bufferA); //b[0] = pa - pb
        VectorTranslator.getFlipped(bufferA, bufferB); // b[1] = pb - pa
        VectorTranslator.normalize(bufferA);
        VectorTranslator.normalize(bufferB);
        float ret = VectorTranslator.dotProduct(velA, bufferB) + VectorTranslator.dotProduct(velB, bufferA);

        BufferManager.freeVec4(bufferA); BufferManager.freeVec4(bufferB);
        return ret;
    }

    public static float closingVelocity(float aX, float aY, float aZ, float avX, float avY, float avZ, float bX, float bY, float bZ, float bvX, float bvY, float bvZ){
        float[] posA = BufferManager.allocateVec4(); float[] velA = BufferManager.allocateVec4();
        float[] posB = BufferManager.allocateVec4(); float[] velB = BufferManager.allocateVec4();

        VectorTranslator.copy(aX, aY, aZ, posA);
        VectorTranslator.copy(avX, avY, avZ, velA);
        VectorTranslator.copy(bX, bY, bZ, posB);
        VectorTranslator.copy(bvX, bvY, bvZ, velB);

        float ret = closingVelocity(posA, velA, posB, velB);

        BufferManager.freeVec4(posA); BufferManager.freeVec4(velA);
        BufferManager.freeVec4(posB); BufferManager.freeVec4(velB);

        return ret;
    }

    public static float closingVelocity(PhysicsObject objA, PhysicsObject objB){
        return objA.getClosingVelocity(objB.x(),objB.y(),objB.z(),objB.vx(),objB.vy(),objB.vz());
    }

    /**
     * Inverse of the closing velocity
     * */
    public static float separatingVelocity(float[] posA, float[] velA, float[] posB, float[] velB){
        return - closingVelocity(posA, velA, posB, velB); // V_s
        // V_s = (vA - vB) . contactNormal
    }

    /**
     * Inverse of the closing velocity
     * */
    public static float separatingVelocity(float pxA, float pyA, float pzA, float vxA, float vyA, float vzA,
                                           float pxB, float pyB, float pzB, float vxB, float vyB, float vzB){
        return -closingVelocity(pxA, pyA, pzA, vxA, vyA, vzA, pxB, pyB, pzB, vxB, vyB, vzB);
    }

    public static float separatingVelocity(PhysicsObject objA, PhysicsObject objB){
        return - objA.getClosingVelocity(objB.x(),objB.y(),objB.z(),objB.vx(),objB.vy(),objB.vz());
    }

    /**
     * Gets the contact normal form object A's perspective
     * assuming A and B are points, the contact normal
     * as any other normal *always* has a magnitude of 1.
     * */
    public static void getContactNormal(float[] posA, float[] posB, @Modified float[] result) {
        VectorTranslator.subtractFromVector(posA,posB,result);
        VectorTranslator.normalize(result);
    }

    private static final float[][] solveCollisionBuffers = new float[5][4];
    // para evitar colisões com a função separatingVelocity

    /**
     * Solves a collision assuming both objects are particles,
     * does not correct the position, only the velocity
     * */
    public static void solveCollision(Collideable a, Collideable b, float restitution){
        float[] posA = solveCollisionBuffers[0], posB = solveCollisionBuffers[1],
                velA = solveCollisionBuffers[2], velB = solveCollisionBuffers[3];
        float[] contact = solveCollisionBuffers[4]; // from A's perspective
        a.getCoords(posA); b.getCoords(posB); a.getVelocity(velA); b.getVelocity(velB);
//        float sep = separatingVelocity(posA,velA,posB,velB);
        float sep = separatingVelocity(a,b);
        if(sep > 0){return;}

        a.getContactNormal(posB,contact);

        final float inverseSum = a.getInverseMass() + b.getInverseMass();
        if(inverseSum <= 0) {return;}

        final float nSep = - restitution * sep;
        final float deltaVelocity = nSep - sep;
        final float impulse = deltaVelocity / inverseSum;

        float[] impulsePerIMass = buffers[0];
        VectorTranslator.scaleVector(contact,impulse,impulsePerIMass);

        a.applyImpulse(impulsePerIMass[0],impulsePerIMass[1],impulsePerIMass[2]);
        VectorTranslator.flipVector(impulsePerIMass);
        b.applyImpulse(impulsePerIMass[0],impulsePerIMass[1],impulsePerIMass[2]);
    }

    /**
     * Solves a collision,
     * does not correct the position, only the velocity
     * */
    public static void solveCollision(Collideable a, Collideable b, float[] contactNormal, float restitution){
        float sep = separatingVelocity(a,b);
        if(sep > 0){return;}
        solveCollision(a,b,sep,contactNormal,restitution);
    }

    public static void solveCollision(Collideable a, Collideable b, float separatingVelocity, float[] contactNormal, float restitution){
        float[] posA = solveCollisionBuffers[0], posB = solveCollisionBuffers[1],
                velA = solveCollisionBuffers[2], velB = solveCollisionBuffers[3];
        float[] contact = solveCollisionBuffers[4]; // from A's perspective
        a.getCoords(posA); b.getCoords(posB); a.getVelocity(velA); b.getVelocity(velB);
//        float sep = separatingVelocity(posA,velA,posB,velB);

        VectorTranslator.copy(contactNormal,contact);

        final float inverseSum = a.getInverseMass() + b.getInverseMass();
        if(inverseSum <= 0) {return;}

        final float nSep = - restitution * separatingVelocity;
        final float deltaVelocity = nSep - separatingVelocity;
        final float impulse = deltaVelocity / inverseSum;

        float[] impulsePerIMass = buffers[0];
        VectorTranslator.scaleVector(contact,impulse,impulsePerIMass);

        a.applyImpulse(impulsePerIMass[0],impulsePerIMass[1],impulsePerIMass[2]);
        VectorTranslator.flipVector(impulsePerIMass);
        b.applyImpulse(impulsePerIMass[0],impulsePerIMass[1],impulsePerIMass[2]);
    }

    /**
     * Distances two objects that are colliding with one another
     * @param contactNormal contact normal from A's perspective
     * */
    public static void solveContact(PhysicsObject a, PhysicsObject b, float[] contactNormal, float contactDepth){
        if(contactDepth <= 0){return;}

        final float invMassSum = a.getInverseMass() + b.getInverseMass();
        //invMassSum *= contactDepth;

        VectorTranslator.getNormalized(contactNormal,buffers[0]);
        contactNormal = buffers[0];
        VectorTranslator.scaleVector(contactNormal,- contactDepth / invMassSum, buffers[1]);

        VectorTranslator.scaleVector(buffers[1], -a.getInverseMass(), buffers[2]);
        a.applyTranslation(buffers[2]);

        VectorTranslator.scaleVector(buffers[1],  b.getInverseMass(), buffers[2]);
        b.applyTranslation(buffers[2]);
    }

    /**
     * Distances two objects that are colliding with one another
     * @param contactNormal contact normal from A's perspective (in world coords)
     * */
    public static void solveContact(RigidBody a, RigidBody b, float[] contactPoint, float[] contactNormal, float contactDepth){//todo
        if(contactDepth <= 0){return;}
        final float invMassSum = a.getInverseMass() + b.getInverseMass();

        VectorTranslator.getNormalized(contactNormal,buffers[0]);
        contactNormal = buffers[0];
        VectorTranslator.scaleVector(contactNormal,- contactDepth / invMassSum, buffers[1]);

        VectorTranslator.scaleVector(buffers[1], -a.getInverseMass(), buffers[2]);
//        a.applyRotationalCorrection(buffers[2],contactPoint);
        a.applyTranslation(buffers[2]);

        VectorTranslator.scaleVector(buffers[1],  b.getInverseMass(), buffers[2]);
//        b.applyRotationalCorrection(buffers[2],contactPoint);
        a.applyTranslation(buffers[2]);
    }

    /**
     * Distances two objects that are colliding with one another
     * @param contactNormal contact normal from A's perspective (in world coords)
     * */
    public static void solveContact(RigidBody a, PhysicsObject b, float[] contactPoint, float[] contactNormal, float contactDepth){//todo olhisso
        if(contactDepth <= 0){return;}
        final float invMassSum = a.getInverseMass() + b.getInverseMass();

        VectorTranslator.getNormalized(contactNormal,buffers[0]);
        contactNormal = buffers[0];
        VectorTranslator.scaleVector(contactNormal,- contactDepth / invMassSum, buffers[1]);

        VectorTranslator.scaleVector(buffers[1], -a.getInverseMass(), buffers[2]);
        a.applyRotationalCorrection(buffers[2],contactPoint);

        VectorTranslator.scaleVector(buffers[1], b.getInverseMass(), buffers[2]);
        b.applyTranslation(buffers[2]);
    }

    public static void resolveContact(float[] pointA, float[] velA, float invMassA,
                                      float[] pointB, float[] velB, float invMassB,
                                      float[] contactNormal, float contactDepth){
        //TODO :3
    }
}
