package net.mega2223.aguaengine3d.graphics.objects.modeling.uiutils;

import net.mega2223.aguaengine3d.graphics.objects.modeling.InterfaceComponent;

public class BitmapFont {

    final int texture;
    final float cellH, cellW, textW;
    final float[] widths;
    final char[] dictionary;

    /**For coordinates in the standard OpenGL texture coordinate system*/
    public BitmapFont(int texture, float cellW, float cellH, float textW, float[] baseWidths, char[] dictionary){
        this.texture = texture;
        this.cellH = cellH; this.cellW = cellW;
        this.textW = textW;
        this.widths = baseWidths.clone();
        this.dictionary = dictionary;
    }
    /**For image coordinates*/
    public BitmapFont(int texture, int imageWidth, int imageHeight, int cellWidth, int cellHeight, int textWidth, int[] baseWidths, char[] dictionary){
        this(texture,(float) cellWidth / imageWidth,(float) cellHeight / imageHeight,(float) textWidth / imageWidth,convertWidthsFromInts(baseWidths,imageWidth),dictionary);
    }

    public float getCharacterWidth(char character){
        for (int i = 0; i < dictionary.length; i++){
            if (character == dictionary[i]){return widths[i];}
        }
        return textW;
    }

    public float[] genFromString(CharSequence text){
        for (int i = 0; i < text.length(); i++) {
            //TODO
        }
        return new float[0];
    }

    public float[] getStartingPointFromChar(char character){
        for (int i = 0; i < dictionary.length; i++){
            if(character == dictionary[i]){
                return new float[]{i*cellW,i%cellH};
            }
        }
        return new float[0];
    }

    public int getTexture() {
        return texture;
    }

    private static float[] convertWidthsFromInts(int[] widths, int imgW){
        float[] baseW = new float[widths.length];
        for (int i = 0; i < widths.length; i++) {
            baseW[i] = 1 - (widths[i]/(float)imgW);
        }
        return baseW;
    }

}
