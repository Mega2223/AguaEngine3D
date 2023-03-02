package net.mega2223.lwjgltest.aguaengine3d;


import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.TexturedModel;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.procedural.StructureUtils;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.DisplayBasedTextureShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.SolidColorShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.TextureShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.TextureManager;
import net.mega2223.lwjgltest.aguaengine3d.logic.Context;
import net.mega2223.lwjgltest.aguaengine3d.logic.objects.StaticRenderable;
import net.mega2223.lwjgltest.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;
import net.mega2223.lwjgltest.aguaengine3d.objects.WindowManager;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.Model;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("unused")

public class Gaem3D {

    protected static final String TITLE = "3 DIMENSÇÕES";
    static int framesElapsed = 0;
    public static final float[] camera = {0,.65f,10,0};
    public static final int TARGET_FPS = 120;
    static WindowManager manager;
    static Context context = new Context();

    public static void main(String[] args) {

        //GLFW
        manager = new WindowManager(300,300, TITLE);
        manager.init();
        manager.clearColor(.5f, .5f, .6f, 1f);

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

        //test area
        Model test = TexturedModel.loadTexturedModel(
                Utils.readFile(Utils.MODELS_DIR+"\\SimpleFloor.obj").split("\n"),
                new TextureShaderProgram(),
                TextureManager.loadTexture(Utils.TEXTURES_DIR+"\\xadrez.png")
        );
        Model pillar = Model.loadModel(
                Utils.readFile(Utils.MODELS_DIR+"\\Pilar.obj").split("\n"),
                new SolidColorShaderProgram(.65f,.65f,.65f)
        );
        TexturedModel gat = TexturedModel.loadTexturedModel(
                Utils.readFile(Utils.MODELS_DIR+"\\cube.obj").split("\n"),
                new DisplayBasedTextureShaderProgram(),
                TextureManager.loadTexture(Utils.TEXTURES_DIR+"\\img.png")
        );
        //scales down the cube once
        float[] ver = gat.getRelativeVertices();
        for (int i = 0; i < ver.length; i++) {
            ver[i]/=4;
        }
        gat.setVertices(ver);

        int wallTexture = TextureManager.loadTexture(Utils.TEXTURES_DIR+"\\TijoloSuave.png");

        Model[] walls = {
                StructureUtils.generateWall(5,5,5,-5,3,0,wallTexture),
                StructureUtils.generateWall(5,-5,-5,-5,3,0,wallTexture),
                StructureUtils.generateWall(-5,-5,-5,5,3,0,wallTexture),
                StructureUtils.generateWall(-5,5,5,5,3,0,wallTexture)
        };

        context.addObject(new StaticRenderable(test));
        context.addObject(new StaticRenderable(pillar));
        context.addObject(new StaticRenderable(gat){
            @Override
            public void doLogic(int itneration) {
                this.setPosition(new float[]{0, ((float) Math.cos((float)itneration/30)+10)/10,0,0});
                Model ac = this.getModels().get(0);
                float[] relativeVertices = ac.getRelativeVertices();
                for (int i = 0; i < relativeVertices.length; i+=4) {
                    MatrixTranslator.rotateVector3(relativeVertices,.05,0.00001,.01,i);
                }
                ac.setVertices(relativeVertices);
            }
        });
        context.addObject(new StaticRenderable(walls));

        Model AMONGUS = Model.loadModel(Utils.readFile(Utils.MODELS_DIR + "\\IMPOSTER.obj").split("\n"), new SolidColorShaderProgram(1, 0, 0));
        float[] relativeVertices = AMONGUS.getRelativeVertices();
        for (int i = 0; i < relativeVertices.length; i+=4) {
            float[] newPos = {relativeVertices[i],relativeVertices[i+1],relativeVertices[i+2],0};
            MatrixTranslator.rotateVector3(newPos,0,Math.PI/2,0);
            for (int j = 0; j < 4; j++) {
                relativeVertices[i+j]=newPos[j];
            }
            relativeVertices[i]+=20;
            relativeVertices[i+2]+=20;

        }
        AMONGUS.setVertices(relativeVertices);

        context.addObject(new StaticRenderable(AMONGUS));

        //Render Logic be like:
        long unrendered = 0;
        final long applicationStart = System.currentTimeMillis();
        long lastLoop = applicationStart;
        int framesLastSecond = 0;
        long fLSLastUpdate = 0;
        long lastCycleDuration = 0;

        while (!GLFW.glfwWindowShouldClose(manager.windowName)) {
            unrendered += System.currentTimeMillis() - lastLoop;
            lastLoop = System.currentTimeMillis();
            if(System.currentTimeMillis() - fLSLastUpdate > 1000){
                fLSLastUpdate = System.currentTimeMillis();
                GLFW.glfwSetWindowTitle(manager.windowName,TITLE + "    FPS: " + (framesLastSecond));
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
