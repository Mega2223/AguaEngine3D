package net.mega2223.lwjgltest.aguaengine3d;


import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.TexturedModel;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.procedural.StructureUtils;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.TextureShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.TextureManager;
import net.mega2223.lwjgltest.aguaengine3d.logic.Context;
import net.mega2223.lwjgltest.aguaengine3d.logic.objects.StaticRenderable;
import net.mega2223.lwjgltest.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.lwjgltest.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;
import net.mega2223.lwjgltest.aguaengine3d.objects.WindowManager;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.CallbackI;

import java.util.Vector;

@SuppressWarnings("unused")

public class Gaem3D {

    protected static final String TITLE = "3 DIMENSÇÕES";
    static int framesElapsed = 0;
    public static final float[] camera = {0,.65f,0,0};
    public static final int TARGET_FPS = 120;
    public static final float[] skyColor = {1f,0,0,1};
    static WindowManager manager;
    static Context context = new Context();

    public static void main(String[] args) {

        //GLFW
        manager = new WindowManager(300,300, TITLE);
        manager.init();
        manager.clearColor(skyColor[0],skyColor[1], skyColor[2],skyColor[3]);

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
        TexturedModel model = TexturedModel.loadTexturedModel(
                Utils.readFile(Utils.MODELS_DIR + "\\ABPattern.obj").split("\n"),
                new TextureShaderProgram(),
                TextureManager.loadTexture(Utils.TEXTURES_DIR + "\\xadrez.png")
        );
        StaticRenderable mod = new StaticRenderable(model);
        float[] ver = model.getRelativeVertices();
        float[] texVer = model.getTextureShift();
        int[] indices = model.getIndexes();

        for (int i = 0; i < texVer.length; i++) {
            texVer[i]-=.5f;
            texVer[i]*=1.5;
        }
        VectorTranslator.scaleAllVectors(ver,1.5f);

        model.setVertices(ver);
        model.setTextureShift(texVer);
        int tex = TextureManager.loadTexture(Utils.TEXTURES_DIR+"\\TijoloSuave.png");
        for (int i = 0; i < indices.length; i+=3) {
            float[][] tAct = new float[3][];
            for (int j = 0; j < 3; j++) {
                int act = indices[i+j]*4;
                float[] vAct = {ver[act],ver[act+1],ver[act+2],ver[act+3]};
                tAct[j] = vAct;
            }

            mod.addModel(StructureUtils.generateWall(tAct[0][0],tAct[0][2],tAct[1][0],tAct[1][2],0,-10,tex));
            mod.addModel(StructureUtils.generateWall(tAct[1][0],tAct[1][2],tAct[2][0],tAct[2][2],0,-10,tex));
            mod.addModel(StructureUtils.generateWall(tAct[2][0],tAct[2][2],tAct[0][0],tAct[0][2],0,-10,tex));

        }
        StaticRenderable renderable = new StaticRenderable(
                new TexturedModel(
                        new float[]{-3,-3,-15,0 , -3,3,-15,0 , 3,3,-15,0 , 3,-3,-15,0},
                        new int[]{0,1,2,3,2,0},
                        new float[]{0,1,0,0,1,0,1,1},
                        TextureManager.loadTexture(Utils.TEXTURES_DIR+"\\TROLLF.png")
                )
        );

        context.setActive(true).addObject(mod).addObject(renderable).setBackGroundColor(skyColor);



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
