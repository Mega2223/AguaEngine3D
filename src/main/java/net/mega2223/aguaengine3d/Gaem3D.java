package net.mega2223.aguaengine3d;


import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.misc.Positionable;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Mesh;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Skybox;
import net.mega2223.aguaengine3d.graphics.objects.modeling.TexturedModel;
import net.mega2223.aguaengine3d.graphics.objects.modeling.utils.VertexTracker;
import net.mega2223.aguaengine3d.graphics.objects.shadering.CubemapInterpreterShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.shadering.SolidColorShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.graphics.utils.ShaderDictionary;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.graphics.utils.TextureManager;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.misc.Utils;
import net.mega2223.aguaengine3d.objects.WindowManager;
import net.mega2223.aguaengine3d.physics.PhysicsContext;
import net.mega2223.aguaengine3d.physics.PhysicsObject;
import net.mega2223.aguaengine3d.physics.advanced.RigidBody;
import net.mega2223.aguaengine3d.physics.debug.AngularVelocityVisualizer;
import net.mega2223.aguaengine3d.physics.decorators.PhysicsObjectDecorator;
import net.mega2223.aguaengine3d.physics.forces.Drag;
import net.mega2223.aguaengine3d.physics.forces.Gravity;
import net.mega2223.aguaengine3d.physics.objects.Particle;
import net.mega2223.aguaengine3d.physics.objects.collideable.Cube;
import net.mega2223.aguaengine3d.physics.objects.collideable.FixedPlane;
import net.mega2223.aguaengine3d.physics.objects.collideable.Sphere;
import net.mega2223.aguaengine3d.physics.objects.debug.CollisionVisualization;
import org.lwjgl.glfw.GLFW;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

@SuppressWarnings({"unused"})

/*
 * The official AguaEngine3D TODO list:
 * Remove shadow acnes somehow (I hate normals so much it's unreal) <- At least i did make the normal calculator lol
 * Font rendering <- unfinished <- almost finished
 * Rewrite texture loading function <- maybe not???
 * Convert light objects to structs in shaders
 * The floor is slightly transparent somehow <- FIXED
 * Geometry Shader support (Possibly compute shaders aswell, may require an OpenGL upgrade) <- Done
 * Move aero to another module? (also finish it lol) <- DONE
 * Move shadow calculation algorithm to the default shader dictionary <- goes along with standardizing uniforms i guess
 * Reform the shader dictionary lol <- SHADER MACROS !!!
 * Cubemap support (for lights and skyboxes) <- Done
 * Logo (kindadone) and Readme.md (done)
 * Figure out why the FPS loop is weird
 * Improvements on procedural building generation (aka multi building and scaling support) (Done, kinda)
 * Optimize OpenGL calls, ESPECIALLY the VBOs that store the model data
 * We NEED VAOs
 * Model blueprint class <- Done
 * Coverage testing <- what
 * Sound stuff
 * Physics stuff <- Uma hora eu chego lá kkkkkkkkk
 * Trigger stuff
 * Collision stuff <- WIP
 * Animation stuff
 * Perhaps a static OpenGL manager class?
 * Denote static buffers explicitly as static? <- DONE afaik
 * Interaction radius detection interface <- Done
 * Object declaration instantiation generation annotation?
 * Calculate the restitution variable lol <- Womp womp
 * Also the physics module needs the friction force <- Womp womp
 * Parallel contact is weird currently <- Womp womp
 * Static functions that creates objects with bound buffers
 * Shader recompile function
 * Render order priority variable/method? (Done)
 * Rewrite the normal handling code (Done)
 * Model editor (maybe a inbuilt tools tools package)
 * Interpolation interface and objects (Done)
 * FPS manager for windowmanagers
 * TAG para operações que criam objetos <- +- feito
 * Filter functions? (functions that filter :p) <- what was bro yapping about
 * Dinamically alocated text object
 * Texturable interface?
 * Modular uniform sync
 * Subdividing triangles of a model for debugging purposes
 * Shader macros (better than the shaderdict) [fog calculations, light calculations, shadow calculations etc.]
 *      maybe bind it to the RenderingContext object (for global qualities like fog rendering)
 * Classe que representa uma série de transformações? <- Transform?
 * Multi threadening <- lmao good luck with all these static buffers
 * Material Interface (for friction, restitution etc.)
 * */

//FIXME: seems like SolidColorShaderProgram throws an OpenGL error somehow
// TODO o cálculo do inverso matricial é válido? seria legal ter um teste p/ isso
// Shader dict: Refaz tudo, uma função deve ser $(nomeDaFuncao), usa um REGEX pelamor
// talvez só substituir o corpo da função para evitar problemas de compatibilidade?
// buffer de texto: faz +- igual o... scons?? esqueci o nome
// buffer livre com função de flip

// Manter a rotação do tensor inercial e o atrito do plano ao mesmo tempo tem efeitos ruins
// eu não tenho a mínima ideia de se o problema é o tensor inercial invertido rotacionado ou
// a projeção feita pra calcular o atrito no plano

public class Gaem3D {

    public static final int TARGET_FPS = 120;
    public static final float[] DEFAULT_SKY_COLOR = {.5f, .5f, .5f, 1};
    public static final float SPEED = .1F;
    public static final float[] camera = {0, 4, 0, 0};
    protected static final String TITLE = "3 DIMENSÇÕES";
    protected static final int D = 512;
    public static int framesElapsed = 0;
    static WindowManager manager;
    static RenderingContext context;
    static PhysicsContext physicsContext = new PhysicsContext();

    static float[] projectionMatrix = new float[16];

    static PhysicsObjectDecorator<PhysicsObject, Positionable> p = null;
    static final Random r = new Random(2223);

    // Pelo amor de deus eu não vou fazer um cast para todas as teclas
    // "ah erro de precisão mimimimimimi"
    // me dá um tempo IntelliJ
    @SuppressWarnings("lossy-conversions")
    public static void main(String[] args) {

        //GLFW
        manager = new WindowManager(600, 400, TITLE);
        manager.init();
        manager.addUpdateEvent(() -> { //walk events
            double s = Math.sin(camera[3]);
            double c = Math.cos(camera[3]);
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
                camera[2] += SPEED * c;
                camera[0] += SPEED * s;
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
                camera[2] -= SPEED * c;
                camera[0] -= SPEED * s;
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
                camera[0] += SPEED * c;
                camera[2] -= SPEED * s;
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
                camera[0] -= SPEED * c;
                camera[2] += SPEED * s;
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_Q) == GLFW.GLFW_PRESS) {
                camera[3] += Math.PI / 90;
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_E) == GLFW.GLFW_PRESS) {
                camera[3] -= Math.PI / 90;
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_Z) == GLFW.GLFW_PRESS) {
                camera[1] += SPEED;
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_X) == GLFW.GLFW_PRESS) {
                camera[1] -= SPEED;
            }

            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) {
                p.applyAcceleration(.1F, 0, 0);
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) {
                p.applyAcceleration(0, 0, -.1F);
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS) {
                p.applyAcceleration(0, 0, .1F);
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) {
                p.applyAcceleration(-.1F, 0, 0);
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_R) == GLFW.GLFW_PRESS) {
                ((RigidBody)p.getActor()).applyTorque(.04F,0,0);
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_T) == GLFW.GLFW_PRESS) {
                ((RigidBody)p.getActor()).applyTorque(-.04F,0,0);
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_Y) == GLFW.GLFW_PRESS) {
                ((RigidBody)p.getActor()).applyTorque(0,.04F,0);
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_U) == GLFW.GLFW_PRESS) {
                ((RigidBody)p.getActor()).applyTorque(0,-.04F,0);
            }
            if (GLFW.glfwGetKey(manager.getWindow(), GLFW.GLFW_KEY_P) == GLFW.GLFW_PRESS) {
                ((RigidBody)p.getActor()).setAngularVelocity(0,0,0);
            }
        });

        ShaderManager.setIsGlobalShaderDictEnabled(true);
        ShaderDictionary globalDict = ShaderManager.getGlobalShaderDictionary();
        globalDict.addAllValues(ShaderDictionary.fromFile(Utils.SHADERS_DIR + "/DefaultShaderDictionary.sdc"));
        context = new RenderingContext();

        //scenery setup

        context.setLight(0, 0, 10, 0, 1000)
                .setBackGroundColor(.5f, .5f, .6f)
                .setActive(true)
                .setFogDetails(700, 20);

        TexturedModel chessFloor = new TexturedModel(
                new float[]{-50, 0, -50, 0, 50, 0, -50, 0, -50, 0, 50, 0, 50, 0, 50, 0},
                new int[]{0, 1, 2, 2, 1, 3},
                new float[]{0, 0, 100, 0, 0, 100, 100, 100},
                TextureManager.loadTexture(Utils.TEXTURES_DIR + "/xadrez.png") // não funciona em distribuições unix???
        );

        context.addObject(chessFloor);

//        Model cube = Model.loadModel(Utils.readFile(Utils.MODELS_DIR + "/cube.obj"), new SolidColorShaderProgram(0, 1, 0));
        BufferedImage cat = Utils.readImage(Utils.TEXTURES_DIR + "/img.png");
        Skybox sk = new Skybox(TextureManager.generateCubemapTexture(
                new BufferedImage[]{cat, cat, cat, cat, cat, cat}
        ));
        context.addObject(sk);
        context.addScript(((CubemapInterpreterShaderProgram) sk.getShader()).genRotationUpdateRunnable(camera));

        // Phys Obj

        FixedPlane fixedPlane = new FixedPlane(
                new float[]{0, 1, 0},
                new float[]{0, 0, 0}
        );
        fixedPlane.setFriction(0.0F);
        physicsContext.addObject(fixedPlane);

        PhysicsObjectDecorator<Particle, Model> ball = new PhysicsObjectDecorator<>(
                new Sphere(1,1),
                Mesh.CUBE.toModel(
                        new SolidColorShaderProgram(1, 0, 0)
                )
        );
        physicsContext.addObject(ball);
        context.addObject(ball);

        physicsContext.addForce(new Gravity(9.8F));
        physicsContext.addForce(new Drag(.01F,.01F));

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
                GLFW.glfwSetWindowTitle(manager.windowName, TITLE + "    FPS: " + (framesLastSecond) + "(x: " + camera[0] + " y: " + camera[1] + " z:" + camera[2] + ")");
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

    Renderable line = null;

    protected static void doLogic() {
        int n = 1;
        float rate = 1F;
        for (int i = 0; i < n; i++) {
            physicsContext.update(rate / (60F*n));
        }

        List<CollisionVisualization> collisionOutputStream = CollisionVisualization.COLLISION_OUTPUT_STREAM;
        if(collisionOutputStream != null && !collisionOutputStream.isEmpty()){
            context.addObject(collisionOutputStream.get(0));
            collisionOutputStream.remove(0);
        }

        if (framesElapsed % (60 * 39284) == 0) {
            System.out.println("SHAW");
            RigidBody r = new Cube(1);
            Model m = Model.loadModel(Utils.readFile(Utils.MODELS_DIR + "/cube.obj"), new SolidColorShaderProgram(0, 1, 0,.5F));
            p = new PhysicsObjectDecorator<>(
//                    new Sphere(60*r.nextFloat()+.01F, 1.0F),
//                    new Sphere(1, 1.0F),
                    r,
                    m
            );

            context.addObject(p);
            context.addObject(new VertexTracker((Model) p.getRenderable()));
            physicsContext.addObject(p);
            //p.setCoordinates(Gaem3D.r.nextFloat() - .5F, 2f, Gaem3D.r.nextFloat() - .5F);
            p.setCoordinates(0,2,0);

            final float[] rVertices = m.getRelativeVertices();
            for (int i = 0; i < rVertices.length; i+= 4) {
                AngularVelocityVisualizer l = new AngularVelocityVisualizer(m,r,i);
                context.addObject(l);
            }
            VectorTranslator.debugVector(p.x(),p.y(),p.z());
        }

    }

    protected static void doRenderLogic() {
        MatrixTranslator.generatePerspectiveProjectionMatrix(projectionMatrix, 0.01f, 1000f, (float) Math.toRadians(45), manager.viewportSize[0], manager.viewportSize[1]);
        MatrixTranslator.applyLookTransformation(camera, (float) (camera[0] + Math.sin(camera[3])), camera[1], (float) (camera[2] + Math.cos(camera[3])), 0, 1, 0, projectionMatrix);
        context.doLogic();
        manager.fitViewport();
        context.doRender(projectionMatrix);
        RenderingManager.printErrorQueue();
        manager.update();
    }

}
