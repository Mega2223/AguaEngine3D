package net.mega2223.aguaengine3d.generictests;

import net.mega2223.aguaengine3d.graphics.utils.ShaderDictionary;

public class ShaderDictionaryReplacerTest {

    static String teste = "--@fogFunction \n void main(){fogFunction()}";


    public static void main(String[] args) {
        ShaderDictionary dict = new ShaderDictionary();
        dict.addAllValues("fogFunction","fogfunction(){\n    equations();\n}");
        System.out.println(dict.resolve(teste));
    }

}
