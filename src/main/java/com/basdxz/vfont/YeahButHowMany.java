package com.basdxz.vfont;

import com.basdxz.vfont.data.Glyph;
import lombok.*;
import org.joml.Vector2f;
import sun.font.FontUtilities;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.RectangularShape;
import java.io.File;

public class YeahButHowMany extends BaseCanvasFrame {
    public static void main(String[] args) {
        new YeahButHowMany();
    }

    @Override
    @SneakyThrows
    protected void paintImpl() {
        drawXAxis(0);
        drawYAxis(0);

        val frc = new FontRenderContext(AffineTransform.getScaleInstance(1D, -1D), false, false);
        val font = Font.createFont(Font.TRUETYPE_FONT, new File("FiraCode-Regular.ttf")).deriveFont(Font.PLAIN, 128F);

        val f2d = FontUtilities.getFont2D(font);
        val strike = f2d.getStrike(font, frc);

        val method = strike.getClass().getDeclaredMethod("getGlyphOutline", int.class, float.class, float.class);
        method.setAccessible(true);
        val path = (Path2D) ((GeneralPath) method.invoke(strike, f2d.charToGlyph('M'), 0, 0));



        val emActual = font.createGlyphVector(frc, "M");
        g.draw(Glyph.testPath(path).createTransformedShape(invNormalTransform(emActual.getGlyphOutline(0))));
    }

    public static AffineTransform invNormalTransform(@NonNull Shape shape) {
        val bounds = shape.getBounds2D();
        val transform = new AffineTransform();
        transform.translate(bounds.getX(), 0);
        transform.scale(bounds.getWidth(), bounds.getHeight());
        System.out.println(bounds.getMaxY());

        return transform;
    }

}
