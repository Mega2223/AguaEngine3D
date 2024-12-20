package net.mega2223.aguaengine3d.graphics.utils;

import net.mega2223.aguaengine3d.misc.Utils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class ShaderDictionary {

    List<ShaderDictionaryEntry> entries =  new ArrayList<>();

    public static final String ENTRY_DIV = "--!";
    public static final String TITLE_DIV = "--:";
    public static final String REPLACE_SIGN = "//--@";

    public ShaderDictionary(){

    }

    public ShaderDictionary addAllValues(String alias, String content){
        entries.add(new ShaderDictionaryEntry(alias,content));
        return this;
    }

    public ShaderDictionary addAllValues(ShaderDictionary content){
        entries.addAll(content.entries);
        return this;
    }

    public ShaderDictionary add(ShaderDictionaryEntry content){
        entries.add(content);
        return this;
    }

    public ShaderDictionary remove(String alias){
        ArrayList<ShaderDictionaryEntry> toRemove = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            if(entries.get(i).name.equals(alias)){toRemove.add(entries.get(i));}
        }
        entries.removeAll(toRemove);
        return this;
    }

    public String resolve(String content){
        for(ShaderDictionaryEntry act : entries){
            content = content.replace(REPLACE_SIGN+act.getName(),act.getContent());
        }
        return content;
    }

    private static class ShaderDictionaryEntry{
        final String name;
        final String content;
        public ShaderDictionaryEntry(String name, String content){
            this.name = name; this.content = content;
        }
        public String getName() {return name;}
        public String getContent() {return content;}
    }

    public static ShaderDictionary fromContent(String sdcText){
        String[] lines = sdcText.split(ENTRY_DIV);
        ShaderDictionary ret = new ShaderDictionary();
        for (int l = 0; l < lines.length; l++) {
            if(lines[l].toCharArray()[0] == '#'){continue;}
            String[] split = lines[l].split(TITLE_DIV);
            if(split.length != 2){continue;}
            ret.addAllValues(split[0],split[1]);
        }
        return ret;
    }

    public static ShaderDictionary fromFile(String path){
        String content = Utils.readFile(path);
        return fromContent(content);
    }
}
