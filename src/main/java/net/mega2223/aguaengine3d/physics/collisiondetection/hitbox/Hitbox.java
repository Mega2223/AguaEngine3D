package net.mega2223.aguaengine3d.physics.collisiondetection.hitbox;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.physics.CollisionSolver;
import net.mega2223.aguaengine3d.physics.PhysicsManager;
import net.mega2223.aguaengine3d.physics.collisiondetection.hierarchy.Collidiable;
import net.mega2223.aguaengine3d.physics.objects.PhysicsSystem;
import net.mega2223.aguaengine3d.physics.objects.RigidBodySystem;

public abstract class Hitbox implements Collidiable {
    //it's up to the superclass wether it returns 0 or a negative number for non colliding objects
    public abstract void getAxisDepthRelative(float[] result, float locX, float locY, float locZ);

    public abstract float getDepth(float locX, float locY, float locZ);

    private static final float[] buffer = new float[16];

    protected final PhysicsSystem linkedSystem;
    protected final boolean isRigidBody;

    protected Hitbox(PhysicsSystem linkedSystem){
        this.linkedSystem = linkedSystem;
        linkedSystem.bindHitbox(this); //todo exception in case there's another bound hitbox
        isRigidBody = linkedSystem instanceof RigidBodySystem;
    }
    protected Hitbox(){
        isRigidBody = false;
        linkedSystem = null;
    }

    public void getTranslatedVector(float x, float y, float z, float[] result){
        result[0] = x - getX();
        result[1] = y - getY();
        result[2] = z- getZ();
        PhysicsSystem linkedSystem = getLinkedSystem();
        if(isRigidBody){
            ((RigidBodySystem) linkedSystem).getRotationMatrix(buffer);
            MatrixTranslator.multiplyVec4Mat4(result,buffer);
        }
    }

    public void getTranslatedVector(float[] worldVec4, float[] result){
        result[0] = worldVec4[0] - getX();
        result[1] = worldVec4[1] - getY();
        result[2] = worldVec4[2] - getZ();
        PhysicsSystem linkedSystem = getLinkedSystem();
        if(isRigidBody){
            ((RigidBodySystem) linkedSystem).getRotationMatrix(buffer);
            MatrixTranslator.multiplyVec4Mat4(result,buffer);
        }
    }

    public PhysicsSystem getLinkedSystem(){return linkedSystem;}

    protected abstract void resolveCollision(Hitbox hitbox);

    public void resolveForHitbox(Hitbox hitbox){
        float ix = hitbox.getX(), iy = hitbox.getY(), iz = hitbox.getZ(), ir = hitbox.getEffectiveInteractionRadius();
        if(CollisionSolver.checkIfSpheresCollide(ix,iy,iz,ir,getX(),getY(),getZ(),getEffectiveInteractionRadius())){
            resolveCollision(hitbox);
        }
    }

    public void getContactNormal(float x, float y, float z, float[] result){
        PhysicsManager.getContactNormal(x,y,z,this.getX(),this.getY(),this.getZ(),result);
    }

}
