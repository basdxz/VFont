package com.basdxz.vfont.data;

import lombok.*;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.awt.*;
import java.awt.geom.AffineTransform;
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
    protected final List<Set<Curve>> subGlyphs;

    // Check what intersects into each grid
    public Glyph(@NonNull Shape shape) {
        val curves = newCurveSet(shape);
        val points = curveToPoints(curves);
        xGrids = xGrids(points);
        yGrids = yGrids(points);
        subGlyphs = newSubGlyphs(curves, xGrids, yGrids);
    }

    public static Path2D.Float testPath(@NonNull Shape shape) {
        val path = new Path2D.Float();
        for (val curve : newCurveSet(shape)) {
            path.moveTo(curve.start.x(), curve.start.y());
            path.quadTo(curve.control.x(), curve.control.y(), curve.end.x(), curve.end.y());
        }
        return path;
    }

    //TODO: Implement approximation of cubic curves
    protected static Set<Curve> newCurveSet(@NonNull Shape shape) {
        val curves = new HashSet<Curve>();
        val pathIterator = shape.getPathIterator(normalTransform(shape));
        val coords = new float[6];
        Vector2fc last = new Vector2f();
        Vector2fc control;
        Vector2fc end;
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
        return Collections.unmodifiableSet(curves);
    }

    public static AffineTransform normalTransform(@NonNull Shape shape) {
        val bounds = shape.getBounds2D();
        val transform = new AffineTransform();
        transform.scale(1 / bounds.getWidth(), 1 / bounds.getHeight());
        transform.translate(-bounds.getX(), -bounds.getY());
        return transform;
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

    protected static List<Set<Curve>> newSubGlyphs(Iterable<Curve> curves, int xGrids, int yGrids) {
        val xGridSize = 1F / xGrids;
        val yGridSize = 1F / yGrids;
        val tempSubGlyphs = new ArrayList<Set<Curve>>();
        tempSubGlyphs.ensureCapacity(xGrids * yGrids);
        for (var x = 0; x < yGrids; x++) {
            for (var y = 0; y < xGrids; y++) {
                val subGlyph = new HashSet<Curve>();
                for (val curve : curves) {
                    if (curve.intersects(x * xGridSize, y * yGridSize, xGridSize, yGridSize))
                        subGlyph.add(curve);
                }
                tempSubGlyphs.add(subGlyph);
            }
        }
        return Collections.unmodifiableList(tempSubGlyphs);
    }
}
