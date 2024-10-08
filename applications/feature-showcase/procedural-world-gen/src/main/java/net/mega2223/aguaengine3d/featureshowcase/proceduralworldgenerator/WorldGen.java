package net.mega2223.aguaengine3d.featureshowcase.proceduralworldgenerator;

import net.mega2223.aguaengine3d.featureshowcase.proceduralworldgenerator.daycycle.CycleKeyframe;
import net.mega2223.aguaengine3d.featureshowcase.proceduralworldgenerator.daycycle.CycleTimeline;
import net.mega2223.aguaengine3d.featureshowcase.proceduralworldgenerator.shaders.GrassShaderProgram;
import net.mega2223.aguaengine3d.featureshowcase.proceduralworldgenerator.shaders.SkyShaderProgram;
import net.mega2223.aguaengine3d.featureshowcase.proceduralworldgenerator.shaders.WaterShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.ScriptedSequence;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.Noise;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.PerlinNoise;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.StackedNoises;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.UniformNoise;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.graphics.utils.ShaderDictionary;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.interpolation.CubicInterpolator;
import net.mega2223.aguaengine3d.mathematics.interpolation.LinearInterpolator;
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
    public static final int RENDER_DIST = 3;
    public static final float STEP = .6F;
    public static final Thread MAIN_THREAD = Thread.currentThread();
    public static final CycleTimeline TIMELINE = new CycleTimeline();
    public static final float CYCLE_SPEED = .005F * .5F;//0.01f
    public static Skybox skybox;
    public static long framesElapsed = 0L;

    public static GrassShaderProgram GRASS_SHADER;
    public static WaterShaderProgram WATER_SHADER;
    public static SkyShaderProgram SKY_SHADER;
    public static Collection<Renderable> readyToUse = new ArrayBlockingQueue<>(32*32);
    public static List<int[]> toMake = new ArrayList<>();

    static final float[] skyColor = new float[4];
    private static final float[] proj = new float[16];
    private static final float[] unitV = {0,0,1,0};
    private static final float[] bufferVec = new float[4], bufferMat = new float[16];

    //MAPA
    //SKYBOX
    //ILHAS
    //CONSRUÇÕES
    //estrelas? seria bonito
    //4 skyboxes interpoladas seria o ideal, tem como rotar skyboxes?

    static StackedNoises map = new StackedNoises();
    static RenderingContext context = new RenderingContext();
    static WindowManager manager = new WindowManager(300,300,TITLE);
    static float[] camera = new float[5];
    static int[] currentChunk = {0XF,0};
    static float mapHeight = 150F;
    public static final List<int[]> generatedLocations = new LinkedList<>();
    static final ArrayList<Renderable> toRemove = new ArrayList<>();
    public static MapComponent mapView;
    private static float cycleTime;

    public static void main(String[] args) {
        //GLFW, OPENGL & Context setup:
        manager.init();
        manager.addUpdateEvent(() -> { //walk events
            double s = Math.sin(camera[3]);
            double c = Math.cos(camera[3]);
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
                camera[2] += (float) (SPEED * c); camera[0] += (float) (SPEED * s); }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
                camera[2] -= (float) (SPEED * c); camera[0] -= (float) (SPEED * s); }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
                camera[0] += (float) (SPEED * c); camera[2] -= (float) (SPEED * s); }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
                camera[0] -= (float) (SPEED * c); camera[2] += (float) (SPEED * s); }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_Q) == GLFW.GLFW_PRESS) {
                camera[3] += (float) (Math.PI / 90); }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_E) == GLFW.GLFW_PRESS) {
                camera[3] -= (float) (Math.PI / 90); }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_Z) == GLFW.GLFW_PRESS) {
                camera[1] += SPEED; }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_X) == GLFW.GLFW_PRESS) {
                camera[1] -= SPEED; }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_M) == GLFW.GLFW_PRESS) {
                camera[4] -= (float) (Math.PI / 90); }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_N) == GLFW.GLFW_PRESS) {
                camera[4] += (float) (Math.PI / 90); }
        });
        manager.addKeypressEvent((window, key, scancode, action, mods) -> {
            if (action != GLFW.GLFW_PRESS) {
                return;
            }
            if (key == GLFW.GLFW_KEY_PAGE_UP) {
                mapHeight += 50;
            } else if (key == GLFW.GLFW_KEY_PAGE_DOWN) {
                mapHeight -= 50;
            }
            mapHeight = Math.max(50, mapHeight);
        });

        context = new RenderingContext();
        context.setLight(0, 0, 10, 0, 1000)
                .setBackGroundColor(.5f, .5f, .6f)
                .setActive(true)
                .setFogDetails(1000, 100);
        ShaderManager.setIsGlobalShaderDictEnabled(true);
        ShaderDictionary globalDict = ShaderManager.getGlobalShaderDictionary();
        globalDict.addAllValues(ShaderDictionary.fromFile(Utils.SHADERS_DIR + "\\DefaultShaderDictionary.sdc"));

        //Skybox setup
        skybox = new Skybox();
        context.addObject(skybox);

        final float[] brightSky = {135F/255F, 206F/255F, 250F/255F};
        final float[] morningSky = {120F/255F, 180F/255F, 240F/255F};
        final float[] bluerSky = {115F/255F, 180F/255F, 250F/255F};
        final float[] sunset = {120F/255F,60F/255F,95F/255F};
        final float[] darkBlue = {.05F,.05F,.2F};
        final float[] darkSky = {.005F,.005F,.1F};

        final float[] cloudColor = {1,1,1};
        final float[] fogData = {-1,-1};

        TIMELINE.add(new CycleKeyframe(0F/24F,darkSky,cloudColor,fogData,1.2F));
        TIMELINE.add(new CycleKeyframe(3F/24F,darkSky,cloudColor,fogData,1));
        TIMELINE.add(new CycleKeyframe(5.6F/24F,darkSky,cloudColor,fogData,1));
        TIMELINE.add(new CycleKeyframe(7/24F,morningSky,cloudColor,fogData,0));
        TIMELINE.add(new CycleKeyframe(12/24F,brightSky,cloudColor,fogData,0));
        TIMELINE.add(new CycleKeyframe(17.3F/24F,bluerSky,cloudColor,fogData,0));
        TIMELINE.add(new CycleKeyframe(18F/24F,sunset,cloudColor,fogData,0));
        TIMELINE.add(new CycleKeyframe(18.5F/24F,darkBlue,cloudColor,fogData,1));
        TIMELINE.add(new CycleKeyframe(21/24F,darkSky,cloudColor,fogData,1));

        TIMELINE.setInterpolationMethod(CubicInterpolator.INSTANCE);

        //Map setup:

        final float mapSize = .35F;
        int mapS = 128;
        mapView = new MapComponent(mapSize, mapS, mapS);

        context.addScript(mapView.updateSequence);
        //context.addObject(mapView);
        //Logic setup:

        modelAssembler.start();

        for (int i = 0; i < 8; i++) {
            int i1 = i + 1;
            int s = 8 + i1 + i1 * 2 + i1 * 3;
            PerlinNoise per = new PerlinNoise(s, s, 1);
            // interesting float decay = (float) (Math.pow(i1,2.5F) * .025);
            float decay = (float) (Math.pow(i1, 1.75F) * .035);
            per.setTranslations(0, 0, 8 / decay, 8 / decay);
            per.setHeightScale(1F / decay);
            per.setDislocation(-126F / decay);
            per.setInterpolationMethod(LinearInterpolator.INSTANCE);
            map.add(per);
        }
        map.setDislocation(-map.get(0, 0));

        WATER_SHADER = new WaterShaderProgram();
        Model water = Noise.NoiseToModel(new UniformNoise(0), -220, -220, 220F, 220F, 1, 1F, 1F, WATER_SHADER);

        context.addObject(water);
        water.setCoords(0,0.0187446F,0);
        context.addScript(new ScriptedSequence("water_pos") {
            @Override
            protected void preLogic(int iteration, RenderingContext context) {
                water.setCoords(camera[0],0,camera[2]);
            }
        });
        GRASS_SHADER = new GrassShaderProgram();

        //Render Logic:
        long unrendered = 0;
        long lastLoop = System.currentTimeMillis();
        int framesLastSecond = 0;
        int fps = 0;
        long fpsLastUpdate = 0;
        context.setActive(true);
        while (!GLFW.glfwWindowShouldClose(manager.windowName)) {
            unrendered += System.currentTimeMillis() - lastLoop;
            lastLoop = System.currentTimeMillis();
            if (System.currentTimeMillis() - fpsLastUpdate > 1000) {
                fpsLastUpdate = System.currentTimeMillis();
                fps = framesLastSecond;
                framesLastSecond = 0;
                int timeHour = (int)(cycleTime * 24) % 24, timeMin = (int)(((cycleTime * 24)%1F)*60F) % 60;
                GLFW.glfwSetWindowTitle(manager.windowName, TITLE + "    FPS: " + fps +
                        " (x: " + camera[0] + " y: " + camera[1] + " z: " + camera[2] + ") (T=" +
                        ((timeHour < 10) ? "0" + timeHour : timeHour) + ":" +
                        (timeMin < 10 ? "0" + timeMin : timeMin) + ")"
                );
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
        int curX = (int) Math.floor((camera[0]+50F)/100F), curZ = (int) Math.floor((camera[2]+50F)/100F);
        if(curX != currentChunk[0] || curZ != currentChunk[1]){
            int dist = RENDER_DIST;
            for (int i = -dist; i < dist; i++) {
                for (int j = -dist; j < dist; j++) {
                    requestRender(curX+i,curZ+j);
                }
            }
        }

        for (Renderable act : readyToUse){
            ((Model)act).setShader(GRASS_SHADER);
            context.addObject(act);
            toRemove.add(act);
        }
        readyToUse.removeAll(toRemove);
        currentChunk[0] = curX; currentChunk[1] = curZ;
        cycleTime = (CYCLE_SPEED * ((framesElapsed) / 60F)) + .25F;
        float cycleHeight = (float) -Math.cos(2 * cycleTime * Math.PI);
        GRASS_SHADER.setLightDirection((float) Math.sin(2 * cycleTime * Math.PI), cycleHeight,0);
        WATER_SHADER.setLightDirection((float) Math.sin(2 * cycleTime  * Math.PI), cycleHeight,0);
        SKY_SHADER.setLightDirection((float) Math.sin(2 * cycleTime * Math.PI), cycleHeight,0);

        TIMELINE.getSkyColor(cycleTime,skyColor);
        context.setBackGroundColor(skyColor[0],skyColor[1],skyColor[2]);
    }

    public static void doRenderLogic(){
        camera[1] = map.get(camera[0], camera[2])+2F;
        MatrixTranslator.generatePerspectiveProjectionMatrix(proj, 0.01f, 1000f, (float) Math.toRadians(45), manager.viewportSize[0], manager.viewportSize[1]);
        MatrixTranslator.generateRotationMatrix(0,camera[3],camera[4],bufferMat);
        MatrixTranslator.multiplyVec4Mat4(unitV,bufferMat,bufferVec);
        skybox.setSkyboxTranslation(camera[0],camera[1],camera[2],bufferVec[0], 0,bufferVec[2]);
        MatrixTranslator.applyLookTransformation(camera, camera[0]+bufferVec[0], camera[1]+bufferVec[1], camera[2]+bufferVec[2], 0, 1, 0, proj);
        context.doLogic();
        manager.fitViewport();
        context.doRender(proj);
        RenderingManager.printErrorQueue();
        manager.update();
    }

    public final static Thread modelAssembler = new Thread(() -> {
        while (MAIN_THREAD.isAlive()) {
            if (toMake.isEmpty()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
                continue;
            }
            int[] req = toMake.get(0);
            genModelForReg(req[0], req[1]);
            toMake.remove(req);
        }
    });

    public static void requestRender(int x, int z){
        for (int[] act : generatedLocations){
            if(act[0]==x&&act[1]==z){return;}
        }
        toMake.add(new int[]{x,z});
    }

    public static void genModelForReg(int x, int z){
        generatedLocations.add(new int[]{x,z});
        float xF = x*100F - 50F; float zF = z*100F - 50F;
        Model model = Noise.NoiseToModel(map, xF - STEP/10, zF - STEP/10, xF + 100.1F + (STEP/10), zF + 100.1F + (STEP/10), STEP, 1F, 1F, null);
        while(readyToUse.size() >= 32 * 32){
            System.out.println("WAITING FOR THE MODEL QUEUE");
            try { Thread.sleep(10);
            } catch (InterruptedException ignored) {}
        }
        readyToUse.add(model);
    }
}
