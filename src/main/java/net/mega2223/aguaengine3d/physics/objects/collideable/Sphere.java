package net.mega2223.aguaengine3d.physics.objects.collideable;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.misc.annotations.Modified;
import net.mega2223.aguaengine3d.physics.collisions.CollisionMath;
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
    public float solveCollision(Collideable c, @Modified float[] contactNormalBuffer) {
        if(c instanceof Sphere){
            Sphere sphere = (Sphere) c;
            float depth = (sphere.radius + radius) - VectorTranslator.getDistance(pos,sphere.pos);
            CollisionMath.getContactNormalPoints(pos,sphere.pos,contactNormalBuffer);
            depth = Math.max(depth,0);
            if(depth > 0){
                CollisionMath.solveContactSpheres(this, sphere, contactNormalBuffer, depth);
                CollisionMath.solveCollisionSpheres(this, sphere, 1.0F); // TODO restitution
            }
            return depth;
        }
//        else if (c instanceof FixedPlane) {
            // a classe FixedPlane já lida com esse caso
//        }
        return 0;
    }

    @Override
    public float getCollision(float[] point, float[] result) {
        CollisionMath.getContactNormalPoints(pos, point,result);
        return VectorTranslator.getDistance(point,pos) - radius;
    }
}
