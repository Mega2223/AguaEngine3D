#version 330 core


uniform vec3 color2;

out vec4 color;

void main(){
    color = vec4(color2.xyz,1);
}