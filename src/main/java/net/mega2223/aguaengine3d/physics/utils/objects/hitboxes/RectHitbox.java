package net.mega2223.aguaengine3d.physics.utils.objects.hitboxes;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.CollisionResolver;
import net.mega2223.aguaengine3d.physics.collisiondetection.hitbox.Hitbox;
import net.mega2223.aguaengine3d.physics.objects.PhysicsSystem;
import net.mega2223.aguaengine3d.physics.objects.RigidBodySystem;
import org.lwjgl.system.CallbackI;

import java.util.Arrays;

public class RectHitbox extends Hitbox {

    final float minX, minY, minZ, maxX, maxY, maxZ;
    final float[] pointBuffer = new float[32];
    private static final float[] bufferVec3 = new float[4];
    float radius;

    public RectHitbox(PhysicsSystem linkedSystem, float bx, float by, float bz, float ex, float ey, float ez){
        super(linkedSystem);
        linkedSystem.bindHitbox(this);
        minX = Math.min(bx,ex);
        minY = Math.min(by,ey);
        minZ = Math.min(bz,ez);
        maxX = Math.max(bx,ex);
        maxY = Math.max(by,ey);
        maxZ = Math.max(bz,ez);
        radius = computeFarthestPointFromCenter();
        updatePointBuffer();
    }

    @Override
    public void getAxisDepthRelative(float[] dest, float locX, float locY, float locZ) {
        dest[0] = locX < 0 ? Math.max(0,locX - minX) : Math.min(locX - maxX,0);
        dest[1] = locY < 0 ? Math.max(0,locY - minY) : Math.min(locY - maxY,0);
        dest[2] = locZ < 0 ? Math.max(0,locZ - minZ) : Math.min(locZ - maxZ,0);
    }

    @Override
    public float getDepth(float locX, float locY, float locZ) {
        if (collides(locX,locY,locZ)){
            float deX = locX < 0 ? Math.max(0,locX - minX) : Math.min(locX - maxX,0);
            float deY = locY < 0 ? Math.max(0,locY - minY) : Math.min(locY - maxY,0);
            float deZ = locZ < 0 ? Math.max(0,locZ - minZ) : Math.min(locZ - maxZ,0);
            return Math.abs(Math.max(Math.max(deX,deY),deZ));
        }
        return 0;
    }

    @Override
    public void doLogic(float time) {

    }

    @Override
    protected void resolveCollision(Hitbox hitbox) {
        if(hitbox == this){return;}
        if(hitbox instanceof AxisParallelPlaneHitbox){
            AxisParallelPlaneHitbox  act = (AxisParallelPlaneHitbox) hitbox;
            for (int i = 0; i < 32; i+=4) {
                bufferVec3[0] = pointBuffer[i];
                bufferVec3[1]  = pointBuffer[i + 1];
                bufferVec3[2]  = pointBuffer[i + 2];
                linkedSystem.toLocalCoords(bufferVec3);
                VectorTranslator.debugVector(bufferVec3);
                float px = bufferVec3[0], py = bufferVec3[1], pz = bufferVec3[2];
                float d = act.getDepth(px, py, pz);
                if(d <= 0){continue;}
                act.getContactNormal(px,py,pz,bufferVec3);
                CollisionResolver.resolveConflict(linkedSystem,px,py,pz,bufferVec3[0],bufferVec3[1],bufferVec3[2],d);
                //todo custom collision normals
                if(isRigidBody){
                    CollisionResolver.resolveCollision((RigidBodySystem)linkedSystem,px,py,pz,bufferVec3[0],bufferVec3[1],bufferVec3[2],CollisionResolver.DEF_RESTITUTION);
                } else {
                    CollisionResolver.resolveCollision(linkedSystem,px,py,pz,CollisionResolver.DEF_RESTITUTION);
                }
            }
        }
        if(hitbox instanceof RectHitbox){
            RectHitbox act = (RectHitbox) hitbox;
            for (int i = 0; i < 32; i+=4) {
                float px = pointBuffer[i] + getX();
                float py = pointBuffer[i + 1] + getY();
                float pz = pointBuffer[i + 2] + getZ();
                float d = act.getDepth(px, py, pz);
                if(d <= 0){continue;}
                act.getContactNormal(px,py,pz,bufferVec3);
                CollisionResolver.resolveConflict(linkedSystem,px,py,pz,bufferVec3[0],bufferVec3[1],bufferVec3[2],d);

                if(isRigidBody){
                    System.out.println("COLLISION: s1= " + getX() + ", " + getY() + ", " + getZ() + " s2 = " + hitbox.getX() + ", " + hitbox.getY() + ", " + hitbox.getZ());
                    System.out.println("CONTACT POINT: " + px + ", " + py + ", " + pz);
                    System.out.println("RESOLUTION = " + bufferVec3[0] + ", " + bufferVec3[1] + ", " + bufferVec3[2]);
                    CollisionResolver.resolveCollision((RigidBodySystem)linkedSystem,px,py,pz,bufferVec3[0],bufferVec3[1],bufferVec3[2],CollisionResolver.DEF_RESTITUTION);
                } else {
                    CollisionResolver.resolveCollision(linkedSystem,px,py,pz,CollisionResolver.DEF_RESTITUTION);
                }
            }
        }
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
    //todo rotations :P:P:P:P
    @Override
    public boolean collides(float x, float y, float z) {
        return x >= minX + getX() && x <= maxX + getX() &&
                y >= minY + getY() && y <= maxY + getY()&&
                z >= minZ + getZ() && z <= maxZ + getZ();
    }

    @Override
    public float getEffectiveInteractionRadius() {
        return radius;
    }

    private float computeFarthestPointFromCenter(){
        //todo honestly i'm 99% sure there's a better approach
        return (float) Math.max(
                Math.sqrt(maxX*maxX+maxY*maxY+maxZ*maxZ),
                Math.sqrt(minX*minX+minY*minY+minZ*minZ)
        );
    }

    @Override
    public String toString() {
        return "RectHitbox{" +
                "coords=" + Arrays.toString(linkedSystem.getCoords()) +
                ", corners="  + minX + ", " + minY + ", " + minZ + " : " + maxX + ", " + maxY + ", " + maxZ +
                ", radius=" + radius +
                '}';
    }

    protected void updatePointBuffer(){
        pointBuffer[0] = minX; pointBuffer[1] = minY; pointBuffer[2] = minZ; pointBuffer[3] = 0;
        pointBuffer[4] = maxX; pointBuffer[5] = minY; pointBuffer[6] = minZ; pointBuffer[7] = 0;
        pointBuffer[8] = minX; pointBuffer[9] = minY; pointBuffer[10] = maxZ; pointBuffer[11] = 0;
        pointBuffer[28] = maxX; pointBuffer[29] = minY; pointBuffer[30] = maxZ; pointBuffer[31] = 0;
        pointBuffer[12] = minX; pointBuffer[13] = maxY; pointBuffer[14] = minZ; pointBuffer[15] = 0;
        pointBuffer[16] = maxX; pointBuffer[17] = maxY; pointBuffer[18] = minZ; pointBuffer[19] = 0;
        pointBuffer[20] = minX; pointBuffer[21] = maxY; pointBuffer[22] = maxZ; pointBuffer[23] = 0;
        pointBuffer[24] = maxX; pointBuffer[25] = maxY; pointBuffer[26] = maxZ; pointBuffer[27] = 0;
    }

    @Override
    public void getContactNormal(float x, float y, float z, float[] dest) {
        getTranslatedVector(x,y,z,bufferVec3);
        float rX = maxX - minX, rY = maxY - minY, rZ = maxZ - minZ;
        float absX = Math.abs(bufferVec3[0]);
        float abxY = Math.abs(bufferVec3[1]);
        float absZ = Math.abs(bufferVec3[2]);
        if(absX > abxY && absX > absZ){
            dest[0] = 0;
        } else if (abxY > absX && abxY > absZ){
            dest[1] = 0;
        } else {
            dest[2] = 0;
        }
    }
}
