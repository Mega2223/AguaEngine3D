package net.mega2223.aguaengine3d.graphics.objects;

import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;

public interface Renderable {

    void draw();
    void drawForceShader(ShaderProgram shader);
    void doLogic(int itneration);
    void setUniforms(int itneration, float[] projectionMatrix);

}
