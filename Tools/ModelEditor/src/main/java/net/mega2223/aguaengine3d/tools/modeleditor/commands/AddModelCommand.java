package net.mega2223.aguaengine3d.tools.modeleditor.commands;

import net.mega2223.aguaengine3d.tools.modeleditor.Configs;
import net.mega2223.aguaengine3d.tools.modeleditor.Console;
import net.mega2223.aguaengine3d.tools.modeleditor.ConsoleCommand;
import net.mega2223.aguaengine3d.tools.modeleditor.objects.PseudoModel;

public class AddModelCommand implements ConsoleCommand {

    protected Console context;

    public AddModelCommand(Console context){
        this.context = context;
    }

    @Override
    public int getPriority() {
        return -5;
    }

    @Override
    public boolean respondToCommand(CharSequence command) {
        if(command.toString().equalsIgnoreCase("addModel")){
            Configs.currentModel = new PseudoModel();
            context.addToHistory(Configs.currentModel + " has been created");
            return true;
        }
        return false;
    }

    @Override
    public String getAlias() {
        return "addModel";
    }

    @Override
    public String getDescription() {
        return "Creates a new model and focuses it so you can edit it.";
    }
}
