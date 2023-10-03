package net.mega2223.aguaengine3d.physics.utils.objects.hitboxes;

import net.mega2223.aguaengine3d.physics.collisiondetection.hitbox.Hitbox;
import net.mega2223.aguaengine3d.physics.objects.PhysicsSystem;

public class PlaneHitbox extends Hitbox {
    //todo
    final float coordX, coordY, coordZ, axisX, axisY, axisZ;

    protected PlaneHitbox(PhysicsSystem linkedSystem, float cx, float cy, float cz, float ax, float ay, float az) {
        super(linkedSystem);
        coordX = cx;
        coordY = cy;
        coordZ = cz;
        axisX = ax;
        axisY = ay;
        axisZ = az;
    }

    @Override
    public boolean collides(float x, float y, float z) {
        return false;
    }

    @Override
    public float getEffectiveInteractionRadius() {
        return 0;
    }

    @Override
    public float getX() {
        return 0;
    }

    @Override
    public float getY() {
        return 0;
    }

    @Override
    public float getZ() {
        return 0;
    }

    @Override
    public void updateCoords() {

    }

    @Override
    public void getAxisDepthRelative(float[] dest, float locX, float locY, float locZ) {

    }

    @Override
    public float getDepth(float locX, float locY, float locZ) {
        return 0;
    }

    @Override
    protected void update() {

    }

    @Override
    protected void resolveCollision(float localX, float localY, float localZ, float[] resultingForceDest) {

    }
}
