package net.mega2223.aguaengine3d.physics.objects.advanced;

import net.mega2223.aguaengine3d.computing.BufferManager;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.misc.annotations.Modified;
import net.mega2223.aguaengine3d.physics.Material;
import net.mega2223.aguaengine3d.physics.QuaternionTranslator;

import java.util.Arrays;

public class RigidBody implements Rotatable {

    private final static float[][] buffers = new float[2][4];

    protected final float[] pos = new float[3];
    protected final float[] velocity = new float[3];

    protected final float[] rotationQ4 = {1F,0F,0F,0F};
    public final float[] angularVelocity = new float[4]; //FIXME

    protected float mass, invMass;
    protected float[] inertialTensor = new float[16], inverseInertialTensor =  new float[16];

    private final float[] accelerationAccumulator = new float[4];
    private final float[] angularAccelAccumulator = new float[4];
    private final float[] translationAccumulator = new float[4];
    private final float[] deltaPos = new float[4];
    protected final float[] rotationMatrix = new float[16];
    protected final float[] inverseRotationMatrix = new float[16];
    protected final float[] rotatedInverseInertiaTensor = new float[16];

    Material material = Material.DEFAULT;

    public RigidBody(float mass) {
        this.mass = mass;
        this.invMass = 1F/mass;
        MatrixTranslator.generateIdentity(inertialTensor);
        MatrixTranslator.generateIdentity(inverseInertialTensor);
    }

    public RigidBody(float mass, float[] inertialTensor){
        this.mass = mass;
        this.invMass = 1F/mass;
        this.setInertialTensor(inertialTensor);
    }

    public void update(float deltaT){
        // Rotation Normalization
        QuaternionTranslator.normalize(rotationQ4);

        // Accumulated Translations
        // TODO ao meu ver todo PhysObject precisa fazer isso, ou seja, coloca na particula
        VectorTranslator.addToVector(pos,translationAccumulator);
        Arrays.fill(translationAccumulator,0);

        // Linear Velocity
        VectorTranslator.addToVector(velocity,accelerationAccumulator);
        Arrays.fill(accelerationAccumulator,0);
        VectorTranslator.scaleVector(velocity,deltaT, deltaPos);
        VectorTranslator.addToVector(pos, deltaPos);

        //Angular Velocity
        VectorTranslator.addToVector(angularVelocity,angularAccelAccumulator);
        Arrays.fill(angularAccelAccumulator,0);
        QuaternionTranslator.addAngularVelocity(rotationQ4, angularVelocity,deltaT,buffers[0]);
        QuaternionTranslator.copy(buffers[0],rotationQ4);

        QuaternionTranslator.rotationMatrixFromQuaternion(rotationQ4,rotationMatrix);
        MatrixTranslator.transposeMat4(rotationMatrix,inverseRotationMatrix); // The inverse of a rotation matrix is it's transpose, much quicker to calculate :)

        // Inverse Inertial Tensor in world rotation
        // I[w] = R x I x R^(-1)
        MatrixTranslator.multiply4x4Matrices(rotationMatrix,inverseInertialTensor,rotatedInverseInertiaTensor);
        MatrixTranslator.multiply4x4Matrices(rotatedInverseInertiaTensor,inverseRotationMatrix);
        // TODO se isso não funcionar, calcula o inverso depois de rotar o tensor
        //  talvez manter ambos os tensores não seja uma má ideia

//        System.out.println("rotationQ4");
//        VectorTranslator.debugVector(rotationQ4);
//        float[] axis = new float[4];
//        System.out.println("rotationAxis");
//        VectorTranslator.debugVector(axis);
//        System.out.println("rotationMatrix");
//        MatrixTranslator.debugMatrix4x4(rotationMatrix);
//        System.out.println("inverseInertialTensor");
//        MatrixTranslator.debugMatrix4x4(inverseInertialTensor);
//        System.out.println("rotatedInverseInertiaTensor");
//        MatrixTranslator.debugMatrix4x4(rotatedInverseInertiaTensor);
//        System.exit(0);

    }

    @Override
    public void setCoordinates(float x, float y, float z){
        pos[0] = x; pos[1] = y; pos[2] = z;
    }

    @Override
    public void setVelocity(float vx, float vy, float vz){
        velocity[0] = vx; velocity[1] = vy; velocity[2] = vz;
    }

    @Override
    public void applyAcceleration(float ax, float ay, float az) {
        accelerationAccumulator[0] += ax;
        accelerationAccumulator[1] += ay;
        accelerationAccumulator[2] += az;
    }

    @Override
    public void applyVelocity(float vx, float vy, float vz){
        velocity[0] += vx; velocity[1] += vy; velocity[2] += vz;
    }

    @Override
    public void applyTranslation(float x, float y, float z) {
        pos[0] += x; pos[1] += y; pos[2] += z;
    }

    public void applyTranslationAtNextUpdate(float x, float y, float z){
        translationAccumulator[0] += x;
        translationAccumulator[1] += y;
        translationAccumulator[2] += z;
    }

    public float x(){return pos[0];}
    public float y(){return pos[1];}
    public float z(){return pos[2];}
    public void getPos(@Modified float[] dest){
        System.arraycopy(pos,0,dest,0,3);
    }

    public float vx(){return velocity[0];}
    public float vy(){return velocity[1];}
    public float vz(){return velocity[2];}
    public void getVelocity(@Modified float[] dest){
        System.arraycopy(velocity,0,dest,0,3);
    }

    public void toLocalCoordinateSystem(@Modified float[] vec3) {
        VectorTranslator.subtractFromVector(vec3,pos);
        MatrixTranslator.multiplyVec4Mat4(vec3,inverseRotationMatrix);
    }

    public void toGlobalCoordinateSystem(@Modified float[] vec3) {
        MatrixTranslator.multiplyVec4Mat4(vec3,rotationMatrix);
        VectorTranslator.addToVector(vec3,pos);
    }

    private final float[] pBuffer = new float[4];
    private final float[] fBuffer = new float[4];
    @Override
    public void applyForce(float fx, float fy, float fz, float px, float py, float pz) {
        VectorTranslator.copy(px,py,pz,pBuffer);
        VectorTranslator.copy(fx,fy,fz,fBuffer);

        VectorTranslator.subtractFromVector(pBuffer,pos);
        // Presumindo que este seja nosso centro de massa (p.197)
        // a posição do ponto é relativa ao centro de massa,
        // a rotação é no sistema de coordenadas global

        VectorTranslator.crossProduct(pBuffer,fBuffer);
        applyForce(fBuffer);
        applyTorque(pBuffer);
        // Todas as engines que eu olhei o código fazem exatamente assim, isso na minha cabeça
        // não computa, como pode a mesma força sempre trazer a mesma mudança de velocidade
        // enquanto simultaneamente trazer torques com módulos diferentes??
    }

    private final float[] rotBuffer = new float[4];
    private final float[] tBuffer = new float[4];
    /**
     * Translates the point p (world coordinates) by d units,
     * taking into account the object's distribution of mass
     * (aka. it's inertial tensor).
     * */
    public void applyRotationalTranslation(float dx, float dy, float dz, float px, float py, float pz){
        applyTranslation(dx,dy,dz);
        // TODO isso só funciona se A MATEMÁTICA INERCIAL ESTIVER FUNCIONANDO >:(
        // FIXME Deus está morto
    }

    float[] angularImpulseBuffer = new float[4];
    public void applyAngularImpulse(float iX, float iY, float iZ){
        // TODO talvez a tradução de tensor inercial por rotação esteja errada
        //  recomendo comparar com o met[h]od(o) da cyclone e ver se bate por meio
        //  de algum teste
        VectorTranslator.copy(iX,iY,iZ, angularImpulseBuffer);
        MatrixTranslator.multiplyVec4Mat4(angularImpulseBuffer, rotatedInverseInertiaTensor);
        // não daria pra verificar se o tensor inercial reverso faz o trabalho dele direito
        //  tentando rotar o vetor em vez do tensor e comparando os resultados?
        VectorTranslator.addToVector(angularVelocity,angularImpulseBuffer);
    }

    private final float[] rBuffer = new float[4];
    public void applyRotation(float rx, float ry, float rz){
        VectorTranslator.copy(rx,ry,rz,rBuffer);
        QuaternionTranslator.addAngularVelocity(rotationQ4,rBuffer,1,buffers[1]);//fixme
        QuaternionTranslator.copy(buffers[1],rotationQ4);
    }

    public void applyRotation(float[] rotation){
        applyRotation(rotation[0],rotation[1],rotation[2]);
    }

    float[] torqueBuffer = new float[4];
    @Override
    public void applyTorque(float tx, float ty, float tz) {
        // TODO isso tem comportamento irregular, o tensor inercial talvez esteja sendo
        //  traduzido de forma errada
        VectorTranslator.copy(tx,ty,tz,torqueBuffer);
        MatrixTranslator.multiplyVectorMatrix(torqueBuffer, rotatedInverseInertiaTensor);
        VectorTranslator.addToVector(angularAccelAccumulator,torqueBuffer);
    }

    @Override
    public void toLocalVelocity(float[] point, float[] pointVelocity, @Modified float[] dest) {
        dest[0] = pointVelocity[0] - velocity[0];
        dest[1] = pointVelocity[1] - velocity[1];
        dest[2] = pointVelocity[2] - velocity[2];
    }

    float[] angularImpulse = BufferManager.allocateVec4StaticContext(0,0,0,0);
    float[] linearImpulse = BufferManager.allocateVec4StaticContext(0,0,0,0);
    float[] impulsePoint = BufferManager.allocateVec4StaticContext(0,0,0,0);
    @Override
    public void applyImpulse(float ix, float iy, float iz, float px, float py, float pz) {
        VectorTranslator.copy(px,py,pz,impulsePoint);
        VectorTranslator.copy(ix,iy,iz,linearImpulse);

        VectorTranslator.subtractFromVector(impulsePoint,pos);
        // Presumindo que este seja nosso centro de massa (p.197)
        // a posição do ponto é relativa ao centro de massa,
        // a rotação é no sistema de coordenadas global

        VectorTranslator.crossProduct(impulsePoint,linearImpulse,angularImpulse);
        applyImpulse(linearImpulse);
        applyAngularImpulse(angularImpulse);
    }



    public float getMass() {
        return mass;
    }

    public float getInverseMass() {
        return invMass;
    }

    @Override
    public void toGlobalVelocity(float[] point, float[] pointVelocity, float[] dest) {
        //todo
    }

    @Override
    public void getRotationMatrix(float[] m4) {
        MatrixTranslator.copy(rotationMatrix,m4);
    }

    @Override
    public void getRotationQuaternion(float[] q4) {
        QuaternionTranslator.copy(rotationQ4,q4);
    }

    @Override
    public void getLocalPointVelocity(float[] point, @Modified float[] dest) {
        // point e dest estão em world coordinates
        VectorTranslator.subtractFromVector(point,pos, pointVelBuffer);
        VectorTranslator.crossProduct(angularVelocity, pointVelBuffer,dest);
        VectorTranslator.addToVector(dest,velocity);
    }

    public void setInertialTensor(float[] inertialTensor){
        MatrixTranslator.copy(inertialTensor,this.inertialTensor);
        MatrixTranslator.getInverseMatrix4(inertialTensor,this.inverseInertialTensor);
    }

    public void setInverseInertialTensor(float[] inverseInertialTensor){
        MatrixTranslator.copy(inverseInertialTensor,this.inverseInertialTensor);
        MatrixTranslator.getInverseMatrix4(inverseInertialTensor,this.inertialTensor);
    }

    @Override
    public void getInertialTensor(@Modified float[] dest) {
        MatrixTranslator.copy(inertialTensor,dest);
    }

    @Override
    public void getInverseInertialTensor(@Modified float[] dest) {
        MatrixTranslator.copy(inverseInertialTensor,dest);
    }

    private final float[] pointVelBuffer = new float[4];

    public void setAngularVelocity(float x, float y, float z) {
        VectorTranslator.copy(x,y,z,angularVelocity);
    }

    float[] axisBuffer = new float[4];
    public void setOrientationAxis(float x, float y, float z){
        VectorTranslator.copy(x,y,z,axisBuffer);
        QuaternionTranslator.axisAngleToQuaternion(axisBuffer,rotationQ4);
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void debugProperties(){
        //TODO
    }
}
