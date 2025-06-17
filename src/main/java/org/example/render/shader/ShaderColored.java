package org.example.render.shader;

public class ShaderColored extends Shader{
    public ShaderColored() {super("singleColor.vert", "singleColor.frag");}

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations() {

    }
}
