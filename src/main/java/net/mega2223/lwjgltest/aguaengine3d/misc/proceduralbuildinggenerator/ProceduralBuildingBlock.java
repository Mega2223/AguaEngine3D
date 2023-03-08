package net.mega2223.lwjgltest.aguaengine3d.misc.proceduralbuildinggenerator;

public class ProceduralBuildingBlock {
    public static final int NORTH = 0, SOUTH = 1, EAST = 2, WEST = 3;
    float[][] walls = new float[4][];
    float[][] textureCoordinates = new float[4][];
    int[][] indices = new int[4][];
    boolean[] forceRender = new boolean[4];
    String[][] compartibles = new String[4][];

    float bias = -1;
    String name = null;

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
                    String[] cmd = data[i].split("=");
                    switch (cmd[0]){
                        case "name":
                            name = cmd[1];
                        case "bias":
                            bias = Float.parseFloat(cmd[1]);
                            continue;

                        case "compartibleNorth":
                            compartibles[NORTH] = cmd[1].split(",");
                            continue;
                        case "compartibleSouth":
                            compartibles[SOUTH] = cmd[1].split(",");
                            continue;
                        case "compartibleWest":
                            compartibles[WEST] = cmd[1].split(",");
                            continue;
                        case "compartibleEast":
                            compartibles[EAST] = cmd[1].split(",");
                            continue;
                            //Wall specific variables:
                        case "forceRender":
                            forceRender[currentFace] = Boolean.parseBoolean(cmd[1]);
                            continue;
                        case "vertices":
                            String[] split = cmd[1].split(",");
                            walls[currentFace] = new float[split.length];
                            for(int j = 0; j < split.length; j++){walls[currentFace][j] = Float.parseFloat(split[j]);}
                            continue;
                        case "indices:":
                            String[] split2 = cmd[1].split(",");
                            indices[currentFace] = new int[split2.length];
                            for(int j = 0; j < split2.length; j++){indices[currentFace][j] = Integer.parseInt(split2[j]);}
                            continue;
                        case "textureCoords:":
                            String[] split3 = cmd[1].split(","); //why does the interpreter hate me
                            textureCoordinates[currentFace] = new float[split3.length];
                            for(int j = 0; j < split3.length; j++){textureCoordinates[currentFace][j] = Float.parseFloat(split3[j]);}
                            continue;
                    }
            }
        }
    }

}
