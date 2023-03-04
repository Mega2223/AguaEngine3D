package net.mega2223.lwjgltest.aguaengine3d.logic;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.lwjgltest.aguaengine3d.logic.objects.RenderableObject;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public class Context {

    List<RenderableObject> objects = new ArrayList<>();
    int itneration = 0;
    float[] backGroundColor = {.5f,.5f,.6f,1};
    protected boolean active = false;
    public Context(){

    }

    public Context addObjects(List<RenderableObject> toAdd){objects.addAll(toAdd);return this;}
    public Context addObject(RenderableObject toAdd){objects.add(toAdd);return this;}



    public void doLogic(){
        if(!active){return;}
        for(RenderableObject o : objects){
            o.doLogic(itneration);
        }
        itneration++;
    }

    public void doRender(float[] projectionMatrix){
        if(!active){return;}
        for(RenderableObject o : objects){
            o.drawnModels(itneration,projectionMatrix);
        }
    }

    public List<RenderableObject> getObjects(){
        return objects.subList(0,objects.size());
    }

    public boolean isActive() {
        return active;
    }

    public Context setActive(boolean active) {
        this.active = active;
        return this;
    }

    public void setBackGroundColor(float[] backGroundColor) {
        this.backGroundColor = backGroundColor;
        for (RenderableObject ac : getObjects()){
            for(Model acm : ac.getModels()){
                int p = acm.getShader().getID();
                int c = GL30.glGetUniformLocation(p,"fogColor");
                GL30.glUseProgram(p);
                GL30.glUniform4fv(c,backGroundColor);
            }
        }
    }
}
