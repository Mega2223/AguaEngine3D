package net.mega2223.aguaengine3d.tools.modeleditor.objects;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.modeling.TextureInterfaceComponent;
import net.mega2223.aguaengine3d.graphics.objects.shadering.DisplayComponentShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;

import java.util.ArrayList;

public class StackedTexts implements Renderable {
    public ArrayList<TextureInterfaceComponent> components = new ArrayList<>();

    public float[] scale = {1,1,1};
    public float[] coords = new float[4];
    public int aligment = TextureInterfaceComponent.TOP_LEFT_ALIGMENT;

    public StackedTexts(){

    }

    public void setAspectRatio(float aspectRatio) {
        for(TextureInterfaceComponent act : components){
            act.setAspectRatio(aspectRatio);
        }
    }

    public void setScale(float x, float y, float z) {
        this.scale[0] = x; this.scale[1] = y; this.scale[2] = z;
        for(TextureInterfaceComponent act : components){
            act.setCoords(x, y, z);
        }
    }

    public void setAligment(int aligment) {
        this.aligment = aligment;
        for(TextureInterfaceComponent act : components){
            act.setAligment(aligment);
        }
    }

    public void add(TextureInterfaceComponent component){
        this.components.add(component);
        component.setAligment(aligment);
        component.setScale(scale[0],scale[1],scale[2]);
    }

    public void doLogic(int iteration){
        for(TextureInterfaceComponent act : components){act.doLogic(iteration);}
    }

    public void setUniforms(int iteration, float[] projMatrix){
        for(TextureInterfaceComponent act : components){act.setUniforms(iteration,projMatrix);}
    }

    public void setCoords(float x, float y, float z){
        this.coords[0] = x; this.coords[1] = y; this.coords[2] = z;
    }

    @Override
    public ShaderProgram getShader() {
        return new DisplayComponentShaderProgram(-1,new float[0],1);
    }

    public void draw(){
        for (int i = 0; i < components.size(); i++) {
            TextureInterfaceComponent act = components.get(i);

            act.setCoords(coords[0],coords[1]+(components.size()-i),coords[2]);
            act.draw();
        }
    }

    public void drawForceShader(ShaderProgram shader) {
        for(TextureInterfaceComponent act : components){act.drawForceShader(shader);}
    }
}
