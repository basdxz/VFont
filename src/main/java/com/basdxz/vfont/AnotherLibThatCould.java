package com.basdxz.vfont;

import com.basdxz.vfont.utils.AWTUtil;
import lombok.*;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;

public class AnotherLibThatCould extends BaseCanvasFrame {
    protected static float FONT_SIZE = 36F;

    public static void main(String[] args) {
        new AnotherLibThatCould();
    }

    @Override
    @SneakyThrows
    protected void paintImpl() {
        String text = "The quick brown fox jumped over the lazy dog";
        Font font = AWTUtil.loadFont("helvetica.ttf", 16);
        g.setFont(font);

        var x = -150;
        var y = -100;

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g.drawString(text, x, y += 30);

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.drawString(text, x, y += 30);

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g.drawString(text, x, y += 30);

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g.drawString(text, x, y += 30);

        val glyph = font.createGlyphVector(new FontRenderContext(null, true, false), text);
        g.fill(glyph.getOutline(x, y += 30));
    }
}
