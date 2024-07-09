package org.example;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.type.ImFloat;
import org.example.render.LightSource;
import org.example.render.Map.Map;
import org.example.render.render;


public class ImGuiLayer {
    private boolean showText = false;
    Map.object selected;
    int index;
    ImFloat PosX;
    ImFloat PosY;
    ImFloat PosZ;

    ImFloat RotX;
    ImFloat RotY;
    ImFloat RotZ;
    ImFloat Scale;
    private boolean showChange = false;
    public static boolean polygonmode = false;
    public void imgui() {
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


            ImGui.begin("objects");
                for (Map.object object : Map.objects) {
                    //ImGui.text(object.name());
                    if(ImGui.button(object.name())){
                        selected = object;
                        index = Map.objects.indexOf(object);

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


            if(showChange){
                //float[] flt = new float[1];
                ImGui.text("trash");
                ImGui.text("selected:"+selected.name());

                ImGui.inputFloat("X",PosX);
                ImGui.inputFloat("Y",PosY);
                ImGui.inputFloat("Z",PosZ);

                //ImGui.sliderFloat("PosX", flt, -1000, 1000);

                ImGui.inputFloat("RotX", RotX);
                ImGui.inputFloat("RotY", RotY);
                ImGui.inputFloat("RotZ", RotZ);

                ImGui.inputFloat("Scale", Scale);

                Map.object newObj = new Map.object(selected.name(), selected.element(), PosX.get(), PosY.get(), PosZ.get(), selected.fullbright(), RotX.get(), RotY.get(), RotZ.get(), Scale.get());
                Map.objects.set(index, newObj);


                if(ImGui.button("done")){
                    showChange = false;
                }
            }
                ImGui.end();

            ImGui.begin("lightsources");
                for(LightSource light : Map.lights){
                    ImGui.text(light.getName());
                }
            ImGui.end();

            if (ImGui.button("Button")) {
                showText = true;
            }

            if (ImGui.checkbox("Fullbright", render.globalFullbright)) {
                render.globalFullbright = !render.globalFullbright;
            }
            if (ImGui.checkbox("polygonmode", polygonmode)) {
                polygonmode = !polygonmode;
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