package org.example.render.shader;

public class ShaderSky extends  Shader{
    public ShaderSky() {
        super("Skybox.vert", "Skybox.frag");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "skyPos");
        //super.bindAttribute(1, "textureCoords");
    }

    @Override
    protected void getAllUniformLocations() {}
}
