import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator.ProceduralBuilding;

public class ProceduralBuildingTest {

    public static void main(String[] args) {

        String dir = Utils.PROCEDURAL_BUILDINGS_DIR+"\\BrickStyle1";

        ProceduralBuilding build = new ProceduralBuilding(Utils.readFile(dir));

        System.out.println(build.getBias());
        System.out.println();
    }

}
