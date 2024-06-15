package net.mega2223.aguaengine3d.graphics.objects.shadering;

import net.mega2223.aguaengine3d.graphics.objects.modeling.TexturedInterfaceComponent;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;

public class DisplayComponentShaderProgram implements ShaderProgram{
    final int id;
    int texture;

    float aspectRatio;
    protected final float[] nativeProjectionMatrix = new float[16];
    protected final float[] textureCoords;

    int textureCoordsVBO;

    int rotationMatrixLoc = -1;
    int translationMatrixLoc = -1;
    int projectionMatrixLoc = -1;
    int scaleMatrixLoc = -1;
    int aligmentIntLoc = -1;

    private static final float[] bufferMat4 = new float[16];

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

    public DisplayComponentShaderProgram(DisplayComponentShaderProgram shaderProgram){
        this(shaderProgram.texture, shaderProgram.textureCoords.clone(),shaderProgram.aspectRatio);
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
        scaleMatrixLoc = GL30.glGetUniformLocation(id,"scale");
        aligmentIntLoc =GL30.glGetUniformLocation(id,"aligment");
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

    public int getTexture() {
        return texture;
    }

    protected void setTexture(int texture){
        this.texture = texture;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public void setScale(float f){
        setScale(f,f,f);
    }

    public void setScale(float x, float y, float z){
        MatrixTranslator.generateTranslationAndScaleMatrix(bufferMat4,0,0,0,x,y,z);
        GL30.glUseProgram(id);
        GL30.glUniformMatrix4fv(scaleMatrixLoc,false,bufferMat4);
    }

    public void setAligment(int aligment){
        GL30.glUseProgram(id);
        GL30.glUniform1i(aligmentIntLoc,aligment);
    }

    public static final int CENTER_ALIGMENT = TexturedInterfaceComponent.CENTER_ALIGMENT,
            BOTTOM_LEFT_ALIGMENT = TexturedInterfaceComponent.BOTTOM_LEFT_ALIGMENT,
            BOTTOM_RIGHT_ALIGMENT = TexturedInterfaceComponent.BOTTOM_RIGHT_ALIGMENT,
            TOP_LEFT_ALIGMENT = TexturedInterfaceComponent.TOP_LEFT_ALIGMENT,
            TOP_RIGHT_ALIGMENT = TexturedInterfaceComponent.TOP_RIGHT_ALIGMENT;
}
