package org.example.render.shader;

public class ShaderTextured extends Shader{
    public ShaderTextured() {
        super("Textured.vert", "Textured.frag");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

    @Override
    protected void getAllUniformLocations() {}
}
