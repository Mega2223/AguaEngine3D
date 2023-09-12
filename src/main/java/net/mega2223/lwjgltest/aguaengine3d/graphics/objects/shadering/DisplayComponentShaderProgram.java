package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering;

import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.lwjgltest.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;

public class DisplayComponentShaderProgram implements ShaderProgram{
    final int id;
    int texture;

    float aspectRatio;
    protected float[] nativeProjectionMatrix = new float[16];
    protected float[] textureCoords;

    int textureCoordsVBO;

    int rotationMatrixLoc = -1;
    int translationMatrixLoc = -1;
    int projectionMatrixLoc = -1;

    public DisplayComponentShaderProgram(int texture,float[] textureCoords, float aspectRatio){
        id = ShaderManager.loadShaderFromFiles(
                new String[]{
                        Utils.SHADERS_DIR+"\\InterfaceComponentFragmentShader.fsh",
                        Utils.SHADERS_DIR+"\\InterfaceComponentVertexShader.vsh"
                }
        );
        this.texture = texture;
        initUniforms();
        this.aspectRatio = aspectRatio;
        this.textureCoords = textureCoords;
        textureCoordsVBO = RenderingManager.genArrayBufferObject(textureCoords,GL30.GL_STATIC_DRAW);
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
        MatrixTranslator.debugMatrix4x4(nativeProjectionMatrix);
        GL30.glUniformMatrix4fv(projectionMatrixLoc,false,nativeProjectionMatrix);
    }

    @Override
    public void preRenderLogic() {
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,texture);
        GL30.glEnableVertexAttribArray(1);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER,textureCoordsVBO);//fixme unoptimized call, also present at MultipleColorsShaderProgram
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER,textureCoords,GL30.GL_DYNAMIC_DRAW);
        GL30.glVertexAttribPointer(1,2,GL30.GL_FLOAT,false,0,0L);
    }

    @Override
    public void postRenderLogic() {
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,0);
        GL30.glDisableVertexAttribArray(1);
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
