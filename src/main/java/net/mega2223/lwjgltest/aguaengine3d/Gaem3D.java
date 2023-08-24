package net.mega2223.lwjgltest.aguaengine3d;


import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.TexturedModel;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator.ProceduralBuilding;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator.ProceduralBuildingManager;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.*;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.ShaderDictonary;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.TextureManager;
import net.mega2223.lwjgltest.aguaengine3d.logic.Context;
import net.mega2223.lwjgltest.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;
import net.mega2223.lwjgltest.aguaengine3d.objects.WindowManager;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

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

        ShaderManager.setIsGlobalShaderDictEnabled(true);
        ShaderDictonary globalDict = ShaderManager.getGlobalShaderDictionary();
        globalDict.add(ShaderDictonary.fromFile(Utils.SHADERS_DIR+"\\DefaultShaderDictionary.sdc"));

        TextureShaderProgram shaderProgram = new TextureShaderProgram();
        int[][] expectedColors = {{0,255,0},{0,0,0},{255,0,0}};
        int[][] map = ProceduralBuildingManager.pngToBitmap(Utils.TEXTURES_DIR+"\\bitmap.png",expectedColors);

        ProceduralBuilding grass = new ProceduralBuilding(Utils.PROCEDURAL_BUILDINGS_DIR+"\\GrassFloor",shaderProgram);
        ProceduralBuilding tile = new ProceduralBuilding(Utils.PROCEDURAL_BUILDINGS_DIR+"\\TiledFloor",shaderProgram);
        ProceduralBuilding building = new ProceduralBuilding(Utils.PROCEDURAL_BUILDINGS_DIR+"\\BrickStyle1",shaderProgram);


        long time = System.currentTimeMillis();
        context.addObject(grass.generate(map,1));
        context.addObject(tile.generate(map,3));
        System.out.println("Object generation took: " + (System.currentTimeMillis() - time) + " milis");

        TexturedModel buildingModel = TexturedModel.loadTexturedModel(
                Utils.readFile(Utils.MODELS_DIR+"\\buildingModel.obj").split("\n"),
                new TextureShaderProgram(),
                TextureManager.loadTexture(Utils.PROCEDURAL_BUILDINGS_DIR+"\\BrickStyle1\\Texture.png")
        );
        Model am = Model.loadModel(
                Utils.readFile(Utils.MODELS_DIR+"\\IMPOSTER.obj").split("\n"),
                new SolidColorShaderProgram(5f,.3f,.2f)
        );
        am.setCoords(new float[]{10,0,10,0});
        context.addObject(buildingModel);
        context.addObject(am);



        Model triang = new Model(
                new float[]{0,0,0,0 , 0,10,0,0 , 10,0,0,0},
                new int[]{0,1,2},
                new MultipleColorsShaderProgram(new float[]{0,1,0,0 , 1,0,0,0 , 0,0,1,0})
        ){
            final float[] ret = new float[16];
            @Override
            public void doLogic(int itneration) {
                super.doLogic(itneration);
                MatrixTranslator.generateRotationMatrix(ret,0,(float) -itneration/100,0);
                //new Matrix4f().identity().rotationXYZ(0f,(float) -itneration/100,0f).get(ret);
                shader.setRotationMatrix(ret);
            }
        };
        
        TexturedModel mod = new TexturedModel(
                new float[]{/*-1.5f,0,0,0 , -1.5f,3,0,0 , 1.5f,0,0,0 , 1.5f,3,0,0*/},
                new int[]{/*0,1,2,1,2,3*/},
                new float[]{/*1,1 , 1,0 , 0,1 , 0,0*/},
                TextureManager.loadTexture(Utils.TEXTURES_DIR+"\\img.png")
        ){
            float[] rMatrix = new float[16];
            @Override
            public void doLogic(int itneration) {
                super.doLogic(itneration);
                float itnerationF = itneration;
                MatrixTranslator.generateRotationMatrix(rMatrix,itnerationF / 909, itnerationF /100,0);
                this.getShader().setRotationMatrix(rMatrix);
            }
        };
        mod.setCoords(10,0,20);


        context.addObject(mod);
        context.addObject(triang);

        context.setBackGroundColor(.5f,.5f,.6f);
        context.setLight(0,32,2,32,30);
        //context.setLight(6,0,0,0,10);
        context.setActive(true);
        context.setFogDetails(5,20);

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
        /*double sin = Math.sin((float) framesElapsed / 280);
        double cos = Math.cos((float) framesElapsed / 280);
        context.setLight(0,10,4,25,(float) (sin +.9)*6);
        context.setLight(1,10,1,10,.5f);
        context.setLightColor(1,1,0,0,.5f);
        context.setBackGroundColor((float) cos/3, (float) cos/3,(float)(sin+.75f)/2);*/
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
