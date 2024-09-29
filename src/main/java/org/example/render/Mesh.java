package org.example.render;

import java.util.ArrayList;

public class Mesh {
    private int texture = 0;
    private int normalMap = 0;
    private ArrayList<Integer> multextures = new ArrayList<>();
    private ArrayList<Integer> normalMaps = new ArrayList<>();
    private boolean multex = false;
    private int vao;
    private int vertices;
    private float[] uvs;

    public Mesh(int vao, int vertex, float[] uvs){
        this.vao = vao;
        this.vertices = vertex;
        this.uvs = uvs;
        for(int i = 0; i <= 5; i++){
            this.multextures.add(0);
            this.normalMaps.add(0);
        }

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
    public Mesh addSky(String Directory){
        this.texture = Texture.loadCubemap(Directory);
        return this;
    }
    public Mesh addNormalMap(String texture){
        this.normalMap = Texture.loadTexture(texture);
        return this;
    }

    public boolean isMultex(){return multex;}
    public void setMultexFalse(){this.multex = false;}

    public Mesh addMulTextures(String texture){
        multex = true;
        this.multextures.add(Texture.loadTexture(texture));
        return this;
    }

    public Mesh setMulTextures(String[] textures){
        this.multex = true;
        ArrayList<Integer> multex = new ArrayList<>();
        for(int i=0; i < textures.length; i++){
            multex.add(Texture.loadTexture(textures[i]));
        }
        this.multextures = multex;
        return this;
    }

    public Mesh setMulTextureAtInd(int index, String texture){
        this.multex = true;
        this.multextures.set(index, Texture.loadTexture(texture));
        //System.out.println("setmultex");
        //System.out.println(multex);
        return this;
    }

    public Mesh setNormalMaps(String[] normalmaps){
        ArrayList<Integer> maps = new ArrayList<>();
        for(int i=0; i < normalmaps.length; i++){
            maps.add(Texture.loadTexture(normalmaps[i]));
        }
        this.normalMaps = maps;
        return this;
    }

    public Mesh setNormalMapAtInd(int index, String normalmap){
        this.normalMaps.set(index, Texture.loadTexture(normalmap));
        return this;
    }

    public Mesh removeNormalMapAtInd(int index){
        this.normalMaps.set(index, 0);
        return this;
    }


    public Mesh addTexture(String texture, String path){
        this.texture = Texture.loadTextureAbsolutePath(texture, path);
        return this;
    }

    public int getTexture(){
        return this.texture;
    }
    public int getNormalMap(){return this.normalMap;}
    public float[] getUvs(){return this.uvs;}
    public ArrayList<Integer> getMultextures(){
        return this.multextures;
    }
    public ArrayList<Integer> getNormalMaps(){return this.normalMaps;}

}


