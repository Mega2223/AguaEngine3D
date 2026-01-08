package net.mega2223.aguaengine3d.physics.objects.collideable;

import net.mega2223.aguaengine3d.Gaem3D;
import net.mega2223.aguaengine3d.computing.BufferManager;
import net.mega2223.aguaengine3d.graphics.objects.misc.Line;
import net.mega2223.aguaengine3d.graphics.objects.misc.Point;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Mesh;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.PhysicsMath;
import net.mega2223.aguaengine3d.physics.objects.advanced.RigidBody;
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
        PhysicsMath.getInertialTensorForRect(2,2,2,mass,2f,tensor);
        setInertiaTensor(tensor);
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
        updateWorldVertices(); // TODO ele tá reportando o normal de contato?
        if(c instanceof Sphere){
            // TODO
        } else if (c instanceof Cube) {
            Cube cube2 = (Cube) c;
            return resolveCollisionWithCube(cube2);
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

    // TODO HONESTAMENTE, EM VEZ DE FICAR ALOCANDO EM CADA FRAME
    //  SERIA MAIS FÁCIL MANTER O ESCOPO DE BUFFERS PERMANENTES GLOBAL
    //  e sim, eu sei que o BufferManager foi feito justamente pra não
    //  ter que fazer isso, mas fazer o que :p
    //  esse código é terrível de ler, prefiro ter um escopo enorme
    //  also uma alocação de buffer permanente seria legal para
    //  evitar alguns checks, depois eu vejo um jeito de
    //  conter o escopo somente a essa função e manter um acesso rápido

    // a convenção padrão deveria ser assim, cada função declara os seus buffers
    // em cima de si mesma de forma estática, o maior problema com esse padrão
    // é uma potencial colisão de nomes
    float[] nearestPlanePoint = BufferManager.allocateVec4StaticContext(),
            planeVelocity = BufferManager.allocateVec4StaticContext(),
            impulseSelf = BufferManager.allocateVec4StaticContext(),
            translationSelf = BufferManager.allocateVec4StaticContext(),
            currentMeshVertex = BufferManager.allocateVec4StaticContext(),
            contactNormal = BufferManager.allocateVec4StaticContext(),
            vertexVelocity = BufferManager.allocateVec4StaticContext(),
            frictionForce = BufferManager.allocateVec4StaticContext();

    // Iterates for every vertex, if one is inside the plane, resolves accordingly
    protected float resolveCollisionWithPlane(FixedPlane plane){
        for (int v = 0; v < worldVertices.length; v+=4) {

            VectorTranslator.copy(worldVertices[v],worldVertices[v+1],worldVertices[v+2], currentMeshVertex);

            float contactDepth = plane.getCollision(currentMeshVertex, contactNormal);
            VectorTranslator.flipVector(contactNormal);
            contactDepth = Math.max(contactDepth,0);

            if(contactDepth > 0){
                plane.getVelocity(planeVelocity); // It's zero lol
                plane.getClosestPoint(currentMeshVertex, nearestPlanePoint);
                CollisionMath.solveContact(pos,invMass, nearestPlanePoint,plane.getInverseMass(),
                        contactNormal,contactDepth, translationSelf,null);

                applyRotationalTranslation(translationSelf, nearestPlanePoint);

                getLocalPointVelocity(currentMeshVertex, vertexVelocity);
                float[] planeNormal = plane.normal;

                float separatingVelocity = CollisionMath.separatingVelocity(
                        0,0,0, // o plano é constante ent isso é um atalho
                        vertexVelocity[0], vertexVelocity[1], vertexVelocity[2],
                        -planeNormal[0],-planeNormal[1],-planeNormal[2],
                        0,0,0 // plano não se move :p
                );

                CollisionMath.solveCollision(
                        invMass,
                        plane.getInverseMass(),
                        separatingVelocity, contactNormal,
                        .5F*( getRestitution() + plane.getRestitution()),
                        impulseSelf, null
                );
                applyImpulse(impulseSelf, nearestPlanePoint);
                // Applies a friction against the object's speed component which is perpendicular
                // to the plane
                plane.getFriction(
                        // TODO como isso é calcular uma projeção num plano e provavelmente
                        //  funciona para poliedros regulares, seria legal ter um GeometryUtils
                        //  com esse tipo de função
                        vertexVelocity[0],vertexVelocity[1],vertexVelocity[2],
                        getMaterial().getFriction(), frictionForce);
                applyForce(frictionForce[0],frictionForce[1],frictionForce[2],
                        nearestPlanePoint[0], nearestPlanePoint[1], nearestPlanePoint[2]);
                return contactDepth;
            }
        }
        return 0;
    }

    protected float resolveCollisionWithCube(Cube cube){
        return resolveCollisionWithCube(cube,true);
    }

    float[] contactAccum = BufferManager.allocateVec4StaticContext(),
            collisionPointWorld = BufferManager.allocateVec4StaticContext(),
            collisionPointWorldVelocity = BufferManager.allocateVec4StaticContext(),
            cubeCollisionPointW = BufferManager.allocateVec4StaticContext(),
            cubeCollisionPointVelW = BufferManager.allocateVec4StaticContext(),
            impulseCube = BufferManager.allocateVec4StaticContext();

    protected float resolveCollisionWithCube(Cube cube, boolean iterateBoth){
        // TODO falta resolver as acelerações (also contato edge/plano)
        Arrays.fill(contactNormal,0);
        float contactDepth = 0F;
        // Resolves point-face collisions
        for (int v = 0; v < worldVertices.length; v+=4) {
            VectorTranslator.copy(worldVertices[v], worldVertices[v + 1], worldVertices[v + 2], currentMeshVertex);
            cube.toLocalCoordinateSystem(currentMeshVertex);
            // O acumulador de contato é um pedido de correção no sistema de coordenadas do mesh
            // para algum determinado eixo, ele pode ser maior ou menor que zero, só há um contato
            // se os 6 planos forem penetrados
            for (int axis = 0; axis < 3; axis++) {
                float xPosCorrection = Math.max(0,1 - currentMeshVertex[axis]); //sugere correção para o positivo
                float xNegCorrection = Math.min(0,- currentMeshVertex[axis] - 1); // sugere correção para o negativo
                // não há caso onde ambos são nulos, peguemos o de menor valor absoluto
                if(xPosCorrection < -xNegCorrection){
                    contactAccum[axis] = xPosCorrection;
                } else {
                    contactAccum[axis] = xNegCorrection;
                }
            }
            if(contactAccum[0] != 0 && contactAccum[1] != 0 && contactAccum[2] != 0){
                isolateAxisWithLowestAbs(contactAccum);
//                VectorTranslator.debugVector(contactAccum);
                contactDepth = VectorTranslator.magnitude(contactAccum);
                // é melhor pegar o eixo mínimo eu acho
                VectorTranslator.copy(contactAccum,contactNormal);
                VectorTranslator.normalize(contactNormal);
                MatrixTranslator.multiplyVec4Mat4(contactNormal,cube.rotationMatrix);
                VectorTranslator.copy(worldVertices[v], worldVertices[v + 1], worldVertices[v + 2], collisionPointWorld);

                // gets the collision point at the other cube
                VectorTranslator.copy(worldVertices[v], worldVertices[v + 1], worldVertices[v + 2], cubeCollisionPointW);
                VectorTranslator.scaleVector(contactNormal,contactDepth);
                VectorTranslator.addToVector(cubeCollisionPointW,contactNormal);
                VectorTranslator.normalize(contactNormal);
            }
        }
        if(iterateBoth && contactDepth == 0){
            cube.resolveCollisionWithCube(this,false);
        }
        if(contactDepth > 0){
//            Arrays.fill(velocity,0); // FIXME REMOVE ISSO >:(
//            Arrays.fill(cube.velocity,0);
            //FIXME como eu nao to mexendo na aceleração ele vai acumulando até ela 'explodir'
            // e os cubos entrarem um no outro

            // impulso / resolução de colisão
            getLocalPointVelocity(collisionPointWorld,collisionPointWorldVelocity);
            cube.getLocalPointVelocity(cubeCollisionPointW,cubeCollisionPointVelW);
//            float separatingVelocity = CollisionMath.separatingVelocity(
//                    collisionPointWorld[0], collisionPointWorld[1], collisionPointWorld[2],
//                    collisionPointWorldVelocity[0], collisionPointWorldVelocity[1], collisionPointWorldVelocity[2],
//                    cubeCollisionPointW[0], cubeCollisionPointW[1], cubeCollisionPointW[2], // TODO
//                    cubeCollisionPointVelW[0], cubeCollisionPointVelW[1], cubeCollisionPointVelW[2] // TODO
//            );

            float separatingVelocity = CollisionMath.separatingVelocity(
                   cubeCollisionPointW, collisionPointWorldVelocity,
                   collisionPointWorld, cubeCollisionPointVelW
                    // Trocados deliberadamente, presumindo que o ponto do nosso cubo
                    // estiver dentro do outro cubo, ele vai estar tecnicamente 'separando'
                    // pois os pontos estarão se movendo para longe um do outro
            );

            CollisionMath.solveCollision(
                    invMass,
                    cube.invMass,
                    separatingVelocity, contactNormal,
                    .5F*( getRestitution() + cube.getRestitution()),
                    impulseSelf, impulseCube
            );

            VectorTranslator.debugVector("VERTEX IMPULSE",impulseSelf);
            VectorTranslator.debugVector("FACE IMPULSE",impulseCube);
            System.out.printf("SEP: %f\n",separatingVelocity);
            applyImpulse(impulseSelf, collisionPointWorld);
            cube.applyImpulse(impulseCube, cubeCollisionPointW);

            Gaem3D.context.addObject(new Point(
                    .05F,cubeCollisionPointW[0],cubeCollisionPointW[1],cubeCollisionPointW[2],1,0,0){
                int t = 500;
                @Override public boolean isValid() {
                    t--; return t > 0;
                }
            });

            Line cubeImpulse = new Line(1, 0, 0){
                int t = 500;
                @Override public boolean isValid() {
                    t--; return t > 0;
                }};
            cubeImpulse.setStart(cubeCollisionPointW);
            cubeImpulse.setDirection(impulseCube);
            Gaem3D.context.addObject(cubeImpulse);

            Gaem3D.context.addObject(new Point(
                    .05F,collisionPointWorld[0],collisionPointWorld[1],collisionPointWorld[2],0,1,0){
                int t = 500;
                @Override public boolean isValid() {
                    t--; return t > 0;
                }
            });

            Line selfImpulse = new Line(0, 1, 0){
                int t = 500;
                @Override public boolean isValid() {
                    t--; return t > 0;
                }
            };
            selfImpulse.setStart(collisionPointWorld);
            selfImpulse.setDirection(impulseSelf);
            Gaem3D.context.addObject(selfImpulse);

            // tradução / resolução de contato
            float invMassSum = invMass + cube.invMass;
            VectorTranslator.scaleVector(contactNormal,contactDepth * invMass / invMassSum);
            applyRotationalTranslation(contactNormal,collisionPointWorld);
            VectorTranslator.normalize(contactNormal);
            VectorTranslator.flipVector(contactNormal);
            VectorTranslator.scaleVector(contactNormal,contactDepth * cube.invMass / invMassSum);
            cube.applyRotationalTranslation(contactNormal,collisionPointWorld);
//            Line toAdd = new Line(1, 0, 0){
//                int val = 10;
//                @Override
//                public void doLogic(int iteration) {
//                    super.doLogic(iteration);
//                    val--;
//                }
//                @Override
//                public boolean isValid() {
//                    return val > 0;
//                }
//            };
//            toAdd.setStart(collisionPointWorld);
//            toAdd.setDirection(contactNormal);
//            Gaem3D.context.addObject(toAdd);

        }
        return contactDepth;
    }

    protected static void isolateAxisWithLargestAbs(float[] vec3){
        float absX = Math.abs(vec3[0]), absY = Math.abs(vec3[1]), absZ = Math.abs(vec3[2]);
        if(absX > absY && absX > absZ){
            vec3[1] = 0; vec3[2] = 0;
        } else if (absY > absZ){
            vec3[0] = 0; vec3[2] = 0;
        } else {
            vec3[0] = 0; vec3[1] = 0;
        }
    }

    private static void isolateAxisWithLowestAbs(float[] vec3){
        float absX = Math.abs(vec3[0]), absY = Math.abs(vec3[1]), absZ = Math.abs(vec3[2]);
        if(absX < absY && absX < absZ){
            vec3[1] = 0; vec3[2] = 0;
        } else if (absY < absZ){
            vec3[0] = 0; vec3[2] = 0;
        } else {
            vec3[0] = 0; vec3[1] = 0;
        }
    }
}
