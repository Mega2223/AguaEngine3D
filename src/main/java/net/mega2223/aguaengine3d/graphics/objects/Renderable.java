package net.mega2223.aguaengine3d.graphics.objects;

import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;

public interface Renderable {

    void draw();
    void drawForceShader(ShaderProgram shader);
    void doLogic(int iteration);
    void setUniforms(int iteration, float[] projectionMatrix);
    ShaderProgram getShader(); //ideally this would not be here but RenderingManager needs it

    default int getRenderOrderPosition(){return 0;}
}
