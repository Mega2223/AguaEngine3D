package net.mega2223.aguaengine3d.physics.collisiondetection.hitbox;

import net.mega2223.aguaengine3d.physics.CollisionResolver;
import net.mega2223.aguaengine3d.physics.objects.PhysicsSystem;

import java.util.Arrays;

public class RectHitbox extends Hitbox {

    final float minX, minY, minZ, maxX, maxY, maxZ;
    float radius;
    PhysicsSystem linkedSystem;

    //TODO hitboxes should be rotateable

    public RectHitbox(PhysicsSystem linkedSystem, float bx, float by, float bz, float ex, float ey, float ez){
        minX = Math.min(bx,ex);
        minY = Math.min(by,ey);
        minZ = Math.min(bz,ez);
        maxX = Math.max(bx,ex);
        maxY = Math.max(by,ey);
        maxZ = Math.max(bz,ez);

        coords[0] = (ex-bx)*.5F + bx;
        coords[1] = (ey-by)*.5F + by;
        coords[2] = (ez-bz)*.5F + bz;
        radius = computeFarthestPointFromCenter();
    }

    @Override
    public void getDepth(float[] dest, float locX, float locY, float locZ) {
        dest[0] = locX < 0 ? locX - minX : locX + maxX;
        dest[1] = locY < 0 ? locY - minY : locY + maxY;
        dest[2] = locZ < 0 ? locZ - minZ : locZ + maxZ;
    }

    @Override
    public PhysicsSystem getLinkedSystem() {
        return linkedSystem;
    }

    @Override
    public void update() {

    }

    @Override
    void resolveCollision(float wX, float wY, float wZ, float[] resultingForceDest) {
        getDepth(resultingForceDest,wX-getX(),wX-getY(),wZ-getZ());
        CollisionResolver.resolveCollision(linkedSystem,wX,wY,wZ,CollisionResolver.DEF_RESTITUTION);
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
    public void updateCoords() {

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
