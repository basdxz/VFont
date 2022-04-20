package com.basdxz.vfont;

import com.basdxz.vfont.data.Glyph;
import com.basdxz.vfont.data.VFont;
import lombok.*;
import sun.font.FontUtilities;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.io.File;

public class YeahButHowMany extends BaseCanvasFrame {
    public static void main(String[] args) {
        new YeahButHowMany();
    }

    @Override
    @SneakyThrows
    protected void paintImpl() {
        val frc = new FontRenderContext(null, false, false);
        val font = Font.createFont(Font.TRUETYPE_FONT, new File("FiraCode-Regular.ttf")).deriveFont(Font.PLAIN,128F);

        val f2d = FontUtilities.getFont2D(font);
        val strike = f2d.getStrike(font, frc);

        System.out.println(f2d.getNumGlyphs());
        System.out.println(strike.getNumGlyphs());


        val method = strike.getClass().getDeclaredMethod("getGlyphOutline", int.class, float.class, float.class);
        method.setAccessible(true);
        Path2D path = (Path2D)((GeneralPath)method.invoke(strike, f2d.charToGlyph('C'), 0, 0)).createTransformedShape(AffineTransform.getScaleInstance(1D, -1D));
        path = (Path2D)path.createTransformedShape(AffineTransform.getTranslateInstance(50,0));

        g.draw(Glyph.testPath(path));


        long startTime = System.currentTimeMillis();
        new VFont(font);
        long endTime = System.currentTimeMillis();

        System.out.println("VFont init took " + (endTime - startTime) + " ms");
    }
}
