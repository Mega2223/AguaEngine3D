package net.mega2223.lwjgltest.aguaengine3d.logic;

public abstract class ScriptedSequence {
    final String name;
    protected ScriptedSequence(String name){this.name = name;}
    protected abstract void preLogic(int itneration, Context context);
    protected abstract void postLogic(int itneration, Context context);

    protected abstract boolean shouldTrigger(int itneration, boolean isPreLogic, Context context);
    public String getName(){return name;}
}
