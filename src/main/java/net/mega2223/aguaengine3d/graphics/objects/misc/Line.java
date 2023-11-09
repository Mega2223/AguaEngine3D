package net.mega2223.aguaengine3d.graphics.objects.misc;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.shadering.SolidColorShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import org.lwjgl.opengl.GL30;

public class Line implements Renderable {

    final SolidColorShaderProgram shaderProgram;
    final float[] vertices = new float[8];
    int verticesVBO = -1;
    public Line(float r, float g, float b){
        shaderProgram = new SolidColorShaderProgram(r,g,b);
    }

    public void setStart(float x, float y, float z){
        vertices[0] = x; vertices[1] = y; vertices[2] = z;
        updateVerticesVBO();
    }

    public void setEnd(float x, float y, float z){
        vertices[4] = x; vertices[5] = y; vertices[6] = z;
        updateVerticesVBO();
    }

    void updateVerticesVBO(){
        if(verticesVBO == -1){
            verticesVBO = RenderingManager.genArrayBufferObject(vertices, GL30.GL_DYNAMIC_DRAW);
        } else {
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER,verticesVBO);
            GL30.glBufferSubData(GL30.GL_ARRAY_BUFFER,0,vertices);
        }
    }

    @Override
    public void draw() {

    }

    @Override
    public void drawForceShader(ShaderProgram shader) {

    }

    @Override
    public void doLogic(int itneration) {

    }

    @Override
    public void setUniforms(int itneration, float[] projectionMatrix) {

    }
}
