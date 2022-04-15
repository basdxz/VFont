package com.basdxz.vfont;


import lombok.*;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;


public class AnotherAttempt extends BaseCanvasFrame {
    protected final static TTFParser ttfParser = new TTFParser();

    protected TrueTypeFont font;

    public static void main(String[] args) {
        new AnotherAttempt();
    }

    @Override
    @SneakyThrows
    protected void init() {
        setTitle("FontBox Might Save Me");
        font = ttfParser.parse("helvetica.ttf");
    }

    @Override
    @SneakyThrows
    protected void paintImpl() {
        System.out.println(font);
        val pathOfTheLetterA = font.getPath("A");
        System.out.println(pathOfTheLetterA);
        System.out.println(g);
        g.draw(pathOfTheLetterA);
    }
}
