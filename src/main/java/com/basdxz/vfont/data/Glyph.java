package com.basdxz.vfont.data;

import lombok.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Getter
public class Glyph {
    protected final int xGrids;
    protected final int yGrids;
    protected final Set<QuadCurve2D> curves;
    protected final List<Set<QuadCurve2D>> subGlyphs;

    public Glyph(@NonNull Shape shape) {
        curves = newCurveSet(shape);
        val points = curveToPoints(curves);
        xGrids = xGrids(points);
        yGrids = yGrids(points);
        subGlyphs = newSubGlyphs(curves, xGrids, yGrids);
    }

    //TODO: move into tests
    public static Path2D.Float testPath(@NonNull Shape shape) {
        val path = new Path2D.Float();
        newCurveSet(shape).forEach(curve -> path.append(curve, false));
        return path;
    }

    //TODO: Implement approximation of cubic curves
    protected static Set<QuadCurve2D> newCurveSet(@NonNull Shape shape) {
        val curves = new HashSet<QuadCurve2D>();
        val pathIterator = shape.getPathIterator(normalTransform(shape));
        val coords = new float[6];
        Point2D last = new Point2D.Float();
        Point2D control;
        Point2D end;
        while (!pathIterator.isDone()) {
            val segmentType = pathIterator.currentSegment(coords);
            switch (segmentType) {
                case PathIterator.SEG_MOVETO:
                    last = new Point2D.Float(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    end = new Point2D.Float(coords[0], coords[1]);
                    curves.add(newCurve(last, end, end));
                    last = end;
                    break;
                case PathIterator.SEG_QUADTO:
                    control = new Point2D.Float(coords[0], coords[1]);
                    end = new Point2D.Float(coords[2], coords[3]);
                    curves.add(newCurve(last, control, end));
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

    protected static AffineTransform normalTransform(@NonNull Shape shape) {
        val bounds = shape.getBounds2D();
        val transform = new AffineTransform();
        transform.translate(-1,-1);
        transform.scale(2 / bounds.getWidth(), 2 / bounds.getHeight());
        transform.translate(-bounds.getX(), -bounds.getY());
        return transform;
    }

    protected static QuadCurve2D newCurve(Point2D start, Point2D control, Point2D end) {
        val curve = new QuadCurve2D.Float();
        curve.setCurve(start, control, end);
        return curve;
    }

    protected static int xGrids(@NonNull Collection<Point2D> points) {
        return grids(points, Point2D::getX);
    }

    protected static int yGrids(@NonNull Collection<Point2D> points) {
        return grids(points, Point2D::getY);
    }

    protected static int grids(@NonNull Collection<Point2D> points,
                               @NonNull Function<Point2D, Double> xyMapper) {
        return (int) (Math.round(1F / maxSortedPointGap(points, xyMapper)) + 1);
    }

    protected static Set<Point2D> curveToPoints(@NonNull Collection<QuadCurve2D> curves) {
        return curves.stream()
                .flatMap((curve) -> Stream.of(curve.getP1(), curve.getCtrlPt(), curve.getP2()))
                .collect(Collectors.toSet());
    }

    protected static double maxSortedPointGap(@NonNull Collection<Point2D> points,
                                              @NonNull Function<Point2D, Double> xyMapper) {
        val sortedPoints = points.stream()
                .map(xyMapper)
                .sorted()
                .collect(Collectors.toList());
        return IntStream.range(0, sortedPoints.size() - 1)
                .mapToObj(i -> sortedPoints.get(i + 1) - sortedPoints.get(i))
                .max(Double::compare)
                .orElse(1D);
    }

    protected static List<Set<QuadCurve2D>> newSubGlyphs(Iterable<QuadCurve2D> curves, int xGrids, int yGrids) {
        val xGridSize = 1F / xGrids;
        val yGridSize = 1F / yGrids;
        val subGlyphs = new ArrayList<Set<QuadCurve2D>>();
        subGlyphs.ensureCapacity(xGrids * yGrids);
        for (var x = 0; x < yGrids; x++) {
            for (var y = 0; y < xGrids; y++) {
                val subGlyph = new HashSet<QuadCurve2D>();
                for (val curve : curves) {
                    if (curve.intersects(x * xGridSize, y * yGridSize, xGridSize, yGridSize))
                        subGlyph.add(curve);
                }
                subGlyphs.add(subGlyph);
            }
        }
        return Collections.unmodifiableList(subGlyphs);
    }
}
