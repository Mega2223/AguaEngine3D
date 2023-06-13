#version 330 core

uniform vec4 fragColor;
out vec4 color;

void main(){
    color = fragColor;
    color = vec4(0,1,0,0);
}