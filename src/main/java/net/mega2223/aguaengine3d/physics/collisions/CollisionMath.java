package net.mega2223.aguaengine3d.physics.collisions;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.misc.annotations.Modified;
import net.mega2223.aguaengine3d.physics.PhysicsObject;

public class CollisionMath {
    private CollisionMath(){}

    private static final float[][] buffers = new float[5][4];

    /**
     * Calculates closing velocity between two objects,
     * less than zero if they are moving apart
     * */
    public static float closingVelocity(float[] posA, float[] velA, float[] posB, float[] velB){
        VectorTranslator.subtractFromVector(posA,posB,buffers[0]); //b[0] = pa - pb
        VectorTranslator.getFlipped(buffers[0], buffers[1]); // b[1] = pb - pa
        VectorTranslator.normalize(buffers[0]);
        VectorTranslator.normalize(buffers[1]); //todo isso tá certo?
        return VectorTranslator.dotProduct(velA, buffers[1]) +
               VectorTranslator.dotProduct(velB, buffers[0]);
    }

    /**
     * Inverse of the closing velocity
     * */
    public static float separatingVelocity(float[] posA, float[] velA, float[] posB, float[] velB){
        return - closingVelocity(posA, velA, posB, velB); // V_s
        // V_s = (vA - vB) . contactNormal
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

    /**
     * Solves a collision assuming both objects are particles,
     * does not correct the position, only the velocity
     * */
    public static void solveCollision(PhysicsObject a, PhysicsObject b, float restitution){
        float[] posA = buffers[0], posB = buffers[1], velA = buffers[2], velB = buffers[3];
        float[] contact = buffers[4]; // from A's perspective
        a.getCoords(posA); b.getCoords(posB); a.getVelocity(velA); b.getVelocity(velB);

        float sep = separatingVelocity(posA,velA,posB,velB);
        if(sep > 0){return;}

        getContactNormal(posA,posB,contact);

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
}
