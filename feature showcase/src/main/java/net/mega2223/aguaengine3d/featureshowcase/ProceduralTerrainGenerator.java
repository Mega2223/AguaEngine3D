package net.mega2223.aguaengine3d.featureshowcase;

import net.mega2223.aguaengine3d.featureshowcase.utils.GrassShaderProgram;
import net.mega2223.aguaengine3d.featureshowcase.utils.WaterShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.StructureUtils;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.Noise;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.PerlinNoise;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.StackedNoises;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.graphics.utils.ShaderDictonary;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.mathematics.MathUtils;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.misc.Utils;
import net.mega2223.aguaengine3d.objects.WindowManager;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

@SuppressWarnings({"unused"})

public class ProceduralTerrainGenerator {

    public static final int TARGET_FPS = 120;
    public static final float[] BRIGHT_SKY_COLOR = {.8f, .8f, .95f, 1};
    public static final float[] DARK_SKY_COLOR = {.0F,.0F,.01F,1};

    public static float SPEED = .075F;
    protected static final String TITLE = "Geração de terreno :)";
    public static final float[] camera = {0, .9f, 0, 0};
    public static int framesElapsed = 0;
    static WindowManager manager;
    static RenderingContext context;

    static float[] proj = new float[16];

    public static void main(String[] args) throws IOException {

        //GLFW
        manager = new WindowManager(300, 300, TITLE);
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

        //shader dict setup

        ShaderManager.setIsGlobalShaderDictEnabled(true);
        ShaderDictonary globalDict = ShaderManager.getGlobalShaderDictionary();
        globalDict.add(ShaderDictonary.fromFile(Utils.SHADERS_DIR + "\\DefaultShaderDictionary.sdc"));

        //scene setup

        context = new RenderingContext();
        context.setLight(0, 0, 10, 0, 1000)
                .setBackGroundColor(.5f, .5f, .6f)
                .setActive(true)
                .setFogDetails(100, 100);


        GrassShaderProgram grassShaderProgram = new GrassShaderProgram();
        WaterShaderProgram waterShaderProgram = new WaterShaderProgram();

        Model water = StructureUtils.generatePlane(400,1,waterShaderProgram);

        water.setCoords(-200,-.1F,-200);

        PerlinNoise perlin1 = new PerlinNoise(7,7);
        PerlinNoise perlin2 = new PerlinNoise(8,8);
        PerlinNoise perlin3 = new PerlinNoise(6,6);

        StackedNoises stackedNoises = new StackedNoises();
        perlin1.setTranslations(0,0,3.43F,3.43F);
        perlin1.setHeightScale(5);
        perlin2.setTranslations(0,0,.832F,.832F);
        perlin2.setHeightScale(1);
        perlin3.setTranslations(0,0,6.789F,6.789F);
        perlin3.setHeightScale(4);
        stackedNoises.add(perlin1);
        stackedNoises.add(perlin2);
        stackedNoises.add(perlin3);
        //Model grass = Noise.NoiseToModel(noise,64,64,4F/32F,400F/64F,grassShaderProgram);
        int siz = 5;
        Model grass = Noise.NoiseToModel(stackedNoises,-siz,-siz, siz, siz,.1F,200F/ siz,6F,grassShaderProgram);
        float[] verts = grass.getRelativeVertices();
        int[] ind = grass.getIndices();
        int largestIndex = 0; float largestDist = 0;
        for (int i = 0; i < ind.length; i+=3) {
            int i0 = ind[i];int i1 = ind[i+1];int i2 = ind[i+2];
            float x0 = verts[i0*4], y0 = verts[i0*4+1], z0 = verts[i0*4+2];
            float x1 = verts[i1*4], y1 = verts[i1*4+1], z1 = verts[i1*4+2];
            float x2 = verts[i2*4], y2 = verts[i2*4+1], z2 = verts[i2*4+2];
            float dist = (float) Math.abs(Math.sqrt((x1-x0)*(x1-x0)+(y1-y0)*(y1-y0)+(z1-z0)*(z1-z0)));
            boolean isLarger = dist > largestDist;
            largestIndex = isLarger ? i : largestIndex;
            largestDist = isLarger ? dist : largestDist;
            dist = (float) Math.abs(Math.sqrt((x2-x0)*(x2-x0)+(y2-y0)*(y2-y0)+(z2-z0)*(z2-z0)));
            isLarger = dist > largestDist;
            largestIndex = isLarger ? i : largestIndex;
            largestDist = isLarger ? dist : largestDist;
            dist = (float) Math.abs(Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)+(z1-z2)*(z1-z2)));
            isLarger = dist > largestDist;
            largestIndex = isLarger ? i : largestIndex;
            largestDist = isLarger ? dist : largestDist;
        }
        System.out.println("LARGEST TRIANGLE: ");
        int v1 = ind[largestIndex], v2 = ind[largestIndex + 1], v3 = ind[largestIndex + 2];
        int[] all = {v1,v2,v3};
        System.out.println(largestIndex/3 + ": " + v1 + ", " + v2 + ", " + v3);
        for (int i = 0; i < all.length; i++) {
            System.out.println("V"+i+": " + verts[all[i]*4] + ", " + verts[all[i]*4+1] + ", " + verts[all[i]*4+2]);
        }

        context.addObject(grass);
        context.addObject(water);
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
    }

    static float[] skyColorBuffer = new float[3];
    static final float DAY_LEN = 240F;

    protected static void doLogic() {
        float inf = (float) -Math.cos(framesElapsed / DAY_LEN) * .5F + .5F;
        for (int i = 0; i < 3; i++) {
            //skyColorBuffer[i] = MathUtils.cubicInterpolation(BRIGHT_SKY_COLOR[i],DARK_SKY_COLOR[i],inf);
        }
        //context.setBackGroundColor(skyColorBuffer[0],skyColorBuffer[1],skyColorBuffer[2]);
        context.setBackGroundColor(BRIGHT_SKY_COLOR[0],BRIGHT_SKY_COLOR[1],BRIGHT_SKY_COLOR[2]);
    }

    protected static void doRenderLogic() {
        MatrixTranslator.generatePerspectiveProjectionMatrix(proj, 0.01f, 1000f, (float) Math.toRadians(45), manager.viewportSize[0], manager.viewportSize[1]);
        MatrixTranslator.applyLookTransformation(proj, camera, (float) (camera[0] + Math.sin(camera[3])), camera[1], (float) (camera[2] + Math.cos(camera[3])), 0, 1, 0);
        context.doLogic();
        manager.fitViewport();
        context.doRender(proj);
        RenderingManager.printErrorQueue();
        manager.update();
    }

}
