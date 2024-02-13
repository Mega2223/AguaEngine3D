package net.mega2223.aguaengine3d.featureshowcase;

import net.mega2223.aguaengine3d.featureshowcase.utils.GrassShaderProgram;
import net.mega2223.aguaengine3d.featureshowcase.utils.WaterShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.StructureUtils;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator.ProceduralBuilding;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.Noise;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.PerlinNoise;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.StackedNoises;
import net.mega2223.aguaengine3d.graphics.objects.shadering.TextureShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.graphics.utils.ShaderDictonary;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.mathematics.MathUtils;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.misc.Utils;
import net.mega2223.aguaengine3d.objects.WindowManager;
import org.lwjgl.glfw.GLFW;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

@SuppressWarnings({"unused"})

public class ProceduralTerrainGenerator {

    public static final int TARGET_FPS = 120;
    public static final float[] BRIGHT_SKY_COLOR = {.8f, .8f, .95f, 1};
    public static final float[] DARK_SKY_COLOR = {.0F,.0F,.01F,1};

    public static float SPEED = .05F;
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
                //.setFogDetails(1000, 1000);

        GrassShaderProgram grassShaderProgram = new GrassShaderProgram();
        WaterShaderProgram waterShaderProgram = new WaterShaderProgram();

        Model water = StructureUtils.generatePlane(400,1,waterShaderProgram);

        water.setCoords(-200,-.1F,-200);

        PerlinNoise perlin2 = new PerlinNoise(8,8);
        PerlinNoise perlin1 = new PerlinNoise(8,8);
        PerlinNoise perlin3 = new PerlinNoise(8,8);
        PerlinNoise perlin4 = new PerlinNoise(8,8);

        StackedNoises stackedNoises = new StackedNoises();
        perlin1.setTranslations(0,0,8F,8F);
        perlin1.setHeightScale(2F);
        perlin2.setTranslations(0,0,2F,2F);
        perlin2.setHeightScale(1.5F);
        perlin3.setTranslations(0,0,1F,1F);
        perlin3.setHeightScale(1F);
        perlin4.setTranslations(0,0,1F/3F,1F/3F);
        perlin4.setHeightScale(1F);

        stackedNoises.add(perlin1);
        stackedNoises.add(perlin2);
        stackedNoises.add(perlin3);
        stackedNoises.add(perlin4);

        ProceduralBuilding build = new ProceduralBuilding(Utils.PROCEDURAL_BUILDINGS_DIR+"\\BrickStyle1",new TextureShaderProgram());
        int[][] map = new int[16][16];

        context.addObject(build.generate(map,0));

        //Model grass = Noise.NoiseToModel(noise,64,64,4F/32F,400F/64F,grassShaderProgram);
        int siz = 5;
        List<Noise> noises = stackedNoises.getNoises();
        for (int i = 0; i < noises.size(); i++) {
            ImageIO.write(
                    Noise.NoiseToImage(noises.get(i),1024,1024,-siz,-siz,(2*siz)/1024F,true),
                    "png",new File(Utils.USER_DIR+"\\feature showcase\\src\\main\\resources\\noises\\noise_"+(i+1)+".png"));
        }
        ImageIO.write(
                Noise.NoiseToImage(stackedNoises,1024,1024,-siz,-siz,(2*siz)/1024F,true),
                "png",new File(Utils.USER_DIR+"\\feature showcase\\src\\main\\resources\\noises\\all.png"));

        Model grass = Noise.NoiseToModel(stackedNoises,-siz,-siz, siz, siz,.1F,200F/ siz,6F,grassShaderProgram);

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
