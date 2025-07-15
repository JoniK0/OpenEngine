layout (location = 0) in vec2 texCoords;
//res yellow
uniform sampler2D textureSampler;

out vec4 FragColor;

void main()
{
    float depthValue = texture(textureSampler, texCoords).r;
    FragColor = vec4(vec3(depthValue), 1.0);
}