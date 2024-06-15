package net.mega2223.aguaengine3d.tools.modeleditor;

import net.mega2223.aguaengine3d.graphics.objects.modeling.InterfaceComponent;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ui.BitmapFont;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ui.TextManipulator;
import net.mega2223.aguaengine3d.graphics.objects.shadering.DisplayComponentShaderProgram;
import net.mega2223.aguaengine3d.objects.WindowManager;

public class Console extends InterfaceComponent {
    public boolean isVisible = false;
    protected BitmapFont font;
    protected StringBuilder data = new StringBuilder();
    public Console(WindowManager context, BitmapFont font) {
        super(new float[]{}, new int[]{}, new float[]{}, -1, 1, null);
        this.font = font;
        DisplayComponentShaderProgram shader = new DisplayComponentShaderProgram(font.getTexture(), this.textureCoords, 1);
        this.setShader(shader);
        this.shaderProgram = shader;
        add("teste");
    }

    public void add(String what){
        data.append(what);
        refresh();
    }

    public void refresh(){
        //TODO UNOPTIMAL >:(
        InterfaceComponent mod = font.genFromString(data);
        this.setVertices(mod.getRelativeVertices());
        this.setIndices(mod.getIndices());
        this.setTextureCoords(mod.getTextureCoords());
        this.unloadVBOS();
    }

    @Override
    public void draw() {
        super.draw();
    }
}
