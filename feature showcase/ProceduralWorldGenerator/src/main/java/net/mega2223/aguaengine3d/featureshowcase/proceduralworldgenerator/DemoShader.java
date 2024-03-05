package net.mega2223.aguaengine3d.featureshowcase.proceduralworldgenerator;

import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgramTemplate;
import org.lwjgl.opengl.GL30;

public abstract class DemoShader extends ShaderProgramTemplate implements ShaderProgram {
    public abstract void setLightDirection(float x, float y, float z);
}
