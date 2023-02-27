package net.mega2223.lwjgltest.aguaengine3d.logic;

import net.mega2223.lwjgltest.aguaengine3d.logic.objects.RenderableObject;

import java.util.ArrayList;
import java.util.List;

public class Context {

    List<RenderableObject> objects = new ArrayList<>();
    int itneration = 0;

    public Context(){

    }

    public Context addObjects(List<RenderableObject> toAdd){objects.addAll(toAdd);return this;}
    public Context addObject(RenderableObject toAdd){objects.add(toAdd);return this;}


    public void doLogic(){
        for(RenderableObject o : objects){
            o.doLogic(itneration);
        }
        itneration++;
    }

    public void doRender(float[] projectionMatrix){
        for(RenderableObject o : objects){
            o.drawnModels(itneration,projectionMatrix);
        }
    }

}
