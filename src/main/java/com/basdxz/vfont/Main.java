package com.basdxz.vfont;

import lombok.*;
import org.apache.fontbox.ttf.TTFParser;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        val parser = new TTFParser();
        val font = parser.parse("GreatVibes-Regular.ttf");
        val letterPath = font.getPath("H");

       // letterPath.
    }
}
