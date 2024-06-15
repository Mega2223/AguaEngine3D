package net.mega2223.aguaengine3d.tools.modeleditor;

import net.mega2223.aguaengine3d.AguaEngine;
import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.modeling.TexturedInterfaceComponent;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ui.BitmapFont;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.objects.WindowManager;
import net.mega2223.aguaengine3d.tools.modeleditor.objects.StackedTexts;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class Console implements Renderable {

    protected BitmapFont font;
    protected StringBuilder cmd = new StringBuilder();

    public boolean isVisible = false;
    public StackedTexts text = new StackedTexts();
    public TexturedInterfaceComponent prompt;
    public ArrayList<ConsoleCommand> commands  = new ArrayList<>(10);

    public Console(WindowManager context, BitmapFont font) {
        this.font = font;
        add("AguaEngine3D Version "+AguaEngine.VERSION);

        context.addUpdateEvent(() -> {
            text.setAspectRatio(context.getAspectRatio());
            prompt.setAspectRatio(context.getAspectRatio());
        });
        context.addKeypressEvent((window, key, scancode, action, mods) -> {
            if(action == GLFW.GLFW_PRESS){
                switch (key){
                    case GLFW.GLFW_KEY_TAB: isVisible = !isVisible; break;
                    case GLFW.GLFW_KEY_ENTER: add("\n"); break;
                    case GLFW.GLFW_KEY_BACKSPACE: cmd.delete(cmd.length()-1,cmd.length()); refresh(); break;
                }
            }
        });
        context.addKeyCharEvent((window, codepoint) -> {
            if(isVisible){
                add(String.valueOf((char) codepoint));
            }
        });
        text.setScale(.1F,.15F,.15F);
        text.setAligment(TexturedInterfaceComponent.BOTTOM_LEFT_ALIGMENT);
        text.setCoords(0,2,0);
        prompt.setAligment(TexturedInterfaceComponent.BOTTOM_LEFT_ALIGMENT);
        prompt.setCoords(0,1,0);
        refresh();
    }

    public void add(String what){
        if(what.charAt(0) == '\n' && what.length() == 1){
            text.add(prompt);
            cmd.delete(0,cmd.length());
        }
        cmd.append(what);
        refresh();
    }

    public void refresh(){
        this.prompt = font.genFromString(cmd);
        prompt.setScale(.1F,.15F,.15F);
        prompt.setAligment(TexturedInterfaceComponent.TOP_LEFT_ALIGMENT);
        prompt.setCoords(0,1,0);
    }

    @Override
    public void draw() {
        if(isVisible){
            prompt.draw();
            text.draw();
        }
    }

    @Override
    public void drawForceShader(ShaderProgram shader) {
        if(isVisible){
            text.drawForceShader(shader);
            prompt.drawForceShader(shader);
        }
    }

    @Override
    public void doLogic(int iteration) {
        prompt.doLogic(iteration);
        text.doLogic(iteration);
    }

    @Override
    public void setUniforms(int iteration, float[] projectionMatrix) {
        prompt.setUniforms(iteration,projectionMatrix);
        text.setUniforms(iteration,projectionMatrix);
    }

    @Override
    public ShaderProgram getShader() {
        return prompt.getShader();
    }

    @Override
    public int getRenderOrderPosition() {
        return -1;
    }
}
