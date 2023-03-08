package net.mega2223.lwjgltest.aguaengine3d.misc.proceduralbuildinggenerator;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;

import java.io.File;

public class ProceduralBuildingTemplate {

    final String program;



    ProceduralBuildingTemplate(String dir){
        program = Utils.readFile(dir);
    }

    public void generate(int[][] pattern){

    }

    

    class ProceduralBuildingBlock{
        
        
    }
}
