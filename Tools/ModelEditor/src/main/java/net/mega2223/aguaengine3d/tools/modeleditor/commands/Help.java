package net.mega2223.aguaengine3d.tools.modeleditor.commands;

import com.sun.istack.internal.NotNull;
import net.mega2223.aguaengine3d.tools.modeleditor.Console;
import net.mega2223.aguaengine3d.tools.modeleditor.ConsoleCommand;

public class Help implements ConsoleCommand  {

    Console context;
    public Help(Console context){
        this.context = context;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean respondToCommand(CharSequence command) {
        if(command.toString().equalsIgnoreCase("help")){
            for (int i = 0; i < context.commands.size(); i++) {
                ConsoleCommand act = context.commands.get(i);
                context.addToHistory(act.getAlias() + ": " + act.getDescription());
            }
            return true;
        }
        return false;
    }

    @Override
    public String getAlias() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Lists all commands.";
    }
}
