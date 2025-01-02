import net.mega2223.aguaengine3d.graphics.objects.misc.Line;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Mesh;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ModelUtils;
import net.mega2223.aguaengine3d.graphics.objects.shadering.NormalDebugShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
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

    public static Model genPolyhedron(int m, int n){
        ShaderProgram shader = new NormalDebugShaderProgram();

        List<Model> models = new ArrayList<>();
        float radius = Math.max(m,n), scale = 1F/radius, bound = (m + n) * SQRT_3;

        List<Float> planeSample = sampleHexagonPlane(bound, bound);
        for (int i = 0; i < planeSample.size(); i++) { planeSample.set(i,planeSample.get(i)/SQRT_3);}
        planeSample = filter(planeSample,m,n);

        float[] p1 = new float[4];
        float[] p2 = { m + n*COS_60 , n*SIN_60 , 0,0};
        float sideLen = VectorTranslator.getMagnitude(p2);

        float[] triangleCenter = {-p2[1],p2[0],0,0};
        VectorTranslator.scaleVector(triangleCenter,SQRT_3/2F);
        VectorTranslator.addToVector(p2[0]/2F,p2[1]/2F,0,triangleCenter);
        VectorTranslator.addToVector(triangleCenter,p2);
        VectorTranslator.scaleVector(triangleCenter,1F/3F);

        float[] right = {1,0,0,0};
        float[] axis = new float[4];
        VectorTranslator.getAxisAngle(p2,right,axis);

        float[] buffer = new float[4];

        for (int i = 0; i < planeSample.size(); i+=4) {
            float[] current = {planeSample.get(i),planeSample.get(i+1),planeSample.get(i+2),0};
            VectorTranslator.subtractFromVector(current,triangleCenter);
            VectorTranslator.rotateAlongAxis(current,axis,buffer);
            System.arraycopy(buffer,0,current,0,3);
            for (int j = 0; j < 3; j++) {planeSample.set(i+j,(.2F*current[j])/sideLen);}
            //todo eu tô fazendo essa transformação 3 VEZES, não é melhor tacar tudo numa matriz?
        }

        VectorTranslator.subtractFromVector(p2,triangleCenter);
        VectorTranslator.subtractFromVector(p1,triangleCenter);

        VectorTranslator.scaleVector(p2,1F/sideLen);
        VectorTranslator.scaleVector(p1,1F/sideLen);

        List<Float> finalSample = new ArrayList<>(planeSample.size() * 20);

        Mesh icosahedron = Mesh.ICOSAHEDRON;

        int[] indices = icosahedron.getIndices();
        float[] vertices = icosahedron.getVertices();
        int trC = indices.length;

        float[] zPositive = {0,0,1,0};
        float[] normalRotAxis = new float[4];
        float[] currentSample = new float[4];
        float[] rotated = new float[4];
        float[] center = new float[4];
        float[] normal = new float[4];
        float[] p2Trans = new float[4], p1Trans = new float[4];

        Model icosaModel = icosahedron.toModel(shader);
        models.add(icosaModel);

        for (int i = 0; i < trC; i+=3) {
            int a = indices[i], b = indices[i+1], c = indices[i+2];
            float aX = vertices[a*4], aY = vertices[a*4+1], aZ = vertices[a*4+2];
            float bX = vertices[b*4], bY = vertices[b*4+1], bZ = vertices[b*4+2];
            float cX = vertices[c*4], cY = vertices[c*4+1], cZ = vertices[c*4+2];
            center[0] = (aX + bX + cX)/3; center[1] = (aY + bY + cY)/3; center[2] = (aZ + bZ + cZ)/3;

            final float v1x = aX - cX, v1y = aY - cY, v1z = aZ - cZ;
            final float v2x = bX - cX, v2y = bY - cY, v2z = bZ - cZ;
            final float v3x = aX - bX, v3y = aY - bY, v3z = aZ - bZ;

            VectorTranslator.getCrossProduct(v1x, v1y, v1z, v2x, v2y, v2z,normal);
            VectorTranslator.normalize(normal);
            if(VectorTranslator.getAngleBetweenVectors(normal,center) > Math.PI){
                VectorTranslator.flipVector(normal);
            }
            VectorTranslator.getAxisAngle(normal,zPositive,normalRotAxis);
            VectorTranslator.flipVector(normalRotAxis);

            for (int j = 0; j < planeSample.size(); j+=4) {
                currentSample[0] = planeSample.get(j); currentSample[1] = planeSample.get(j+1);
                currentSample[2] = planeSample.get(j+2); currentSample[3] = 0;

                float[] current = new float[4];
                float[] currentY = {0,1,0,0};

                VectorTranslator.rotateAlongAxis(currentSample,normalRotAxis,rotated);
                VectorTranslator.rotateAlongAxis(currentY.clone(),normalRotAxis,currentY);

                VectorTranslator.rotateAlongAxis(p2,normalRotAxis,p2Trans);
                VectorTranslator.addToVector(p2Trans,center);
                VectorTranslator.rotateAlongAxis(p1,normalRotAxis,p1Trans);
                VectorTranslator.addToVector(p1Trans,center);

                Line line = new Line(1, 0, 0);
                line.setEnd(p2Trans); line.setStart(p1Trans);
                Civ.context.addObject(line);

                line = new Line(1, 1, 0);
                line.setEnd(aX,aY,aZ); line.setStart(bX,bY,bZ);
                Civ.context.addObject(line);

                VectorTranslator.addToVector(current,rotated);
                VectorTranslator.addToVector(current,center);

//                VectorTranslator.addToVector(currentZ,rotated);
//                VectorTranslator.addToVector(currentZ,center);

                float[] triangleAxis = new float[4];
                float p2x = p2Trans[0] - p1Trans[0], p2y = p2Trans[1] - p1Trans[1], p2z = p2Trans[2] - p1Trans[2];
                VectorTranslator.getAxisAngle(p2x, p2y, p2z,v3x,v3y,v3z,triangleAxis);

                VectorTranslator.rotateAlongAxis(current.clone(),triangleAxis,current);

                VectorTranslator.rotateAlongAxis(currentY.clone(),triangleAxis,currentY);
                Line up = new Line(0, 0, 1);
                up.setStart(center);
                up.setDirection(currentY);
                Civ.context.addObject(up);

                Line obj = new Line(0,1,1);
                obj.setStart(center);
                obj.setEnd(aX,aY,aZ);
                Civ.context.addObject(obj);
                obj = new Line(0,1,1);
                obj.setStart(center);
                obj.setEnd(bX,bY,bZ);
                Civ.context.addObject(obj);
                obj = new Line(0,1,1);
                obj.setStart(center);
                obj.setEnd(cX,cY,cZ);
                Civ.context.addObject(obj);

                for (int k = 0; k < 4; k++) {
                    finalSample.add(current[k]);
                }
            }
        }

        for (int i = 0; i < planeSample.size(); i+=4) {
            for (int j = 0; j < 3; j++) {
                finalSample.add(planeSample.get(i+j));
            }
            finalSample.add(0f);
//            System.out.printf(Locale.US,"%.3f,%.3f,%.3f\n",
//                    planeSample.get(i),planeSample.get(i+1),planeSample.get(i+2)
//                    );
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
