package net.mega2223.aguaengine3d.usecases.aero;

import net.mega2223.aguaengine3d.graphics.objects.modeling.TexturedModel;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator.ProceduralBuildingManager;
import net.mega2223.aguaengine3d.graphics.objects.shadering.TextureShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.TextureManager;
import net.mega2223.aguaengine3d.logic.Context;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.objects.WindowManager;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator.ProceduralBuilding;
import net.mega2223.aguaengine3d.misc.Utils;
import net.mega2223.aguaengine3d.usecases.aero.objects.simobjects.FlyingSimObject;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings({"ALL"})

public class Airsim {

    protected static final String TITLE = "Airsim";
    static int framesElapsed = 0;
    public static final float[] camera = {10,.9f,17,0};
    public static final int TARGET_FPS = 120;
    static WindowManager manager;
    static Context context = new Context();

    static FlyingSimObject plane;

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

        //tests

        int[][] colors = {{0,0,0},{0,255,0},{255,0,0}};
        int[][] pattern = ProceduralBuildingManager.pngToBitmap(Utils.TEXTURES_DIR+"\\BitMap.png",colors);

        ProceduralBuilding b = new ProceduralBuilding(Utils.PROCEDURAL_BUILDINGS_DIR+"\\BrickStyle1");
        TexturedModel build = b.generate(pattern, 1);
        float[] mat =  new float[16];
        MatrixTranslator.generateRotationMatrix(mat,0,0,0);

        //context.addObject(build);
        ProceduralBuilding g = new ProceduralBuilding(Utils.PROCEDURAL_BUILDINGS_DIR+"\\GrassFloor");
        //context.addObject(g.generate(pattern,2));
        ProceduralBuilding p = new ProceduralBuilding(Utils.PROCEDURAL_BUILDINGS_DIR+"\\TiledFloor");
        //context.addObject(p.generate(pattern,3));

        //air tests
        plane = new FlyingSimObject(
                //new TexturedModel(
                //        new float[]{-1,.1f,0,0 , 1,.1f,0,0 , 0,.1f,1.5f,0},
                //        new int[]{0,1,2},
                //        new float[]{0,0 , 0,1 , 1,1},
                //        TextureManager.loadTexture(Utils.TEXTURES_DIR+"\\img.png")
                //)
                TexturedModel.loadTexturedModel(Utils.readFile(Utils.MODELS_DIR+"\\cube2.obj").split("\n"),new TextureShaderProgram(), TextureManager.loadTexture(Utils.TEXTURES_DIR+"\\img.png"))
        ) {
            public void doLogic(int itneration) {
                super.doLogic(itneration);
            }
        };
        float[] ver = plane.getRelativeVertices();
        VectorTranslator.addToAllVectors(ver,0,1,0);
        plane.setVertices(ver);

        TexturedModel chessBoardFloor = new TexturedModel(
                new float[]{-1000,0,-1000,0 , -1000,0,1000,0 , 1000,0,1000,0 , 1000,0,-1000,0},
                new int[]{0,1,2,2,3,0},
                new float[]{0,0 , 0,2000 , 2000,2000 , 2000,0},
                TextureManager.loadTexture(Utils.TEXTURES_DIR+"\\xadrez.png")
        );

        context.addObject(chessBoardFloor);

        manager.addKeypressEvent((window, key, scancode, action, mods) -> {
            if(key == GLFW.GLFW_KEY_P && action == GLFW.GLFW_PRESS){
                plane.setThrottle(0);
                plane.setYawControl(0);
                plane.setPitchControl(0);
            }
            if(key == GLFW.GLFW_KEY_F3 && action == GLFW.GLFW_PRESS){
                plane.addToThrottleControl(.1f);
            }
            if(key == GLFW.GLFW_KEY_F2 && action == GLFW.GLFW_PRESS){
                plane.addToThrottleControl(-.1f);
            }

        });
        manager.addUpdateEvent(() -> {
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS) {
                plane.addToPitchControl(0.1F);
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) {
                plane.addToPitchControl(-0.1F);
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) {
                plane.addToYawControl(0.1F);
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) {
                plane.addToYawControl(-0.1F);
            }
        }
        );

        context.addObject(plane);
        plane.setCoords(10,0,17);
        //plane.setThrottle(.01f);

        //Render Logic be like:

        context.setBackGroundColor(.5f,.5f,.6f);
        context.setLight(6,0,0,0,1000);
        context.setActive(true);
        context.setFogDetails(0,100);

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
                float[] coords = plane.getCoords();
                GLFW.glfwSetWindowTitle(manager.windowName,TITLE + "    FPS: " + (framesLastSecond) + "(x: " + coords[0] + " y: " + coords[1]+ " z: " + coords[2] + ")");
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

    protected static void doRenderLogic(){/*fixme

        float asp = (float) manager.viewportSize[0]/(float) manager.viewportSize[1];
        float[] coords = plane.getCoords();
        float[] pred = plane.getRotation();
        pred = PhysicsUtils.generatePredictionVector(1,pred[0],pred[1],pred[2]);
        float[] trans = new float[16];
        //todo
        context.doLogic();
        context.doRender(trans);

        manager.update();*/
    }




}
