package net.mega2223.aguaengine3d.physics.collisiondetection.hierarchy;

import net.mega2223.aguaengine3d.physics.collisiondetection.hitbox.Hitbox;

public interface Collidiable {
    boolean collides(float x, float y, float z);
    default boolean collides(float[] coord){
        return collides(coord[0],coord[1],coord[2]);
    }
    float getEffectiveInteractionRadius();

    float getX(); float getY(); float getZ();

    default float[] getCenter(){return new float[]{getX(),getY(),getZ(),0};}
    void doLogic(float time);
    void resolveForHitbox(Hitbox hitbox);
}
