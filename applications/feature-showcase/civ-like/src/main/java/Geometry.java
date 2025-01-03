import net.mega2223.aguaengine3d.graphics.objects.misc.Line;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Mesh;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ModelUtils;
import net.mega2223.aguaengine3d.graphics.objects.shadering.NormalDebugShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.shadering.SolidColorShaderProgram;
import net.mega2223.aguaengine3d.mathematics.Transform;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.misc.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Geometry {

    public static final float DEG_60 = (float) Math.toRadians(60);
    public static final float SQRT_3 = (float) Math.sqrt(3);

    private Geometry(){}
    private static final float[][] bufferMat = new float[4][16];

    public static Mesh genPolygon(int edges, float radius){
        float[] vertices = new float[4 + edges * 4];
        for (int i = 0; i < edges * 4; i+=4) {
            double angle = 0.5F * Math.PI * (((float) i) / edges);
            vertices[i] = (float) (radius * Math.sin(angle));
            vertices[i+1] = (float) (radius * Math.cos(angle));
        }
        int[] indices = new int[edges * 3];

        int c = 0;
        for (int i = 0; i < indices.length; i+=3) {
            indices[i] = (vertices.length / 4) - 1;
            indices[i + 1] = c % edges;
            indices[i + 2] = (c+1) % edges;
            c++;
        }
        return new Mesh(vertices,indices);
    }

    private static final float[][] bufferPoints = new float[2][4];
    private static final float COS_60 = (float) Math.cos(Math.toRadians(60));
    private static final float SIN_60 = (float) Math.sin(Math.toRadians(60));

    static boolean temp = false;
    public static Model genPolyhedron(int m, int n){
        ShaderProgram shader = new NormalDebugShaderProgram();

        List<Model> models = new ArrayList<>();
        float radius = Math.max(m,n), scale = 1F/radius, bound = (m + n) * SQRT_3;

        List<Float> planeSample = sampleHexagonPlane(bound, bound);
        for (int i = 0; i < planeSample.size(); i++) { planeSample.set(i,planeSample.get(i)/SQRT_3);}
        planeSample = filter(planeSample,m,n);

//        planeSample.removeAll(planeSample);
//        for (int i = 0; i < 10; i++) {
//            planeSample.add(0F);
//            planeSample.add(0F);
//            planeSample.add((float) i);
//            planeSample.add(0F);
//
//            planeSample.add(0F);
//            planeSample.add((float) i);
//            planeSample.add(0F);
//            planeSample.add(0F);
//        }

        final float[] p1 = new float[4];
        final float[] p2 = { m + n*COS_60 , n*SIN_60 , 0,0};
        float sideLen = VectorTranslator.getMagnitude(p2);

        float[] triangleCenter = {-p2[1],p2[0],0,0};
        VectorTranslator.scaleVector(triangleCenter,SQRT_3/2F);
        VectorTranslator.addToVector(p2[0]/2F,p2[1]/2F,0,triangleCenter);
        VectorTranslator.addToVector(triangleCenter,p2);
        VectorTranslator.scaleVector(triangleCenter,1F/3F);

        float[] xPositive = {1,0,0,0};
        float[] yPositive = {0,1,0,0};
        float[] zPositive = {0,0,1,0};

        float[] axis = new float[4];
        VectorTranslator.getAxisAngle(p2,xPositive,axis);

        Transform planeNormalization = new Transform() {
            private final float[] buffer = new float[4];
            @Override
            public void transform(float x, float y, float z, float[] dest) {
                buffer[0] = x; buffer[1] = y; buffer[2] = z;
                VectorTranslator.subtractFromVector(buffer,triangleCenter);
                VectorTranslator.rotateAlongAxis(buffer,axis,dest);
                VectorTranslator.scaleVector(dest,1F/sideLen);
            }

            @Override
            public void reverse(float x, float y, float z, float[] dest) {}
        };

        float[] buffer = new float[4];

        for (int i = 0; i < planeSample.size(); i+=4) {
            float x = planeSample.get(i), y = planeSample.get(i + 1), z = planeSample.get(i + 2);
            planeNormalization.transform(x,y,z,buffer);
            for (int j = 0; j < 3; j++) {planeSample.set(i+j,(buffer[j]));}
        }
        planeNormalization.transform(p1,p1);
        planeNormalization.transform(p2,p2);

        List<Float> finalSample = new ArrayList<>(planeSample.size() * 20);

        Mesh icosahedron = Mesh.ICOSAHEDRON;

        int[] indices = icosahedron.getIndices();
        float[] vertices = icosahedron.getVertices();
        int indexC = indices.length;

        float[] normalRotationAxis = new float[4];
        float[] planeVertex = new float[4];
        float[] rotated = new float[4];
        float[] trCenter = new float[4];
        float[] trNormal = new float[4];
        float[] p2Trans = new float[4], p1Trans = new float[4];

        Model icosaModel = icosahedron.toModel(shader);
        models.add(icosaModel);

        for (int i = 0; i < indexC; i+=3) {
            int a = indices[i], b = indices[i+1], c = indices[i+2];
            final float aX = vertices[a*4], aY = vertices[a*4+1], aZ = vertices[a*4+2];
            final float bX = vertices[b*4], bY = vertices[b*4+1], bZ = vertices[b*4+2];
            final float cX = vertices[c*4], cY = vertices[c*4+1], cZ = vertices[c*4+2];

            final float v1x = aX - cX, v1y = aY - cY, v1z = aZ - cZ;
            final float v2x = cX - bX, v2y = cY - bY, v2z = cZ - bZ;
            final float v3x = bX - aX, v3y = bY - aY, v3z = bZ - aZ;

            trCenter[0] = (aX + bX + cX)/3F; trCenter[1] = (aY + bY + cY)/3F; trCenter[2] = (aZ + bZ + cZ)/3F;

            float[][] directions = {
                    {-trCenter[0]+aX,-trCenter[1]+aY,-trCenter[2]+aZ,0},
                    {-trCenter[0]+bX,-trCenter[1]+bY,-trCenter[2]+bZ,0},
                    {-trCenter[0]+cX,-trCenter[1]+cY,-trCenter[2]+cZ,0}
            };

            for (int j = 0; j < 3; j++) {
                Line dir = new Line(0,0,0);
                dir.setStart(trCenter);
                dir.setDirection(directions[j]);
                Civ.context.addObject(dir);
            }

//            VectorTranslator.getCrossProduct(v1x, v1y, v1z, v2x, v2y, v2z, faceNormal);
//            VectorTranslator.normalize(faceNormal);
//
//                if(VectorTranslator.getAngleBetweenVectors(faceNormal,center) > .001F){
//                    System.out.println("DESTROY!");
//                    VectorTranslator.flipVector(faceNormal);
//                }
            //fixme isso não funciona com o produto vetorial
            // MESMO QUANDO eu verifico o ângulo entre o normal e o centro do triângulo
            System.arraycopy(trCenter,0,trNormal,0,3);
            VectorTranslator.normalize(trNormal);

            Transform normalAlign = new Transform() {
                final float[] localBuffer = new float[4];
                public void transform(float x, float y, float z, float[] dest) {
                    localBuffer[0] = x; localBuffer[1] = y; localBuffer[2] = z;
                    VectorTranslator.rotateAlongAxis(localBuffer,normalRotationAxis, dest);
                    VectorTranslator.addToVector(dest,trCenter);
                }
                public void reverse(float x, float y, float z, float[] dest) {

                }
            };

            VectorTranslator.getAxisAngle(trNormal,zPositive,normalRotationAxis);
            VectorTranslator.flipVector(normalRotationAxis);

            for (int j = 0; j < planeSample.size(); j+=4) {
                planeVertex[0] = planeSample.get(j); planeVertex[1] = planeSample.get(j+1);
                planeVertex[2] = planeSample.get(j+2); planeVertex[3] = 0;

                float[] current = new float[4];
                float[] planeBoundB = p1.clone(), nPlaneBoundB = new float[4],nPlaneBoundB2 = new float[4];
                float[] planeBoundF = p2.clone(), nPlaneBoundF = new float[4],nPlaneBoundF2 = new float[4];
                float[] planeBoundV = new float[4];

                normalAlign.transform(planeBoundF,planeBoundF);
                normalAlign.transform(planeBoundB,planeBoundB);
                normalAlign.transform(planeVertex,current);
                VectorTranslator.subtractFromVector(planeBoundB,planeBoundF,planeBoundV);

                float[] triangleAlignAxis = new float[4];
                VectorTranslator.getAxisAngle(
                        planeBoundV[0],planeBoundV[1],planeBoundV[2],v1x,v1y,v1z,
                        triangleAlignAxis);

                Transform triangleCorrection = new Transform() {
                    @Override
                    public void transform(float x, float y, float z, float[] dest) {
                        buffer[0] = x; buffer[1] = y; buffer[2] = z; buffer[3] = 0;
                        VectorTranslator.rotateAlongAxis(buffer,triangleAlignAxis,dest);
                    }

                    public void reverse(float x, float y, float z, float[] dest) {}
                };
                triangleCorrection.transform(planeBoundF,nPlaneBoundF);
                triangleCorrection.transform(planeBoundB,nPlaneBoundB);

                VectorTranslator.flipVector(triangleAlignAxis);
                triangleCorrection.transform(planeBoundF,nPlaneBoundF2);
                triangleCorrection.transform(planeBoundB,nPlaneBoundB2);
                VectorTranslator.flipVector(triangleAlignAxis);

                float[] nYpositive = new float[4];
                normalAlign.transform(yPositive,nYpositive);
                triangleCorrection.transform(nYpositive);
                VectorTranslator.subtractFromVector(nYpositive,trCenter);

                boolean isRightAngle = false;
                for (int k = 0; k < 3; k++) {
                    isRightAngle |= VectorTranslator.getAngleBetweenVectors(directions[k], nYpositive) <= Math.toRadians(2);
                }
                triangleCorrection.transform(current);
                if(!isRightAngle){
                    Model WRONG = Mesh.CUBE.toModel(new SolidColorShaderProgram(1,.1F,.1F));
                    ModelUtils.scaleModel(WRONG,.1F);
                    WRONG.setCoords(2.5F*trNormal[0],2.5F*trNormal[1],2.5F*trNormal[2]);
                    Civ.context.addObject(WRONG);
//                    System.out.println("AAAAAAAAAAGSDG");
                    float[] normal2 = trNormal.clone();
                    VectorTranslator.normalize(normal2);
                    VectorTranslator.scaleVector(normal2, (float) Math.PI);
                    VectorTranslator.rotateAlongAxis(current.clone(),normal2,current);
                }

                for (int k = 0; k < 4; k++) { finalSample.add(current[k]); }

                Line normal = new Line(1, 1, 0);
                normal.setStart(trCenter); normal.setDirection(trNormal);
                Civ.context.addObject(normal);

                Line initialBound = new Line(0, 1, 1);
                initialBound.setStart(planeBoundB); initialBound.setEnd(planeBoundF);
                Civ.context.addObject(initialBound);

                Line finalBound = new Line(.7F, .5F, .2F);
                finalBound.setStart(nPlaneBoundB); finalBound.setEnd(nPlaneBoundF);
                Civ.context.addObject(finalBound);

                Line final2 = new Line(.7F,.5F,.35F);
                final2.setStart(nPlaneBoundB2); final2.setEnd(nPlaneBoundF2);
                Civ.context.addObject(final2);

                Line yPosit = new Line(1,1,1);
                yPosit.setStart(trCenter); yPosit.setDirection(nYpositive);
                Civ.context.addObject(yPosit);
            }
        }

        for (int i = 0; i < planeSample.size(); i+=4) {
            for (int j = 0; j < 3; j++) {
                finalSample.add(planeSample.get(i+j));
            }
            finalSample.add(0f);
        }

        Model points = ModelUtils.plotPoints(
                Utils.toPrimitiveArray(finalSample)
                , .025F
        );
        models.add(new Model(
                points.getRelativeVertices(), points.getIndices(), shader
        ));

        Model[] ret = new Model[models.size()];
        ret = models.toArray(ret);
        return ModelUtils.mergeModels(ret);
    }

    static List<Float> filter (List<Float> vertices, float m, float n){
        List<Float> ret = new ArrayList<>();
        Arrays.fill(bufferPoints[0],0);
        VectorTranslator.addToVector(m,0,0,bufferPoints[0]);
        VectorTranslator.addToVector(COS_60*n,SIN_60*n,0,bufferPoints[0]);

        float[] p2 = { m + n*COS_60 , n*SIN_60 , 0,0};
        float sideLen = VectorTranslator.getMagnitude(p2);

        float[] p3 = {-p2[1],p2[0],0,0};
        VectorTranslator.scaleVector(p3,SQRT_3/2F);
        VectorTranslator.addToVector(p2[0]/2F,p2[1]/2F,0,p3);

        float[] vN = p3.clone();
        VectorTranslator.subtractFromVector(vN,p2);

        for (int i = 0; i < vertices.size(); i+=4) {
            float x = vertices.get(i), y = vertices.get(i+1), z = vertices.get(i+2);
            float ratio = y / x;
            // eu sou o maior matemático de todos os tempos
            boolean a = ratio <= p3[1] / p3[0];
            boolean b = ratio >= p2[1] / p2[0];
            boolean c = (y - p2[1]) / (x - p2[0]) >= vN[1] / vN[0];
            boolean d = x < p2[0];
            boolean valid = a && b && c && d && x >= 0 && y >= 0;
            if(!valid){continue;}
            ret.add(x); ret.add(y); ret.add(z); ret.add(0f);
        }
//
//        for (int i = 0; i < ret.size(); i+=4) {
//            System.out.printf(Locale.US,"(%.3f,%.3f),",ret.get(i),ret.get(i+1));
//        }
        System.out.println();
        return ret;
    }

    static List<Float> sampleHexagonPlane(float maxX, float maxY){
        List<Float> ret = new ArrayList<>();
        boolean even = true;
        for (int r = 0;; r++){
            float y = r * .5F + ((r + 1) >> 1) *.5F - .5F;
            if(y > maxY){break;}
            for (int c = 0;; c++){
                boolean odd = (r % 4) / 2 == 1;
                float x = c * SQRT_3 + (odd ? 0 : SQRT_3 / 2F);
                if(x > maxX){break;}
                ret.add(x); ret.add(y); ret.add(0F); ret.add(0F);
            }
        }
        return ret;
    }
}
