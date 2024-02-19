#version 330 core

in vec3 dir;
uniform samplerCube cubemap;

out vec4 color;

void main(){
    color = texture(cubemap,dir);
    gl_FragDepth = 0;
}