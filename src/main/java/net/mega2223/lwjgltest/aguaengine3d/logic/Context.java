package net.mega2223.lwjgltest.aguaengine3d.logic;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.mathematics.MatrixTranslator;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public class Context {

    protected List<Model> objects = new ArrayList<>();
    int itneration = 0;
    float[] backGroundColor = {.5f,.5f,.6f,1};
    protected boolean active = false;
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

    private float[] transMatrix = new float[16];
    public void doRender(float[] projectionMatrix){
        for(Model o : objects){
            MatrixTranslator.generateTranslationMatrix(transMatrix,o.getCoords());
            o.getShader().setUniforms(itneration,transMatrix,projectionMatrix);
            o.drawn();
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
    float[][] lights = new float[ShaderProgram.MAX_LIGHTS][4];
    public void setLights(float[][] lights){
        for (Model o : objects){
            o.getShader().setLights(lights);
        }
        this.lights = lights;
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
}
