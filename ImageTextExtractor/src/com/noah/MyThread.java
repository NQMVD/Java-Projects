package com.noah;

import processing.core.PApplet;

import java.awt.image.BufferedImage;

public class MyThread extends Thread {
    public Run parent;
    public BufferedImage screen, out;
    public ScreenViewer sv;
    public boolean done = false;

    MyThread(BufferedImage img, Run p) {
        this.parent = p;
        this.screen = img;
        this.sv = new ScreenViewer(this);
        start();
    }

    @Override
    public void run() {
        PApplet.runSketch(new String[]{"ScreenViewer"}, sv);
    }

    public void setDone() {
        this.parent.doneSelecting = true;
    }
}