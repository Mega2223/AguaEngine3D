package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering;

import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;

public class DepthDisplayShaderProgram extends DisplayShaderProgram {

    public DepthDisplayShaderProgram(float[] textureCoords, int sX, int sY){
        this(textureCoords, RenderingManager.genTextureFrameBufferObject(sX,sY));
    }
    public DepthDisplayShaderProgram(float[] textureCoords, int[] FBO){
        this(textureCoords,FBO,new String[]{
                Utils.SHADERS_DIR+"\\DepthDisplayFragShader.fsh",
                Utils.SHADERS_DIR+"\\DisplayTextureVertexShader.vsh"
        });
    }

    protected DepthDisplayShaderProgram(float[] textureCoords, int[] FBO, String[] shaderFiles) {
        super(textureCoords, FBO, shaderFiles);
    }
}
