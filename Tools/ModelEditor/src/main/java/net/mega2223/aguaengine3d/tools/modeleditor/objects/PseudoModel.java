package net.mega2223.aguaengine3d.tools.modeleditor.objects;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.modeling.TexturedModel;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;

import java.util.ArrayList;

public class PseudoModel implements Renderable {

    private static int modelCount = 0;

    protected final String name; //setname?

    public ArrayList<Float> vertices = new ArrayList<>();
    public ArrayList<Integer> indices = new ArrayList<>();
    public ArrayList<Float> textureCoords = new ArrayList<>();
    protected boolean isVisible = true;

    private final TexturedModel actualModel = new TexturedModel(new float[0], new int[0], new float[0],-1);

    public PseudoModel(){
        modelCount++;
        name = "Model #" + modelCount;
    }

    public void addVertex(float x, float y, float z){
        vertices.add(x); vertices.add(y); vertices.add(z); vertices.add(0F);
        refresh();
    }

    public void addTriangle(int i1, int i2, int i3){
        indices.add(i1); indices.add(i2); indices.add(i3);
        refresh();
    }

    public void addTextureCoords(float tx, float ty){
        textureCoords.add(tx); textureCoords.add(ty);
        refresh();
    }

    public float[] getVertexAt(int index){
        int i = index / 4;
        return new float[]{
                vertices.get(i),vertices.get(i+1),vertices.get(i+2)
        };
    }

    public float[] getVertexTextureCoords(int index){
        int i = index / 2;
        return new float[]{
                textureCoords.get(i),textureCoords.get(i+1),textureCoords.get(i+2)
        };
    }

    public int[] getTriangle(int index){
        int i = index / 3;
        return new int[]{
                indices.get(i),indices.get(i+1),indices.get(i+2)
        };
    }

    public void refresh(){
        float[] vertexArray = new float[vertices.size()];
        for (int i = 0; i < vertexArray.length; i++) {vertexArray[i] = vertices.get(i);}

        float[] textureCoordArray = new float[textureCoords.size()];
        for (int i = 0; i < textureCoordArray.length; i++) {textureCoordArray[i] = textureCoords.get(i);}

        int[] indexArray = new int[indices.size()];
        for (int i = 0; i < indexArray.length; i++) {indexArray[i] = indices.get(i);}

        actualModel.setVertices(vertexArray);
        actualModel.setIndices(indexArray);
        actualModel.setTextureShift(textureCoordArray);
    }

    @Override
    public String toString() {
        return name;
    }


    @Override
    public void draw() {
        actualModel.draw();
    }

    @Override
    public void drawForceShader(ShaderProgram shader) {
        actualModel.drawForceShader(shader);
    }

    @Override
    public void doLogic(int iteration) {
        actualModel.doLogic(iteration);
    }

    @Override
    public void setUniforms(int iteration, float[] projectionMatrix) {
        actualModel.setUniforms(iteration,projectionMatrix);
    }

    @Override
    public ShaderProgram getShader() {
        return actualModel.getShader();
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
