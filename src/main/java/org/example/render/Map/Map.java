package org.example.render.Map;

import org.example.render.*;

import java.util.ArrayList;

public class Map {

    public static record object(Mesh element, float x, float y, float z, boolean fullbright, float scale){ }

    ObjectLoader objectLoader = new ObjectLoader();
    MeshLoader meshLoader = new MeshLoader();

    public ArrayList<object> objects = new ArrayList<object>();
    public ArrayList<LightSource> lights = new ArrayList<>();


    //initializes static objects
    LightSource source = new LightSource(20, 8, 5, 0.0f, 0.0f, 1.0f);
    LightSource sourcetwo = new LightSource(0, 0, 0, 1.0f, 0.0f, 0.0f);

    public void initMap() {



        try {
            Mesh[] model = StaticModelLoader.load("fire_axe");
            for (Mesh mesh:model){
                objects.add(new object(mesh, 3, 3, 3, false, 0));
                if(mesh == null){
                    System.out.println("stooopid");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }






        Mesh SkyBox = objectLoader.createSkyBox(800f, "Galaxy.png");
        Mesh sphere = objectLoader.Sphere(5,50,50).addTexture("white.jpg");//
        //Mesh lightbulb = objectLoader.Sphere(0.5f, 30, 30).addTexture("white.jpg");
        Mesh pointlight = objectLoader.Sphere(0.5f, 30, 30).addTexture("white.jpg");
        Mesh Cube = objectLoader.createCube(1).addTexture("white.jpg");//
        Mesh cube = objectLoader.createCube(5).addTexture("ctexture.png");
        Mesh lightbulb = objectLoader.Sphere(0.5f, 30, 30).addTexture("white.jpg");


        //LightSource source = new LightSource(20, 8, 5, 0.0f, 0.0f, 1.0f);
        //LightSource sourcetwo = new LightSource(0, 0, 0, 1.0f, 0.0f, 0.0f);


        object Skybox = new object(SkyBox, 0, 0, 0, true, 0);
        object Sphere = new object(sphere, -2, -5, 10, false, 0);
        object secondSphere = new object(sphere, 0.0f, 0.0f, -15f, false, 0);
        object Lightbulb = new object(lightbulb, source.getLightPosition().x, source.getLightPosition().y, source.getLightPosition().z, true, 0);
        object Pointlight = new object(pointlight, sourcetwo.getLightPosition().x, sourcetwo.getLightPosition().y, sourcetwo.getLightPosition().z, true, 0);
        object Kubus = new object(Cube,-20f, 0.0f, 3.0f, false, 0);
        object kubus = new object(cube, 20f, 2f, -2f, false, 0);



        objects.add(Skybox);
        objects.add(Sphere);
        objects.add(Lightbulb);
        objects.add(Pointlight);
        objects.add(Kubus);
        objects.add(kubus);

        lights.add(source);
        lights.add(sourcetwo);
    }

    public void dynamic(){
        Mesh lightbulb = objectLoader.Sphere(0.5f, 30, 30).addTexture("white.jpg");
        object Lightbulb = new object(lightbulb, source.getLightPosition().x, source.getLightPosition().y, source.getLightPosition().z, true, 0);
        objects.add(Lightbulb);
    }

    public ArrayList<object> getObjects(){
        return objects;
    }

    public ArrayList<LightSource> getLights(){
        return lights;
    }

}
