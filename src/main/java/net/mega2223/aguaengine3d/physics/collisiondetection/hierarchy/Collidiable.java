package net.mega2223.aguaengine3d.physics.collisiondetection.hierarchy;

public interface Collidiable {
    boolean collides(float x, float y, float z);
    default boolean collides(float[] coord){
        return collides(coord[0],coord[1],coord[2]);
    }
    float getEffectiveInteractionRadius();

    float getX(); float getY(); float getZ();

    default float[] getCenter(){return new float[]{getX(),getY(),getZ()};}
    void updateCoords();
}
