public class TriDimRotTest {
    static float[]vect = {1,0,0};

    private static double DEG = Math.PI;
    public static void main(String[] args) {
        printVec(vect);
        rX(vect,DEG);
        printVec(vect);
        rY(vect,DEG);
        printVec(vect);
    }

    static void rX(float[] vect, double r){
        double mY = Math.cos(r) - Math.sin(r);
        double mZ = Math.sin(r) + Math.cos(r);
        vect[1]*= mY;
        vect[2]*= mZ;
    }
    static void rY(float[] vect, double r){
        vect[0]*= Math.cos(r) + Math.sin(r);
        vect[2]*= -Math.sin(r) + Math.cos(r);
    }


    private static void printVec(float[] vec){
        System.out.println("("+vec[0]+" "+vec[1]+" "+vec[2]+")");
    }
}
