package net.mega2223.aguaengine3d.graphics.objects.modeling;

import net.mega2223.aguaengine3d.graphics.objects.shadering.DisplayComponentShaderProgram;

public class InterfaceComponent extends Model{

    final DisplayComponentShaderProgram shaderProgram;

    public InterfaceComponent(float[] vertices, int[] indexes, float[] textureCoords, int texture,float aspectRatio) {
        super(vertices, indexes, new DisplayComponentShaderProgram(texture, textureCoords,aspectRatio));
        shaderProgram = (DisplayComponentShaderProgram) getShader();
    }

    public void setAspectRatio(float ar){
        shaderProgram.setAspectRatio(ar);
    }
}
