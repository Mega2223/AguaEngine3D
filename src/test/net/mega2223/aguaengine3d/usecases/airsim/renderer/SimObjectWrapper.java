package net.mega2223.aguaengine3d.usecases.airsim.renderer;

import net.mega2223.aguaengine3d.usecases.airsim.objects.SimObject;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import org.lwjgl.opengl.GL30;

public class SimObjectWrapper extends SimObject {
    SimObject toWrap;
    Model model;

    public SimObjectWrapper(SimObject obj, Model model){
        super(obj.getX(), obj.getZ(), obj.getY());
        toWrap = obj;
        this.model = model;
        rotationUniformLoc = GL30.glGetUniformLocation(model.getShader().getID(),"rotation");
    }


    float[] transArray = new float[16];
    int rotationUniformLoc = -1;

    public void update(){
        toWrap.update();
        model.setCoords(toWrap.getX(),toWrap.getZ(),toWrap.getY());//todo temp fix
        float[] rotation = toWrap.getRotation();
        /*trans.identity().rotationXYZ(
                0,
                0,
                -rotation[1]
        ).get(transArray);fixme*/
        GL30.glUseProgram(model.getShader().getID());
        GL30.glUniformMatrix4fv(rotationUniformLoc,false,transArray);
    }
    public Model getModel(){return model;}

}
