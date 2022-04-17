package com.basdxz.vfont;

import lombok.*;
import org.joml.Vector2f;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.HashMap;
import java.util.stream.IntStream;


public class ImTiredButIllGetThere extends BaseCanvasFrame {
    protected static final String ALPHABET = IntStream.rangeClosed('a', 'z').collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

    public static void main(String[] args) {
        new ImTiredButIllGetThere();
    }

    @Override
    @SneakyThrows
    protected void paintImpl() {
        val frc = new FontRenderContext(null, false, false);
        val font = Font.createFont(Font.TRUETYPE_FONT, new File("GreatVibes-Regular.ttf")).deriveFont(48F);

        val alphabetShapes = new HashMap<Character, Shape>();
        for (val c : ALPHABET.toCharArray())
            alphabetShapes.put(c, font.createGlyphVector(frc, Character.toString(c)).getOutline());

        val testString = "bababoey";
        val testGlyphVec = font.createGlyphVector(frc, testString);
        var transform = new AffineTransform();

        for (int i = 0; i < testString.length(); i++) {
            g.draw(quadConvert(transform.createTransformedShape(alphabetShapes.get(testString.charAt(i)))));
            transform.translate(testGlyphVec.getGlyphMetrics(i).getAdvanceX(), 0);
        }
    }

    protected static Shape quadConvert(Shape shape) {
        val quadPath = new Path2D.Float();
        val coords = new float[6];
        val pathIterator = shape.getPathIterator(null);
        val lastPos = new Point2D.Float();
        while (!pathIterator.isDone()) {
            val segmentType = pathIterator.currentSegment(coords);
            switch (segmentType) {
                case PathIterator.SEG_MOVETO:
                    quadPath.moveTo(coords[0], coords[1]);
                    lastPos.setLocation(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    quadPath.quadTo(coords[0], coords[1], coords[0], coords[1]);
                    lastPos.setLocation(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    quadPath.quadTo(coords[0], coords[1], coords[2], coords[3]);
                    lastPos.setLocation(coords[2], coords[3]);
                    break;
                case PathIterator.SEG_CLOSE:
                    quadPath.closePath();
                    break;
                default:
                    throw new RuntimeException("Unexpected Segment Type: " + segmentType);
            }
            pathIterator.next();
        }
        return quadPath;
    }

    // Pretty much converting a shape outline to just a shape made up of quad curves.
    protected static Shape quadConvertQuickClose(Shape shape) {
        val quadPath = new Path2D.Float();
        val coords = new float[6];
        val pathIterator = shape.getPathIterator(null);
        val lastPos = new Point2D.Float();
        while (!pathIterator.isDone()) {
            val segmentType = pathIterator.currentSegment(coords);
            switch (segmentType) {
                case PathIterator.SEG_MOVETO:
                    lastPos.setLocation(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    quadPath.moveTo(lastPos.x, lastPos.y);
                    quadPath.quadTo(coords[0], coords[1], coords[0], coords[1]);
                    quadPath.closePath();
                    lastPos.setLocation(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    quadPath.moveTo(lastPos.x, lastPos.y);
                    quadPath.quadTo(coords[0], coords[1], coords[2], coords[3]);
                    quadPath.closePath();
                    lastPos.setLocation(coords[2], coords[3]);
                    break;
                case PathIterator.SEG_CLOSE:
                    break;
                default:
                    throw new RuntimeException("Unexpected Segment Type: " + segmentType);
            }
            pathIterator.next();
        }
        return quadPath;
    }
}
