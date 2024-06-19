package net.mega2223.aguaengine3d.tools.modeleditor;

import net.mega2223.aguaengine3d.AguaEngine;
import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.modeling.TextureInterfaceComponent;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ui.BitmapFont;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ui.DinAllocTextComponent;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.objects.WindowManager;
import net.mega2223.aguaengine3d.tools.modeleditor.commands.Help;
import org.lwjgl.glfw.GLFW;

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
        addToHistory("AguaEngine3D Version " + AguaEngine.VERSION);
        context.addUpdateEvent(() -> {
            textDisplay.setAspectRatio(context.getAspectRatio());
        });
        context.addKeypressEvent((window, key, scancode, action, mods) -> {
            if(action == GLFW.GLFW_PRESS){
                switch (key){
                    case GLFW.GLFW_KEY_TAB: isVisible = !isVisible; break;
                    case GLFW.GLFW_KEY_ENTER: if(!isVisible){break;} processCommand(); break;
                    case GLFW.GLFW_KEY_BACKSPACE: if(!isVisible){break;} eraseCharacterPrompt(); break;
                }
            }
        });
        context.addKeyCharEvent((window, codepoint) -> {
            if(isVisible){
                addToPrompt(String.valueOf((char) codepoint));
            }
        });

        commands.add(new Help(this));

        textDisplay.setScale(.05F,.075F,.075F);
        textDisplay.setAligment(TextureInterfaceComponent.BOTTOM_LEFT_ALIGMENT);
        textDisplay.setCoords(0,1,0,0);
        refresh();
    }

    public void addToPrompt(String what){
        cmd.append(what);
        refresh();
    }

    public void eraseCharacterPrompt(){
        eraseCharacterPrompt(1);
    }

    public void eraseCharacterPrompt(int num){
        cmd.delete(Math.max(cmd.length()-num,0),cmd.length());
        refresh();
    }

    public void addToHistory(CharSequence what){
        history.append(what).append('\n');
        lineBreaks++;
        refresh();
    }

    public void processCommand(){
        addToHistory(cmd);
        for(ConsoleCommand act : commands){
            if(act.respondToCommand(cmd)){break;}
        }
        cmd.delete(0,cmd.length());
        refresh();
    }

    public void refresh(){
        textDisplay.setText(
                history+"::> "+cmd,font
        );
        textDisplay.setCoords(0.01F,1+lineBreaks,0,0);
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
