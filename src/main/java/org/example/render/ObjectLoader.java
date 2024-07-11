package org.example.render;

import org.example.Main;
import org.example.WindowManager;
import org.joml.Math;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.io.*;
import java.util.List;

public class ObjectLoader {
    //private final WindowManager manager;
    //private final render renderer;

    float[] texture = {0.0f, 0.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        2.0f, 2.0f, 2.0f, 2.0f,
                        3.0f, 3.0f, 3.0f, 3.0f,
                        4.0f, 4.0f, 4.0f, 4.0f,
                        5.0f, 5.0f, 5.0f, 5.0f};


    public ObjectLoader(){
        //this.manager = Main.getWindowManager();
        //this.renderer = manager.getRender();
    }

    public Mesh createSkyBox(float length, String CubeMap)
    {

        float[] SkyBoxVertices = {
                //back
                -length/2, length/2f, length/2f,
                -length/2f, -length/2f, length/2f,
                length/2f, -length/2f, length/2f,
                length/2f,length/2f, length/2f,
                //Right side
                length/2f,length/2f, length/2f,//3
                length/2f, -length/2f, length/2f,//2
                length/2f,-length/2f,-length/2f,//6
                length/2f,length/2f,-length/2f,//7
                //left side
                -length/2f,length/2f,-length/2f,//4
                -length/2f,-length/2f,-length/2f,//5
                -length/2f, -length/2f, length/2f,//1
                -length/2f, length/2f, length/2f,//0
                //front side
                length/2f,length/2f,-length/2f,
                length/2f,-length/2f,-length/2f,
                -length/2f,-length/2f,-length/2f,
                -length/2f,length/2f,-length/2f,
                //top
                -length/2f,length/2f,-length/2f,
                -length/2f, length/2f, length/2f,
                length/2f,length/2f, length/2f,
                length/2f,length/2f,-length/2f,
                //bottom
                -length/2f, -length/2f, length/2f,
                -length/2f,-length/2f,-length/2f,
                length/2f,-length/2f,-length/2f,
                length/2f, -length/2f, length/2f,
        };
        int[] SkyBoxIndices = {
                3,1,0,
                3,2,1,
                //right
                7,5,4,
                7,6,5,
                //left
                11,9,8,
                11,10,9,
                //back
                15,13,12,
                15,14,13,
                //top
                19,17,16,
                19,18,17,
                //bottom
                23,21,20,
                23,22,21
        };
        float[] SkyBoxUVs = {
                1.0f, 2/3f,
                1.0f, 1/3f,
                0.75f, 1/3f,
                0.75f, 2/3f,

                0.75f, 2/3f,
                0.75f, 1/3f,
                0.5f, 1/3f,
                0.5f, 2/3f,

                0.25f, 2/3f,
                0.25f, 1/3f,
                0.0f, 1/3f,
                0.0f, 2/3f,

                0.5f, 2/3f,
                0.5f, 1/3f,
                0.25f, 1/3f,
                0.25f, 2/3f,

                0.25f, 2/3f,
                0.25f, 1f,
                0.5f, 1f,
                0.5f, 2/3f,

                0.25f, 0f,
                0.25f, 1/3f,
                0.5f, 1/3f,
                0.5f, 0f,
        };

        float[] cubeNormals =
                {
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,

                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,

                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,

                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,

                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,

                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                };

        float[] skyBoxTex = new float[24];

        Mesh SkyBox = MeshLoader.createMesh(SkyBoxVertices, SkyBoxUVs, SkyBoxIndices, cubeNormals, skyBoxTex).addTexture(CubeMap);
        return SkyBox;
    }


    public Mesh Sphere(float radius, float sectors, float stacks)
    {
        float x, y, z, xy;
        float s, t;
        float sectorStep = (float) (2 * Math.PI / sectors) ;
        float stackStep = (float) (Math.PI / stacks);
        float sectorAngle;
        float stackAngle;
        ArrayList<Float> VertTemp = new ArrayList<Float>();
        ArrayList<Float> uvsTemp = new ArrayList<Float>();

        for(int i = 0; i <= stacks; i++) {
        stackAngle = (float) (Math.PI / 2 - i * stackStep);
        xy = radius * Math.cos(stackAngle);
        z = radius * Math.sin(stackAngle);

            for(int j = 0; j <= sectors; j++){
                sectorAngle = j* sectorStep;

                x = xy * Math.cos(sectorAngle);
                y = xy * Math.sin(sectorAngle);
                VertTemp.add(x);
                VertTemp.add(y);
                VertTemp.add(z);

                s = (float) j / sectors;
                t = (float) i / stacks;
                uvsTemp.add(s);
                uvsTemp.add(t);
            }
        }

        float[] vertices = new float[VertTemp.size()];
        for(int k = 0; k < VertTemp.size(); k++){
            vertices[k] = VertTemp.get(k);
        }
        float[] UVs = new float[uvsTemp.size()];
        for(int k = 0; k < uvsTemp.size(); k++){
            UVs[k] = uvsTemp.get(k);
        }

        ArrayList<Integer> indices = new ArrayList<Integer>();
        ArrayList<Integer> lineIndices = new ArrayList<Integer>();

        int k1, k2;
        for(int i = 0; i < stacks; ++i)
        {
            k1 = (int) (i * (sectors + 1));     // beginning of current stack
            k2 = (int) (k1 + sectors + 1);      // beginning of next stack

            for(int j = 0; j < sectors; ++j, ++k1, ++k2)
            {
                // 2 triangles per sector excluding first and last stacks
                // k1 => k2 => k1+1
                if(i != 0)
                {
                    indices.add(k1);
                    indices.add(k2);
                    indices.add(k1 + 1);
                }

                // k1+1 => k2 => k2+1
                if(i != (stacks-1))
                {
                    indices.add(k1 + 1);
                    indices.add(k2);
                    indices.add(k2 + 1);
                }

                // store indices for lines
                // vertical lines for all stacks, k1 => k2
                lineIndices.add(k1);
                lineIndices.add(k2);
                if(i != 0)  // horizontal lines except 1st stack, k1 => k+1
                {
                    lineIndices.add(k1);
                    lineIndices.add(k1 + 1);
                }
            }
        }

        //System.out.println(indices);
        //System.out.println(lineIndices);

        int[] sphereIndices = new int[indices.size()];
        for(int k = 0; k < indices.size(); k++){
            sphereIndices[k] = indices.get(k);
        }



        float[] spheretexture = new float[vertices.length];
        Mesh Sphere = MeshLoader.createMesh(vertices,UVs,sphereIndices, vertices, spheretexture);
        return Sphere;
    }

    public Mesh createCube(float length){// method could be shortened

        float[] CubeVertices = {
                //Front
                -length/2, length/2f, length/2f,
                -length/2f, -length/2f, length/2f,
                length/2f, -length/2f, length/2f,
                length/2f,length/2f, length/2f,
                //Right side
                length/2f,length/2f, length/2f,//3
                length/2f, -length/2f, length/2f,//2
                length/2f,-length/2f,-length/2f,//6
                length/2f,length/2f,-length/2f,//7
                //left side
                -length/2f,length/2f,-length/2f,//4
                -length/2f,-length/2f,-length/2f,//5
                -length/2f, -length/2f, length/2f,//1
                -length/2f, length/2f, length/2f,//0
                //front side
                length/2f,length/2f,-length/2f,
                length/2f,-length/2f,-length/2f,
                -length/2f,-length/2f,-length/2f,
                -length/2f,length/2f,-length/2f,
                //top
                -length/2f,length/2f,-length/2f,
                -length/2f, length/2f, length/2f,
                length/2f,length/2f, length/2f,
                length/2f,length/2f,-length/2f,
                //bottom
                -length/2f, -length/2f, length/2f,
                -length/2f,-length/2f,-length/2f,
                length/2f,-length/2f,-length/2f,
                length/2f, -length/2f, length/2f,

        };
        int[] CubeIndices = {
                //front
                0,1,3,
                1,2,3,
                //right
                4,5,7,
                5,6,7,
                //left
                8,9,11,
                9,10,11,
                //back
                12,13,15,
                13,14,15,
                //top
                16,17,19,
                17,18,19,
                //bottom
                20,21,23,
                21,22,23
        };

        float[] cubeNormals =
                {
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,

                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,

                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,

                        //
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        //
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,

                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                };

        float[] CubeUVs = {
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,

                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,

                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,

                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,

                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,

                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        };

        float[] texUnit = new float[24];
        Mesh cube = MeshLoader.createMesh(CubeVertices, CubeUVs, CubeIndices, cubeNormals, texUnit);
        return cube;

    }

    public Mesh createCubeMulTex(float length, float texF1, float texF2, float texF3, float texF4, float texF5, float texF6){

        float[] CubeVertices = {
                //Front
                -length/2, length/2f, length/2f,
                -length/2f, -length/2f, length/2f,
                length/2f, -length/2f, length/2f,
                length/2f,length/2f, length/2f,
                //Right side
                length/2f,length/2f, length/2f,//3
                length/2f, -length/2f, length/2f,//2
                length/2f,-length/2f,-length/2f,//6
                length/2f,length/2f,-length/2f,//7
                //left side
                -length/2f,length/2f,-length/2f,//4
                -length/2f,-length/2f,-length/2f,//5
                -length/2f, -length/2f, length/2f,//1
                -length/2f, length/2f, length/2f,//0
                //front side
                length/2f,length/2f,-length/2f,
                length/2f,-length/2f,-length/2f,
                -length/2f,-length/2f,-length/2f,
                -length/2f,length/2f,-length/2f,
                //top
                -length/2f,length/2f,-length/2f,
                -length/2f, length/2f, length/2f,
                length/2f,length/2f, length/2f,
                length/2f,length/2f,-length/2f,
                //bottom
                -length/2f, -length/2f, length/2f,
                -length/2f,-length/2f,-length/2f,
                length/2f,-length/2f,-length/2f,
                length/2f, -length/2f, length/2f,

        };
        int[] CubeIndices = {
                //front
                0,1,3,
                1,2,3,
                //right
                4,5,7,
                5,6,7,
                //left
                8,9,11,
                9,10,11,
                //back
                12,13,15,
                13,14,15,
                //top
                16,17,19,
                17,18,19,
                //bottom
                20,21,23,
                21,22,23
        };

        float[] cubeNormals =
                {
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,

                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,

                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,

                        //
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        //
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,

                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                };

        float[] CubeUVs = {
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,

                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,

                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,

                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,

                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,

                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        };

        float[] textureUnit = {texF1, texF1, texF1, texF1,
                texF2, texF2, texF2, texF2,
                texF3, texF3, texF3, texF3,
                texF4,texF4,texF4,texF4,
                texF5,texF5,texF5,texF5,
                texF6,texF6,texF6,texF6};

        Mesh cube = MeshLoader.createMesh(CubeVertices, CubeUVs, CubeIndices, cubeNormals, textureUnit);
        return cube;

    }





}
