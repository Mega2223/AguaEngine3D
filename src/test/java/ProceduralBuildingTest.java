import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;
import net.mega2223.lwjgltest.aguaengine3d.misc.proceduralbuildinggenerator.ProceduralBuilding;
import net.mega2223.lwjgltest.aguaengine3d.misc.proceduralbuildinggenerator.ProceduralBuildingBlock;
import net.mega2223.lwjgltest.aguaengine3d.misc.proceduralbuildinggenerator.ProceduralBuildingFloor;

public class ProceduralBuildingTest {

    public static void main(String[] args) {

        String dir = Utils.PROCEDURAL_BUILDINGS_DIR+"\\BrickStyle1";

        ProceduralBuilding build = new ProceduralBuilding(Utils.readFile(Utils.PROCEDURAL_BUILDINGS_DIR+"\\BrickStyle1\\Main.procb").split("\n"),dir);

        System.out.println(build.getBias());
        System.out.println();
    }

}
