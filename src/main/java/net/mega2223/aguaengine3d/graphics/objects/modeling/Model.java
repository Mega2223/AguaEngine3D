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
    protected float[] normals; //each vertex has a normal
    protected float[] coords = {0,0,0,0};
    protected int[] indices;

    protected ShaderProgram shader;

    protected int vertexVBO = -1;
    protected int indicesVBO = -1;
    protected int textureCoordsVBO = -1;
    protected int normalsVBO = -1;

    protected static final float[] bufferTranslationMatrix = new float[16];

    public Model(float[] vertices, int[] indices, float[] normals, ShaderProgram shader){
        this.vertices = vertices;
        this.indices = indices;
        this.shader = shader;
        this.normals = normals;
    }

    public Model(float[] vertices, int[] indices, ShaderProgram shader){
        this(vertices,indices,null,shader);
        this.normals = ModelUtils.generateNormals(this);
    }

    public static Model loadModel(String objData, ShaderProgram shader){
        return loadModel(objData.split("\n"),shader);
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
                    String act = split[j]; act = act.split("/")[0]; //removes the texture coordinate since the Model class does not support them
                    int index = existingVerticeCombinations.indexOf(act);
                    if (index == -1) {
                        existingVerticeCombinations.add(act);
                        index = existingVerticeCombinations.indexOf(act);
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
                if(sp[j].isEmpty()){continue;}
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

    public float[] getNormals() {
        return normals.clone();
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
        int vertexVBO = RenderingManager.genArrayBufferObject(vertices, GL30.GL_STATIC_DRAW);
        int indicesVBO = RenderingManager.genIndexBufferObject(indices, GL30.GL_STATIC_DRAW);
        int normalsVBO = RenderingManager.genArrayBufferObject(normals,GL30.GL_STATIC_DRAW);
        this.setVertexVBO(vertexVBO);
        this.setIndicesVBO(indicesVBO);
        this.normalsVBO = normalsVBO;
        GL30.glUseProgram(shader.getID());
    }

    public void unloadVBOS(){
        GL30.glDeleteBuffers(getIndicesVBO());
        GL30.glDeleteBuffers(getVertexVBO());
        GL30.glDeleteBuffers(getTextureCoordsVBO());
        GL30.glDeleteBuffers(this.normalsVBO);
        setIndicesVBO(-1);
        setVertexVBO(-1);
        setTextureCoordsVBO(-1);
        this.normalsVBO = -1;
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
        GL30.glEnableVertexAttribArray(RenderingManager.NORMAL_DATA_LOC);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER,normalsVBO);
        GL30.glVertexAttribPointer(RenderingManager.NORMAL_DATA_LOC, 4, GL30.GL_FLOAT, false, 0, 0L);
        drawnIndexBufferVBO(getVertexVBO(),GL30.GL_TRIANGLES,4,shader, getIndicesVBO(), indices.length);
        shader.postRenderLogic();
        GL30.glDisableVertexAttribArray(RenderingManager.NORMAL_DATA_LOC);
    }

    //for subclasses
    public void doLogic(int iteration){

    }

    /** @noinspection BooleanMethodIsAlwaysInverted*/
    public boolean areVBOSInitialized(){
        return getVertexVBO() != -1 && getIndicesVBO() != -1;
    }

    public float[] getCoords(){
        return coords.clone();
    }

    public void setCoords(float[] coords){
        System.arraycopy(coords, 0, this.coords, 0, this.coords.length);
    }

    public void setCoords(float x, float y, float z){coords[0] = x; coords[1] = y; coords[2] = z;}

    public int getVertexVBO() {
        return vertexVBO;
    }

    public void setVertexVBO(int verticesVBO) {
        this.vertexVBO = verticesVBO;
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

    public void setUniforms(int iteration, float[] projectionMatrix){
        MatrixTranslator.generateTranslationMatrix(coords, bufferTranslationMatrix);
        shader.setUniforms(iteration,bufferTranslationMatrix,projectionMatrix);
    }

}
