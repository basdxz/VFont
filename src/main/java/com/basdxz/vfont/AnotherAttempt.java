package com.basdxz.vfont;


import com.basdxz.vfont.utils.AWTUtil;
import lombok.*;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;

import java.awt.geom.AffineTransform;


public class AnotherAttempt extends BaseCanvasFrame {
    protected static float DPI = 96F; // Windows 100% scaling generally sets the DPI to 96
    protected static float FONT_SIZE = 8F;

    public static void main(String[] args) {
        new AnotherAttempt();
    }

    @Override
    @SneakyThrows
    protected void paintImpl() {
//        drawString(new TTFParser().parse("tenika.ttf"), "Bababoey");

        val awtFont = AWTUtil.loadFont("helvetica.ttf", FONT_SIZE*5);

//        for (var c = 'a'; c <= 'z'; c++) {
//            System.out.println(AWTUtil.getFontCharsKerning(awtFont, (char) (c - 1), c));
//        }

        AWTUtil.getFontCharsSpacing(awtFont, 'a','a', g);
    }


    @SneakyThrows
    protected void drawString(TrueTypeFont font, String text) {
        //val kernTable = font.getKerning().getHorizontalKerningSubtable();
//
        //System.out.println(kernTable);
        //System.out.println(kernTable);
        //System.out.println(kernTable);
        //System.out.println(kernTable);

        var offset = 0;
        String lastCharacter = null;
        for (val character : text.split("")) {
            //if (lastCharacter != null)
            //    offset += emToPx(font, kernTable.getKerning(font.nameToGID(character), font.nameToGID(lastCharacter)));
            offset += drawChar(font, character, offset);
            lastCharacter = character;
        }
    }

    @SneakyThrows
    protected float drawChar(TrueTypeFont font, String charName, float offset) {
        val charPath = font.getPath(charName);
        charPath.transform(fontScaleTransform(font));
        val shift = new AffineTransform();
        shift.translate(offset, 0F);
        charPath.transform(shift);
        g.fill(charPath);
        return emToPx(font, font.getWidth(charName))*1.01F;
    }

    @SneakyThrows
    protected static AffineTransform fontScaleTransform(TrueTypeFont font) {
        val scale = emToPx(font, 1F);
        val transform = new AffineTransform();
        transform.scale(scale, scale);
        return transform;
    }

    @SneakyThrows
    protected static float emToPx(TrueTypeFont font, float em) {
        return em * FONT_SIZE * DPI / (72F * font.getUnitsPerEm());
    }
}
