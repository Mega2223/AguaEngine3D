package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering;

import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.lwjgltest.aguaengine3d.logic.ScriptedSequence;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;

public class DisplayShaderProgram extends ShaderProgramTemplate implements ShaderProgram {

    public static final int TEXTURE_VERTEX_ATTRIBUTE_POS = 1;

    int[] FBO;
    float[] textureCoords;
    int textureCoordsVBO;

    public DisplayShaderProgram(float[] textureCoords, int sX, int sY){
        this(textureCoords,RenderingManager.genTextureFrameBufferObject(sX,sY));
    }
    public DisplayShaderProgram(float[] textureCoords, int[] FBO){
        this(textureCoords,FBO,new String[]{
                Utils.SHADERS_DIR+"\\DisplayTextureFragShader.fsh",
                Utils.SHADERS_DIR+"\\DisplayTextureVertexShader.vsh"
        });
    }
    protected DisplayShaderProgram(float[] textureCoords, int[] FBO, String[] shaderFiles){
        id = ShaderManager.loadShaderFromFiles(shaderFiles);
        this.FBO = FBO;
        this.textureCoords = textureCoords;
        this.textureCoordsVBO = RenderingManager.genArrayBufferObject(textureCoords,GL30.GL_DYNAMIC_DRAW);
        initUniforms();
    }

    @Override
    public void initUniforms() {
        super.initUniforms();
    }

    @Override
    public void preRenderLogic() {
        GL30.glUseProgram(id);
        GL30.glEnable(GL30.GL_TEXTURE_2D);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER,textureCoordsVBO);
        //todo sub-optimal bufferData call
        //this happens thru all the engine, perhaps i need to clean up things a little
        //besides, i should be specifying STATIC_DRAW access anyway
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER,textureCoords,GL30.GL_DYNAMIC_DRAW);
        GL30.glEnableVertexAttribArray(TEXTURE_VERTEX_ATTRIBUTE_POS);
        GL30.glVertexAttribPointer(TEXTURE_VERTEX_ATTRIBUTE_POS,2,GL30.GL_FLOAT,false,0,0L);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,this.FBO[1]);
        super.preRenderLogic();
    }

    @Override
    public void postRenderLogic() {
        super.postRenderLogic();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,0);
        GL30.glDisableVertexAttribArray(TEXTURE_VERTEX_ATTRIBUTE_POS);
        GL30.glDisable(GL30.GL_TEXTURE_2D);
    }



    public int[] getFBO(){
        return FBO.clone();
    }
}
