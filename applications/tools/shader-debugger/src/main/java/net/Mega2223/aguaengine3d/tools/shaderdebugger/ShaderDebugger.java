package net.Mega2223.aguaengine3d.tools.shaderdebugger;

import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.graphics.utils.ShaderDictionary;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.misc.Utils;
import net.mega2223.aguaengine3d.objects.WindowManager;
import org.lwjgl.glfw.GLFW;

public class ShaderDebugger {
    static RenderingContext context;
    static WindowManager manager;
    static String TITLE = "SHADER DEBUG";
    static final int TARGET_FPS = 60;
    static final float[] camera = {0, 0, -1, 0};
    static final float[] proj = new float[16];
    static int framesElapsed = 0;

    public static void main(String[] args) {

        //GLFW
        manager = new WindowManager(300, 300, TITLE);
        manager.init();
        manager.addUpdateEvent(() -> { //walk events
            double s = Math.sin(camera[3]);
            double c = Math.cos(camera[3]);
            float speed = .05F;
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_W)==GLFW.GLFW_PRESS){camera[2] += speed*c;camera[0] += speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_S)==GLFW.GLFW_PRESS){camera[2] -= speed*c;camera[0] -= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_A)==GLFW.GLFW_PRESS){camera[0] += speed*c;camera[2]-= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_D)==GLFW.GLFW_PRESS){camera[0] -= speed*c;camera[2]+= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_Q)==GLFW.GLFW_PRESS){camera[3] += Math.PI/90;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_E)==GLFW.GLFW_PRESS){camera[3] -= Math.PI/90;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_Z)==GLFW.GLFW_PRESS){camera[1] += speed;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_X)==GLFW.GLFW_PRESS){camera[1] -= speed;}
        });

        //shader dict setup
        ShaderManager.setIsGlobalShaderDictEnabled(true);
        ShaderDictionary globalDict = ShaderManager.getGlobalShaderDictionary();
        globalDict.addAllValues(ShaderDictionary.fromFile(Utils.SHADERS_DIR + "\\DefaultShaderDictionary.sdc"));
        context = new RenderingContext();

        //scenery setup
        context.setLight(0, 0, 10, 0, 10000)
                .setBackGroundColor(.5f, .5f, .6f)
                .setActive(true)
                .setFogDetails(500, 1000);

        Model mod = new Model(
                new float[]{-10,-10,5,0, 10,-10,5,0, -10,10,5,0, 10,10,5,0},
                new int[]{0,1,2,1,2,3},
                new LoadableShaderProgram()
        );

        context.addObject(mod);

        //Render Logic be like:
        long unrendered = 0;
        long lastLoop = System.currentTimeMillis();
        int framesLastSecond = 0;
        long fLSLastUpdate = 0;
        context.setActive(true);

        while (!GLFW.glfwWindowShouldClose(manager.windowName)) {
            unrendered += System.currentTimeMillis() - lastLoop;
            lastLoop = System.currentTimeMillis();
            if (System.currentTimeMillis() - fLSLastUpdate > 1000) {
                fLSLastUpdate = System.currentTimeMillis();
                GLFW.glfwSetWindowTitle(manager.windowName, TITLE + "    FPS: " + (framesLastSecond) + "(x: " + camera[0] + " z:" + camera[2] + ")");
                framesLastSecond = 0;

            }
            if (unrendered > (1000 / TARGET_FPS)) {
                long cycleStart = System.currentTimeMillis();
                doLogic();
                doRenderLogic();
                unrendered = 0;
                framesElapsed++;
                framesLastSecond++;
            }
        }

    }
    protected static void doLogic() {

    }

    protected static void doRenderLogic() {
        MatrixTranslator.generatePerspectiveProjectionMatrix(proj, 0.01f, 1000f, (float) Math.toRadians(45), manager.viewportSize[0], manager.viewportSize[1]);
        MatrixTranslator.applyLookTransformation(camera, (float) (camera[0] + Math.sin(camera[3])), camera[1], (float) (camera[2] + Math.cos(camera[3])), 0, 1, 0, proj);
        context.doLogic();
        manager.fitViewport();
        context.doRender(proj);
        RenderingManager.printErrorQueue();
        manager.update();
    }
}
