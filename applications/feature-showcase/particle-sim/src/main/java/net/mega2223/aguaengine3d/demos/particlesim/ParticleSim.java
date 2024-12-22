package net.mega2223.aguaengine3d.demos.particlesim;

import com.sun.org.apache.xpath.internal.operations.Bool;
import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.graphics.utils.ShaderDictionary;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.misc.Utils;
import net.mega2223.aguaengine3d.objects.WindowManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleSim {
    public static final int TARGET_FPS = 60;
    public static final String TITLE = "Particulas :)";

    public static final Force.Gravity GRAVITY = new Force.Gravity(1, .1F);
    public static final Force.Drag DRAG = new Force.Drag(.1F, 1F);
    public static final Force.Repulsion REPULSION = new Force.Repulsion(15, .5F);
    public static final Force.Electromag ELECTROMAG = new Force.Electromag(1, .1F);
    public static final Force.Strong STRONG = new Force.Strong(1,1,1,.1F);

    static RenderingContext context;
    static WindowManager manager;
    static long framesElapsed = 0;
    static float[] projection = new float[16];
    static float[] cam = {0,0,100};

    static final List<Force> forces = new ArrayList<>();
    static boolean pause = false;

    public static void main(String[] args) {
        manager = new WindowManager(720,720, TITLE);
        manager.init();

        ShaderManager.setIsGlobalShaderDictEnabled(true);
        ShaderDictionary globalDict = ShaderManager.getGlobalShaderDictionary();
        globalDict.addAllValues(ShaderDictionary.fromFile(Utils.SHADERS_DIR + "/DefaultShaderDictionary.sdc"));
        context = new RenderingContext();
        context.setLight(0, 0, 10, 0, 10)
                .setBackGroundColor(.5f, .5f, .6f)
                .setActive(true)
                .setFogDetails(7, 20);
        long notRendered = 0;
        long lastLoop = System.currentTimeMillis();
        int framesLastSecond = 0;
        long fLSLastUpdate = 0;
        context.setActive(true);

        manager.addKeypressEvent(new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if(key == GLFW.GLFW_KEY_ENTER && action == GLFW.GLFW_PRESS){
                    Particle p = new Particle(1);
                    p.x = -cam[0]; p.y = -cam[1];
                    context.addObject(p);
                }
                if(key == GLFW.GLFW_KEY_1 && action == GLFW.GLFW_PRESS){
                    Particle p = new Particle(.25F,-1F);
                    p.x = -cam[0]; p.y = -cam[1];
                    context.addObject(p);
                }
                if(key == GLFW.GLFW_KEY_2 && action == GLFW.GLFW_PRESS){
                    Particle p = new Particle(1,0);
                    p.x = -cam[0]; p.y = -cam[1];
                    context.addObject(p);
                }
                if(key == GLFW.GLFW_KEY_3 && action == GLFW.GLFW_PRESS){
                    Particle p = new Particle(1,1);
                    p.x = -cam[0]; p.y = -cam[1];
                    context.addObject(p);
                }
                if(key == GLFW.GLFW_KEY_G && action == GLFW.GLFW_PRESS){
                    Force f = GRAVITY;
                    if(forces.contains(f)){forces.remove(f);} else {
                        forces.add(f);
                    }
                }
                if(key == GLFW.GLFW_KEY_H && action == GLFW.GLFW_PRESS){
                    Force f = STRONG;
                    if(forces.contains(f)){forces.remove(f);} else {
                        forces.add(f);
                    }
                }
                else if (key == GLFW.GLFW_KEY_UP&& action == GLFW.GLFW_PRESS){cam[1]-=.1F *cam[2];}
                else if (key == GLFW.GLFW_KEY_DOWN&& action == GLFW.GLFW_PRESS){cam[1]+=.1F *cam[2];}
                else if (key == GLFW.GLFW_KEY_LEFT&& action == GLFW.GLFW_PRESS){cam[0]+=.1F *cam[2];}
                else if (key == GLFW.GLFW_KEY_RIGHT&& action == GLFW.GLFW_PRESS){cam[0]-=.1F *cam[2];}
                else if (key == GLFW.GLFW_KEY_PAGE_UP&& action == GLFW.GLFW_PRESS){cam[2]-=25;}
                else if (key == GLFW.GLFW_KEY_PAGE_DOWN&& action == GLFW.GLFW_PRESS){cam[2]+=25;}
                if(key == GLFW.GLFW_KEY_P && action == GLFW.GLFW_PRESS){pause = !pause;}
            }
        });

        forces.add(GRAVITY);
        forces.add(DRAG);
        forces.add(REPULSION);
        forces.add(ELECTROMAG);
        forces.add(STRONG);

        for (int i = 0; i < 256; i++) {
            float x = (float) Math.random(); float y = (float) Math.random();
            x *= 700; y *= 700;
            boolean e = new Random().nextBoolean();
            Particle p;
            if (e){
                p = new Particle(.1F,-1);
            } else {
                p = new Particle(1,1);
            }
            context.addObject(p);
            p.x = x; p.y = y;
        }

        while (!GLFW.glfwWindowShouldClose(manager.windowName)) {
            notRendered += System.currentTimeMillis() - lastLoop;
            lastLoop = System.currentTimeMillis();
            if (System.currentTimeMillis() - fLSLastUpdate > 1000) {
                fLSLastUpdate = System.currentTimeMillis();
                GLFW.glfwSetWindowTitle(manager.windowName, TITLE + "    FPS: " + (framesLastSecond));
                framesLastSecond = 0;
            }
            if (notRendered > (1000 / TARGET_FPS)) {
                long cycleStart = System.currentTimeMillis();
                doLogic();
                doRenderLogic();
                notRendered = 0; framesElapsed++; framesLastSecond++;
            }
        }
    }

    public static void doLogic(){
        MatrixTranslator.generateTranslationMatrix(cam[0],cam[1],0,projection);
        MatrixTranslator.getTransposeMatrix4(projection);
        projection[15] = cam[2];
        if(pause){return;}
        float avgX = 0, avgY = 0, c = 0;
        for(Renderable act : context.getObjects()){
            if(!(act instanceof Particle)){continue;}
            Particle p = (Particle) act;
            p.doLogic((int) framesElapsed,context,forces);
            avgX+=p.x; avgY = p.y;c+=1;
        }
        avgX /= c; avgY /=c;
//        for(Renderable act : context.getObjects()){
//            if(!(act instanceof Particle)){continue;}
//            Particle p = (Particle) act;
//            p.x-=avgX; p.y-= avgY;
//        }
    }

    public static void doRenderLogic(){
        context.doLogic();
        manager.fitViewport();
        context.doRender(projection);
        RenderingManager.printErrorQueue();
        manager.update();
    }

}
