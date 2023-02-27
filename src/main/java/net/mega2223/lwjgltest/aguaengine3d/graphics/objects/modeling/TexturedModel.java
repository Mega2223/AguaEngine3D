package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.TextureShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.TextureManager;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;

public class TexturedModel extends Model{

    int texture;
    float[] textureShift;

    public TexturedModel(float[] triangles,int[] indices, float[] textureShift, String textureDir) {
        super(triangles,indices,new TextureShaderProgram());
        init(textureShift,TextureManager.loadTexture(textureDir));
    }
    public TexturedModel(float[] triangles,int[] indices, float[] textureShift, ShaderProgram shader, String textureDir) {
        super(triangles,indices,shader);
        init(textureShift,TextureManager.loadTexture(textureDir));
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
        this.textureShift = textureData;
        textureSamplerUniformCoord = GL30.glGetUniformLocation(shader.getID(),"samplerTexture");
    };

    //puts the texture data in the first slot of the additional vbos on the super method
    @Override
    protected void initVAO(int[] additionalVBOS){
        int siz = 1;
        if(additionalVBOS != null){siz += additionalVBOS.length;}

        int VBOS[] = new int[siz];
        VBOS[0] = RenderingManager.genArrayBufferObject(textureShift,GL30.GL_DYNAMIC_DRAW);
        for (int i = 1; i < VBOS.length && additionalVBOS != null; i++) {
            VBOS[i] = additionalVBOS[i-1];
        }

        super.initVAO(VBOS);
    }

    int textureSamplerUniformCoord = -1;



    @Override
    public void drawnVAO() {
        if(VBOS == null || VAO == -1){
            initVAO();
        }

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER,VBOS[VAO_TEXTURE_DATA_LOCATION]);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, textureShift,GL30.GL_DYNAMIC_DRAW);
        GL30.glBindVertexArray(VAO);
        GL30.glUseProgram(shader.getID());
        GL30.glEnableVertexAttribArray(1);
        //GL30.texparamet
        GL30.glVertexAttribPointer(1,2,GL30.GL_FLOAT,false,0,0L);
        //GL30.glActiveTexture(GL30.GL_TEXTURE0);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,texture);
        //GL30.glTexImage2D();
        super.drawnVAO();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,0);
        GL30.glDisable(GL30.GL_TEXTURE_2D);
        GL30.glDisableVertexAttribArray(1);
    }

    public static TexturedModel loadTexturedModel(String[] objData, ShaderProgram shader, int texture){

        ArrayList<String> existingIndexes = new ArrayList<>();
        ArrayList<Float> vertices = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();
        ArrayList<Float> textureIndexes = new ArrayList<>();
        for (int i = 0; i < objData.length; i++) {
            String[] split = objData[i].split(" ");
            String type = split[0];
            if(type.equalsIgnoreCase("v")&&split.length == 4){
                vertices.add(Float.parseFloat(split[1]));
                vertices.add(Float.parseFloat(split[2]));
                vertices.add(Float.parseFloat(split[3]));
                vertices.add(0f);
            }
            else if(type.equalsIgnoreCase("vt")&&split.length==3){
                textureIndexes.add(1-Float.parseFloat(split[1]));
                textureIndexes.add(1-Float.parseFloat(split[2]));
            }
        }
        if(vertices.size()%4 != 0){
            throw new UnsupportedOperationException("invalid modle");
        }

        for (String objDatum : objData) {
            String[] split = objDatum.split(" ");
            String type = split[0];
            if (type.equalsIgnoreCase("f") && split.length == 4) {
                for (int j = 1; j < split.length; j++) {
                    int index = existingIndexes.indexOf(split[j]);
                    if (index == -1) {
                        existingIndexes.add(split[j]);
                        index = existingIndexes.indexOf(split[j]);
                    }
                    indices.add(index);
                }
            }
        }

        float[] vertData = new float[existingIndexes.size()*4];
        float[] texData = new float[existingIndexes.size()*2];
        int[] indData = new int[indices.size()];

        for(int i = 0; i<existingIndexes.size(); i++){
            String[] sp = existingIndexes.get(i).split("/");
            int[] spI = {Integer.parseInt(sp[0]),Integer.parseInt(sp[1])};
            for (int j = 0; j < 3; j++) {
                vertData[(i*4)+j] = vertices.get((spI[0]-1)*4+j);
            }
            for (int j = 0; j < 2; j++) {
                texData[(i*2)+j] = 1-textureIndexes.get((spI[0]-1)*2+j);
            }
        }
        for (int i = 0; i < indData.length; i++) {
            indData[i]=indices.get(i);
        }
        //indData =  new int[]{0,1,3};

        TexturedModel ret = new TexturedModel(vertData,indData,texData,shader,texture);

        return ret;
    }

    public static void debugTexturedModel(TexturedModel model){
        float[] vertices = model.vertices;
        float[] textureData = model.textureShift;
        int[] indexes = model.indexes;

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

}
