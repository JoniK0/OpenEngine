package org.example.render;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Math;

public class LightSource {
    public Vector3f light = new Vector3f();
    public Vector3f origin = new Vector3f();
    public Transformations transform = new Transformations();
    public Vector3f lightColor = new Vector3f();

    public LightSource(float x, float y, float z, float R, float G, float B){
        this.light.x = x;
        this.light.y = y;
        this.light.z = z;

        this.origin.x = x;
        this.origin.y = y;
        this.origin.z = z;

        this.lightColor.x = R;
        this.lightColor.y = G;
        this.lightColor.z = B;
    }

    public Vector3f getLightPosition(){
        return light;
    }
    public Vector3f getLightColor(){
        return lightColor;
    }
    public void rotate(float angle){
        light.x = (origin.x*Math.cos(angle)) - (origin.z*Math.sin(angle));
        light.z = (origin.x*Math.sin(angle)) + (origin.z*Math.cos(angle));
    }
}
