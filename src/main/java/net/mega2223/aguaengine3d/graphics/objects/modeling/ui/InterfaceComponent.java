package net.mega2223.aguaengine3d.graphics.objects.modeling.ui;

public interface InterfaceComponent {
    int CENTER_ALIGMENT = 0, BOTTOM_LEFT_ALIGMENT = 1,
            BOTTOM_RIGHT_ALIGMENT = 2, TOP_LEFT_ALIGMENT = 3,
            TOP_RIGHT_ALIGMENT = 4;

    float getAspectRatio();
    void setAspectRatio(float aspectRatio);

    void setRenderOrderPosition(int pos);

    float[] getTextureCoords();
    default void setScale(float f){setScale(f,f,f);}

    void setScale(float x,float y,float z);

    int getAligment();
    void setAligment(int aligment);
}
