package net.mega2223.aguaengine3d.tools.modeleditor;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.modeling.InterfaceComponent;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ModelUtils;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ui.BitmapFont;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ui.TextManipulator;
import net.mega2223.aguaengine3d.graphics.objects.shadering.DisplayComponentShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.objects.WindowManager;

public class Console implements Renderable {
    public boolean isVisible = false;
    protected BitmapFont font;
    protected StringBuilder data = new StringBuilder();
    public InterfaceComponent text = null;
    public Console(WindowManager context, BitmapFont font) {
        this.font = font;
        add("teste\nAMONG US\nIN real life");

        context.addUpdateEvent(() -> text.setAspectRatio(context.getAspectRatio()));
        text.setScale(.2F,.3F,.3F);
        text.setAligment(InterfaceComponent.TOP_LEFT_ALIGMENT);
    }

    public void add(String what){
        data.append(what);
        refresh();
    }

    public void refresh(){
        text = font.genFromString(data);
        ModelUtils.rescaleModel(text,0.5F);
    }

    @Override
    public void draw() {
        text.draw();
    }

    @Override
    public void drawForceShader(ShaderProgram shader) {
        text.drawForceShader(shader);
    }

    @Override
    public void doLogic(int iteration) {
        text.doLogic(iteration);
    }

    @Override
    public void setUniforms(int iteration, float[] projectionMatrix) {
        text.setUniforms(iteration,projectionMatrix);
    }

    @Override
    public ShaderProgram getShader() {
        return text.getShader();
    }

    @Override
    public int getRenderOrderPosition() {
        return -1;
    }
}
