package org.example;

import imgui.ImGui;

public class ImGuiLayer {
    private boolean showText = false;
    public void imgui(){
        ImGui.begin("test window");

        if(ImGui.button("Button")){
            showText = true;
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
