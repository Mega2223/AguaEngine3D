package net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator;

import net.mega2223.aguaengine3d.graphics.objects.modeling.TexturedModel;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ModelUtils;
import net.mega2223.aguaengine3d.mathematics.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProceduralBuildingFloor implements ProceduralBuildingObject{

    String name = null;
    String[] possibleBlocks = null;
    String[] validAbove = null;
    float bias = -1;
    int height = 1;
    int[] compartibleHeights = null;
    boolean compartibleHeightsExclusive = false;

    ProceduralBuilding context;//maybe fixme? idk i need that to get the objects, and storing the objects in the class also seems unnecessary

    //builds object according to data
    public ProceduralBuildingFloor(String[] data, ProceduralBuilding context){
        this.context = context;
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
                case("compartibleHeights"):
                    if(cmd[1].charAt(0) == '!'){compartibleHeightsExclusive = true; cmd[1] = cmd[1].replace("!","");}
                    String[] heights = cmd[1].split(",");
                    compartibleHeights = new int[heights.length];
                    if(heights[0].equalsIgnoreCase(ANY)){compartibleHeights[0] = ANY_INTEGER; continue;}
                    for(int h = 0; h < heights.length; h++){compartibleHeights[h] = Integer.parseInt(heights[h]);}
            }
        }
    }

    public TexturedModel generate(int[][] map, int height, int where, boolean shouldConsiderMiddleBlocks){
        List<TexturedModel> models = new ArrayList<>();
        Random r = new Random();

        //map must be a matrix
        //perhaps check for that?
        ProceduralBuildingBlock[][] blockMap = new ProceduralBuildingBlock[map.length][map[0].length];

        genIt(map,height,where,blockMap,0,0,models,shouldConsiderMiddleBlocks);

        TexturedModel[] ret = new TexturedModel[models.size()];
        for(int i = 0; i < ret.length; i++){ret[i]=models.get(i);}
        return ModelUtils.mergeModels(ret, context.texture);
    }

    protected void genIt(int[][] buildMap, int height, int whereToBuild, ProceduralBuildingBlock[][] blockMap, int z, int x, List<TexturedModel> whereToPlace,boolean shouldConsiderMiddleBlocks){
        if(x>=buildMap[z].length){x=0;z++;}
        if(z>=blockMap.length){return;}
        while(buildMap[z][x] != whereToBuild || blockMap[z][x] != null){
            x++; if(x >= blockMap[z].length){z++; x = 0;}
            if(z>=blockMap.length){return;}
        }

        if(!shouldConsiderMiddleBlocks){
            boolean northEmpty = z == 0, southEmpty = z >= buildMap.length-1, eastEmpty = x >= buildMap[z].length-1, westEmpty = x == 0;
            if(!northEmpty){northEmpty = buildMap[z-1][x] != whereToBuild;}
            if(!southEmpty){southEmpty = buildMap[z+1][x] != whereToBuild;}
            if(!eastEmpty){eastEmpty = buildMap[z][x+1] != whereToBuild;}
            if(!westEmpty){westEmpty = buildMap[z][x-1] != whereToBuild;}
            if(!northEmpty && !southEmpty && !eastEmpty && !westEmpty){
                genIt(buildMap,height,whereToBuild,blockMap,z,x+1,whereToPlace, false);//always false
                return;
            }
        }

        List<ProceduralBuildingBlock> possibleBlocks = getUsableBlocks();

        boolean couldPlace = false;
        while (!couldPlace && possibleBlocks.size() > 0){
            float[] weights = new float[possibleBlocks.size()];
            for(int i = 0; i < possibleBlocks.size(); i++){weights[i] = possibleBlocks.get(i).bias;}
            ProceduralBuildingBlock toTry = (ProceduralBuildingBlock) MathUtils.doWeightedSelection(possibleBlocks,weights);
            couldPlace = tryToPlace(z,x,height,buildMap,whereToBuild,blockMap,toTry,whereToPlace);
            if(couldPlace){
                try {genIt(buildMap,height,whereToBuild,blockMap,z,x+1,whereToPlace,shouldConsiderMiddleBlocks);return;} catch (ContradictionException ignored) {}
            } else {possibleBlocks.remove(toTry);
        }
        }
        blockMap[z][x] = null;
        throw new ContradictionException("The generator has run into a contradiction at the construction " + context.name + ", floor " + height + " of type " + name);
    }

    static class ContradictionException extends RuntimeException {
        String message;
        ContradictionException(String message){
            super(message);
            this.message = message;
        }
    }

    private boolean tryToPlace(int z, int x, int height, int[][] buildMap, int where, ProceduralBuildingBlock[][] blockMap, ProceduralBuildingBlock block, List<TexturedModel> whereToAdd){
        boolean canBuildNorth = z > 0, canBuildSouth = z < buildMap.length -1,
                canBuildEast = x < buildMap[z].length -1, canBuildWest = x > 0;
        ProceduralBuildingBlock north = null, south = null, east = null, west = null;

        if(canBuildNorth){north = blockMap[z-1][x];}
        if(canBuildSouth){south = blockMap[z+1][x];}
        if(canBuildEast){east = blockMap[z][x+1];}
        if(canBuildWest){west = blockMap[z][x-1];}

        canBuildNorth = block.isCompartible(ProceduralBuildingBlock.NORTH,north);
        canBuildSouth = block.isCompartible(ProceduralBuildingBlock.SOUTH,south);
        canBuildEast = block.isCompartible(ProceduralBuildingBlock.EAST,east);
        canBuildWest = block.isCompartible(ProceduralBuildingBlock.WEST,west);

        if(canBuildNorth && canBuildSouth && canBuildEast && canBuildWest){
            boolean genNorth = z <= 0, genSouth = z >= buildMap.length -1,
                    genEast = x >= buildMap[z].length -1, genWest = x <= 0;
            if(!genNorth){genNorth = buildMap[z-1][x] != where;}
            if(!genSouth){genSouth = buildMap[z+1][x] != where;}
            if(!genEast){genEast = buildMap[z][x+1] != where;}
            if(!genWest){genWest = buildMap[z][x-1] != where;}

            whereToAdd.add(block.generate(genNorth,genSouth,genEast,genWest,x,height,z,context.shaderProgram));
            blockMap[z][x] = block;
            return true;
        }

        return false;
    }

    public List<ProceduralBuildingBlock> getUsableBlocks(){
        List<ProceduralBuildingBlock> avail = new ArrayList<>();
        for (String act : possibleBlocks){
            avail.add(context.getBlock(act));
        }
        return avail;
    }

    public boolean canBeBuilt(int floor, ProceduralBuildingFloor floorBelow){
        boolean canAboveFloor = floorBelow == null, canNumber = compartibleHeights[0] == ANY_INTEGER;
        if(!canAboveFloor){canAboveFloor = validAbove[0].equalsIgnoreCase(ANY);}
        for(int i = 0; i < validAbove.length && !canAboveFloor; i++){
            canAboveFloor = validAbove[i].equals(floorBelow.name);
        }

        for (int i = 0; i < compartibleHeights.length && !canNumber; i++) {
            canNumber = compartibleHeights[i] == floor;
        }
        if(compartibleHeightsExclusive){canNumber = !canNumber;}
        return canAboveFloor && canNumber;
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
