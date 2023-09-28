package net.mega2223.aguaengine3d.physics.collisiondetection.hitbox;

import java.util.Arrays;

public class RectHitbox implements Hitbox{

    float[] coords = new float[3];
    float[] corners;
    float radius;

    //TODO hitboxes should be rotateable

    public RectHitbox(float bx, float by, float bz, float ex, float ey, float ez){
        corners = new float[]{
                Math.min(bx,ex),
                Math.min(by,ey),
                Math.min(bz,ez),
                0,
                Math.max(bx,ex),
                Math.max(by,ey),
                Math.max(bz,ez),
                0
        };
        coords[0] = (ex-bx)*.5F + bx;
        coords[1] = (ey-by)*.5F + by;
        coords[2] = (ez-bz)*.5F + bz;
        radius = computeFarthestPointFromCenter();
    }

    @Override
    public float getDepth(float cx, float cy, float cz) {
        return 0;//todo
    }

    @Override
    public float getX() {
        return coords[0];
    }

    @Override
    public float getY() {
        return coords[1];
    }

    @Override
    public float getZ() {
        return coords[2];
    }

    @Override
    public void updateCoords() {

    }

    @Override
    public boolean collides(float x, float y, float z) {
        return x >= corners[0] && x <= corners[4] &&
                y >= corners[1] && y <= corners[5] &&
                z >= corners[2] && z <= corners[6];
    }

    @Override
    public float getEffectiveInteractionRadius() {
        return radius;
    }

    private float computeFarthestPointFromCenter(){
        //todo honestly i'm 99% sure there's a better approach
        float c0 = corners[0], c1 = corners[1], c2 = corners[2],
                c4 = corners[4], c5 = corners[5], c6 = corners[6];
        return (float) Math.max(
                Math.sqrt(c0*c0*c1*c1*c2*c2),
                Math.sqrt(c4*c4*c5*c5*c6*c6)
        );
    }

    @Override
    public String toString() {
        return "RectHitbox{" +
                "coords=" + Arrays.toString(coords) +
                ", corners=" + Arrays.toString(corners) +
                ", radius=" + radius +
                '}';
    }
}
