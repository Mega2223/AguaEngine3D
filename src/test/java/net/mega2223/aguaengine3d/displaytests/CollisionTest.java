package net.mega2223.aguaengine3d.displaytests;

import net.mega2223.aguaengine3d.graphics.objects.modeling.TexturedModel;
import net.mega2223.aguaengine3d.graphics.objects.shadering.TextureShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.graphics.utils.TextureManager;
import net.mega2223.aguaengine3d.graphics.utils.WorldspaceDataVisualizer;
import net.mega2223.aguaengine3d.logic.PhysicsRenderContext;
import net.mega2223.aguaengine3d.logic.RigidBodyAggregate;
import net.mega2223.aguaengine3d.misc.Utils;
import net.mega2223.aguaengine3d.objects.WindowManager;
import net.mega2223.aguaengine3d.physics.PhysicsUtils;
import net.mega2223.aguaengine3d.physics.objects.RigidBodySystem;
import net.mega2223.aguaengine3d.physics.utils.objects.forces.ConstantForce;
import net.mega2223.aguaengine3d.physics.utils.objects.forces.DragForce;
import net.mega2223.aguaengine3d.physics.utils.objects.hitboxes.AxisParallelPlaneHitbox;
import net.mega2223.aguaengine3d.physics.utils.objects.hitboxes.RectHitbox;
import org.lwjgl.glfw.GLFW;

public class CollisionTest {
    public static void main(String[] args) {
        DefaultTestingEnviroment testingEnviroment = new DefaultTestingEnviroment();
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
        RigidBodyAggregate cube2 = new RigidBodyAggregate(cubeModel2,cube2Physics);

        PhysicsRenderContext context = testingEnviroment.context;
        WindowManager manager = testingEnviroment.manager;
        context.physContext().addForce(new DragForce(0.1F,1F));
        context.physContext().addForce(gravity);
        context.addObject(referenceCube);
        //context.physContext().addForce(new SpringForce(2,.1F,0,5,0));

        float[] test = new float[4];
        //PhysicsUtils.radiansToQuaternion(0,1,0,test);
        cube1Physics.setOrientation(test[0],test[1],test[2],test[3]);

        cube2.physicsHandler().addForce(gravity);
        cube2.physicsHandler().setCoordX(5);

        RectHitbox cube1Hitbox =  new RectHitbox(cube1.physicsHandler(),-1,-1,-1,1,1,1);
        cube1.physicsHandler().setCoords(2,2,0);
        new RectHitbox(cube2.physicsHandler(),-1,-1,-1,1,1,1);

        WorldspaceDataVisualizer.visualize3DVector(context.renderContext(),cube1Hitbox.lastCollisionCoords,cube1Hitbox.lastCollisionBuffer,1);

        context.physContext().getCollisionEnviroment().addHitbox(new AxisParallelPlaneHitbox(0));
        manager.addUpdateEvent(()->{
            final float amount = .1F;
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_UP)==GLFW.GLFW_PRESS){
                cube1Physics.applyOrientationTransform(amount,0,0);
            }
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_DOWN)==GLFW.GLFW_PRESS){
                cube1Physics.applyOrientationTransform(-amount,0,0);
            }
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_LEFT)==GLFW.GLFW_PRESS){
                cube1Physics.applyOrientationTransform(0,0,-amount);
            }
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_RIGHT)==GLFW.GLFW_PRESS){
                cube1Physics.applyOrientationTransform(0,0,amount);
            }
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_ENTER)==GLFW.GLFW_PRESS){
                cube1Physics.applyTorque(0,0,amount);
            }
            if(GLFW.glfwGetKey(manager.getWindow(),GLFW.GLFW_KEY_K)==GLFW.GLFW_PRESS){
                //cube1Physics.applyAcceleration(0,.1F,0,0,0,.1F,true);
                cube1Physics.applyForce(2,1,2,0,0,0.03F);
            }

        });
        float[] pointBuffer = new float[32];
        float[] matBuffer = new float[16];

        context.addObject(cube1);//.addObject(cube2);
        cube1Physics.applySpin(0,1,0);
        RenderingManager.printErrorQueue();
        testingEnviroment.timeToSimulate = 0;
        testingEnviroment.start();
    }
}
