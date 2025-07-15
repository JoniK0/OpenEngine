layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 texCoords;

layout(location = 0) out vec2 TexCoords;

void main()
{
    TexCoords = texCoords;
    gl_Position = vec4(aPos.x, aPos.y, 0.0, 1.0);
}