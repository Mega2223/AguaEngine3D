package net.mega2223.aguaengine3d.physics.decorators;

import net.mega2223.aguaengine3d.graphics.objects.misc.Positionable;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.physics.PhysicsObject;

public class PhysicsObjectDecorator<P extends PhysicsObject, R extends Positionable> implements Positionable, PhysicsObject {

    protected R renderable;
    protected P physicsObject;

    public PhysicsObjectDecorator(P physicsObject, R renderable) {
        this.physicsObject = physicsObject;
        this.renderable = renderable;
    }

    @Override
    public void draw() {
        renderable.draw();
    }

    @Override
    public void drawForceShader(ShaderProgram shader) {
        renderable.drawForceShader(shader);
    }

    @Override
    public void doLogic(int iteration) {
        renderable.setCoords(physicsObject.x(), physicsObject.y(), physicsObject.z());
    }

    @Override
    public void setUniforms(int iteration, float[] projectionMatrix) {
        renderable.setUniforms(iteration, projectionMatrix);
    }

    @Override
    public ShaderProgram getShader() {
        return renderable.getShader();
    }

    @Override
    public void applyAcceleration(float ax, float ay, float az) {
        physicsObject.applyAcceleration(ax,ay,az);
    }

    @Override
    public void applyVelocity(float vx, float vy, float vz) {
        physicsObject.applyVelocity(vx,vy,vz);
    }

    @Override
    public void applyTranslation(float x, float y, float z) {
        physicsObject.applyTranslation(x,y,z);
    }

    @Override
    public void setVelocity(float vx, float vy, float vz) {
        physicsObject.setVelocity(vx,vy,vz);
    }

    @Override
    public void setCoordinates(float x, float y, float z) {
        physicsObject.setCoordinates(x,y,z);
    }

    @Override
    public float getMass() {
        return physicsObject.getMass();
    }

    @Override
    public float getInverseMass() {
        return physicsObject.getInverseMass();
    }

    @Override
    public void update(float deltaT) {
        physicsObject.update(deltaT);
    }

    @Override
    public float x() {
        return physicsObject.x();
    }

    @Override
    public float y() {
        return physicsObject.y();
    }

    @Override
    public float z() {
        return physicsObject.z();
    }

    @Override
    public void setCoords(float x, float y, float z) {
        setCoordinates(x,y,z);
    }

    @Override
    public void setCoords(float[] coords) {
        setCoords(coords[0],coords[1],coords[2]);
    }

    @Override
    public void getCoords(float[] result) {
        Positionable.super.getCoords(result);
    }

    @Override
    public float vx() {
        return physicsObject.vx();
    }

    @Override
    public float vy() {
        return physicsObject.vy();
    }

    @Override
    public float vz() {
        return physicsObject.vz();
    }

    @Override
    public PhysicsObject getActor() {
        return this.physicsObject;
    }
}
