package com.basdxz.vfont;

import lombok.*;
import org.joml.Matrix2f;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/*
    Generate translation mat based on iterations

 */
public class LineTestLmao extends JFrame {
    protected final static int FRAME_SIZE = 400;
    protected final static int POINT_THICKNESS = 5;

    public LineTestLmao() {
        setTitle("Line Draw Test");
        setSize(FRAME_SIZE, FRAME_SIZE);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new LineTestLmao();
    }

    @Override
    public void paint(Graphics gIn) {
        System.out.println("painting!");
        val g = prepareGraphics(gIn);
        drawAxisLines(g);

        val p0 = new Point2D.Float(70, 90); // Curve start
        val p1 = new Point2D.Float(15, 50);     // Curve control point
        val p2 = new Point2D.Float(80, 10);  // Curve end
        g.setColor(Color.RED);
        drawQuadBezier(g, p0, p1, p2);
        drawQuadBezierIntersections(g, p0, p1, p2);

        g.dispose();
    }

    /*
        Moves the graphics to the center and inverts the Y axis.
     */
    public static Graphics2D prepareGraphics(Graphics gIn) {
        val g = (Graphics2D) gIn;
        val frameCenter = FRAME_SIZE / 2;
        g.translate(frameCenter, frameCenter);
        g.scale(1, -1);
        return g;
    }

    public static void drawAxisLines(Graphics2D g) {
        val frameCenter = FRAME_SIZE / 2;
        drawLine(g, new Point2D.Float(frameCenter, 0), new Point2D.Float(-frameCenter, 0));
        drawLine(g, new Point2D.Float(0, frameCenter), new Point2D.Float(0, -frameCenter));
    }

    public static void drawLine(Graphics2D g, Point2D p0, Point2D p1) {
        g.drawLine((int) p0.getX(), (int) p0.getY(), (int) p1.getX(), (int) p1.getY());
    }

    public static void drawQuadBezier(Graphics2D g, Vector2f v0, Vector2f v1, Vector2f v2) {
        val curve = new Path2D.Float();
        curve.moveTo(v0.x(), v0.y());
        curve.quadTo(v1.x(), v1.y(), v2.x(), v2.y());
        g.draw(curve);
    }

    public static void drawQuadBezier(Graphics2D g, Point2D p0, Point2D p1, Point2D p2) {
        val curve = new Path2D.Float();
        curve.moveTo(p0.getX(), p0.getY());
        curve.quadTo(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        g.draw(curve);
    }
    public static void drawQuadBezierIntersections(Graphics2D g, Vector2fc p0, Vector2fc p1, Vector2fc p2) {
        drawQuadBezierIntersections(g,
                new Point2D.Float(p0.x(), p0.y()),
                new Point2D.Float(p1.x(), p1.y()),
                new Point2D.Float(p2.x(), p2.y()));
    }

    public static void drawQuadBezierIntersections(Graphics2D g, Point2D p0, Point2D p1, Point2D p2) {
        for (float t : getXAxisIntersections(p0, p1, p2))
            drawCircle(g, quadPoint(p0, p1, p2, t));

        for (float t : getYAxisIntersections(p0, p1, p2))
            drawCircle(g, quadPoint(p0, p1, p2, t));
    }

    public static float[] getXAxisIntersections(Point2D p0, Point2D p1, Point2D p2) {
        return getAxisIntersections((float) p0.getY(), (float) p1.getY(), (float) p2.getY());
    }

    public static float[] getYAxisIntersections(Point2D p0, Point2D p1, Point2D p2) {
        return getAxisIntersections((float) p0.getX(), (float) p1.getX(), (float) p2.getX());
    }

    public static float[] getAxisIntersections(float p0, float p1, float p2) {
        if (almostEqual(p0, 2.0F * p1 - p2))
            return new float[]{0.5F * (p2 - 2.0F * p1) / (p2 - p1)};

        var sqrtTerm = p1 * p1 - p0 * p2;
        if (sqrtTerm < 0.0)
            return new float[0];

        sqrtTerm = (float) Math.sqrt(sqrtTerm);
        val denom = p0 - 2.0F * p1 + p2;
        val t = new float[2];
        t[0] = (p0 - p1 + sqrtTerm) / denom;
        t[1] = (p0 - p1 - sqrtTerm) / denom;
        return t;
    }

    public static boolean almostEqual(float a, float b) {
        return Math.abs(a - b) < 1E-5;
    }

    public static void drawCircle(Graphics2D g, Point2D center) {
        drawCircle(g, center, POINT_THICKNESS);
    }

    public static void drawCircle(Graphics2D g, Point2D center, int r) {
        val x = (int) (center.getX() - (r / 2));
        val y = (int) (center.getY() - (r / 2));
        g.fillOval(x, y, r, r);
    }

    public static Point2D quadPoint(Point2D p0, Point2D p1, Point2D p2, float t) {
        return new Point2D.Float(
                quadPoint(p0.getX(), p1.getX(), p2.getX(), t),
                quadPoint(p0.getY(), p1.getY(), p2.getY(), t)
        );
    }

    public static float quadPoint(double p0, double p1, double p2, float t) {
        return (float) (Math.pow(1 - t, 2) * p0 + 2 * (1 - t) * t * p1 + Math.pow(t, 2) * p2);
    }
}
