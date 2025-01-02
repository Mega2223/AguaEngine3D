#version 330 core

in vec4 worldCoord;
in vec4 objectiveCoord;
in vec4 fragmentNormal;

out vec4 color;

void main(){
    color = normalize(fragmentNormal) * 0.5F + 0.5F;
//    color = vec4(1,1,1,1);
    color.a = 1;
}