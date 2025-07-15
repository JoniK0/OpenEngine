package org.example;

public class Main {
    private static WindowManager window;
    public static void main(String[] args) {
        System.out.println("Hello world!");
        window = new WindowManager(new ImGuiLayer());
        window.run();
    }

    public static WindowManager getWindowManager() {
        return window;
    }
}