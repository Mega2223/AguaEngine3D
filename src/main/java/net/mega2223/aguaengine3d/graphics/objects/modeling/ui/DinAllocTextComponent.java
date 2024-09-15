package net.mega2223.aguaengine3d.graphics.objects.modeling.ui;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.shadering.DisplayComponentShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import org.lwjgl.opengl.GL30;

public class DinAllocTextComponent implements Renderable, InterfaceComponent {

    protected int vertexVBO = -1, indexVBO = -1;
    protected int aligment = InterfaceComponent.CENTER_ALIGMENT;

    protected float[] vertices;
    protected int[] indices;
    protected float[] textureCoords;
    protected DisplayComponentShaderProgram displayShaderProgram;
    protected float[] coords = new float[4];
    protected int texture;

    private final float[] translationMatrix = new float[16];
    protected boolean areVBOSvalid = true;

    DinAllocTextComponent(float[] vertices, int[] indices, float[] textureCoords, int texture) {
        this.vertices = vertices; this.indices = indices; this.textureCoords = textureCoords;
        genVBOS();
        this.displayShaderProgram = new DisplayComponentShaderProgram(texture,textureCoords,1F);
        this.texture = texture;
    }

    public static DinAllocTextComponent generate(CharSequence data, BitmapFont font){
        return font.genFromString(data, null);
    }

    public void setText(CharSequence data, BitmapFont font){
        font.genFromString(data, this);
        areVBOSvalid = false;
    }

    protected void genVBOS(){
        indexVBO = RenderingManager.genIndexBufferObject(indices, GL30.GL_DYNAMIC_DRAW);
        vertexVBO = RenderingManager.genArrayBufferObject(vertices,GL30.GL_DYNAMIC_DRAW);
        }

    public void refreshVBOS(){
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER,vertexVBO);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER,vertices,GL30.GL_DYNAMIC_DRAW);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER,indexVBO);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER,indices,GL30.GL_DYNAMIC_DRAW);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER,0);
        displayShaderProgram.setTextureCoords(textureCoords);

        areVBOSvalid = true;
    }

    @Override
    public void draw() {
        drawForceShader(getShader());
    }

    @Override
    public void drawForceShader(ShaderProgram shader){
        if(!areVBOSvalid){refreshVBOS();}

        shader.preRenderLogic();
        GL30.glUseProgram(shader.getID());

        GL30.glEnableVertexAttribArray(RenderingManager.TEXTURE_COORDS_LOC);
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,texture);
        GL30.glEnableVertexAttribArray(1);
        GL30.glVertexAttribPointer(1,2,GL30.GL_FLOAT,false,0,0L);

        RenderingManager.drawnIndexBufferVBO(vertexVBO,GL30.GL_TRIANGLES,4,shader, indexVBO, indices.length);

        shader.postRenderLogic();
        GL30.glDisableVertexAttribArray(RenderingManager.TEXTURE_COORDS_LOC);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,0);
    }

    @Override
    public void doLogic(int iteration) {

    }

    public void setCoords(float ... coords){
        System.arraycopy(coords,0,this.coords,0,this.coords.length);
    }

    @Override
    public void setUniforms(int iteration, float[] projectionMatrix) {
        MatrixTranslator.generateTranslationMatrix(coords, translationMatrix);
        displayShaderProgram.setUniforms(iteration,translationMatrix,projectionMatrix);
    }

    @Override
    public ShaderProgram getShader() {
        return displayShaderProgram;
    }

    @Override
    public float getAspectRatio() {
        return displayShaderProgram.getAspectRatio();
    }

    @Override
    public void setAspectRatio(float aspectRatio) {
        displayShaderProgram.setAspectRatio(aspectRatio);
    }

    @Override
    public void setRenderOrderPosition(int pos) {

    }

    @Override
    public float[] getTextureCoords() {
        return textureCoords.clone();
    }

    @Override
    public void setScale(float x, float y, float z) {
        displayShaderProgram.setScale(x,y,z);
    }

    @Override
    public int getAligment() {
        return aligment;
    }

    @Override
    public void setAligment(int aligment) {
        this.aligment = aligment;
        displayShaderProgram.setAligment(aligment);
    }

    public float[] getVerticeBuffer() {
        return vertices;
    }

    public int[] getIndiceBuffer() {
        return indices;
    }

    public float[] getTextureCoordBuffer(){
        return textureCoords;
    }
}
