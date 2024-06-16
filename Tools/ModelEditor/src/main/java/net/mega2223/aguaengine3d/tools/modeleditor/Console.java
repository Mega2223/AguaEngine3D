package net.mega2223.aguaengine3d.tools.modeleditor;

import net.mega2223.aguaengine3d.AguaEngine;
import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.modeling.TextureInterfaceComponent;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ui.BitmapFont;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ui.DinAllocTextComponent;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.objects.WindowManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;

public class Console implements Renderable {

    protected BitmapFont font;

    protected StringBuilder history = new StringBuilder();
    protected StringBuilder cmd = new StringBuilder();
    protected int lineBreaks = 0;

    public boolean isVisible = false;
    public DinAllocTextComponent textDisplay;

    public ArrayList<ConsoleCommand> commands  = new ArrayList<>(10);

    public Console(WindowManager context, BitmapFont font) {
        this.font = font;
        textDisplay = DinAllocTextComponent.generate(history.toString(),font);
        add("AguaEngine3D Version " + AguaEngine.VERSION);
        add("\n");
        context.addUpdateEvent(() -> {
            textDisplay.setAspectRatio(context.getAspectRatio());
        });
        context.addKeypressEvent((window, key, scancode, action, mods) -> {
            if(action == GLFW.GLFW_PRESS){
                switch (key){
                    case GLFW.GLFW_KEY_TAB: isVisible = !isVisible; break;
                    case GLFW.GLFW_KEY_ENTER: if(!isVisible){break;} add("\n"); break;
                    case GLFW.GLFW_KEY_BACKSPACE: if(!isVisible){break;} cmd.delete(cmd.length()-1,cmd.length()); refresh(); break;
                }
            }
        });
        context.addKeyCharEvent((window, codepoint) -> {
            if(isVisible){
                add(String.valueOf((char) codepoint));
            }
        });
        textDisplay.setScale(.05F,.075F,.075F);
        textDisplay.setAligment(TextureInterfaceComponent.BOTTOM_LEFT_ALIGMENT);
        textDisplay.setCoords(0,1,0,0);
        refresh();
    }

    public void add(String what){
        if(what.charAt(0) == '\n' && what.length() == 1){
            history.append(cmd).append('\n');
            cmd.delete(0,cmd.length()); lineBreaks++;
            refresh();
            return;
        }
        cmd.append(what);
        refresh();
    }

    public void refresh(){
        textDisplay.setText(
                history+"::> "+cmd,font
        );
        textDisplay.setCoords(0,1+lineBreaks,0,0);
        textDisplay.refreshVBOS();
    }

    @Override
    public void draw() {
        if(isVisible){
            textDisplay.draw();
        }
    }

    @Override
    public void drawForceShader(ShaderProgram shader) {
        if(isVisible){
            textDisplay.drawForceShader(shader);
        }
    }

    @Override
    public void doLogic(int iteration) {
        textDisplay.doLogic(iteration);
    }

    @Override
    public void setUniforms(int iteration, float[] projectionMatrix) {
        textDisplay.setUniforms(iteration,projectionMatrix);
    }

    @Override
    public ShaderProgram getShader() {
        return textDisplay.getShader();
    }

    @Override
    public int getRenderOrderPosition() {
        return -1;
    }
}
