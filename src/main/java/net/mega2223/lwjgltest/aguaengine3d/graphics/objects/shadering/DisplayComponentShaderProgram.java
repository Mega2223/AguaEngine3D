package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering;

import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.lwjgltest.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;

public class DisplayComponentShaderProgram implements ShaderProgram{
    final int id;
    int texture;

    float aspectRatio;
    protected float[] nativeProjectionMatrix = new float[16];
    int rotationMatrixLoc = -1;
    int translationMatrixLoc = -1;
    int projectionMatrixLoc = -1;

    public DisplayComponentShaderProgram(int texture, float aspectRatio){
        id = ShaderManager.loadShaderFromFiles(
                new String[]{
                        Utils.SHADERS_DIR+"\\InterfaceComponentFragmentShader.fsh",
                        Utils.SHADERS_DIR+"\\InterfaceComponentVertexShader.vsh"
                }
        );
        this.texture = texture;
        initUniforms();
        this.aspectRatio = aspectRatio;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void initUniforms() {
        translationMatrixLoc = GL30.glGetUniformLocation(id,"translation");
        rotationMatrixLoc = GL30.glGetUniformLocation(id,"translation");
        projectionMatrixLoc = GL30.glGetUniformLocation(id,"projection");
    }
    /**This shader does not support projection matrices*/
    @Override
    public void setUniforms(int interation, float[] translationMatrix, float[] projectionMatrix) {
        GL30.glUseProgram(id);
        GL30.glUniformMatrix4fv(translationMatrixLoc,false,translationMatrix);
        MatrixTranslator.generateStaticInterfaceProjectionMatrix(nativeProjectionMatrix,aspectRatio);
        GL30.glUniformMatrix4fv(projectionMatrixLoc,false,nativeProjectionMatrix);
    }

    @Override
    public void preRenderLogic() {
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,texture);
    }

    @Override
    public void postRenderLogic() {
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,0);
    }

    @Override
    public void setRotationMatrix(float[] m4) {
        GL30.glUniformMatrix4fv(rotationMatrixLoc,false,m4);
    }
    @Override
    public void setRenderShadows(int index, boolean s) {}
    @Override
    public int[] getLightspaceTextureLocs() {return null;}

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }
}
