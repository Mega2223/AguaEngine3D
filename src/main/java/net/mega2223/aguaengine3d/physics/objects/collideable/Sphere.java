package net.mega2223.aguaengine3d.physics.objects.collideable;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.misc.annotations.Modified;
import net.mega2223.aguaengine3d.physics.PhysicsObject;
import net.mega2223.aguaengine3d.physics.collisions.CollisionManager;
import net.mega2223.aguaengine3d.physics.collisions.CollisionMath;
import net.mega2223.aguaengine3d.physics.objects.Particle;
import net.mega2223.aguaengine3d.physics.collisions.Collideable;

import java.util.Locale;

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
    public float getCollision(Collideable c, @Modified float[] contactNormalBuffer) {
        if(c instanceof Sphere){
            Sphere sphere = (Sphere) c;
            float depth = (sphere.radius + radius) - VectorTranslator.getDistance(pos,sphere.pos);
            CollisionMath.getContactNormal(pos,sphere.pos,contactNormalBuffer);
            return Math.max(depth,0);
        }
        return 0;
    }

}
