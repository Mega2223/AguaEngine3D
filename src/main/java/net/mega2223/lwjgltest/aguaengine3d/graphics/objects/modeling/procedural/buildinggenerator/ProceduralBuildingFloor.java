package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.ModelUtils;
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

    ProceduralBuilding context;//mayme fixme? idk i need that to get the objects, and storing the objects in the class also seems unnecessary

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
            }
        }
    }

    public TexturedModel generate(int[][] map, int height){
        List<TexturedModel> models = new ArrayList<>();
        Random r = new Random();

        //map must be a matrix
        //perhaps check for that?
        ProceduralBuildingBlock[][] blockMap = new ProceduralBuildingBlock[map.length][map[0].length];

        for (int z = 0; z < blockMap.length; z++) {
            for (int x = 0; x < blockMap[z].length; x++) {
                if(map[z][x]==0){continue;}
                boolean doNorth = z == 0, doSouth = z == blockMap.length - 1,
                        doEast = x == blockMap[z].length - 1, doWest = x == 0;
                if(!doNorth&&map[z-1][x]==0){doNorth=true;}
                if(!doSouth&&map[z+1][x]==0){doSouth=true;}
                if(!doEast&&map[z][x+1]==0){doEast=true;}
                if(!doWest&&map[z][x-1]==0){doWest=true;}

                ProceduralBuildingBlock toConstruct = select(x,z,blockMap);
                blockMap[z][x] = toConstruct;
                models.add(toConstruct.generate(doNorth,doSouth,doEast,doWest,x,height,z));

            }
        }

        TexturedModel[] ret = new TexturedModel[models.size()];
        for(int i = 0; i < ret.length; i++){ret[i]=models.get(i);}
        return ModelUtils.mergeModels(ret, context.texture);

    }

    private ProceduralBuildingBlock select(int z, int x, ProceduralBuildingBlock[][] map){
        List<ProceduralBuildingBlock> floorBlocks = getUsableBlocks();
        List<ProceduralBuildingBlock> potential = new ArrayList<>();
        //todo: weighted probability calculations
        boolean boundaryNorth = z == 0, boundarySouth = z == map[x].length-1,
                boundaryEast = x == map.length-1, boundaryWest = x == 0;
        ProceduralBuildingBlock north = null, south = null, east = null, west = null;
        if(!boundaryNorth){north = map[x][z-1];}
        if(!boundarySouth){south = map[x][z+1];}
        if(!boundaryEast){east = map[x+1][z];}
        if(!boundaryWest){west = map[x-1][z];}


        for(ProceduralBuildingBlock act : floorBlocks){
            boolean n = false,s = false,e = false,w = false; //maybe invert the directions? idk
            if(act.isCompartible(ProceduralBuildingBlock.NORTH,north)){n = true;}
            if(act.isCompartible(ProceduralBuildingBlock.SOUTH,south)){s =  true;}
            if(act.isCompartible(ProceduralBuildingBlock.EAST,east)){e = true;}
            if(act.isCompartible(ProceduralBuildingBlock.WEST,west)){w = true;}
            if(n&&s&&e&&w){potential.add(act);}
        }

        if(potential.size()==0){return null;}
        return potential.get(new Random().nextInt(potential.size()));
    }

    public List<ProceduralBuildingBlock> getUsableBlocks(){
        List<ProceduralBuildingBlock> avail = new ArrayList<>();
        for (String act : possibleBlocks){
            avail.add(context.getBlock(act));
        }
        return avail;
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
