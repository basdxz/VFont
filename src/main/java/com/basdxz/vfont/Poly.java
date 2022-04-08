package com.basdxz.vfont;

import lombok.*;
import org.apache.fontbox.ttf.TTFParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;

public class Poly extends JPanel {

    private final Path2D theActualLetterH;

    public static void main(String... args){
        JFrame frame = new JFrame("My First GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,300);
        //JButton button = new JButton("Press");
        //frame.getContentPane().add(button); // Adds Button to content pane of frame
        frame.add(new Poly());
        frame.setVisible(true);
    }

    @SneakyThrows
    public Poly() {
        val parser = new TTFParser();
        val font = parser.parse("GreatVibes-Regular.ttf");
        theActualLetterH = font.getPath("H");

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                System.out.println(theActualLetterH.contains(p));

                repaint();
            }
        });

    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.fill(new Shape() {
            @Override
            public Rectangle getBounds() {
                return theActualLetterH.getBounds();
            }

            @Override
            public Rectangle2D getBounds2D() {
                return theActualLetterH.getBounds2D();
            }

            @Override
            public boolean contains(double x, double y) {
                return theActualLetterH.contains(x, y);
            }

            @Override
            public boolean contains(Point2D p) {
                return theActualLetterH.contains(p);
            }

            @Override
            public boolean intersects(double x, double y, double w, double h) {
                return theActualLetterH.intersects(x, y, w, h);
            }

            @Override
            public boolean intersects(Rectangle2D r) {
                return theActualLetterH.intersects(r);
            }

            @Override
            public boolean contains(double x, double y, double w, double h) {
                return theActualLetterH.contains(x, y, w, h);
            }

            @Override
            public boolean contains(Rectangle2D r) {
                return theActualLetterH.contains(r);
            }

            @Override
            public PathIterator getPathIterator(AffineTransform at) {
                return theActualLetterH.getPathIterator(at);
            }

            @Override
            public PathIterator getPathIterator(AffineTransform at, double flatness) {
                return theActualLetterH.getPathIterator(at, flatness);
            }
        });
        g2d.dispose();

    }
}