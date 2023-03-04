package net.mega2223.lwjgltest.aguaengine3d.logic.objects;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class StaticRenderable implements RenderableObject{
    List<Model> models = new ArrayList<>();
    float[] position = new float[]{0,0,0,0};

    public StaticRenderable(Model[] models){
        this.models.addAll(Arrays.asList(models));
    }
    public StaticRenderable(Model model){
        this.models.add(model);
    }

    @Override
    public void doLogic(int itneration) {
        //literally nothing, that's the point
    }

    @Override
    public List<Model> getModels() {
        return models;
    }

    @Override
    public float[] getPosition() {
        return position;
    }

    @Override
    public void setPosition(float[] position) {
        this.position[0] = position[0];
        this.position[1] = position[1];
        this.position[2] = position[2];
    }

    public void generateBuilding(int gridX, int gridY){
        int[][] grid = new int[gridX][gridY];
        Random r = new Random();
        int[] center = {gridX/2,gridY/2};
    }
    public void addModel(Model model){
        models.add(model);
    }
}
