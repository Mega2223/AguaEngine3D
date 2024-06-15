package net.mega2223.aguaengine3d.tools.modeleditor;

import net.mega2223.aguaengine3d.graphics.objects.modeling.ui.BitmapFont;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ui.TextManipulator;
import net.mega2223.aguaengine3d.graphics.utils.TextureManager;
import net.mega2223.aguaengine3d.misc.Utils;

public class Misc {
    private Misc(){}
    public static BitmapFont loadConsolas(){
        return TextManipulator.decompileCSV(
                Utils.FONTS_DIR+"\\highResConsolas\\HSConsolas.csv",
                Utils.FONTS_DIR+"\\highResConsolas\\HSConsolas.png"
        );
    }
}
