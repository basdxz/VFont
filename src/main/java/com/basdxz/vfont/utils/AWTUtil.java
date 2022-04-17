package com.basdxz.vfont.utils;

import lombok.*;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.io.File;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Map;

public class AWTUtil {

    @SneakyThrows
    public static Font loadFont(String pathName, float size) {
        val font = Font.createFont(Font.TRUETYPE_FONT, new File(pathName)).deriveFont(size);
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        return font;
    }

    public static int getFontCharsSpacing(Font font, char char1, char char2, Graphics2D g) {
        //val chars = new char[]{char1, char2};
        val chars = "Bababoey".toCharArray();
        val withKerningAttrs = new HashMap<AttributedCharacterIterator.Attribute, Object>();

        withKerningAttrs.put(TextAttribute.FONT, font);
        withKerningAttrs.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);

        val withKerningFont = Font.getFont(withKerningAttrs);
        val withKerningVector = withKerningFont.layoutGlyphVector(getFontRenderContext(withKerningFont), chars, 0, chars.length, Font.LAYOUT_LEFT_TO_RIGHT);


        System.out.println(withKerningVector.getGlyphOutline(0));

        g.draw(withKerningVector.getOutline());

        return withKerningVector.getGlyphLogicalBounds(1).getBounds().x;
    }

    public static int getFontCharsKerning(Font font, char char1, char char2) {
        char[] chars = new char[]{char1, char2};
        Map<AttributedCharacterIterator.Attribute, Object> withKerningAttrs = new HashMap<>();

        withKerningAttrs.put(TextAttribute.FONT, font);
        withKerningAttrs.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);

        Font withKerningFont = Font.getFont(withKerningAttrs);
        GlyphVector withKerningVector = withKerningFont.layoutGlyphVector(getFontRenderContext(withKerningFont), chars, 0, chars.length, Font.LAYOUT_LEFT_TO_RIGHT);
        int withKerningX = withKerningVector.getGlyphLogicalBounds(1).getBounds().x;

        Map<AttributedCharacterIterator.Attribute, Object> noKerningAttrs = new HashMap<>();
        noKerningAttrs.put(TextAttribute.FONT, font);
        noKerningAttrs.put(TextAttribute.KERNING, 0);

        Font noKerningFont = Font.getFont(noKerningAttrs);

        GlyphVector noKerningVector = noKerningFont.layoutGlyphVector(
                getFontRenderContext(noKerningFont), chars, 0,
                chars.length, Font.LAYOUT_LEFT_TO_RIGHT);
        int noKerningX = noKerningVector.getGlyphLogicalBounds(1).getBounds().x;
        return withKerningX - noKerningX;
    }

    private static FontRenderContext getFontRenderContext(Font font) {
        return (new JPanel()).getFontMetrics(font).getFontRenderContext();
    }
}
