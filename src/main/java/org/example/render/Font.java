package org.example.render;


import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.Library;
import org.lwjgl.system.SharedLibrary;
import org.lwjgl.util.freetype.FT_Memory;
import org.lwjgl.util.freetype.FreeType;

import org.lwjgl.util.harfbuzz.*;

import java.awt.font.FontRenderContext;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.util.freetype.FreeType.*;
import org.lwjgl.system.Configuration;

public class Font {
    public void initFont(){

        SharedLibrary library = FreeType.getLibrary();
        LongBuffer buffer = BufferUtils.createLongBuffer(1);
        buffer.put(library.address());
        FT_Memory memory = FT_Memory.create();
        PointerBuffer pointerBuffer = PointerBuffer.allocateDirect(10);
        pointerBuffer.put(buffer);

        FT_New_Library(memory, pointerBuffer);

        if(FT_Init_FreeType(pointerBuffer) != 0){
            System.out.println("couldnt load Freetype");
        }


    }
}
