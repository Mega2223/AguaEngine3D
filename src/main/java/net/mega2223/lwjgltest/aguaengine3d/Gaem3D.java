package net.mega2223.lwjgltest.aguaengine3d;


import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.TexturedModel;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.DisplayBasedTextureShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.SolidColorShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.TextureShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.TextureManager;
import net.mega2223.lwjgltest.aguaengine3d.logic.Context;
import net.mega2223.lwjgltest.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.lwjgltest.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;
import net.mega2223.lwjgltest.aguaengine3d.objects.WindowManager;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;

import java.util.Vector;

@SuppressWarnings("unused")

public class Gaem3D {

    protected static final String TITLE = "3 DIMENSÇÕES";
    static int framesElapsed = 0;
    public static final float[] camera = {0,.9f,0,0};
    public static final int TARGET_FPS = 120;
    public static final float[] DEFAULT_SKY_COLOR = {.5f,.5f,.5f,1};
    static WindowManager manager;
    static Context context = new Context();

    public static void main(String[] args) {

        //GLFW
        manager = new WindowManager(300,300, TITLE);
        manager.init();
        manager.addUpdateEvent(() -> {
            double s = Math.sin(camera[3]);
            double c = Math.cos(camera[3]);
            float speed = .03f;
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_W)==GLFW.GLFW_PRESS){camera[2] += speed*c;camera[0] += speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_S)==GLFW.GLFW_PRESS){camera[2] -= speed*c;camera[0] -= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_A)==GLFW.GLFW_PRESS){camera[0] += speed*c;camera[2]-= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_D)==GLFW.GLFW_PRESS){camera[0] -= speed*c;camera[2]+= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_Q)==GLFW.GLFW_PRESS){camera[3] += Math.PI/90;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_E)==GLFW.GLFW_PRESS){camera[3] -= Math.PI/90;}
        });

        //pra eu não ter que maximizar na gravação
        GLFW.glfwMaximizeWindow(manager.getWindow());

        //tests

        int chessTexture = TextureManager.loadTexture(Utils.TEXTURES_DIR + "\\xadrez.png");
        TexturedModel chessBoardFloor = new TexturedModel(
                new float[]{-120,0,-120,0, 120,0,-120,0, 120,0,120,0, -120,0,120,0},
                new int[]{0,1,2,3,2,0},
                new float[]{0,0,240,0,240,240,0,240},
                chessTexture
        );
        context.addObject(chessBoardFloor);

        TexturedModel texturedCube = TexturedModel.loadTexturedModel(
                Utils.readFile("C:\\Users\\Imperiums\\Documents\\TexturedCube.obj").split("\n"),new TextureShaderProgram(),
                TextureManager.loadTexture("C:\\Users\\Imperiums\\Documents\\TexturedCube.png")
        );
        context.addObject(texturedCube);
        texturedCube.setCoords(new float[]{0,1,0,0});
        cube = texturedCube;

        context.setLight(0,0,1,0,.8f);
        context.setLightColor(0,0,0,0,0);

        context.setFogDetails(10,2.5f);
        context.setBackGroundColor(new float[]{0,0,0,0});
        //context.setBackGroundColor(new float[]{.35f,.35f,.35f,1});

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
    static TexturedModel cube;
    protected static void doLogic(){
        cube.getShader().setLight(0,0,1,0,.8f);
        float r = (float) Math.sin((float)framesElapsed/15), g = (float) Math.sin((float)framesElapsed/10), b = (float) Math.cos((float)framesElapsed/(45/2));
        cube.getShader().setLightColor(0,r/2+.5f,g/2+.5f,b/2+.5f,.35f);
        float[] v = cube.getRelativeVertices();
        for (int i = 0; i < v.length; i+=4) {
            MatrixTranslator.rotateVector3(v,0,.04,0,i);
        }
        float s = (float) Math.sin((double) framesElapsed/40)/2 + .5f;
        cube.setCoords(new float[]{0,s+1,0,0});
        cube.setVertices(v);
    }

    protected static void doRenderLogic(){

        float asp = (float) manager.viewportSize[0]/(float) manager.viewportSize[1];
        Matrix4f proj = new Matrix4f().perspective((float) Math.toRadians(45.0f), asp, 0.01f, 100.0f)
                .lookAt(camera[0], camera[1], camera[2],
                        (float) (camera[0]+Math.sin(camera[3])), camera[1], (float) (camera[2]+Math.cos(camera[3])),
                        0.0f, 1.0f, 0.0f);
        float[] trans = {1,0,0,0 , 0,1,0,0 , 0,0,1,0, 0,0,0,1};
        float[] trans2 = {1,0,0,0 , 0,1,0,0 , 0,0,1,0, 0,0,0,1};

        proj.get(trans);

        context.doLogic();
        context.doRender(trans);

        manager.update();
    }



}
