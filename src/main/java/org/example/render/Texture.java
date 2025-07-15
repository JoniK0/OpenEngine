package org.example.render;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

public class Texture {

    public static HashMap<String, Integer> idMap = new HashMap<String, Integer>();

    public static int loadCubemap(String Directory) {
        int id;
        int width;
        int height;
        id = GL11.glGenTextures();
        GL11.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, id);

        String[] faces = {"right.jpg", "left.jpg", "top.jpg", "bottom.jpg", "front.jpg", "back.jpg"};

        for (int i = 0; i < faces.length; i++) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer channels = stack.mallocInt(1);
                ByteBuffer buffer;

                InputStream stream = Texture.class.getResourceAsStream("/textures/" + Directory + "/" + faces[i]);
                byte[] streamByte = stream.readAllBytes();
                ByteBuffer streamByteBuffer = BufferUtils.createByteBuffer(streamByte.length);
                streamByteBuffer.put(streamByte);
                streamByteBuffer.flip();

                buffer = STBImage.stbi_load_from_memory(streamByteBuffer, w, h, channels, 4);

                if (buffer == null) {
                    throw new Exception("Can't load file: " + faces[i] + " " + STBImage.stbi_failure_reason());
                }
                width = w.get();
                height = h.get();

                GL11.glTexImage2D(GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

                GL11.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE);
                GL11.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE);
                GL11.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP, GL20.GL_TEXTURE_WRAP_R, GL20.GL_CLAMP_TO_EDGE);

                STBImage.stbi_image_free(buffer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return id;
    }

    public static int loadTexture(String texture) {
        int width;
        int height;
        ByteBuffer buffer;

        try (MemoryStack stack = MemoryStack.stackPush()) {

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (idMap.containsKey(texture)) {
            return idMap.get(texture);
        } else {

            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer channels = stack.mallocInt(1);

                File file = new File(Texture.class.getResource("/textures/" + texture).getFile());
                String path = Texture.class.getResource("/textures/" + texture).getPath();

                InputStream stream = Texture.class.getResourceAsStream("/textures/" + texture);
                byte[] streamByte = stream.readAllBytes();
                ByteBuffer streamByteBuffer = BufferUtils.createByteBuffer(streamByte.length);
                streamByteBuffer.put(streamByte);
                streamByteBuffer.flip();

                buffer = STBImage.stbi_load_from_memory(streamByteBuffer, w, h, channels, 4);

                if (buffer == null) {
                    throw new Exception("Can't load file: " + texture + " " + STBImage.stbi_failure_reason());
                }
                width = w.get();
                height = h.get();

                int id = GL11.glGenTextures();
                idMap.put(texture, id);

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
                GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
                GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR_MIPMAP_LINEAR);
                GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);

                STBImage.stbi_image_free(buffer);
                System.out.println("Texture initialized " + id);
                return id;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }

    }

    public static int loadTextureAbsolutePath(String texture, String path) {
        int width;
        int height;
        ByteBuffer buffer;

        try (MemoryStack stack = MemoryStack.stackPush()) {

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (idMap.containsKey(texture)) {
            return idMap.get(texture);
        } else {

            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer channels = stack.mallocInt(1);

                String filepath;
                filepath = Texture.class.getResource(path + "/" + texture).getPath();

                buffer = STBImage.stbi_load(filepath, w, h, channels, 4);
                if (buffer == null) {
                    throw new Exception("Can't load file" + texture + " " + STBImage.stbi_failure_reason());
                }
                width = w.get();
                height = h.get();

                int id = GL11.glGenTextures();
                idMap.put(texture, id);

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

                GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
                STBImage.stbi_image_free(buffer);
                System.out.println("Texture initialized " + id);
                return id;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }
    }


}
