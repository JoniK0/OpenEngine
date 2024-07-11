package org.example.render;

import java.util.ArrayList;

public class Mesh {
    private int texture = 0;
    private ArrayList<Integer> multextures = new ArrayList<>();
    private boolean multex = false;
    private int vao;
    private int vertices;
    private float[] uvs;

    public Mesh(int vao, int vertex, float[] uvs){
        this.vao = vao;
        this.vertices = vertex;
        this.uvs = uvs;

    }

    public int getVaoID(){
        return vao;
    }

    public int getVertexCount(){
        return vertices;
    }

    public Mesh addTexture(String texture){
        this.texture = Texture.loadTexture(texture);
        //this.multextures.add(Texture.loadTexture(texture));
        return this;
    }

    public boolean isMultex(){return multex;}

    public Mesh addMulTextures(String texture){
        multex = true;
        this.multextures.add(Texture.loadTexture(texture));
        return this;
    }

    public Mesh addTexture(String texture, String path){
        this.texture = Texture.loadTextureAbsolutePath(texture, path);
        return this;
    }

    public int getTexture(){
        return this.texture;
    }
    public ArrayList<Integer> getMultextures(){
        return this.multextures;
    }

}


