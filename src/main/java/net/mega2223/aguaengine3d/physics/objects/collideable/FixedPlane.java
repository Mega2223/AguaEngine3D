package net.mega2223.aguaengine3d.physics.objects.collideable;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.misc.annotations.Modified;
import net.mega2223.aguaengine3d.physics.collisions.Collideable;
import net.mega2223.aguaengine3d.physics.collisions.CollisionMath;
import net.mega2223.aguaengine3d.physics.objects.Particle;

import java.util.Arrays;
/** Represents an infinite plane collideable object
 * */
public class FixedPlane extends Particle implements Collideable {
    private static final float[] buffer = new float[4];

    float[] normal = new float[4], point = new float[4];

    /** Creates a FixedPlane object
     * @param normal Plane normal, that is, a vector which is orthogonal with all possible vectors inside the plane
     * @param point Some point that belongs to the plane
     * */
    public FixedPlane(float[] normal, float[] point){
        super(Float.POSITIVE_INFINITY);
        VectorTranslator.getNormalized(normal,this.normal);
        VectorTranslator.copy(point,this.point);
    }

    @Override
    public void update(float deltaT) {
        Arrays.fill(velocity,0);
        super.update(deltaT);
        Arrays.fill(velocity,0);
    }

    @Override
    public boolean collides(float x, float y, float z) {
        buffer[0] = x; buffer[1] = y; buffer[2] = z;
        return VectorTranslator.dotProduct(buffer,normal) == 0;
    }

    @Override
    public float maxRadius() {
        return Float.POSITIVE_INFINITY;
    }

    @Override
    public void getContactNormal(float[] point, float[] result) {
        VectorTranslator.getFlipped(normal,result);
    }

    @Override
    public float solveCollision(Collideable c, float[] contactNormalDest) {
        if(c instanceof Sphere){
            Sphere s = (Sphere) c;
            s.getPos(buffer);
            VectorTranslator.subtractFromVector(buffer,point);
            VectorTranslator.flipVector(buffer);
            float depth = Math.max(0, VectorTranslator.dotProduct(buffer,normal) + s.radius);

            if(depth > 0){
                VectorTranslator.copy(normal,contactNormalDest);
                VectorTranslator.flipVector(contactNormalDest);
                CollisionMath.solveContactSpheres(this, s, contactNormalDest, depth);

                // computes the closest point in the plane from the center of the sphere
                VectorTranslator.subtractFromVector(point[0],point[1],point[2],c.x(),c.y(),c.z(),contactPointBuffer);
                VectorTranslator.scaleVector(normal, VectorTranslator.dotProduct(contactPointBuffer,normal), contactPointBuffer);
                VectorTranslator.addToVector(c.x(),c.y(),c.z(),contactPointBuffer);

                float sep = CollisionMath.separatingVelocity(
                        contactPointBuffer[0],contactPointBuffer[1],contactPointBuffer[2],
                        0,0,0, c.x(),c.y(),c.z(), c.vx(),c.vy(),c.vz()
                );

                CollisionMath.solveCollision(c,this,sep,normal,1);
            }
            return depth;
        }
        return 0;
    }

    private final float[] contactPointBuffer = new float[4];

    @Override
    public float getCollision(float[] point, float[] result){
        VectorTranslator.copy(point,buffer);
        VectorTranslator.subtractFromVector(buffer,this.point);
        VectorTranslator.flipVector(buffer);
        float depth = Math.max(0, VectorTranslator.dotProduct(buffer,normal));
        VectorTranslator.copy(normal, result);
        VectorTranslator.flipVector(result);
        return Math.max(0,depth);
    }

    /**Returns distance from the closest point iin the plane*/
    public float getDistance(float[] coord){
        VectorTranslator.subtractFromVector(point,coord,buffer);
        return VectorTranslator.dotProduct(buffer,normal) + VectorTranslator.magnitude(coord);
    }

    public void getClosestPoint(float[] coord, @Modified float[] dest){
        VectorTranslator.scaleVector(normal,getDistance(coord),dest);
        VectorTranslator.addToVector(dest,coord);
    }

    @Override
    public float x() {
        return point[0];
    }

    @Override
    public float y() {
        return point[1];
    }

    @Override
    public float z() {
        return point[2];
    }

    static final float[][] vBuffer = new float[4][4];
    @Override
    public float getClosingVelocity(float x, float y, float z, float vx, float vy, float vz) {
        VectorTranslator.copy(x,y,z,buffer);
        getClosestPoint(buffer, vBuffer[0]);
        Arrays.fill(vBuffer[1],0);
        VectorTranslator.copy(x,y,z,vBuffer[2]);
        VectorTranslator.copy(vx,vy,vz,vBuffer[3]);
        return CollisionMath.closingVelocity(vBuffer[0],vBuffer[1],vBuffer[2], vBuffer[3]);
    }

    @Override
    public void getVelocity(float[] dest) {
        super.getVelocity(dest);
    }

    @Override
    public void setVelocity(float vx, float vy, float vz) {
        System.out.println("whar");
    }
}
