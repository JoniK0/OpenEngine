package org.example.render;
import org.example.render.shader.Shader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Math;

public class LightSource {
    public Vector3f light = new Vector3f();
    public Vector3f origin = new Vector3f();
    public Transformations transform = new Transformations();
    public Vector3f lightColor = new Vector3f();
    String name;

    public LightSource(String name, float x, float y, float z, float r, float g, float b){

        this.light.x = x;
        this.light.y = y;
        this.light.z = z;

        this.origin.x = x;
        this.origin.y = y;
        this.origin.z = z;

        this.lightColor.x = r;
        this.lightColor.y = g;
        this.lightColor.z = b;

        this.name = name;
    }



    public Vector3f getLightPosition(){
        return this.light;
    }
    public Vector3f getLightColor(){
        return this.lightColor;
    }
    public String getName(){return this.name; }
    public void rotate(float angle){
        light.x = (origin.x*Math.cos(angle)) - (origin.z*Math.sin(angle));
        light.z = (origin.x*Math.sin(angle)) + (origin.z*Math.cos(angle));
    }
}
