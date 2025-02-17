package net.mega2223.aguaengine3d.featureshowcase.proceduralbuildinggenerator;

import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.modeling.TexturedModel;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator.ProceduralBuilding;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.graphics.utils.ShaderDictionary;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.misc.Utils;
import net.mega2223.aguaengine3d.objects.WindowManager;
import org.lwjgl.glfw.GLFW;

import java.util.Random;

public class BuildingGenerator {
    static RenderingContext context;
    static WindowManager manager;
    static String TITLE = "Procedural Buildings :D";
    static final int TARGET_FPS = 120;
    static final float[] camera = {0, 4, 0, 0};
    static final float[] proj = new float[16];
    static int framesElapsed = 0;

    public static void main(String[] args) {

        //GLFW
        manager = new WindowManager(300, 300, TITLE);
        manager.init();
        manager.addUpdateEvent(() -> { //walk events
            double s = Math.sin(camera[3]);
            double c = Math.cos(camera[3]);
            float speed = .075F;
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
                camera[2] += speed * c;
                camera[0] += speed * s;
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
                camera[2] -= speed * c;
                camera[0] -= speed * s;
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
                camera[0] += speed * c;
                camera[2] -= speed * s;
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
                camera[0] -= speed * c;
                camera[2] += speed * s;
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_Q) == GLFW.GLFW_PRESS) {
                camera[3] += Math.PI / 90;
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_E) == GLFW.GLFW_PRESS) {
                camera[3] -= Math.PI / 90;
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_Z) == GLFW.GLFW_PRESS) {
                camera[1] += speed;
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_X) == GLFW.GLFW_PRESS) {
                camera[1] -= speed;
            }
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

        //TexturedModel chessFloor = new TexturedModel(
        //        new float[]{-50, 0, -50, 0, 50, 0, -50, 0, -50, 0, 50, 0, 50, 0, 50, 0},
        //        new int[]{0, 1, 2, 2, 1, 3},
        //        new float[]{0, 0, 100, 0, 0, 100, 100, 100},
        //        TextureManager.loadTexture(Utils.TEXTURES_DIR + "\\xadrez.png")
        //);
        //context.addObject(chessFloor);

        //terrain setup
        int[][] map = {
                {3, 3, 3, 1, 2, 2, 2, 1, 16, 16, 16, 1, 17, 17, 17},
                {3, 3, 3, 1, 2, 2, 2, 1, 16, 16, 16, 1, 17, 17, 17},
                {3, 3, 3, 1, 2, 2, 2, 1, 16, 16, 16, 1, 17, 17, 17},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {4, 4, 4, 1, 5, 5, 5, 1, 6, 6, 6, 1, 7, 7, 7},
                {4, 4, 4, 1, 5, 5, 5, 1, 6, 6, 6, 1, 7, 7, 7},
                {4, 4, 4, 1, 5, 5, 5, 1, 6, 6, 6, 1, 7, 7, 7},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {9, 9, 9, 1, 10, 10, 10, 1, 11, 11, 11, 1, 12, 12, 12},
                {9, 9, 9, 1, 10, 10, 10, 1, 11, 11, 11, 1, 12, 12, 12},
                {9, 9, 9, 1, 10, 10, 10, 1, 11, 11, 11, 1, 12, 12, 12},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {13, 13, 13, 1, 14, 14, 14, 1, 15, 15, 15, 1, 8, 8, 8},
                {13, 13, 13, 1, 14, 14, 14, 1, 15, 15, 15, 1, 8, 8, 8},
                {13, 13, 13, 1, 14, 14, 14, 1, 15, 15, 15, 1, 8, 8, 8}
        };
        ProceduralBuilding road = new ProceduralBuilding(Utils.PROCEDURAL_BUILDINGS_DIR + "\\ImprovedRoads");
        float scale = 18;
        TexturedModel roads = road.generate(map, 1, scale);
        ProceduralBuilding[] bricks = {
                new ProceduralBuilding(Utils.PROCEDURAL_BUILDINGS_DIR + "\\BrickStyle2"),
                new ProceduralBuilding(Utils.PROCEDURAL_BUILDINGS_DIR+"\\BrickStyle3"),
                new ProceduralBuilding(Utils.PROCEDURAL_BUILDINGS_DIR+"\\BrickStyle4")
        };
        Random r = new Random();
        roads.setCoords(-15 * scale * 0.5F, 0, -15 * scale * 0.5F);
        context.addObject(roads);
        for (int i = 2; i < 18; i++) {
            System.out.println(i);
            TexturedModel gen2 = bricks[r.nextInt(bricks.length)].generate(map, i, 10, scale);
            gen2.setCoords(-15 * scale * 0.5F, 0, -15 * scale * 0.5F);
            context.addObject(gen2);
        }

        //Render Logic be like:
        long unrendered = 0;
        long lastLoop = System.currentTimeMillis();
        int framesLastSecond = 0;
        long fLSLastUpdate = 0;
        //long lastCycleDuration = 0; STOP SCREAMING AT ME INTELLIJ I GET IT
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
                //cpu logic
                doLogic();
                //render logic
                doRenderLogic();
                unrendered = 0;
                framesElapsed++;
                //lastCycleDuration = System.currentTimeMillis() - cycleStart;
                framesLastSecond++;
            }
        }

    }
    protected static void doLogic() {

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
