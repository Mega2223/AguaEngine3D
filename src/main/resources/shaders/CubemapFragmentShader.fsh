#version 330 core

in vec3 dir;
uniform sampler2D cubemap;

out vec4 color;

void main(){
    color = texture(cubemap,dir);
}