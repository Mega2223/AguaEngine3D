package net.mega2223.lwjgltest.aguaengine3d.misc.proceduralbuildinggenerator;

public class ProceduralBuildingFloor {

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
