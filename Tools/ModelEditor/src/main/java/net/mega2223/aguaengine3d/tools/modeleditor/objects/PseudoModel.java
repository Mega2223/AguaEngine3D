package net.mega2223.aguaengine3d.tools.modeleditor.objects;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;

import java.util.ArrayList;

public class PseudoModel {

    private static int modelCount = 0;

    protected final String name; //setname?

    ArrayList<Float> vertices = new ArrayList<>();
    ArrayList<Integer> indices = new ArrayList<>();
    ArrayList<Float> textureCoords = new ArrayList<>();

    public PseudoModel(){
        modelCount++;
        name = "Model #" + modelCount;
    }

    @Override
    public String toString() {
        return name;
    }
}
