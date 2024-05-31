package org.example;

import imgui.ImGui;
import org.example.render.render;


public class ImGuiLayer {
    private boolean showText = false;
    public static boolean polygonmode = false;
    public void imgui(){
        ImGui.begin("test window");

        if(ImGui.button("Button")){
            showText = true;
        }

        if(ImGui.checkbox("Fullbright", render.globalFullbright)){
            render.globalFullbright = !render.globalFullbright;
        }
        if(ImGui.checkbox("polygonmode", polygonmode)){
            polygonmode = !polygonmode;
        }

        if(showText){
            ImGui.text("Text");
            ImGui.sameLine();
            if(ImGui.button("Stop showing text")){
                showText = false;
            }
        }

        ImGui.end();
    }
}