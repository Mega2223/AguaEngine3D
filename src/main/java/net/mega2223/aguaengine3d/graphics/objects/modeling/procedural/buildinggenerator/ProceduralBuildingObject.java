package net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator;

public interface ProceduralBuildingObject {

    String getName();
    float getBias();

    String ANY = "%any%";
    String NONE = "%none%";
    String NOT_INSIDE_BUILDING = "%nobuild%";
    String OUT_OF_BOUNDS_SINTAX = "%outofbounds%";

    ProceduralBuildingBlock OUT_OF_BITMAP_BOUNDS = ProceduralBuildingBlock.OUT_OF_BITMAP_BOUNDS;
    ProceduralBuildingBlock OUT_OF_BITMAP_BUILD_PATH = ProceduralBuildingBlock.OUT_OF_BITMAP_BUILD_PATH;
    String OUT_OF_PATH_NAME = "OutOfBuildPath";
    String OUT_OF_BITMAP_NAME = "OutOfBitMap";
    int ANY_INTEGER = -33;


}
