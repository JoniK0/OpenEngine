package org.example.render;

public class Variables {
    static ObjectLoader loader = new ObjectLoader();
    public static boolean flashlight = false;
    public static int shadowWidth = 1024;
    public static int shadowHeight = 1024;
    public static Mesh screenQuad = loader.screenQuad();
    public static boolean depthmap = false;
}
