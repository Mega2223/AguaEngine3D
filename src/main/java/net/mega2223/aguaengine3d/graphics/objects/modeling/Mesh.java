package net.mega2223.aguaengine3d.graphics.objects.modeling;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;

public class Mesh implements Renderable {
    /** The Mesh class is intended to be used as a way to store model templates,
     * it's behavior does not render the mesh*/

    float[] vertices; int[] indices; float[] textureCoords;

    public Mesh(float[] vertices, int[] indices){
        this(vertices,indices,new float[vertices.length/2]);
    }

    public Mesh(float[] vertices, int[] indices, float[] textureCoords){
        this.vertices = vertices;
        this.indices = indices;
        this.textureCoords = textureCoords;
    }

    public Model toModel(ShaderProgram shader){
        return new Model(vertices,indices,shader);
    }

    public TexturedModel toTexturedModel(ShaderProgram shaderProgram, int texture){
        return new TexturedModel(vertices,indices,textureCoords,shaderProgram,texture);
    }

    public TexturedModel toTexturedModel(int texture){
        return new TexturedModel(vertices,indices,textureCoords,texture);
    }

    @Override
    public void draw() {

    }

    @Override
    public void drawForceShader(ShaderProgram shader) {

    }

    @Override
    public void doLogic(int iteration) {

    }

    @Override
    public void setUniforms(int iteration, float[] projectionMatrix) {

    }

    @Override
    public ShaderProgram getShader() {
        return null;
    }

    public static Mesh CUBE = new Mesh(
            new float[]{
                    1.0F, 1.0F, 1.0F, 0.0F,
                    -1.0F, 1.0F, -1.0F, 0.0F,
                    -1.0F, 1.0F, 1.0F, 0.0F,
                    1.0F, -1.0F, -1.0F, 0.0F,
                    -1.0F, -1.0F, -1.0F, 0.0F,
                    1.0F, 1.0F, -1.0F, 0.0F,
                    1.0F, -1.0F, 1.0F, 0.0F,
                    1.0F, -1.0F, -1.0F, 0.0F,
                    -1.0F, -1.0F, 1.0F, 0.0F,
                    1.0F, -1.0F, -1.0F, 0.0F,
                    1.0F, -1.0F, 1.0F, 0.0F,
                    1.0F, 1.0F, 1.0F, 0.0F,
                    1.0F, 1.0F, -1.0F, 0.0F,
                    1.0F, 1.0F, -1.0F, 0.0F
            },
            new int[]{
                    0, 1, 2,  1, 3, 4,  5, 6, 7,
                    8, 9, 10,  2, 4, 8,  11, 8, 6,
                    0, 12, 1,  1, 13, 3,  5, 11, 6,
                    8, 4, 9,  2, 1, 4,  11, 2, 8
            }
    );
}
