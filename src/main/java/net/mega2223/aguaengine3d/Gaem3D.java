package net.mega2223.aguaengine3d;


import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.ScriptedSequence;
import net.mega2223.aguaengine3d.graphics.objects.modeling.InterfaceComponent;
import net.mega2223.aguaengine3d.graphics.objects.modeling.TexturedModel;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ui.BitmapFont;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ui.TextManipulator;
import net.mega2223.aguaengine3d.graphics.objects.shadering.DisplayComponentShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.shadering.TextureShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.graphics.utils.ShaderDictonary;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.graphics.utils.TextureManager;
import net.mega2223.aguaengine3d.logic.ModelPhysicsAggregate;
import net.mega2223.aguaengine3d.logic.RigidBodyAggregate;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.misc.Utils;
import net.mega2223.aguaengine3d.logic.PhysicsRenderContext;
import net.mega2223.aguaengine3d.objects.WindowManager;
import net.mega2223.aguaengine3d.physics.CollisionResolver;
import net.mega2223.aguaengine3d.physics.PhysicsUtils;
import net.mega2223.aguaengine3d.physics.objects.*;
import net.mega2223.aguaengine3d.physics.utils.objects.ConstantForce;
import net.mega2223.aguaengine3d.physics.utils.objects.DragForce;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;

@SuppressWarnings({"unused"})

/*
* The official AguaEngine3D TODO list:
* Remove shadow acnes somehow (I hade normals so much it's unreal)
* Font rendering (unfinished)
* Rewrite texture loading function
* Convert light objects to structs in shaders
* The floor is slightly transparent somehow
* Geometry Shader support (Possibly compute shaders aswell, may require an OpenGL upgrade)
* Move aero to another module? (also finish it lol) <- DONE
* Move shadow calculation algorithm to the default shader dictionary
* Cubemap support (for lights and skyboxes)
* Logo and Readme.md
* Figure out why the FPS loop is weird
* Improvements on procedural building generation (aka multi building and scaling support)
* Optimize OpenGL calls
* Model blueprint class
* Coverage testing
* Sound stuff
* Physics stuff
* Trigger stuff
* Collision stuff
* Animation stuff
* Perhaps a static OpenGL manager class?
* */

public class Gaem3D {

    public static final int TARGET_FPS = 120;
    public static final float[] DEFAULT_SKY_COLOR = {.5f, .5f, .5f, 1};
    protected static final String TITLE = "3 DIMENSÇÕES";
    protected static final int D = 512;
    private static final float[] camera = {0, .9f, 0, 0};
    public static int framesElapsed = 0;
    static WindowManager manager;
    static PhysicsRenderContext context = new PhysicsRenderContext();

    static float[] trans = new float[16];
    static float[] proj = new float[16];

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
        });

        //shader dict setup

        ShaderManager.setIsGlobalShaderDictEnabled(true);
        ShaderDictonary globalDict = ShaderManager.getGlobalShaderDictionary();
        globalDict.add(ShaderDictonary.fromFile(Utils.SHADERS_DIR + "\\DefaultShaderDictionary.sdc"));

        //tests

        TexturedModel chessFloor = new TexturedModel(
                new float[]{-50, 0, -50, 0, 50, 0, -50, 0, -50, 0, 50, 0, 50, 0, 50, 0},
                new int[]{0, 1, 2, 2, 1, 3},
                new float[]{0, 0, 100, 0, 0, 100, 100, 100},
                TextureManager.loadTexture(Utils.TEXTURES_DIR + "\\xadrez.png")
        );

        TexturedModel cubeModel = TexturedModel.loadTexturedModel(
                Utils.readFile(Utils.MODELS_DIR + "\\cube.obj").split("\n"), new TextureShaderProgram(),
                TextureManager.loadTexture(Utils.TEXTURES_DIR + "\\img.png")
        );
        TexturedModel cubeModel2 = TexturedModel.loadTexturedModel(
                Utils.readFile(Utils.MODELS_DIR + "\\cube.obj").split("\n"), new TextureShaderProgram(),
                TextureManager.loadTexture(Utils.TEXTURES_DIR + "\\img.png")
        );
        TexturedModel cubeModel3 = TexturedModel.loadTexturedModel(
                Utils.readFile(Utils.MODELS_DIR + "\\cube.obj").split("\n"), new TextureShaderProgram(),
                TextureManager.loadTexture(Utils.TEXTURES_DIR + "\\img.png")
        );

        TexturedModel referenceCube = TexturedModel.loadTexturedModel(
                Utils.readFile(Utils.MODELS_DIR + "\\cube.obj").split("\n"), new TextureShaderProgram(),
                TextureManager.loadTexture(Utils.TEXTURES_DIR + "\\img.png")
        );
        referenceCube.setCoords(0,.5F,3);
        context.addObject(referenceCube);
        //physics

        PhysicsSystem cubePhysics = new ParticleSystem(Float.POSITIVE_INFINITY);
        PhysicsSystem cube2Physics = new ParticleSystem(10);
        cube2Physics.setCoordY(5);

        final ModelPhysicsAggregate cube = new ModelPhysicsAggregate(cubeModel,cubePhysics){
            @Override
            public void doLogic() {
                super.doLogic();
                PhysicsSystem phys = this.physicsHandler;
                if(phys.getCoordY() < 1){
                    phys.setCoordY(1);
                }
            }
        };
        final ModelPhysicsAggregate cube2 = new ModelPhysicsAggregate(cubeModel2,cube2Physics){
            @Override
            public void doLogic() {
                super.doLogic();
                float distance = VectorTranslator.getDistance(cube.physicsHandler().getCoords(), this.physicsHandler().getCoords());
                PhysicsSystem phys = this.physicsHandler;
                if(distance <= 2){
                    CollisionResolver.resolveConflict(cube.physicsHandler(), phys, - distance + 2F);
                    CollisionResolver.resolveCollision(cube.physicsHandler(),this.physicsHandler());
                }
                if(phys.getCoordY() < 1){
                    CollisionResolver.resolveConflict(phys,phys.getCoordX(),-1,phys.getCoordZ(),-phys.getCoordY()+1);
                    CollisionResolver.resolveCollision(phys,phys.getCoordX(),-1,phys.getCoordZ(),.5F);
                }
                /*if(cube.physicsHandler().getCoordY() < 1){
                    CollisionResolver.resolveConflict(cube.physicsHandler(),cube.physicsHandler().getCoordX(),-1,cube.physicsHandler().getCoordZ(),0);
                    CollisionResolver.resolveCollision(cube.physicsHandler(),cube.physicsHandler().getCoordX(),-1,cube.physicsHandler().getCoordZ(),.5F);
                }*/
            }
        };

        DragForce drag = new DragForce(.1F, .04F);
        ConstantForce gravity = new ConstantForce(0, -.01F, 0);

        cube.physicsHandler().addForce(drag);
        cube.physicsHandler().setCoordY(2);
        //cube.physicsHandler().addForce(new SpringForce(10F,.01F,0,5,0));
        cube2.physicsHandler().addForce(drag);
        //cube2.physicsHandler().addForce(new SpringForce(3F,.01F,0,5,0));

        cube.physicsHandler().addForce(gravity);
        cube2.physicsHandler().addForce(gravity);

        manager.addUpdateEvent(()->{
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_UP)==GLFW.GLFW_PRESS){
                cube.physicsHandler().applyForce(.05F,0,0);

            }
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_DOWN)==GLFW.GLFW_PRESS){
                cube.physicsHandler().applyForce(-.05F,0,0);
            }
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_LEFT)==GLFW.GLFW_PRESS){
                cube.physicsHandler().applyForce(0,0,-.05F);

            }
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_RIGHT)==GLFW.GLFW_PRESS){
                cube.physicsHandler().applyForce(0,0,.05F);
            }

        });

        context.addObject(cube).addObject(cube2);

        //physics

        float[] tensor = new float[9];
        PhysicsUtils.generateInertiaTensor(10,10,10,tensor);
        RigidBodySystem test = new RigidBodySystem(1,tensor);

        RigidBodyAggregate cube23 = new RigidBodyAggregate(cubeModel3, test);
        cube23.physicsHandler().setCoordY(10);
        test.applyForce(0,0.1F,0,0,0,0.1F);
        context.renderContext().addScript(new ScriptedSequence("THE PRINTER") {
            @Override
            protected void preLogic(int itneration, RenderingContext context) {
                VectorTranslator.debugVector(test.getCoords());
                test.setCoords(0,5,0);
            }
        });
        context.addObject(cube23);

        //est.applyForce(0,-.001F,0,0,-.01F,0);

        //interface

        String bitmapFile = Utils.FONTS_DIR + "\\consolas\\Consolas.png";


        InterfaceComponent comp = new InterfaceComponent(
                new float[]{.5F, .5F, 0, 0, .5F, 0, 0, 0, 0, .5F, 0, 0, 0, 0, 0, 0},
                new int[]{0, 1, 2, 3, 2, 1},
                new float[]{0, 0, 0, 1, 1, 0, 1, 1},
                3,
                1
        ) {
            @Override
            public void doLogic(int itneration) {
                setAspectRatio(manager.getAspectRatio());
            }
        };


        BitmapFont font = TextManipulator.decompileCSV(
                Utils.FONTS_DIR+"\\consolas\\Consolas.csv",
                bitmapFile
        );

        assert font != null;
        InterfaceComponent og = font.genFromString("Teste :)");
        InterfaceComponent text = new InterfaceComponent(og,
                new DisplayComponentShaderProgram(new DisplayComponentShaderProgram(og.getCastShader())){
                    @Override
                    public void setUniforms(int interation, float[] translationMatrix, float[] projectionMatrix) {
                        MatrixTranslator.generateStaticInterfaceProjectionMatrix(translationMatrix,getAspectRatio(),0,0,0,1,.5F,1);
                        super.setUniforms(interation, translationMatrix, projectionMatrix);
                    }
                }
        ){
            @Override
            public void doLogic(int itneration) {
                getCastShader().setAspectRatio(manager.getAspectRatio());
            }
        };


        context/*.addObject(cube).addObject(cube2)*/.addObject(chessFloor)/*.addObject(comp).*/.addObject(text);

        context.renderContext().setLight(0, 0, 10, 0, 10)
                .setBackGroundColor(.5f, .5f, .6f)
                .setActive(true)
                .setFogDetails(10, 20);

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
        MatrixTranslator.generatePerspectiveProjectionMatrix(proj, 0.01f, 100.0f, (float) Math.toRadians(45), manager.viewportSize[0], manager.viewportSize[1]);
        MatrixTranslator.applyLookTransformation(proj, camera, (float) (camera[0] + Math.sin(camera[3])), camera[1], (float) (camera[2] + Math.cos(camera[3])), 0, 1, 0);
        context.doLogic();
        manager.fitViewport();
        context.doRender(proj);
        RenderingManager.printErrorQueue();
        manager.update();
    }

}
