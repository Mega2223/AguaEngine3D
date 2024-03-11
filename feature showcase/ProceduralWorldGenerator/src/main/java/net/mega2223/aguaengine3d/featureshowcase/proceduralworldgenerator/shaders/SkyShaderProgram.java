package net.mega2223.aguaengine3d.featureshowcase.proceduralworldgenerator.shaders;

import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;

public class SkyShaderProgram implements ShaderProgram {
    private final int id;
    private final int direction_vector_loc;
    private final int projection_matrix_loc;
    private final int rotation_matrix_loc;
    public SkyShaderProgram(){
        id = ShaderManager.loadShaderFromFiles(new String[]{
                        Utils.USER_DIR+"\\feature showcase\\ProceduralWorldGenerator\\src\\main\\resources\\SkyboxFragment.fsh",
                        Utils.USER_DIR+"\\feature showcase\\ProceduralWorldGenerator\\src\\main\\resources\\SkyboxVertex.vsh"
                }
        );
        direction_vector_loc = GL30.glGetUniformLocation(id,"direction");
        projection_matrix_loc = GL30.glGetUniformLocation(id,"projection");
        rotation_matrix_loc = GL30.glGetUniformLocation(id,"rotation");
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void initUniforms() {

    }

    private final float[] rotationMatrix = new float[16];
    @Override
    public void setUniforms(int interation, float[] translationMatrix, float[] projectionMatrix) {
        GL30.glUseProgram(id);
        GL30.glUniformMatrix4fv(projection_matrix_loc,false,projectionMatrix);
        GL30.glUniformMatrix4fv(rotation_matrix_loc,false, rotationMatrix);
    }

    @Override
    public void setRotationMatrix(float[] m4) {
        System.arraycopy(m4,0, rotationMatrix,0,0);
    }

    @Override
    public void setRenderShadows(int index, boolean s) {

    }
    @Override
    public int[] getLightspaceTextureLocs() {
        return null;
    }

    public void setDirection(float x, float y, float z){
        GL30.glUseProgram(id);
        GL30.glUniform3f(direction_vector_loc,x,y,z);
    }
}
