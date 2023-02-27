package net.mega2223.lwjgltest.aguaengine3d.objects;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class WindowManager {
    public long windowName;
    public final int[] viewportSize;
    public WindowManager(int w, int h, String title){
        if ( !glfwInit() ) {throw new IllegalStateException("Unable to initialize GLFW");}
        windowName = GLFW.glfwCreateWindow(w,h,title, MemoryUtil.NULL,MemoryUtil.NULL);
        viewportSize = new int[]{w,h};
    }

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
        GL30.glDepthFunc(GL30.GL_LESS);
        //GLFW.glfwSwapInterval(1);

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
        GLFW.glfwSetWindowSizeCallback(windowName, (l, w, h) -> {
            GL30.glViewport(0,0,w,h);
            viewportSize[0] = w;
            viewportSize[1] = h;
            System.out.println("resize");
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

    void clear(){
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
    }

    public void clearColor(float r, float g, float b, float a){
        GL30.glClearColor(r,g,b,a);
    }

    public long getWindow(){return windowName;}

    List<GLFWKeyCallbackI> keyCallbacksList = new ArrayList<>();
    List<Runnable> updateEventList = new ArrayList<>();

    private void doKeyCallbackEvents(long window, int key, int scancode, int action, int mods){
        for(GLFWKeyCallbackI act : keyCallbacksList){
            act.invoke(window,key,scancode,action,mods);
        }
    }


    public void addUpdateEvent(Runnable event){
        updateEventList.add(event);
    }
    public void addKeypressEvent(GLFWKeyCallbackI keyCallback){
        keyCallbacksList.add(keyCallback);
    }
}
