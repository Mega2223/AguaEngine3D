package net.mega2223.aguaengine3d.physics.decorators;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.misc.Positionable;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.physics.PhysicsObject;

public class PhysicsObjectDecorator extends PhysicsObject implements Renderable {

    protected Positionable renderable;

    public PhysicsObjectDecorator(float mass, Positionable object) {
        super(mass);
        this.renderable = object;
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
        renderable.setCoords(pos[0], pos[1], pos[2]);
    }

    @Override
    public void setUniforms(int iteration, float[] projectionMatrix) {
        renderable.setUniforms(iteration, projectionMatrix);
    }

    @Override
    public ShaderProgram getShader() {
        return renderable.getShader();
    }
}
