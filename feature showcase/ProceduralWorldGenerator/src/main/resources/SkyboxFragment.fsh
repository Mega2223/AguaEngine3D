#version 330 core

in vec3 dir;

uniform int iteration;

out vec4 color;

const float PI = 3.14159265359;

void main(){
    float r = 0, g = 0, b = 0;
    vec3 dira = dir;
    vec3 dir2 = normalize(dira);
    //vec3 sunCoord = vec3(cos(iteration/100.0F),sin(iteration/100.0F),0); // remove dps :)
    vec3 sunCoord = vec3(0,0,1);
    sunCoord = normalize(sunCoord);
    float ll = length(dir2 - sunCoord);
    r = max(0,(.033-ll*ll)/.03F);
    g = r; b = g/3;

    float k = .85;
    float i = float(iteration);
    dira.y += sin(dira.x * PI * 2 + i/1000)/4 + cos(dira.z * PI * 2 + i / 1000)/4;
    dira.x += sin(i/1200); dira.z += cos(i/600);
    vec3 dir3 = mod(dira,k);
    vec3 dir4 = 1 - abs(dir3 - k/2) / (k/2);
    //r = dir4.x; g = dir4.y; b = dir4.z;
    r = g = b = length(dir4) < k ? 1 : 0;
    color = vec4(r,g,b,1);
}