package com.basdxz.vfont;

import javax.swing.*;

public class TheLetterH extends JFrame {
    protected final static int FRAME_SIZE = 400;

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
}
