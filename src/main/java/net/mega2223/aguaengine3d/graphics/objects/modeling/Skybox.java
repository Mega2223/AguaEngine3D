package net.mega2223.aguaengine3d.graphics.objects.modeling;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.shadering.CubemapInterpreterShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;

public class Skybox implements Renderable {
    float[] mesh;
    int[] indices;

    ShaderProgram program;
    int texture, verticesVBO, indicesVBO;

    public Skybox(int texture) {
        Model model = TexturedModel.loadTexturedModel(Utils.readFile(Utils.MODELS_DIR+"\\cube.obj").split("\n"),null,texture);
        this.mesh = model.vertices.clone(); this.indices = model.indices.clone();
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

    @Override
    public int getRenderOrderPosition() {
        return -2;
    }
}
