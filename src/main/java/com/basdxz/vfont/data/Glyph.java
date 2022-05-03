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
    protected final Shape shape;
    protected final int xGrids;
    protected final int yGrids;
    protected final List<QuadCurve2D> curves;
    protected final List<SubGlyph> subGlyphs;

    public Glyph(@NonNull Shape rawShape) {
        shape = normalizeShape(rawShape);
        curves = newCurveSet(shape);
        val points = curveToPoints(curves);
        xGrids = xGrids(points);
        yGrids = yGrids(points);
        //        xGrids = 100;
        //        yGrids = 100;
        subGlyphs = newSubGlyphs(shape, curves, xGrids, yGrids);
    }

    protected static Shape normalizeShape(@NonNull Shape shape) {
        return normalTransform(shape).createTransformedShape(shape);
    }

    protected static AffineTransform normalTransform(@NonNull Shape shape) {
        val bounds = shape.getBounds2D();
        val transform = new AffineTransform();
        transform.translate(-1, -1);
        transform.scale(2 / bounds.getWidth(), 2 / bounds.getHeight());
        transform.translate(-bounds.getX(), -bounds.getY());
        return transform;
    }

    //TODO: move into tests
    public static Path2D.Float testPath(@NonNull Shape shape) {
        val path = new Path2D.Float();
        newCurveSet(shape).forEach(curve -> path.append(curve, false));
        return path;
    }

    protected static List<QuadCurve2D> newCurveSet(@NonNull Shape shape) {
        val curves = new ArrayList<QuadCurve2D>();
        val pathIterator = shape.getPathIterator(null);
        val coords = new float[6];
        val start = new Point2D.Float();
        val control = new Point2D.Float();
        val control2 = new Point2D.Float();
        val end = new Point2D.Float();
        while (!pathIterator.isDone()) {
            val segmentType = pathIterator.currentSegment(coords);
            switch (segmentType) {
                case PathIterator.SEG_MOVETO:
                    start.setLocation(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    end.setLocation(coords[0], coords[1]);
                    curves.add(newCurve(start, end, end));
                    start.setLocation(end);
                    break;
                case PathIterator.SEG_QUADTO:
                    control.setLocation(coords[0], coords[1]);
                    end.setLocation(coords[2], coords[3]);
                    curves.add(newCurve(start, control, end));
                    start.setLocation(end);
                    break;
                case PathIterator.SEG_CUBICTO:
                    control.setLocation(coords[0], coords[1]);
                    control2.setLocation(coords[2], coords[3]);
                    end.setLocation(coords[4], coords[5]);
                    control.setLocation(
                            -0.25F * start.x + .75 * control.x + .75 * control2.x - 0.25 * end.x,
                            -0.25F * start.y + .75 * control.y + .75 * control2.y - 0.25 * end.y);
                    curves.add(newCurve(start, control, end));
                    start.setLocation(end);
                    break;
                case PathIterator.SEG_CLOSE:
                    break;
                default:
                    throw new RuntimeException("Unexpected Segment Type: " + segmentType);
            }
            pathIterator.next();
        }
        return Collections.unmodifiableList(curves);
    }

    protected static QuadCurve2D newCurve(@NonNull Point2D start, @NonNull Point2D control, @NonNull Point2D end) {
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

    protected static List<SubGlyph> newSubGlyphs(@NonNull Shape shape, @NonNull List<QuadCurve2D> curves,
                                                 int xGrids, int yGrids) {
        val xSize = 2F / xGrids; // Width of a unit circle is 2F
        val ySize = 2F / yGrids;
        val subGlyphs = new ArrayList<SubGlyph>();
        subGlyphs.ensureCapacity(xGrids * yGrids);
        for (var y = 0; y < yGrids; y++)
            for (var x = 0; x < xGrids; x++)
                subGlyphs.add(newSubGlyph(shape, curves, (x * xSize) - 1F, (y * ySize) - 1F, xSize, ySize));
        return Collections.unmodifiableList(subGlyphs);
    }

    protected static SubGlyph newSubGlyph(@NonNull Shape shape, @NonNull List<QuadCurve2D> curves,
                                          float xPos, float yPos, float xSize, float ySize) {
        if (shape.intersects(xPos, yPos, xSize, ySize)) {
            if (shape.contains(xPos, yPos, xSize, ySize))
                return new SubGlyph(true);
            return new SubGlyph(IntStream
                    .range(0, curves.size())
                    .filter(i -> curves.get(i).intersects(xPos, yPos, xSize, ySize))
                    .toArray());
        }
        return new SubGlyph(false);
    }
}
