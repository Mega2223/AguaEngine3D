package net.mega2223.aguaengine3d.graphics.objects.shadering;

import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;

public class CubemapInterpreterShaderProgram implements ShaderProgram{

    final int id;
    final int texture_loc;
    final int direction_vector_loc;
    final int texture;

    public CubemapInterpreterShaderProgram(int texture){
        id = ShaderManager.loadShaderFromFiles(new String[]{
                Utils.SHADERS_DIR+"\\CubemapVertexShader.vsh",
                Utils.SHADERS_DIR+"\\CubemapFragmentShader.vsh"
        });
        this.texture = texture;
        texture_loc = GL30.glGetUniformLocation(id,"cubemap");
        direction_vector_loc = GL30.glGetUniformLocation(id,"direction");
        GL30.glUniform1i(texture_loc,0);//redundant but ok
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public int[] getLightspaceTextureLocs() {
        return new int[0];
    }

    @Override
    public void initUniforms() {

    }

    @Override
    public void setUniforms(int interation, float[] translationMatrix, float[] projectionMatrix) {

    }

    @Override
    public void setRotationMatrix(float[] m4) {

    }

    @Override
    public void setRenderShadows(int index, boolean s){}

    @Override
    public void preRenderLogic() {
        GL30.glUseProgram(id);
        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP,texture);
        GL30.glActiveTexture(GL30.GL_TEXTURE0);

    }
}
