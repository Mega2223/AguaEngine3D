package net.mega2223.lwjgltest.aguaengine3d.logic;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.DepthBufferShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.lwjgltest.aguaengine3d.mathematics.MatrixTranslator;
import org.lwjgl.opengl.GL30;

import java.util.List;

public class LightSpaceRenderingManager {

    public static final int FIRST_TEXTURE_LIGHTMAP_LOC = 3; // 3 - 13
    static final int SHADOW_RES = 512;

    boolean[] doShadowMapping = new boolean[10];
    int[][] shadowMappingFBOS = new int[10][];
    float[][] shadowMappingProjectionMatrices = new float[10][];

    final Context associatedContext;

    LightSpaceRenderingManager(Context context){
        associatedContext = context;
    }

    public void applyRenderMaps(){
        //GL30.glActiv
    }

    public void renderLightmapsAsNeeded(){
        List<Model> objects = associatedContext.getObjects();
        GL30.glUseProgram(DepthBufferShaderProgram.GLOBAL_INSTANCE.getID());
        for (int i = 0; i < ShaderManager.SHADER_MAX_LIGHTS; i++) {
            if(doShadowMapping[i]){
                assureFBOIsValid(i);
                int[] FBO = shadowMappingFBOS[i];
                float[] projM4 = shadowMappingProjectionMatrices[i];
                float[] pos = associatedContext.lights[i];
                MatrixTranslator.generateProjectionMatrix(projM4,20F,60F, (float) Math.toRadians(45),1);
                MatrixTranslator.applyLookTransformation(projM4,pos[0],pos[1],pos[2],0,0,0); //fixme
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,FBO[0]);
                GL30.glClear(GL30.GL_DEPTH_BUFFER_BIT);
                GL30.glViewport(0,0,SHADOW_RES,SHADOW_RES);
                associatedContext.doCustomRenderForceShader(projM4,DepthBufferShaderProgram.GLOBAL_INSTANCE);
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,0);
            }
        }
    }

    public void setDoShadowMapping(int index, boolean state){
        doShadowMapping[index] = state;
        List<Model> objects = associatedContext.getObjects();
        for(Model m : objects){
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
