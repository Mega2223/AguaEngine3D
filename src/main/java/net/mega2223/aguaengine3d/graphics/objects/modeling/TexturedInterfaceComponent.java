package net.mega2223.aguaengine3d.graphics.objects.modeling;

import net.mega2223.aguaengine3d.graphics.objects.modeling.ui.InterfaceComponent;
import net.mega2223.aguaengine3d.graphics.objects.shadering.DisplayComponentShaderProgram;

public class TexturedInterfaceComponent extends Model implements InterfaceComponent {

    protected DisplayComponentShaderProgram displayShader;

    protected float[] textureCoords;
    protected int renderOrderPosition = -1;
    protected float scale = 1F;
    protected int aligment = 0;



    public TexturedInterfaceComponent(float[] vertices, int[] indices, float[] textureCoords, int texture, float aspectRatio, DisplayComponentShaderProgram displayShader) {
        super(vertices, indices, displayShader);
        this.displayShader = (DisplayComponentShaderProgram) getShader();
        this.textureCoords = textureCoords;
    }

    public TexturedInterfaceComponent(float[] vertices, int[] indices, float[] textureCoords, int texture, float aspectRatio) {
        super(vertices, indices, new DisplayComponentShaderProgram(texture, textureCoords,aspectRatio));
        displayShader = (DisplayComponentShaderProgram) getShader();
        this.textureCoords = textureCoords;
    }

    public TexturedInterfaceComponent(TexturedInterfaceComponent component, DisplayComponentShaderProgram displayShader){
        this(component.vertices,component.indices,component.textureCoords,component.displayShader.getTexture(),component.getAspectRatio(), displayShader);
    }

    public TexturedInterfaceComponent(TexturedInterfaceComponent component){
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

    @Override
    public void setCoords(float[] coords) {
        setCoords(coords[0],coords[1],coords[2]);
    }

    @Override
    public void setCoords(float x, float y, float z) {
        super.setCoords(x,y,z);
    }

    public void setScale(float f){
        setScale(f,f,f);
    }

    public void setScale(float x,float y,float z){
        displayShader.setScale(x,y,z);
    }

    public DisplayComponentShaderProgram getCastShader(){
        return displayShader;
    }

    public int getAligment() {
        return aligment;
    }

    public void setAligment(int aligment) {
        this.aligment = aligment;
        displayShader.setAligment(aligment);
    }
}
