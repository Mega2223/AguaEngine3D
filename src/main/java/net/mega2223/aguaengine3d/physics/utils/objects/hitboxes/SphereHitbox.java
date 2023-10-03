package net.mega2223.aguaengine3d.physics.utils.objects.hitboxes;

import net.mega2223.aguaengine3d.mathematics.MathUtils;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.CollisionResolver;
import net.mega2223.aguaengine3d.physics.collisiondetection.hitbox.Hitbox;
import net.mega2223.aguaengine3d.physics.objects.PhysicsSystem;

public class SphereHitbox extends Hitbox {

    final float radius;

    public SphereHitbox(PhysicsSystem linkedSystem, float radius) {
        super(linkedSystem);
        this.radius = radius;
    }

    @Override
    public boolean collides(float x, float y, float z) {
        return radius <= Math.sqrt(x*x+y*y+z*z);
    }

    @Override
    public float getEffectiveInteractionRadius() {
        return radius;
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
    public void doLogic(float time) {

    }

    @Override
    public void getAxisDepthRelative(float[] dest, float locX, float locY, float locZ) {

    }

    @Override
    public float getDepth(float locX, float locY, float locZ) {
        return 0;
    }

    @Override
    protected void resolveCollision(Hitbox hitbox) {
        if(hitbox instanceof AxisParallelPlaneHitbox){
            float px = this.getX();
            float py = this.getY() - radius;
            float pz = this.getZ();
            float d = hitbox.getDepth(px, py, pz);
            CollisionResolver.resolveCollision(getLinkedSystem(), px, py, pz,CollisionResolver.DEF_RESTITUTION);
            CollisionResolver.resolveConflict(getLinkedSystem(),px,py,pz,d);
        }
        else if(hitbox instanceof SphereHitbox){
            float x = getX(), y = getY(), z = getZ(),
            ox = hitbox.getX(), oy = hitbox.getY(), oz = hitbox.getZ();
            x-=ox; y-= oy; z-=oz;
            float m = VectorTranslator.getMagnitudeVec3(x,y,z);
            float d = VectorTranslator.getDistance(x,y,z,ox,oy,oz);
            if(m>0){x/=m;y/=m;z/=m;}
            x*=radius;y*=radius;z*=radius;
            x+=ox;y+=oy;z+=oz;
            d = -(d-(radius + ((SphereHitbox) hitbox).radius));
            if(x+y+z > 0){System.out.println("Contact: " + x + ", " + y + ", " + z);}
            //CollisionResolver.resolveConflict(getLinkedSystem(),x,y,z,d);
            CollisionResolver.resolveCollision(getLinkedSystem(),x, y, z,CollisionResolver.DEF_RESTITUTION);
        }
    }
}
