package net.mega2223.aguaengine3d.tools.modeleditor.commands.modeling;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.tools.modeleditor.Configs;
import net.mega2223.aguaengine3d.tools.modeleditor.Console;
import net.mega2223.aguaengine3d.tools.modeleditor.ConsoleCommand;
import net.mega2223.aguaengine3d.tools.modeleditor.ModelEditor;
import net.mega2223.aguaengine3d.tools.modeleditor.objects.PseudoModel;

import java.util.Arrays;
import java.util.List;

public class ModelingCommands {
    private ModelingCommands(){}

    public static List<ConsoleCommand> getCommands(Console context){
        return Arrays.asList(
                new AddModelCommand(context),
                new ListAllModels(context)
        );
    }

    static class AddModelCommand implements ConsoleCommand {

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
                ModelEditor.context.addObject(Configs.currentModel);
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

    static class ListAllModels implements ConsoleCommand{

        Console context;

        public ListAllModels(Console context) {
            this.context = context;
        }

        @Override
        public int getPriority() {
            return -10;
        }

        @Override
        public boolean respondToCommand(CharSequence command) {
            if(command.toString().equalsIgnoreCase("listModels")){
                List<Renderable> renderables = ModelEditor.context.getObjects();
                for (int i = 0; i < renderables.size(); i++) {
                    Renderable act = renderables.get(i);
                    if(act instanceof PseudoModel){
                        PseudoModel actP = (PseudoModel) act;
                        context.addToHistory(actP + " (" + (actP.isVisible() ? "visible" : "not visible") + "): " + actP.vertices.size()/4 + " vertices, " + actP.indices.size()/3 + " triangles.");
                    }
                }
                return true;
            }
            return false;
        }

        @Override
        public String getAlias() {
            return "listModels";
        }

        @Override
        public String getDescription() {
            return "Lists all loaded models.";
        }
    }
}
