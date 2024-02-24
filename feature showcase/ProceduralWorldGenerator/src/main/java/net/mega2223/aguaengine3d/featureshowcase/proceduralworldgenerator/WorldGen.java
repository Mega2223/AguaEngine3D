package net.mega2223.aguaengine3d.featureshowcase.proceduralworldgenerator;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.ScriptedSequence;
import net.mega2223.aguaengine3d.graphics.objects.modeling.InterfaceComponent;
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
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class WorldGen {

    public static final String TITLE = "World Generator :o";
    public static final float SPEED = .05F * 100;
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
    static float mapHeight = 1000F;
    public static final List<Integer> generatedLocations = new LinkedList<>();
    public static MapComponent mapView;

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
        manager.addKeypressEvent((window,key,scancode,action,mods) -> {
            if(action != GLFW.GLFW_PRESS){return;}
            if(key == GLFW.GLFW_KEY_PAGE_UP){
                mapHeight += 200;
            } else if (key == GLFW.GLFW_KEY_PAGE_DOWN){
                mapHeight -= 200;
            }
        });

        context = new RenderingContext();
        context.setLight(0, 0, 10, 0, 1000)
                .setBackGroundColor(.5f, .5f, .6f)
                .setActive(true)
                .setFogDetails(1000, 100);
        ShaderManager.setIsGlobalShaderDictEnabled(true);
        ShaderDictonary globalDict = ShaderManager.getGlobalShaderDictionary();
        globalDict.add(ShaderDictonary.fromFile(Utils.SHADERS_DIR + "\\DefaultShaderDictionary.sdc"));

        //Map setup:
        final int[] mapViewTexture = RenderingManager.genTextureFrameBufferObject(64,64);
        final float mapSize = .35F;
        mapView = new MapComponent(mapSize,900,mapViewTexture);

        context.addScript(mapView.updateSequence);
        context.addObject(mapView);
        //Logic setup:

        modelAssembler.start();

        PerlinNoise perlinMajor1 = new PerlinNoise(26,26);
        perlinMajor1.setTranslations(0,0,512,512);
        perlinMajor1.setHeightScale(-90);
        perlinMajor1.setDislocation(-6);

        PerlinNoise perlinMajor2 = new PerlinNoise(37,37);
        perlinMajor2.setTranslations(0,0,512,512);
        perlinMajor2.setHeightScale(-60);
        perlinMajor2.setDislocation(-13);

        PerlinNoise perlinMinor1 = new PerlinNoise(32, 32);
        perlinMinor1.setTranslations(0,0,256,256);
        perlinMinor1.setHeightScale(-30);

        PerlinNoise perlinMinor2 = new PerlinNoise(33, 33);
        perlinMinor2.setTranslations(0,0,128,128);
        perlinMinor2.setHeightScale(-15);

        PerlinNoise perlinMinor3 = new PerlinNoise(34, 34);
        perlinMinor3.setTranslations(0,0,64,64);
        perlinMinor3.setHeightScale(-7.5F);


        map.add(perlinMinor1);
        map.add(perlinMinor2);
        map.add(perlinMinor3);
        map.add(perlinMajor1);
        map.add(perlinMajor2);
        context.addObject(new Model(
                new float[]{-160,0,-160,0, 160,0,-160,0, 160,0,160,0, -160,0,160,0},
                new int[]{0,1,2,3,2,0},
                new SolidColorShaderProgram(.2F,.2F,.4F)
        ));


        GRASS_SHADER = new GrassShaderProgram();

        for (int i = -100; i < 100; i++) {
            for (int j = -100; j < 100; j++) {
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
    static final ArrayList<Renderable> toRemove = new ArrayList<>();
    public static void doLogic(){
        for (Renderable act : readyToUse){
            ((Model)act).setShader(GRASS_SHADER);
            context.addObject(act);
            toRemove.add(act);
        }
        readyToUse.removeAll(toRemove);
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
        Model model = Noise.NoiseToModel(map, xF, zF, xF + 100.1F, zF + 100.1F, 1F, 1F, 1F, null);
        while(readyToUse.size() >= 32 * 32){
            System.out.println("WAITING FOR THE MODEL QUEUE");
            try { Thread.sleep(10);
            } catch (InterruptedException ignored) {}
        }
        readyToUse.add(model);
    }
}
