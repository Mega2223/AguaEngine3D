package net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.buildinggenerator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ProceduralBuildingManager {

    private ProceduralBuildingManager(){}

    public static void printBitMap (int[][] map){
        StringBuilder out = new StringBuilder();
        for (int y = 0; y < map.length; y++) {
            out.append("[");
            for (int x = 0; x < map[y].length-1; x++) {
                out.append(map[y][x]).append(",");
            }
            out.append(map[y][map[y].length - 1]).append("]\n");
        }
        System.out.println(out);
    }

    public static void debugMap(ProceduralBuildingBlock[][] map){
        StringBuilder out = new StringBuilder();
        StringBuilder buf = new StringBuilder();
        int longestString = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                ProceduralBuildingBlock act = map[i][j];
                int len = act == null ? 4 : act.getName().length();
                longestString = Math.max(len, longestString);
            }
        }
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                ProceduralBuildingBlock act = map[y][x];
                String name = act == null ? "NULL" :  act.getName();
                buf.delete(0,Math.max(buf.length()-1,0));
                for (int i = name.length(); i < longestString; i++) {buf.append(" ");}
                out.append(name).append(buf);
                if(x+1!=map[y].length){out.append("| ");}
            }
            out.append("\n");
        }
        System.out.println(out);
    }

    public static int[][] pngToBitmap(String dir, int[][] expectedColors){
        try {
            BufferedImage img = ImageIO.read(new File(dir));
            return pngToBitmap(img, expectedColors);
        } catch (IOException e) {return null;}
    }

    public static int[][] pngToBitmap(BufferedImage img, int[][] expectedColors){
        int[][] bitmap = new int[img.getHeight()][img.getWidth()];
        for (int c = 0; c < expectedColors.length; c++) {
            int[] expectedColor = expectedColors[c];
            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    int color = img.getRGB(x,y);
                    int b = color & 0xff;
                    int g = (color & 0xff00) >> 8;
                    int r = (color & 0xff0000) >> 16;
                    if(r==expectedColor[0]&&g==expectedColor[1]&&b==expectedColor[2]){
                        bitmap[y][x] = c+1; //leave the 0 value for colors that are not expected
                    }
                }
            }
        }
        return bitmap;
    }

}
