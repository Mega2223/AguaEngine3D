package net.mega2223.aguaengine3d.logic;

public abstract class ScriptedSequence {
    final String name;
    protected ScriptedSequence(String name){this.name = name;}
    protected void preLogic(int itneration, Context context){}
    protected void postLogic(int itneration, Context context){}
    public String getName(){return name;}
}