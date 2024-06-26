package net.mega2223.aguaengine3d.objects;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class WindowManager {
    public final long windowName;
    public final int[] viewportSize;
    public WindowManager(int w, int h, String title){
        if ( !glfwInit() ) {throw new IllegalStateException("Unable to initialize GLFW");}
        windowName = GLFW.glfwCreateWindow(w,h,title, MemoryUtil.NULL,MemoryUtil.NULL);
        viewportSize = new int[]{w,h};
    }

    @SuppressWarnings("CommentedOutCode")
    public void init(){
        GLFWErrorCallback.createPrint(System.err).set();
        GLFW.glfwMakeContextCurrent(windowName);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE,GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE,GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL30.GL_TRUE);
        //GLFW.glfwWindowHint(GLFW.GLFW_VERSION_MINOR,2);
        //GLFW.glfwWindowHint(GLFW.GLFW_VERSION_MAJOR,3);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwDefaultWindowHints();

        GL.createCapabilities();
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glDisable(GL30.GL_CULL_FACE);
        GL30.glDepthFunc(GL30.GL_LESS);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

        GLFW.glfwShowWindow(windowName);

        GLFW.glfwSetKeyCallback(windowName, new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if(key == GLFW_KEY_ESCAPE){
                    GLFW.glfwSetWindowShouldClose(windowName,true);
                }
                doKeyCallbackEvents(window,key,scancode,action,mods);
            }
        });
        GLFW.glfwSetCharCallback(windowName, this::doCharCallbackEvents);
        GLFW.glfwSetWindowSizeCallback(windowName, (l, w, h) -> {
            viewportSize[0] = w;
            viewportSize[1] = h;
            fitViewport();
        });
    }

    public void update(){

        if(glfwWindowShouldClose(windowName)){
            GLFW.glfwDestroyWindow(windowName);
            System.exit(0);
        }

        GLFW.glfwSwapBuffers(windowName);
        GLFW.glfwPollEvents();

        for (Runnable act : updateEventList){
            act.run();
        }

        clear();
    }

    public void fitViewport(){
        GL30.glViewport(0,0,viewportSize[0],viewportSize[1]);
    }

    void clear(){
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
    }

    public void clearColor(float r, float g, float b, float a){
        GL30.glClearColor(r,g,b,a);
    }

    public long getWindow(){return windowName;}

    List<GLFWKeyCallbackI> keyCallbacksList = new ArrayList<>();
    List<Runnable> updateEventList = new ArrayList<>();
    List<GLFWCharCallbackI> charCallbacksList = new ArrayList<>();

    private void doKeyCallbackEvents(long window, int key, int scancode, int action, int mods){
        for(GLFWKeyCallbackI act : keyCallbacksList){
            act.invoke(window,key,scancode,action,mods);
        }
    }
    private void doCharCallbackEvents(long window, int code){
        for (GLFWCharCallbackI act : charCallbacksList){
            act.invoke(window,code);
        }
    }

    public float getAspectRatio(){
        return viewportSize[0]/(float)viewportSize[1];
    }

    public void addUpdateEvent(Runnable event){
        updateEventList.add(event);
    }
    public void addKeypressEvent(GLFWKeyCallbackI keyCallback){
        keyCallbacksList.add(keyCallback);
    }
    public void addKeyCharEvent(GLFWCharCallbackI charCallbackI){
        charCallbacksList.add(charCallbackI);
    }
}
