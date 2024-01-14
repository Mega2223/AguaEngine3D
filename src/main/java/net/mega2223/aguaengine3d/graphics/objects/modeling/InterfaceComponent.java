package net.mega2223.aguaengine3d.graphics.objects.modeling;

import net.mega2223.aguaengine3d.graphics.objects.shadering.DisplayComponentShaderProgram;

public class InterfaceComponent extends Model{

    final DisplayComponentShaderProgram shaderProgram;

    float[] textureCoords;

    public InterfaceComponent(float[] vertices, int[] indexes, float[] textureCoords, int texture,float aspectRatio, DisplayComponentShaderProgram shaderProgram) {
        super(vertices, indexes, shaderProgram);
        this.shaderProgram = (DisplayComponentShaderProgram) getShader();
        this.textureCoords = textureCoords;
    }

    public InterfaceComponent(float[] vertices, int[] indexes, float[] textureCoords, int texture,float aspectRatio) {
        super(vertices, indexes, new DisplayComponentShaderProgram(texture, textureCoords,aspectRatio));
        shaderProgram = (DisplayComponentShaderProgram) getShader();
        this.textureCoords = textureCoords;
    }

    public InterfaceComponent(InterfaceComponent component, DisplayComponentShaderProgram shaderProgram){
        this(component.vertices,component.indices,component.textureCoords,component.shaderProgram.getTexture(),component.getAspectRatio(), shaderProgram);
    }

    public InterfaceComponent(InterfaceComponent component){
        this(component.vertices,component.indices,component.textureCoords,component.shaderProgram.getTexture(),component.getAspectRatio());
    }

    public void setAspectRatio(float ar){
        shaderProgram.setAspectRatio(ar);
    }

    public float getAspectRatio(){
        return shaderProgram.getAspectRatio();
    }

    public DisplayComponentShaderProgram getCastShader(){
        return shaderProgram;
    }
}
