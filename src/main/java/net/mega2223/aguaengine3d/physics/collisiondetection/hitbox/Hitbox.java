package net.mega2223.aguaengine3d.physics.collisiondetection.hitbox;

import net.mega2223.aguaengine3d.physics.collisiondetection.hierarchy.Collidiable;

public interface Hitbox extends Collidiable {
    //it's up to the superclass wether it returns 0 or a negative number for non colliding objects
    float getDepth(float cx, float cy, float cz);

    default void getTranslatedVector(float[] worldVec3, float[] dest){
        dest[0] = worldVec3[0] - getX();
        dest[1] = worldVec3[1] - getY();
        dest[2] = worldVec3[2] - getZ();
    }
}
