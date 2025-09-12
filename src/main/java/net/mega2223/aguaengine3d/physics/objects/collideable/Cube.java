package net.mega2223.aguaengine3d.physics.objects.collideable;

import net.mega2223.aguaengine3d.graphics.objects.modeling.Mesh;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.misc.annotations.Modified;
import net.mega2223.aguaengine3d.physics.PhysicsMath;
import net.mega2223.aguaengine3d.physics.advanced.RigidBody;
import net.mega2223.aguaengine3d.physics.collisions.Collideable;
import net.mega2223.aguaengine3d.physics.collisions.CollisionManager;
import net.mega2223.aguaengine3d.physics.collisions.CollisionMath;

public class Cube extends RigidBody implements Collideable {

    private static final float[] buffer = new float[4];
    float[] vertices = Mesh.CUBE.getVertices();
    float[] worldVertices = vertices.clone();

    public Cube(float mass) {
        super(mass);
        float[] tensor = new float[16];
        PhysicsMath.getInertialTensorForRect(1,1,1,mass,tensor);
        setInertialTensor(tensor);
    }

    @Override
    public void update(float deltaT) {
        super.update(deltaT);
        for (int i = 0; i < vertices.length; i+=4) {
            buffer[0] = vertices[i]; buffer[1] = vertices[i+1]; buffer[2] = vertices[i+2];
            toGlobalCoordinateSystem(buffer);
            worldVertices[i] = buffer[0]; worldVertices[i+1] = buffer[1]; worldVertices[i+2] = buffer[2];
        }

    }

    public boolean collides(float x, float y, float z) {
        buffer[0] = x; buffer[1] = y; buffer[2] = z;
        toLocalCoordinateSystem(buffer);
        return buffer[0] <= 1 && buffer[0] >= -1 &&
                buffer[1] <= 1 && buffer[1] >= -1 &&
                buffer[2] <= 1 && buffer[2] >= -1;
    }

    public float maxRadius() {
        return 2; //sqrt 2 mas fds
    }

    public void getContactNormal(float[] coord, @Modified float[] result) {
        //TODO returns bool?
        VectorTranslator.copy(coord,buffer);
        toLocalCoordinateSystem(buffer);
        // f_mx(p) = max(0,min(-p.x+1,p.x+1))
        //f(p) = min(f_mx(p),f_my(p),f_mz(p))
        float colX = Math.max(0,Math.min(-buffer[0] + 1, buffer[0] + 1));
        float colY = Math.max(0,Math.min(-buffer[1] + 1, buffer[1] + 1));
        float colZ = Math.max(0,Math.min(-buffer[2] + 1, buffer[2] + 1));

        int contactAxis = colX > colY ? colX > colZ ? 0 : 2 : colY > colZ ? 1 : 2;
        //float contactDepth = contactAxis == 0 ? colX : contactAxis == 1 ? colY : colZ;
        result[0] = contactAxis == 0 ? colX : 0;
        result[1] = contactAxis == 1 ? colY : 0;
        result[2] = contactAxis == 2 ? colZ : 0;
        result[3] = 0;
        toGlobalCoordinateSystem(result);
    }

    @Override
    public void applyForce(float fx, float fy, float fz) {
        super.applyForce(fx, fy, fz);
    }

    public float solveCollision(Collideable c, float[] contactNormalDest) {
        if(c instanceof Sphere){

        } else if (c instanceof Cube) {

        } else if (c instanceof FixedPlane) {
            FixedPlane f = ((FixedPlane) c);
            float depth = 0;
            for (int v = 0; v < worldVertices.length; v+=4) {
                vertexBuffer[0] = worldVertices[v];
                vertexBuffer[1] = worldVertices[v+1];
                vertexBuffer[2] = worldVertices[v+2];
                vertexBuffer[3] = worldVertices[v+3];
                float contactDepth = f.getCollision(vertexBuffer, buffer);
                VectorTranslator.flipVector(buffer);
                contactDepth = Math.max(contactDepth,0);
                if(contactDepth > 0){
                    CollisionMath.solveContact(this,c,vertexBuffer,buffer,contactDepth);
                    // solve Collision
//                    VectorTranslator.copy(f.normal,buffer);
//                    VectorTranslator.scaleVector(buffer,contactDepth);
//                    applyRotationalCorrection(buffer[0],buffer[1],buffer[2],vertexBuffer[0],vertexBuffer[1],vertexBuffer[2]);
//                    setVelocity();
//                    applyForce(buffer[0],buffer[1],buffer[1],vertexBuffer[0],vertexBuffer[1],vertexBuffer[2]);
//                    setVelocity(vx(),-vy()*.5F,vz());
//                    setCoordinates(x(),y()+contactDepth,z());
                    return depth;
                }
                depth = Math.max(depth,contactDepth);
            }
            return depth;
        }
        return 0F;
    }
    float[] vertexBuffer = new float[4];
    //float[] buffer = new float[4];
}
