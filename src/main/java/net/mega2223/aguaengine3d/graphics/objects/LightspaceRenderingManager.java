package net.mega2223.aguaengine3d.graphics.objects;

import net.mega2223.aguaengine3d.graphics.objects.shadering.DepthBufferShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import org.lwjgl.opengl.GL30;

import java.util.List;

public class LightspaceRenderingManager {

    public static final int FIRST_TEXTURE_LIGHTMAP_LOC = 3; // 3 - 13
    static final int SHADOW_RES = 512;

    boolean[] doShadowMapping = new boolean[ShaderProgram.MAX_LIGHTS];
    int[][] shadowMappingFBOS = new int[ShaderProgram.MAX_LIGHTS][];
    float[][] shadowMappingProjectionMatrices = new float[ShaderProgram.MAX_LIGHTS][];

    final RenderingContext associatedRenderingContext;

    LightspaceRenderingManager(RenderingContext context){
        associatedRenderingContext = context;
    }

    public void applyRenderMaps(){
        //GL30.glActiv
    }

    float[] bufferM4 = new float[16];
    float[] bufferM42 = new float[16];

    public void renderLightmapsAsNeeded(){
        GL30.glUseProgram(DepthBufferShaderProgram.GLOBAL_INSTANCE.getID());
        for (int i = 0; i < ShaderManager.SHADER_MAX_LIGHTS; i++) {
            if(doShadowMapping[i]){
                assureFBOIsValid(i);
                int[] FBO = shadowMappingFBOS[i];
                genProjectionMatrixForLight(bufferM4,i);
                genTranslationMatrixForLight(bufferM42,i);
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,FBO[0]);
                GL30.glClear(GL30.GL_DEPTH_BUFFER_BIT);
                GL30.glViewport(0,0,SHADOW_RES,SHADOW_RES);
                GL30.glActiveTexture(GL30.GL_TEXTURE0 + FIRST_TEXTURE_LIGHTMAP_LOC + i);
                GL30.glBindTexture(GL30.GL_TEXTURE_2D,shadowMappingFBOS[i][1]);
                setLightspaceProjMatricesUniforms(i,bufferM4,bufferM42);
                associatedRenderingContext.doCustomRenderForceShader(bufferM4,DepthBufferShaderProgram.GLOBAL_INSTANCE);
                GL30.glBindTexture(GL30.GL_TEXTURE_2D,0);
                GL30.glActiveTexture(GL30.GL_TEXTURE0);
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,0);
            }
        }
    }

    public void genTranslationMatrixForLight(float[] m4, int index){
        float[] pos = associatedRenderingContext.lights[index];
        MatrixTranslator.generateTranslationMatrix(pos[0], pos[1], pos[2], m4);
    }

    public void genProjectionMatrixForLight(float[] m4,int index){
        float[] pos = associatedRenderingContext.lights[index];
        MatrixTranslator.generatePerspectiveProjectionMatrix(m4,0.1f,80f, (float) Math.toRadians(45),1);
        MatrixTranslator.applyLookTransformation(pos[0], pos[1], pos[2], 0, 0, 0, m4); //fixme
    }

    void setLightspaceProjMatricesUniforms(int index,float[] projection, float[] translation){
        List<Renderable> objects = associatedRenderingContext.getObjects();
        for(Renderable ac : objects){
            ShaderProgram shader = ac.getShader();
            GL30.glUseProgram(shader.getID());
            int projLoc = GL30.glGetUniformLocation(shader.getID(),"lightspace_projections["+index+"]");
            int transLoc = GL30.glGetUniformLocation(shader.getID(),"lightspace_translations["+index+"]");
            GL30.glUniformMatrix4fv(projLoc,false,projection);
            GL30.glUniformMatrix4fv(transLoc,false,translation);
        }
    }

    public void setDoShadowMapping(int index, boolean state){
        doShadowMapping[index] = state;
        List<Renderable> objects = associatedRenderingContext.getObjects();
        for(Renderable m : objects){
            m.getShader().setRenderShadows(index,state);
        }
    }

    public void alignUniforms(ShaderProgram program){
        for (int i = 0; i < ShaderManager.SHADER_MAX_LIGHTS; i++) {
            program.setRenderShadows(i,doShadowMapping[i]);
        }
    }

    void assureFBOIsValid(int index){
        if(shadowMappingFBOS[index] == null){
            shadowMappingFBOS[index] = RenderingManager.genDepthFrameBufferObject(SHADOW_RES,SHADOW_RES);
            shadowMappingProjectionMatrices[index] = new float[16];
        }
    }

    public void erradicateFBO(int index){
        GL30.glDeleteFramebuffers(shadowMappingFBOS[0]); //todo maybe move to RenderingManager?
        GL30.glDeleteTextures(shadowMappingFBOS[1]);
        GL30.glDeleteRenderbuffers(shadowMappingFBOS[2]);
        shadowMappingFBOS[index] = null;
    }

    public int[] getAssossiatedFBO(int index){
        return shadowMappingFBOS[index];
    }
    public int getAssossiatedTexture(int index){
        return shadowMappingFBOS[index][1];
    }
}
