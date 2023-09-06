package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.TextureShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.TextureManager;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;

public class TexturedModel extends Model{

    int texture;
    protected float[] textureShift;

    public TexturedModel(float[] triangles,int[] indices, float[] textureShift, String textureDir) {
        super(triangles,indices,new TextureShaderProgram());
        init(textureShift,TextureManager.loadTexture(textureDir));
    }
    public TexturedModel(float[] triangles,int[] indices, float[] textureShift, ShaderProgram shader, String textureDir) {
        super(triangles,indices,shader);
        init(textureShift,TextureManager.loadTexture(textureDir));
    }

    public TexturedModel(float[] triangles,int[] indices, float[] textureShift, ShaderProgram shader, float[] normals, String textureDir) {
        super(triangles,indices,shader,normals);
        init(textureShift,TextureManager.loadTexture(textureDir));
    }
    public TexturedModel(float[] triangles,int[] indices, float[] textureShift, ShaderProgram shader, float[] normals, int texture) {
        super(triangles,indices,shader,normals);
        init(textureShift,texture);
    }
    public TexturedModel(float[] triangles, int[] indices,float[] textureShift, ShaderProgram shader, int texture) {
        super(triangles,indices,shader);
        init(textureShift,texture);
    }
    public TexturedModel(float[] triangles, int[] indices,float[] textureShift, int texture) {
        super(triangles,indices,new TextureShaderProgram());
        init(textureShift,texture);
    }
    protected void init(float[] textureData, int texture){
        this.texture = texture;
        this.setTextureShift(textureData);
        textureSamplerUniformCoord = GL30.glGetUniformLocation(shader.getID(),"samplerTexture");
    }

    public void mirrorTexturesVertically(){
        for(int i = 1; i < textureShift.length; i+=2){textureShift[i]=1f-textureShift[i];}
    }

    //puts the texture data in the first slot of the additional vbos on the super method
    @Override
    protected void initVBOS(){
        //todo
        super.initVBOS();
    }

    int textureSamplerUniformCoord = -1;

    @Override
    public void draw() {
        drawForceShader(shader);
    }

    @Override
    public void drawForceShader(ShaderProgram shader){
        if(!areVBOSInitialized()){
            initVBOS();
        }

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, getTextureCoordsVBO());
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, getTextureShift(),GL30.GL_DYNAMIC_DRAW);

        GL30.glUseProgram(shader.getID());
        GL30.glEnable(GL30.GL_TEXTURE_2D);
        GL30.glEnableVertexAttribArray(1);
        //GL30.texparamet
        GL30.glVertexAttribPointer(1,2,GL30.GL_FLOAT,false,0,0L);
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,texture);
        //GL30.glTexImage2D();
        super.drawForceShader(shader);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,0);
        GL30.glDisable(GL30.GL_TEXTURE_2D);
        GL30.glDisableVertexAttribArray(1);
    }

    /**loads data from a .obj datafront format string array,
     * then finds vertex combinations that are identical to each other in order to
     * reference them in the same index, so if the file contais combination "x/y" trice,
     * it will have 3 references in the index array and only one in the other arrays
     * */
    public static TexturedModel loadTexturedModel(String[] objData, ShaderProgram shader, int texture){

        boolean modelHasNormals = false; //either a model has normals on ALL faces, or in no faces whatsoever
        boolean modelHasTextureCoords = false; //same thing as above

        ArrayList<String> existingVerticeCombinations = new ArrayList<>();
        ArrayList<Float> vertices = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();
        ArrayList<Float> textureIndexes = new ArrayList<>();
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
            else if(type.equalsIgnoreCase("vt")&&split.length==3){
                modelHasTextureCoords = true;
                textureIndexes.add(Float.parseFloat(split[1]));
                textureIndexes.add(1-Float.parseFloat(split[2]));
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
        float[] texData = new float[existingVerticeCombinations.size()*2];
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
            if(modelHasTextureCoords){ //combination[1] will exist either way, but it may be empty
                for (int j = 0; j < 2; j++) {
                    texData[(i*2)+j] = textureIndexes.get((combination[1]-1)*2+j);
                }
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
            return new TexturedModel(vertData,indData,texData,shader,normalsData,texture);
        }

        return new TexturedModel(vertData,indData,texData,shader,texture);
    }

    public static void debugTexturedModel(TexturedModel model){
        float[] vertices = model.vertices;
        float[] textureData = model.getTextureShift();
        int[] indexes = model.indices;

        StringBuilder debug = new StringBuilder("Debugging model with " + vertices.length + " vertices\n");
        debug.append("It is using the shaderProgram ").append(model.shader.getID());

        for (int i = 0; i < vertices.length; i+=4) {
            debug.append("\nVertice ").append(i / 4).append(":");
            debug.append("\nCoordinates: [").append(vertices[i]).append(",").append(vertices[i + 1]).append(",").append(vertices[i + 2]).append("]");
            debug.append("\nTexture Data: [").append(textureData[i / 2]).append(",").append(textureData[i / 2 + 1]).append("]");
        }
        debug.append("\nThis model is constructed to build in the order [");

        for (int i = 0; i < indexes.length-1; i++) {
            debug.append(indexes[i]).append(",");
        }
        debug.append(indexes[indexes.length - 1]).append("]");
        System.out.println(debug);
    };

    public float[] getTextureShift() {
        return textureShift.clone();
    }

    public void setTextureShift(float[] textureShift) {
        this.textureShift = textureShift;
    }

    public int getTexture() {
        return texture;
    }
}
