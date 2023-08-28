package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.RenderingManager;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;

import static net.mega2223.lwjgltest.aguaengine3d.graphics.utils.RenderingManager.drawnIndexBufferVBO;

public class Model {

    public static final int SHADER_VERTEX_DATA_LOCATION = 0;
    public static final int SHADER_TEXTURE_DATA_LOCATION = 1;
    public static final int SHADER_NORMALS_LOCATION = 2;

    protected float[] vertices; //each vertex has 4 attributes
    protected float[] coords = {0,0,0,0};
    protected int[] indexes;
    protected float[] normals = null;//should've done this way before lol, normals are 3d vectors, no 4th coord

    protected ShaderProgram shader;

    protected int verticesVBO = -1;
    protected int indicesVBO = -1;
    protected int textureCoordsVBO = -1;
    protected int normalsVBO = -1;

    public Model(float[] vertices, int[] indexes, ShaderProgram shader){
        this.setVertices(vertices);
        this.setIndexes(indexes);
        this.setShader(shader);
        genNormals();
    }

    public Model(float[] vertices, int[] indexes, ShaderProgram shader, float[] normals){
        this.setVertices(vertices);
        this.setIndexes(indexes);
        this.setShader(shader);
        this.setNormals(normals);
    }

    public static Model loadModel(String[] objData, ShaderProgram shader){

        boolean modelHasNormals = false; //either a model has normals on ALL faces, or in no faces whatsoever


        ArrayList<String> existingVerticeCombinations = new ArrayList<>();
        ArrayList<Float> vertices = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();
        ArrayList<Float> normals = new ArrayList<>();

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
            else if(type.equalsIgnoreCase("vn")&&split.length==4){
                modelHasNormals = true;
                normals.add(Float.parseFloat(split[1]));
                normals.add(Float.parseFloat(split[2]));
                normals.add(Float.parseFloat(split[3]));
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
        float[] normalsData = new float[existingVerticeCombinations.size()*3];
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

            if(modelHasNormals){ //otherwise, this would throw an ArrayOutOfBoundsEx
                for (int j = 0; j < 3; j++) {
                    normalsData[(i*3)+j] = normals.get((combination[2]-1)*3+j);
                }
            }
        }

        for (int i = 0; i < indData.length; i++) {
            indData[i]=indices.get(i);
        }

        if(modelHasNormals){
            return new Model(vertData,indData,shader,normalsData);
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

    public int[] getIndexes() {
        return indexes;
    }

    public void setIndexes(int[] indexes) {
        this.indexes = indexes.clone();
        unloadVBOS();
    }

    protected void initVBOS(){

        int verticeVBO = RenderingManager.genArrayBufferObject(vertices, GL30.GL_DYNAMIC_DRAW);
        int indicesVBO = RenderingManager.genIndexBufferObject(indexes, GL30.GL_DYNAMIC_DRAW);
        int normalsVBO = RenderingManager.genArrayBufferObject(normals,GL30.GL_DYNAMIC_DRAW);
        this.setVerticesVBO(verticeVBO);
        this.setIndicesVBO(indicesVBO);
        this.setNormalsVBO(normalsVBO);
    }


    public void unloadVBOS(){
        GL30.glDeleteBuffers(getIndicesVBO());
        GL30.glDeleteBuffers(getVerticesVBO());
        GL30.glDeleteBuffers(getNormalsVBO());
        GL30.glDeleteBuffers(getTextureCoordsVBO());
        setIndicesVBO(-1);
        setVerticesVBO(-1);
        setNormalsVBO(-1);
        setTextureCoordsVBO(-1);
    }
    //todo remove normals entirely from scene drawing logic, it's literally useless
    public void draw(){
        if(!areVBOSInitialized()){
            initVBOS();
        }
        shader.preRenderLogic();
        //set normals data, each normal is a 3D vector representing a vertex normal
        GL30.glEnableVertexAttribArray(SHADER_NORMALS_LOCATION);
        GL30.glVertexAttribPointer(SHADER_NORMALS_LOCATION,3,GL30.GL_FLOAT,false,0,0L);
        GL30.glUseProgram(this.shader.getID());
        drawnIndexBufferVBO(getVerticesVBO(),GL30.GL_TRIANGLES,4,this.shader, getIndicesVBO(), indexes.length);
        GL30.glDisableVertexAttribArray(SHADER_NORMALS_LOCATION);
        shader.postRenderLogic();
    }

    public void drawForceShader(ShaderProgram shader){
        if(!areVBOSInitialized()){
            initVBOS();
        }
        shader.preRenderLogic();
        GL30.glUseProgram(shader.getID());
        drawnIndexBufferVBO(getVerticesVBO(),GL30.GL_TRIANGLES,4,shader, getIndicesVBO(), indexes.length);
        shader.postRenderLogic();
    }

    //for subclasses
    public void doLogic(int itneration){

    }

    public float[] getNormals() {
        return normals.clone();
    }

    public void setNormals(float[] normals) {
        this.normals = normals;
    }

    public boolean areVBOSInitialized(){
        return getVerticesVBO() != -1 && getIndicesVBO() != -1 && getNormalsVBO() != -1;
    }

    protected void genNormals(){//todo
        normals = new float[vertices.length - (vertices.length/4)];

        //3/4ths relation

    }

    public float[] getCoords(){
        return coords.clone();
    }
    public void setCoords(float[] coords){
        for (int i = 0; i < this.coords.length; i++) {this.coords[i] = coords[i];}
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

    public int getNormalsVBO() {
        return normalsVBO;
    }

    public void setNormalsVBO(int normalsVBO) {
        this.normalsVBO = normalsVBO;
    }
}
