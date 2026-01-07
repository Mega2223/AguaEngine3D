package net.mega2223.aguaengine3d;

import java.io.PrintStream;

public class AguaEngine {
    private AguaEngine(){}

    public static final String VERSION = "BETA-0.5-U";
    public static final boolean STABLE = false;

    public static PrintStream STDOUT = System.out;
    public static PrintStream STDERR = System.err;
    public static PrintStream STDDEBUG = null;

    public static PrintStream PHYSICS_OUT = null;
    public static PrintStream CONTROL_OUT = null;
    public static PrintStream RENDER_OUT = null;
    public static PrintStream SOUND_OUT = null;
}
