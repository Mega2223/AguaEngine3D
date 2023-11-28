package net.mega2223.aguaengine3d.generictests;

import net.mega2223.aguaengine3d.mathematics.MathUtils;

public class ObjectWeightSelectionTest {

    public static void main(String[] args) {
        String[] prob = {"50","25","10","15"};
        float[] probs = {.5f,.25f,.1f,.15f};

        int[] count = new int[4];

        for (int i = 0; i < 10000; i++) {
            Object selected = MathUtils.doWeightedSelection(prob, probs);
            if(selected.equals("50")){count[0]++;}
            if(selected.equals("25")){count[1]++;}
            if(selected.equals("10")){count[2]++;}
            if(selected.equals("15")){count[3]++;}
            System.out.println(selected);
        }
        System.out.println("Stats:\n");
        for (int i = 0; i < prob.length; i++) {
            System.out.println(probs[i] + ": " + count[i]);
        }
    }

}
