package com.basdxz.vfont.data;

import lombok.*;

import java.awt.geom.QuadCurve2D;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.basdxz.vfont.data.SubGlyph.Type.*;

@Getter
public class SubGlyph {
    protected final Type type;
    protected final int[] curveIndices;

    public SubGlyph(int... indices) {
        type = Partial;
        this.curveIndices = indices;
    }

    public SubGlyph(boolean filled) {
        type = filled ? Filled : Empty;
        curveIndices = new int[0];
    }

    public enum Type {
        Empty, Filled, Partial
    }
}
