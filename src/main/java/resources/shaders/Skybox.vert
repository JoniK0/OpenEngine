
layout (location = 0) in vec3 skyPos;

uniform mat4 Projection;
uniform mat4 View;
uniform mat4 WorldTransform;

out vec3 TexCoords;

void main() {
    TexCoords = skyPos;
    gl_Position = Projection * View * vec4(skyPos.x, skyPos.y, skyPos.z , 0.0);
}