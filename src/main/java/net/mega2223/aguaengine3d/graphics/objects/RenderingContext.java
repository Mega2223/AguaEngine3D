package net.mega2223.aguaengine3d.graphics.objects;

import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.graphics.objects.shadering.*;
import net.mega2223.aguaengine3d.graphics.utils.RenderingManager;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RenderingContext {

    protected final List<Renderable> objects = new ArrayList<>();
    protected final List<ScriptedSequence> scripts = new ArrayList<>();//perhaps have 2 lists?

    int itneration = 0;
    float[] backGroundColor = {.5f,.5f,.6f,1};
    final float[] fogDetails = new float[2];
    protected boolean active = false;
    protected boolean areFBOSValid = false;
    private final LightSpaceRenderingManager lightSpaceRenderingManager = new LightSpaceRenderingManager(this);

    public RenderingContext(){

    }

    public RenderingContext addObjects(List<Renderable> toAdd){
        for(Renderable ac : toAdd){addObject(ac);}
        return this;
    }
    public RenderingContext addObject(Renderable toAdd){
        objects.add(toAdd);
        this.synchronizeUniforms(toAdd.getShader());
        return this;
    }

    public void doLogic(){
        if(!active){return;}
        for(ScriptedSequence s : scripts){
            s.preLogic(itneration,this);
        }
        for(Renderable o : objects){o.doLogic(itneration);}
        for(ScriptedSequence s : scripts){
            s.postLogic(itneration,this);
        }
        itneration++;
    }

    private final float[] bufferTransMatrix = new float[16];

    public void doRender(float[] projectionMatrix){
        if(!areFBOSValid){initFBOS();}
        //todo shadow render pipeline
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,0);
        doCustomRender(projectionMatrix);
    }

    /** Assumes that the rendering context is already in place */
    public void doCustomRender(float[] projectionMatrix){
        for(Renderable m : objects){
            m.setUniforms(itneration,projectionMatrix);
            m.draw();
        }
    }

    public void doCustomRenderForceShader(float[] projectionMatrix, ShaderProgram shaderProgram){
        for(Renderable m : objects){
            m.setUniforms(itneration,projectionMatrix);
            m.drawForceShader(shaderProgram);
        }
    }

    public List<Renderable> getObjects(){
        return objects.subList(0,objects.size());
    }

    public boolean isActive() {
        return active;
    }

    public RenderingContext setActive(boolean active) {
        this.active = active;
        return this;
    }
    @Deprecated
    public RenderingContext setBackGroundColor(float[] backGroundColor) {
        this.backGroundColor = backGroundColor;
        GL30.glClearColor(backGroundColor[0],backGroundColor[1],backGroundColor[2],backGroundColor[3]);
        //sets the fog color uniform variable for every shader
        for (Renderable ac : getObjects()){
            int p = ac.getShader().getID();
            int c = GL30.glGetUniformLocation(p,"fogColor");
            GL30.glUseProgram(p);
            GL30.glUniform4fv(c,backGroundColor);
        }
        return this;
    }
    public RenderingContext setBackGroundColor(float r, float g, float b) {
        this.backGroundColor[0] = r;
        this.backGroundColor[1] = g;
        this.backGroundColor[2] = b;
        GL30.glClearColor(backGroundColor[0],backGroundColor[1],backGroundColor[2],backGroundColor[3]);
        //sets the fog color uniform variable for every shader
        for (Renderable ac : getObjects()){
            int p = ac.getShader().getID();
            int c = GL30.glGetUniformLocation(p,"fogColor");
            GL30.glUseProgram(p);
            GL30.glUniform4fv(c,backGroundColor);
        }
        return this;
    }

    public void setFogDetails(float dist, float dissolve){
        fogDetails[0] = dist;
        fogDetails[1] = dissolve;
        for (Renderable o : objects) {
            //todo sub optimal uniform call
            GL30.glUseProgram(o.getShader().getID());
            GL30.glUniform1f(GL30.glGetUniformLocation(o.getShader().getID(), "fogStart"),dist);
            GL30.glUniform1f(GL30.glGetUniformLocation(o.getShader().getID(), "fogDissolve"),dissolve);
        }
    }
    final int[][] shadowFBOS = new int[ShaderProgram.MAX_LIGHTS][];
    final float[][] lights = new float[ShaderProgram.MAX_LIGHTS][4];
    final float[][] lightColors = new float[ShaderProgram.MAX_LIGHTS][4];

    public void setLights(float[][] lights){
        for (int i = 0; i < lights.length; i++) {
            setLight(i,lights[i][0],lights[i][1],lights[i][2],lights[i][3]);
        }

    }
    public RenderingContext setLight(int index, float x, float y, float z, float brightness){
        for (Renderable o : objects){
            o.getShader().setLight(index,x,y,z,brightness);
        }
        lights[index][0] = x;
        lights[index][1] = y;
        lights[index][2] = z;
        lights[index][3] = brightness;
        lightSpaceRenderingManager.renderLightmapsAsNeeded();
        return this;
    }

    public RenderingContext setLightColor(int index, float r, float g, float b, float influence){
        lightColors[index][0] = r;
        lightColors[index][1] = g;
        lightColors[index][2] = b;
        lightColors[index][3] = influence;

        for (Renderable o : objects){
            o.getShader().setLightColor(index, r, g, b, influence);
        }
        return this;
    }

    public void setEnableShadowsForLight(int index, boolean value){
        lightSpaceRenderingManager.setDoShadowMapping(index,value);
    }

    public void addScript(ScriptedSequence sequence) {
        scripts.add(sequence);
    }
    public void removeScript(ScriptedSequence sequence) {
        scripts.remove(sequence);
    }
    public void removeScript(String sequenceName) {
        List<ScriptedSequence> toRemove = new ArrayList<>();
        for(ScriptedSequence ac : scripts){if(ac.name.equals(sequenceName)){toRemove.add(ac);}}
        scripts.removeAll(toRemove);
    }

    public void synchronizeUniforms(ShaderProgram program){
        GL30.glUseProgram(program.getID());
        program.setLights(lights);
        for(int i = 0; i < lightColors.length; i++){program.setLightColor(i,lightColors[i][0],lightColors[i][1],lightColors[i][2],lightColors[i][3]);}
        //todo sub optimal uniform call
        GL30.glUniform1f(GL30.glGetUniformLocation(program.getID(), "fogStart"),fogDetails[0]);
        GL30.glUniform1f(GL30.glGetUniformLocation(program.getID(), "fogDissolve"),fogDetails[1]);
        lightSpaceRenderingManager.alignUniforms(program);
    }

    void initFBOS(){
        for(int i = 0; i < shadowFBOS.length; i++){
            shadowFBOS[i]=RenderingManager.genDepthFrameBufferObject(1000,1000);//fixme
        }
        areFBOSValid = true;
    }
    //for testing purposes
    public LightSpaceRenderingManager getLightSpaceRenderingManager() {
        return lightSpaceRenderingManager;
    }
}
