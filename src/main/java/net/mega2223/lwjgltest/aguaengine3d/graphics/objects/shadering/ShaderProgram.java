package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering;

public interface ShaderProgram {

    int getID();

    void initUniforms();//because i forgot to put it once so now all classes must have it
    void setUniforms(int interation, float[] translationMatrix, float[] projectionMatrix);

}
