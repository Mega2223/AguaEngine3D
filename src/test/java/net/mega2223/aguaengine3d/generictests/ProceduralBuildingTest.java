package net.mega2223.aguaengine3d.generictests;

import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator.ProceduralBuilding;
import net.mega2223.aguaengine3d.misc.Utils;

public class ProceduralBuildingTest {

    public static void main(String[] args) {

        String dir = Utils.PROCEDURAL_BUILDINGS_DIR+"\\BrickStyle1";

        ProceduralBuilding build = new ProceduralBuilding(Utils.readFile(dir));

        System.out.println(build.getBias());
        System.out.println();
    }

}
