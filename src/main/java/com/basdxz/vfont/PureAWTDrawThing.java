package com.basdxz.vfont;

import com.basdxz.vfont.utils.AWTUtil;
import lombok.*;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;

public class PureAWTDrawThing extends BaseCanvasFrame {
    protected static float FONT_SIZE = 36F;

    public static void main(String[] args) {
        new PureAWTDrawThing();
    }

    @Override
    protected void paintImpl() {
        val font = AWTUtil.loadFont("helvetica.ttf", FONT_SIZE);

        val chars = "Bababoey".toCharArray();
        val withKerningAttrs = new HashMap<AttributedCharacterIterator.Attribute, Object>();

        withKerningAttrs.put(TextAttribute.FONT, font);
        withKerningAttrs.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);

        val withKerningFont = Font.getFont(withKerningAttrs);
        val withKerningVector = withKerningFont.layoutGlyphVector(getFontRenderContext(withKerningFont), chars, 0, chars.length, Font.LAYOUT_LEFT_TO_RIGHT);


        System.out.println(withKerningVector.getGlyphOutline(0));

        g.draw(withKerningVector.getOutline());
    }

    private static FontRenderContext getFontRenderContext(Font font) {
        return (new JPanel()).getFontMetrics(font).getFontRenderContext();
    }
}
