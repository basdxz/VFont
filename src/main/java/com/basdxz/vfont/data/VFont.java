package com.basdxz.vfont.data;

import lombok.*;
import sun.font.FontStrike;
import sun.font.FontUtilities;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class VFont {
    // Performs a -1 scale on the Y to undo the scale intended for rendering to a Java window, Anti-Aliasing metric is practically ignored.
    protected static final FontRenderContext frc = new FontRenderContext(AffineTransform.getScaleInstance(1D, -1D), false, false);
    protected static Method getGlyphOutlineMethodCached;

    protected final Font font;
    protected final List<Glyph> glyphs;

    /*
        Will not work for Java 9+, also causes a fair few of the warnings ;p
    */
    @SneakyThrows
    protected static Method getGlyphOutlineMethod(@NonNull FontStrike strike) {
        if (getGlyphOutlineMethodCached == null)
            getGlyphOutlineMethodCached = strike.getClass().getDeclaredMethod("getGlyphOutline", int.class, float.class, float.class);
        getGlyphOutlineMethodCached.setAccessible(true);
        return getGlyphOutlineMethodCached;
    }

    public VFont(@NonNull Font font) {
        this.font = font;
        val f2d = FontUtilities.getFont2D(font);
        val strike = f2d.getStrike(font, frc);
        val glyphCount = f2d.getNumGlyphs();

        val tempGlyphs = new ArrayList<Glyph>();
        tempGlyphs.ensureCapacity(glyphCount);
        for (int i = 0; i < glyphCount; i++)
            tempGlyphs.add(new Glyph(glyphOutline(strike, i)));
        glyphs = Collections.unmodifiableList(tempGlyphs);
    }

    @SneakyThrows
    protected static GeneralPath glyphOutline(@NonNull FontStrike strike, int gid) {
        return (GeneralPath) getGlyphOutlineMethod(strike).invoke(strike, gid, 0, 0);
    }
}
