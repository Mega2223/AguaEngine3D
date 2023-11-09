package net.mega2223.aguaengine3d.graphics.objects.modeling;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;

import static net.mega2223.aguaengine3d.graphics.utils.RenderingManager.drawnIndexBufferVBO;

public class Model implements Renderable {

    public static final int SHADER_VERTEX_DATA_LOCATION = 0;
    public static final int SHADER_TEXTURE_DATA_LOCATION = 1;

    protected float[] vertices; //each vertex has 4 attributes
    protected float[] coords = {0,0,0,0};
    protected int[] indices;

    protected ShaderProgram shader;

    protected int verticesVBO = -1;
    protected int indicesVBO = -1;
    protected int textureCoordsVBO = -1;

    protected static final float[] bufferTranslationMatrix = new float[16];

    public Model(float[] vertices, int[] indexes, ShaderProgram shader){
        this.setVertices(vertices);
        this.setIndices(indexes);
        this.setShader(shader);
    }

    public static Model loadModel(String[] objData, ShaderProgram shader){

        ArrayList<String> existingVerticeCombinations = new ArrayList<>();
        ArrayList<Float> vertices = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();

        //extracts vertices and texture coordinates and puts them in their respective arrays
        for (int i = 0; i < objData.length; i++) {
            String[] split = objData[i].split(" ");
            String type = split[0];
            if(type.equalsIgnoreCase("v")&&split.length == 4){
                vertices.add(Float.parseFloat(split[1]));
                vertices.add(Float.parseFloat(split[2]));
                vertices.add(Float.parseFloat(split[3]));
                vertices.add(0f);
                //wavefront only has 3 vertex coordinates, but our model system has 4, last line accounts for the 4th
            }


        }
        if(vertices.size()%4 != 0){
            throw new UnsupportedOperationException("invalid model");
        }

        for (String objDatum : objData) {
            String[] split = objDatum.split(" ");
            String type = split[0];
            if (type.equalsIgnoreCase("f") && split.length == 4) {
                for (int j = 1; j < split.length; j++) {
                    int index = existingVerticeCombinations.indexOf(split[j]);
                    if (index == -1) {
                        existingVerticeCombinations.add(split[j]);
                        index = existingVerticeCombinations.indexOf(split[j]);
                    }
                    indices.add(index);
                }
            }
        }
        float[] vertData = new float[existingVerticeCombinations.size()*4];
        int[] indData = new int[indices.size()];

        for(int i = 0; i<existingVerticeCombinations.size(); i++){
            String[] sp = existingVerticeCombinations.get(i).split("/");
            int[] combination = new int[sp.length];
            for (int j = 0; j < sp.length; j++) {
                if(sp[j].equals("")){continue;}
                combination[j] = Integer.parseInt(sp[j]);
            }
            for (int j = 0; j < 3; j++) {
                vertData[(i*4)+j] = vertices.get((combination[0]-1)*4+j);
            }

        }

        for (int i = 0; i < indData.length; i++) {
            indData[i]=indices.get(i);
        }

        return new Model(vertData,indData,shader);
    }

    public float[] getRelativeVertices() {
        return vertices.clone();
    }

    public void setVertices(float[] vertices) {
        this.vertices = vertices.clone();
        unloadVBOS();
    }

    public ShaderProgram getShader() {
        return shader;
    }

    public void setShader(ShaderProgram shader) {
        this.shader = shader;
    }

    public int[] getIndices() {
        return indices;
    }

    public void setIndices(int[] indices) {
        this.indices = indices.clone();
        unloadVBOS();
    }

    protected void initVBOS(){
        int verticeVBO = RenderingManager.genArrayBufferObject(vertices, GL30.GL_DYNAMIC_DRAW);
        int indicesVBO = RenderingManager.genIndexBufferObject(indices, GL30.GL_DYNAMIC_DRAW);
        this.setVerticesVBO(verticeVBO);
        this.setIndicesVBO(indicesVBO);
    }

    public void unloadVBOS(){
        GL30.glDeleteBuffers(getIndicesVBO());
        GL30.glDeleteBuffers(getVerticesVBO());
        GL30.glDeleteBuffers(getTextureCoordsVBO());
        setIndicesVBO(-1);
        setVerticesVBO(-1);
        setTextureCoordsVBO(-1);
    }

    public void draw(){
        drawForceShader(this.getShader());
    }

    public void drawForceShader(ShaderProgram shader){
        if(!areVBOSInitialized()){
            initVBOS();
        }
        shader.preRenderLogic();
        GL30.glUseProgram(shader.getID());
        drawnIndexBufferVBO(getVerticesVBO(),GL30.GL_TRIANGLES,4,shader, getIndicesVBO(), indices.length);
        shader.postRenderLogic();
    }

    //for subclasses
    public void doLogic(int itneration){

    }

    /** @noinspection BooleanMethodIsAlwaysInverted*/
    public boolean areVBOSInitialized(){
        return getVerticesVBO() != -1 && getIndicesVBO() != -1;
    }

    public float[] getCoords(){
        return coords.clone();
    }
    public void setCoords(float[] coords){
        System.arraycopy(coords, 0, this.coords, 0, this.coords.length);
    }
    public void setCoords(float x, float y, float z){coords[0] = x; coords[1] = y; coords[2] = z;}

    public int getVerticesVBO() {
        return verticesVBO;
    }

    public void setVerticesVBO(int verticesVBO) {
        this.verticesVBO = verticesVBO;
    }

    public int getIndicesVBO() {
        return indicesVBO;
    }

    public void setIndicesVBO(int indicesVBO) {
        this.indicesVBO = indicesVBO;
    }

    public int getTextureCoordsVBO() {
        return textureCoordsVBO;
    }

    public void setTextureCoordsVBO(int textureCoordsVBO) {
        this.textureCoordsVBO = textureCoordsVBO;
    }

    public void setUniforms(int itneration, float[] projectionMatrix){
        MatrixTranslator.generateTranslationMatrix(bufferTranslationMatrix,coords);
        shader.setUniforms(itneration,bufferTranslationMatrix,projectionMatrix);
    }

}
