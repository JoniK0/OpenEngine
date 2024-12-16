package org.example.render;

import org.joml.Vector3f;

public class Sun {
    Vector3f direction = new Vector3f();
    Vector3f color = new Vector3f();
    public Sun(float PosX, float PosY, float PosZ, float R, float G, float B){
        this.direction.x = -PosX;
        this.direction.y = -PosY;
        this.direction.z = -PosZ;

        this.color.x = R;
        this.color.y = G;
        this.color.z = B;
    }

    public Vector3f getDirection(){return direction;}
    public Vector3f getColor(){return this.color;}
}
