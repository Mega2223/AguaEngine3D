package net.mega2223.lwjgltest.aguaengine3d.usecases.Airsim;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.TexturedModel;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator.ProceduralBuilding;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator.ProceduralBuildingManager;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.MultipleColorsShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.TextureShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.TextureManager;
import net.mega2223.lwjgltest.aguaengine3d.logic.Context;
import net.mega2223.lwjgltest.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;
import net.mega2223.lwjgltest.aguaengine3d.objects.WindowManager;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;

public class Airsim {

    protected static final String TITLE = "Airsim";
    static int framesElapsed = 0;
    public static final float[] camera = {10,.9f,17,0};
    public static final int TARGET_FPS = 120;
    static WindowManager manager;
    static Context context = new Context();

    public static void main(String[] args) {

        //GLFW
        manager = new WindowManager(300,300, TITLE);
        manager.init();
        manager.addUpdateEvent(() -> { //walk events
            double s = Math.sin(camera[3]);
            double c = Math.cos(camera[3]);
            float speed = .03f;
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_W)==GLFW.GLFW_PRESS){camera[2] += speed*c;camera[0] += speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_S)==GLFW.GLFW_PRESS){camera[2] -= speed*c;camera[0] -= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_A)==GLFW.GLFW_PRESS){camera[0] += speed*c;camera[2]-= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_D)==GLFW.GLFW_PRESS){camera[0] -= speed*c;camera[2]+= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_Q)==GLFW.GLFW_PRESS){camera[3] += Math.PI/90;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_E)==GLFW.GLFW_PRESS){camera[3] -= Math.PI/90;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_Z)==GLFW.GLFW_PRESS){camera[1] += speed;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_X)==GLFW.GLFW_PRESS){camera[1] -= speed;}
        });
        GLFW.glfwMaximizeWindow(manager.getWindow());

        //tests

        int[][] colors = {{0,0,0},{0,255,0},{255,0,0}};
        int[][] pattern = ProceduralBuildingManager.pngToBitmap(Utils.TEXTURES_DIR+"\\BitMap.png",colors);

        ProceduralBuilding b = new ProceduralBuilding(Utils.PROCEDURAL_BUILDINGS_DIR+"\\BrickStyle1");
        TexturedModel build = b.generate(pattern, 1);
        float[] mat =  new float[16];
        MatrixTranslator.generateRotatioMatrix(mat,0.5f,0,0);
        ((TextureShaderProgram) build.getShader()).setRotationMatrix(mat);
        context.addObject(build);
        ProceduralBuilding g = new ProceduralBuilding(Utils.PROCEDURAL_BUILDINGS_DIR+"\\GrassFloor");
        context.addObject(g.generate(pattern,2));
        ProceduralBuilding p = new ProceduralBuilding(Utils.PROCEDURAL_BUILDINGS_DIR+"\\TiledFloor");
        context.addObject(p.generate(pattern,3));
        context.setBackGroundColor(.5f,.5f,.6f);
        context.setLight(6,0,0,0,1000);
        context.setActive(true);
        context.setFogDetails(1500,0);

        //air tests
        FlyingSimObject plane = new FlyingSimObject(
                TexturedModel.loadTexturedModel(
                        Utils.readFile(Utils.MODELS_DIR+"\\cube.obj").split("\n"),
                        new TextureShaderProgram(),
                        TextureManager.loadTexture(Utils.TEXTURES_DIR+"\\tijolo.png")
                )
        ) {
            public void doLogic(int itneration) {
                super.doLogic(itneration);
            }
        };
        plane.setThrottle(.01F);
        manager.addKeypressEvent((window, key, scancode, action, mods) -> {
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS) {
                plane.addToPitchControl(0.1F);
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) {
                plane.addToPitchControl(-0.1F);
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) {
                plane.addToYawControl(-0.1F);
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) {
                plane.addToYawControl(0.1F);
            }

                    System.out.println("P"+plane.getPitchControl());
        }
        );

        context.addObject(plane);
        plane.setCoords(10,0,17);
        //plane.setThrottle(.01f);

        //Render Logic be like:
        long unrendered = 0;
        final long applicationStart = System.currentTimeMillis();
        long lastLoop = applicationStart;
        int framesLastSecond = 0;
        long fLSLastUpdate = 0;
        long lastCycleDuration = 0;
        context.setActive(true);

        while (!GLFW.glfwWindowShouldClose(manager.windowName)) {
            unrendered += System.currentTimeMillis() - lastLoop;
            lastLoop = System.currentTimeMillis();
            if(System.currentTimeMillis() - fLSLastUpdate > 1000){
                fLSLastUpdate = System.currentTimeMillis();
                GLFW.glfwSetWindowTitle(manager.windowName,TITLE + "    FPS: " + (framesLastSecond) + "(x: " + camera[0] + " z:" + camera[2] + ")");
                framesLastSecond = 0;

            }
            if (unrendered > (1000 / TARGET_FPS)) {
                long cycleStart = System.currentTimeMillis();

                //experimentation zone

                //cpu logic
                doLogic();
                //render logic
                doRenderLogic();
                unrendered = 0;
                framesElapsed++;
                lastCycleDuration = System.currentTimeMillis() - cycleStart;
                framesLastSecond++;
            }
        }
    }

    protected static void doLogic(){

    }

    protected static void doRenderLogic(){

        float asp = (float) manager.viewportSize[0]/(float) manager.viewportSize[1];
        Matrix4f proj = new Matrix4f().perspective((float) Math.toRadians(45.0f), asp, 0.01f, 10000.0f)
                .lookAt(camera[0], camera[1], camera[2],
                        (float) (camera[0]+Math.sin(camera[3])), camera[1], (float) (camera[2]+Math.cos(camera[3])),
                        0.0f, 1.0f, 0.0f);
        float[] trans = {1,0,0,0 , 0,1,0,0 , 0,0,1,0, 0,0,0,1};
        proj.get(trans);

        context.doLogic();
        context.doRender(trans);

        manager.update();
    }




}
