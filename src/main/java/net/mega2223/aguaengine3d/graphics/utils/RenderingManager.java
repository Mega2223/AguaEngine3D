package net.mega2223.aguaengine3d.graphics.utils;

import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

/**
 * Class responsible for handling the most important OpenGL calls
 */
public class RenderingManager {
    public static final int VERT_DATA_LOC = 0;

    private RenderingManager() {
    }

    public static void drawArrayVBO(int vbo, int mode, int attribSize, ShaderProgram program, int totalVarCount) {
        //not having the vao binded may have some negative consequences
        GL30.glUseProgram(program.getID());
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
        GL30.glEnableVertexAttribArray(VERT_DATA_LOC);
        GL30.glVertexAttribPointer(VERT_DATA_LOC, attribSize, GL30.GL_FLOAT, false, 0, 0l);
        GL30.glDrawArrays(mode, 0, totalVarCount / attribSize);
        GL30.glDisableVertexAttribArray(VERT_DATA_LOC);

    }

    public static void drawnIndexBufferVBO(int verticesVBO, int mode, int attribSize, ShaderProgram program, int indicesVBO, int indicesSize) {
        GL30.glUseProgram(program.getID());
        GL30.glEnableVertexAttribArray(VERT_DATA_LOC);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, verticesVBO);
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, indicesVBO);
        GL30.glVertexAttribPointer(VERT_DATA_LOC, attribSize, GL30.GL_FLOAT, false, 0, 0l);
        GL30.glDrawElements(mode, indicesSize, GL30.GL_UNSIGNED_INT, 0);
        GL30.glDisableVertexAttribArray(VERT_DATA_LOC);
    }

    public static int genArrayBufferObject(float[] data, int accessMode) {
        int vbo = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, data, accessMode);
        return vbo;
    }

    public static int genIndexBufferObject(int[] data, int acessMode) {
        int vbo = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, vbo);
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, data, acessMode);
        return vbo;
    }

    public static int genVAO(int[] vbos) {
        int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);
        for (int i = 0; i < vbos.length; i++) {
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbos[i]);
        }
        GL30.glBindVertexArray(0);
        return vao;
    }

    public static int genVAO(float[][] data) {
        int[] vbos = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            int vbo = genArrayBufferObject(data[i], GL30.GL_STREAM_DRAW);
            vbos[i] = vbo;
        }
        int vao = genVAO(vbos);
        GL30.glBindVertexArray(vao);
        for (int i = 0; i < vbos.length; i++) {
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbos[i]);
        }
        GL30.glBindVertexArray(0);
        return vao;
    }

    public static int genFrameBufferObject() {
        return GL30.glGenFramebuffers();
    }

    public static int genRenderBufferObject() {
        return GL30.glGenRenderbuffers();
    }

    public static int[] genTextureFrameBufferObject(int tX, int tY) { //todo maybe move the texture logic to a method in the TextureManager class?
        int id = genFrameBufferObject();
        int texture = TextureManager.generateImageTexture(tX,tY);
        int depthBuffer = GL30.glGenRenderbuffers();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, texture);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER,depthBuffer);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_TEXTURE_2D, texture, 0);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER,GL30.GL_DEPTH_COMPONENT,tX,tY);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER,GL30.GL_DEPTH_ATTACHMENT,GL30.GL_RENDERBUFFER,depthBuffer);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER,0);
        return new int[]{id, texture, depthBuffer};
    }

    public static int[] genDepthFrameBufferObject(int tX, int tY){
        int frameBuffer = GL30.glGenFramebuffers();
        int texture = TextureManager.generateDepthTexture(tX,tY);
        int renderBuffer = GL30.glGenRenderbuffers();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,texture);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,frameBuffer);
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER,renderBuffer);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER,GL30.GL_DEPTH_ATTACHMENT,GL30.GL_TEXTURE_2D,texture,0);
        GL30.glDrawBuffer(GL30.GL_NONE);
        GL30.glReadBuffer(GL30.GL_NONE);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,0);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,0);
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER,0);
        return new int[]{frameBuffer,texture,renderBuffer};
    }

    @Deprecated //more like @Useless lmao
    public static int[] genRenderBuffer(int tX, int tY){
        int id = genFrameBufferObject();
        int texture = GL30.glGenTextures();
        int renderBuffer = genRenderBufferObject();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,texture);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,id);
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D,0,GL30.GL_DEPTH_COMPONENT,tX,tY,0, GL30.GL_RGB,GL30.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER,GL30.GL_COLOR_ATTACHMENT0,GL30.GL_TEXTURE_2D,texture,0);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, renderBuffer);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,0);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,0);
        return new int[]{id,texture};
    }

    public static void renderDepthFrameBufferObject(int[] fbo, int w, int h) {
        //must be called after having the proper shader uniforms setup
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo[0]);
        GL30.glViewport(0, 0, w, h); //The WindowManager class will automatically readjust this
        //However, this is not the optimal way of doing this
        GL30.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public static void setVBOData(int VBO, float[] data) {

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, VBO);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, data, GL30.GL_DYNAMIC_DRAW);

    }

    public static void setVAOData(int VAO, int index, int VBO) {
        GL30.glBindVertexArray(VAO);
        GL30.glEnableVertexAttribArray(index);
        GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
        GL30.glDisableVertexAttribArray(index);
    }

    public static void debugPolyArray(float[][] polis) {
        for (float[] act : polis) {
            debugPolygon(act);
        }
    }

    public static void debugPolygon(float[] poly) {
        StringBuilder debug = new StringBuilder();
        for (int i = 0; i < poly.length; i += 4) {
            debug.append("Vertice ").append(i / 4).append(": (x:").append(poly[i]).append(",y:").append(poly[i + 1]).append(",z:").append(poly[i + 2]).append(")\n");
        }
        System.out.println(debug);
    }

    public static void printErrorQueue(){
        int e = GL30.glGetError();
        while (e != GL30.GL_NO_ERROR){
            System.out.println("Error: " + e);
            e = GL30.glGetError();
        }
    }
}
