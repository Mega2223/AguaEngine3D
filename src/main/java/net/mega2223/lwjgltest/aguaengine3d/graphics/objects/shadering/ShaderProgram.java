package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering;

import org.lwjgl.opengl.GL30;

public interface ShaderProgram {

    int getID();

    void initUniforms();//because i forgot to put it once so now all classes must have it
    void setUniforms(int interation, float[] translationMatrix, float[] projectionMatrix);

    default void uniformVec4(int location,float[] v4){
        GL30.glUseProgram(getID());
        GL30.glUniform4f(location,v4[0],v4[1],v4[2],v4[3]);
    };
}
