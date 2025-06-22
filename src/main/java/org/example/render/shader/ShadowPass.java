package org.example.render.shader;

public class ShadowPass extends  Shader{
    public ShadowPass() {super ("ShadowPass.vert", "ShadowPass.frag"); }
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "aPos");
    }

    @Override
    protected void getAllUniformLocations() {

    }
}
