package com.basdxz.vfont;

import com.basdxz.vfont.data.Glyph;
import lombok.*;
import sun.font.FontUtilities;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.io.File;

public class GridTest extends BaseCanvasFrame {
    protected static final int GRID_SIZE = 100;
    protected static final int SQUARE_SIZE = 3;

    public static void main(String[] args) {
        new GridTest();
    }

    @Override
    @SneakyThrows
    protected void paintImpl() {
        val frc = new FontRenderContext(AffineTransform.getScaleInstance(1D, -1D), false, false);
        val font = Font.createFont(Font.TRUETYPE_FONT, new File("FiraCode-Regular.ttf")).deriveFont(Font.PLAIN, 128F);

        val f2d = FontUtilities.getFont2D(font);
        val strike = f2d.getStrike(font, frc);

        val method = strike.getClass().getDeclaredMethod("getGlyphOutline", int.class, float.class, float.class);
        method.setAccessible(true);
        val path = (Path2D) method.invoke(strike, f2d.charToGlyph('C'), 0, 0);

        val glyph = new Glyph(path);
        val array = glyph.subGlyphs().stream().mapToInt((x) -> {
            switch (x.type()) {
                case Filled:
                    return -2;
                case Empty:
                    return -1;
                default:
                    return 0;
            }
        }).toArray();

        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                var index = array[x + (y * GRID_SIZE)];
                switch (index) {
                    case -1:
                        g.setColor(Color.GREEN);
                        break;
                    case -2:
                        g.setColor(Color.CYAN);
                        break;
                    default:
                        g.setColor(Color.RED);
                }
                g.fill(new Rectangle(
                        (x * SQUARE_SIZE) - ((GRID_SIZE / 2) * SQUARE_SIZE),
                        (y * SQUARE_SIZE) - ((GRID_SIZE / 2) * SQUARE_SIZE),
                        SQUARE_SIZE,
                        SQUARE_SIZE));
                System.out.print(index + ", ");
            }
            System.out.println();
        }
        System.out.println("\ncut here\n");
    }
}
