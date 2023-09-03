package net.mega2223.lwjgltest.aguaengine3d.logic;

public abstract class ScriptedSequence {
    final String name;
    ScriptedSequence(String name){this.name = name;}
    abstract void preLogic(int itneration, Context context);
    abstract void postLogic(int itneration, Context context);

    abstract boolean shouldTrigger(int itneration, boolean isPreLogic, Context context);
    public String getName(){return name;}
}
