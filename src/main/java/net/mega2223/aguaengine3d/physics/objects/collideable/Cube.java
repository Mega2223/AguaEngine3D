package net.mega2223.aguaengine3d.physics.objects.collideable;

import net.mega2223.aguaengine3d.computing.BufferManager;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Mesh;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.PhysicsMath;
import net.mega2223.aguaengine3d.physics.advanced.RigidBody;
import net.mega2223.aguaengine3d.physics.collisions.Collideable;
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
        updateWorldVertices();
    }

    public void updateWorldVertices(){
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

//    public void getContactNormal(float[] coord, @Modified float[] result) {
//
//    }

    @Override
    public void applyForce(float fx, float fy, float fz) {
        super.applyForce(fx, fy, fz);
    }

    public float solveCollision(Collideable c, float[] contactNormalDest) {
        updateWorldVertices();
        if(c instanceof Sphere){

        } else if (c instanceof Cube) {

        } else if (c instanceof FixedPlane) {
            FixedPlane plane = ((FixedPlane) c);

            for (int v = 0; v < worldVertices.length; v+=4) {
                float[] currentVertex = BufferManager.allocateVec4();
                float[] contactNormal = BufferManager.allocateVec4();

//                currentVertex[0] = worldVertices[v]; currentVertex[1] = worldVertices[v+1];
//                currentVertex[2] = worldVertices[v+2]; currentVertex[3] = worldVertices[v+3];

                VectorTranslator.copy(worldVertices[v],worldVertices[v+1],worldVertices[v+2],currentVertex);

                float contactDepth = plane.getCollision(currentVertex, contactNormal);

                VectorTranslator.flipVector(contactNormal);
                contactDepth = Math.max(contactDepth,0);

                if(contactDepth > 0){
                    float[] planePoint = BufferManager.allocateVec4(),
                            planeVel = BufferManager.allocateVec4(),
                            impulseA = BufferManager.allocateVec4(),
                            impulseB = BufferManager.allocateVec4(),
                            translation = BufferManager.allocateVec4();

                    plane.getVelocity(planeVel);
                    plane.getClosestPoint(currentVertex,planePoint);
                    CollisionMath.solveContact(pos,invMass,planePoint,plane.getInverseMass(),
                            contactNormal,contactDepth,translation,null);
                    applyRotationalTranslation(translation,planePoint);
//                    updateWorldVertices();
//                    VectorTranslator.copy(worldVertices[v],worldVertices[v+1],worldVertices[v+2],currentVertex);

                    float[] vertexVel = BufferManager.allocateVec4();
                    getLocalPointVelocity(currentVertex,vertexVel);
                    float[] pNormal = plane.normal;

                    float separatingVelocity = CollisionMath.separatingVelocity(
                            0,0,0,
                            vertexVel[0],vertexVel[1],vertexVel[2],
                            -pNormal[0],-pNormal[1],-pNormal[2],
                            0,0,0
                    );

                    BufferManager.freeVec4(vertexVel);

                    CollisionMath.solveCollision(
                            invMass,
                            plane.getInverseMass(),
                            separatingVelocity, contactNormal, .5F,
                            impulseA, impulseB
                    );

                    //FIXME ele só tá considerando o primeiro ponto de colisão :p

                    VectorTranslator.debugVector("center",pos);
                    VectorTranslator.debugVector("velocity",velocity);
                    VectorTranslator.debugVector("angularVelocity",angularVelocity);
                    VectorTranslator.debugVector("point",currentVertex);
                    VectorTranslator.debugVector("pointVelocity",vertexVel);
                    VectorTranslator.debugVector("planePoint",planePoint);
                    VectorTranslator.debugVector("separatingVelocity",separatingVelocity);
                    VectorTranslator.debugVector("resultingImpulse",impulseA);
                    VectorTranslator.debugVector("resultingTranslation",translation);
//                    System.exit(2223);

                    applyImpulse(impulseA,planePoint);

                    BufferManager.freeVec4(planeVel); BufferManager.freeVec4(planePoint);
                    BufferManager.freeVec4(translation);
                    BufferManager.freeVec4(impulseA); BufferManager.freeVec4(impulseB);

                    BufferManager.freeVec4(currentVertex);
                    BufferManager.freeVec4(contactNormal);
                    return contactDepth;
                }
                BufferManager.freeVec4(currentVertex);
                BufferManager.freeVec4(contactNormal);
            }

            return 0;
        }
        return 0F;
    }

    @Override
    public float getCollision(float[] point, float[] result) {
        float[] buffer = BufferManager.allocateVec4();

        VectorTranslator.copy(point,buffer);
        toLocalCoordinateSystem(buffer);
        // f_mx(p) = max(0,min(-p.x+1,p.x+1))
        // f(p) = min(f_mx(p),f_my(p),f_mz(p))
        float colX = Math.max(0,Math.min(-buffer[0] + 1, buffer[0] + 1));
        float colY = Math.max(0,Math.min(-buffer[1] + 1, buffer[1] + 1));
        float colZ = Math.max(0,Math.min(-buffer[2] + 1, buffer[2] + 1));

        int contactAxis = colX > colY ? colX > colZ ? 0 : 2 : colY > colZ ? 1 : 2;

        float contactDepth = contactAxis == 0 ? colX : contactAxis == 1 ? colY : colZ;

        result[0] = contactAxis == 0 ? colX : 0;
        result[1] = contactAxis == 1 ? colY : 0;
        result[2] = contactAxis == 2 ? colZ : 0;
        result[3] = 0;
        toGlobalCoordinateSystem(result);

        BufferManager.freeVec4(buffer);

        return contactDepth;
    }
}
