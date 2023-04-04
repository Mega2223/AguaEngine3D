package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.RenderingManager;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;

import static net.mega2223.lwjgltest.aguaengine3d.graphics.utils.RenderingManager.drawnIndexBufferVBO;

public class Model {

    public static final int VAO_VERTEX_DATA_LOCATION = 0;
    public static final int VAO_INDEX_DATA_LOCATION = 1;
    public static final int VAO_TEXTURE_DATA_LOCATION = 2;
    public static final int VAO_NORMALS_LOCATION = 3;

    protected float[] vertices; //each vertex has 4 attributes
    protected float[] coords = {0,0,0,0};
    protected int[] indexes;
    protected float[] normals = null;//should've done this way before lol, normals are 3d vectors, no 4th coord

    protected ShaderProgram shader;
    protected int VAO = -1;
    protected int[] VBOS = null;

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
        unloadVAOS();
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
        unloadVAOS();
    }

    public int getVAO(){
        while (VAO == -1){
            initVAO();
        }
        return VAO;
    }
    public int[] getVBOS(){
        while (VBOS == null){
            initVAO();
        }
        return VBOS;
    }

    protected void initVAO(){
        initVAO(null);
    }
    protected void initVAO(int[] additionalVBOS){
        int quant = 2;//fixme initial size of VBO array, first to the vertices, second to indexes and third to normals
        if(additionalVBOS != null){quant += additionalVBOS.length;}

        VBOS = new int[quant];

        //initializes the VBO and inputs vertex data in it
        //the VBO with vetex data always has the same ID
        //it stores itself in a way which each vertex has a lenght of 4 attributes
        int vertexVBO = RenderingManager.genArrayBufferObject(vertices, GL30.GL_DYNAMIC_DRAW);
        int indicesVBO = RenderingManager.genIndexBufferObject(indexes, GL30.GL_DYNAMIC_DRAW);
        VBOS[0] = vertexVBO;
        VBOS[1] = indicesVBO;
        for (int i = 2; i < VBOS.length && additionalVBOS != null; i++) {
            VBOS[i] = additionalVBOS[i-2];
        }
        VAO = RenderingManager.genVAO(VBOS);
    }


    public void setVBOData(int index, float[] data){
        while (VAO == -1){
            initVAO();}
        RenderingManager.setVBOData(VBOS[index],data);
    }

    public void unloadVAOS(){
        if(VAO == -1 && VBOS == null){return;}
        GL30.glDeleteVertexArrays(VAO);
        for (int i = 0; i < VBOS.length; i++) {
            GL30.glDeleteBuffers(VBOS[i]);
        }
        VAO = -1;
        VBOS = null;
    }
    public void drawnVAO(){
        if(VAO == -1){
            initVAO();
        }
        GL30.glEnableVertexAttribArray(2);//normals location

        GL30.glVertexAttribPointer(2,3,GL30.GL_FLOAT,false,0,0L);

        GL30.glUseProgram(this.shader.getID());
        drawnIndexBufferVBO(VBOS[VAO_VERTEX_DATA_LOCATION],GL30.GL_TRIANGLES,4,this.shader, getVBOS()[VAO_INDEX_DATA_LOCATION], indexes.length);
        GL30.glDisableVertexAttribArray(2);
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

    protected void genNormals(){
        normals = new float[vertices.length - (vertices.length/4)];
        for (int i = 2; i < normals.length; i+=3) {
            normals[i] = 1;
        }
        //3/4ths relation
        //todo maybe calculate normals based in the relation between the shape center and the vertex?

    }

}
