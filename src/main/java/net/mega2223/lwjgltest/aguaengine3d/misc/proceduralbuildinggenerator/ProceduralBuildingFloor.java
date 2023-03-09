package net.mega2223.lwjgltest.aguaengine3d.misc.proceduralbuildinggenerator;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.TexturedModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProceduralBuildingFloor implements ProceduralBuildingObject{

    String name = null;
    String[] possibleBlocks = null;
    String[] validAbove = null;
    float bias = -1;
    int height = 1;

    //builds object according to data
    public ProceduralBuildingFloor(String[] data){
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
                    continue;
                case("blocks"):
                    possibleBlocks = cmd[1].split(",");
                    continue;
                case("validAbove"):
                    validAbove = cmd[1].split(",");
                    continue;
            }
        }
    }

    public TexturedModel generate(int[][] map, int height, ProceduralBuilding context){
        List<TexturedModel> models = new ArrayList<>();
        Random r = new Random();

        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                if(map[x][y] >= 0){
                    boolean north = false, south = false, east = false, west = false;

                }
            }
        }
    }

    public List<ProceduralBuildingBlock> usableBlocks(){
        List<ProceduralBuildingBlock> avail = new ArrayList<>();
        for (String act : possibleBlocks){

        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getBias() {
        return bias;
    }

}
