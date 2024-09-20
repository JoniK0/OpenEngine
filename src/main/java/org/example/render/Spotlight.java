package org.example.render;

import org.joml.Vector3f;

public class Spotlight extends LightSource{
    float cutoff;
    Vector3f direction = new Vector3f();
    public Spotlight(String name, float x, float y, float z, float r, float g, float b, Vector3f direction, float cutoff) {
        super(name, x, y, z, r, g, b);
        this.direction = direction;
        this.cutoff = cutoff;
    }

    public Vector3f getDirection(){return this.direction;}
    public float getCutoff(){return this.cutoff;}

}
