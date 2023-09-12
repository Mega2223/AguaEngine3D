package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.DisplayComponentShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.ShaderProgram;

public class InterfaceComponent extends Model{

    DisplayComponentShaderProgram shaderProgram;

    public InterfaceComponent(float[] vertices, int[] indexes, int texture,float aspectRatio) {
        super(vertices, indexes, new DisplayComponentShaderProgram(texture, aspectRatio));
        shaderProgram = (DisplayComponentShaderProgram) getShader();
    }
}
