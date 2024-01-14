#version 330 core

layout (triangles) in;
layout (line_strip, max_vertices = 6) out;

in vec4 objectiveCoord[];

void main() {

    for(int i = 0; i < 4; i++){
        gl_Position = gl_in[i%3].gl_Position;
        EmitVertex();
    }
    EndPrimitive();

    vec4 average = (gl_in[0].gl_Position + gl_in[1].gl_Position + gl_in[2].gl_Position)/3F;
    gl_Position = average;
    EmitVertex();

    vec4 crossp = average;
    crossp.xyz = cross(objectiveCoord[1].xyz-objectiveCoord[0].xyz,objectiveCoord[2].xyz-objectiveCoord[0].xyz);

    gl_Position = average + normalize(crossp)*10;//average + vec4(0,6,0,0);
    gl_Position.w = average.w;
    EmitVertex();
    EndPrimitive();
}