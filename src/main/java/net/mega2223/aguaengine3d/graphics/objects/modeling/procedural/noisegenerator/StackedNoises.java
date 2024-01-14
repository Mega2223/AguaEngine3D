package net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StackedNoises extends TransformableNoise {

    ArrayList<Noise> noises = new ArrayList<>();
    public StackedNoises(){

    }
    @Override
    public float get(float x, float z) {
        float v = 0;
        for (int i = 0; i < noises.size(); i++) {
            v+=noises.get(i).get(x,z);
        }
        return super.applyTransformations(v);
    }

    public void add(Noise noise){
        noises.add(noise);
    }

    public List<Noise> getNoises() {
        return Collections.unmodifiableList(noises);
    }
}
