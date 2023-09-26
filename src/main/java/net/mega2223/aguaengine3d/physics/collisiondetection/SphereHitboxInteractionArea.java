package net.mega2223.aguaengine3d.physics.collisiondetection;

public class SphereHitboxInteractionArea extends HitboxInteractionArea {
    float radius;

    public SphereHitboxInteractionArea(float radius, Hitbox assossiatedHitbox){
        super(assossiatedHitbox);
        this.radius = radius;
    }

    @Override
    public boolean collides(float x, float y, float z) {
        return Math.sqrt(x*x+y*y+z*z)<=radius;
    }
}
