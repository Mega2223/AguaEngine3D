package net.mega2223.aguaengine3d.physics.collisiondetection;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;

import java.util.Vector;

public class Node implements Collideable{
    final float[] coords;
    final float radius;

    final Collideable n1;
    final Collideable n2;

    public Node(Collideable n1, Collideable n2){
        coords = n2.getCenter();
        float[] n1c = n1.getCenter();
        float[] n2c = n2.getCenter();

        VectorTranslator.subtractFromVector(coords, n1c);
        VectorTranslator.scaleVec3(coords,0.5F);
        VectorTranslator.addToVector(coords, n1c);
        this.radius = Math.max(
                VectorTranslator.getDistance(coords,n1c) + n1.getRadius(),
                VectorTranslator.getDistance(coords,n2c) + n2.getRadius()
        );
        this.n1 = n1;
        this.n2 = n2;
    }

    public Node(float radius, float x, float y, float z, Collideable n1, Collideable n2){
        this.radius = radius;
        this.n1 = n1;
        this.n2 = n2;
        coords = new float[]{x,y,z};
    }

    float getX(){return coords[0];}
    float getY(){return coords[1];}
    float getZ(){return coords[2];}
    void getTranslatedVector(float[] worldVector, float[] dest, float radius, Collideable n1, Collideable n2){
        for (int i = 0; i < 3; i++) {
            dest[i] = worldVector[i] - coords[i];
        }
    }

    @Override
    public boolean collides(float x, float y, float z) {
        x-=coords[0]; y-=coords[1]; z-=coords[2];
        return Math.sqrt(x*x+y*y+z*z)<=radius && (n1.collides(x,y,z)||n2.collides(x,y,z));
    }

    public float getRadius() {
        return radius;
    }

    public Collideable getN1() {
        return n1;
    }

    public Collideable getN2() {
        return n2;
    }

    @Override
    public float[] getCenter() {
        return coords.clone();
    }
}
