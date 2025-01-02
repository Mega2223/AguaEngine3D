package net.mega2223.aguaengine3d;


import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Skybox;
import net.mega2223.aguaengine3d.graphics.objects.modeling.TexturedModel;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.Noise;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.PerlinNoise;
import net.mega2223.aguaengine3d.graphics.objects.shadering.CubemapInterpreterShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.shadering.SolidColorShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.graphics.utils.ShaderDictionary;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.graphics.utils.TextureManager;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.misc.Utils;
import net.mega2223.aguaengine3d.objects.WindowManager;
import org.lwjgl.glfw.GLFW;

import java.awt.image.BufferedImage;
import java.io.IOException;

@SuppressWarnings({"unused"})

/*
* The official AguaEngine3D TODO list:
* Remove shadow acnes somehow (I hate normals so much it's unreal) <- At least i did make the normal calculator lol
* Font rendering <- unfinished <- almost finished
* Rewrite texture loading function
* Convert light objects to structs in shaders
* The floor is slightly transparent somehow <- FIXED
* Geometry Shader support (Possibly compute shaders aswell, may require an OpenGL upgrade) <- Done
* Move aero to another module? (also finish it lol) <- DONE
* Move shadow calculation algorithm to the default shader dictionary <- goes along with standardizing uniforms i guess
* Reform the shader dictionary lol
* Cubemap support (for lights and skyboxes) <- Done
* Logo (kindadone) and Readme.md (done)
* Figure out why the FPS loop is weird
* Improvements on procedural building generation (aka multi building and scaling support) (Done, kinda)
* Optimize OpenGL calls, ESPECIALLY the VBOs that store the model data
* We NEED VAOs
* Model blueprint class <- Done
* Coverage testing <- what
* Sound stuff
* Physics stuff <- WIP lol
* Trigger stuff
* Collision stuff <- WIP
* Animation stuff
* Perhaps a static OpenGL manager class?
* Denote static buffers explicitly as static? <- DONE afaik
* Interaction radius detection interface <- Done
* Object declaration instantiation generation annotation?
* Standardize array arguments (especially for the phyisics module)
* Calculate the restitution variable lol <- Done?
* Also the physics module needs the friction force
* Parallel contact is weird currently
* Static functions that creates objects with bound buffers
* Shader recompile function
* Render order priority variable/method? (Done)
* Rewrite the normal handling code (Done)
* Model editor (maybe a inbuilt tools tools package)
* Interpolation interface and objects (Done)
* FPS manager for windowmanagers
* TAG para operações que criam objetos
* Filter functions? (functions that filter :p) <- what was bro yapping about
* Dinamically alocated text object
* Texturable interface?
* Modular uniform sync
* Subdividing triangles of a model for debugging purposes
* */

//FIXME: seems like SolidColorShaderProgram throws an OpenGL error somehow

public class Gaem3D {

    public static final int TARGET_FPS = 120;
    public static final float[] DEFAULT_SKY_COLOR = {.5f, .5f, .5f, 1};
    protected static final String TITLE = "3 DIMENSÇÕES";
    protected static final int D = 512;
    public static final float[] camera = {0, 4, 0, 0};
    public static int framesElapsed = 0;
    static WindowManager manager;
    static RenderingContext context;

    static float[] trans = new float[16];
    static float[] proj = new float[16];

    public static void main(String[] args) throws IOException {

        //GLFW
        manager = new WindowManager(300, 300, TITLE);
        manager.init();
        manager.addUpdateEvent(() -> { //walk events
            double s = Math.sin(camera[3]);
            double c = Math.cos(camera[3]);
            float speed = .075F;
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_W)==GLFW.GLFW_PRESS){camera[2] += speed*c;camera[0] += speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_S)==GLFW.GLFW_PRESS){camera[2] -= speed*c;camera[0] -= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_A)==GLFW.GLFW_PRESS){camera[0] += speed*c;camera[2]-= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_D)==GLFW.GLFW_PRESS){camera[0] -= speed*c;camera[2]+= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_Q)==GLFW.GLFW_PRESS){camera[3] += Math.PI/90;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_E)==GLFW.GLFW_PRESS){camera[3] -= Math.PI/90;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_Z)==GLFW.GLFW_PRESS){camera[1] += speed;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_X)==GLFW.GLFW_PRESS){camera[1] -= speed;}
        });

        ShaderManager.setIsGlobalShaderDictEnabled(true);
        ShaderDictionary globalDict = ShaderManager.getGlobalShaderDictionary();
        globalDict.addAllValues(ShaderDictionary.fromFile(Utils.SHADERS_DIR + "/DefaultShaderDictionary.sdc"));
        context = new RenderingContext();

        //scenery setup

        context.setLight(0, 0, 10, 0, 1000)
                .setBackGroundColor(.5f, .5f, .6f)
                .setActive(true)
                .setFogDetails(7, 20);

        TexturedModel chessFloor = new TexturedModel(
                new float[]{-50, 0, -50, 0, 50, 0, -50, 0, -50, 0, 50, 0, 50, 0, 50, 0},
                new int[]{0, 1, 2, 2, 1, 3},
                new float[]{0, 0, 100, 0, 0, 100, 100, 100},
                TextureManager.loadTexture(Utils.TEXTURES_DIR + "/xadrez.png")
        );
        context.addObject(chessFloor);

        Model cube = Model.loadModel(Utils.readFile(Utils.MODELS_DIR+"/cube.obj"),new SolidColorShaderProgram(0,1,0));
        BufferedImage cat = Utils.readImage(Utils.TEXTURES_DIR + "/img.png");
        Skybox sk = new Skybox(TextureManager.generateCubemapTexture(
                new BufferedImage[]{cat,cat,cat,cat,cat,cat}
        ));
        //context.addObject(sk);
        context.addScript(((CubemapInterpreterShaderProgram)sk.getShader()).genRotationUpdateRunnable(camera));

        //Render Logic be like:
        long notRendered = 0;
        long lastLoop = System.currentTimeMillis();
        int framesLastSecond = 0;
        long fLSLastUpdate = 0;
        context.setActive(true);

        while (!GLFW.glfwWindowShouldClose(manager.windowName)) {
            notRendered += System.currentTimeMillis() - lastLoop;
            lastLoop = System.currentTimeMillis();
            if (System.currentTimeMillis() - fLSLastUpdate > 1000) {
                fLSLastUpdate = System.currentTimeMillis();
                GLFW.glfwSetWindowTitle(manager.windowName, TITLE + "    FPS: " + (framesLastSecond) + "(x: " + camera[0] + " z:" + camera[2] + ")");
                framesLastSecond = 0;

            }
            if (notRendered > (1000 / TARGET_FPS)) {
                long cycleStart = System.currentTimeMillis();
                //cpu logic
                doLogic();
                //render logic
                doRenderLogic();
                notRendered = 0;
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
        MatrixTranslator.applyLookTransformation(camera, (float) (camera[0] + Math.sin(camera[3])), camera[1], (float) (camera[2] + Math.cos(camera[3])), 0, 1, 0, proj);
        context.doLogic();
        manager.fitViewport();
        context.doRender(proj);
        RenderingManager.printErrorQueue();
        manager.update();
    }

}
