package net.mega2223.aguaengine3d.physics.utils.objects.hitboxes;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.CollisionSolver;
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
            CollisionSolver.resolveCollision(linkedSystem, px, py, pz, CollisionSolver.DEF_RESTITUTION);
            CollisionSolver.resolveConflict(linkedSystem,linkedSystem.getInverseMass(),px,py,pz,d); //APH should simulate virtually infinite mass
        }
        else if(hitbox instanceof SphereHitbox){
            SphereHitbox sh = (SphereHitbox) hitbox;
            float x = (sh.getX() - getX())/2, y = (sh.getY() - getY())/2, z = (sh.getZ() - getZ());
            x += sh.getX(); y+= sh.getY(); z+= sh.getZ();
            float dis = VectorTranslator.getDistance(getX(), getY(), getZ(), sh.getX(), sh.getY(), sh.getZ()) / 2;
            System.out.println(dis);
            float d = (radius+sh.radius)/2 - dis;
            //if(d>0){System.out.println(d);}
            CollisionSolver.resolveCollision(linkedSystem,x,y,z, CollisionSolver.DEF_RESTITUTION);
            CollisionSolver.resolveConflict(linkedSystem,linkedSystem.getInverseMass(),x,y,z,d);
        }
    }
}
