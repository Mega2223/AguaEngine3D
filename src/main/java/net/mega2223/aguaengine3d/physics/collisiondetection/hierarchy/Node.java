package net.mega2223.aguaengine3d.physics.collisiondetection.hierarchy;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.CollisionSolver;
import net.mega2223.aguaengine3d.physics.collisiondetection.hitbox.Hitbox;

import java.util.Arrays;

public class Node implements Collidiable {

    float radius;

    final Collidiable n1;
    final Collidiable n2;

    public Node(Collidiable n1, Collidiable n2){
        this.n1 = n1;
        this.n2 = n2;
        updateCoords();
    }
    protected float[] coords = new float[4];

    public void updateCoords() {
        //TODO way too much object instantiation here since the getCenter method creates a clone each call
        System.arraycopy(n2.getCenter(),0,coords,0,3);
        float[] n1c = n1.getCenter();
        float[] n2c = n2.getCenter();
        VectorTranslator.subtractFromVector(coords, n1c);
        VectorTranslator.scaleVec3(coords,0.5F);
        VectorTranslator.addToVector(coords, n1c);
        this.radius = Math.max(
                VectorTranslator.getDistance(coords,n1c) + n1.getEffectiveInteractionRadius(),
                VectorTranslator.getDistance(coords,n2c) + n2.getEffectiveInteractionRadius()
        );
        if(n1 instanceof Node){((Node) n1).updateCoords();}
        if(n2 instanceof Node){((Node) n2).updateCoords();}
    }

    @Override
    public void doLogic(float time) {

    }

    @Override
    public void resolveForHitbox(Hitbox hitbox) {
        float ix = hitbox.getX(), iy = hitbox.getY(), iz = hitbox.getZ(), ir = hitbox.getEffectiveInteractionRadius();
        float px = getX(), py = getY(), pz = getZ(), pr = getEffectiveInteractionRadius();
        if(CollisionSolver.checkIfSpheresCollide(ix,iy,iz,ir, px, py, pz, pr)){
            n1.resolveForHitbox(hitbox);
            n2.resolveForHitbox(hitbox);
        }
    }

    public void forceResolveForHitbox(Hitbox hitbox){
        n1.resolveForHitbox(hitbox);
        n2.resolveForHitbox(hitbox);
    }

    public Node(float radius, float x, float y, float z, Collidiable n1, Collidiable n2){
        this.radius = radius;
        this.n1 = n1;
        this.n2 = n2;
        coords[0] = x; coords[1] = y; coords[2] = z;
    }

    public float getX(){return coords[0];}
    public float getY(){return coords[1];}
    public float getZ(){return coords[2];}

    void getTranslatedVector(float[] worldVector, float[] dest, float radius, Collidiable n1, Collidiable n2){
        for (int i = 0; i < 3; i++) {
            dest[i] = worldVector[i] - coords[i];
        }
    }

    @Override
    public boolean collides(float x, float y, float z) {
        x-=coords[0]; y-=coords[1]; z-=coords[2];
        return Math.sqrt(x*x+y*y+z*z)<=radius && (n1.collides(x,y,z)||n2.collides(x,y,z));
    }

    public float getEffectiveInteractionRadius() {
        return radius;
    }

    public Collidiable getN1() {
        return n1;
    }

    public Collidiable getN2() {
        return n2;
    }

    @Override
    public float[] getCenter() {
        return coords.clone();
    }

    @Override
    public String toString() {
        return "Node{" +
                "coords=" + Arrays.toString(coords) +
                ", radius=" + radius +
                '}';
    }
}
