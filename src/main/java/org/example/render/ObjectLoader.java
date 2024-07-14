package org.example.render;

import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;

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

        float[][] TanBitan = createTanBitan(SkyBoxVertices, SkyBoxUVs);
        float[] Tan = TanBitan[0];
        float[] Bitan = TanBitan[1];

        Mesh SkyBox = MeshLoader.createMesh(SkyBoxVertices, SkyBoxUVs, SkyBoxIndices, cubeNormals, skyBoxTex, Tan, Bitan).addTexture(CubeMap);
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

        float[][] TanBitan = createTanBitan(vertices, UVs); //probably wrong for spheres
        float[] Tan = TanBitan[0];
        float[] Bitan = TanBitan[1];

        Mesh Sphere = MeshLoader.createMesh(vertices,UVs,sphereIndices, vertices, spheretexture, Tan, Bitan);
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

        float[][] TanBitan = createTanBitan(CubeVertices, CubeUVs);
        float[] Tan = TanBitan[0];
        float[] Bitan = TanBitan[1];

        Mesh cube = MeshLoader.createMesh(CubeVertices, CubeUVs, CubeIndices, cubeNormals, texUnit, Tan, Bitan);
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

        //createTanBitan(CubeVertices, CubeUVs);

        float[][] TanBitan = createTanBitan(CubeVertices, CubeUVs);
        float[] Tan = TanBitan[0];
        float[] Bitan = TanBitan[1];

        Mesh cube = MeshLoader.createMesh(CubeVertices, CubeUVs, CubeIndices, cubeNormals, textureUnit, Tan, Bitan);
        return cube;

    }



    public Mesh createQuad(float s1, float s2, float s3, float texScale){

        float[] quadvertices = {
                //Front
                -s2/2f, s3/2f, s1/2f,
                -s2/2f, -s3/2f, s1/2f,
                s2/2f, -s3/2f, s1/2f,
                s2/2f,s3/2f, s1/2f,
                //Right side
                s2/2f,s3/2f, s1/2f,//3
                s2/2f, -s3/2f, s1/2f,//2
                s2/2f,-s3/2f,-s1/2f,//6
                s2/2f,s3/2f,-s1/2f,//7
                //left side
                -s2/2f,s3/2f,-s1/2f,//4
                -s2/2f,-s3/2f,-s1/2f,//5
                -s2/2f, -s3/2f, s1/2f,//1
                -s2/2f, s3/2f, s1/2f,//0
                //front side
                s2/2f,s3/2f,-s1/2f,
                s2/2f,-s3/2f,-s1/2f,
                -s2/2f,-s3/2f,-s1/2f,
                -s2/2f,s3/2f,-s1/2f,
                //top
                -s2/2f,s3/2f,-s1/2f,
                -s2/2f, s3/2f, s1/2f,
                s2/2f,s3/2f, s1/2f,
                s2/2f,s3/2f,-s1/2f,
                //bottom
                -s2/2f, -s3/2f, s1/2f,
                -s2/2f,-s3/2f,-s1/2f,
                s2/2f,-s3/2f,-s1/2f,
                        s2/2f, -s3/2f, s1/2f,
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

    //float texScale = 7;

    float[] CubeUVs = {
            0.0f, 1.0f*(s3/texScale),
            0.0f, 0.0f,
            1.0f*(s2/texScale), 0.0f,
            1.0f*(s2/texScale), 1.0f*(s3/texScale),

            0.0f, 1.0f*(s3/texScale),
            0.0f, 0.0f,
            1.0f*(s1/texScale), 0.0f,
            1.0f*(s1/texScale), 1.0f*(s3/texScale),

            0.0f, 1.0f*(s3/texScale),
            0.0f, 0.0f,
            1.0f*(s1/texScale), 0.0f,
            1.0f*(s1/texScale), 1.0f*(s3/texScale),

            0.0f, 1.0f*(s3/texScale),
            0.0f, 0.0f,
            1.0f*(s2/texScale), 0.0f,
            1.0f*(s2/texScale), 1.0f*(s3/texScale),

            0.0f, 1.0f*(s1/texScale),
            0.0f, 0.0f,
            1.0f*(s2/texScale), 0.0f,
            1.0f*(s2/texScale), 1.0f*(s1/texScale),

            0.0f, 1.0f*(s1/texScale),
            0.0f, 0.0f,
            1.0f*(s2/texScale), 0.0f,
            1.0f*(s2/texScale), 1.0f*(s1/texScale)
    };




        float[] trash = {
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



    float[] texunits = {0,0,0,0,
                        1,1,1,1,
                        2,2,2,2,
                        3,3,3,3,
                        4,4,4,4,
                        5,5,5,5,};


    float[][] TanBitan = createTanBitan(quadvertices, CubeUVs);
    float[] Tan = TanBitan[0];
    float[] Bitan = TanBitan[1];



    //System.out.println("Tangents: "+ Arrays.toString(Tan));
    //System.out.println("Bitangents: "+Arrays.toString(Bitan));

        Mesh Quad = MeshLoader.createMesh(quadvertices, CubeUVs, CubeIndices, cubeNormals, texunits, Tan, Bitan);
        return Quad;
    }


    public static float[][] createTanBitan(float[] pos, float[] UVs){

        float[][] result = new float[2][pos.length*3];

        float[] result_tangent = new float[pos.length];
        float[] result_bitangent = new float[pos.length];


        Vector3f tangent = new Vector3f();
        Vector3f bitangent = new Vector3f();

        for(int i = 0; i <= (pos.length / (3*4))-1; i++){
            Vector3f pos1 = new Vector3f(pos[i*12], pos[i*12+1], pos[i*12+2]);
            Vector3f pos2 = new Vector3f(pos[i*12+3],pos[i*12+4], pos[i*12+5]);
            Vector3f pos3 = new Vector3f(pos[i*12+6],pos[i*12+7], pos[i*12+8]);
            Vector3f pos4 = new Vector3f(pos[i*12+9],pos[i*12+10], pos[i*12+11]);

            Vector2f uv1 = new Vector2f(UVs[i*8], UVs[i*8+1]);
            Vector2f uv2 = new Vector2f(UVs[i*8+2], UVs[i*8+3]);
            Vector2f uv3 = new Vector2f(UVs[i*8+4], UVs[i*8+5]);
            Vector2f uv4 = new Vector2f(UVs[i*8+6], UVs[i*8+7]);


            Vector3f edge1 =new Vector3f();
            pos2.sub(pos1, edge1);


            Vector3f edge2 = new Vector3f();
            pos3.sub(pos1, edge2);

            //System.out.println("edge1: "+edge1);
            //System.out.println("edge2: "+edge2);


            Vector2f deltaUV1 = new Vector2f();
            Vector2f deltaUV2 = new Vector2f();

            uv2.sub(uv1, deltaUV1);
            uv3.sub(uv1, deltaUV2);



            float f = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y);

            //System.out.println(deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y);
            //System.out.println("f : "+f);

            tangent.x = f * (deltaUV2.y * edge1.x - deltaUV1.y * edge2.x);
            tangent.y = f * (deltaUV2.y * edge1.y - deltaUV1.y * edge2.y);
            tangent.z = f * (deltaUV2.y * edge1.z - deltaUV1.y * edge2.z);


            bitangent.x = f * (-deltaUV2.x * edge1.x + deltaUV1.x * edge2.x);
            bitangent.y = f * (-deltaUV2.x * edge1.y + deltaUV1.x * edge2.y);
            bitangent.z = f * (-deltaUV2.x * edge1.z + deltaUV1.x * edge2.z);

            result_tangent[i*4] = tangent.x;
            result_tangent[i*4+1] = tangent.y;
            result_tangent[i*4+2] = tangent.z;

            result_bitangent[i*4] = bitangent.x;
            result_bitangent[i*4+1] = bitangent.y;
            result_bitangent[i*4+2] = bitangent.z;

            tangent.normalize();
            bitangent.normalize();

            //System.out.println("tangent:" + tangent);
            //System.out.println("bitangent:"+ bitangent);

            result[0][i*12] = tangent.x;
            result[0][i*12+1] = tangent.y;
            result[0][i*12+2] = tangent.z;

            result[0][i*12+3] = tangent.x;
            result[0][i*12+4] = tangent.y;
            result[0][i*12+5] = tangent.z;

            result[0][i*12+6] = tangent.x;
            result[0][i*12+7] = tangent.y;
            result[0][i*12+8] = tangent.z;

            result[0][i*12+9] = tangent.x;
            result[0][i*12+10] = tangent.y;
            result[0][i*12+11] = tangent.z;


            result[1][i*12] = bitangent.x;
            result[1][i*12+1] = bitangent.y;
            result[1][i*12+2] = bitangent.z;

            result[1][i*12+3] = bitangent.x;
            result[1][i*12+4] = bitangent.y;
            result[1][i*12+5] = bitangent.z;

            result[1][i*12+6] = bitangent.x;
            result[1][i*12+7] = bitangent.y;
            result[1][i*12+8] = bitangent.z;

            result[1][i*12+9] = bitangent.x;
            result[1][i*12+10] = bitangent.y;
            result[1][i*12+11] = bitangent.z;

        }

        return result;


    }

    public static float[] offsetUVs(float[] uvs, float uOffset, float vOffset, float texRot, float scale){
        float[] result = new float[uvs.length];

        for(int i = 0; i < uvs.length; i++){
            if(i % 2 == 0){
                result[i] = (uvs[i]+uOffset)/scale;
            }
            else{
                result[i] = (uvs[i]+vOffset)/scale;
            }
        }

        //float[] result = new float[uvs.length];
        return result;
    }











}
