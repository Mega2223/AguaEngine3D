package net.mega2223.lwjgltest.aguaengine3d.logic.objects;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.lwjgltest.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.lwjgltest.aguaengine3d.mathematics.VectorTranslator;

import java.util.List;

public interface RenderableObject {
    void doLogic(int itneration);
    default void drawnModels(int itnerarion, float[] projectionMatrix){
        float[] translationMatrix = new float[16];
        MatrixTranslator.generateTranslationMatrix(translationMatrix,getPosition());
        for (Model act : getModels()){
            act.getShader().setUniforms(itnerarion,translationMatrix,projectionMatrix);
            act.drawnVAO();
        }
    }
    List<Model> getModels();
    float[] getPosition();
    void setPosition(float[] position);


    default float[] getTranslatedModel(int index){
        Model model = getModels().get(index);
        float[] toTrans = model.getRelativeVertices();
        VectorTranslator.addToAllVectors(toTrans,getPosition());
        return toTrans;
    }



}
