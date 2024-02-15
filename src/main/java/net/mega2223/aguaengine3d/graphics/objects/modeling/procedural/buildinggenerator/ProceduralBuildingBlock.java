package net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator;

import net.mega2223.aguaengine3d.graphics.objects.modeling.ModelUtils;
import net.mega2223.aguaengine3d.graphics.objects.modeling.TexturedModel;

import java.util.ArrayList;

public class ProceduralBuildingBlock implements ProceduralBuildingObject {

    public static final int SIMMETRY_NONE = 0, SIMMETRY_DOUBLE = 1, SIMMENTRY_QUAD = 2;
    int simmetry = SIMMETRY_NONE;

    //side-independent variables
    public static final int NORTH = 0, SOUTH = 1, EAST = 2, WEST = 3, RENDER_ALWAYS = 4;
    float[][] wallVertices = new float[5][];
    float[][] textureCoordinates = new float[5][];

    int[][] indices = new int[5][];
    boolean[] forceRender = new boolean[5];
    boolean[] isAdjacencyExclusive = {false,false,false,false,false};//is that really the best way to do so?
    String[][] compartiblesForAdjacency = new String[5][];

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
                case "all:":
                    currentFace = RENDER_ALWAYS;
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
                            setCompartibles(NORTH,cmd);
                            continue;
                        case "compartibleSouth":
                            setCompartibles(SOUTH,cmd);
                            continue;
                        case "compartibleEast":
                            setCompartibles(EAST,cmd);
                            continue;
                        case "compartibleWest":
                            setCompartibles(WEST,cmd);
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
                        case "simmetry":
                            switch (cmd[1]){
                                case "none": simmetry = SIMMETRY_NONE; break;
                                case "mirror": simmetry = SIMMETRY_DOUBLE; break;
                                case "quad": simmetry = SIMMENTRY_QUAD; break;
                            }
                    }
            }
        }

    }

    private ProceduralBuildingBlock(int simmetry, float[][] wallVertices, float[][] textureCoordinates, int[][] indices, boolean[] forceRender, boolean[] isAdjacencyExclusive, String[][] compartiblesForAdjacency, float bias, ProceduralBuilding context, String name) {
        this.simmetry = simmetry;
        this.wallVertices = wallVertices;
        this.textureCoordinates = textureCoordinates;
        this.indices = indices;
        this.forceRender = forceRender;
        this.isAdjacencyExclusive = isAdjacencyExclusive;
        this.compartiblesForAdjacency = compartiblesForAdjacency;
        this.bias = bias;
        this.context = context;
        this.name = name;
    }

    public TexturedModel generate(boolean genNorth, boolean genSouth, boolean genEast, boolean genWest, float correctionX, float correctionY, float correctionZ){
        ArrayList<TexturedModel> models = new ArrayList<>(4);
        if(genNorth){models.add(generateWall(NORTH));}
        if(genSouth){models.add(generateWall(SOUTH));}
        if(genEast){models.add(generateWall(EAST));}
        if(genWest){models.add(generateWall(WEST));}
        models.add(generateWall(RENDER_ALWAYS));
        TexturedModel[] modelArray = new TexturedModel[models.size()];
        for(int i = 0; i < modelArray.length;i++){modelArray[i]=models.get(i);}
        TexturedModel finishedModel = ModelUtils.mergeModels(modelArray, context.texture, null);
        ModelUtils.translateModel(finishedModel,correctionX,correctionY,correctionZ);
        return finishedModel;
    }

    private TexturedModel generateWall(int index){
        if(wallVertices[index]==null){
            wallVertices[index] = new float[0];
            indices[index] = new int[0];
            textureCoordinates[index] = new float[0];
        }
        return new TexturedModel(
                wallVertices[index],
                indices[index],
                textureCoordinates[index],
                context.shaderProgram,
                context.texture
        );
    }

    private void setCompartibles(int side, String[] cmd){ // to avoid verbose code
        String arg = cmd[1];
        boolean isNegative = arg.charAt(0) == '!';
        if(isNegative) {isAdjacencyExclusive[side] = true;arg = arg.replace("!","");}
        arg = !arg.contains(NOT_INSIDE_BUILDING) && context.implicitlyAssumeNoBuildOK &&  !isNegative ? arg + "," + NOT_INSIDE_BUILDING : arg;
        compartiblesForAdjacency[side] = arg.split(",");
    }

    public static int mirrorDirection(int dir){
        switch (dir){
            case NORTH: return SOUTH;
            case SOUTH: return NORTH;
            case EAST: return WEST;
            case WEST: return EAST;
            default: return dir;
        }
    }

    public static int directionToLeft (int dir){
        switch (dir){
            case NORTH: return WEST;
            case SOUTH: return EAST;
            case EAST: return SOUTH;
            case WEST: return NORTH;
            default: return dir;
        }
    }

    public static int directionToRight (int dir){
        switch (dir){
            case NORTH: return EAST;
            case SOUTH: return WEST;
            case EAST: return NORTH;
            case WEST: return SOUTH;
            default: return dir;
        }
    }

    public boolean isCompatible(int direction, ProceduralBuildingBlock block){
        return isCompatible(direction,block,true);
    }

    public boolean isCompatible(int direction, ProceduralBuildingBlock block, boolean checkForBoth){
        if(block == null){
            return true;
        }

        checkForBoth = checkForBoth && !(block == OUT_OF_BITMAP_BOUNDS || block == OUT_OF_BITMAP_BUILD_PATH);

        boolean compartForMe = isCompatible(direction,block.name);
        if(!checkForBoth){return compartForMe;}
        boolean compartForThem = block.isCompatible(mirrorDirection(direction),this,false);
        return compartForThem && compartForMe;
    }

    public boolean isCompatible(int direction, String blockName){
        if(compartiblesForAdjacency[direction][0].equalsIgnoreCase(ANY)){return true;}
        for(String act : compartiblesForAdjacency[direction]){
            if(act.equals(NOT_INSIDE_BUILDING)){
                boolean b = blockName.equals(OUT_OF_BITMAP_NAME) || blockName.equals(OUT_OF_PATH_NAME);
                if(isAdjacencyExclusive[direction]){b = !b;}
                return b;
            } else if (act.equals(OUT_OF_BOUNDS_SINTAX)){
                boolean b = blockName.equals(OUT_OF_BITMAP_NAME);
                if(isAdjacencyExclusive[direction]){b = !b;}
                return b;
            }
            else if(act.equals(blockName)){
                return !isAdjacencyExclusive[direction];
            }
        }
        return isAdjacencyExclusive[direction];
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

    public static final ProceduralBuildingBlock OUT_OF_BITMAP_BUILD_PATH = new ProceduralBuildingBlock(0,null,null,null,null,null,null,0,null, OUT_OF_PATH_NAME);
    public static final ProceduralBuildingBlock OUT_OF_BITMAP_BOUNDS = new ProceduralBuildingBlock(0,null,null,null,null,null,null,0,null, OUT_OF_BITMAP_NAME);

}
