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

        ArrayList<Float> vertices = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < objData.length; i++) {
            String[] split = objData[i].split(" ");
            if(split[0].equalsIgnoreCase("v")&&split.length == 4){
                vertices.add(Float.parseFloat(split[1]));
                vertices.add(Float.parseFloat(split[2]));
                vertices.add(Float.parseFloat(split[3]));
                vertices.add(0f);
            }
            else if(split[0].equalsIgnoreCase("f")&&split.length == 4){
                for (int j = 1; j < split.length; j++) {
                    int ind = Integer.parseInt(split[j].split("/")[0])-1;
                    indices.add(ind);
                }

            }
        }
        if(vertices.size()%4 != 0){
            throw new UnsupportedOperationException("invalid modle");
        }
        float[] vertData = new float[vertices.size()];
        int[] indData = new int[indices.size()];

        for (int i = 0; i < vertices.size(); i++) {
            vertData[i]=vertices.get(i);
        }
        for (int i = 0; i < indices.size(); i++) {
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
        this.setVerticesVBO(verticeVBO);
        this.setIndicesVBO(indicesVBO);

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
    public void drawn(){
        if(!areVBOSInitialized()){
            initVBOS();
        }
        //set normals data, each normal is a 3D vector representing a face
        //since every face is 3 indices, the indice list and the normals list shall have an 1:1 scale
        GL30.glVertexAttribPointer(SHADER_NORMALS_LOCATION,3,GL30.GL_FLOAT,false,0,0L);

        GL30.glUseProgram(this.shader.getID());
        drawnIndexBufferVBO(getVerticesVBO(),GL30.GL_TRIANGLES,4,this.shader, getIndicesVBO(), indexes.length);

    }

    public float[] getCoords(){
        return coords.clone();
    }
    public void setCoords(float[] coords){
        for (int i = 0; i < this.coords.length; i++) {this.coords[i] = coords[i];}
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

    protected void genNormals(){
        normals = new float[vertices.length - (vertices.length/4)];
        for (int i = 2; i < normals.length; i+=3) {
            normals[i] = 1;
        }
        //3/4ths relation
        //todo maybe calculate normals based in the relation between the shape center and the vertex?

    }

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
