package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering;

import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;

/**
 * Not supposed to be used for standard rendering, does not support lighting.
 * */
public class DepthBufferShaderProgram implements ShaderProgram{
    int id = -1;
    int rotationMatrixLoc = -1;
    int translationMatrixLoc = -1;
    int projectionMatrixLoc = -1;

    public static final DepthBufferShaderProgram GLOBAL_INSTANCE = new DepthBufferShaderProgram();

    final int[] FBO;

    public DepthBufferShaderProgram(){
        this(RenderingManager.genDepthFrameBufferObject(128,128));
    }
    public DepthBufferShaderProgram(int[] FBO){
        id = ShaderManager.loadShaderFromFiles(new String[]{
                Utils.SHADERS_DIR + "\\DepthAlgVertShader.vsh",
                Utils.SHADERS_DIR + "\\DepthAlgFragShader.fsh"}
        );
        this.FBO = FBO;
        initUniforms();
    }

    public int[] getFBO(){
        return FBO.clone();
    }
    public int getFBOBuffer(){
        return FBO[0];
    }
    public int getFBOTexture(){
        return FBO[1];
    }
    public int getFBORenderBuffer(){
        return FBO[2];
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void initUniforms() {
        GL30.glUseProgram(id);
        rotationMatrixLoc = GL30.glGetUniformLocation(id,"rotation");
        translationMatrixLoc = GL30.glGetUniformLocation(id,"translation");
        projectionMatrixLoc = GL30.glGetUniformLocation(id,"projection");
    }

    @Override
    public void setUniforms(int interation, float[] translationMatrix, float[] projectionMatrix) {
        GL30.glUseProgram(id);
        GL30.glUniformMatrix4fv(projectionMatrixLoc,false,projectionMatrix);
        GL30.glUniformMatrix4fv(translationMatrixLoc,false,translationMatrix);
    }

    @Override
    public void setRotationMatrix(float[] m4) {
        GL30.glUseProgram(id);
        GL30.glUniformMatrix4fv(rotationMatrixLoc,false,m4);
    }

    @Override
    public void setRenderShadows(int index, boolean s) {
        //do nothing
    }

    @Override
    public int[] getLightspaceTextureLocs() {
        return new int[0]; //do nothing
    }


    @Override
    public void setLights(float[][] lights) {
        //do nothing
    }

    @Override
    public void setLight(int index, float x, float y, float z, float brightness) {
        //do nothing
    }

    @Override
    public void setLightColor(int index, float r, float g, float b, float influence) {
        //do nothing
    }
}
