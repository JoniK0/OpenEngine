

out vec4 out_Color;

in vec3 TexCoords;
uniform samplerCube skybox;

void main() {
    out_Color = texture(skybox, TexCoords);
}