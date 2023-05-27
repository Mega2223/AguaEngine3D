package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.ModelUtils;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.TexturedModel;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.TextureShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.TextureManager;
import net.mega2223.lwjgltest.aguaengine3d.mathematics.MathUtils;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProceduralBuilding implements ProceduralBuildingObject {

    public static final String PROCEDURAL_FLOOR_EXTENSION_NAME = ".procf";
    public static final String PROCEDURAL_BLOCK_EXTENSION_NAME = ".procbloc";
    public static final String PROCEDURAL_BUILDING_EXTENSION_NAME = ".procb";

    protected int minFloors = 1, maxFloors = 20;

    protected String sourceDirectory;
    protected String name = null;
    protected float bias = -1;
    protected int texture;
    protected ShaderProgram shaderProgram;
    protected boolean shouldConsiderMiddleBlocks = true;

    ArrayList<ProceduralBuildingBlock> allBlocks = new ArrayList<>();
    ArrayList<ProceduralBuildingFloor> allFloors = new ArrayList<>();

    public ProceduralBuilding(String sourceDir){
        sourceDirectory = sourceDir;
        shaderProgram = new TextureShaderProgram();
        texture = TextureManager.loadTexture(sourceDir+"\\Texture.png");
        String[] data = Utils.readFile(sourceDir+"\\Main.procb").split("\n");
        decompileData(data); //maybe put it in a constant as the directory names?
    }

    public ProceduralBuilding(String sourceDir, ShaderProgram program){
        sourceDirectory = sourceDir;
        shaderProgram = program;
        texture = TextureManager.loadTexture(sourceDir+"\\Texture.png");
        String[] data = Utils.readFile(sourceDir+"\\Main.procb").split("\n");
        decompileData(data); //maybe put it in a constant as the directory names?
    }


    private void decompileData(String[] data){
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
                case "maxFloors":
                    maxFloors = Integer.parseInt(cmd[1]);
                case "minFloors":
                    minFloors = Integer.parseInt(cmd[1]);
                case "validSidings":
                    //todo
                    continue;
                case "shouldRenderMiddle":
                    shouldConsiderMiddleBlocks = Boolean.parseBoolean(cmd[1]);
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

    public TexturedModel generate(int[][] pattern, int where){

        Random r = new Random();
        int bound = maxFloors - minFloors;
        int desiredHeight = maxFloors;
        if(bound>0){desiredHeight = r.nextInt(bound)+minFloors;}
        int currentHeight = 0;

        ArrayList<TexturedModel> floorModels = new ArrayList<>(desiredHeight);

        ProceduralBuildingFloor[] floorMap = new ProceduralBuildingFloor[desiredHeight];//fixme
        for (int f = 0; currentHeight < desiredHeight; f++) {
            ProceduralBuildingFloor prevFloor = null;
            if(f > 0){prevFloor = floorMap[f-1];}
            List<ProceduralBuildingFloor> possibleFloors = new ArrayList();
            for(ProceduralBuildingFloor act : allFloors){if(act.canBeBuilt(f,prevFloor)){possibleFloors.add(act);}}
            float[] probabilites = new float[possibleFloors.size()];
            for(int i = 0; i < possibleFloors.size(); i++){probabilites[i]=possibleFloors.get(i).bias;}
            ProceduralBuildingFloor floorToBuild = (ProceduralBuildingFloor) MathUtils.doWeightedSelection(possibleFloors,probabilites);
            //the application can get stuck here, i really need to up the contratiction model
            floorModels.add(floorToBuild.generate(pattern,currentHeight,where,shouldConsiderMiddleBlocks));
            floorMap[f] = floorToBuild;
            currentHeight += floorToBuild.height;
        }
        TexturedModel[] floorArray = new TexturedModel[floorModels.size()];
        floorArray = floorModels.toArray(floorArray);
        return ModelUtils.mergeModels(floorArray, texture, shaderProgram);
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
