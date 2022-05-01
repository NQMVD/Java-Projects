package com.noah;

import com.aspose.ocr.AsposeOCR;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;
import java.io.IOException;





public class Run extends Template {
    public MyThread applet;
    public BufferedImage screenShot;
    public boolean doneSelecting;

    public Run() {
        super();

        int number = 10;
        int[] test = new int[20];

        test[0] = 6;

        System.out.println(test[0]);

    }










































    void test() {

//        screenShot = takeScreenshot();
        screenShot = getClipboardImage();
        if (screenShot == null)
            exit();

//        applet = new MyThread(screenShot, this);
//
//        while (!doneSelecting) applet.isAlive();

        extract(screenShot);

//        process();


//        saveImage(sketchPath() + "/data/screenshot.png", applet.out);


    }
















    public void process() {
        println("Doing stuff...");

        println("exit");
        exit();
    }

    public void extract(BufferedImage img) {
        String result = "";
//        AsposeOCR api = new AsposeOCR();
//        try {
//            result = api.RecognizePage(img);
//            println(result);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        Process p = exec("tesseract", sketchPath() + "/data/screenshot.png", sketchPath() + "/data/output");
//        try {
//            int result = p.waitFor();
////            println("the process returned " + result);
//        } catch (InterruptedException ignored) {
//        }

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(sketchPath() + "/data/tessdata");
        tesseract.setLanguage("eng");
        tesseract.setPageSegMode(1);
        tesseract.setOcrEngineMode(3);
        try {
            result = tesseract.doOCR(img);
            println(result);
        } catch (TesseractException e) {
            e.printStackTrace();
        }

//        String[] imgTextLines = loadStrings(sketchPath() + "/data/output.txt");
//        int textLength = 0;
//        String imgText = "";
//
//        if (imgTextLines != null) {
//            imgText = join(imgTextLines, '\n');
//            for (String s : imgTextLines)
//                if (s.length() > textLength)
//                    textLength = s.length();
//
//            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ignored) {
//            }
//
//            Font font = null;
//            try (InputStream is = createInput("/data/JetBrainsMono.ttf")) {
//                assert is != null;
//                font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(15f);
//            } catch (Exception ignored) {
//            }
//
//            TextAreaDemo tad = new TextAreaDemo(result, font);
//            tad.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - (textLength * 12) / 2, Toolkit.getDefaultToolkit().getScreenSize().height / 2 - (100 + imgTextLines.length * 25) / 2, textLength * 12, 100 + imgTextLines.length * 25);
//            tad.setVisible(true);
//        }

    }
}
