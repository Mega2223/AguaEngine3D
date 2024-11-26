#version 330 core

in vec4 worldCoord;
in vec4 objectiveCoord;
in vec4 relativeCoord;

uniform vec4 color2;
uniform float radius = 1.0F;

out vec4 color;

void main(){
    color = color2;
    if(length(relativeCoord)>radius){
        color.a = 0;
    }
}