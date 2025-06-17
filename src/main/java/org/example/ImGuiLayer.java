package org.example;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.example.render.*;
import org.example.render.Map.Map;
import org.joml.Vector3f;
import org.lwjgl.glfw.*;

import java.io.File;
import java.security.Key;
import java.util.HashMap;


public class ImGuiLayer {
    WindowManager windowmanager;
    ImInt screenWidth = new ImInt(WindowManager.width);
    ImInt screenHeight = new ImInt(WindowManager.height);
    private boolean texture_selection = false;
    private boolean normal = true;
    private boolean texture_viewer = false;
    private boolean showText = false;
    private boolean create_object = false;
    Map.object selected = null;
    LightSource selected_source;
    String selected_texture;
    int index;
    int lightIndex;

    ImFloat FOV = new ImFloat(Transformations.FOV);
    boolean remove = false;

    int removeIndex = -1;

    ImString name;
    ImFloat PosX;
    ImFloat PosY;
    ImFloat PosZ;

    ImFloat RotX;
    ImFloat RotY;
    ImFloat RotZ;

    ImString lightName;
    ImFloat ColPosX;
    ImFloat ColPosY;
    ImFloat ColPosZ;

    ImFloat cutOff;
    ImFloat targetX;
    ImFloat targetY;
    ImFloat targetZ;

    ImFloat R;
    ImFloat G;
    ImFloat B;

    ImFloat Scale;
    ImInt faceindex = new ImInt(0);
    ImFloat texScale = new ImFloat(1);
    ImFloat uOffset = new ImFloat(0);
    ImFloat vOffset = new ImFloat(0);
    ImFloat texRot = new ImFloat(0);
    private boolean showChange = false;
    private boolean showSourceChange = false;
    private boolean newSource = false;
    public static boolean polygonmode = false;
    ObjectLoader loader = new ObjectLoader();
    Camera selectedCam;
    String objName = "new0";

    boolean spotlightchange = false;


    public void imgui() {
        this.windowmanager = Main.getWindowManager();
        selectedCam = render.activeCam;
        /*
        Map.object selected = Map.objects.get(0);
        ImFloat PosX = new ImFloat(selected.x());
        ImFloat PosY = new ImFloat(selected.y());
        ImFloat PosZ = new ImFloat(selected.z());

        ImFloat RotX = new ImFloat(selected.rotX());
        ImFloat RotY = new ImFloat(selected.rotY());
        ImFloat RotZ = new ImFloat(selected.rotZ());

         */

        ImGui.begin("DevConsole");



        if(texture_selection) {
            ImGui.begin("textures");
            //File Directory = new File("src/main/java/resources/textures");
            File Directory = new File(ImGuiLayer.class.getResource("/textures").getFile());
            File[] textures = Directory.listFiles();

            /*
            for(File texture : textures){
                System.out.println(texture.getName());
            }

             */
            int numTextures = 1;
            int id;

            //System.out.println("doggo");
            for (File texture : textures) {
                if (!texture.getName().equals("models") && !texture.getName().equals("Sky")) {
                    id = Texture.loadTexture(texture.getName());
                    if (ImGui.imageButton(id, 60, 60)) {
                        selected_texture = texture.getName();
                        System.out.println(selected_texture);
                        if (selected != null) {
                            selected.element().setMulTextureAtInd(faceindex.get(), texture.getName());
                            //selected.element().addTexture(texture.getName());
                            texture_selection = false;
                        }
                    }
                    ImGui.sameLine();
                    ImGui.text(texture.getName());
                    if (numTextures % 3 != 0) {
                        ImGui.sameLine();
                    }
                    numTextures += 1;
                }
            }

            ImGui.end();
        }

        if(texture_viewer){
            ImGui.begin("textureviewer");
            ImGui.inputInt("faceindex", faceindex);


            ImGui.inputFloat("U-offset", uOffset);
            ImGui.inputFloat("V-offset", vOffset);
            ImGui.inputFloat("rotation", texRot);
            ImGui.inputFloat("scale", texScale);

            //System.out.println("doggo");

            float[] test = new float[72];
           //MeshLoader.subData(selected.element(), 2, ObjectLoader.offsetUVs(selected.element().getUvs(), uOffset.get(), vOffset.get(), texRot.get(), texScale.get()));
            //MeshLoader.subData(selected.element(), 1, test);

            if(ImGui.button("texture")){
                texture_selection = true;
            }
            ImGui.sameLine();
            if(ImGui.button("done")){
                texture_viewer = false;
            }
            ImGui.end();
        }


            ImGui.begin("objects");

                ImGui.text("");
                ImGui.text("Objects: ");
                ImGui.text("");

                for (Map.object object : Map.objects) {
                    //ImGui.text(object.name());
                    String buttonName = object.name();
                    if(buttonName.equals("")){
                        buttonName = "?";
                    }

                    //System.out.println(buttonName);

                    if(ImGui.button(buttonName)){
                        selected = object;
                        index = Map.objects.indexOf(object);

                        name = new ImString(selected.name());

                        PosX = new ImFloat(selected.x());
                        PosY = new ImFloat(selected.y());
                        PosZ = new ImFloat(selected.z());

                        RotX = new ImFloat(selected.rotX());
                        RotY = new ImFloat(selected.rotY());
                        RotZ = new ImFloat(selected.rotZ());

                        Scale = new ImFloat(selected.sizeScale());

                        showChange = true;
                    }

                }

                ImGui.text("");
                ImGui.text("Models: ");
                ImGui.text("");

                for(Map.model mod : Map.models){
                    String bname = mod.name();
                    if(bname.equals("")){
                        bname = "?";
                    }
                    if(ImGui.button(bname)){

                    }
                }


            if(showChange){
                //float[] flt = new float[1];
                //float empty = 0;
                //flt[0] = empty;

                ImGui.text("");
                ImGui.text("selected:"+selected.name());
                ImGui.text("");

                ImGui.inputText("name: ",name);

                if(ImGui.button("texture")){
                    //texture_selection = true;
                    texture_viewer = true;
                }

                ImGui.inputFloat("X",PosX);
                ImGui.inputFloat("Y",PosY);
                ImGui.inputFloat("Z",PosZ);

                //ImGui.sliderFloat("PosX", flt, -1000, 1000);

                ImGui.inputFloat("RotX", RotX);
                ImGui.inputFloat("RotY", RotY);
                ImGui.inputFloat("RotZ", RotZ);

                ImGui.inputFloat("Scale", Scale);


                Map.object newObj = new Map.object(name.get(), selected.element(), PosX.get(), PosY.get(), PosZ.get(), selected.fullbright(), RotX.get(), RotY.get(), RotZ.get(), Scale.get());
                Map.objects.set(index, newObj);




                if(ImGui.button("done")){
                    showChange = false;
                }


            }

            ImGui.text("");

        if(ImGui.button("new Object"))
        {
            create_object = true;
        }

        if(create_object)
        {
            ImGui.begin("create_object");

            selectedCam = render.activeCam;

            if(ImGui.button("Quad"))
            {
                Map.object obj = new Map.object("new", loader.createCubeMulTex(1, 0, 1, 2, 3, 4, 5).setMulTextures(new String[]{"planks.jpg", "planks.jpg", "planks.jpg", "planks.jpg", "planks.jpg", "planks.jpg"}), selectedCam.m_pos.x-selectedCam.m_target.x*3, selectedCam.m_pos.y-selectedCam.m_target.y*3, selectedCam.m_pos.z-selectedCam.m_target.z*3, false, 0, 0, 0, 1);
                Map.objects.add(obj);
            }
            if(ImGui.button("Sphere"))
            {
                Map.object obj = new Map.object("new", loader.Sphere(1, 30, 30).addTexture("white.jpg"), selectedCam.m_pos.x-selectedCam.m_target.x*3, selectedCam.m_pos.y-selectedCam.m_target.y*3, selectedCam.m_pos.z-selectedCam.m_target.z*3, false, 0, 0, 0, 1);
                Map.objects.add(obj);
            }

            if(ImGui.button("done"))
            {
                create_object = false;
            }

            ImGui.end();

        }
                ImGui.end();

            ImGui.begin("lightsources");

                ImGui.text("");
                ImGui.text("Spotlights: ");
                ImGui.text("");

                for(LightSource light : Map.lights)
                {
                    if(light instanceof  Spotlight)
                    {
                        if(ImGui.button(light.getName()))
                        {
                            remove = true;
                            selected_source = light;
                            lightIndex = Map.lights.indexOf(light);
                            System.out.println(lightIndex);

                            lightName = new ImString(selected_source.getName());

                            ColPosX = new ImFloat(selected_source.getLightPosition().x);
                            ColPosY = new ImFloat(selected_source.getLightPosition().y);
                            ColPosZ = new ImFloat(selected_source.getLightPosition().z);

                            targetX = new ImFloat(((Spotlight) selected_source).getDirection().x);
                            targetY = new ImFloat(((Spotlight) selected_source).getDirection().y);
                            targetZ = new ImFloat(((Spotlight) selected_source).getDirection().z);

                            cutOff = new ImFloat(((Spotlight) light).getCutoff());

                            R = new ImFloat(selected_source.getLightColor().x);
                            G = new ImFloat(selected_source.getLightColor().y);
                            B = new ImFloat(selected_source.getLightColor().z);

                            spotlightchange = true;
                        }

                        ImGui.sameLine();

                        if(remove)
                        {
                            if(ImGui.button("X"))
                            {
                                removeIndex = lightIndex;
                                remove = false;
                            }
                        }

                        if(spotlightchange)
                        {
                            ImGui.inputText("name: ", lightName);

                            ImGui.inputFloat("PosX", ColPosX);
                            ImGui.inputFloat("PosY", ColPosY);
                            ImGui.inputFloat("PosZ", ColPosZ);

                            ImGui.inputFloat("R",R);
                            ImGui.inputFloat("G",G);
                            ImGui.inputFloat("B",B);

                            ImGui.inputFloat("TargetX", targetX);
                            ImGui.inputFloat("TargetY", targetY);
                            ImGui.inputFloat("TargetZ", targetZ);

                            Spotlight spot = new Spotlight(lightName.get(), ColPosX.get(), ColPosY.get(), ColPosZ.get(), R.get(), G.get(), B.get(), new Vector3f(targetX.get(), targetY.get(), targetZ.get()), cutOff.get());
                            Map.lights.set(lightIndex, spot);

                            if(ImGui.button("done"))
                            {
                                spotlightchange = false;
                            }
                        }
                    }
                }

                if(removeIndex != -1)
                {
                    Map.lights.remove(removeIndex);
                    System.out.println(removeIndex);
                    removeIndex = -1;
                }


                ImGui.text("");

                for(LightSource light : Map.lights){
                    if(light instanceof Spotlight){

                    }
                    else{
                        String buttonname = light.getName();
                        if(light.getName().equals("")){
                            buttonname = "?";
                        }
                        if(ImGui.button(buttonname)){
                            selected_source = light;
                            lightIndex = Map.lights.indexOf(light);

                            lightName = new ImString(selected_source.getName());

                            ColPosX = new ImFloat(selected_source.getLightPosition().x);
                            ColPosY = new ImFloat(selected_source.getLightPosition().y);
                            ColPosZ = new ImFloat(selected_source.getLightPosition().z);

                            R = new ImFloat(selected_source.getLightColor().x);
                            G = new ImFloat(selected_source.getLightColor().y);
                            B = new ImFloat(selected_source.getLightColor().z);



                        showSourceChange = true;
                        }

                    }
                }

                if(showSourceChange){
                    ImGui.text("");
                    ImGui.text("selected:"+selected_source.getName());
                    ImGui.text("");

                    ImGui.inputText("name: ", lightName);

                    ImGui.inputFloat("PosX", ColPosX);
                    ImGui.inputFloat("PosY", ColPosY);
                    ImGui.inputFloat("PosZ", ColPosZ);

                    ImGui.inputFloat("R",R);
                    ImGui.inputFloat("G",G);
                    ImGui.inputFloat("B",B);

                    LightSource newLight = new LightSource(lightName.get(), ColPosX.get(), ColPosY.get(), ColPosZ.get(), R.get(), G.get(), B.get());
                    Map.lights.set(lightIndex, newLight);

                    if(ImGui.button("done")){
                        showSourceChange = false;
                    }
                }

                if(ImGui.button("new source"))
                {
                    newSource = true;
                }

                if(newSource)
                {
                    if(ImGui.button("Pointlight"))
                    {

                        int i = 1;
                        Boolean nameFound = false;


                            for(LightSource light : Map.lights)
                            {
                                if(light.getName() == objName)
                                {
                                    objName = objName.substring(0, objName.length()-1) + i;
                                    i++;
                                }
                            }

                        LightSource newLight = new LightSource(objName, selectedCam.m_pos.x, selectedCam.m_pos.y, selectedCam.m_pos.z, 1, 1, 1);
                        Map.lights.add(newLight);
                    }
                    if(ImGui.button("Spotlight"))
                    {
                        Vector3f target = new Vector3f(-selectedCam.m_target.x, -selectedCam.m_target.y, -selectedCam.m_target.z);
                        Spotlight newSpot = new Spotlight("new", selectedCam.m_pos.x, selectedCam.m_pos.y, selectedCam.m_pos.z, 1, 1, 1, target, 15);
                        Map.lights.add(newSpot);
                    }
                }

            ImGui.end();

            if (ImGui.button("Button")) {
                showText = true;
            }

            if(ImGui.checkbox("Normalmap", normal)){
                normal = !normal;
            }
        if(normal){
            Map.objects.get(8).element().setNormalMapAtInd(3, "Brickwall2_normal.jpg");
            Map.objects.get(8).element().setNormalMapAtInd(0, "Brickwall2_normal.jpg");
        }
        else{
            Map.objects.get(8).element().removeNormalMapAtInd(3);
            Map.objects.get(8).element().removeNormalMapAtInd(0);
            //System.out.println("what");
        }

            if (ImGui.checkbox("Fullbright", render.globalFullbright)) {
                render.globalFullbright = !render.globalFullbright;
            }
            if (ImGui.checkbox("polygonmode", polygonmode)) {
                polygonmode = !polygonmode;
            }
            if(ImGui.checkbox("Flashlight", Variables.flashlight)){
                Variables.flashlight = !Variables.flashlight;
            }


            ImGui.inputFloat("FOV:", FOV);
            Transformations.FOV = FOV.get();
            //System.out.println("haii");

            ImGui.text("Screensize");
            ImGui.inputInt("ScreenWidth: ",screenWidth);
            ImGui.inputInt("ScreenHeight", screenHeight);

            if(ImGui.button("apply")){
                WindowManager.width = screenWidth.get();
                WindowManager.height = screenHeight.get();

                GLFW.glfwSetWindowSize(windowmanager.getWindow(), screenWidth.get(), screenHeight.get());

            }





            if (showText) {
                ImGui.text("Text");
                ImGui.sameLine();
                if (ImGui.button("Stop showing text")) {
                    showText = false;
                }
            }

            ImGui.end();



    }

}