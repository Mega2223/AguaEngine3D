package net.mega2223.aguaengine3d.physics.collisions;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.misc.annotations.Modified;

public class CollisionMath {
    private CollisionMath(){};
    private static final float[][] buffers = new float[3][4];

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
}
