package com.basdxz.vfont;

import lombok.*;
import org.apache.fontbox.ttf.TTFParser;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TheLetterH extends JFrame {
    protected final static int FRAME_SIZE = 500;
    protected final static boolean CENTERED = true;
    protected final static float SCALE = 0.1F;
    protected final static boolean FLIPPED_Y = true;

    protected final static int POINT_THICKNESS = 50;

    public TheLetterH() {
        setTitle("The Letter H");
        setSize(FRAME_SIZE, FRAME_SIZE);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new TheLetterH();
    }

    @Override
    @SneakyThrows
    public void paint(Graphics gIn) {
        val g = prepareGraphics(gIn);

        g.setColor(Color.RED);
        drawCircle(g, new Vector2f(0F, 0F));

        g.setColor(Color.BLACK);
        val parser = new TTFParser();
        val font = parser.parse("helvetica.ttf");

        val theActualLetterH = font.getPath("a");
        g.draw(theActualLetterH);

        g.setColor(Color.BLUE);
        val shapePoints = dumpShapePoints(theActualLetterH);
        shapePoints.forEach((p) -> drawCircle(g, p));

        val bounds = theActualLetterH.getBounds();
        g.setColor(Color.CYAN);

//        drawCircle(g, new Vector2f(bounds.x, bounds.y));
//        drawCircle(g, new Vector2f(bounds.x + bounds.width, bounds.y + bounds.height));

        val xGrids = Math.round(bounds.width / maxSortedPointGap(shapePoints, Vector2fc::x)) + 1;
        val xGridSize = (bounds.width + Math.abs(bounds.x * 2)) / xGrids;

        for (int i = 0; i <= xGrids; i++)
            drawYAxis(g, xGridSize * i);

        val yGrids = Math.round(bounds.height / maxSortedPointGap(shapePoints, Vector2fc::y)) + 1;
        val yGridSize = (bounds.height + Math.abs(bounds.y * 2)) / yGrids;

        for (int i = 0; i <= yGrids; i++)
            drawXAxis(g, (yGridSize * i));

        g.dispose();
    }

    protected static Graphics2D prepareGraphics(Graphics gIn) {
        val g = (Graphics2D) gIn.create();
        val frameCenter = FRAME_SIZE / 2;
        if (CENTERED)
            g.translate(frameCenter, frameCenter);
        g.scale(SCALE, SCALE * (FLIPPED_Y ? -1 : 1));
        return g;
    }

    protected static List<Vector2fc> dumpShapePoints(Shape shape) {
        val points = new ArrayList<Vector2fc>();
        val coords = new float[6];
        val pathIterator = shape.getPathIterator(null);
        while (!pathIterator.isDone()) {
            switch (pathIterator.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO:
                    points.add(new Vector2f(coords[0], coords[1]));
//                    System.out.printf("move to x1=%f, y1=%f\n",
//                            coords[0], coords[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    points.add(new Vector2f(coords[0], coords[1]));
//                    System.out.printf("line to x1=%f, y1=%f\n",
//                            coords[0], coords[1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    points.add(new Vector2f(coords[0], coords[1]));
                    points.add(new Vector2f(coords[2], coords[3]));
//                    System.out.printf("quad to x1=%f, y1=%f, x2=%f, y2=%f\n",
//                            coords[0], coords[1], coords[2], coords[3]);
                    break;
                case PathIterator.SEG_CUBICTO:
                    points.add(new Vector2f(coords[0], coords[1]));
                    points.add(new Vector2f(coords[2], coords[3]));
                    points.add(new Vector2f(coords[4], coords[5]));
//                    System.out.printf("cubic to x1=%f, y1=%f, x2=%f, y2=%f, x3=%f, y3=%f\n",
//                            coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
                    break;
                case PathIterator.SEG_CLOSE:
//                    System.out.printf("close\n");
                    break;
            }
            pathIterator.next();
        }
        return points;
    }

    public static void drawCircle(Graphics2D g, Vector2fc center) {
        drawCircle(g, center, POINT_THICKNESS);
    }

    public static void drawCircle(Graphics2D g, Vector2fc center, int r) {
        val x = Math.round(center.x() - (r / 2F));
        val y = Math.round(center.y() - (r / 2F));
        g.fillOval(x, y, r, r);
    }

    public static float maxSortedPointGap(List<Vector2fc> points, Function<Vector2fc, Float> xyDimMapper) {
        val xSortedPoints = points.stream()
                .map(xyDimMapper)
                .sorted()
                .collect(Collectors.toList());

        return IntStream.range(0, xSortedPoints.size() - 1)
                .mapToObj(i -> xSortedPoints.get(i + 1) - xSortedPoints.get(i))
                .max(Float::compare)
                .orElse(0F);
    }

    public static void drawXAxis(Graphics2D g, float y) {
        val frameCenter = FRAME_SIZE / SCALE / 2F;
        drawLine(g, new Vector2f(frameCenter, y), new Vector2f(-frameCenter, y));
    }

    public static void drawYAxis(Graphics2D g, float x) {
        val frameCenter = FRAME_SIZE / SCALE / 2F;
        drawLine(g, new Vector2f(x, frameCenter), new Vector2f(x, -frameCenter));
    }

    public static void drawLine(Graphics2D g, Vector2fc p0, Vector2fc p1) {
        g.drawLine(Math.round(p0.x()), Math.round(p0.y()), Math.round(p1.x()), Math.round(p1.y()));
    }
}
