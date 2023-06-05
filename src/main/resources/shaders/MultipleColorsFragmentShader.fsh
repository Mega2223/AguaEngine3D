#version 330 core

in vec4 relativeCoord;
in vec4 objectiveCoord;
in vec4 fragmentColor;

out vec4 color;

void main(){
    color = fragmentColor;
}