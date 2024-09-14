package net.mega2223.aguaengine3d.featureshowcase.proceduralterrain;

import net.mega2223.aguaengine3d.featureshowcase.proceduralterrain.utils.GrassShaderProgram;
import net.mega2223.aguaengine3d.featureshowcase.proceduralterrain.utils.WaterShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.StructureUtils;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.Noise;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.PerlinNoise;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.RadialNoise;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.StackedNoises;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.graphics.utils.ShaderDictionary;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
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
    static StackedNoises stackedNoises;
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
        ShaderDictionary globalDict = ShaderManager.getGlobalShaderDictionary();
        globalDict.addAllValues(ShaderDictionary.fromFile(Utils.SHADERS_DIR + "\\DefaultShaderDictionary.sdc"));

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
        PerlinNoise perlin1 = new PerlinNoise(12,12);
        PerlinNoise perlin3 = new PerlinNoise(16,16);
        PerlinNoise perlin4 = new PerlinNoise(20,20);

        stackedNoises = new StackedNoises();
        perlin1.setTranslations(0,0,8F,8F);
        perlin1.setHeightScale(3F);
        perlin2.setTranslations(0,0,2F,2F);
        perlin2.setHeightScale(3F/2);
        perlin3.setTranslations(0,0,1F,1F);
        perlin3.setHeightScale(3F/3);
        perlin4.setTranslations(0,0,.5F,.5F);
        perlin4.setHeightScale(3F/4);

        stackedNoises.add(perlin1);
        stackedNoises.add(perlin2);
        stackedNoises.add(perlin3);
        stackedNoises.add(perlin4);
        RadialNoise radial1 = new RadialNoise(0, 0, 0.5F, RadialNoise.SQUARE,true);
        radial1.setTranslations(0,0,2.5F,2.5F);
        radial1.setDislocation(.5F);

        stackedNoises.add(radial1);

//        ProceduralBuilding build = new ProceduralBuilding(Utils.PROCEDURAL_BUILDINGS_DIR+"\\BrickStyle1",new TextureShaderProgram());
//        int[][] map = new int[16][16];
//        TexturedModel generate = build.generate(map, 0);
//        generate.setCoords(-8,stackedNoises.get(0,0),-8);
//        context.addObject(generate);

        //Model grass = Noise.NoiseToModel(noise,64,64,4F/32F,400F/64F,grassShaderProgram);
        int siz = 5;
        List<Noise> noises = stackedNoises.getNoises();

        File out = new File(System.getProperty("user.dir")+"\\src\\main\\resources\\noises");
        out.mkdirs();

        for (int i = 0; i < noises.size(); i++) {
            ImageIO.write(
                    Noise.NoiseToImage(noises.get(i),1024,1024,-siz,-siz,(2*siz)/1024F,new float[]{0,1,0},new float[]{0,0,1},false),
                    "png",new File(out.getAbsolutePath()+"\\noise_"+(i+1)+".png"));
        }
        ImageIO.write(
                Noise.NoiseToImage(stackedNoises,1024,1024,-siz,-siz,(2*siz)/1024F,new float[]{0,1,0},new float[]{0,0,1},false),
                "png",new File(out.getAbsolutePath()+"\\all.png"));

        Model grass = Noise.NoiseToModel(stackedNoises,-siz,-siz, siz, siz,.1F,200F/ siz,10F,grassShaderProgram);

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
        camera[1] = stackedNoises.get(camera[0]/40,camera[2]/40) * 10 + 1.5F;
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
