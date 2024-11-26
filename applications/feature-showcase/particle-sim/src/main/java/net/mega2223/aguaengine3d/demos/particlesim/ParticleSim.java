package net.mega2223.aguaengine3d.demos.particlesim;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Particle;
import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.aguaengine3d.graphics.utils.ShaderDictionary;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.misc.Utils;
import net.mega2223.aguaengine3d.objects.WindowManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public class ParticleSim {
    public static final int TARGET_FPS = 60;
    public static final String TITLE = "Particulas :)";

    static RenderingContext context;
    static WindowManager manager;
    static long framesElapsed = 0;
    static float[] projection = new float[16];

    static final List<Force> forces = new ArrayList<>();

    public static void main(String[] args) {
        manager = new WindowManager(256,256, TITLE);
        manager.init();

        ShaderManager.setIsGlobalShaderDictEnabled(true);
        ShaderDictionary globalDict = ShaderManager.getGlobalShaderDictionary();
        globalDict.addAllValues(ShaderDictionary.fromFile(Utils.SHADERS_DIR + "/DefaultShaderDictionary.sdc"));
        context = new RenderingContext();
        context.setLight(0, 0, 10, 0, 1000)
                .setBackGroundColor(.5f, .5f, .6f)
                .setActive(true)
                .setFogDetails(7, 20);
        long notRendered = 0;
        long lastLoop = System.currentTimeMillis();
        int framesLastSecond = 0;
        long fLSLastUpdate = 0;
        context.setActive(true);

        for (int i = 0; i < 100; i++) {
            Particle p = new Particle();
            context.addObject(p);
            p.x = 60 * (float) (Math.random() - 0.5F);
            p.y = 60 * (float) (Math.random() - 0.5F);
            p.sX = 20 * (float) (Math.random() - 0.5F);
            p.sY = 20 * (float) (Math.random() - 0.5F);
        }

        Force gravity = (context, p) -> {
            for(Renderable act : context){
                if(!(act instanceof Particle)){continue;}
                Particle p2 = (Particle) act;
                float dist = (float) Math.sqrt(
                        (p2.x - p.x)*(p2.x - p.x)+
                        (p2.y - p.y)*(p2.y - p.y)
                );
                p.sX -= (float) (p.x - p2.x)/600.0F;
                p.sY -= (float) (p.y - p2.y)/600.0F;
            }
        };
        Force rep = (context, p) -> {
            for(Renderable act : context){
                if(!(act instanceof Particle)){continue;}
                Particle p2 = (Particle) act;
                float dist = (float) Math.sqrt((p2.x - p.x)*(p2.x - p.x)+ (p2.y - p.y)*(p2.y - p.y));
                dist = Math.max(1,dist*dist);
                p.sX += (p.x - p2.x) /(dist);
                p.sY += (p.y - p2.y) /(dist);
            }
        };
        Force drag = (context, p) -> {
            p.sX -= p.sX * .01F;
            p.sY -= p.sY * .01F;
        };
        forces.add(gravity);
        forces.add(drag);
        forces.add(rep);

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

        float avgX = 0, avgY = 0, c = 0;
        for(Renderable act : context.getObjects()){
            if(!(act instanceof Particle)){continue;}
            Particle p = (Particle) act;
            avgX+=p.x; avgY = p.y;c+=1;
        }
        avgX /= c; avgY /=c;
        MatrixTranslator.generateTranslationMatrix(0,0,0,projection);
        for(Renderable act : context.getObjects()){
            if(!(act instanceof Particle)){continue;}
            Particle p = (Particle) act;
            p.x-=avgX; p.y-= avgY;
        }

        projection[15] = 60;
    }

    public static void doRenderLogic(){
        context.doLogic();
        manager.fitViewport();
        context.doRender(projection);
        RenderingManager.printErrorQueue();
        manager.update();
    }

    public static class Particle extends Model {
        private static final float[] v = {-1,-1,0,0, 1,-1,0,0, -1,1,0,0, 1,1,0,0};
        private static final int[] i = {0,1,2, 1,2,3};
        public ParticleShaderProgram shader;

        float x = 0, y = 0, sX = 0, sY = 0;

        public Particle() {
            super(v, i, new ParticleShaderProgram());
            this.shader = (ParticleShaderProgram) this.getShader();
        }

        @Override
        public void doLogic(int iteration) {
            x += sX; y+= sY;
            List<Renderable> renderables = context.getObjects();
            for (Force act : forces) {
                act.apply(renderables,this);
            }
            this.coords[0] = x; this.coords[1] = y;
        }

    }

    public static class ParticleShaderProgram implements ShaderProgram{
        int id, translation_loc = 0, rotation_loc = 0, projection_loc = 0, color_loc = 0;
        public ParticleShaderProgram(){
            id = ShaderManager.loadShaderFromFiles(
                    new String[]{
                            Utils.SHADERS_DIR + "\\fragment.fsh", Utils.SHADERS_DIR + "\\vertex.vsh"
                    });
            initUniforms();
        }

        @Override
        public int getID() {
            return id;
        }

        public void initUniforms() {
            translation_loc = GL30.glGetUniformLocation(id,"translation");
            rotation_loc = GL30.glGetUniformLocation(id,"rotation");
            projection_loc = GL30.glGetUniformLocation(id,"projection");
            color_loc = GL30.glGetUniformLocation(id,"color2");
            GL30.glUseProgram(id);
            GL30.glUniform4f(color_loc,
                    (float) Math.random(),
                    (float) Math.random(),
                    (float) Math.random(),
                    1);
        }
        public void setUniforms(int iteration, float[] translationMatrix, float[] projectionMatrix) {
            GL30.glUseProgram(id);
            GL30.glUniformMatrix4fv(translation_loc,false,translationMatrix);
            GL30.glUniformMatrix4fv(projection_loc,false,projectionMatrix);
        }

        public int[] getLightspaceTextureLocs() {return null;}
        public void setRotationMatrix(float[] m4) {}
        public void setRenderShadows(int index, boolean s) {}
    }

    public interface Force{
        void apply(List<Renderable> context, Particle p);
    }
}
