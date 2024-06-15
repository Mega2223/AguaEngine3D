package net.mega2223.aguaengine3d.graphics.objects.modeling;

import net.mega2223.aguaengine3d.graphics.objects.shadering.DisplayComponentShaderProgram;

public class InterfaceComponent extends Model{

    protected DisplayComponentShaderProgram shaderProgram;

    protected float[] textureCoords;
    protected int renderOrderPosition = -1;

    public InterfaceComponent(float[] vertices, int[] indices, float[] textureCoords, int texture,float aspectRatio, DisplayComponentShaderProgram shaderProgram) {
        super(vertices, indices, shaderProgram);
        this.shaderProgram = (DisplayComponentShaderProgram) getShader();
        this.textureCoords = textureCoords;
    }

    public InterfaceComponent(float[] vertices, int[] indices, float[] textureCoords, int texture,float aspectRatio) {
        super(vertices, indices, new DisplayComponentShaderProgram(texture, textureCoords,aspectRatio));
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

    public void setRenderOrderPosition(int pos){
        this.renderOrderPosition = pos;
    }

    @Override
    public int getRenderOrderPosition() {
        return renderOrderPosition;
    }

    public float getAspectRatio(){
        return shaderProgram.getAspectRatio();
    }

    public float[] getTextureCoords() {
        return textureCoords.clone();
    }

    public void setTextureCoords(float[] textureCoords) {
        this.textureCoords = textureCoords;
        unloadVBOS();
    }

    public DisplayComponentShaderProgram getCastShader(){
        return shaderProgram;
    }
}
