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

    class ProceduralBuildingFloor{
        String name;
        float bias;
        int height;

        //builds object according to data
        ProceduralBuildingFloor(String[] data){
            for (int i = 0; i < data.length; i++) {
                String[] cmd = data[i].split("=");
                switch (cmd[0]){
                    case ("name"):
                        name = cmd[1];
                        continue;
                    case ("bias"):
                        bias = Float.parseFloat(cmd[1]);
                        continue;
                    case ("height"):
                        height = Integer.parseInt(cmd[1]);
                }
            }
        }
    }

    class ProceduralBuildingBlock{
        public static final int NORTH = 0, SOUTH = 1, EAST = 2, WEST = 3;
        float[][] walls = new float[4][];
        float[][] textureCoordinates = new float[4][];
        int[][] indices = new int[4][];



        ProceduralBuildingBlock(String[] data){

            int currentFace = -1;
            for (int i = 0; i < data.length; i++) {
                switch (data[i]){
                    case "wallNorth:":
                        currentFace = NORTH;
                        continue;
                    case "wallSouth:":
                        currentFace = SOUTH;
                        continue;
                    case "wallEast:":
                        currentFace = EAST;
                        continue;
                    case "wallWest:":
                        currentFace = WEST;
                        continue;
                    default:
                        String[] arg = data[i].split("=");

                }
            }
        }

        Model build(boolean buildNorth, boolean buildSouth, boolean buildEast, boolean buildWest){

        }
    }
}
