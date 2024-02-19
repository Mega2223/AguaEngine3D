package net.mega2223.aguaengine3d.featureshowcase.proceduralworldgenerator;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.Noise;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.PerlinNoise;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.StackedNoises;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.shadering.SolidColorShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.graphics.utils.ShaderDictonary;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.misc.Utils;
import net.mega2223.aguaengine3d.objects.WindowManager;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class WorldGen {

    public static final String TITLE = "World Generator :o";
    public static final float SPEED = .05F;
    public static final int TARGET_FPS = 60;
    public static long framesElapsed = 0L;

    //MAPA
    //SKYBOX
    //ILHAS
    //CONSRUÇÕES
    //estrelas? seria bonito
    //4 skyboxes interpoladas seria o ideal, tem como rotar skyboxes?

    static StackedNoises map = new StackedNoises();
    static RenderingContext context = new RenderingContext();
    static WindowManager manager = new WindowManager(300,300,TITLE);
    static float[] camera = new float[4];
    public static final List<Integer> generatedLocations = new LinkedList<>();

    public static void main(String[] args) {
        //GLFW, OPENGL & Context setup:
        manager.init();
        manager.addUpdateEvent(() -> { //walk events
            double s = Math.sin(camera[3]);
            double c = Math.cos(camera[3]);
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_W)==GLFW.GLFW_PRESS){camera[2] += SPEED*c;camera[0] += SPEED*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_S)==GLFW.GLFW_PRESS){camera[2] -= SPEED*c;camera[0] -= SPEED*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_A)==GLFW.GLFW_PRESS){camera[0] += SPEED*c;camera[2]-= SPEED*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_D)==GLFW.GLFW_PRESS){camera[0] -= SPEED*c;camera[2]+= SPEED*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_Q)==GLFW.GLFW_PRESS){camera[3] += Math.PI/90;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_E)==GLFW.GLFW_PRESS){camera[3] -= Math.PI/90;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_Z)==GLFW.GLFW_PRESS){camera[1] += SPEED;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_X)==GLFW.GLFW_PRESS){camera[1] -= SPEED;}
        });
        context = new RenderingContext();
        context.setLight(0, 0, 10, 0, 1000)
                .setBackGroundColor(.5f, .5f, .6f)
                .setActive(true)
                .setFogDetails(1000, 100);
        ShaderManager.setIsGlobalShaderDictEnabled(true);
        ShaderDictonary globalDict = ShaderManager.getGlobalShaderDictionary();
        globalDict.add(ShaderDictonary.fromFile(Utils.SHADERS_DIR + "\\DefaultShaderDictionary.sdc"));

        //Logic setup:

        modelAssembler.start();

        PerlinNoise perlinMajor = new PerlinNoise(37,37);
        perlinMajor.setTranslations(0,0,256,256);
        perlinMajor.setHeightScale(42);
        perlinMajor.setDislocation(-.4F);

        PerlinNoise perlinMinor1 = new PerlinNoise(32, 32);
        perlinMinor1.setTranslations(0,0,22,22);
        perlinMinor1.setHeightScale(16);

        map.add(perlinMinor1);
        map.add(perlinMajor);
        context.addObject(new Model(
                new float[]{-160,0,-160,0, 160,0,-160,0, 160,0,160,0, -160,0,160,0},
                new int[]{0,1,2,3,2,0},
                new SolidColorShaderProgram(.2F,.2F,.4F)
        ));


        GRASS_SHADER = new GrassShaderProgram();

        for (int i = -16; i < 16; i++) {
            for (int j = -16; j < 16; j++) {
                requestRender(i,j);
            }
        }

        //Render Logic:
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
                GLFW.glfwSetWindowTitle(manager.windowName, TITLE + "    FPS: " + (framesLastSecond) + "(x: " + camera[0] + " y:" + camera[1] +  " z:" + camera[2] + ")");
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
        System.exit(0);
    }

    public static void doLogic(){
        for (Renderable act : readyToUse){
            ((Model)act).setShader(GRASS_SHADER);
            context.addObject(act);
        }
        readyToUse.clear();
    }
    private static final float[] proj = new float[16];
    public static void doRenderLogic(){
        MatrixTranslator.generatePerspectiveProjectionMatrix(proj, 0.01f, 1000f, (float) Math.toRadians(45), manager.viewportSize[0], manager.viewportSize[1]);
        MatrixTranslator.applyLookTransformation(proj, camera, (float) (camera[0] + Math.sin(camera[3])), camera[1], (float) (camera[2] + Math.cos(camera[3])), 0, 1, 0);
        context.doLogic();
        manager.fitViewport();
        context.doRender(proj);
        RenderingManager.printErrorQueue();
        manager.update();
    }

    public static ShaderProgram GRASS_SHADER;
    public static Collection<Renderable> readyToUse = new ArrayBlockingQueue<>(32*32);
    public static List<int[]> toMake = new ArrayList<>();
    public static Thread modelAssembler = new Thread(() -> {
        while (true) {
            if(toMake.isEmpty()){
                try{Thread.sleep(500);} catch (InterruptedException ignored){}
                continue;
            }
            int[] req = toMake.get(0);
            genModelForReg(req[0], req[1]);
            toMake.remove(req);
        }
    });
    public static void requestRender(int x, int z){
        toMake.add(new int[]{x,z});
    }
    public static void genModelForReg(int x, int z){
        generatedLocations.add(x); generatedLocations.add(z);
        float xF = x*100F - 50F; float zF = z*100F - 50F;
        Model model = Noise.NoiseToModel(map, xF, zF, xF + 100F, zF + 100F, 1F, 1F, 1F, null);
        readyToUse.add(model);
    }
}
