package net.mega2223.aguaengine3d.graphics.objects.shadering;

import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.graphics.utils.ShaderDictionary;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;

public class MultipleColorsShaderProgram extends ShaderProgramTemplate implements ShaderProgram{

    public static final int VERTEX_COLOR_LOCATION = 4;

    float[] colorData;
    int colorDataVBO = -1;

    public MultipleColorsShaderProgram(float[] colors){this(colors,null);}
    public MultipleColorsShaderProgram(float[] colors, ShaderDictionary dict){
        this.id = ShaderManager.loadShaderFromFiles(new String[] {
                Utils.SHADERS_DIR+"\\MultipleColorsVertexShader.vsh",
                Utils.SHADERS_DIR+"\\MultipleColorsFragmentShader.fsh",}
        ,dict);
        setVertexColors(colors);
        initUniforms();
    }

    public void setVertexColors(float[] colors){
        colorData = colors;
        if(colorDataVBO <= -1){
            colorDataVBO = RenderingManager.genArrayBufferObject(colorData,GL30.GL_DYNAMIC_DRAW);
        }
    }

    public float[] getColorData() {
        return colorData;
    }

    @Override
    public void initUniforms() {
        super.initUniforms();
    }

    @Override
    public void setUniforms(int interation, float[] translationMatrix, float[] projectionMatrix) {
        super.setUniforms(interation, translationMatrix, projectionMatrix);
    }

    @Override
    public void preRenderLogic() {
        GL30.glEnableVertexAttribArray(VERTEX_COLOR_LOCATION);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER,colorDataVBO);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER,colorData,GL30.GL_DYNAMIC_DRAW);
        GL30.glVertexAttribPointer(VERTEX_COLOR_LOCATION,4,GL30.GL_FLOAT,false,0,0L);
    }

    @Override
    public void postRenderLogic() {
        GL30.glDisableVertexAttribArray(VERTEX_COLOR_LOCATION);
    }
}
