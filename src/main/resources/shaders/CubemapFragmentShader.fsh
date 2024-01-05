#version 330 core

in vec3 dir;
uniform samplerCube cubemap;
uniform float map_default_depth = .999999;
out vec4 color;

void main(){
    color = texture(cubemap,dir);
    gl_FragDepth = map_default_depth;
}