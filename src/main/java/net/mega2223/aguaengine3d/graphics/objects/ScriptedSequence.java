package net.mega2223.aguaengine3d.graphics.objects;

public abstract class ScriptedSequence {
    final String name;
    protected ScriptedSequence(String name){this.name = name;}
    protected void preLogic(int iteration, RenderingContext context){}
    protected void postLogic(int iteration, RenderingContext context){}
    public String getName(){return name;}
}
