package net.mega2223.lwjgltest.aguaengine3d.logic;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.DepthBufferShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.lwjgltest.aguaengine3d.mathematics.MatrixTranslator;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public class Context {

    protected List<Model> objects = new ArrayList<>();
    int itneration = 0;
    float[] backGroundColor = {.5f,.5f,.6f,1};
    protected boolean active = false;
    protected boolean areFBOSValid = false;
    public Context(){

    }

    public Context addObjects(List<Model> toAdd){
        objects.addAll(toAdd);
        for(Model ac : toAdd){ac.getShader().setLights(lights);}
        return this;
    }
    public Context addObject(Model toAdd){
        objects.add(toAdd);
        toAdd.getShader().setLights(lights);
        return this;
    }

    public void doLogic(){
        if(!active){return;}
        for(Model o : objects){
            o.doLogic(itneration);
        }
        itneration++;
    }

    private final float[] transMatrix = new float[16];

    public void doRender(float[] projectionMatrix){
        if(!areFBOSValid){initFBOS();}
        //shadow render
        for(int s = 0; s < ShaderProgram.MAX_LIGHTS-9; s++){
            if(lights[s][3] == 0){continue;}

            float[] actProj = new float[16];
            float[] actTrans = MatrixTranslator.createTranslationMatrix(lights[s][0],lights[s][1],lights[s][2]);
            MatrixTranslator.generateProjectionMatrix(actProj,0.1f,100,(float)Math.toRadians(45.0f),100,100);
            MatrixTranslator.applyLookTransformation(actProj,lights[s],lights[s][0]+1,lights[s][1],lights[s][2],0,1,0);
            MatrixTranslator.debugMatrix4x4(actProj);
            GL30.glUseProgram(DepthBufferShaderProgram.GLOBAL_INSTANCE.getID());
            //GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,shadowFBOS[s][0]);
            //GL30.glClear(GL30.GL_DEPTH_BUFFER_BIT);
            DepthBufferShaderProgram.GLOBAL_INSTANCE.setUniforms(itneration,actTrans,actProj);
            for(Model o : objects){
                o.getShader().setUniforms(itneration,actTrans,actProj);
                o.draw();
                //o.drawForceShader(DepthBufferShaderProgram.GLOBAL_INSTANCE);
            }
            //GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,0);
        }
        if(true){return;}
        for(Model o : objects){//scene render
            MatrixTranslator.generateTranslationMatrix(transMatrix,o.getCoords());
            o.getShader().setUniforms(itneration,transMatrix,projectionMatrix);
            o.draw();
        }
    }

    public List<Model> getObjects(){
        return objects.subList(0,objects.size());
    }

    public boolean isActive() {
        return active;
    }

    public Context setActive(boolean active) {
        this.active = active;
        return this;
    }
    @Deprecated
    public void setBackGroundColor(float[] backGroundColor) {
        this.backGroundColor = backGroundColor;
        GL30.glClearColor(backGroundColor[0],backGroundColor[1],backGroundColor[2],backGroundColor[3]);
        //sets the fog color uniform variable for every shader
        for (Model ac : getObjects()){
            int p = ac.getShader().getID();
            int c = GL30.glGetUniformLocation(p,"fogColor");
            GL30.glUseProgram(p);
            GL30.glUniform4fv(c,backGroundColor);
        }
    }
    public void setBackGroundColor(float r, float g, float b) {
        this.backGroundColor[0] = r;
        this.backGroundColor[1] = g;
        this.backGroundColor[2] = b;
        GL30.glClearColor(backGroundColor[0],backGroundColor[1],backGroundColor[2],backGroundColor[3]);
        //sets the fog color uniform variable for every shader
        for (Model ac : getObjects()){
            int p = ac.getShader().getID();
            int c = GL30.glGetUniformLocation(p,"fogColor");
            GL30.glUseProgram(p);
            GL30.glUniform4fv(c,backGroundColor);
        }
    }

    public void setFogDetails(float dist, float dissolve){
        for (Model o : objects) {
            GL30.glUseProgram(o.getShader().getID());
            GL30.glUniform1f(GL30.glGetUniformLocation(o.getShader().getID(), "fogStart"),dist);
            GL30.glUniform1f(GL30.glGetUniformLocation(o.getShader().getID(), "fogDissolve"),dissolve);
        }
    }
    int[][] shadowFBOS = new int[ShaderProgram.MAX_LIGHTS][];
    final float[][] lights = new float[ShaderProgram.MAX_LIGHTS][4];
    public void setLights(float[][] lights){
        System.arraycopy(lights, 0, this.lights, 0, lights.length);
        for (Model o : objects){
            o.getShader().setLights(this.lights);
        }

    }
    public void setLight(int index, float x, float y, float z, float brightness){
        for (Model o : objects){
            o.getShader().setLight(index,x,y,z,brightness);
        }
        lights[index][0] = x;
        lights[index][1] = y;
        lights[index][2] = z;
        lights[index][3] = brightness;

    }

    public void setLightColor(int index, float r, float g, float b, float influence){
        for (Model o : objects){
            o.getShader().setLightColor(index, r, g, b, influence);
        }
    }

    void initFBOS(){
        for(int i = 0; i < shadowFBOS.length; i++){
            shadowFBOS[i]=RenderingManager.genDepthFrameBufferObject(1000,1000);//fixme
        }
        areFBOSValid = true;
    }
}
