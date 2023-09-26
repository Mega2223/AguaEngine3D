package net.mega2223.aguaengine3d.physics.collisiondetection;

public abstract class HitboxInteractionArea implements Collideable {
    float[] coords = new float[3];

    float getX(){return coords[0];}
    float getY(){return coords[0];}
    float getZ(){return coords[0];}

    Hitbox assossiatedHitbox;
    public HitboxInteractionArea(Hitbox assossiatedHitbox){
        this.assossiatedHitbox = assossiatedHitbox;
    }

    void getTranslatedVector(float[] worldVector, float[] dest){
        for (int i = 0; i < 3; i++) {
            dest[i] = worldVector[i] - coords[i];
        }
    }
}
