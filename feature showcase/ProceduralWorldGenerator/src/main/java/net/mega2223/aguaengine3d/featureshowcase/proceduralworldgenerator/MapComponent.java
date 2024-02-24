package net.mega2223.aguaengine3d.featureshowcase.proceduralworldgenerator;

import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.ScriptedSequence;
import net.mega2223.aguaengine3d.graphics.objects.modeling.InterfaceComponent;
import net.mega2223.aguaengine3d.graphics.objects.shadering.DisplayComponentShaderProgram;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import org.lwjgl.opengl.GL30;

import static net.mega2223.aguaengine3d.featureshowcase.proceduralworldgenerator.WorldGen.camera;
import static net.mega2223.aguaengine3d.featureshowcase.proceduralworldgenerator.WorldGen.manager;

public class MapComponent extends InterfaceComponent {

    final float mapSize;
    float mapHeight;
    final int[] texture;

    ScriptedSequence updateSequence = new ScriptedSequence("MapRenderer") {
        final float[] proj = new float[16];
        final GrassShaderProgram noTranslation = new GrassShaderProgram();
        @Override
        protected void preLogic(int iteration, RenderingContext context) {
            mapHeight = WorldGen.mapHeight;
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,texture[0]);
            GL30.glClear(GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_COLOR_BUFFER_BIT);
            GL30.glViewport(0,0,64,64);
            float aspectRatio = manager.getAspectRatio();
            setAspectRatio(aspectRatio);
            setCoords(-aspectRatio + mapSize,1 - mapSize,0);
            MatrixTranslator.generatePerspectiveProjectionMatrix(proj, 0.01f, mapHeight+2000, (float) Math.toRadians(90), 1);
            MatrixTranslator.applyLookTransformation(proj, camera[0],mapHeight,camera[2]+1, camera[0],camera[2]-100,camera[2]);
            context.setFogDetails(100000,100);
            context.doCustomRender(proj,0,Integer.MAX_VALUE);
            context.setFogDetails(100,100);
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,0);
        }
    };

    public MapComponent(float mapSize, float mapHeight, int[] texture) {
        super(
                new float[]{mapSize, mapSize,0,0, mapSize,-mapSize,0,0, -mapSize, mapSize,0,0, -mapSize,-mapSize,0,0},
                new int[]{0,1,2,2,1,3},
                new float[]{0,0, 0,1, 1,0, 1,1},
                texture[1],
                1F);
        this.mapHeight = mapHeight; this.mapSize = mapSize; this.texture = texture;
    }
}
