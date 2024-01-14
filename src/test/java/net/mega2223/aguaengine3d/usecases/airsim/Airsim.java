package net.mega2223.aguaengine3d.usecases.airsim;

import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.objects.WindowManager;
import net.mega2223.aguaengine3d.usecases.airsim.objects.Stage;
import net.mega2223.aguaengine3d.usecases.airsim.objects.simobjects.FlyingObject;
import net.mega2223.aguaengine3d.usecases.airsim.objects.simobjects.PlayerControlledFlyingObject;
import net.mega2223.aguaengine3d.usecases.airsim.renderer.SimObjectWrapper;
import net.mega2223.aguaengine3d.usecases.airsim.renderer.shaders.SimpleSolidColorShaderProgram;
import org.lwjgl.glfw.GLFW;

public class Airsim {

    protected static final String TITLE = "Air Traffic Sim";
    protected static final int TARGET_FPS = 60;
    static float[] projectionMatrix = MatrixTranslator.createTranslationMatrix(.1f,.1f,.1f);
    static int framesElapsed = 0;
    //the translation matrix will initially be isometriaclly projected

    static float[] camera = {0,1,0};
    static RenderingContext context = new RenderingContext();
    static WindowManager manager;
    static Stage stage = new Stage();

    public static void main(String[] args) {
        //GLFW
        manager = new WindowManager(300,300, TITLE);
        manager.init();
        GLFW.glfwMaximizeWindow(manager.getWindow());

        FlyingObject ob = new PlayerControlledFlyingObject( 0,  0,1,0,0,manager);

        SimObjectWrapper wrapped = new SimObjectWrapper(ob,new Model(
                new float[]{-.5f,0,0,0, 0,1f,0,0, .5f,0,0,0},
                new int[]{0,1,2},
                new SimpleSolidColorShaderProgram(0,1,0)
        ));

        stage.add(wrapped);

        context.addObject(wrapped.getModel());
        //context.addObject(testModel);

        //Loop
        long unrendered = 0;
        //noinspection UnnecessaryLocalVariable
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
                //cpu logic
                doLogic();
                //render logic
                render();
                unrendered = 0;
                framesElapsed++;
                lastCycleDuration = System.currentTimeMillis() - cycleStart;
                framesLastSecond++;
            }
        }

    }

    static void doLogic(){
        context.doLogic();
        stage.doTick();

    }
    static void render(){
        float aspectRatio = (float) manager.viewportSize[0]/(float) manager.viewportSize[1];
        for (int i = 0; i < projectionMatrix.length; i++) {
            projectionMatrix[i] = 0;
        }
        float x = 1 / (float) manager.viewportSize[0];
        float y = 1 / (float) manager.viewportSize[1];
        x*= 20; y*= 20;

        projectionMatrix[0] = x;
        projectionMatrix[5] = y;
        projectionMatrix[10] = 1;
        projectionMatrix[15] = 1;

        context.doRender(projectionMatrix);
        manager.update();
    }

}
