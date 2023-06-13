package net.mega2223.aguaengine3d.usecases.airsim.objects;

import java.util.ArrayList;

public class Stage {

    ArrayList<SimObject> flyingObjects = new ArrayList(20);

    public final float gravity = 9.8f;

    public Stage(){}

    public void doTick(){
        for (int i = 0; i < flyingObjects.size(); i++) {
            flyingObjects.get(i).update();
        }
    }

    public void add(SimObject obj){
        flyingObjects.add(obj);
    }

}
