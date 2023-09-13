package net.mega2223.aguaengine3d.graphics.objects.modeling.uiutils;

import net.mega2223.aguaengine3d.graphics.utils.TextureManager;

import java.awt.image.BufferedImage;

public class TextManipulator {
    private TextManipulator(){}

    public BitmapFont decompileCSV(String[] content, BufferedImage image, int start){
        int imageWidth = image.getWidth(); int imageHeight = image.getHeight();
        int cellWidth = -1; int cellHeight = -1;
        int l = 0;
        int texture = TextureManager.loadTexture(image);

        while (l < content.length || (cellHeight != -1 && cellWidth != -1)){
            String[] act = content[l].split(",");
            switch (act[0]){
                case ("Cell Height"):
                    cellHeight = Integer.parseInt(act[1]);
                    break;
                case ("Cell Width"):
                    cellWidth = Integer.parseInt(act[1]);
                    break;
            }
            l++;
        }
        int estimatedCapacity = (imageHeight / cellHeight) * (imageWidth / cellWidth);
        float[] baseWidths = new float[estimatedCapacity];
        for (l = 0; l < content.length; l++) { //todo there's gotta be a better way lol
            String[] act = content[l].split(",");
            if(act[0].contains("Base Width")){
                int loc = Integer.parseInt(act[0].split(" ")[1]);
                if(loc - start >= 0 && loc - start < estimatedCapacity){
                    baseWidths[loc - start] = Integer.parseInt(act[1]);
                }
            }
            //Char 17 Base Width,9

        }

        char[] dict = new char[estimatedCapacity];
        for (int i = 0; i < estimatedCapacity; i++) {
           //int x = estimatedCapacity/imageWidth;
           //int y = estimatedCapacity%imageHeight;
           dict[i] = (char) (start+i);
        }

        return new BitmapFont(texture,cellWidth,cellHeight,cellWidth/2,baseWidths,dict);
    }

}
