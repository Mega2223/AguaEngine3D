package net.mega2223.lwjgltest.aguaengine3d.logic;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.*;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.RenderingManager;
import net.mega2223.lwjgltest.aguaengine3d.mathematics.MatrixTranslator;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public class Context {

    protected List<Model> objects = new ArrayList<>();
    int itneration = 0;
    float[] backGroundColor = {.5f,.5f,.6f,1};
    float[] fogDetails = new float[2];
    protected boolean active = false;
    protected boolean areFBOSValid = false;
    public Context(){

    }

    public Context addObjects(List<Model> toAdd){
        for(Model ac : toAdd){addObject(ac);}
        return this;
    }
    public Context addObject(Model toAdd){
        objects.add(toAdd);
        this.synchronizeUniforms(toAdd.getShader());
        return this;
    }

    public void doLogic(){
        if(!active){return;}
        for(Model o : objects){
            o.doLogic(itneration);
        }
        itneration++;
    }

    private final float[] bufferTransMatrix = new float[16];

    ShaderProgram testShaderProgram = null;

    public void doRender(float[] projectionMatrix){
        if(!areFBOSValid){initFBOS();}
        //shadow render
        for(int l = 0; l < lights.length; l++) {
            float[] transMat = MatrixTranslator.createTranslationMatrix(0, 0, 0);
            float[] projMat = new float[16];
            MatrixTranslator.generateProjectionMatrix(projMat, 0.01F, 1000F, (float) Math.toRadians(45), 200, 200);
            MatrixTranslator.applyLookTransformation(projMat, lights[l], 10, 0, 10, 0, 1, 0);
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,shadowFBOS[l][0]);
            GL30.glClear(GL30.GL_DEPTH_BUFFER_BIT);
            for (Model o : objects) {
                o.getShader().setUniforms(itneration, transMat, projMat);
                o.drawForceShader(DepthBufferShaderProgram.GLOBAL_INSTANCE);
            }
        }
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,0);
        //if(true){return;}

        for(Model o : objects){//scene render
            MatrixTranslator.generateTranslationMatrix(bufferTransMatrix,o.getCoords());
            o.getShader().setUniforms(itneration, bufferTransMatrix,projectionMatrix);
            o.draw();
        }
    }

    /**Assumes that the rendering context is already in place*/
    public void doCustomRender(float projectionMatrix[]){
        MatrixTranslator.generateTranslationMatrix(bufferTransMatrix,0,0,0);
        for(Model m : objects){
            m.getShader().setUniforms(itneration,bufferTransMatrix,projectionMatrix);
            m.draw();
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
        fogDetails[0] = dist;
        fogDetails[1] = dissolve;
        for (Model o : objects) {
            //todo sub optimal uniform call
            GL30.glUseProgram(o.getShader().getID());
            GL30.glUniform1f(GL30.glGetUniformLocation(o.getShader().getID(), "fogStart"),dist);
            GL30.glUniform1f(GL30.glGetUniformLocation(o.getShader().getID(), "fogDissolve"),dissolve);
        }
    }
    int[][] shadowFBOS = new int[ShaderProgram.MAX_LIGHTS][];
    final float[][] lights = new float[ShaderProgram.MAX_LIGHTS][4];
    final float[][] lightColors = new float[ShaderProgram.MAX_LIGHTS][4];

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
        lightColors[index][0] = r;
        lightColors[index][1] = g;
        lightColors[index][2] = b;
        lightColors[index][3] = influence;

        for (Model o : objects){
            o.getShader().setLightColor(index, r, g, b, influence);
        }
    }

    public void synchronizeUniforms(ShaderProgram program){
        GL30.glUseProgram(program.getID());
        program.setLights(lights);
        for(int i = 0; i < lightColors.length; i++){program.setLightColor(i,lightColors[i][0],lightColors[i][1],lightColors[i][2],lightColors[i][3]);}
        //todo sub optimal uniform call
        GL30.glUniform1f(GL30.glGetUniformLocation(program.getID(), "fogStart"),fogDetails[0]);
        GL30.glUniform1f(GL30.glGetUniformLocation(program.getID(), "fogDissolve"),fogDetails[1]);

    }

    void initFBOS(){
        for(int i = 0; i < shadowFBOS.length; i++){
            shadowFBOS[i]=RenderingManager.genDepthFrameBufferObject(1000,1000);//fixme
        }
        areFBOSValid = true;
    }
}
