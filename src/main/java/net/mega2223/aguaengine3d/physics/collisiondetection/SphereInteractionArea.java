package net.mega2223.aguaengine3d.physics.collisiondetection;

public class SphereInteractionArea extends InteractionArea{
    float radius;

    public SphereInteractionArea(float radius, Hitbox assossiatedHitbox){
        super(assossiatedHitbox);
        this.radius = radius;
    }

    @Override
    public boolean collides(float x, float y, float z) {
        return Math.sqrt(x*x+y*y+z*z)<=radius;
    }
}
