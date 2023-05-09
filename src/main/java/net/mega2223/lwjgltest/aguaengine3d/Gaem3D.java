package net.mega2223.lwjgltest.aguaengine3d;


import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.TexturedModel;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.DisplayBasedTextureShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.SolidColorShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.TextureShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.TextureManager;
import net.mega2223.lwjgltest.aguaengine3d.logic.Context;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator.ProceduralBuilding;
import net.mega2223.lwjgltest.aguaengine3d.objects.WindowManager;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;

@SuppressWarnings("unused")

public class Gaem3D {

    protected static final String TITLE = "3 DIMENSÇÕES";
    static int framesElapsed = 0;
    public static final float[] camera = {0,1,0,0};
    public static final int TARGET_FPS = 120;
    public static final float[] DEFAULT_SKY_COLOR = {.5f,.5f,.5f,1};
    static WindowManager manager;
    static Context context = new Context();

    static float testBrightness = 0;


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

        manager.addUpdateEvent(() -> {
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_UP)==GLFW.GLFW_PRESS){System.out.println(testBrightness);testBrightness+=.003f;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_DOWN)==GLFW.GLFW_PRESS){System.out.println(testBrightness);testBrightness-=.003f;}

        });

        Model testCube = Model.loadModel(
                Utils.readFile(Utils.MODELS_DIR+"\\CubeWithNormals.obj").split("\n"),
                new SolidColorShaderProgram(1f,0f,.7f)
                //TextureManager.loadTexture(Utils.TEXTURES_DIR+"\\img.png")
        );
        Model newCube = new Model(testCube.getRelativeVertices(),testCube.getIndexes(),new DisplayBasedTextureShaderProgram(TextureManager.loadTexture(Utils.TEXTURES_DIR+"\\img.png"))){
            @Override
            public void doLogic(int itneration) {
                this.setCoords(new float[]{-.5f, (float) (Math.sin(((double)itneration)/60)/6 + .5),0, 0});
            }
        };

        context.addObject(newCube);

        int chessTexture = TextureManager.loadTexture(Utils.TEXTURES_DIR + "\\xadrez.png");
        TexturedModel chessBoardFloor = new TexturedModel(
                new float[]{-40,0,-40,0, 40,0,-40,0, 40,0,40,0, -40,0,40,0},
                new int[]{0,1,2,3,2,0},
                new float[]{0,0,80,0,80,80,0,80},
                chessTexture
        );
        context.addObject(chessBoardFloor);
        context.setBackGroundColor(new float[]{.45f,.45f,.65f,1f});

        context.setLightColor(0,0f,0f,1f,.4f);
        context.setLightColor(1,1f,0f,0f,.4f);
        context.setLightColor(2,0f,1f,1f,.4f);
        context.setLightColor(3,1f,1f,0f,.4f);
        context.setLightColor(4,0f,1f,1f,.4f);
        context.setLightColor(5,1f,0f,1f,.4f);
        context.setLightColor(6,1f,1f,1f,.4f);
        context.setLightColor(7,0f,0f,0f,.4f);


        newCube.getShader().setLights(new float[][]{{0,0,0,1f}});

        int loc = GL30.glGetUniformLocation(newCube.getShader().getID(),"lights[0]");
        System.out.println(loc);
        GL30.glUseProgram(newCube.getShader().getID());
        GL30.glUniform4fv(loc,new float[]{0,0,0,1f});

        context.setFogDetails(12,6);
        context.setBackGroundColor(new float[]{0,0,0,0});
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

    static float[][] contextLights = new float[8][];

    protected static void doLogic(){
        for (int i = 0; i < 8; i++) {
            float acr = (float) ((float) i * Math.PI)/4.0f;
            contextLights[i] = new float[]{(float) Math.sin((float)framesElapsed/20 + acr)*4,1f, (float) Math.cos((float)framesElapsed/20 + acr)*4,testBrightness};

        }
        context.setLights(contextLights);
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
