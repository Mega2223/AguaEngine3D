package net.mega2223.aguaengine3d.graphics.objects.modeling;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;

public class Mesh implements Renderable {
    /** The Mesh class is intended to be used as a way to store model templates,
     * it's behavior does not render the mesh*/

    private static final float PHI = (float)(1 + Math.sqrt(5)) / 2;

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

    public float[] getVertices() {return vertices.clone();}
    public int[] getIndices() {return indices.clone();}
    public float[] getTextureCoords() {return textureCoords.clone();}

    @Override
    public void draw() {}

    @Override
    public void drawForceShader(ShaderProgram shader) {}

    @Override
    public void doLogic(int iteration) {}

    @Override
    public void setUniforms(int iteration, float[] projectionMatrix) {}

    @Override
    public ShaderProgram getShader() {return null;}

    /**Regular cube with side length of 2 and vertex radius of 1*/
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

    /**Regular Icosahedron with side length of 2*/
    public static final Mesh ICOSAHEDRON = new Mesh(
            new float[]{
                    0, 1, PHI, 0,
                    0, -1, PHI, 0,
                    0, 1, -PHI, 0,
                    0, -1, -PHI, 0,

                    1, PHI, 0, 0,
                    -1, PHI, 0, 0,
                    1, -PHI, 0, 0,
                    -1, -PHI, 0, 0,

                    PHI, 0, 1, 0,
                    -PHI, 0, 1, 0,
                    PHI, 0, -1, 0,
                    -PHI, 0, -1, 0
            },
            new int[]{
                    0,1,8,0,1,9,0,4,5,0,4,8,0,5,9,1,6,7,1,6,8,1,7,9,2,3,10,2,3,11,2,4,5,2,4,10,2,5,11,3,6,7,3,6,10,3,7,11,4,8,10,5,9,11,6,8,10,7,9,11
            }
    );
    /*static {
        //such efficiency
        List<Integer> indices = new ArrayList<>();
        float[] vertices = ICOSAHEDRON.vertices;
        for (int i = 0; i < vertices.length; i+=4){
            float x1 = vertices[i], y1 = vertices[i+1], z1 = vertices[i+2];
            for (int j = 0; j < vertices.length; j+=4) {
                if(i==j){continue;}
                float x2 = vertices[j], y2 = vertices[j+1], z2 = vertices[j+2];
                for (int k = 0; k < vertices.length; k+=4) {
                    float x3 = vertices[k], y3 = vertices[k+1], z3 = vertices[k+2];
                    if(i == k || j == k){continue;}
                    float dA = VectorTranslator.getDistance(x1,y1,z1,x2,y2,z2);
                    float dB = VectorTranslator.getDistance(x2,y2,z2,x3,y3,z3);
                    float dC = VectorTranslator.getDistance(x3,y3,z3,x1,y1,z1);
                    boolean alreadyHas = ModelUtils.alreadyHasTriangle(indices,i/4,j/4,k/4);
                    if(dA < 2.1F && dB < 2.1F && dC < 2.1F && !alreadyHas){
                        indices.add(i/4);indices.add(j/4);indices.add(k/4);
//                        System.out.printf(Locale.US,"%d,%d,%d\n",i/4,j/4,k/4);
                    }
                }
            }
        }
        ICOSAHEDRON.indices = Utils.toPrimitiveIndexArray(indices);
        VectorTranslator.debugVector(ICOSAHEDRON.indices);
        System.out.println(ICOSAHEDRON.indices.length /3);
    }*/

}
