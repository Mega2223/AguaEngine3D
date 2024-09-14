package net.mega2223.aguaengine3d.tools.modeleditor;

import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.utils.ShaderDictonary;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.misc.Utils;
import net.mega2223.aguaengine3d.objects.WindowManager;
import net.mega2223.aguaengine3d.tools.modeleditor.objects.Console;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;

public class ModelEditor {
    public static final int TARGET_FPS = 60;
    public static RenderingContext context = new RenderingContext();
    static WindowManager windowManager = new WindowManager(400,400,"Janela :D");
    static Console console;

    private static final float[] projectionMatrix = new float[16];

    public static void main(String[] args) {
        windowManager.init();

        ShaderManager.setIsGlobalShaderDictEnabled(true);
        ShaderDictonary globalDict = ShaderManager.getGlobalShaderDictionary();
        globalDict.addAllValues(ShaderDictonary.fromFile(Utils.SHADERS_DIR + "\\DefaultShaderDictionary.sdc"));
        context.setLight(1,0,10,0,1000);
        context.setFogDetails(1000,1000);
        console = new Console(windowManager,Misc.loadConsolas());
        context.addObject(console);

        GL30.glClearColor(.5F,.5F,.6F,1F);

        for(long frames = 0, lastRender = 0, currentRender, unrendered; !GLFW.glfwWindowShouldClose(windowManager.getWindow()) ;){
            currentRender = System.currentTimeMillis();
            unrendered = currentRender - lastRender;
            if(unrendered*TARGET_FPS > 1000){
                doLogic(frames);
                doRender();
                windowManager.update();
                lastRender = currentRender;
                frames++;
                GLFW.glfwSetWindowTitle(windowManager.windowName,frames+"");
            }
        }
    }

    public static void doLogic(long iteration){

    }
    public static void doRender(){
        MatrixTranslator.generateIsometricProjectionMatrix(projectionMatrix,1F);
        context.doRender(projectionMatrix);

    }

}
