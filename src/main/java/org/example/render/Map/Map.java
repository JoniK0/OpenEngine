package org.example.render.Map;

import org.example.render.*;

import java.util.ArrayList;

public class Map {

    public record object(String name, Mesh element, float x, float y, float z, boolean fullbright, float rotX, float rotY, float rotZ, float sizeScale){ }

    ObjectLoader objectLoader = new ObjectLoader();
    MeshLoader meshLoader = new MeshLoader();

    public static ArrayList<object> objects = new ArrayList<object>();
    public static ArrayList<LightSource> lights = new ArrayList<>();


    //initializes static objects
    LightSource source = new LightSource("source",20, 8, 5, 0.0f, 0.0f, 1.0f);
    LightSource sourcetwo = new LightSource("source2",0, 0, 0, 1.0f, 0.0f, 0.0f);

    public void initMap() {



        try {
            Mesh[] model = StaticModelLoader.load("fire_axe");
            int i = 0;
            for (Mesh mesh:model){
                objects.add(new object("fireaxe", mesh, 10, 1, 10, false, 0, 0, 0, 0.05f));
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


        object Skybox = new object("SkyBox",SkyBox, 0, 0, 0, true, 0, 0, 0, 1);
        object Sphere = new object("sphere",sphere, -2, -5, 10, false, 0, 0,0, 1);
        object secondSphere = new object("sphere2",sphere, 0.0f, 0.0f, -15f, false, 0,0,0,1);
        object Lightbulb = new object("lightbulb",lightbulb, source.getLightPosition().x, source.getLightPosition().y, source.getLightPosition().z, true, 0,0,0, 1);
        object Pointlight = new object("pointlight",pointlight, sourcetwo.getLightPosition().x, sourcetwo.getLightPosition().y, sourcetwo.getLightPosition().z, true, 0,0,0, 1);
        object Kubus = new object("Cube",Cube,-20f, 0.0f, 3.0f, false, 0,0,0, 1);
        object kubus = new object("Cube2",cube, 20f, 2f, -2f, false, 0,0,0, 1);



        objects.add(secondSphere);
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
        object Lightbulb = new object("light",lightbulb, source.getLightPosition().x, source.getLightPosition().y, source.getLightPosition().z, true, 0,0,0, 1);
        objects.add(Lightbulb);
    }

    public ArrayList<object> getObjects(){
        return objects;
    }

    public ArrayList<LightSource> getLights(){
        return lights;
    }

}
