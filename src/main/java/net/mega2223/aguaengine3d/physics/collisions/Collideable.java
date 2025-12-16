package net.mega2223.aguaengine3d.physics.collisions;

import net.mega2223.aguaengine3d.misc.annotations.Modified;
import net.mega2223.aguaengine3d.physics.PhysicsObject;

public interface Collideable extends PhysicsObject {
    /** Whether the point is 'inside the object'
     * @implNote (x,y,z) are absolute coordinates, in respect to the world, not the object.
     */
    boolean collides(float x, float y, float z);
    default boolean collides(float[] coord){
        return collides(coord[0],coord[1],coord[2]);
    }
    float maxRadius();
    /**
     * Gets the contact normal in the world's rotation system, with relation
     * from this object's perspective (not from point's perspective)
     * */// TODO o vetor normal é rotacionado igual o mundo, mas o normal é em relação a A e não a B (são inversos)
    default void getContactNormal(float[] point, @Modified float[] result){
        getCollision(point,result);
    }

    /**Solves the collision  between objects
     * @param contactNormalDest contact normal from this collideable's perspective
     * @return the contact depth in case the collision was resolved
     * */
    float solveCollision(Collideable c, @Modified float[] contactNormalDest);

    /**Returns the contact depth of the point and the contact normal of the collision
     * (from this object's point of view)
     * @param result resulting contact normal
     * */
    float getCollision(float[] point, float[] result);

    default float getRestitution(){
        return 0;
    }

    float x(); float y(); float z();
}
