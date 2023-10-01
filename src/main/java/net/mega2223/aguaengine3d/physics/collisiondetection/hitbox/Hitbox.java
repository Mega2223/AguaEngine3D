package net.mega2223.aguaengine3d.physics.collisiondetection.hitbox;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.physics.collisiondetection.hierarchy.Collidiable;
import net.mega2223.aguaengine3d.physics.objects.PhysicsSystem;
import net.mega2223.aguaengine3d.physics.objects.RigidBodySystem;

public abstract class Hitbox implements Collidiable {
    //it's up to the superclass wether it returns 0 or a negative number for non colliding objects
    public abstract void getDepth(float[] dest, float locX, float locY, float locZ);

    private static final float[] buffer = new float[16];

    public void getTranslatedVector(float[] worldVec4, float[] dest){
        dest[0] = worldVec4[0] - getX();
        dest[1] = worldVec4[1] - getY();
        dest[2] = worldVec4[2] - getZ();
        PhysicsSystem linkedSystem = getLinkedSystem();
        if(linkedSystem instanceof RigidBodySystem){
            ((RigidBodySystem) linkedSystem).getRotationMatrix(buffer);
            MatrixTranslator.multiplyVec4Mat4(dest,buffer);
        }
    }

    abstract PhysicsSystem getLinkedSystem();

    abstract void update();

    abstract void resolveCollision(float localX, float localY, float localZ, float[] resultingForceDest);
}
