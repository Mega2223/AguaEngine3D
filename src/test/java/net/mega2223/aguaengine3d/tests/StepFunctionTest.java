package net.mega2223.aguaengine3d.tests;

public class StepFunctionTest {


    public static void main(String[] args) {
        final double toVar = 0;
        final double unit = .2f;
        for(double z = -2; z < 2; z += .1){
            System.out.println(z+": " + getStep(z,toVar,unit));
        }
    }

    static double getStep(double var, double varTo, double unit){
        double difference = var - varTo; //neg se var é menor
        if(difference > unit){return - unit;}
        if(difference < -unit){return unit;}
        return -difference;
    }

}
