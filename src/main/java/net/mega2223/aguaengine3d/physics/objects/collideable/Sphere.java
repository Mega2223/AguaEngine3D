package net.mega2223.aguaengine3d.physics.objects.collideable;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.PhysicsObject;
import net.mega2223.aguaengine3d.physics.objects.Particle;
import net.mega2223.aguaengine3d.physics.collisions.Collideable;

public class Sphere extends Particle implements Collideable {

    float radius;

    public Sphere(float mass, float radius) {
        super(mass);
        this.radius = radius;
    }

    @Override
    public boolean collides(float x, float y, float z) {
        return VectorTranslator.getDistance(x,y,z,pos[0],pos[1],pos[2]) <= radius;
    }

    @Override
    public float maxRadius() {
        return radius;
    }

    @Override
    public void getContactNormal(float[] coord, float[] result) {
        // TODO
    }

    @Override
    public boolean collidesWith(Collideable c) {
        if(c instanceof Sphere){
            return ((Sphere) c).radius + radius > VectorTranslator.getDistance(((Sphere) c).pos,pos);
        }
        return false;
    }

}
