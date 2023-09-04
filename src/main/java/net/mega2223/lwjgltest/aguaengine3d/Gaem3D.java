package net.mega2223.lwjgltest.aguaengine3d;


import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator.ProceduralBuilding;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator.ProceduralBuildingManager;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.*;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.ShaderDictonary;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.TextureManager;
import net.mega2223.lwjgltest.aguaengine3d.logic.Context;
import net.mega2223.lwjgltest.aguaengine3d.logic.ScriptedSequence;
import net.mega2223.lwjgltest.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;
import net.mega2223.lwjgltest.aguaengine3d.objects.WindowManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused","UnnecessaryLocalVariable"})

public class Gaem3D {

    protected static final String TITLE = "3 DIMENSÇÕES";
    protected static final int D = 128;
    static int framesElapsed = 0;
    public static final float[] camera = {0,.9f,0,0};
    static float tempCamY = 0f;
    public static final int TARGET_FPS = 120;
    public static final float[] DEFAULT_SKY_COLOR = {.5f,.5f,.5f,1};
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
        //shader dict setup

        ShaderManager.setIsGlobalShaderDictEnabled(true);
        ShaderDictonary globalDict = ShaderManager.getGlobalShaderDictionary();
        globalDict.add(ShaderDictonary.fromFile(Utils.SHADERS_DIR+"\\DefaultShaderDictionary.sdc"));

        //tests

        TextureShaderProgram shaderProgram = new TextureShaderProgram();
        int[][] expectedColors = {{0,255,0},{0,0,0},{255,0,0}};
        int[][] map = ProceduralBuildingManager.pngToBitmap(Utils.TEXTURES_DIR+"\\bitmap.png",expectedColors);

        ProceduralBuilding grass = new ProceduralBuilding(Utils.PROCEDURAL_BUILDINGS_DIR+"\\GrassFloor",shaderProgram);
        ProceduralBuilding tile = new ProceduralBuilding(Utils.PROCEDURAL_BUILDINGS_DIR+"\\TiledFloor",shaderProgram);
        ProceduralBuilding building = new ProceduralBuilding(Utils.PROCEDURAL_BUILDINGS_DIR+"\\BrickStyle1",shaderProgram);

        long time = System.currentTimeMillis();
        context.addObject(grass.generate(map,1));
        context.addObject(building.generate(map,2));
        context.addObject(tile.generate(map,3));
        System.out.println("Object generation took: " + (System.currentTimeMillis() - time) + " milis");

        //rendering tests
        float[] monitorVertices = new float[]{0,0,0,0 , 0,1,0,0 , 1,0,0,0 , 1,1,0,0};
        int[] monitorIndices = new int[]{0,1,2,2,1,3};
        float[] textureCoords = new float[]{1,0, 1,1 , 0,0 , 0,1};
        int[] textureFrameB = RenderingManager.genTextureFrameBufferObject(512,512);
        int[] depthFrameB = RenderingManager.genDepthFrameBufferObject(512,512);
        DisplayShaderProgram dispSP = new DisplayShaderProgram(textureCoords,textureFrameB);
        ShaderProgram depthDispSP = new DepthDisplayShaderProgram(textureCoords,depthFrameB);
        ShaderProgram depthComputer = DepthBufferShaderProgram.GLOBAL_INSTANCE;

        Model testMonitor = new Model(
                monitorVertices,monitorIndices,dispSP
        );
        Model testMonitor2 = new Model(
                monitorVertices,monitorIndices,depthDispSP
        );
        final int[][] data = new int[2][];
        ScriptedSequence up = new ScriptedSequence("FBOUpdater") {
            final float[] projM = new float[16];
            @Override
            protected boolean shouldTrigger(int itneration, boolean isPreLogic, Context context) {return isPreLogic;}
            @Override
            protected void preLogic(int itneration, Context context) {
                MatrixTranslator.generateProjectionMatrix(projM,7F,40F, (float) Math.toRadians(45),1);
                MatrixTranslator.applyLookTransformation(projM,50,25,50,0,0,0);
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,textureFrameB[0]);
                GL30.glClear(GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_COLOR_BUFFER_BIT);
                GL30.glViewport(0,0,512,512);
                context.doCustomRender(projM);
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,depthFrameB[0]);
                GL30.glClear(GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_COLOR_BUFFER_BIT);
                GL30.glViewport(0,0,512,512);
                context.doCustomRender(projM);
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,0);
            }
        };
        testMonitor2.setCoords(2,0,0);

        context.addObject(testMonitor);
        context.addObject(testMonitor2);
        context.addScript(up);

        context.setBackGroundColor(.5f,.5f,.6f);
        context.setActive(true);
        context.setFogDetails(2000,20);
        context.setLight(0,0,100,0,10000);
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

    protected static void doLogic(){}

    static float[] trans =  new float[16];

    protected static void doRenderLogic(){
        float[] methodMatrix = new float[16];

        MatrixTranslator.generateProjectionMatrix(methodMatrix,0.01f,100.0f, (float) Math.toRadians(45),manager.viewportSize[0],manager.viewportSize[1]);
        MatrixTranslator.applyLookTransformation(methodMatrix,camera,(float) (camera[0]+Math.sin(camera[3])), camera[1], (float) (camera[2]+Math.cos(camera[3])),0,1,0);
        context.doLogic();
        manager.fitViewport();
        context.doRender(methodMatrix);
        manager.update();
    }



}
