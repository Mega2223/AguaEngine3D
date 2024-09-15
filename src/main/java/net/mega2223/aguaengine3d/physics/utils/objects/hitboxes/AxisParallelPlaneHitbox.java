package net.mega2223.aguaengine3d.physics.utils.objects.hitboxes;

import net.mega2223.aguaengine3d.physics.collisiondetection.hitbox.Hitbox;

public class AxisParallelPlaneHitbox extends Hitbox {
    final protected float coordY;
    public AxisParallelPlaneHitbox(float cy) {
        super();
        coordY = cy;
    }

    @Override
    public boolean collides(float x, float y, float z) {
        return y<=coordY;
    }

    @Override
    public float getEffectiveInteractionRadius() {
        return Float.POSITIVE_INFINITY;
    }

    @Override
    public float getX() {
        return 0;
    }

    @Override
    public float getY() {
        return coordY;
    }

    @Override
    public float getZ() {
        return 0;
    }

    @Override
    public void doLogic(float time) {

    }

    @Override
    public void getAxisDepthRelative(float[] result, float locX, float locY, float locZ) {
        result[0] = 0;
        result[1] = Math.max(0,locY-this.coordY);
        result[2] = 0;
    }

    @Override
    public float getDepth(float locX, float locY, float locZ) {
        return Math.max(-locY+this.coordY,0);
    }

    @Override
    protected void resolveCollision(Hitbox hitbox) {

    }

    @Override
    public void getContactNormal(float x, float y, float z, float[] result) {
        result[0] = 0;
        result[1] = getDepth(x,y,z);
        result[2] = 0;
    }
}
