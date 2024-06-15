package net.mega2223.aguaengine3d.graphics.objects.modeling.ui;

import net.mega2223.aguaengine3d.graphics.objects.modeling.InterfaceComponent;
import net.mega2223.aguaengine3d.graphics.utils.TextureManager;
import net.mega2223.aguaengine3d.misc.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("ALL")
public class BitmapFont {

    final int texture;
    final float cellH, cellW, textW;
    final float[] widths;
    final char[] dictionary;

    /**For image coordinates*/

    public BitmapFont(int texture, int imageWidth, int imageHeight, int cellWidth, int cellHeight, int textWidth, int[] baseWidths, char[] dictionary){
        this(texture,(float) cellWidth / imageWidth,(float) cellHeight / imageHeight,(float) textWidth / imageWidth,convertWidthsFromInts(baseWidths,imageWidth),dictionary);
    }

    /**For coordinates in the standard OpenGL texture coordinate system*/
    public BitmapFont(int texture, float cellW, float cellH, float textW, float[] baseWidths, char[] dictionary){
        this.texture = texture;
        this.cellH = cellH; this.cellW = cellW;
        this.textW = textW;
        this.widths = baseWidths.clone();
        this.dictionary = dictionary;
    }

    public float getCharacterWidth(char character){
        for (int i = 0; i < dictionary.length; i++){
            if (character == dictionary[i]){return widths[i];}
        }
        return textW;
    }

    public InterfaceComponent genFromString(CharSequence text){
        float[] vertices = new float[text.length()*16];
        int[] indices = new int[text.length()*6];
        float[] textureCoords = getTextureCoords(text);

        for (int t = 0, x = 0, y = 0; t < text.length(); t++) {

            vertices[t*16] = x/2F;
            vertices[t*16+1] = y;

            vertices[t*16+4] = (x+1)/2F;
            vertices[t*16+5] = y;

            vertices[t*16+8] = x/2F;
            vertices[t*16+9] = (y)-1;

            vertices[t*16+12] = (x+1)/2F;
            vertices[t*16+13] = (y)-1;

            x++;
            if(text.charAt(t) == '\n'){y--; x=0;}
        }
        for (int i = 0; i < indices.length; i+=6) {
            indices[i] = i*2/3 + 0;
            indices[i+1] = i*2/3 + 1;
            indices[i+2] = i*2/3 + 2;
            indices[i+3] = i*2/3 + 2;
            indices[i+4] = i*2/3 + 1;
            indices[i+5] = i*2/3 + 3;
        }
        return new InterfaceComponent(vertices,indices,textureCoords,texture,1);
    }

    public float[] getTextureCoords(CharSequence text){
        float[] data = new float[text.length()*8];
        for (int i = 0; i < text.length(); i++) {
            char l = text.charAt(i);
            float x = getX(l);
            float y = getY(l);
            float w = getCharacterWidth(l);
            data[i*8] = x;
            data[i*8+1] = y;
            data[i*8+2] = x + w;
            data[i*8+3] = y;
            data[i*8+4] = x;
            data[i*8+5] = y + cellH;
            data[i*8+6] = x + w;
            data[i*8+7] = y + cellH;
        }
        return data;
    }

    float getX(char character){
        for (int i = 0; i < dictionary.length; i++){
            if(character == dictionary[i]){
                return (i % (1 / cellW)) * cellW;
            }
        }
        return -1;
    }
    float getY(char character){
        for (int i = 0; i < dictionary.length; i++){
            if(character == dictionary[i]){
                float v = (int)(i * cellH);
                return v * cellH; //what?
            }
        }
        return -1;
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
            baseW[i] = (widths[i]/(float)imgW);
        }
        return baseW;
    }

}
