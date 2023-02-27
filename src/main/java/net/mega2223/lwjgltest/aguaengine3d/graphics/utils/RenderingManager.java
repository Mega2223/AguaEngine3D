package net.mega2223.lwjgltest.aguaengine3d.graphics.utils;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class RenderingManager {
    private RenderingManager(){}

    public static final int VERT_DATA_LOC = 0;


    public static void drawArrayVBO(int vbo, int mode, int attribSize, ShaderProgram program, int totalVarCount){
        //not having the vao binded may have some negative consequences
        GL30.glUseProgram(program.getID());
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER,vbo);
        GL30.glEnableVertexAttribArray(VERT_DATA_LOC);
        GL30.glVertexAttribPointer(VERT_DATA_LOC, attribSize, GL30.GL_FLOAT, false, 0, 0l);
        GL30.glDrawArrays(mode,0,totalVarCount/attribSize);
        GL30.glDisableVertexAttribArray(VERT_DATA_LOC);

    }
    public static void drawnIndexBufferVBO(int verticesVBO, int mode, int attribSize, ShaderProgram program, int indicesVBO, int indicesSize){
        GL30.glUseProgram(program.getID());
        GL30.glEnableVertexAttribArray(VERT_DATA_LOC);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER,verticesVBO);
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER,indicesVBO);
        GL30.glVertexAttribPointer(VERT_DATA_LOC, attribSize, GL30.GL_FLOAT, false, 0, 0l);
        GL30.glDrawElements(mode,indicesSize,GL30.GL_UNSIGNED_INT,0);
        GL30.glDisableVertexAttribArray(VERT_DATA_LOC);
    }

    public static int genArrayBufferObject(float[] data, int accessMode){
        int vbo = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER,vbo);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER,data,accessMode);
        return vbo;
    }
    public static int genIndexBufferObject(int[] data, int acessMode){
        int vbo = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER,vbo);
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER,data,acessMode);
        return vbo;
    }

    public static int genVAO(int[] vbos){
        int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);
        for(int i = 0; i < vbos.length;i++){
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbos[i]);
        }
        GL30.glBindVertexArray(0);
        return vao;
    }

    public static int genVAO(float[][] data){
        int[] vbos = new int[data.length];
        for(int i = 0; i < data.length;i++){
            int vbo = genArrayBufferObject(data[i], GL30.GL_STREAM_DRAW);
            vbos[i] = vbo;
        }
        int vao = genVAO(vbos);
        GL30.glBindVertexArray(vao);
        for(int i = 0; i < vbos.length;i++){
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbos[i]);
        }
        GL30.glBindVertexArray(0);
        return vao;
    }

    public static void setVBOData(int VBO, float[] data){

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER,VBO);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER,data,GL30.GL_DYNAMIC_DRAW);

    }

    public static void setVAOData(int VAO, int index, int VBO){
        GL30.glBindVertexArray(VAO);
        GL30.glEnableVertexAttribArray(index);
        GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER,VBO);
        GL30.glDisableVertexAttribArray(index);
    }

    public static void debugPolyArray(float[][] polis){
        for(float[] act : polis){debugPolygon(act);}
    }
    public static void debugPolygon(float[] poly){
        String debug = "";
        for (int i = 0; i < poly.length; i+=4) {
            debug += "Vertice "+ i/4 + ": (x:" + poly[i] + ",y:" + poly[i+1] + ",z:" + poly[i+2] + ")\n";
        }
        System.out.println(debug);
    }
}
