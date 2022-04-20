package com.basdxz.vfont.data;

import lombok.*;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
public class Glyph {
    protected final int xGrids;
    protected final int yGrids;
    protected final Set<Curve> curves;
    protected final List<SubGlyph> subGlyphs;

    // Check what intersects into each grid
    public Glyph(@NonNull Shape shape) {
        curves = newCurveSet(shape);
        val points = curveToPoints(curves);

        xGrids = xGrids(points);
        yGrids = yGrids(points);

        val tempSubGlyphs = new ArrayList<SubGlyph>();
        tempSubGlyphs.ensureCapacity(xGrids * yGrids);
        for (int x = 0; x < yGrids; x++)
            for (int y = 0; y < xGrids; y++)
                tempSubGlyphs.add(new SubGlyph(this, x, y));
        subGlyphs = Collections.unmodifiableList(tempSubGlyphs);
    }

    public static Path2D.Float testPath(@NonNull Shape shape) {
        val path = new Path2D.Float();
        for (val curve : newCurveSet(shape)) {
            path.moveTo(curve.start.x, curve.start.y);
            path.quadTo(curve.control.x, curve.control.y, curve.end.x, curve.end.y);
        }
        return path;
    }

    //TODO: Implement approximation of cubic curves
    protected static Set<Curve> newCurveSet(@NonNull Shape shape) {
        val curves = new HashSet<Curve>();
        val pathIterator = shape.getPathIterator(null);
        val coords = new float[6];
        var last = new Vector2f();
        Vector2f control;
        Vector2f end;
        while (!pathIterator.isDone()) {
            val segmentType = pathIterator.currentSegment(coords);
            switch (segmentType) {
                case PathIterator.SEG_MOVETO:
                    last = new Vector2f(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    end = new Vector2f(coords[0], coords[1]);
                    curves.add(new Curve(last, end, end));
                    last = end;
                    break;
                case PathIterator.SEG_QUADTO:
                    control = new Vector2f(coords[0], coords[1]);
                    end = new Vector2f(coords[2], coords[3]);
                    curves.add(new Curve(last, control, end));
                    last = end;
                    break;
                case PathIterator.SEG_CLOSE:
                    break;
                default:
                    throw new RuntimeException("Unexpected Segment Type: " + segmentType);
            }
            pathIterator.next();
        }
        return curves;
    }

    protected static int xGrids(@NonNull Collection<Vector2fc> points) {
        return grids(points, Vector2fc::x);
    }

    protected static int yGrids(@NonNull Collection<Vector2fc> points) {
        return grids(points, Vector2fc::y);
    }

    protected static int grids(@NonNull Collection<Vector2fc> points,
                               @NonNull Function<Vector2fc, Float> xyMapper) {
        return Math.round(1F / maxSortedPointGap(points, xyMapper)) + 1;
    }

    protected static Set<Vector2fc> curveToPoints(@NonNull Collection<Curve> curves) {
        return curves.stream()
                .flatMap((curve) -> curve.points().stream())
                .collect(Collectors.toSet());
    }

    protected static float maxSortedPointGap(@NonNull Collection<Vector2fc> points,
                                             @NonNull Function<Vector2fc, Float> xyMapper) {
        val sortedPoints = points.stream()
                .map(xyMapper)
                .sorted()
                .collect(Collectors.toList());

        return IntStream.range(0, sortedPoints.size() - 1)
                .mapToObj(i -> sortedPoints.get(i + 1) - sortedPoints.get(i))
                .max(Float::compare)
                .orElse(1F);
    }
}
