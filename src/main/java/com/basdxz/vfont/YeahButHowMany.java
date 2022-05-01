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
import java.text.MessageFormat;

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
        //val path = (Path2D) ((GeneralPath) method.invoke(strike, f2d.charToGlyph('i'), 0, 0));

        val emActual = font.createGlyphVector(frc, "C");
        //val emPath = Glyph.testPath(path);
        //g.draw(emPath.createTransformedShape(invNormalTransform(emActual.getGlyphOutline(0))));

        val glyph = new Glyph(emActual.getGlyphOutline(0));

        val otherPath = new Path2D.Float();
        glyph.curves().forEach(curve -> otherPath.append(curve, false));
        System.out.println(MessageFormat.format("const int curveCount = {0};", glyph.curves().size()));
        System.out.println(MessageFormat.format("const vec2 curves[{0}] = vec2[{0}](", glyph.curves().size() * 3));
        glyph.curves().forEach(c -> {
            System.out.println(MessageFormat.format(
                    "    vec2({0,number,0.000}, {1,number,0.000}), vec2({2,number,0.000}, {3,number,0.000}), vec2({4,number,0.000}, {5,number,0.000}),",
                    c.getX1(), c.getY1(),
                    c.getCtrlX(), c.getCtrlY(),
                    c.getX2(), c.getY2()
            ));
//
            //g.draw(c);
        });

        g.draw(otherPath.createTransformedShape(invNormalTransform(emActual.getGlyphOutline(0))));
    }

    public static AffineTransform invNormalTransform(@NonNull Shape shape) {
        val bounds = shape.getBounds2D();
        val transform = new AffineTransform();
        transform.translate(bounds.getX(), -bounds.getY());
        transform.scale(bounds.getWidth() / 2, -bounds.getHeight() / 2);
        transform.translate(1, 1);
        return transform;
    }
}
