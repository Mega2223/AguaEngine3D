package net.mega2223.aguaengine3d;


import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.misc.Line;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ModelUtils;
import net.mega2223.aguaengine3d.graphics.objects.modeling.TexturedModel;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.Noise;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.PerlinNoise;
import net.mega2223.aguaengine3d.graphics.objects.shadering.SolidColorShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.graphics.utils.ShaderDictonary;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.graphics.utils.TextureManager;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.misc.Utils;
import net.mega2223.aguaengine3d.objects.WindowManager;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

@SuppressWarnings({"unused"})

/*
* The official AguaEngine3D TODO list:
* Remove shadow acnes somehow (I hate normals so much it's unreal)
* Font rendering <- unfinished
* Rewrite texture loading function
* Convert light objects to structs in shaders
* The floor is slightly transparent somehow <- FIXED
* Geometry Shader support (Possibly compute shaders aswell, may require an OpenGL upgrade)
* Move aero to another module? (also finish it lol) <- DONE
* Move shadow calculation algorithm to the default shader dictionary
* Cubemap support (for lights and skyboxes) <- Done
* Logo and Readme.md
* Figure out why the FPS loop is weird
* Improvements on procedural building generation (aka multi building and scaling support)
* Optimize OpenGL calls, ESPECIALLY the VBOs that store the model data
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
* Standardize array arguments
* Calculate the restitution variable lol <- Done?
* Also the physics module needs the friction force
* Parallel contact is weird currently
* Static function that creates objects with bound buffers
* Shader recompile function
* Render order priority variable/method?
* Rewrite the normal handling code
* Model editor (maybe a inbuilt tools tools package)
* */

public class Gaem3D {

    public static final int TARGET_FPS = 120;
    public static final float[] DEFAULT_SKY_COLOR = {.5f, .5f, .5f, 1};
    protected static final String TITLE = "3 DIMENSÇÕES";
    protected static final int D = 512;
    public static final float[] camera = {0, .9f, 0, 0};
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

        //shader dict setup

        ShaderManager.setIsGlobalShaderDictEnabled(true);
        ShaderDictonary globalDict = ShaderManager.getGlobalShaderDictionary();
        globalDict.add(ShaderDictonary.fromFile(Utils.SHADERS_DIR + "\\DefaultShaderDictionary.sdc"));
        context = new RenderingContext();

        //scenery setup

        context.setLight(0, 0, 10, 0, 1000)
                .setBackGroundColor(.5f, .5f, .6f)
                .setActive(true)
                .setFogDetails(100, 1000);

        TexturedModel chessFloor = new TexturedModel(
                new float[]{-50, 0, -50, 0, 50, 0, -50, 0, -50, 0, 50, 0, 50, 0, 50, 0},
                new int[]{0, 1, 2, 2, 1, 3},
                new float[]{0, 0, 100, 0, 0, 100, 100, 100},
                TextureManager.loadTexture(Utils.TEXTURES_DIR + "\\xadrez.png")
        );

        Model water = new Model(
                new float[]{200,0,200,0, 200,0,-200,0, -200,0,200,0, -200,0,-200,0},
                new int[]{0,1,2,1,2,3},
                new SolidColorShaderProgram(.2F,.2F,.8F,.7F)
        );
        water.setCoords(0,-.1F,0);


        PerlinNoise noise = new PerlinNoise(16,16);
        noise.setHeightScale(4);
        Model grass = Noise.NoiseToModel(noise,64,64,4F/32F,400F/64F,new SolidColorShaderProgram(.1F,.6F,.1F));
        context.addObject(grass);


        //context.addObject(chessFloor);
        //FIXME: seems like SolidColorShaderProgram throws an OpenGL error somehow
        Model cube = Model.loadModel(Utils.readFile(Utils.MODELS_DIR+"\\cube.obj"),new SolidColorShaderProgram(0,1,0));
        context.addObject(grass);

        float[] vertices = grass.getRelativeVertices();
        float[] normals = grass.getNormals();
        //VectorTranslator.debugVector(normals);
//        for (int i = 0; i < normals.length; i+=4) {
//            Line toAdd = new Line(1, 0, 0);
//            toAdd.setStart(vertices[i],vertices[i+1],vertices[i+2]);
//            toAdd.setEnd(vertices[i] + normals[i], vertices[i+1] + normals[i+1], vertices[i+2] + normals[i+2]);
//            context.addObject(toAdd);
//        }

        //Render Logic be like:
        long unrendered = 0;
        long lastLoop = System.currentTimeMillis();
        int framesLastSecond = 0;
        long fLSLastUpdate = 0;
        //long lastCycleDuration = 0; STOP SCREAMING AT ME INTELLIJ I GET IT
        context.setActive(true);

        while (!GLFW.glfwWindowShouldClose(manager.windowName)) {
            unrendered += System.currentTimeMillis() - lastLoop;
            lastLoop = System.currentTimeMillis();
            if (System.currentTimeMillis() - fLSLastUpdate > 1000) {
                fLSLastUpdate = System.currentTimeMillis();
                GLFW.glfwSetWindowTitle(manager.windowName, TITLE + "    FPS: " + (framesLastSecond) + "(x: " + camera[0] + " z:" + camera[2] + ")");
                framesLastSecond = 0;

            }
            if (unrendered > (1000 / TARGET_FPS)) {
                long cycleStart = System.currentTimeMillis();
                //cpu logic
                doLogic();
                //render logic
                doRenderLogic();
                unrendered = 0;
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
        MatrixTranslator.applyLookTransformation(proj, camera, (float) (camera[0] + Math.sin(camera[3])), camera[1], (float) (camera[2] + Math.cos(camera[3])), 0, 1, 0);
        context.doLogic();
        manager.fitViewport();
        context.doRender(proj);
        RenderingManager.printErrorQueue();
        manager.update();
    }

}
