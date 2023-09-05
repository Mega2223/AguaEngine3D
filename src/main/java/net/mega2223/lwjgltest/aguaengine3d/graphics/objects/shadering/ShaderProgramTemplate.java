package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering;

import org.lwjgl.opengl.GL30;

public abstract class ShaderProgramTemplate implements ShaderProgram{

    protected int id;
    @Override
    public int getID() {
        return id;
    }

    protected int projectionMatrixLocation = -1;
    protected int translationMatrixLocation = -1;
    protected int rotationMatrixLocation = -1;
    protected int itnerationLocation = -1;

    protected int[] shadowEnableBoolLoc = new int[ShaderProgram.MAX_LIGHTS];
    protected int[] lightSpaceTextureLocs = new int[ShaderProgram.MAX_LIGHTS];

    @Override
    public void initUniforms() {
        projectionMatrixLocation = GL30.glGetUniformLocation(getID(),"projection");
        translationMatrixLocation = GL30.glGetUniformLocation(getID(),"translation");
        rotationMatrixLocation = GL30.glGetUniformLocation(getID(),"rotation");
        itnerationLocation = GL30.glGetUniformLocation(getID(),"itneration");
        for (int i = 0; i < ShaderProgram.MAX_LIGHTS; i++) {
            shadowEnableBoolLoc[i] = GL30.glGetUniformLocation(getID(),"doShadowMapping["+i+"]");
            lightSpaceTextureLocs[i] = GL30.glGetUniformLocation(getID(),"lightspace_positions["+i+"]");
        }
    }

    @Override
    public void setUniforms(int interation, float[] translationMatrix, float[] projectionMatrix) {
        GL30.glUseProgram(getID());
        GL30.glUniformMatrix4fv(translationMatrixLocation,false,translationMatrix);
        GL30.glUniformMatrix4fv(projectionMatrixLocation,false,projectionMatrix);
        GL30.glUniform1i(itnerationLocation,interation);
    }

    public void setRotationMatrix(float[] m4){
        GL30.glUseProgram(getID());
        GL30.glUniformMatrix4fv(rotationMatrixLocation,false,m4);
    }

    @Override
    public void setLights(float[][] lights) {
        ShaderProgram.super.setLights(lights);
    }

    //todo save light uniforms, perhaps remove ShaderProgram entirely?
    @Override
    public void setLight(int index, float x, float y, float z, float brightness) {
        ShaderProgram.super.setLight(index, x, y, z, brightness);
    }

    @Override
    public void setLightColor(int index, float r, float g, float b, float influence) {
        ShaderProgram.super.setLightColor(index, r, g, b, influence);
    }

    @Override
    public void setRenderShadows(int index, boolean s) {
        int should = s? 1:0;
        GL30.glUniform1i(shadowEnableBoolLoc[index],should);
    }

    @Override
    public int[] getLightspaceTextureLocs() {
        return lightSpaceTextureLocs;
    }
}
