import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.TexturedModel;
import net.mega2223.lwjgltest.aguaengine3d.logic.objects.RenderableObject;

import java.util.ArrayList;
import java.util.List;

public class GeneratedMuseum implements RenderableObject {

    int[] dims;
    float[] pos = {0,0,0,0};
    List<Model> allModels, walls, photos = new ArrayList<>();

    public GeneratedMuseum(int[] dims, int wallTexture, int[] photos){
        this.dims = dims;
        for (int i = 0; i < photos.length; i++) {

        }
    }


    @Override
    public void doLogic(int itneration) {

    }

    @Override
    public List<Model> getModels() {
        return allModels;
    }

    @Override
    public float[] getPosition() {
        return pos;
    }

    @Override
    public void setPosition(float[] position) {
        this.pos = position;
    }


}
