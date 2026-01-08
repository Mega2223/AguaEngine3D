package net.mega2223.aguaengine3d.graphics.objects.misc;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Mesh;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ModelUtils;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.shadering.SolidColorShaderProgram;

public class Point implements Renderable {
    // todo extends model seria melhor
    float radius;
    float[] pos = new float[4];
    ShaderProgram shader;
    Model model;

    public Point(float radius, float x, float y, float z, float r, float g, float b, float a) {
        shader = new SolidColorShaderProgram(r,g,b);
        model = Mesh.CUBE.toModel(shader);
        float[] relativeVertices = model.getRelativeVertices();
        ModelUtils.scaleAllVertices(relativeVertices,radius);
        model.setVertices(relativeVertices);
        model.setCoords(x,y,z);
    }

    public Point(float radius, float x, float y, float z, float r, float g, float b) {
        this(radius,x,y,z,r,g,b,1F);
    }

    public Point(float radius, float x, float y, float z){
        this(radius,x,y,z,1,0,0);
    }

    //TODO o over fez um modelo de esfera
    @Override
    public void draw() {
        model.draw();
    }

    @Override
    public void drawForceShader(ShaderProgram shader) {
        model.drawForceShader(shader);
    }

    @Override
    public void doLogic(int iteration) {
        model.doLogic(iteration);
    }

    @Override
    public void setUniforms(int iteration, float[] projectionMatrix) {
        model.setUniforms(iteration, projectionMatrix);
    }

    @Override
    public ShaderProgram getShader() {
        return shader;
    }

    @Override
    public int getRenderOrderPosition() {
        return -5;
    }
}
