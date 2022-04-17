package com.basdxz.vfont;

import lombok.*;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import javax.swing.*;
import java.awt.*;

public abstract class BaseCanvasFrame extends JFrame {
    protected static final int FRAME_SIZE = 500;
    protected static final boolean CENTERED = true;
    //protected static final float SCALE = 0.1F;
    protected static final float SCALE = 1F;
//    protected static final boolean FLIPPED_Y = true;
    protected static final boolean FLIPPED_Y = false;
    protected static final int POINT_THICKNESS = 50;

    protected Graphics2D g;

    public BaseCanvasFrame() {
        setTitle("Kerning Sucks Ass");
        setSize(FRAME_SIZE, FRAME_SIZE);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void paint(Graphics gIn) {
        g = prepareGraphics(gIn);
        paintImpl();
        g.dispose();
    }

    protected abstract void paintImpl();

    protected Graphics2D prepareGraphics(Graphics gIn) {
        val g = (Graphics2D) gIn.create();
        val frameCenter = FRAME_SIZE / 2;
        if (CENTERED)
            g.translate(frameCenter, frameCenter);
        g.scale(SCALE, SCALE * (FLIPPED_Y ? -1 : 1));
        return g;
    }

    public void drawCircle(Vector2fc center) {
        drawCircle(center, POINT_THICKNESS);
    }

    public void drawCircle(Vector2fc center, int r) {
        val x = Math.round(center.x() - (r / 2F));
        val y = Math.round(center.y() - (r / 2F));
        g.fillOval(x, y, r, r);
    }

    public void drawXAxis(float y) {
        val frameCenter = FRAME_SIZE / SCALE / 2F;
        drawLine(new Vector2f(frameCenter, y), new Vector2f(-frameCenter, y));
    }

    public void drawYAxis(float x) {
        val frameCenter = FRAME_SIZE / SCALE / 2F;
        drawLine(new Vector2f(x, frameCenter), new Vector2f(x, -frameCenter));
    }

    public void drawLine(Vector2fc p0, Vector2fc p1) {
        g.drawLine(Math.round(p0.x()), Math.round(p0.y()), Math.round(p1.x()), Math.round(p1.y()));
    }
}
