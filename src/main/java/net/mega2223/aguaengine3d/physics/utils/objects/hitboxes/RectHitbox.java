package net.mega2223.aguaengine3d.physics.utils.objects.hitboxes;

import net.mega2223.aguaengine3d.physics.CollisionResolver;
import net.mega2223.aguaengine3d.physics.collisiondetection.hitbox.Hitbox;
import net.mega2223.aguaengine3d.physics.objects.PhysicsSystem;

import java.util.Arrays;

public class RectHitbox extends Hitbox {

    final float minX, minY, minZ, maxX, maxY, maxZ;
    float radius;

    public RectHitbox(PhysicsSystem linkedSystem, float bx, float by, float bz, float ex, float ey, float ez){
        super(linkedSystem);
        linkedSystem.bindHitbox(this);
        minX = Math.min(bx,ex);
        minY = Math.min(by,ey);
        minZ = Math.min(bz,ez);
        maxX = Math.max(bx,ex);
        maxY = Math.max(by,ey);
        maxZ = Math.max(bz,ez);
        radius = computeFarthestPointFromCenter();
    }

    @Override
    public void getAxisDepthRelative(float[] dest, float locX, float locY, float locZ) {
        dest[0] = locX < 0 ? Math.max(0,locX - minX) : Math.min(locX - maxX,0);
        dest[1] = locY < 0 ? Math.max(0,locY - minY) : Math.min(locY - maxY,0);
        dest[2] = locZ < 0 ? Math.max(0,locZ - minZ) : Math.min(locZ - maxZ,0);
    }

    @Override
    public float getDepth(float locX, float locY, float locZ) {
        if (collides(locX,locY,locZ)){
            float deX = locX < 0 ? Math.max(0,locX - minX) : Math.min(locX - maxX,0);
            float deY = locY < 0 ? Math.max(0,locY - minY) : Math.min(locY - maxY,0);
            float deZ = locZ < 0 ? Math.max(0,locZ - minZ) : Math.min(locZ - maxZ,0);
            return Math.abs(Math.max(Math.max(deX,deY),deZ));
        }
        return 0;
    }

    @Override
    public void doLogic(float time) {

    }

    @Override
    protected void resolveCollision(Hitbox hitbox) {

    }

    @Override
    public float getX() {
        return linkedSystem.getCoordX();
    }

    @Override
    public float getY() {
        return linkedSystem.getCoordY();
    }

    @Override
    public float getZ() {
        return linkedSystem.getCoordZ();
    }

    @Override
    public boolean collides(float x, float y, float z) {
        return x >= minX && x <= maxX &&
                y >= minY && y <= maxY &&
                z >= minZ && z <= maxZ;
    }

    @Override
    public float getEffectiveInteractionRadius() {
        return radius;
    }

    private float computeFarthestPointFromCenter(){
        //todo honestly i'm 99% sure there's a better approach
        return (float) Math.max(
                Math.sqrt(maxX*maxX+maxY*maxY+maxZ*maxZ),
                Math.sqrt(minX*minX+minY*minY+minZ*minZ)
        );
    }

    @Override
    public String toString() {
        return "RectHitbox{" +
                "coords=" + Arrays.toString(linkedSystem.getCoords()) +
                ", corners="  + minX + ", " + minY + ", " + minZ + " : " + maxX + ", " + maxY + ", " + maxZ +
                ", radius=" + radius +
                '}';
    }
}
