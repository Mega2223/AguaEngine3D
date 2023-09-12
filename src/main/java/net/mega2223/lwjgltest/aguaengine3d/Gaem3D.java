package net.mega2223.lwjgltest.aguaengine3d;


import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.InterfaceComponent;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.TexturedModel;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator.ProceduralBuilding;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator.ProceduralBuildingManager;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.*;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.ShaderDictonary;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.TextureManager;
import net.mega2223.lwjgltest.aguaengine3d.logic.Context;
import net.mega2223.lwjgltest.aguaengine3d.logic.LightSpaceRenderingManager;
import net.mega2223.lwjgltest.aguaengine3d.logic.ScriptedSequence;
import net.mega2223.lwjgltest.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.lwjgltest.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;
import net.mega2223.lwjgltest.aguaengine3d.objects.WindowManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;

@SuppressWarnings({"unused","UnnecessaryLocalVariable"})

public class Gaem3D {

    protected static final String TITLE = "3 DIMENSÇÕES";
    protected static final int D = 512;
    public static int framesElapsed = 0;
    private static final float[] camera = {0,.9f,0,0};
    public static final float[] lightPerspective = {0,0,0,0};
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
            float speed = .3F;
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_W)==GLFW.GLFW_PRESS){camera[2] += speed*c;camera[0] += speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_S)==GLFW.GLFW_PRESS){camera[2] -= speed*c;camera[0] -= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_A)==GLFW.GLFW_PRESS){camera[0] += speed*c;camera[2]-= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_D)==GLFW.GLFW_PRESS){camera[0] -= speed*c;camera[2]+= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_Q)==GLFW.GLFW_PRESS){camera[3] += Math.PI/90;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_E)==GLFW.GLFW_PRESS){camera[3] -= Math.PI/90;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_Z)==GLFW.GLFW_PRESS){camera[1] += speed;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_X)==GLFW.GLFW_PRESS){camera[1] -= speed;}

            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_UP)==GLFW.GLFW_PRESS){lightPerspective[2] += speed*c;lightPerspective[0] += speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_DOWN)==GLFW.GLFW_PRESS){lightPerspective[2] -= speed*c;lightPerspective[0] -= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_LEFT)==GLFW.GLFW_PRESS){lightPerspective[0] += speed*c;lightPerspective[2]-= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_RIGHT)==GLFW.GLFW_PRESS){lightPerspective[0] -= speed*c;lightPerspective[2] += speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_PAGE_UP)==GLFW.GLFW_PRESS){lightPerspective[1] += speed;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_PAGE_DOWN)==GLFW.GLFW_PRESS){lightPerspective[1] -= speed;}

        });

        //GLFW.glfwMaximizeWindow(manager.getWindow());
        //shader dict setup

        ShaderManager.setIsGlobalShaderDictEnabled(true);
        ShaderDictonary globalDict = ShaderManager.getGlobalShaderDictionary();
        globalDict.add(ShaderDictonary.fromFile(Utils.SHADERS_DIR+"\\DefaultShaderDictionary.sdc"));

        //tests

        //rendering tests
        float[] monitorVertices = new float[]{0,0,0,0 , 0,.5F,0,0 , .25F,0,0,0 , .25F,.5F,0,0};
        int[] monitorIndices = new int[]{0,1,2,2,1,3};
        float[] textureCoords = new float[]{1,0, 1,1 , 0,0 , 0,1};
        int[] textureFrameB = RenderingManager.genTextureFrameBufferObject(D,D);

        context.setEnableShadowsForLight(0,true);
        context.setEnableShadowsForLight(1,true);
        context.setEnableShadowsForLight(2,true);
        context.setEnableShadowsForLight(3,true);
        context.setLightColor(0,1,0,0,.2F);
        context.setLightColor(1,1,1,0,.2F);
        context.setLightColor(2,0,1,0,.2F);
        context.setLightColor(3,0,0,1,.2F);

        context.getLightSpaceRenderingManager().renderLightmapsAsNeeded();

        int[] depthFrameB = context.getLightSpaceRenderingManager().getAssossiatedFBO(0);

        DisplayShaderProgram dispSP = new DisplayShaderProgram(textureCoords,textureFrameB);
        ShaderProgram depthDispSP = new DepthDisplayShaderProgram(textureCoords,depthFrameB);

        Model testMonitor = new InterfaceComponent(
               monitorVertices,monitorIndices,dispSP
        );
        Model testMonitor2 = new InterfaceComponent(
                monitorVertices,monitorIndices,depthDispSP
        );
        testMonitor.setCoords(-.6F,0,0);

        ScriptedSequence FBOUpdater = new ScriptedSequence("FBOUpdater") {
            final float[] projM = new float[16];
            @Override
            protected boolean shouldTrigger(int itneration, boolean isPreLogic, Context context) {return isPreLogic;}
            @Override
            protected void preLogic(int itneration, Context context) {
                context.getLightSpaceRenderingManager().genProjectionMatrixForLight(projM,0);
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,textureFrameB[0]);
                GL30.glClear(GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_COLOR_BUFFER_BIT);
                GL30.glViewport(0,0,D,D);
                context.doCustomRender(projM);
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,0);
                context.getLightSpaceRenderingManager().renderLightmapsAsNeeded();
            }
        };

        //context.addObject(testMonitor);
        //context.addObject(testMonitor2);
        context.addScript(FBOUpdater);

        /*building generator
        i have no idea why, but initializing this stuff before the stuff above causes the depth frame buffer to be empty
        weirldy enough, it only happens when:
        1- this is executed BEFORE the monitors are placed/initialized
        2- The TextureFragShader.fsh initializes it's doShadowMapping uniform (and OpenGL does not optimizes it away)
        3- If the buildings do not use the TextureFragShader program this will also happen in some situations

          I cannot stress this enough, the depth buffer is computed with the DepthAlg fragment and vertex shaders
          and the monitor itself is rendered with the DepthDisplay fragment shader and the Display vertex shader.
          changes in the TextureFragShader.fsh shader should NOT affect the depth display framebuffer whatsoever.
          I have ABSOLUTELY NO IDEA why this happens, if you do please submit a PR or contact me.
          this may very well require a line by line analisis of the shader initialization process

        * Current theories:
        * - Shader initialization is wrong somehow, i have no idea why changing the TextureFragShader affects the rendering tho
        * It's worth noting that the shaders that work with depth calculations do not extend the TemplateShaderProgram class, that could be somehow omitting important initialization steps
        * - Since the status of the int[] doShadowmapping at the TextureFragShader.fsh file affects this, this very well supports the theory of
        * shader inialization being messed up somehow, I'll look into this deeper
        * - Maybe the texture is fine and it's displaying that is wrong somehow
        * */

        /*int[][] expectedColors = {{0,255,0},{0,0,0},{255,0,0}};
        int[][] map = ProceduralBuildingManager.pngToBitmap(Utils.TEXTURES_DIR+"\\bitmap.png",expectedColors);
        ShaderProgram textureShaderProgram = new TextureShaderProgram();
        ProceduralBuilding grass = new ProceduralBuilding(Utils.PROCEDURAL_BUILDINGS_DIR+"\\GrassFloor",textureShaderProgram);
        ProceduralBuilding tile = new ProceduralBuilding(Utils.PROCEDURAL_BUILDINGS_DIR+"\\TiledFloor",textureShaderProgram);
        ProceduralBuilding building = new ProceduralBuilding(Utils.PROCEDURAL_BUILDINGS_DIR+"\\BrickStyle1",textureShaderProgram);
        long time = System.currentTimeMillis();
        context.addObject(grass.generate(map,1));
        TexturedModel buildingModel = building.generate(map, 2);
        context.addObject(buildingModel);
        context.addObject(tile.generate(map,3));
        System.out.println("Object generation took: " + (System.currentTimeMillis() - time) + " milis");
        */

        TexturedModel chessFloor = new TexturedModel(
                new float[]{-50,0,-50,0 , 50,0,-50,0 , -50,0,50,0 , 50,0,50,0},
                new int[]{0,1,2,2,1,3},
                new float[]{0,0 , 100,0 , 0,100 , 100,100},
                TextureManager.loadTexture(Utils.TEXTURES_DIR+"\\xadrez.png")
        );
        TexturedModel cube = TexturedModel.loadTexturedModel(
                Utils.readFile(Utils.MODELS_DIR+"\\cube.obj").split("\n"),new TextureShaderProgram(),
                TextureManager.loadTexture(Utils.TEXTURES_DIR+"\\img.png")
        );

        TexturedModel lightFollower = TexturedModel.loadTexturedModel(
                Utils.readFile(Utils.MODELS_DIR+"\\cube.obj").split("\n"),new TextureShaderProgram(),
                TextureManager.loadTexture(Utils.TEXTURES_DIR+"\\img.png")
        );
        float[] v = lightFollower.getRelativeVertices();
        VectorTranslator.scaleAllVectors(v,.1F);
        lightFollower.setVertices(v);

        context.addScript(new ScriptedSequence("lightPosCameraUpdater") {
            @Override
            protected boolean shouldTrigger(int itneration, boolean isPreLogic, Context context) {
                return isPreLogic;
            }

            @Override
            protected void preLogic(int itneration, Context context) {
                lightFollower.setCoords(lightPerspective[0],lightPerspective[1]+1,lightPerspective[2]);
            }
        });

        //cube.setCoords(0,1,0);
        context.addObject(chessFloor);
        context.addObject(cube);
        context.addObject(lightFollower);

        context.setBackGroundColor(.5f,.5f,.6f);
        context.setActive(true);
        context.setFogDetails(2000,0);

        RenderingManager.printErrorQueue();

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

        context.setLight(0,
                (float) (Math.cos(framesElapsed/60F)/2F)*40,1,
                (float) (Math.sin(framesElapsed/60F)/2F)*40,10);
        context.setLight(1,
                (float) (Math.cos(framesElapsed/60F+Math.PI)/2F)*40,1,
                (float) (Math.sin(framesElapsed/60F+Math.PI)/2F)*40,10);
        context.setLight(2,
                (float) (Math.cos(framesElapsed/60F+Math.PI/2)/2F)*40,1,
                (float) (Math.sin(framesElapsed/60F+Math.PI/2)/2F)*40,10);
        context.setLight(3,
                (float) (Math.cos(framesElapsed/60F+Math.PI*1.5F)/2F)*40,1,
                (float) (Math.sin(framesElapsed/60F+Math.PI*1.5F)/2F)*40,10);


    }

    static float[] trans =  new float[16];
    static float[] proj = new float[16];

    protected static void doRenderLogic(){
        MatrixTranslator.generateProjectionMatrix(proj,0.01f,100.0f, (float) Math.toRadians(45),manager.viewportSize[0],manager.viewportSize[1]);
        MatrixTranslator.applyLookTransformation(proj,camera,(float) (camera[0]+Math.sin(camera[3])), camera[1], (float) (camera[2]+Math.cos(camera[3])),0,1,0);
        context.doLogic();
        manager.fitViewport();
        context.doRender(proj);
        RenderingManager.printErrorQueue();
        manager.update();
    }



}
