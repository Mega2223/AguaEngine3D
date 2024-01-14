package net.mega2223.aguaengine3d.graphics.objects.modeling;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.shadering.CubemapInterpreterShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import org.lwjgl.opengl.GL30;

public class Skybox implements Renderable {
    float[] mesh = {-1,-1,-1,0,
            -1,1,-1,0,
            -1,1,1,0,
            -1,-1,1,0,
            1,-1,-1,0,
            1,1,-1,0,
            1,1,1,0,
            1,-1,1,0,};
    int[] indices = {0,1,2,2,3,1,4,5,6,7,6,5};
    ShaderProgram program;
    int texture, verticesVBO, indicesVBO;

    public Skybox(int texture) {
        program = new CubemapInterpreterShaderProgram(texture);
        this.texture = texture;
        this.verticesVBO = RenderingManager.genArrayBufferObject(mesh, GL30.GL_STATIC_DRAW);
        this.indicesVBO = RenderingManager.genIndexBufferObject(indices, GL30.GL_STATIC_DRAW);
    }

    @Override
    public void draw() {
        GL30.glDisable(GL30.GL_DEPTH_TEST);
        program.preRenderLogic();
        RenderingManager.drawnIndexBufferVBO(verticesVBO,GL30.GL_TRIANGLES,4,program,indicesVBO,indices.length);
        program.postRenderLogic();
        GL30.glEnable(GL30.GL_DEPTH_TEST);
    }

    @Override
    public void drawForceShader(ShaderProgram shader) {

    }

    @Override
    public void doLogic(int itneration) {

    }

    private static final float[] translationMatrix = {
            1,0,0,0 , 0,1,0,0 , 0,0,1,0 , 0,0,0,1
    };

    @Override
    public void setUniforms(int itneration, float[] projectionMatrix) {
        program.setUniforms(itneration,translationMatrix,projectionMatrix);
    }

    @Override
    public ShaderProgram getShader() {
        return program;
    }
}
