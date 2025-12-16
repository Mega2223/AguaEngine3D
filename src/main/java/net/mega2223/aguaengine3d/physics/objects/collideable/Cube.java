package net.mega2223.aguaengine3d.physics.objects.collideable;

import net.mega2223.aguaengine3d.computing.BufferManager;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Mesh;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.misc.annotations.Modified;
import net.mega2223.aguaengine3d.physics.PhysicsMath;
import net.mega2223.aguaengine3d.physics.advanced.RigidBody;
import net.mega2223.aguaengine3d.physics.collisions.Collideable;
import net.mega2223.aguaengine3d.physics.collisions.CollisionMath;

import java.util.Arrays;

public class Cube extends RigidBody implements Collideable {

    private static final float[] buffer = new float[4];
    public static final float TOLERANCE = 0.1F;
    float[] vertices = Mesh.CUBE.getVertices();
    float[] worldVertices = vertices.clone();

    public Cube(float mass) {
        super(mass);
        float[] tensor = new float[16];
        PhysicsMath.getInertialTensorForRect(1,1,1,mass,2f,tensor);
        setInertialTensor(tensor);
    }

    @Override
    public void update(float deltaT) {
        super.update(deltaT);
        updateWorldVertices();
    }

    protected void updateWorldVertices(){
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

    @Override
    public void applyForce(float fx, float fy, float fz) {
        super.applyForce(fx, fy, fz);
    }

    public float solveCollision(Collideable c, float[] contactNormalDest) {
        updateWorldVertices();
        if(c instanceof Sphere){

        } else if (c instanceof Cube) {
            Cube cube2 = (Cube) c;

        } else if (c instanceof FixedPlane) {
            FixedPlane plane = ((FixedPlane) c);
            return resolveCollisionWithPlane(plane);
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
        Arrays.fill(rotationQ4,0);
        rotationQ4[0] = 1; updateWorldVertices();//fixme ja sabe ne
        // mano isso aqui sequer funciona???? TODO

        float xPlus = -buffer[0] + 1, xLess = buffer[0] + 1; // Profundidades já positivadas
        float yPlus = -buffer[1] + 1, yLess = buffer[1] + 1;
        float zPlus = -buffer[2] + 1, zLess = buffer[2] + 1;

        float contactX = Math.min(xPlus,xLess);
        float contactY = Math.min(yPlus,yLess);
        float contactZ = Math.min(zPlus,zLess);

        int maxAxis = contactX >= contactY ? contactX >= contactZ ? 0 : 2 : contactY > contactZ ? 1 : 2;
        int minAxis = contactX < contactY ? contactX < contactZ ? 0 : 2 : contactY <= contactZ ? 1 : 2;

        result[0] = minAxis == 0 ? contactX : 0;
        result[1] = minAxis == 1 ? contactY : 0;
        result[2] = minAxis == 2 ? contactZ : 0;
        
        VectorTranslator.normalize(result);
        VectorTranslator.debugVector("res = ", result);

        if(VectorTranslator.magnitude(velocity) > 1){
            VectorTranslator.normalize(velocity);
        }

        float contactDepth = Math.min(contactX+TOLERANCE,Math.min(contactY+TOLERANCE,contactZ+TOLERANCE));

        contactDepth = Math.max(contactDepth,0);

        MatrixTranslator.multiplyVec4Mat4(result,rotationMatrix);
        BufferManager.freeVec4(buffer);

        VectorTranslator.normalize(result);

        System.out.println("cDepth = "+contactDepth);
        return contactDepth;
    }

    float resolveCollisionWithPlane(FixedPlane plane){
        for (int v = 0; v < worldVertices.length; v+=4) {
            float[] currentVertex = BufferManager.allocateVec4();
            float[] contactNormal = BufferManager.allocateVec4();

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

                float[] vertexVel = BufferManager.allocateVec4();
                getLocalPointVelocity(currentVertex,vertexVel);
                float[] pNormal = plane.normal;

                float separatingVelocity = CollisionMath.separatingVelocity(
                        0,0,0, // o plano é constante ent isso é um atalho
                        vertexVel[0],vertexVel[1],vertexVel[2],
                        -pNormal[0],-pNormal[1],-pNormal[2],
                        0,0,0 // plano não se move :p
                );

                CollisionMath.solveCollision(
                        invMass,
                        plane.getInverseMass(),
                        separatingVelocity, contactNormal, .5F,
                        impulseA, impulseB
                );

                applyImpulse(impulseA,planePoint);

                float[] frictionForce = impulseA; // redundante mas enfim
                plane.getFriction(vertexVel[0],vertexVel[1],vertexVel[2],frictionForce);
                applyForce(frictionForce[0],frictionForce[1],frictionForce[2],
                        planePoint[0],planePoint[1],planePoint[2]);
                // TODO isso tá estranho, o ponto tá certo?
                // Talvez seja a física to Torque q esteja meio ruim msm

                BufferManager.freeVec4(vertexVel);
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

}
