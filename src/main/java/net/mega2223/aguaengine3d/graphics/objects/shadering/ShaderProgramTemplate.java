package net.mega2223.aguaengine3d.graphics.objects.shadering;

import net.mega2223.aguaengine3d.graphics.objects.LightspaceRenderingManager;
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

    protected int[] shadowEnableBoolLoc = new int[MAX_LIGHTS];
    protected int[] lightSpacePositions = new int[MAX_LIGHTS];
    protected int[] lightSpaceTranslations = new int[MAX_LIGHTS];
    protected int[] shadowmapSamplers = new int[MAX_LIGHTS];

    @Override
    public void initUniforms() {
        projectionMatrixLocation = GL30.glGetUniformLocation(getID(),"projection");
        translationMatrixLocation = GL30.glGetUniformLocation(getID(),"translation");
        rotationMatrixLocation = GL30.glGetUniformLocation(getID(),"rotation");
        itnerationLocation = GL30.glGetUniformLocation(getID(),"itneration");
        GL30.glUseProgram(getID());
        //fixme this may be messing up the depth display shader somehow
        for (int i = 0; i < MAX_LIGHTS; i++) {
            shadowEnableBoolLoc[i] = GL30.glGetUniformLocation(getID(),"doShadowMapping["+i+"]");
            lightSpacePositions[i] = GL30.glGetUniformLocation(getID(),"lightspace_projections["+i+"]");
            lightSpaceTranslations[i] = GL30.glGetUniformLocation(getID(),"lightspace_translations["+i+"]");
            shadowmapSamplers[i] = GL30.glGetUniformLocation(getID(),"shadowmaps["+i+"]");
            GL30.glUniform1i(shadowmapSamplers[i], LightspaceRenderingManager.FIRST_TEXTURE_LIGHTMAP_LOC+i);
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
        return lightSpacePositions;
    }
}
