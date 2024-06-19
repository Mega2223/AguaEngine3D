package net.mega2223.aguaengine3d.tools.modeleditor;

public interface ConsoleCommand {
    int getPriority();

    boolean respondToCommand(CharSequence command);
    String getAlias();
    String getDescription();
}
