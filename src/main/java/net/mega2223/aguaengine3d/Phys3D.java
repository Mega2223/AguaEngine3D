package net.mega2223.aguaengine3d;


import net.mega2223.aguaengine3d.graphics.objects.modeling.TexturedModel;
import net.mega2223.aguaengine3d.graphics.objects.shadering.TextureShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.graphics.utils.ShaderDictonary;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.graphics.utils.TextureManager;
import net.mega2223.aguaengine3d.logic.PhysicsRenderContext;
import net.mega2223.aguaengine3d.logic.RigidBodyAggregate;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.misc.Utils;
import net.mega2223.aguaengine3d.objects.WindowManager;
import net.mega2223.aguaengine3d.physics.PhysicsUtils;
import net.mega2223.aguaengine3d.physics.objects.RigidBodySystem;
import net.mega2223.aguaengine3d.physics.utils.objects.forces.ConstantForce;
import net.mega2223.aguaengine3d.physics.utils.objects.forces.DragForce;
import net.mega2223.aguaengine3d.physics.utils.objects.hitboxes.AxisParallelPlaneHitbox;
import net.mega2223.aguaengine3d.physics.utils.objects.hitboxes.RectHitbox;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings({"unused"})
public class Phys3D {

    public static final int TARGET_FPS = 120;
    public static final float[] DEFAULT_SKY_COLOR = {.5f, .5f, .5f, 1};
    protected static final String TITLE = "3 DIMENSÇÕES";
    protected static final int D = 512;
    public static final float[] camera = {0, .9f, 0, 0};
    public static int framesElapsed = 0;
    public static float timeToSimulate = .00001F;
    static WindowManager manager;
    static PhysicsRenderContext context = new PhysicsRenderContext();

    static float[] trans = new float[16];
    static float[] proj = new float[16];

    static RigidBodyAggregate test;

    public static void main(String[] args) {

        //GLFW
        manager = new WindowManager(300, 300, TITLE);
        manager.init();
        manager.addUpdateEvent(() -> { //walk events
            double s = Math.sin(camera[3]);
            double c = Math.cos(camera[3]);
            float speed = .15F;
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_W)==GLFW.GLFW_PRESS){camera[2] += speed*c;camera[0] += speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_S)==GLFW.GLFW_PRESS){camera[2] -= speed*c;camera[0] -= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_A)==GLFW.GLFW_PRESS){camera[0] += speed*c;camera[2]-= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_D)==GLFW.GLFW_PRESS){camera[0] -= speed*c;camera[2]+= speed*s;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_Q)==GLFW.GLFW_PRESS){camera[3] += Math.PI/90;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_E)==GLFW.GLFW_PRESS){camera[3] -= Math.PI/90;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_Z)==GLFW.GLFW_PRESS){camera[1] += speed;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_X)==GLFW.GLFW_PRESS){camera[1] -= speed;}
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_PAGE_UP)==GLFW.GLFW_PRESS){
                timeToSimulate = Math.max(0,Math.min(timeToSimulate + 0.01F,2));
            }
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_PAGE_DOWN)==GLFW.GLFW_PRESS){
                timeToSimulate = Math.max(0,Math.min(timeToSimulate - 0.01F,2));
            }
        });

        //shader dict setup

        ShaderManager.setIsGlobalShaderDictEnabled(true);
        ShaderDictonary globalDict = ShaderManager.getGlobalShaderDictionary();
        globalDict.add(ShaderDictonary.fromFile(Utils.SHADERS_DIR + "\\DefaultShaderDictionary.sdc"));

        //scenery setup

        TexturedModel chessFloor = new TexturedModel(
                new float[]{-50, 0, -50, 0, 50, 0, -50, 0, -50, 0, 50, 0, 50, 0, 50, 0},
                new int[]{0, 1, 2, 2, 1, 3},
                new float[]{0, 0, 100, 0, 0, 100, 100, 100},
                TextureManager.loadTexture(Utils.TEXTURES_DIR + "\\xadrez.png")
        );
        //tests

        TexturedModel cubeModel = TexturedModel.loadTexturedModel(
                Utils.readFile(Utils.MODELS_DIR + "\\cube.obj").split("\n"), new TextureShaderProgram(),
                TextureManager.loadTexture(Utils.TEXTURES_DIR + "\\DEBUG.png")
        );
        TexturedModel cubeModel2 = TexturedModel.loadTexturedModel(
                Utils.readFile(Utils.MODELS_DIR + "\\cube.obj").split("\n"), new TextureShaderProgram(),
                TextureManager.loadTexture(Utils.TEXTURES_DIR + "\\grass.png")
        );
        TexturedModel cubeModel3 = TexturedModel.loadTexturedModel(
                Utils.readFile(Utils.MODELS_DIR + "\\cube.obj").split("\n"), new TextureShaderProgram(),
                TextureManager.loadTexture(Utils.TEXTURES_DIR + "\\tijolo.png")
        );
        TexturedModel referenceCube = TexturedModel.loadTexturedModel(
                Utils.readFile(Utils.MODELS_DIR + "\\cube.obj").split("\n"), new TextureShaderProgram(),
                TextureManager.loadTexture(Utils.TEXTURES_DIR + "\\DEBUG.png")
        );
        referenceCube.setCoords(0,4f,3);
        //context.addObject(referenceCube);

        //physics

        ConstantForce gravity = new ConstantForce(0, -.01F, 0);

        float[] innertiaTensor = new float[9];
        PhysicsUtils.generateTensorForRect(1,1,1,1,innertiaTensor);
        RigidBodySystem cube1Physics = new RigidBodySystem(1,innertiaTensor);
        cube1Physics.setCoords(0,4,0);
        cube1Physics.setOrientation(1,0,.5F,.5F);
        RigidBodySystem cube2Physics = new RigidBodySystem(1,innertiaTensor);

        RigidBodyAggregate cube1 = new RigidBodyAggregate(cubeModel,cube1Physics);
        test = cube1;
        RigidBodyAggregate cube2 = new RigidBodyAggregate(cubeModel2,cube2Physics);

        context.physContext().addForce(new DragForce(0.4F,1.1F));
        context.addObject(referenceCube);
        //context.physContext().addForce(new SpringForce(2,.1F,0,5,0));

        cube1.physicsHandler().addForce(gravity);
        float[] test = new float[4];
        PhysicsUtils.radiansToQuaternion(0,1,0,test);
        cube1Physics.setOrientation(test[0],test[1],test[2],test[3]);

        cube2.physicsHandler().addForce(gravity);
        cube2.physicsHandler().setCoordX(5);

        RectHitbox cube1Hitbox =  new RectHitbox(cube1.physicsHandler(),-1,-1,-1,1,1,1);
        new RectHitbox(cube2.physicsHandler(),-1,-1,-1,1,1,1);

        context.physContext().getCollisionEnviroment().addHitbox(new AxisParallelPlaneHitbox(0));

        manager.addUpdateEvent(()->{
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_UP)==GLFW.GLFW_PRESS){
                cube1.physicsHandler().applyAcceleration(.05F,0,0);
            }
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_DOWN)==GLFW.GLFW_PRESS){
                cube1.physicsHandler().applyAcceleration(-.05F,0,0);
            }
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_LEFT)==GLFW.GLFW_PRESS){
                cube1.physicsHandler().applyAcceleration(0,0,-.05F);
            }
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_RIGHT)==GLFW.GLFW_PRESS){
                cube1.physicsHandler().applyAcceleration(0,0,.05F);
            }
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_ENTER)==GLFW.GLFW_PRESS){
                cube1Physics.applyTorque(0,0,0.1F);
            }
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_K)==GLFW.GLFW_PRESS){
                //cube1Physics.applyAcceleration(0,.1F,0,0,0,.1F,true);
                cube1Physics.applyForce(2,1,2,0,0,0.3F);
            }

        });
        context.addObject(cube1);//.addObject(cube2);

        //physics

        context.addObject(chessFloor);

        context.renderContext().setLight(0, 0, 10, 0, 10)
                .setBackGroundColor(.5f, .5f, .6f)
                .setActive(true)
                .setFogDetails(30, 20);

        RenderingManager.printErrorQueue();

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
                GLFW.glfwSetWindowTitle(manager.windowName, TITLE + "    FPS: " + (framesLastSecond) + "(x: " + camera[0] + " z:" + camera[2] + ") Sim time: " + timeToSimulate);
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
        MatrixTranslator.generatePerspectiveProjectionMatrix(proj, 0.01f, 100.0f, (float) Math.toRadians(45), manager.viewportSize[0], manager.viewportSize[1]);
        MatrixTranslator.applyLookTransformation(proj, camera, (float) (camera[0] + Math.sin(camera[3])), camera[1], (float) (camera[2] + Math.cos(camera[3])), 0, 1, 0);

        context.doLogic(timeToSimulate);
        manager.fitViewport();
        context.doRender(proj);
        RenderingManager.printErrorQueue();
        manager.update();
    }

}
