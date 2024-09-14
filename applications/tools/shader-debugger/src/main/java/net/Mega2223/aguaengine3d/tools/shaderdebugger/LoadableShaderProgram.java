package net.Mega2223.aguaengine3d.tools.shaderdebugger;

import net.mega2223.aguaengine3d.graphics.objects.LightspaceRenderingManager;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;

public class LoadableShaderProgram implements ShaderProgram {
    int id = -1;
    protected int projectionMatrixLocation = -1;
    protected int translationMatrixLocation = -1;
    protected int rotationMatrixLocation = -1;
    protected int itnerationLocation = -1;

    public LoadableShaderProgram(){
        id = ShaderManager.loadShaderFromFiles(new String[]{
                Utils.USER_DIR+"\\Tools\\ShaderDebugger\\src\\main\\resources\\VertexShader.vsh",
                Utils.USER_DIR+"\\Tools\\ShaderDebugger\\src\\main\\resources\\FragmentShader.fsh",
        },null);
        initUniforms();
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
        projectionMatrixLocation = GL30.glGetUniformLocation(getID(),"projection");
        translationMatrixLocation = GL30.glGetUniformLocation(getID(),"translation");
        rotationMatrixLocation = GL30.glGetUniformLocation(getID(),"rotation");
        itnerationLocation = GL30.glGetUniformLocation(getID(),"iteration");
        GL30.glUseProgram(getID());
//        for (int i = 0; i < MAX_LIGHTS; i++) {
//            shadowEnableBoolLoc[i] = GL30.glGetUniformLocation(getID(),"doShadowMapping["+i+"]");
//            lightSpacePositions[i] = GL30.glGetUniformLocation(getID(),"lightspace_projections["+i+"]");
//            lightSpaceTranslations[i] = GL30.glGetUniformLocation(getID(),"lightspace_translations["+i+"]");
//            shadowmapSamplers[i] = GL30.glGetUniformLocation(getID(),"shadowmaps["+i+"]");
//            GL30.glUniform1i(shadowmapSamplers[i], LightspaceRenderingManager.FIRST_TEXTURE_LIGHTMAP_LOC+i);
//        }

    }

    @Override
    public void setUniforms(int interation, float[] translationMatrix, float[] projectionMatrix) {
        GL30.glUseProgram(getID());
        GL30.glUniformMatrix4fv(translationMatrixLocation,false,translationMatrix);
        GL30.glUniformMatrix4fv(projectionMatrixLocation,false,projectionMatrix);
        GL30.glUniform1i(itnerationLocation,interation);
    }
    @Override
    public void setRotationMatrix(float[] m4){
        GL30.glUseProgram(getID());
        GL30.glUniformMatrix4fv(rotationMatrixLocation,false,m4);
    }


    @Override
    public void setRenderShadows(int index, boolean s) {
        //TODO
    }
}
