package com.noah;

import processing.awt.ShimAWT;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.MouseEvent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class ScreenViewer extends PApplet {
    public MyThread parent;
    public PImage screenshot;
    public PVector pos1, pos2;
    public boolean isDone = false;

    public ScreenViewer(MyThread p) {
        parent = p;
        screenshot = convertSimple(p.screen);
    }

    public void setup() {
        frameRate(170);
        cursor(CROSS);
        pos1 = new PVector(-1, -1);
        pos2 = new PVector(-1, -1);
    }

    public void draw() {
        background(screenshot);

        stroke(200, 200);
        strokeWeight(3);
        fill(200, 10);
        rectMode(CORNERS);
        rect(pos1.x, pos1.y, pos2.x, pos2.y);
    }

    public void mousePressed(MouseEvent e) {
        pos1.set(e.getX(), e.getY());
        pos2.set(e.getX(), e.getY());
    }

    public void mouseReleased(MouseEvent e) {
        pos2.set(e.getX(), e.getY());
    }

    public void mouseDragged(MouseEvent e) {
        pos2.set(e.getX(), e.getY());
    }

    public void keyPressed() {
        if (keyCode == ENTER || keyCode == ' ') {
            convertOutput();
        } else if (keyCode == 'r' || keyCode == 'R') {
            pos1 = new PVector(-1, -1);
            pos2 = new PVector(-1, -1);
        }
    }

    public void settings() {
        fullScreen();
    }

    public void convertOutput() {
        PImage out = screenshot.get((int) pos1.x, (int) pos1.y, (int) (pos2.x - pos1.x), (int) (pos2.y - pos1.y));

//        new Template().saveImage(sketchPath() + "/data/screenshotI.png", output);

        parent.out = (BufferedImage) out.getNative();
        parent.setDone();
    }


    public BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        return new BufferedImage(cm, bi.copyData(null), cm.isAlphaPremultiplied(), null);
    }

    public PImage convertSimple(BufferedImage image) {
        final int imgW = image.getWidth(), imgH = image.getHeight();
        PImage resultImg = createImage(imgW, imgH, RGB);
        resultImg.loadPixels();

        for (int y = 0; y < imgH; y++)
            for (int x = 0; x < imgW; x++) {
                resultImg.pixels[x + y * imgW] = image.getRGB(x, y);
                if (x == 0 || y == 0 || x == imgW - 1 || y == imgH - 1)
                    resultImg.pixels[x + y * imgW] = color(166, 226, 44);
            }

        resultImg.updatePixels();
        return resultImg;
    }
}
