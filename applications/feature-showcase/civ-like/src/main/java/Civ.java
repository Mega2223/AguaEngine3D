import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.shadering.SolidColorShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.graphics.utils.ShaderDictionary;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.misc.Utils;
import net.mega2223.aguaengine3d.objects.WindowManager;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class Civ {

    public static final int TARGET_FPS = 60;
    public static final String TITLE = "Sid Meyer nao me processa :)";

    static RenderingContext context = null;
    static WindowManager manager = null;
    static long framesElapsed = 0;
    static float[] projection = new float[16];
    public static final float[] cam = {0, 0, -4, 0};

    public static void main(String[] args) {
        setup();
        Model teste = Geometry.genPolygon(6,1);
        teste = new Model(
                teste.getRelativeVertices(), teste.getIndices(), new SolidColorShaderProgram(1,1,1)
        );
        context.addObject(teste);
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
                GLFW.glfwSetWindowTitle(manager.windowName, TITLE + "    FPS: " + (framesLastSecond));
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
