package net.mega2223.aguaengine3d.graphics.objects.misc;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.shadering.SolidColorShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import org.lwjgl.opengl.GL30;

public class Line implements Renderable {

    final SolidColorShaderProgram shaderProgram;
    final float[] vertices = new float[8];
    final float[] coords = new float[4];

    private static final float[] transMatBuffer = new float[16];

    int verticesVBO = -1;
    public Line(float r, float g, float b){
        shaderProgram = new SolidColorShaderProgram(r,g,b);
        updateVerticesVBO();
    }

    public void setStart(float[] vec3){
        setStart(vec3[0], vec3[1], vec3[2]);
    }

    public void setStart(float x, float y, float z){
        vertices[0] = x; vertices[1] = y; vertices[2] = z;
        updateVerticesVBO();
    }

    public void setEnd(float[] vec3){
        setEnd(vec3[0],vec3[1],vec3[2]);
    }

    public void setEnd(float x, float y, float z){
        vertices[4] = x; vertices[5] = y; vertices[6] = z;
        updateVerticesVBO();
    }

    void updateVerticesVBO(){
        if(verticesVBO == -1){
            verticesVBO = GL30.glGenBuffers();
        }
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER,verticesVBO);//fixme this keeps throwing a 0X502 error at my face for some reason
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER,vertices,GL30.GL_DYNAMIC_DRAW);
    }

    @Override
    public void draw() {
        drawForceShader(shaderProgram);
    }

    @Override
    public void drawForceShader(ShaderProgram shader) {
        shader.preRenderLogic(); // <- these are kinda redundant since SolidColorShaderProgram does not do anything with the pre or post logic
        RenderingManager.drawArrayVBO(verticesVBO,GL30.GL_LINES,4,shader,vertices.length);
        shader.postRenderLogic();
    }

    @Override
    public void doLogic(int iteration) {

    }

    @Override
    public void setUniforms(int iteration, float[] projectionMatrix) {
        MatrixTranslator.generateTranslationMatrix(coords[0], coords[1], coords[2], transMatBuffer);
        shaderProgram.setUniforms(iteration,transMatBuffer,projectionMatrix);
    }

    @Override
    public ShaderProgram getShader() {
        return shaderProgram;
    }
}
