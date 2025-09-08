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
     * Gets the contact normal with same orientation as the world
     * from this object's perspective (not from coord's perspective)
     * */
    void getContactNormal(float[] coord, @Modified float[] result);

    float getCollision(Collideable c, @Modified float[] contactNormalDest);

    float x(); float y(); float z();
}
