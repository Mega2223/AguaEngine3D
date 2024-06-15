package net.mega2223.aguaengine3d.tools.modeleditor;

import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.modeling.InterfaceComponent;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ModelUtils;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ui.TextManipulator;
import net.mega2223.aguaengine3d.graphics.objects.shadering.SolidColorShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.ShaderDictonary;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.misc.Utils;
import net.mega2223.aguaengine3d.objects.WindowManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;

public class ModelEditor {
    public static final int TARGET_FPS = 60;
    static RenderingContext context = new RenderingContext();
    static WindowManager windowManager = new WindowManager(400,400,"Janela :D");
    static Console console;

    static float[] projectionMatrix = new float[16];

    public static void main(String[] args) {
        windowManager.init();

        ShaderManager.setIsGlobalShaderDictEnabled(true);
        ShaderDictonary globalDict = ShaderManager.getGlobalShaderDictionary();
        globalDict.addAllValues(ShaderDictonary.fromFile(Utils.SHADERS_DIR + "\\DefaultShaderDictionary.sdc"));
        context.setLight(1,0,10,0,1000);
        context.setFogDetails(1000,1000);
        //console = new Console(windowManager,Misc.loadConsolas());

        InterfaceComponent teste = Misc.loadConsolas().genFromString("TESTE");
        ModelUtils.rescaleModel(teste,5);
        context.addObject(
                teste
        );

//        context.addObject(new Model(
//                new float[]{0,0,0,0, 0,1,0,0, 1,0,0,0, 1,1,0,0},
//                new int[]{0,1,2,3,2,1},
//                new SolidColorShaderProgram(1,0,0,1)
//        ));

        GL30.glClearColor(.5F,.5F,.6F,1F);

        for(long frames = 0, lastRender = 0, currentRender, unrendered; !GLFW.glfwWindowShouldClose(windowManager.getWindow()) ; frames++){
            currentRender = System.currentTimeMillis();
            unrendered = currentRender - lastRender;
            if(unrendered > 1000 / TARGET_FPS){
                doLogic();
                doRender();
                windowManager.update();
                lastRender = currentRender;
            }
        }
    }

    public static void doLogic(){

    }
    public static void doRender(){
        MatrixTranslator.generateIsometricProjectionMatrix(projectionMatrix,1F);
        context.doRender(projectionMatrix);

    }

}
