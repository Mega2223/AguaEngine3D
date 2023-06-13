package net.mega2223.aguaengine3d.usecases.airsim.objects.simobjects;

import net.mega2223.lwjgltest.aguaengine3d.objects.WindowManager;
import org.lwjgl.glfw.GLFW;

public class PlayerControlledFlyingObject extends FlyingObject {

    public PlayerControlledFlyingObject(float x, float z, float weight, float altitude, float initialDirection, WindowManager glfwContext) {
        super(x, z, weight, altitude, initialDirection);
        glfwContext.addUpdateEvent(() -> {
            if(GLFW.glfwGetKey(glfwContext.getWindow(),GLFW.GLFW_KEY_W)==GLFW.GLFW_PRESS){
                addToThrottle(0.01f);}
            if(GLFW.glfwGetKey(glfwContext.getWindow(),GLFW.GLFW_KEY_S)==GLFW.GLFW_PRESS){
                addToThrottle(-0.01f);}
            if(GLFW.glfwGetKey(glfwContext.getWindow(),GLFW.GLFW_KEY_Q)==GLFW.GLFW_PRESS){
                addToYawControl(-0.01f);}
            if(GLFW.glfwGetKey(glfwContext.getWindow(),GLFW.GLFW_KEY_E)==GLFW.GLFW_PRESS){
                addToYawControl(0.01f);}
            if(GLFW.glfwGetKey(glfwContext.getWindow(),GLFW.GLFW_KEY_Z)==GLFW.GLFW_PRESS){
                addToPitchControl(-0.01f);}
            if(GLFW.glfwGetKey(glfwContext.getWindow(),GLFW.GLFW_KEY_X)==GLFW.GLFW_PRESS){
                addToPitchControl(0.01f);}

            FlyingObject.debugFlyingObject(this);
        });
    }
}
