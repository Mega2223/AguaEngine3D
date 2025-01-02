import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.modeling.*;
import net.mega2223.aguaengine3d.graphics.objects.shadering.SolidColorShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.graphics.utils.ShaderDictionary;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.graphics.utils.TextureManager;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.misc.Utils;
import net.mega2223.aguaengine3d.objects.WindowManager;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Civ {

    public static final int TARGET_FPS = 60;
    public static final String TITLE = "Sid Meyer nao me processa :)";

    static RenderingContext context = null;
    static WindowManager manager = null;
    static Random r = new Random();
    static long framesElapsed = 0;
    static float[] projection = new float[16];
    public static final float[] cam = {0, 0, 4, (float) Math.PI};

    private static final float[] mat4 = new float[16];

    public static void main(String[] args) {
        setup();
        Model polyhedron = Geometry.genPolyhedron(4, 0);
        context.addObject(polyhedron);
        begin();
    }

    protected static void setup(){
        manager = new WindowManager(720,720, TITLE);
        manager.init();
        ShaderManager.setIsGlobalShaderDictEnabled(true);
        ShaderDictionary globalDict = ShaderManager.getGlobalShaderDictionary();
        globalDict.addAllValues(ShaderDictionary.fromFile(Utils.SHADERS_DIR + "/DefaultShaderDictionary.sdc"));
        context = new RenderingContext();
        context.setLight(0, 0, 10, 0, 10)
                .setBackGroundColor(.5f, .5f, .6f)
                .setActive(true)
                .setFogDetails(7, 20);
        context.setActive(true);
        manager.addUpdateEvent(() -> { //walk events
            double s = Math.sin(cam[3]);
            double c = Math.cos(cam[3]);
            float speed = .075F;
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_W)==GLFW.GLFW_PRESS){cam[2] += speed*c;cam[0] += speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_S)==GLFW.GLFW_PRESS){cam[2] -= speed*c;cam[0] -= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_A)==GLFW.GLFW_PRESS){cam[0] += speed*c;cam[2]-= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_D)==GLFW.GLFW_PRESS){cam[0] -= speed*c;cam[2]+= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_Q)==GLFW.GLFW_PRESS){cam[3] += Math.PI/90;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_E)==GLFW.GLFW_PRESS){cam[3] -= Math.PI/90;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_Z)==GLFW.GLFW_PRESS){cam[1] += speed;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_X)==GLFW.GLFW_PRESS){cam[1] -= speed;}
        });
    }

    protected static void begin(){
        long notRendered = 0;
        long lastLoop = System.currentTimeMillis();
        int framesLastSecond = 0;
        long fLSLastUpdate = 0;
        while (!GLFW.glfwWindowShouldClose(manager.windowName)) {
            notRendered += System.currentTimeMillis() - lastLoop;
            lastLoop = System.currentTimeMillis();
            if (System.currentTimeMillis() - fLSLastUpdate > 1000) {
                fLSLastUpdate = System.currentTimeMillis();
                String title = TITLE + String.format(Locale.US,"    FPS: %2d,   CAM: %.3f %.3f %.3f", framesLastSecond,cam[0],cam[1],cam[2]);
                GLFW.glfwSetWindowTitle(manager.windowName, title);
                framesLastSecond = 0;
            }
            if (notRendered > (1000 / TARGET_FPS)) {
                long cycleStart = System.currentTimeMillis();
                doLogic();
                doRenderLogic();
                notRendered = 0; framesElapsed++; framesLastSecond++;
            }
        }
    }

    protected static void doLogic() {
    }

    protected static void doRenderLogic() {
        MatrixTranslator.generatePerspectiveProjectionMatrix(projection, 0.01f, 1000f, (float) Math.toRadians(45), manager.viewportSize[0], manager.viewportSize[1]);
        MatrixTranslator.applyLookTransformation(cam, (float) (cam[0] + Math.sin(cam[3])), cam[1], (float) (cam[2] + Math.cos(cam[3])), 0, 1, 0, projection);
        context.doLogic();
        manager.fitViewport();
        context.doRender(projection);
        RenderingManager.printErrorQueue();
        manager.update();
    }
}
