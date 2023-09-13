package net.mega2223.aguaengine3d.tests;

import net.mega2223.aguaengine3d.graphics.utils.ShaderDictonary;

public class ShaderDictionaryReplacerTest {

    static String teste = "--@fogFunction \n void main(){fogFunction()}";


    public static void main(String[] args) {
        ShaderDictonary dict = new ShaderDictonary();
        dict.add("fogFunction","fogfunction(){\n    equations();\n}");
        System.out.println(dict.resolve(teste));
    }

}
