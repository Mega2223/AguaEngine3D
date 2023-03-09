package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.ModelUtils;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.TexturedModel;

import java.util.ArrayList;
import java.util.List;

public class ProceduralBuildingBlock implements ProceduralBuildingObject {

    //side-idependent variables
    public static final int NORTH = 0, SOUTH = 1, EAST = 2, WEST = 3;
    float[][] wallVertices = new float[4][];
    float[][] textureCoordinates = new float[4][];
    int[][] indices = new int[4][];
    boolean[] forceRender = new boolean[4];
    String[][] compartiblesForAdjacency = new String[4][];

    List<ProceduralBuildingBlock>[] compartibleAdjacentBuildingBlocks = new List[4];

    float bias = -1;
    ProceduralBuilding context;
    String name = null;

    public ProceduralBuildingBlock(String[] data, ProceduralBuilding context){
        this.context = context;
        int currentFace = -1;
        //fOr StAtEmEnT cAN Be rePlACeD wITh eNhAncEd 'fOr' 🤓 🤓 🤓
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
                            continue;
                        case "bias":
                            bias = Float.parseFloat(cmd[1]);
                            continue;
                        case "compartibleNorth":
                            compartiblesForAdjacency[NORTH] = cmd[1].split(",");
                            continue;
                        case "compartibleSouth":
                            compartiblesForAdjacency[SOUTH] = cmd[1].split(",");
                            continue;
                        case "compartibleWest":
                            compartiblesForAdjacency[WEST] = cmd[1].split(",");
                            continue;
                        case "compartibleEast":
                            compartiblesForAdjacency[EAST] = cmd[1].split(",");
                            continue;
                            //Wall specific variables:
                        case "forceRender":
                            forceRender[currentFace] = Boolean.parseBoolean(cmd[1]);
                            continue;
                        case "vertices":
                            String[] split = cmd[1].split(",");
                            wallVertices[currentFace] = new float[split.length];
                            for(int j = 0; j < split.length; j++){
                                wallVertices[currentFace][j] = Float.parseFloat(split[j]);}
                            continue;
                        case "indices":
                            String[] split2 = cmd[1].split(",");
                            indices[currentFace] = new int[split2.length];
                            for(int j = 0; j < split2.length; j++){indices[currentFace][j] = Integer.parseInt(split2[j]);}
                            continue;
                        case "textureCoords":
                            String[] split3 = cmd[1].split(","); //why does the interpreter hate me
                            textureCoordinates[currentFace] = new float[split3.length];
                            for(int j = 0; j < split3.length; j++){textureCoordinates[currentFace][j] = Float.parseFloat(split3[j]);}
                            continue;
                    }
            }
        }
    }

    public TexturedModel generate(boolean genNorth, boolean genSouth, boolean genEast, boolean genWest, float correctionX, float correctionY, float correctionZ){
        ArrayList<TexturedModel> models = new ArrayList<>(4);
        if(genNorth){models.add(generateWall(NORTH));}
        if(genSouth){models.add(generateWall(SOUTH));}
        if(genEast){models.add(generateWall(EAST));}
        if(genWest){models.add(generateWall(WEST));}
        TexturedModel[] modelArray = new TexturedModel[models.size()];
        for(int i = 0; i < modelArray.length;i++){modelArray[i]=models.get(i);}
        TexturedModel finishedModel = ModelUtils.mergeModels(modelArray, context.texture);
        ModelUtils.translateModel(finishedModel,correctionX,correctionY,correctionZ);
        return finishedModel;
    }

    private TexturedModel generateWall(int index){
        return new TexturedModel(
                wallVertices[index],
                indices[index],
                textureCoordinates[index],
                context.texture
        );
    }

    public static int invertDirection(int direction){
        switch (direction){
            case NORTH: return SOUTH;
            case SOUTH: return NORTH;
            case EAST: return WEST;
            case WEST: return EAST;
            default: return direction;
        }
    }
    public boolean isCompartible(int direction, ProceduralBuildingBlock block){
        return isCompartible(direction,block,true);
    }
    public boolean isCompartible(int direction, ProceduralBuildingBlock block, boolean checkForBoth){
        if(block == null){return true;}

        boolean compartForMe = isCompartible(direction,block.name);
        if(!checkForBoth){return compartForMe;}
        boolean compartForThem = block.isCompartible(invertDirection(direction),this,false);
        return compartForThem && compartForMe;
    }

    public boolean isCompartible(int direction, String blockName){
        if(compartiblesForAdjacency[direction][0].equalsIgnoreCase("%any%")){
            return true;
        }
        for(String act : compartiblesForAdjacency[direction]){
            if(act.equals(blockName)){return true;}
        }
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getBias() {
        return bias;
    }

    @Override
    public String toString(){
        return name;
    }
}
