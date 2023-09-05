package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering;

import org.lwjgl.opengl.GL30;

public interface ShaderProgram {

    int MAX_LIGHTS = 10;

    int getID();
    int[] getLightspaceTextureLocs();

    void initUniforms();//because i forgot to put it once so now all classes must have it
    void setUniforms(int interation, float[] translationMatrix, float[] projectionMatrix);
    void setRotationMatrix(float[] m4);
    void setRenderShadows(int index,boolean s);

    default void setLights(float[][] lights){
        GL30.glUseProgram(getID());
        for (int i = 0; i < lights.length; i++) {
            int location = GL30.glGetUniformLocation(getID(), "lights[" + i + "]");
            GL30.glUniform4f(location,lights[i][0],lights[i][1],lights[i][2],lights[i][3]);
        }
    }
    default void setLight(int index, float x, float y, float z, float brightness){
        int location = GL30.glGetUniformLocation(getID(), "lights[" + index + "]");
        GL30.glUseProgram(getID());
        GL30.glUniform4f(location,x,y,z,brightness);
    }
    default void setLightColor(int index, float r, float g, float b, float influence){
        int location = GL30.glGetUniformLocation(getID(), "lightColors[" + index + "]");
        GL30.glUseProgram(getID());
        GL30.glUniform4f(location,r,g,b,influence);
    }

    default void preRenderLogic(){}
    default void postRenderLogic(){}
}
