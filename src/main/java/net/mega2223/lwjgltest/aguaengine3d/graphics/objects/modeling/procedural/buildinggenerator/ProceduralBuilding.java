package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.ModelUtils;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.TexturedModel;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.TextureManager;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;

import java.util.ArrayList;
import java.util.Random;

public class ProceduralBuilding implements ProceduralBuildingObject {

    public static final String PROCEDURAL_FLOOR_EXTENSION_NAME = ".procf";
    public static final String PROCEDURAL_BLOCK_EXTENSION_NAME = ".procbloc";
    public static final String PROCEDURAL_BUILDING_EXTENSION_NAME = ".procb";


    int minFloors = 1, maxFloors = 20; //todo
    float[] minSize = {1,1};

    String sourceDirectory;
    String name = null;
    float bias = -1;
    int texture = -1;

    ArrayList<ProceduralBuildingBlock> allBlocks = new ArrayList<>();
    ArrayList<ProceduralBuildingFloor> allFloors = new ArrayList<>();

    public ProceduralBuilding(String sourceDir){
        sourceDirectory = sourceDir;
        texture = TextureManager.loadTexture(sourceDir+"\\Texture.png");
        String[] data = Utils.readFile(sourceDir+"\\Main.procb").split("\n");
        //maybe put it in a constant as the directory names?
        for (int i = 0; i < data.length; i++) {
            String[] cmd = data[i].split("=");
            switch (cmd[0]){
                case "name":
                    name = cmd[1];
                    continue;
                case "bias":
                    bias = Float.parseFloat(cmd[1]);
                    continue;
                case "floors":
                    compileFloors(cmd[1].split(","));
                    continue;
                case "blocks":
                    compileBlocks(cmd[1].split(","));
                    continue;
                case "validSidings":
                    //todo
                    continue;
            }

        }

    }

    private void compileFloors(String[] names){
        for(String act : names){
            ProceduralBuildingFloor floor = new ProceduralBuildingFloor(Utils.readFile(sourceDirectory+"\\"+act+ PROCEDURAL_FLOOR_EXTENSION_NAME).split("\n"),this);
            allFloors.add(floor);
        }
    }

    private void compileBlocks(String[] names){
        for(String act : names){
            ProceduralBuildingBlock block = new ProceduralBuildingBlock(Utils.readFile(sourceDirectory+"\\"+act+ PROCEDURAL_BLOCK_EXTENSION_NAME).split("\n"),this);
            allBlocks.add(block);
        }
    }

    public TexturedModel generate(int[][] pattern){

        Random r = new Random();
        int desiredHeight = r.nextInt(maxFloors-minFloors)+minFloors;
        int currentHeight = 0;

        ArrayList<TexturedModel> floorModels = new ArrayList<>(desiredHeight*2);
        ProceduralBuildingFloor[] floorMap = new ProceduralBuildingFloor[desiredHeight];
        for (int f = 0; currentHeight < desiredHeight; f++) {
            //todo add object-indenpendent check to see if floor can fit it's desired level
            //todo floor height compat
            ProceduralBuildingFloor floor = allFloors.get(r.nextInt(allFloors.size()));
            ProceduralBuildingFloor prevFloor = null;
            if(f > 0){prevFloor = floorMap[f-1];}
            //the application can get stuck here, i really need to up the contratiction model
            while(!floor.canBeBuilt(f,prevFloor)){floor =  allFloors.get(r.nextInt(allFloors.size()));}
            floorModels.add(floor.generate(pattern,currentHeight));
            floorMap[f] = floor;
            currentHeight += floor.height;
        }
        TexturedModel[] floorArray = new TexturedModel[floorModels.size()];
        floorArray = floorModels.toArray(floorArray);
        return ModelUtils.mergeModels(floorArray,texture);
    }

    public ProceduralBuildingBlock getBlock(String name){
        for(ProceduralBuildingBlock act : allBlocks){
            if(act.name.equals(name)){return act;}
        }
        return null;
    }

    public ProceduralBuildingFloor getFloor(String name){
        for(ProceduralBuildingFloor act : allFloors){
            if(act.name.equals(name)){return act;}
        }
        return null;
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
