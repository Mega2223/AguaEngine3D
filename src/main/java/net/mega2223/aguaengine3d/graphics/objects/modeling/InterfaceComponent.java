package net.mega2223.aguaengine3d.graphics.objects.modeling;

import net.mega2223.aguaengine3d.graphics.objects.shadering.DisplayComponentShaderProgram;

public class InterfaceComponent extends Model{

    protected DisplayComponentShaderProgram displayShader;

    protected float[] textureCoords;
    protected int renderOrderPosition = -1;

    public InterfaceComponent(float[] vertices, int[] indices, float[] textureCoords, int texture,float aspectRatio, DisplayComponentShaderProgram displayShader) {
        super(vertices, indices, displayShader);
        this.displayShader = (DisplayComponentShaderProgram) getShader();
        this.textureCoords = textureCoords;
    }

    public InterfaceComponent(float[] vertices, int[] indices, float[] textureCoords, int texture,float aspectRatio) {
        super(vertices, indices, new DisplayComponentShaderProgram(texture, textureCoords,aspectRatio));
        displayShader = (DisplayComponentShaderProgram) getShader();
        this.textureCoords = textureCoords;
    }

    public InterfaceComponent(InterfaceComponent component, DisplayComponentShaderProgram displayShader){
        this(component.vertices,component.indices,component.textureCoords,component.displayShader.getTexture(),component.getAspectRatio(), displayShader);
    }

    public InterfaceComponent(InterfaceComponent component){
        this(component.vertices,component.indices,component.textureCoords,component.displayShader.getTexture(),component.getAspectRatio());
        this.renderOrderPosition = component.renderOrderPosition;
    }

    public void setAspectRatio(float ar){
        displayShader.setAspectRatio(ar);
    }

    public void setRenderOrderPosition(int pos){
        this.renderOrderPosition = pos;
    }

    @Override
    public int getRenderOrderPosition() {
        return renderOrderPosition;
    }

    public float getAspectRatio(){
        return displayShader.getAspectRatio();
    }

    public float[] getTextureCoords() {
        return textureCoords.clone();
    }

    public void setTextureCoords(float[] textureCoords) {
        this.textureCoords = textureCoords;
        unloadVBOS();
    }

    public DisplayComponentShaderProgram getCastShader(){
        return displayShader;
    }
}
