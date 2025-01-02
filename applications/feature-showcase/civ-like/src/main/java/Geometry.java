import net.mega2223.aguaengine3d.graphics.objects.modeling.Mesh;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ModelUtils;
import net.mega2223.aguaengine3d.graphics.objects.shadering.SolidColorShaderProgram;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.misc.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Geometry {

    public static final float DEG_60 = (float) Math.toRadians(60);
    public static final float SQRT_3 = (float) Math.sqrt(3);

    private Geometry(){}
    private static final float[][] bufferMat = new float[4][16];

    public static Model genPolygon(int edges, float radius){
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
        return new Model(vertices,indices,null);
    }

    private static final float[][] bufferPoints = new float[2][4];
    private static final float COS_60 = (float) Math.cos(Math.toRadians(60));
    private static final float SIN_60 = (float) Math.sin(Math.toRadians(60));

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
            boolean d = x <= p2[0];
            boolean valid = a && b && c && d && x >= 0 && y >= 0;
//            valid = x >= 0 && y >= 0 && a && b;
            if(!valid){continue;}
            ret.add(x); ret.add(y);ret.add(z);ret.add(0f);
        }
        return ret;
    }

    public static Model genPolyhedron(int m, int n){
        SolidColorShaderProgram shader = new SolidColorShaderProgram(.5F, 1F, .6F,1F);

        List<Model> models = new ArrayList<>();
        float r = Math.max(m,n), scale = 1F/r;
        float bound = (m + n) * SQRT_3;
        List<Float> planeSample = sampleHexagonPlane(bound, bound);
        for (int i = 0; i < planeSample.size(); i++) { planeSample.set(i,planeSample.get(i)/SQRT_3);}
        planeSample = filter(planeSample,m,n);

        for (int i = 0; i < planeSample.size(); i++) {
            planeSample.set(i,planeSample.get(i)*scale);
        }

//        planeSample.removeAll(planeSample);

//        float rad = .5F;
//        for (double i = - Math.PI; i < Math.PI; i+= Math.PI / 50) {
//            planeSample.add((float) Math.cos(i)*rad); planeSample.add((float) Math.sin(i)*rad);
//            planeSample.add(0f); planeSample.add(0f);
//        }
//
//        for (float i = 0; i < 15; i+=.05F) {
//            planeSample.add(0f); planeSample.add(0f); planeSample.add(i); planeSample.add(0f);
//        }

        List<Float> finalSample = new ArrayList<>(planeSample.size() * 20);

        Mesh icosahedron = Mesh.ICOSAHEDRON; //fixme deus não existe e o ângulo ainda está errado

        int[] indices = icosahedron.getIndices();
        float[] vertices = icosahedron.getVertices();
        int tr = indices.length; final float s = 1F;
        float[] normal = new float[4];
        float[] zPositive = {0,0,1,0};
        float[] rotAxis = new float[4];
        float[] currentSample = new float[4];
        float[] rotated = new float[4];
        float[] center = new float[4];

//        Model icosaModel = icosahedron.toModel(shader);
//        models.add(icosaModel);

        for (int i = 0; i < tr; i+=3) {
            int a = indices[i], b = indices[i+1], c = indices[i+2];
            float aX = vertices[a*4], aY = vertices[a*4+1], aZ = vertices[a*4+2];
            float bX = vertices[b*4], bY = vertices[b*4+1], bZ = vertices[b*4+2];
            float cX = vertices[c*4], cY = vertices[c*4+1], cZ = vertices[c*4+2];

            //TODO era pra ser um ponto equidistante dos outros, mas
            // os triângulos são equiláteros então eu acho que não tem problema???
            // enfim, teste :)
            // me parece ser ok mas teste mesmo assim, tipo literalmente só mede a distância bobalhão
            center[0] = (aX + bX + cX)/3; center[1] = (aY + bY + cY)/3; center[2] = (aZ + bZ + cZ)/3;

            VectorTranslator.getCrossProduct(aX-cX,aY-cY,aZ-cZ,bX-cX,bY-cY,bZ-cZ,normal);
            VectorTranslator.normalize(normal);
            if(VectorTranslator.getAngleBetweenVectors(normal,center) > Math.PI){
                VectorTranslator.flipVector(normal);
            }
            VectorTranslator.getAxisAngle(normal,zPositive,rotAxis);
            VectorTranslator.flipVector(rotAxis);

            for (int j = 0; j < planeSample.size(); j+=4) {
                currentSample[0] = planeSample.get(j); currentSample[1] = planeSample.get(j+1);
                currentSample[2] = planeSample.get(j+2); currentSample[3] = 0;
//
                VectorTranslator.rotateAlongAxis(currentSample,rotAxis,rotated);
                for (int k = 0; k < 3; k++) {
//                    finalSample.add(rotated[k] + center[k]);
//                    finalSample.add(planeSample.get(j+k));
                }
            }
//            System.out.println(i/3);
//            if(i > 4){break;};
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
        VectorTranslator.debugVector(Utils.toPrimitiveArray(finalSample));

        Model[] ret = new Model[models.size()];
        ret = models.toArray(ret);
        return ModelUtils.mergeModels(ret);
    }

    static List<Float> sampleHexagonPlane(float maxX, float maxY){
        List<Float> ret = new ArrayList<>();
        boolean even = true;
        for (int r = 0;; r++){
            float y = r * .5F + ((r + 1) >> 1) *.5F - .5F;
            if(y > maxY){break;}
            for (int c = 0;; c++){
                boolean odd = (r % 4) / 2 == 1;
                float x = c * SQRT_3 + (odd ? 0 : SQRT_3 / 2);
                if(x > maxX){break;}
//                System.out.printf(Locale.US,"(%.3f, %.3f),", x,y);
                ret.add(x); ret.add(y); ret.add(0F); ret.add(0F);
            }
        }
//        float tX = ret.get(0), tY = ret.get(1), cld = Float.MAX_VALUE;
//        final float mx = maxX/2, my = maxY/2;
//        for (int i = 0; i < ret.size(); i+=4) {
//            float x = ret.get(i), y = ret.get(i + 1);
//            float dx = x - mx, dy = y - my;
//            float dist = (float) Math.sqrt(dx*dx + dy*dy);
//            if(dist < cld){
//                cld = dist; tX = x; tY = y;
//            }
//        }
//        for (int i = 0; i < ret.size(); i+=4) {
//            ret.set(i,ret.get(i)-tX);
//            ret.set(i+1,ret.get(i+1)-tY);
//        }

        for (int i = 0; i < ret.size(); i+=4) {
            System.out.printf(Locale.US,"(%.3f,%.3f),",ret.get(i),ret.get(i+1));
        }
        System.out.println();
        return ret;
    }
}
