package com.basdxz.vfont.data;


import lombok.*;
import org.joml.Vector2fc;

import java.awt.*;
import java.awt.geom.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static lombok.AccessLevel.NONE;

@Getter
public class Curve implements Shape {
    protected final Vector2fc start;
    protected final Vector2fc control;
    protected final Vector2fc end;
    protected final List<Vector2fc> points;
    @Getter(NONE)
    protected final Path2D path;

    public Curve(Vector2fc start, Vector2fc control, Vector2fc end) {
        this.start = start;
        this.control = control;
        this.end = end;
        points = Collections.unmodifiableList(Arrays.asList(start, control, end));
        path = new Path2D.Float();
        path.moveTo(start.x(), start.y());
        path.quadTo(control.x(), control.y(), end.x(), end.y());
    }

    //region Shape Delegates
    @Override
    public Rectangle getBounds() {
        return path.getBounds();
    }

    @Override
    public Rectangle2D getBounds2D() {
        return path.getBounds2D();
    }

    @Override
    public boolean contains(double x, double y) {
        return path.contains(x, y);
    }

    @Override
    public boolean contains(Point2D p) {
        return path.contains(p);
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
        return path.intersects(x, y, w, h);
    }

    @Override
    public boolean intersects(Rectangle2D r) {
        return path.intersects(r);
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
        return path.contains(x, y, w, h);
    }

    @Override
    public boolean contains(Rectangle2D r) {
        return path.contains(r);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        return path.getPathIterator(at);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return path.getPathIterator(at, flatness);
    }
    //endregion
}
