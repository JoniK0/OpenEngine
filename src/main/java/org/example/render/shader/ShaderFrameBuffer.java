package org.example.render.shader;

public class ShaderFrameBuffer extends Shader{

    public ShaderFrameBuffer(){super ("ShaderFrameBuffer.vert", "ShaderFrameBuffer.frag");}

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "aPos");
        super.bindAttribute(1, "texCoords");
    }

    @Override
    protected void getAllUniformLocations() {

    }
}
