package net.mega2223.aguaengine3d.physics.collisiondetection;

public interface Collideable {
    boolean collides(float x, float y, float z);
    default boolean collides(float[] coord){
        return collides(coord[0],coord[1],coord[2]);
    }
    float getRadius();
    float[] getCenter();
}
