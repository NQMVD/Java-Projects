package com.noah;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class Template {

    public String sketchPath;

    public final boolean DEBUG = false;

    public final double PI = Math.PI;
    public final double HALF_PI = (Math.PI / 2.0);
    public final double THIRD_PI = (Math.PI / 3.0);
    public final double QUARTER_PI = (Math.PI / 4.0);
    public final double TWO_PI = (2.0 * Math.PI);
    public final double TAU = (2.0 * Math.PI);
    public final double DEG_TO_RAD = PI / 180.0f;
    public final double RAD_TO_DEG = 180.0f / PI;

    public final double SINCOS_PRECISION = 0.5;
    public final int SINCOS_LENGTH = (int) (360 / SINCOS_PRECISION);


    public double[] sinLUT;
    public double[] cosLUT;

    public void generateSinCos() {
        sinLUT = new double[SINCOS_LENGTH];
        cosLUT = new double[SINCOS_LENGTH];
        for (int i = 0; i < SINCOS_LENGTH; i++) {
            sinLUT[i] = Math.sin(i * DEG_TO_RAD * SINCOS_PRECISION);
            cosLUT[i] = Math.cos(i * DEG_TO_RAD * SINCOS_PRECISION);
        }
    }


    //////////////////// TIME AND DATE ////////////////////

    public long millisOffset = System.currentTimeMillis();

    public int millis() {
        return (int) (System.currentTimeMillis() - millisOffset);
    }

    public int second() {
        return Calendar.getInstance().get(Calendar.SECOND);
    }

    public int minute() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    public int hour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public int day() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public int month() {
        // months are number 0..11 so change to colloquial 1..12
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public int year() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }


    //////////////////// LAUNCH AND EXEC AND SHELL ////////////////////


    /**
     * Attempts to open an application or file using your platform's launcher. The
     * filename parameter is a String specifying the file name and
     * location. The location parameter must be a full path name, or the name of
     * an executable in the system's PATH. In most cases, using a full path is the
     * best option, rather than relying on the system PATH. Be sure to make the
     * file executable before attempting to open it (chmod +x).
     * <p>
     * If you're trying to run command line functions
     * directly, use the exec() function instead (see below).
     */
    public Process launch(String... args) {
        String[] params = null;
        params = new String[]{"cmd", "/c"};

        if (params != null) {
            if (params[0].equals(args[0])) {
                return exec(args);
            } else {
                params = concat(params, args);
                return exec(params);
            }
        } else {
            return exec(args);
        }
    }

    /**
     * Pass a set of arguments directly to the command line.
     * <p>
     * exec("/usr/bin/say", "welcome to the command line");
     * <p>
     * Or if you want to wait until it's completed, something like this:
     * <p>
     * Process p = exec("/usr/bin/say", "waiting until done");
     * try {
     * int result = p.waitFor();
     * println("the process returned " + result);
     * } catch (InterruptedException e) { }
     */
    public Process exec(String... args) {
        try {
            return Runtime.getRuntime().exec(args);
        } catch (Exception e) {
            throw new RuntimeException("Exception while attempting " + join(args, ' '), e);
        }
    }


    class LineThread extends Thread {
        InputStream input;
        String[] output;


        LineThread(InputStream input, String[] output) {
            this.input = input;
            this.output = output;
            start();
        }

        @Override
        public void run() {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                saveStream(baos, input);
                BufferedReader reader =
                        createReader(new ByteArrayInputStream(baos.toByteArray()));
                String line;
                while ((line = reader.readLine()) != null) {
                    append(output, line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * Alternative version of exec() that retrieves stdout and stderr into the
     * StringList objects provided. This is a convenience function that handles
     * simple exec() calls. If the results will be more than a couple lines,
     * you shouldn't use this function, you should use a more elaborate method
     * that makes use of proper threading (to drain the shell output) and error
     * handling to address the many things that can go wrong within this method.
     *
     * @param stdout a non-null StringList object to be filled with any output
     * @param stderr a non-null StringList object to be filled with error lines
     * @param args   each argument to be passed as a series of String objects
     * @return the result returned from the application, or -1 if an Exception
     * occurs before the application is able to return a result.
     */

    public int exec(String[] stdout, String[] stderr, String... args) {
        Process p = exec(args);

        Thread outThread = new LineThread(p.getInputStream(), stdout);
        Thread errThread = new LineThread(p.getErrorStream(), stderr);
        try {
            int result = p.waitFor();
            outThread.join();
            errThread.join();
            return result;
        } catch (InterruptedException e) {
            // Throwing the exception here because we can't give a valid 'result'
            throw new RuntimeException(e);
        }
    }


    public int shell(String[] stdout, String[] stderr, String... args) {
        String shell;
        String runCmd;
        String[] argList = new String[0];

        shell = System.getenv("COMSPEC");
        runCmd = "/C";

        for (String arg : args) {
            append(argList, arg);
        }
        return exec(stdout, stderr, shell, runCmd, join(argList, " "));
    }

    public void exit() {
        exitActual();
    }


    public void exitActual() {
        try {
            System.exit(0);
        } catch (SecurityException e) {
            // don't care about applet security exceptions
        }
    }


    //////////////////// CALL METHOD AND THREAD ////////////////////


    /**
     * Call a method in the current class based on its name.
     * Note that the function being called must be public.
     */
    public void method(String name) {
        try {
            Method method = getClass().getMethod(name);
            method.invoke(this);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.getTargetException().printStackTrace();
        } catch (NoSuchMethodException nsme) {
            System.err.println("There is no public " + name + "() method " + "in the class " + getClass().getName());
        }
    }

    public void method(String name, Object... args) {
        try {
            Method method = getClass().getMethod(name);
            method.invoke(this, args);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.getTargetException().printStackTrace();
        } catch (NoSuchMethodException nsme) {
            System.err.println("There is no public " + name + "() method " + "in the class " + getClass().getName());
        }
    }


    public void thread(final String name) {
        new Thread(() -> method(name)).start();
    }

    public void thread(final String name, Object... args) {
        new Thread(() -> method(name, args)).start();
    }


    //////////////////// SCREENSHOT ////////////////////

    public BufferedImage takeScreenshot() {
        BufferedImage image = null;
        try {
            image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        } catch (AWTException e) {
            e.printStackTrace();
        }

        return image;

    }

    //////////////////// CLIPBOARD ////////////////////

    public void setClipboard(String text) {
        Clipboard clipboard = getSystemClipboard();
        clipboard.setContents(new StringSelection(text), null);
    }

    public String getClipboardText() {
        Clipboard systemClipboard = getSystemClipboard();
        DataFlavor dataFlavor = DataFlavor.stringFlavor;

        if (systemClipboard.isDataFlavorAvailable(dataFlavor)) {
            Object text = null;
            try {
                text = systemClipboard.getData(dataFlavor);
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
            return (String) text;
        }

        return null;
    }

    public BufferedImage getClipboardImage() {
        Transferable content = getSystemClipboard().getContents(null);
        if (content == null) {
            System.err.println("error: nothing found in clipboard");
            return null;
        }
        if (!content.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            System.err.println("error: no image found in clipboard");
            return null;
        }
        BufferedImage img = null;
        try {
            img = (BufferedImage) content.getTransferData(DataFlavor.imageFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    public Clipboard getSystemClipboard() {
        Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        return defaultToolkit.getSystemClipboard();
    }


    //////////////////// PRINT AND PRINTLN ////////////////////


    public void print(byte what) {
        System.out.print(what);
        System.out.flush();
    }

    public void print(boolean what) {
        System.out.print(what);
        System.out.flush();
    }

    public void print(char what) {
        System.out.print(what);
        System.out.flush();
    }

    public void print(int what) {
        System.out.print(what);
        System.out.flush();
    }

    public void print(long what) {
        System.out.print(what);
        System.out.flush();
    }

    public void print(float what) {
        System.out.print(what);
        System.out.flush();
    }

    public void print(double what) {
        System.out.print(what);
        System.out.flush();
    }

    public void print(String what) {
        System.out.print(what);
        System.out.flush();
    }

    public void print(Object... variables) {
        StringBuilder sb = new StringBuilder();
        for (Object o : variables) {
            if (sb.length() != 0) {
                sb.append(" ");
            }
            if (o == null) {
                sb.append("null");
            } else {
                sb.append(o);
            }
        }
        System.out.print(sb);
    }


    public void println() {
        System.out.println();
    }

    public void println(byte what) {
        System.out.println(what);
        System.out.flush();
    }

    public void println(boolean what) {
        System.out.println(what);
        System.out.flush();
    }

    public void println(char what) {
        System.out.println(what);
        System.out.flush();
    }

    public void println(int what) {
        System.out.println(what);
        System.out.flush();
    }

    public void println(long what) {
        System.out.println(what);
        System.out.flush();
    }

    public void println(float what) {
        System.out.println(what);
        System.out.flush();
    }

    public void println(double what) {
        System.out.println(what);
        System.out.flush();
    }

    public void println(String what) {
        System.out.println(what);
        System.out.flush();
    }

    public void println(Object... variables) {
        print(variables);
        println();
    }


    /**
     * For arrays, use printArray() instead. This function causes a warning
     * because the new print(Object...) and println(Object...) functions can't
     * be reliably bound by the compiler.
     */
    // public void println(Object what) {
    // 	if (what == null) {
    // 		System.out.println("null");
    // 	} else if (what.getClass().isArray()) {
    // 		printArray(what);
    // 	} else {
    // 		System.out.println(what);
    // 		System.out.flush();
    // 	}
    // }
    public void printArray(Object what) {
        if (what == null) {
            System.out.println("null");
        } else {
            String name = what.getClass().getName();
            if (name.charAt(0) == '[') {
                switch (name.charAt(1)) {
                    case '[':
                        System.out.println(what);
                        break;

                    case 'L':
                        // print a 1D array of objects as individual elements
                        Object[] poo = (Object[]) what;
                        for (int i = 0; i < poo.length; i++) {
                            if (poo[i] instanceof String) {
                                System.out.println("[" + i + "] \"" + poo[i] + "\"");
                            } else {
                                System.out.println("[" + i + "] " + poo[i]);
                            }
                        }
                        break;

                    case 'Z':  // boolean
                        boolean[] zz = (boolean[]) what;
                        for (int i = 0; i < zz.length; i++) {
                            System.out.println("[" + i + "] " + zz[i]);
                        }
                        break;

                    case 'B':  // byte
                        byte[] bb = (byte[]) what;
                        for (int i = 0; i < bb.length; i++) {
                            System.out.println("[" + i + "] " + bb[i]);
                        }
                        break;

                    case 'C':  // char
                        char[] cc = (char[]) what;
                        for (int i = 0; i < cc.length; i++) {
                            System.out.println("[" + i + "] '" + cc[i] + "'");
                        }
                        break;

                    case 'I':  // int
                        int[] ii = (int[]) what;
                        for (int i = 0; i < ii.length; i++) {
                            System.out.println("[" + i + "] " + ii[i]);
                        }
                        break;

                    case 'J':  // int
                        long[] jj = (long[]) what;
                        for (int i = 0; i < jj.length; i++) {
                            System.out.println("[" + i + "] " + jj[i]);
                        }
                        break;

                    case 'F':  // float
                        float[] ff = (float[]) what;
                        for (int i = 0; i < ff.length; i++) {
                            System.out.println("[" + i + "] " + ff[i]);
                        }
                        break;

                    case 'D':  // double
                        double[] dd = (double[]) what;
                        for (int i = 0; i < dd.length; i++) {
                            System.out.println("[" + i + "] " + dd[i]);
                        }
                        break;

                    default:
                        System.out.println(what);
                }
            } else { // not an array
                System.out.println(what);
            }
        }
        System.out.flush();
    }


    // not very useful, because it only works for public (and protec?)
    // fields of a class, not local variables to methods

    // class Run extends Template {
    // 	public int test = 0;

    // 	public Run() {
    // 		printvar("test");
    // 	}

    // }

    public void printvar(String name) {
        try {
            Field field = getClass().getDeclaredField(name);
            println(name + " = " + field.get(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printf(String format, Object... args) {
        System.out.print(String.format(format, args));
    }

    public void printlnf(String format, Object... args) {
        System.out.println(String.format(format, args));
    }

    public void debug(String msg) {
        if (DEBUG) println("[DEBUG]: " + msg);
    }

    public void printStackTrace(Throwable t) {
        t.printStackTrace();
    }

    public Map<String, Object> warnings;


    /**
     * Show an error, and keep track of it so that it's only shown once.
     */
    public void showWarning(String msg) {
        if (warnings == null) {
            warnings = new HashMap<>();
        }
        if (!warnings.containsKey(msg)) {
            System.err.println(msg);
            warnings.put(msg, new Object());
        }
    }


    public void showWarning(String msg, Object... args) {
        showWarning(String.format(msg, args));
    }


    //////////////////// MATH ////////////////////


    // lots of convenience methods for math with floats.
    // doubles are overkill for processing applets, and casting
    // things all the time is annoying, thus the functions below.

    /**
     * Calculates the absolute value (magnitude) of a number. The absolute
     * value of a number is always positive.
     */

    public final int abs(int n) {
        return (n < 0) ? -n : n;
    }

    public final double abs(double n) {
        return (n < 0) ? -n : n;
    }

    /**
     * Squares a number (multiplies a number by itself). The result is always a
     * positive number, as multiplying two negative numbers always yields a
     * positive result. For example, -1 * -1 = 1.
     */
    public final double sq(double n) {
        return n * n;
    }

    /**
     * Calculates the square root of a number. The square root of a number is
     * always positive, even though there may be a valid negative root. The
     * square root s of number a is such that s*s = a. It
     * is the opposite of squaring.
     */
    public final double sqrt(double n) {
        return Math.sqrt(n);
    }

    /**
     * Calculates the natural logarithm (the base-<i>e</i> logarithm) of a
     * number. This function expects the values greater than 0.0.
     */
    public final double log(double n) {
        return Math.log(n);
    }

    /**
     * Returns Euler's number <i>e</i> (2.71828...) raised to the power of the
     * value parameter.
     */
    public final double exp(double n) {
        return Math.exp(n);
    }

    /**
     * Facilitates exponential expressions. The pow() function is an
     * efficient way of multiplying numbers by themselves (or their reciprocal)
     * in large quantities. For example, pow(3, 5) is equivalent to the
     * expression 3*3*3*3*3 and pow(3, -5) is equivalent to 1 / 3*3*3*3*3.
     */
    public final double pow(double n, double e) {
        return Math.pow(n, e);
    }

    /**
     * Determines the largest value in a sequence of numbers, and then returns that
     * value. max() accepts either two or three float or int
     * values as parameters, or an array of any length.
     */
    public final int max(int a, int b) {
        return (a > b) ? a : b;
    }

    public final double max(double a, double b) {
        return (a > b) ? a : b;
    }


    public final int max(int a, int b, int c) {
        return (a > b) ? ((a > c) ? a : c) : ((b > c) ? b : c);
    }


    public final double max(double a, double b, double c) {
        return (a > b) ? ((a > c) ? a : c) : ((b > c) ? b : c);
    }

    public final int max(int[] list) {
        if (list.length == 0) {
            throw new ArrayIndexOutOfBoundsException("Cannot use min() or max() on an empty array.");
        }
        int max = list[0];
        for (int i = 1; i < list.length; i++) {
            if (list[i] > max) max = list[i];
        }
        return max;
    }

    public final double max(double[] list) {
        if (list.length == 0) {
            throw new ArrayIndexOutOfBoundsException("Cannot use min() or max() on an empty array.");
        }
        double max = list[0];
        for (int i = 1; i < list.length; i++) {
            if (list[i] > max) max = list[i];
        }
        return max;
    }


    /**
     * Determines the smallest value in a sequence of numbers, and then returns that
     * value. min() accepts either two or three float or int
     * values as parameters, or an array of any length.
     */

    public final int min(int a, int b) {
        return (a < b) ? a : b;
    }

    public final double min(double a, double b) {
        return (a < b) ? a : b;
    }


    public final int min(int a, int b, int c) {
        return (a < b) ? ((a < c) ? a : c) : ((b < c) ? b : c);
    }

    public final double min(double a, double b, double c) {
        return (a < b) ? ((a < c) ? a : c) : ((b < c) ? b : c);
    }


    public final int min(int[] list) {
        if (list.length == 0) {
            throw new ArrayIndexOutOfBoundsException("Cannot use min() or max() on an empty array.");
        }
        int min = list[0];
        for (int i = 1; i < list.length; i++) {
            if (list[i] < min) min = list[i];
        }
        return min;
    }

    public final double min(double[] list) {
        if (list.length == 0) {
            throw new ArrayIndexOutOfBoundsException("Cannot use min() or max() on an empty array.");
        }
        double min = list[0];
        for (int i = 1; i < list.length; i++) {
            if (list[i] < min) min = list[i];
        }
        return min;
    }


    /**
     * Constrains a value to not exceed a maximum and minimum value.
     */

    public final int constrain(int amt, int low, int high) {
        return (amt < low) ? low : ((amt > high) ? high : amt);
    }

    public final double constrain(double amt, double low, double high) {
        return (amt < low) ? low : ((amt > high) ? high : amt);
    }

    /**
     * Calculates the sine of an angle. This function expects the values of the
     * angle parameter to be provided in radians (values from 0 to
     * 6.28). Values are returned in the range -1 to 1.
     */
    public final double sin(double angle) {
        return Math.sin(angle);
    }

    /**
     * Calculates the cosine of an angle. This function expects the values of
     * the angle parameter to be provided in radians (values from 0 to
     * PI*2). Values are returned in the range -1 to 1.
     */
    public final double cos(double angle) {
        return Math.cos(angle);
    }

    /**
     * Calculates the ratio of the sine and cosine of an angle. This function
     * expects the values of the angle parameter to be provided in
     * radians (values from 0 to PI*2). Values are returned in the range
     * infinity to -infinity.
     */
    public final double tan(double angle) {
        return Math.tan(angle);
    }

    /**
     * The inverse of sin(), returns the arc sine of a value. This
     * function expects the values in the range of -1 to 1 and values are
     * returned in the range -PI/2 to PI/2.
     */
    public final double asin(double value) {
        return Math.asin(value);
    }

    /**
     * The inverse of cos(), returns the arc cosine of a value. This
     * function expects the values in the range of -1 to 1 and values are
     * returned in the range 0 to PI (3.1415927).
     */
    public final double acos(double value) {
        return Math.acos(value);
    }

    /**
     * The inverse of tan(), returns the arc tangent of a value. This
     * function expects the values in the range of -Infinity to Infinity
     * (exclusive) and values are returned in the range -PI/2 to PI/2 .
     */
    public final double atan(double value) {
        return Math.atan(value);
    }

    /**
     * Calculates the angle (in radians) from a specified point to the
     * coordinate origin as measured from the positive x-axis. Values are
     * returned as a float in the range from PI to -PI.
     * The atan2() function is most often used for orienting geometry to
     * the position of the cursor.  Note: The y-coordinate of the point is the
     * first parameter and the x-coordinate is the second due the the structure
     * of calculating the tangent.
     */
    public final double atan2(double y, double x) {
        return Math.atan2(y, x);
    }

    /**
     * Converts a radian measurement to its corresponding value in degrees.
     * Radians and degrees are two ways of measuring the same thing. There are
     * 360 degrees in a circle and 2*PI radians in a circle. For example,
     * 90&deg; = PI/2 = 1.5707964. All trigonometric functions in Processing
     * require their parameters to be specified in radians.
     */
    public final double degrees(double radians) {
        return radians * RAD_TO_DEG;
    }

    /**
     * Converts a degree measurement to its corresponding value in radians.
     * Radians and degrees are two ways of measuring the same thing. There are
     * 360 degrees in a circle and 2*PI radians in a circle. For example,
     * 90&deg; = PI/2 = 1.5707964. All trigonometric functions in Processing
     * require their parameters to be specified in radians.
     */
    public final double radians(double degrees) {
        return degrees * DEG_TO_RAD;
    }

    /**
     * Calculates the closest int value that is greater than or equal to the
     * value of the parameter. For example, ceil(9.03) returns the value 10.
     */
    public final int ceil(double n) {
        return (int) Math.ceil(n);
    }

    /**
     * Calculates the closest int value that is less than or equal to the value
     * of the parameter.
     */
    public final int floor(double n) {
        return (int) Math.floor(n);
    }

    /**
     * Calculates the integer closest to the n parameter. For example,
     * round(133.8) returns the value 134.
     */
    public final int round(double n) {
        return (int) Math.round(n);
    }


    /**
     * Calculates the magnitude (or length) of a vector. A vector is a
     * direction in space commonly used in computer graphics and linear
     * algebra. Because it has no "start" position, the magnitude of a vector
     * can be thought of as the distance from coordinate (0,0) to its (x,y)
     * value. Therefore, mag() is a shortcut for writing dist(0, 0, x, y).
     */

    public final double mag(double a, double b) {
        return Math.sqrt(a * a + b * b);
    }

    public final double mag(double a, double b, double c) {
        return Math.sqrt(a * a + b * b + c * c);
    }


    /**
     * Calculates the distance between two points.
     */

    public final double dist(double x1, double y1, double x2, double y2) {
        return sqrt(sq(x2 - x1) + sq(y2 - y1));
    }

    public final double dist(double x1, double y1, double z1,
                             double x2, double y2, double z2) {
        return sqrt(sq(x2 - x1) + sq(y2 - y1) + sq(z2 - z1));
    }

    /**
     * Calculates a number between two numbers at a specific increment. The
     * amt parameter is the amount to interpolate between the two values
     * where 0.0 equal to the first point, 0.1 is very near the first point,
     * 0.5 is half-way in between, etc. The lerp function is convenient for
     * creating motion along a straight path and for drawing dotted lines.
     */
    public final double lerp(double start, double stop, double amt) {
        return start + (stop - start) * amt;
    }

    /**
     * Normalizes a number from another range into a value between 0 and 1.
     * Identical to map(value, low, high, 0, 1).
     * <p>
     * Numbers outside of the range are not clamped to 0 and 1, because
     * out-of-range values are often intentional and useful. (See the second
     * example above.)
     */
    public final double norm(double value, double start, double stop) {
        return (value - start) / (stop - start);
    }

    /**
     * Re-maps a number from one range to another.
     * <p>
     * In the first example above, the number 25 is converted from a value in the
     * range of 0 to 100 into a value that ranges from the left edge of the window
     * (0) to the right edge (width).
     * <p>
     * As shown in the second example, numbers outside of the range are not
     * clamped to the minimum and maximum parameters values, because out-of-range
     * values are often intentional and useful.
     */
    public final double map(double value,
                            double start1, double stop1,
                            double start2, double stop2) {
        double outgoing =
                start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
        String badness = null;
        if (outgoing != outgoing) {
            badness = "NaN (not a number)";
        } else if (outgoing == Double.NEGATIVE_INFINITY ||
                outgoing == Double.POSITIVE_INFINITY) {
            badness = "infinity";
        }
        if (badness != null) {
            final String msg =
                    String.format("map(%s, %s, %s, %s, %s) called, which returns %s",
                            nf(value), nf(start1), nf(stop1),
                            nf(start2), nf(stop2), badness);
            showWarning(msg);
        }
        return outgoing;
    }


    //////////////////// RANDOM ////////////////////


    Random internalRandom;

    public final double random(double high) {
        // avoid an infinite loop when 0 or NaN are passed in
        if (high == 0 || high != high) {
            return 0;
        }

        if (internalRandom == null) {
            internalRandom = new Random();
        }

        double value;
        do {
            value = internalRandom.nextDouble() * high;
        } while (value == high);
        return value;
    }

    /**
     * Returns a float from a random series of numbers having a mean of 0
     * and standard deviation of 1. Each time the randomGaussian()
     * function is called, it returns a number fitting a Gaussian, or
     * normal, distribution. There is theoretically no minimum or maximum
     * value that randomGaussian() might return. Rather, there is
     * just a very low probability that values far from the mean will be
     * returned; and a higher probability that numbers near the mean will
     * be returned.
     */
    public final double randomGaussian() {
        if (internalRandom == null) {
            internalRandom = new Random();
        }
        return internalRandom.nextGaussian();
    }


    /**
     * Generates random numbers. Each time the random() function is called,
     * it returns an unexpected value within the specified range. If only one
     * parameter is passed to the function, it will return a float between zero
     * and the value of the high parameter. For example, random(5)
     * returns values between 0 and 5 (starting at zero, and up to, but not
     * including, 5).
     * <p>
     * If two parameters are specified, the function will return a float with a
     * value between the two values. For example, random(-5, 10.2) returns
     * values starting at -5 and up to (but not including) 10.2. To convert a
     * floating-point random number to an integer, use the int() function.
     */
    public final double random(double low, double high) {
        if (low >= high) return low;
        double diff = high - low;
        double value;
        do {
            value = random(diff) + low;
        } while (value == high);
        return value;
    }


    /**
     * Sets the seed value for random(). By default, random()
     * produces different results each time the program is run. Set the seed
     * parameter to a constant to return the same pseudo-random numbers each time
     * the software is run.
     */
    public final void randomSeed(long seed) {
        if (internalRandom == null) {
            internalRandom = new Random();
        }
        internalRandom.setSeed(seed);
    }


    //////////////////////////////////////////////////////////////

    // PERLIN NOISE

    final int PERLIN_YWRAPB = 4;
    final int PERLIN_YWRAP = 1 << PERLIN_YWRAPB;
    final int PERLIN_ZWRAPB = 8;
    final int PERLIN_ZWRAP = 1 << PERLIN_ZWRAPB;
    final int PERLIN_SIZE = 4095;

    int perlin_octaves = 4; // default to medium smooth
    double perlin_amp_falloff = 0.5d; // 50% reduction/octave

    int perlin_TWOPI, perlin_PI;
    double[] perlin_cosTable;
    double[] perlin;

    Random perlinRandom;


    public double noise(double x) {
        return noise(x, 0f, 0f);
    }

    public double noise(double x, double y) {
        return noise(x, y, 0f);
    }

    /**
     * Returns the Perlin noise value at specified coordinates. Perlin noise is a
     * random sequence generator producing a more natural, harmonic succession of
     * numbers than that of the standard random() function. It was
     * developed by Ken Perlin in the 1980s and has been used in graphical
     * applications to generate procedural textures, shapes, terrains, and other
     * seemingly organic forms.
     * <p>
     * In contrast to the random() function, Perlin noise is defined in an
     * infinite n-dimensional space, in which each pair of coordinates corresponds
     * to a fixed semi-random value (fixed only for the lifespan of the program).
     * The resulting value will always be between 0.0 and 1.0. Processing can
     * compute 1D, 2D and 3D noise, depending on the number of coordinates given.
     * The noise value can be animated by moving through the noise space, as
     * demonstrated in the first example above. The 2nd and 3rd dimensions can
     * also be interpreted as time.
     * <p>
     * The actual noise structure is similar to that of an audio signal, in
     * respect to the function's use of frequencies. Similar to the concept of
     * harmonics in physics, Perlin noise is computed over several octaves which
     * are added together for the final result.
     * <p>
     * Another way to adjust the character of the resulting sequence is the scale
     * of the input coordinates. As the function works within an infinite space,
     * the value of the coordinates doesn't matter as such; only the
     * distance between successive coordinates is important (such as when
     * using noise() within a loop). As a general rule, the smaller the
     * difference between coordinates, the smoother the resulting noise sequence.
     * Steps of 0.005-0.03 work best for most applications, but this will differ
     * depending on use.
     * <p>
     * There have been debates over the accuracy of the implementation of noise in
     * Processing. For clarification, it's an implementation of "classic Perlin
     * noise" from 1983, and not the newer "simplex noise" method from 2001.
     */
    public double noise(double x, double y, double z) {
        if (perlin == null) {
            if (perlinRandom == null) {
                perlinRandom = new Random();
            }
            perlin = new double[PERLIN_SIZE + 1];
            for (int i = 0; i < PERLIN_SIZE + 1; i++) {
                perlin[i] = perlinRandom.nextDouble();
            }
            perlin_cosTable = cosLUT;
            perlin_TWOPI = perlin_PI = SINCOS_LENGTH;
            perlin_PI >>= 1;
        }

        if (x < 0) x = -x;
        if (y < 0) y = -y;
        if (z < 0) z = -z;

        int xi = (int) x, yi = (int) y, zi = (int) z;
        double xf = x - xi;
        double yf = y - yi;
        double zf = z - zi;
        double rxf, ryf;

        double r = 0;
        double ampl = 0.5f;

        double n1, n2, n3;

        for (int i = 0; i < perlin_octaves; i++) {
            int of = xi + (yi << PERLIN_YWRAPB) + (zi << PERLIN_ZWRAPB);

            rxf = noise_fsc(xf);
            ryf = noise_fsc(yf);

            n1 = perlin[of & PERLIN_SIZE];
            n1 += rxf * (perlin[(of + 1) & PERLIN_SIZE] - n1);
            n2 = perlin[(of + PERLIN_YWRAP) & PERLIN_SIZE];
            n2 += rxf * (perlin[(of + PERLIN_YWRAP + 1) & PERLIN_SIZE] - n2);
            n1 += ryf * (n2 - n1);

            of += PERLIN_ZWRAP;
            n2 = perlin[of & PERLIN_SIZE];
            n2 += rxf * (perlin[(of + 1) & PERLIN_SIZE] - n2);
            n3 = perlin[(of + PERLIN_YWRAP) & PERLIN_SIZE];
            n3 += rxf * (perlin[(of + PERLIN_YWRAP + 1) & PERLIN_SIZE] - n3);
            n2 += ryf * (n3 - n2);

            n1 += noise_fsc(zf) * (n2 - n1);

            r += n1 * ampl;
            ampl *= perlin_amp_falloff;
            xi <<= 1;
            xf *= 2;
            yi <<= 1;
            yf *= 2;
            zi <<= 1;
            zf *= 2;

            if (xf >= 1.0f) {
                xi++;
                xf--;
            }
            if (yf >= 1.0f) {
                yi++;
                yf--;
            }
            if (zf >= 1.0f) {
                zi++;
                zf--;
            }
        }
        return r;
    }

    public double noise_fsc(double i) {
        return 0.5f * (1.0f - perlin_cosTable[(int) (i * perlin_PI) % perlin_TWOPI]);
    }

    /**
     * Adjusts the character and level of detail produced by the Perlin noise
     * function. Similar to harmonics in physics, noise is computed over several
     * octaves. Lower octaves contribute more to the output signal and as such
     * define the overall intensity of the noise, whereas higher octaves create
     * finer-grained details in the noise sequence.
     * <p>
     * By default, noise is computed over 4 octaves with each octave contributing
     * exactly half than its predecessor, starting at 50% strength for the first
     * octave. This falloff amount can be changed by adding an additional function
     * parameter. For example, a falloff factor of 0.75 means each octave will now
     * have 75% impact (25% less) of the previous lower octave. While any number
     * between 0.0 and 1.0 is valid, note that values greater than 0.5 may result
     * in noise() returning values greater than 1.0.
     * <p>
     * By changing these parameters, the signal created by the noise()
     * function can be adapted to fit very specific needs and characteristics.
     */
    public void noiseDetail(int lod) {
        if (lod > 0) perlin_octaves = lod;
    }

    public void noiseDetail(int lod, double falloff) {
        if (lod > 0) perlin_octaves = lod;
        if (falloff > 0) perlin_amp_falloff = falloff;
    }

    /**
     * Sets the seed value for noise(). By default, noise()
     * produces different results each time the program is run. Set the
     * value parameter to a constant to return the same pseudo-random
     * numbers each time the software is run.
     */
    public void noiseSeed(long seed) {
        if (perlinRandom == null) perlinRandom = new Random();
        perlinRandom.setSeed(seed);
        perlin = null;
    }


    //////////////////// FILE/FOLDER SELECTION ////////////////////

    /**
     * Open a platform-specific file chooser dialog to select a file for input.
     * After the selection is made, the selected File will be passed to the
     * 'callback' function. If the dialog is closed or canceled, null will be sent
     * to the function, so that the program is not waiting for additional input.
     * The callback is necessary because of how threading works.
     *
     * <h3>Advanced</h3>
     * <pre>
     * void setup() {
     *   selectInput("Select a file to process:", "fileSelected");
     * }
     *
     * void fileSelected(File selection) {
     *   if (selection == null) {
     *     println("Window was closed or the user hit cancel.");
     *   } else {
     *     println("User selected " + fileSelected.getAbsolutePath());
     *   }
     * }
     * </pre>
     * <p>
     * For advanced users, the method must be 'public', which is true for all
     * methods inside a sketch when run from the PDE, but must explicitly be set
     * when using Eclipse or other development environments.
     *
     * @param prompt   message to the user
     * @param callback name of the method to be called when the selection is made
     * @webref input:files
     * @webBrief Open a platform-specific file chooser dialog to select a file for
     * input
     */
    public void selectInput(String prompt, String callback) {
        selectInput(prompt, callback, null);
    }


    public void selectInput(String prompt, String callback, File file) {
        selectInput(prompt, callback, file, this);
    }


    public void selectInput(String prompt, String callback, File file, Object callbackObject) {
        selectImpl(prompt, callback, file, callbackObject, FileDialog.LOAD);
    }


    /**
     * Opens a platform-specific file chooser dialog to select a file for output.
     * After the selection is made, the selected File will be passed to the
     * 'callback' function. If the dialog is closed or canceled, null will be sent
     * to the function, so that the program is not waiting for additional input.
     * The callback is necessary because of how threading works.
     *
     * @param prompt   message to the user
     * @param callback name of the method to be called when the selection is made
     * @webref output:files
     * @webBrief Opens a platform-specific file chooser dialog to select a file for output
     */
    public void selectOutput(String prompt, String callback) {
        selectOutput(prompt, callback, null);
    }


    public void selectOutput(String prompt, String callback, File file) {
        selectOutput(prompt, callback, file, this);
    }


    public void selectOutput(String prompt, String callback,
                             File file, Object callbackObject) {
        //selectOutput(prompt, callback, file, callbackObject, null, this);
        // surface.selectOutput(prompt, callback, file, callbackObject);
        selectImpl(prompt, callback, file, callbackObject, FileDialog.SAVE);
    }


    // public void selectOutput(String prompt, String callbackMethod,
    //                                 File file, Object callbackObject, Frame parent) {
    // 	selectImpl(prompt, callbackMethod, file, callbackObject, parent, FileDialog.SAVE, null);
    // }


    // public void selectOutput(String prompt, String callbackMethod,
    //                                 File file, Object callbackObject, Frame parent,
    //                                 PApplet sketch) {
    // 	selectImpl(prompt, callbackMethod, file, callbackObject, parent, FileDialog.SAVE, sketch);
    // }


    public void selectImpl(final String prompt,
                           final String callbackMethod,
                           final File defaultSelection,
                           final Object callbackObject,
                           final int mode) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                File selectedFile = null;

                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle(prompt);
                if (defaultSelection != null) {
                    chooser.setSelectedFile(defaultSelection);
                }

                int result = -1;
                if (mode == FileDialog.SAVE) {
                    result = chooser.showSaveDialog(null);
                } else if (mode == FileDialog.LOAD) {
                    result = chooser.showOpenDialog(null);
                }
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = chooser.getSelectedFile();
                }


                selectCallback(selectedFile, callbackMethod, callbackObject);
            }
        });
    }


    /**
     * Opens a platform-specific file chooser dialog to select a folder.
     * After the selection is made, the selection will be passed to the
     * 'callback' function. If the dialog is closed or canceled, null
     * will be sent to the function, so that the program is not waiting
     * for additional input. The callback is necessary because of how
     * threading works.
     *
     * @param prompt   message to the user
     * @param callback name of the method to be called when the selection is made
     * @webref input:files
     * @webBrief Opens a platform-specific file chooser dialog to select a folder
     */
    public void selectFolder(String prompt, String callback) {
        selectFolder(prompt, callback, null);
    }


    public void selectFolder(String prompt, String callback, File file) {
        selectFolder(prompt, callback, file, this);
    }


    // public void selectFolder(String prompt, String callback,
    //                          File file, Object callbackObject) {
    // 	surface.selectFolder(prompt, callback, file, callbackObject);
    // }


    // public void selectFolder(final String prompt,
    //                                 final String callbackMethod,
    //                                 final File defaultSelection,
    //                                 final Object callbackObject,
    //                                 final Frame parentFrame) {
    // 	selectFolder(prompt, callbackMethod, defaultSelection, callbackObject, parentFrame, null);
    // }


    public void selectFolder(final String prompt,
                             final String callbackMethod,
                             final File defaultSelection,
                             final Object callbackObject) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                File selectedFile = null;

                // checkLookAndFeel();
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle(prompt);
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (defaultSelection != null) {
                    fileChooser.setCurrentDirectory(defaultSelection);
                }

                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                }

                selectCallback(selectedFile, callbackMethod, callbackObject);
            }
        });
    }


    public void selectCallback(File selectedFile,
                               String callbackMethod,
                               Object callbackObject) {
        try {
            Class<?> callbackClass = callbackObject.getClass();
            Method selectMethod =
                    callbackClass.getMethod(callbackMethod, File.class);
            selectMethod.invoke(callbackObject, selectedFile);
        } catch (IllegalAccessException iae) {
            System.err.println(callbackMethod + "() must be public");
        } catch (InvocationTargetException ite) {
            ite.printStackTrace();
        } catch (NoSuchMethodException nsme) {
            System.err.println(callbackMethod + "() could not be found");
        }
    }


    //////////////////// LISTING DIRECTORIES ////////////////////

    public String[] listPaths(String path, String... options) {
        File[] list = listFiles(path, options);

        int offset = 0;
        for (String opt : options) {
            if (opt.equals("relative")) {
                if (!path.endsWith(File.pathSeparator)) {
                    path += File.pathSeparator;
                }
                offset = path.length();
                break;
            }
        }
        String[] outgoing = new String[list.length];
        for (int i = 0; i < list.length; i++) {
            // as of Java 1.8, substring(0) returns the original object
            outgoing[i] = list[i].getAbsolutePath().substring(offset);
        }
        return outgoing;
    }


    public File[] listFiles(String path, String... options) {
        File file = new File(path);
        // if not an absolute path, make it relative to the sketch folder
        if (!file.isAbsolute()) {
            file = sketchFile(path);
        }
        return listFiles(file, options);
    }


    // "relative" -> no effect with the Files version, but important for listPaths
    // "recursive"
    // "extension=js" or "extensions=js|csv|txt" (no dot)
    // "directories" -> only directories
    // "files" -> only files
    // "hidden" -> include hidden files (prefixed with .) disabled by default
    public File[] listFiles(File base, String... options) {
        boolean recursive = false;
        String[] extensions = null;
        boolean directories = true;
        boolean files = true;
        boolean hidden = false;

        for (String opt : options) {
            if (opt.equals("recursive")) {
                recursive = true;
            } else if (opt.startsWith("extension=")) {
                extensions = new String[]{opt.substring(10)};
            } else if (opt.startsWith("extensions=")) {
                extensions = split(opt.substring(11), ',');
            } else if (opt.equals("files")) {
                directories = false;
            } else if (opt.equals("directories")) {
                files = false;
            } else if (opt.equals("hidden")) {
                hidden = true;
            } else //noinspection StatementWithEmptyBody
                if (opt.equals("relative")) {
                    // ignored
                } else {
                    throw new RuntimeException(opt + " is not a listFiles() option");
                }
        }

        if (extensions != null) {
            for (int i = 0; i < extensions.length; i++) {
                extensions[i] = "." + extensions[i];
            }
        }

        if (!files && !directories) {
            // just make "only files" and "only directories" mean... both
            files = true;
            directories = true;
        }

        if (!base.canRead()) {
            return null;
        }

        List<File> outgoing = new ArrayList<>();
        listFilesImpl(base, recursive, extensions, hidden, directories, files, outgoing);
        return outgoing.toArray(new File[0]);
    }


    public boolean listFilesExt(String name, String[] extensions) {
        for (String ext : extensions) {
            if (name.toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }


    void listFilesImpl(File folder, boolean recursive,
                       String[] extensions, boolean hidden,
                       boolean directories, boolean files,
                       List<File> list) {
        File[] items = folder.listFiles();
        if (items != null) {
            for (File item : items) {
                String name = item.getName();
                if (!hidden && name.charAt(0) == '.') {
                    continue;
                }
                if (item.isDirectory()) {
                    if (recursive) {
                        listFilesImpl(item, recursive, extensions, hidden, directories, files, list);
                    }
                    if (directories) {
                        if (extensions == null || listFilesExt(item.getName(), extensions)) {
                            list.add(item);
                        }
                    }
                } else if (files) {
                    if (extensions == null || listFilesExt(item.getName(), extensions)) {
                        list.add(item);
                    }
                }
            }
        }
    }

    /**
     * Get the compression-free extension for this filename.
     *
     * @param filename The filename to check
     * @return an extension, skipping past .gz if it's present
     */
    public String checkExtension(String filename) {
        // Don't consider the .gz as part of the name, createInput()
        // and createOutput() will take care of fixing that up.
        if (filename.toLowerCase().endsWith(".gz")) {
            filename = filename.substring(0, filename.length() - 3);
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex != -1) {
            return filename.substring(dotIndex + 1).toLowerCase();
        }
        return null;
    }


    //////////////////// READERS AND WRITERS ////////////////////

    /**
     * Creates a BufferedReader object that can be used to read files
     * line-by-line as individual String objects. This is the complement to
     * the createWriter() function. For more information about the
     * BufferedReader class and its methods like readLine() and
     * close used in the above example, please consult a Java
     * reference.
     * <p>
     * Starting with Processing release 0134, all files loaded and saved by the
     * Processing API use UTF-8 encoding. In previous releases, the default
     * encoding for your platform was used, which causes problems when files are
     * moved to other platforms.
     *
     * @param filename name of the file to be opened
     * @webref input:files
     * @webBrief Creates a BufferedReader object that can be used to read
     * files line-by-line as individual String objects
     */
    public BufferedReader createReader(String filename) {
        InputStream is = createInput(filename);
        if (is == null) {
            System.err.println("The file \"" + filename + "\" " +
                    "is missing or inaccessible, make sure " +
                    "the URL is valid or that the file has been " +
                    "added to your sketch and is readable.");
            return null;
        }
        return createReader(is);
    }


    /**
     * @nowebref
     */
    public BufferedReader createReader(File file) {
        try {
            InputStream is = new FileInputStream(file);
            if (file.getName().toLowerCase().endsWith(".gz")) {
                is = new GZIPInputStream(is);
            }
            return createReader(is);
        } catch (IOException e) {
            // Re-wrap rather than forcing novices to learn about exceptions
            throw new RuntimeException(e);
        }
    }


    /**
     * @nowebref I want to read lines from a stream. If I have to type the
     * following lines any more I'm gonna send Sun my medical bills.
     */
    public BufferedReader createReader(InputStream input) {
        InputStreamReader isr =
                new InputStreamReader(input, StandardCharsets.UTF_8);

        BufferedReader reader = new BufferedReader(isr);
        // consume the Unicode BOM (byte order marker) if present
        try {
            reader.mark(1);
            int c = reader.read();
            // if not the BOM, back up to the beginning again
            if (c != '\uFEFF') {
                reader.reset();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reader;
    }


    /**
     * Creates a new file in the sketch folder, and a PrintWriter object
     * to write to it. For the file to be made correctly, it should be flushed
     * and must be closed with its flush() and close() methods
     * (see above example).
     * <br/> <br/>
     * Starting with Processing release 0134, all files loaded and saved by the
     * Processing API use UTF-8 encoding. In previous releases, the default
     * encoding for your platform was used, which causes problems when files
     * are moved to other platforms.
     *
     * @param filename name of the file to be created
     * @webref output:files
     * @webBrief Creates a new file in the sketch folder, and a PrintWriter object
     * to write to it
     */
    public PrintWriter createWriter(String filename) {
        return createWriter(saveFile(filename));
    }


    /**
     * @nowebref I want to print lines to a file. I have RSI from typing these
     * eight lines of code so many times.
     */
    public PrintWriter createWriter(File file) {
        if (file == null) {
            throw new RuntimeException("File passed to createWriter() was null");
        }
        try {
            createPath(file);  // make sure in-between folders exist
            OutputStream output = new FileOutputStream(file);
            if (file.getName().toLowerCase().endsWith(".gz")) {
                output = new GZIPOutputStream(output);
            }
            return createWriter(output);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't create a writer for " +
                    file.getAbsolutePath(), e);
        }
    }

    /**
     * @nowebref I want to print lines to a file. Why am I always explaining myself?
     * It's the JavaSoft API engineers who need to explain themselves.
     */
    public PrintWriter createWriter(OutputStream output) {
        BufferedOutputStream bos = new BufferedOutputStream(output, 8192);
        OutputStreamWriter osw =
                new OutputStreamWriter(bos, StandardCharsets.UTF_8);
        return new PrintWriter(osw);
    }


    //////////////////// FILE I/O ////////////////////


    /**
     * This is a function for advanced programmers to open a Java InputStream.
     * It's useful if you want to use the facilities provided by PApplet to
     * easily open files from the data folder or from a URL, but want an
     * InputStream object so that you can use other parts of Java to take more
     * control of how the stream is read.
     * <p>
     * The filename passed in can be:
     * - A URL, for instance openStream("http://processing.org/")
     * - A file in the sketch's data folder
     * - The full path to a file to be opened locally (when running as an
     * application)
     * <p>
     * If the requested item doesn't exist, null is returned. If not online,
     * this will also check to see if the user is asking for a file whose name
     * isn't properly capitalized. If capitalization is different, an error
     * will be printed to the console. This helps prevent issues that appear
     * when a sketch is exported to the web, where case sensitivity matters, as
     * opposed to running from inside the Processing Development Environment on
     * Windows or Mac OS, where case sensitivity is preserved but ignored.
     * <p>
     * If the file ends with .gz, the stream will automatically be gzip
     * decompressed. If you don't want the automatic decompression, use the
     * related function createInputRaw().
     * <p>
     * In earlier releases, this function was called openStream().
     *
     *
     *
     * <h3>Advanced</h3>
     * Simplified method to open a Java InputStream.
     * <p>
     * This method is useful if you want to use the facilities provided
     * by PApplet to easily open things from the data folder or from a URL,
     * but want an InputStream object so that you can use other Java
     * methods to take more control of how the stream is read.
     * <p>
     * If the requested item doesn't exist, null is returned.
     * (Prior to 0096, die() would be called, killing the applet)
     * <p>
     * For 0096+, the "data" folder is exported intact with subfolders,
     * and openStream() properly handles subdirectories from the data folder
     * <p>
     * If not online, this will also check to see if the user is asking
     * for a file whose name isn't properly capitalized. This helps prevent
     * issues when a sketch is exported to the web, where case sensitivity
     * matters, as opposed to Windows and the Mac OS default where
     * case sensitivity is preserved but ignored.
     * <p>
     * It is strongly recommended that libraries use this method to open
     * data files, so that the loading sequence is handled in the same way
     * as functions like loadBytes(), loadImage(), etc.
     * <p>
     * The filename passed in can be:
     * <UL>
     * <LI>A URL, for instance openStream("http://processing.org/");
     * <LI>A file in the sketch's data folder
     * <LI>Another file to be opened locally (when running as an application)
     * </UL>
     *
     * @param filename the name of the file to use as input
     * @webref input:files
     * @webBrief This is a function for advanced programmers to open a Java InputStream
     */
    public InputStream createInput(String filename) {
        InputStream input = createInputRaw(filename);
        if (input != null) {
            // if it's gzip-encoded, automatically decode
            final String lower = filename.toLowerCase();
            if (lower.endsWith(".gz") || lower.endsWith(".svgz")) {
                try {
                    // buffered has to go *around* the GZ, otherwise 25x slower
                    return new BufferedInputStream(new GZIPInputStream(input));
                } catch (IOException e) {
                    printStackTrace(e);
                }
            } else {
                return new BufferedInputStream(input);
            }
        }
        return null;
    }


    /**
     * Call openStream() without automatic gzip decompression.
     */
    public InputStream createInputRaw(String filename) {
        if (filename == null) return null;

        if (sketchPath() == null) {
            System.err.println("The sketch path is not set.");
            throw new RuntimeException("Files must be loaded inside setup() or after it has been called.");
        }

        if (filename.length() == 0) {
            // an error will be called by the parent function
            //System.err.println("The filename passed to openStream() was empty.");
            return null;
        }

        // First check whether this looks like a URL
        if (filename.contains(":")) {  // at least smells like URL
            try {
                URL url = new URL(filename);
                URLConnection conn = url.openConnection();

                if (conn instanceof HttpURLConnection) {
                    HttpURLConnection httpConn = (HttpURLConnection) conn;
                    // Will not handle a protocol change (see below)
                    httpConn.setInstanceFollowRedirects(true);
                    int response = httpConn.getResponseCode();
                    // Default won't follow HTTP -> HTTPS redirects for security reasons
                    // http://stackoverflow.com/a/1884427
                    if (response >= 300 && response < 400) {
                        String newLocation = httpConn.getHeaderField("Location");
                        return createInputRaw(newLocation);
                    }
                    return conn.getInputStream();
                } else if (conn instanceof JarURLConnection) {
                    return url.openStream();
                }
            } catch (MalformedURLException mfue) {
                // not a url, that's fine
            } catch (FileNotFoundException fnfe) {
                // Added in 0119 b/c Java 1.5 throws FNFE when URL not available.
                // http://dev.processing.org/bugs/show_bug.cgi?id=403
            } catch (IOException e) {
                // changed for 0117, shouldn't be throwing exception
                printStackTrace(e);
                //System.err.println("Error downloading from URL " + filename);
                return null;
                //throw new RuntimeException("Error downloading from URL " + filename);
            }
        }

        InputStream stream;

        // Moved this earlier than the getResourceAsStream() checks, because
        // calling getResourceAsStream() on a directory lists its contents.
        // http://dev.processing.org/bugs/show_bug.cgi?id=716
        try {
            // First see if it's in a data folder. This may fail by throwing
            // a SecurityException. If so, this whole block will be skipped.
            File file = new File(dataPath(filename));
            if (!file.exists()) {
                // next see if it's just in the sketch folder
                file = sketchFile(filename);
            }

            if (file.isDirectory()) {
                return null;
            }
            if (file.exists()) {
                try {
                    // handle case sensitivity check
                    String filePath = file.getCanonicalPath();
                    String filenameActual = new File(filePath).getName();
                    // make sure there isn't a subfolder prepended to the name
                    String filenameShort = new File(filename).getName();
                    // if the actual filename is the same, but capitalized
                    // differently, warn the user.
                    //if (filenameActual.equalsIgnoreCase(filenameShort) &&
                    //!filenameActual.equals(filenameShort)) {
                    if (!filenameActual.equals(filenameShort)) {
                        throw new RuntimeException("This file is named " +
                                filenameActual + " not " +
                                filename + ". Rename the file " +
                                "or change your code.");
                    }
                } catch (IOException ignored) {
                }
            }

            // if this file is ok, may as well just load it
            return new FileInputStream(file);

            // have to break these out because a general Exception might
            // catch the RuntimeException being thrown above
        } catch (IOException | SecurityException ignored) {
        }

        // Using getClassLoader() prevents java from converting dots
        // to slashes or requiring a slash at the beginning.
        // (a slash as a prefix means that it'll load from the root of
        // the jar, rather than trying to dig into the package location)
        ClassLoader cl = getClass().getClassLoader();

        // by default, data files are exported to the root path of the jar.
        // (not the data folder) so check there first.
        stream = cl.getResourceAsStream("data/" + filename);
        if (stream != null) {
            String cn = stream.getClass().getName();
            // this is an irritation of sun's java plug-in, which will return
            // a non-null stream for an object that doesn't exist. like all good
            // things, this is probably introduced in java 1.5. awesome!
            // http://dev.processing.org/bugs/show_bug.cgi?id=359
            if (!cn.equals("sun.plugin.cache.EmptyInputStream")) {
                return stream;
            }
        }

        // When used with an online script, also need to check without the
        // data folder, in case it's not in a subfolder called 'data'.
        // http://dev.processing.org/bugs/show_bug.cgi?id=389
        stream = cl.getResourceAsStream(filename);
        if (stream != null) {
            String cn = stream.getClass().getName();
            if (!cn.equals("sun.plugin.cache.EmptyInputStream")) {
                return stream;
            }
        }

        try {
            // attempt to load from a local file, used when running as
            // an application, or as a signed applet
            try {  // first try to catch any security exceptions
                try {
                    return new FileInputStream(dataPath(filename));
                } catch (IOException ignored) {
                }

                try {
                    return new FileInputStream(sketchPath(filename));
                } catch (Exception ignored) {
                }

                try {
                    return new FileInputStream(filename);
                } catch (IOException ignored) {
                }
            } catch (SecurityException ignored) {
            }  // online, whups
        } catch (Exception e) {
            printStackTrace(e);
        }

        return null;
    }


    /**
     * @nowebref
     */
    public InputStream createInput(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File passed to createInput() was null");
        }
        if (!file.exists()) {
            System.err.println(file + " does not exist, createInput() will return null");
            return null;
        }
        try {
            InputStream input = new FileInputStream(file);
            final String lower = file.getName().toLowerCase();
            if (lower.endsWith(".gz") || lower.endsWith(".svgz")) {
                return new BufferedInputStream(new GZIPInputStream(input));
            }
            return new BufferedInputStream(input);
        } catch (IOException e) {
            System.err.println("Could not createInput() for " + file);
            e.printStackTrace();
            return null;
        }
    }


    // FILE OUTPUT


    /**
     * Similar to createInput(), this creates a Java OutputStream
     * for a given filename or path. The file will be created in the sketch
     * folder, or in the same folder as an exported application.
     * <p>
     * If the path does not exist, intermediate folders will be created. If an
     * exception occurs, it will be printed to the console, and null will
     * be returned.
     * <p>
     * This function is a convenience over the Java approach that requires you to
     * 1) create a FileOutputStream object, 2) determine the exact file location,
     * and 3) handle exceptions. Exceptions are handled internally by the
     * function, which is more appropriate for "sketch" projects.
     * <p>
     * If the output filename ends with .gz, the output will be
     * automatically GZIP compressed as it is written.
     *
     * @param filename name of the file to open
     * @webref output:files
     * @webBrief Similar to createInput(), this creates a Java
     * OutputStream for a given filename or path
     */
    public OutputStream createOutput(String filename) {
        return createOutput(saveFile(filename));
    }

    /**
     * @nowebref
     */
    public OutputStream createOutput(File file) {
        try {
            createPath(file);  // make sure the path exists
            OutputStream output = new FileOutputStream(file);
            if (file.getName().toLowerCase().endsWith(".gz")) {
                return new BufferedOutputStream(new GZIPOutputStream(output));
            }
            return new BufferedOutputStream(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Reads the contents of a file and places it in a byte array. If the name of
     * the file is used as the parameter, as in the above example, the file must
     * be loaded in the sketch's "data" directory/folder.
     * <p>
     * Alternatively, the file maybe be loaded from anywhere on the local computer
     * using an absolute path (something that starts with / on Unix and Linux, or
     * a drive letter on Windows), or the filename parameter can be a URL for a
     * file found on a network.
     * <p>
     * If the file is not available or an error occurs, null will be
     * returned and an error message will be printed to the console. The error
     * message does not halt the program, however the null value may cause a
     * NullPointerException if your code does not check whether the value returned
     * is null.
     *
     * @param filename name of a file in the data folder or a URL.
     * @webref input:files
     * @webBrief Reads the contents of a file or url and places it in a byte
     * array
     */
    public byte[] loadBytes(String filename) {
        String lower = filename.toLowerCase();
        // If it's not a .gz file, then we might be able to uncompress it into
        // a fixed-size buffer, which should help speed because we won't have to
        // reallocate and resize the target array each time it gets full.
        if (!lower.endsWith(".gz")) {
            // If this looks like a URL, try to load it that way. Use the fact that
            // URL connections may have a content length header to size the array.
            if (filename.contains(":")) {  // at least smells like URL
                InputStream input = null;
                try {
                    URL url = new URL(filename);
                    URLConnection conn = url.openConnection();
                    int length = -1;

                    if (conn instanceof HttpURLConnection) {
                        HttpURLConnection httpConn = (HttpURLConnection) conn;
                        // Will not handle a protocol change (see below)
                        httpConn.setInstanceFollowRedirects(true);
                        int response = httpConn.getResponseCode();
                        // Default won't follow HTTP -> HTTPS redirects for security reasons
                        // http://stackoverflow.com/a/1884427
                        if (response >= 300 && response < 400) {
                            String newLocation = httpConn.getHeaderField("Location");
                            return loadBytes(newLocation);
                        }
                        length = conn.getContentLength();
                        input = conn.getInputStream();
                    } else if (conn instanceof JarURLConnection) {
                        length = conn.getContentLength();
                        input = url.openStream();
                    }

                    if (input != null) {
                        byte[] buffer;
                        if (length != -1) {
                            buffer = new byte[length];
                            int count;
                            int offset = 0;
                            while ((count = input.read(buffer, offset, length - offset)) > 0) {
                                offset += count;
                            }
                        } else {
                            buffer = loadBytes(input);
                        }
                        input.close();
                        return buffer;
                    }
                } catch (MalformedURLException mfue) {
                    // not a url, that's fine
                } catch (FileNotFoundException fnfe) {
                    // Java 1.5+ throws FNFE when URL not available
                    // http://dev.processing.org/bugs/show_bug.cgi?id=403
                } catch (IOException e) {
                    printStackTrace(e);
                    return null;
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e) {
                            // just deal
                        }
                    }
                }
            }
        }

        InputStream is = createInput(filename);
        if (is != null) {
            byte[] outgoing = loadBytes(is);
            try {
                is.close();
            } catch (IOException e) {
                printStackTrace(e);  // shouldn't happen
            }
            return outgoing;
        }

        System.err.println("The file \"" + filename + "\" " +
                "is missing or inaccessible, make sure " +
                "the URL is valid or that the file has been " +
                "added to your sketch and is readable.");
        return null;
    }


    /**
     * @nowebref
     */
    public byte[] loadBytes(InputStream input) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];

            int bytesRead = input.read(buffer);
            while (bytesRead != -1) {
                out.write(buffer, 0, bytesRead);
                bytesRead = input.read(buffer);
            }
            out.flush();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @nowebref
     */
    public byte[] loadBytes(File file) {
        if (!file.exists()) {
            System.err.println(file + " does not exist, loadBytes() will return null");
            return null;
        }

        try {
            InputStream input;
            int length;

            if (file.getName().toLowerCase().endsWith(".gz")) {
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                raf.seek(raf.length() - 4);
                int b4 = raf.read();
                int b3 = raf.read();
                int b2 = raf.read();
                int b1 = raf.read();
                length = (b1 << 24) | (b2 << 16) + (b3 << 8) + b4;
                raf.close();

                // buffered has to go *around* the GZ, otherwise 25x slower
                input = new BufferedInputStream(new GZIPInputStream(new FileInputStream(file)));
            } else {
                long len = file.length();
                // http://stackoverflow.com/a/3039805
                int maxArraySize = Integer.MAX_VALUE - 5;
                if (len > maxArraySize) {
                    System.err.println("Cannot use loadBytes() on a file larger than " + maxArraySize);
                    return null;
                }
                length = (int) len;
                input = new BufferedInputStream(new FileInputStream(file));
            }
            byte[] buffer = new byte[length];
            int count;
            int offset = 0;
            // count will come back 0 when complete (or -1 if somehow going long?)
            while ((count = input.read(buffer, offset, length - offset)) > 0) {
                offset += count;
            }
            input.close();
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * @nowebref
     */
    public String[] loadStrings(File file) {
        if (!file.exists()) {
            System.err.println(file + " does not exist, loadStrings() will return null");
            return null;
        }

        InputStream is = createInput(file);
        if (is != null) {
            String[] outgoing = loadStrings(is);
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return outgoing;
        }
        return null;
    }


    /**
     * Reads the contents of a file and creates a String array of its individual
     * lines. If the name of the file is used as the parameter, as in the above
     * example, the file must be loaded in the sketch's "data" directory/folder.
     * <p>
     * <p>
     * Alternatively, the file maybe be loaded from anywhere on the local computer
     * using an absolute path (something that starts with / on Unix and Linux, or
     * a drive letter on Windows), or the filename parameter can be a URL for a
     * file found on a network.
     * <p>
     * If the file is not available or an error occurs, null will be
     * returned and an error message will be printed to the console. The error
     * message does not halt the program, however the null value may cause a
     * NullPointerException if your code does not check whether the value returned
     * is null.
     * <p>
     * Starting with Processing release 0134, all files loaded and saved by the
     * Processing API use UTF-8 encoding. In previous releases, the default
     * encoding for your platform was used, which causes problems when files are
     * moved to other platforms.
     *
     *
     * <h3>Advanced</h3> Load data from a file and shove it into a String array.
     * <p>
     * Exceptions are handled internally, when an error, occurs, an exception is
     * printed to the console and 'null' is returned, but the program continues
     * running. This is a tradeoff between 1) showing the user that there was a
     * problem but 2) not requiring that all i/o code is contained in try/catch
     * blocks, for the sake of new users (or people who are just trying to get
     * things done in a "scripting" fashion. If you want to handle exceptions, use
     * Java methods for I/O.
     *
     * @param filename name of the file or url to load
     * @webref input:files
     * @webBrief Reads the contents of a file or url and creates a String array of
     * its individual lines
     */
    public String[] loadStrings(String filename) {
        InputStream is = createInput(filename);
        if (is != null) {
            String[] strArr = loadStrings(is);
            try {
                is.close();
            } catch (IOException e) {
                printStackTrace(e);
            }
            return strArr;
        }

        System.err.println("The file \"" + filename + "\" " +
                "is missing or inaccessible, make sure " +
                "the URL is valid or that the file has been " +
                "added to your sketch and is readable.");
        return null;
    }

    /**
     * @nowebref
     */
    public String[] loadStrings(InputStream input) {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        return loadStrings(reader);
    }


    public String[] loadStrings(BufferedReader reader) {
        try {
            String[] lines = new String[100];
            int lineCount = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                if (lineCount == lines.length) {
                    String[] temp = new String[lineCount << 1];
                    System.arraycopy(lines, 0, temp, 0, lineCount);
                    lines = temp;
                }
                lines[lineCount++] = line;
            }
            reader.close();

            if (lineCount == lines.length) {
                return lines;
            }

            // resize array to appropriate amount for these lines
            String[] output = new String[lineCount];
            System.arraycopy(lines, 0, output, 0, lineCount);
            return output;
        } catch (IOException e) {
            e.printStackTrace();
            //throw new RuntimeException("Error inside loadStrings()");
        }
        return null;
    }


    /**
     * Save the contents of a stream to a file in the sketch folder. This is
     * basically saveBytes(blah, loadBytes()), but done more efficiently
     * (and with less confusing syntax).
     * <p>
     * The target parameter can be either a String specifying a file name,
     * or, for greater control over the file location, a File object. (Note
     * that, unlike some other functions, this will not automatically compress or
     * uncompress gzip files.)
     *
     * @param target name of the file to write to
     * @param source location to read from (a filename, path, or URL)
     * @webref output:files
     * @webBrief Save the contents of a stream to a file in the sketch folder
     */
    public boolean saveStream(String target, String source) {
        return saveStream(saveFile(target), source);
    }

    /**
     * Identical to the other saveStream(), but writes to a File
     * object, for greater control over the file location.
     * <p/>
     * Note that unlike other api methods, this will not automatically
     * compress or uncompress gzip files.
     */
    public boolean saveStream(File target, String source) {
        return saveStream(target, createInputRaw(source));
    }

    /**
     * @nowebref
     */
    public boolean saveStream(String target, InputStream source) {
        return saveStream(saveFile(target), source);
    }

    /**
     * @nowebref
     */
    public boolean saveStream(File target, InputStream source) {
        File tempFile = null;
        try {
            // make sure that this path actually exists before writing
            createPath(target);
            tempFile = createTempFile(target);
            FileOutputStream targetStream = new FileOutputStream(tempFile);

            saveStream(targetStream, source);
            targetStream.close();

            if (target.exists()) {
                if (!target.delete()) {
                    System.err.println("Could not replace " + target);
                }
            }
            if (!tempFile.renameTo(target)) {
                System.err.println("Could not rename temporary file " + tempFile);
                return false;
            }
            return true;
        } catch (IOException e) {
            if (tempFile != null) {
                if (!tempFile.delete()) {
                    System.err.println("Could not rename temporary file " + tempFile);
                }
            }
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @nowebref
     */
    public void saveStream(OutputStream target,
                           InputStream source) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(source, 16384);
        BufferedOutputStream bos = new BufferedOutputStream(target);

        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = bis.read(buffer)) != -1) {
            bos.write(buffer, 0, bytesRead);
        }

        bos.flush();
    }


    /**
     * As the opposite of loadBytes(), this function will write an entire
     * array of bytes to a file. The data is saved in binary format. This file is
     * saved to the sketch's folder, which is opened by selecting "Show Sketch
     * Folder" from the "Sketch" menu. Alternatively, the files can be saved to
     * any location on the computer by using an absolute path (something that
     * starts with / on Unix and Linux, or a drive letter on Windows).
     *
     * @param filename name of the file to write to
     * @param data     array of bytes to be written
     * @webref output:files
     * @webBrief Opposite of loadBytes(), will write an entire array of
     * bytes to a file
     */
    public void saveBytes(String filename, byte[] data) {
        saveBytes(saveFile(filename), data);
    }


    /**
     * Creates a temporary file based on the name/extension of another file
     * and in the same parent directory. Ensures that the same extension is used
     * (i.e. so that .gz files are gzip compressed on output) and that it's done
     * from the same directory so that renaming the file later won't cross file
     * system boundaries.
     */
    public File createTempFile(File file) throws IOException {
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Could not make directories for " + parentDir);
            }
        }
        String name = file.getName();
        String prefix;
        String suffix = null;
        int dot = name.lastIndexOf('.');
        if (dot == -1) {
            prefix = name;
        } else {
            // preserve the extension so that .gz works properly
            prefix = name.substring(0, dot);
            suffix = name.substring(dot);
        }
        // Prefix must be three characters
        if (prefix.length() < 3) {
            prefix += "TEMP";
        }
        return File.createTempFile(prefix, suffix, parentDir);
    }


    /**
     * @nowebref Saves bytes to a specific File location specified by the user.
     */
    public void saveBytes(File file, byte[] data) {
        File tempFile = null;
        try {
            tempFile = createTempFile(file);

            OutputStream output = createOutput(tempFile);
            if (output != null) {
                saveBytes(output, data);
                output.close();
            } else {
                System.err.println("Could not write to " + tempFile);
            }

            if (file.exists()) {
                if (!file.delete()) {
                    System.err.println("Could not replace " + file);
                }
            }

            if (!tempFile.renameTo(file)) {
                System.err.println("Could not rename temporary file " + tempFile);
            }
        } catch (IOException e) {
            System.err.println("error saving bytes to " + file);
            if (tempFile != null) {
                if (!tempFile.delete()) {
                    System.err.println("Could not delete temporary file " + tempFile);
                }
            }
            e.printStackTrace();
        }
    }


    /**
     * @nowebref Spews a buffer of bytes to an OutputStream.
     */
    public void saveBytes(OutputStream output, byte[] data) {
        try {
            output.write(data);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //

    /**
     * Writes an array of Strings to a file, one line per String. By default, this
     * file is saved to the sketch's folder. This folder is opened by selecting
     * "Show Sketch Folder" from the "Sketch" menu.
     * <p>
     * Alternatively, the file can be saved to any location on the computer by
     * using an absolute path (something that starts with / on Unix and Linux, or
     * a drive letter on Windows).
     * <p>
     * Starting with Processing 1.0, all files loaded and saved by the Processing
     * API use UTF-8 encoding. In earlier releases, the default encoding for your
     * platform was used, which causes problems when files are moved to other
     * platforms.
     */
    public void saveStrings(String filename, String[] data) {
        saveStrings(saveFile(filename), data);
    }


    /**
     * @nowebref
     */
    public void saveStrings(File file, String[] data) {
        saveStrings(createOutput(file), data);
    }


    /**
     * @nowebref
     */
    public void saveStrings(OutputStream output, String[] data) {
        PrintWriter writer = createWriter(output);
        for (String item : data) {
            writer.println(item);
        }
        writer.flush();
        writer.close();
    }


    public void saveImage(String filename, BufferedImage img) {
        try {
            ImageIO.write(img, "png", new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    ///////////////////////////// PATHS /////////////////////////////////


    public String calcSketchPath() {
        // try to get the user folder. if running under java web start,
        // this may cause a security exception if the code is not signed.
        String folder = null;
        try {
            folder = System.getProperty("user.dir");
//            println("sk");
            URL jarURL = Template.class.getProtectionDomain().getCodeSource().getLocation();
            String jarPath = jarURL.toURI().getSchemeSpecificPart();

            // Working directory may not be set properly, try some options
            if (jarPath.contains("/lib/")) {
                // Windows or Linux, back up a directory to get the executable
                folder = new File(jarPath, "../..").getCanonicalPath();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return folder;
    }


    public String sketchPath() {
        if (sketchPath == null) {
            sketchPath = calcSketchPath();
        }
        return sketchPath;
    }


    /**
     * Prepend the sketch folder path to the filename (or path) that is
     * passed in. External libraries should use this function to save to
     * the sketch folder.
     * <p>
     * Note that when running as an applet inside a web browser,
     * the sketchPath will be set to null, because security restrictions
     * prevent applets from accessing that information.
     * <p>
     * This will also cause an error if the sketch is not inited properly,
     * meaning that init() was never called on the PApplet when hosted
     * my some other main() or by other code. For proper use of init(),
     * see the examples in the main description text for PApplet.
     */
    public String sketchPath(String where) {
        if (sketchPath() == null) {
            return where;
        }
        try {
            if (new File(where).isAbsolute()) {
                return where;
            }
        } catch (Exception e) {
            // do nothing
        }
        return sketchPath() + File.separator + where;
    }


    public File sketchFile(String where) {
        return new File(sketchPath(where));
    }


    /**
     * Returns a path inside the applet folder to save to. Like sketchPath(),
     * but creates any in-between folders so that things save properly.
     * <p>
     * All save() functions use the path to the sketch folder, rather than
     * its data folder. Once exported, the data folder will be found inside the
     * jar file of the exported application or applet. In this case, it's not
     * possible to save data into the jar file, because it will often be running
     * from a server, or marked in-use if running from a local file system.
     * With this in mind, saving to the data path doesn't make sense anyway.
     * If you know you're running locally, and want to save to the data folder,
     * use save("data/blah.dat").
     */
    public String savePath(String where) {
        if (where == null) return null;
        String filename = sketchPath(where);
        createPath(filename);
        return filename;
    }


    /**
     * Identical to savePath(), but returns a File object.
     */
    public File saveFile(String where) {
        return new File(savePath(where));
    }

    /**
     * This function almost certainly does not do the thing you want it to.
     * The data path is handled differently on each platform, and should not be
     * considered a location to write files. It should also not be assumed that
     * this location can be read from or listed. This function is used internally
     * as a possible location for reading files. It's still "public" as a
     * holdover from earlier code.
     * <p>
     * Libraries should use createInput() to get an InputStream or createOutput()
     * to get an OutputStream. sketchPath() can be used to get a location
     * relative to the sketch. Again, do not use this to get relative
     * locations of files. You'll be disappointed when your app runs on different
     * platforms.
     */
    public String dataPath(String where) {
        return dataFile(where).getAbsolutePath();
    }


    /**
     * Return a full path to an item in the data folder as a File object.
     * See the dataPath() method for more information.
     */
    public File dataFile(String where) {
        // isAbsolute() could throw an access exception, but so will writing
        // to the local disk using the sketch path, so this is safe here.
        File why = new File(where);
        if (why.isAbsolute()) return why;

        URL jarURL = getClass().getProtectionDomain().getCodeSource().getLocation();
        // Decode URL
        String jarPath;
        try {
            jarPath = jarURL.toURI().getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        if (jarPath.contains("Contents/Java/")) {
            File containingFolder = new File(jarPath).getParentFile();
            File dataFolder = new File(containingFolder, "data");
            return new File(dataFolder, where);
        }
        // Windows, Linux, or when not using a Mac OS X .app file
        return new File(sketchPath + File.separator + ".." + File.separator + "data" + File.separator + where);
    }


    /**
     * Takes a path and creates any in-between folders if they don't
     * already exist. Useful when trying to save to a subfolder that
     * may not actually exist.
     */
    public void createPath(String path) {
        createPath(new File(path));
    }


    public void createPath(File file) {
        try {
            String parent = file.getParent();
            if (parent != null) {
                File unit = new File(parent);
                if (!unit.exists()) {
                    boolean result = unit.mkdirs();
                    if (!result) {
                        System.err.println("Could not create " + unit);
                    }
                }
            }
        } catch (SecurityException se) {
            System.err.println("You don't have permissions to create " +
                    file.getAbsolutePath());
        }
    }


    public String getExtension(String filename) {
        String extension;

        String lower = filename.toLowerCase();
        int dot = filename.lastIndexOf('.');
        if (dot == -1) {
            return "";  // no extension found
        }
        extension = lower.substring(dot + 1);

        // check for, and strip any parameters on the url, i.e.
        // filename.jpg?blah=blah&something=that
        int question = extension.indexOf('?');
        if (question != -1) {
            extension = extension.substring(0, question);
        }

        return extension;
    }


    File desktopFolder;

    public File desktopFile(String what) {
        if (desktopFolder == null) {
            // Should work on Linux and OS X (on OS X, even with the localized version).
            desktopFolder = new File(System.getProperty("user.home"), "Desktop");
            if (!desktopFolder.exists()) {
                desktopFolder = FileSystemView.getFileSystemView().getHomeDirectory();
            }
        }
        return new File(desktopFolder, what);
    }


    public String desktopPath(String what) {
        return desktopFile(what).getAbsolutePath();
    }


    //////////////////// ARRAY UTILITIES ////////////////////

    /**
     * Copies an array (or part of an array) to another array. The src
     * array is copied to the dst array, beginning at the position
     * specified by srcPosition and into the position specified by
     * dstPosition. The number of elements to copy is determined by
     * length. Note that copying values overwrites existing values in the
     * destination array. To append values instead of overwriting them, use
     * concat().
     * <p>
     * The simplified version with only two arguments arrayCopy(src,
     * dst) copies an entire array to another of the same size. It is
     * equivalent to arrayCopy(src, 0, dst, 0, src.length).
     * <p>
     * Using this function is far more efficient for copying array data than
     * iterating through a for() loop and copying each element
     * individually. This function only copies references, which means that for
     * most purposes it only copies one-dimensional arrays (a single set of
     * brackets). If used with a two (or three or more) dimensional array, it will
     * only copy the references at the first level, because a two dimensional
     * array is simply an "array of arrays". This does not produce an error,
     * however, because this is often the desired behavior. Internally, this
     * function calls Java's System.arraycopy()
     * method, so most things that apply there are inherited.
     *
     * @param src         the source array
     * @param srcPosition starting position in the source array
     * @param dst         the destination array of the same data type as the source array
     * @param dstPosition starting position in the destination array
     * @param length      number of array elements to be copied
     * @webref data:array functions
     * @webBrief Copies an array (or part of an array) to another array
     */
    @SuppressWarnings("SuspiciousSystemArraycopy")
    public void arrayCopy(Object src, int srcPosition,
                          Object dst, int dstPosition,
                          int length) {
        System.arraycopy(src, srcPosition, dst, dstPosition, length);
    }

    /**
     * Convenience method for arraycopy().
     * Identical to arraycopy(src, 0, dst, 0, length);
     */
    @SuppressWarnings("SuspiciousSystemArraycopy")
    public void arrayCopy(Object src, Object dst, int length) {
        System.arraycopy(src, 0, dst, 0, length);
    }

    /**
     * Shortcut to copy the entire contents of
     * the source into the destination array.
     * Identical to arraycopy(src, 0, dst, 0, src.length);
     */
    @SuppressWarnings("SuspiciousSystemArraycopy")
    public void arrayCopy(Object src, Object dst) {
        System.arraycopy(src, 0, dst, 0, Array.getLength(src));
    }


    /**
     * Increases the size of a one-dimensional array. By default, this function
     * doubles the size of the array, but the optional newSize parameter
     * provides precise control over the increase in size.
     * <p>
     * When using an array of objects, the data returned from the function must be
     * cast to the object array's data type. For example: SomeClass[] items =
     * (SomeClass[]) expand(originalArray)
     *
     * @param list the array to expand
     * @webref data:array functions
     * @webBrief Increases the size of an array
     */
    public boolean[] expand(boolean[] list) {
        return expand(list, list.length > 0 ? list.length << 1 : 1);
    }

    /**
     * @param newSize new size for the array
     */
    public boolean[] expand(boolean[] list, int newSize) {
        boolean[] temp = new boolean[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    public byte[] expand(byte[] list) {
        return expand(list, list.length > 0 ? list.length << 1 : 1);
    }

    public byte[] expand(byte[] list, int newSize) {
        byte[] temp = new byte[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    public char[] expand(char[] list) {
        return expand(list, list.length > 0 ? list.length << 1 : 1);
    }

    public char[] expand(char[] list, int newSize) {
        char[] temp = new char[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    public int[] expand(int[] list) {
        return expand(list, list.length > 0 ? list.length << 1 : 1);
    }

    public int[] expand(int[] list, int newSize) {
        int[] temp = new int[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    public long[] expand(long[] list) {
        return expand(list, list.length > 0 ? list.length << 1 : 1);
    }

    public long[] expand(long[] list, int newSize) {
        long[] temp = new long[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    public float[] expand(float[] list) {
        return expand(list, list.length > 0 ? list.length << 1 : 1);
    }

    public float[] expand(float[] list, int newSize) {
        float[] temp = new float[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    public double[] expand(double[] list) {
        return expand(list, list.length > 0 ? list.length << 1 : 1);
    }

    public double[] expand(double[] list, int newSize) {
        double[] temp = new double[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    public String[] expand(String[] list) {
        return expand(list, list.length > 0 ? list.length << 1 : 1);
    }

    public String[] expand(String[] list, int newSize) {
        String[] temp = new String[newSize];
        // in case the new size is smaller than list.length
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    /**
     * @nowebref
     */
    public Object expand(Object array) {
        int len = Array.getLength(array);
        return expand(array, len > 0 ? len << 1 : 1);
    }

    @SuppressWarnings("SuspiciousSystemArraycopy")
    public Object expand(Object list, int newSize) {
        Class<?> type = list.getClass().getComponentType();
        Object temp = Array.newInstance(type, newSize);
        System.arraycopy(list, 0, temp, 0,
                Math.min(Array.getLength(list), newSize));
        return temp;
    }

    // contract() has been removed in revision 0124, use subset() instead.
    // (expand() is also functionally equivalent)

    /**
     * Expands an array by one element and adds data to the new position. The
     * datatype of the element parameter must be the same as the
     * datatype of the array.
     * <br/> <br/>
     * When using an array of objects, the data returned from the function must
     * be cast to the object array's data type. For example: SomeClass[]
     * items = (SomeClass[]) append(originalArray, element).
     *
     * @param array array to append
     * @param value new data for the array
     * @webref data:array functions
     * @webBrief Expands an array by one element and adds data to the new position
     */
    public byte[] append(byte[] array, byte value) {
        array = expand(array, array.length + 1);
        array[array.length - 1] = value;
        return array;
    }

    public char[] append(char[] array, char value) {
        array = expand(array, array.length + 1);
        array[array.length - 1] = value;
        return array;
    }

    public int[] append(int[] array, int value) {
        array = expand(array, array.length + 1);
        array[array.length - 1] = value;
        return array;
    }

    public float[] append(float[] array, float value) {
        array = expand(array, array.length + 1);
        array[array.length - 1] = value;
        return array;
    }

    public double[] append(double[] array, double value) {
        array = expand(array, array.length + 1);
        array[array.length - 1] = value;
        return array;
    }

    public String[] append(String[] array, String value) {
        array = expand(array, array.length + 1);
        array[array.length - 1] = value;
        return array;
    }

    public Object append(Object array, Object value) {
        int length = Array.getLength(array);
        array = expand(array, length + 1);
        Array.set(array, length, value);
        return array;
    }


    /**
     * Decreases an array by one element and returns the shortened array.
     * <br/> <br/>
     * When using an array of objects, the data returned from the function must
     * be cast to the object array's data type. For example: SomeClass[]
     * items = (SomeClass[]) shorten(originalArray).
     *
     * @param list array to shorten
     * @webref data:array functions
     * @webBrief Decreases an array by one element and returns the shortened array
     */

    public boolean[] shorten(boolean[] list) {
        return subset(list, 0, list.length - 1);
    }

    public boolean[] shorten(boolean[] list, int amount) {
        return subset(list, 0, list.length - (amount < list.length ? amount : 1));
    }

    public byte[] shorten(byte[] list) {
        return subset(list, 0, list.length - 1);
    }

    public byte[] shorten(byte[] list, int amount) {
        return subset(list, 0, list.length - (amount < list.length ? amount : 1));
    }

    public char[] shorten(char[] list) {
        return subset(list, 0, list.length - 1);
    }

    public char[] shorten(char[] list, int amount) {
        return subset(list, 0, list.length - (amount < list.length ? amount : 1));
    }

    public int[] shorten(int[] list) {
        return subset(list, 0, list.length - 1);
    }

    public int[] shorten(int[] list, int amount) {
        return subset(list, 0, list.length - (amount < list.length ? amount : 1));
    }

    public float[] shorten(float[] list) {
        return subset(list, 0, list.length - 1);
    }

    public float[] shorten(float[] list, int amount) {
        return subset(list, 0, list.length - (amount < list.length ? amount : 1));
    }

    public double[] shorten(double[] list) {
        return subset(list, 0, list.length - 1);
    }

    public double[] shorten(double[] list, int amount) {
        return subset(list, 0, list.length - (amount < list.length ? amount : 1));
    }

    public String[] shorten(String[] list) {
        return subset(list, 0, list.length - 1);
    }

    public String[] shorten(String[] list, int amount) {
        return subset(list, 0, list.length - (amount < list.length ? amount : 1));
    }


    /**
     * Inserts a value or an array of values into an existing array. The first two
     * parameters must be arrays of the same datatype. The first parameter
     * specifies the initial array to be modified, and the second parameter
     * defines the data to be inserted. The third parameter is an index value
     * which specifies the array position from which to insert data. (Remember
     * that array index numbering starts at zero, so the first position is 0, the
     * second position is 1, and so on.)
     * <p>
     * When splicing an array of objects, the data returned from the function must
     * be cast to the object array's data type. For example: SomeClass[] items
     * = (SomeClass[]) splice(array1, array2, index)
     *
     * @param list  array to splice into
     * @param value value to be spliced in
     * @param index position in the array from which to insert data
     * @webref data:array functions
     * @webBrief Inserts a value or array of values into an existing array
     */
    final public boolean[] splice(boolean[] list,
                                  boolean value, int index) {
        boolean[] outgoing = new boolean[list.length + 1];
        System.arraycopy(list, 0, outgoing, 0, index);
        outgoing[index] = value;
        System.arraycopy(list, index, outgoing, index + 1,
                list.length - index);
        return outgoing;
    }

    final public boolean[] splice(boolean[] list,
                                  boolean[] value, int index) {
        boolean[] outgoing = new boolean[list.length + value.length];
        System.arraycopy(list, 0, outgoing, 0, index);
        System.arraycopy(value, 0, outgoing, index, value.length);
        System.arraycopy(list, index, outgoing, index + value.length,
                list.length - index);
        return outgoing;
    }

    final public byte[] splice(byte[] list,
                               byte value, int index) {
        byte[] outgoing = new byte[list.length + 1];
        System.arraycopy(list, 0, outgoing, 0, index);
        outgoing[index] = value;
        System.arraycopy(list, index, outgoing, index + 1,
                list.length - index);
        return outgoing;
    }

    final public byte[] splice(byte[] list,
                               byte[] value, int index) {
        byte[] outgoing = new byte[list.length + value.length];
        System.arraycopy(list, 0, outgoing, 0, index);
        System.arraycopy(value, 0, outgoing, index, value.length);
        System.arraycopy(list, index, outgoing, index + value.length,
                list.length - index);
        return outgoing;
    }


    final public char[] splice(char[] list,
                               char value, int index) {
        char[] outgoing = new char[list.length + 1];
        System.arraycopy(list, 0, outgoing, 0, index);
        outgoing[index] = value;
        System.arraycopy(list, index, outgoing, index + 1,
                list.length - index);
        return outgoing;
    }

    final public char[] splice(char[] list,
                               char[] value, int index) {
        char[] outgoing = new char[list.length + value.length];
        System.arraycopy(list, 0, outgoing, 0, index);
        System.arraycopy(value, 0, outgoing, index, value.length);
        System.arraycopy(list, index, outgoing, index + value.length,
                list.length - index);
        return outgoing;
    }

    final public int[] splice(int[] list,
                              int value, int index) {
        int[] outgoing = new int[list.length + 1];
        System.arraycopy(list, 0, outgoing, 0, index);
        outgoing[index] = value;
        System.arraycopy(list, index, outgoing, index + 1,
                list.length - index);
        return outgoing;
    }

    final public int[] splice(int[] list,
                              int[] value, int index) {
        int[] outgoing = new int[list.length + value.length];
        System.arraycopy(list, 0, outgoing, 0, index);
        System.arraycopy(value, 0, outgoing, index, value.length);
        System.arraycopy(list, index, outgoing, index + value.length,
                list.length - index);
        return outgoing;
    }

    final public float[] splice(float[] list,
                                float value, int index) {
        float[] outgoing = new float[list.length + 1];
        System.arraycopy(list, 0, outgoing, 0, index);
        outgoing[index] = value;
        System.arraycopy(list, index, outgoing, index + 1,
                list.length - index);
        return outgoing;
    }

    final public float[] splice(float[] list,
                                float[] value, int index) {
        float[] outgoing = new float[list.length + value.length];
        System.arraycopy(list, 0, outgoing, 0, index);
        System.arraycopy(value, 0, outgoing, index, value.length);
        System.arraycopy(list, index, outgoing, index + value.length,
                list.length - index);
        return outgoing;
    }

    final public String[] splice(String[] list,
                                 String value, int index) {
        String[] outgoing = new String[list.length + 1];
        System.arraycopy(list, 0, outgoing, 0, index);
        outgoing[index] = value;
        System.arraycopy(list, index, outgoing, index + 1,
                list.length - index);
        return outgoing;
    }

    final public String[] splice(String[] list,
                                 String[] value, int index) {
        String[] outgoing = new String[list.length + value.length];
        System.arraycopy(list, 0, outgoing, 0, index);
        System.arraycopy(value, 0, outgoing, index, value.length);
        System.arraycopy(list, index, outgoing, index + value.length,
                list.length - index);
        return outgoing;
    }

    @SuppressWarnings("SuspiciousSystemArraycopy")
    final public Object splice(Object list, Object value, int index) {
        Class<?> type = list.getClass().getComponentType();
        Object outgoing;
        int length = Array.getLength(list);

        // check whether item being spliced in is an array
        if (value.getClass().getName().charAt(0) == '[') {
            int vlength = Array.getLength(value);
            outgoing = Array.newInstance(type, length + vlength);
            System.arraycopy(list, 0, outgoing, 0, index);
            System.arraycopy(value, 0, outgoing, index, vlength);
            System.arraycopy(list, index, outgoing, index + vlength, length - index);
        } else {
            outgoing = Array.newInstance(type, length + 1);
            System.arraycopy(list, 0, outgoing, 0, index);
            Array.set(outgoing, index, value);
            System.arraycopy(list, index, outgoing, index + 1, length - index);
        }
        return outgoing;
    }


    /**
     * Extracts an array of elements from an existing array. The list
     * parameter defines the array from which the elements will be copied, and the
     * start and count parameters specify which elements to extract.
     * If no count is given, elements will be extracted from the
     * start to the end of the array. When specifying the start,
     * remember that the first array element is 0. This function does not change
     * the source array.
     * <p>
     * When using an array of objects, the data returned from the function must be
     * cast to the object array's data type. For example: SomeClass[] items =
     * (SomeClass[]) subset(originalArray, 0, 4)
     *
     * @param list  array to extract from
     * @param start position to begin
     * @webref data:array functions
     * @webBrief Extracts an array of elements from an existing array
     */

    public boolean[] subset(boolean[] list, int start) {
        return subset(list, start, list.length - start);
    }

    public boolean[] subset(boolean[] list, int start, int count) {
        boolean[] output = new boolean[count];
        System.arraycopy(list, start, output, 0, count);
        return output;
    }


    public byte[] subset(byte[] list, int start) {
        return subset(list, start, list.length - start);
    }


    public byte[] subset(byte[] list, int start, int count) {
        byte[] output = new byte[count];
        System.arraycopy(list, start, output, 0, count);
        return output;
    }


    public char[] subset(char[] list, int start) {
        return subset(list, start, list.length - start);
    }


    public char[] subset(char[] list, int start, int count) {
        char[] output = new char[count];
        System.arraycopy(list, start, output, 0, count);
        return output;
    }


    public int[] subset(int[] list, int start) {
        return subset(list, start, list.length - start);
    }


    public int[] subset(int[] list, int start, int count) {
        int[] output = new int[count];
        System.arraycopy(list, start, output, 0, count);
        return output;
    }


    public long[] subset(long[] list, int start) {
        return subset(list, start, list.length - start);
    }


    public long[] subset(long[] list, int start, int count) {
        long[] output = new long[count];
        System.arraycopy(list, start, output, 0, count);
        return output;
    }


    public float[] subset(float[] list, int start) {
        return subset(list, start, list.length - start);
    }


    public float[] subset(float[] list, int start, int count) {
        float[] output = new float[count];
        System.arraycopy(list, start, output, 0, count);
        return output;
    }


    public double[] subset(double[] list, int start) {
        return subset(list, start, list.length - start);
    }


    public double[] subset(double[] list, int start, int count) {
        double[] output = new double[count];
        System.arraycopy(list, start, output, 0, count);
        return output;
    }


    public String[] subset(String[] list, int start) {
        return subset(list, start, list.length - start);
    }


    public String[] subset(String[] list, int start, int count) {
        String[] output = new String[count];
        System.arraycopy(list, start, output, 0, count);
        return output;
    }


    public Object subset(Object list, int start) {
        int length = Array.getLength(list);
        return subset(list, start, length - start);
    }


    @SuppressWarnings("SuspiciousSystemArraycopy")
    public Object subset(Object list, int start, int count) {
        Class<?> type = list.getClass().getComponentType();
        Object outgoing = Array.newInstance(type, count);
        System.arraycopy(list, start, outgoing, 0, count);
        return outgoing;
    }


    /**
     * Concatenates two arrays. For example, concatenating the array { 1, 2, 3 }
     * and the array { 4, 5, 6 } yields { 1, 2, 3, 4, 5, 6 }. Both parameters must
     * be arrays of the same datatype.
     * <p>
     * When using an array of objects, the data returned from the function must be
     * cast to the object array's data type. For example: SomeClass[] items =
     * (SomeClass[]) concat(array1, array2).
     *
     * @param a first array to concatenate
     * @param b second array to concatenate
     * @webref data:array functions
     * @webBrief Concatenates two arrays
     */
    public boolean[] concat(boolean[] a, boolean[] b) {
        boolean[] c = new boolean[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public char[] concat(char[] a, char[] b) {
        char[] c = new char[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public int[] concat(int[] a, int[] b) {
        int[] c = new int[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public float[] concat(float[] a, float[] b) {
        float[] c = new float[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public double[] concat(double[] a, double[] b) {
        double[] c = new double[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public String[] concat(String[] a, String[] b) {
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    @SuppressWarnings("SuspiciousSystemArraycopy")
    public Object concat(Object a, Object b) {
        Class<?> type = a.getClass().getComponentType();
        int alength = Array.getLength(a);
        int blength = Array.getLength(b);
        Object outgoing = Array.newInstance(type, alength + blength);
        System.arraycopy(a, 0, outgoing, 0, alength);
        System.arraycopy(b, 0, outgoing, alength, blength);
        return outgoing;
    }

    //


    /**
     * Reverses the order of an array.
     *
     * @param list booleans[], bytes[], chars[], ints[], floats[], doubles[], or Strings[]
     * @webref data:array functions
     * @webBrief Reverses the order of an array
     */
    public boolean[] reverse(boolean[] list) {
        boolean[] outgoing = new boolean[list.length];
        int length1 = list.length - 1;
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[length1 - i];
        }
        return outgoing;
    }

    public byte[] reverse(byte[] list) {
        byte[] outgoing = new byte[list.length];
        int length1 = list.length - 1;
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[length1 - i];
        }
        return outgoing;
    }

    public char[] reverse(char[] list) {
        char[] outgoing = new char[list.length];
        int length1 = list.length - 1;
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[length1 - i];
        }
        return outgoing;
    }

    public int[] reverse(int[] list) {
        int[] outgoing = new int[list.length];
        int length1 = list.length - 1;
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[length1 - i];
        }
        return outgoing;
    }

    public float[] reverse(float[] list) {
        float[] outgoing = new float[list.length];
        int length1 = list.length - 1;
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[length1 - i];
        }
        return outgoing;
    }

    public double[] reverse(double[] list) {
        double[] outgoing = new double[list.length];
        int length1 = list.length - 1;
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[length1 - i];
        }
        return outgoing;
    }

    public String[] reverse(String[] list) {
        String[] outgoing = new String[list.length];
        int length1 = list.length - 1;
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[length1 - i];
        }
        return outgoing;
    }

    public Object reverse(Object list) {
        Class<?> type = list.getClass().getComponentType();
        int length = Array.getLength(list);
        Object outgoing = Array.newInstance(type, length);
        for (int i = 0; i < length; i++) {
            Array.set(outgoing, i, Array.get(list, (length - 1) - i));
        }
        return outgoing;
    }


    //////////////////// STRINGS ////////////////////

    /**
     * Removes whitespace characters from the beginning and end of a String. In
     * addition to standard whitespace characters such as space, carriage
     * return, and tab, this function also removes the Unicode "nbsp" (U+00A0)
     * character and the zero width no-break space (U+FEFF) character.
     */
    public String trim(String str) {
        if (str == null) {
            return null;
        }
        return str.replace('\u00A0', ' ').replace('\uFEFF', ' ').trim();
    }


    public String[] trim(String[] array) {
        if (array == null) {
            return null;
        }
        String[] outgoing = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                outgoing[i] = trim(array[i]);
            }
        }
        return outgoing;
    }


    /**
     * Combines an array of Strings into one String, each separated by the
     * character(s) used for the separator parameter. To join arrays of
     * ints or floats, it's necessary to first convert them to Strings using
     * nf() or nfs().
     */
    public String join(String[] list, char separator) {
        return join(list, String.valueOf(separator));
    }


    public String join(String[] list, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.length; i++) {
            if (i != 0) sb.append(separator);
            sb.append(list[i]);
        }
        return sb.toString();
    }


    /**
     * The splitTokens() function splits a String at one or many character
     * delimiters or "tokens". The delim parameter specifies the character
     * or characters to be used as a boundary.
     * <p>
     * If no delim characters are specified, any whitespace character is
     * used to split. Whitespace characters include tab (\t), line feed
     * (\n), carriage return (\r), form feed (\f), and space.
     * <p>
     * After using this function to parse incoming data, it is common to convert
     * the data from Strings to integers or floats by using the datatype
     * conversion functions int() and float().
     */


    public String[] splitTokens(String value) {
        return splitTokens(value, " \t\n\r\f\u00A0");
    }


    public String[] splitTokens(String value, String delim) {
        StringTokenizer toker = new StringTokenizer(value, delim);
        String[] pieces = new String[toker.countTokens()];

        int index = 0;
        while (toker.hasMoreTokens()) {
            pieces[index++] = toker.nextToken();
        }
        return pieces;
    }


    /**
     * The split() function breaks a String into pieces using a character
     * or string as the delimiter. The delim parameter specifies the
     * character or characters that mark the boundaries between each piece. A
     * String[] array is returned that contains each of the pieces.
     * <p>
     * If the result is a set of numbers, you can convert the String[] array to a
     * float[] or int[] array using the datatype conversion functions int()
     * and float(). (See the second example above.)
     * <p>
     * The splitTokens() function works in a similar fashion, except that
     * it splits using a range of characters instead of a specific character or
     * sequence. <!--
     * <p>
     * This function uses regular expressions to determine how the delim
     * parameter divides the str parameter. Therefore, if you use
     * characters such parentheses and brackets that are used with regular
     * expressions as a part of the delim parameter, you'll need to put two
     * backslashes (\\\\) in front of the character (see example above). You can
     * read more about
     * <a href="http://en.wikipedia.org/wiki/Regular_expression">regular
     * expressions</a> and
     * <a href="http://en.wikipedia.org/wiki/Escape_character">escape
     * characters</a> on Wikipedia. -->
     */
    public String[] split(String value, char delim) {
        // do this so that the exception occurs inside the user's
        // program, rather than appearing to be a bug inside split()
        if (value == null) return null;
        //return split(what, String.valueOf(delim));  // huh

        char[] chars = value.toCharArray();
        int splitCount = 0; //1;
        for (char ch : chars) {
            if (ch == delim) splitCount++;
        }

        if (splitCount == 0) {
            String[] splits = new String[1];
            splits[0] = value;
            return splits;
        }
        String[] splits = new String[splitCount + 1];
        int splitIndex = 0;
        int startIndex = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == delim) {
                splits[splitIndex++] =
                        new String(chars, startIndex, i - startIndex);
                startIndex = i + 1;
            }
        }
        splits[splitIndex] = new String(chars, startIndex, chars.length - startIndex);
        return splits;
    }


    public String[] split(String value, String delim) {
        List<String> items = new ArrayList<>();
        int index;
        int offset = 0;
        while ((index = value.indexOf(delim, offset)) != -1) {
            items.add(value.substring(offset, index));
            offset = index + delim.length();
        }
        items.add(value.substring(offset));
        String[] outgoing = new String[items.size()];
        items.toArray(outgoing);
        return outgoing;
    }


    /**
     * This function is used to apply a regular expression to a piece of text, and
     * return matching groups (elements found inside parentheses) as a String
     * array. If there are no matches, a null value will be returned. If no groups
     * are specified in the regular expression, but the sequence matches, an array
     * of length 1 (with the matched text as the first element of the array) will
     * be returned.
     * <p>
     * To use the function, first check to see if the result is null. If the
     * result is null, then the sequence did not match at all. If the sequence did
     * match, an array is returned.
     * <p>
     * If there are groups (specified by sets of parentheses) in the regular
     * expression, then the contents of each will be returned in the array.
     * Element [0] of a regular expression match returns the entire matching
     * string, and the match groups start at element [1] (the first group is [1],
     * the second [2], and so on).
     * <p>
     * The syntax can be found in the reference for Java's
     * "https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html" Pattern
     * class. For regular expression syntax, read the
     * "https://docs.oracle.com/javase/tutorial/essential/regex/" Java
     * Tutorial on the topic.
     *
     * @webref data:string_functions
     * @webBrief The function is used to apply a regular expression to a
     * piece of text, and return matching groups (elements found inside
     * parentheses) as a String array
     * @param str
     * the String to be searched
     * @param regexp
     * the regexp to be used for matching
     */

    public LinkedHashMap<String, Pattern> matchPatterns;

    Pattern matchPattern(String regexp) {
        Pattern p = null;
        if (matchPatterns == null) {
            matchPatterns = new LinkedHashMap<>(16, 0.75f, true) {
                @Override
                public boolean removeEldestEntry(Map.Entry<String, Pattern> eldest) {
                    return size() == 10;
                }
            };
        } else {
            p = matchPatterns.get(regexp);
        }
        if (p == null) {
            p = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL);
            matchPatterns.put(regexp, p);
        }
        return p;
    }


    public String[] match(String str, String regexp) {
        Pattern p = matchPattern(regexp);
        Matcher m = p.matcher(str);
        if (m.find()) {
            int count = m.groupCount() + 1;
            String[] groups = new String[count];
            for (int i = 0; i < count; i++) {
                groups[i] = m.group(i);
            }
            return groups;
        }
        return null;
    }


    /**
     * This function is used to apply a regular expression to a piece of text, and
     * return a list of matching groups (elements found inside parentheses) as a
     * two-dimensional String array. If there are no matches, a null value will be
     * returned. If no groups are specified in the regular expression, but the
     * sequence matches, a two dimensional array is still returned, but the second
     * dimension is only of length one.
     * <p>
     * To use the function, first check to see if the result is null. If the
     * result is null, then the sequence did not match at all. If the sequence did
     * match, a 2D array is returned.
     * <p>
     * If there are groups (specified by sets of parentheses) in the regular
     * expression, then the contents of each will be returned in the array.
     * Assuming a loop with counter variable i, element [i][0] of a regular
     * expression match returns the entire matching string, and the match groups
     * start at element [i][1] (the first group is [i][1], the second [i][2], and
     * so on).
     * <p>
     * The syntax can be found in the reference for Java's
     * "https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html" Pattern
     * class. For regular expression syntax, read the
     * "https://docs.oracle.com/javase/tutorial/essential/regex/" Java
     * Tutorial on the topic.
     *
     * @param str    the String to be searched
     * @param regexp the regexp to be used for matching
     * @webref data:string_functions
     * @webBrief This function is used to apply a regular expression to a piece of
     * text
     */
    public String[][] matchAll(String str, String regexp) {
        Pattern p = matchPattern(regexp);
        Matcher m = p.matcher(str);
        List<String[]> results = new ArrayList<>();
        int count = m.groupCount() + 1;
        while (m.find()) {
            String[] groups = new String[count];
            for (int i = 0; i < count; i++) {
                groups[i] = m.group(i);
            }
            results.add(groups);
        }
        if (results.isEmpty()) {
            return null;
        }
        String[][] matches = new String[results.size()][count];
        for (int i = 0; i < matches.length; i++) {
            matches[i] = results.get(i);
        }
        return matches;
    }


    //////////////////// CASTING FUNCTIONS ////////////////////

    /**
     * <p>Convert an integer to a boolean. Because of how Java handles upgrading
     * numbers, this will also cover byte and char (as they will upgrade to
     * an int without any sort of explicit cast).</p>
     * <p>The preprocessor will convert boolean(what) to parseBoolean(what).</p>
     *
     * @return false if 0, true if any other number
     */
    final public boolean parseBoolean(int what) {
        return (what != 0);
    }

    /**
     * Convert the string "true" or "false" to a boolean.
     *
     * @return true if 'what' is "true" or "TRUE", false otherwise
     */
    final public boolean parseBoolean(String what) {
        return Boolean.parseBoolean(what);
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    /**
     * Convert an int array to a boolean array. An int equal
     * to zero will return false, and any other value will return true.
     *
     * @return array of boolean elements
     */
    final public boolean[] parseBoolean(int[] what) {
        boolean[] outgoing = new boolean[what.length];
        for (int i = 0; i < what.length; i++) {
            outgoing[i] = (what[i] != 0);
        }
        return outgoing;
    }

    final public boolean[] parseBoolean(String[] what) {
        boolean[] outgoing = new boolean[what.length];
        for (int i = 0; i < what.length; i++) {
            outgoing[i] = Boolean.parseBoolean(what[i]);
        }
        return outgoing;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    final public byte parseByte(boolean what) {
        return what ? (byte) 1 : 0;
    }

    final public byte parseByte(char what) {
        return (byte) what;
    }

    final public byte parseByte(int what) {
        return (byte) what;
    }

    final public byte parseByte(double what) {
        return (byte) what;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    final public byte[] parseByte(boolean[] what) {
        byte[] outgoing = new byte[what.length];
        for (int i = 0; i < what.length; i++) {
            outgoing[i] = what[i] ? (byte) 1 : 0;
        }
        return outgoing;
    }

    final public byte[] parseByte(char[] what) {
        byte[] outgoing = new byte[what.length];
        for (int i = 0; i < what.length; i++) {
            outgoing[i] = (byte) what[i];
        }
        return outgoing;
    }

    final public byte[] parseByte(int[] what) {
        byte[] outgoing = new byte[what.length];
        for (int i = 0; i < what.length; i++) {
            outgoing[i] = (byte) what[i];
        }
        return outgoing;
    }

    final public byte[] parseByte(double[] what) {
        byte[] outgoing = new byte[what.length];
        for (int i = 0; i < what.length; i++) {
            outgoing[i] = (byte) what[i];
        }
        return outgoing;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    final public char parseChar(byte what) {
        return (char) (what & 0xff);
    }

    final public char parseChar(int what) {
        return (char) what;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    final public char[] parseChar(byte[] what) {
        char[] outgoing = new char[what.length];
        for (int i = 0; i < what.length; i++) {
            outgoing[i] = (char) (what[i] & 0xff);
        }
        return outgoing;
    }

    final public char[] parseChar(int[] what) {
        char[] outgoing = new char[what.length];
        for (int i = 0; i < what.length; i++) {
            outgoing[i] = (char) what[i];
        }
        return outgoing;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    final public int parseInt(boolean what) {
        return what ? 1 : 0;
    }

    /**
     * Note that parseInt() will un-sign a signed byte value.
     */
    final public int parseInt(byte what) {
        return what & 0xff;
    }

    /**
     * Note that parseInt('5') is unlike String in the sense that it
     * won't return 5, but the ascii value. This is because ((int) someChar)
     * returns the ascii value, and parseInt() is just longhand for the cast.
     */
    final public int parseInt(char what) {
        return what;
    }

    /**
     * Same as floor(), or an (int) cast.
     */
    final public int parseInt(double what) {
        return (int) what;
    }

    /**
     * Parse a String into an int value. Returns 0 if the value is bad.
     */
    final public int parseInt(String what) {
        return parseInt(what, 0);
    }

    /**
     * Parse a String to an int, and provide an alternate value that
     * should be used when the number is invalid. If there's a decimal place,
     * it will be truncated, making this more of a toInt() than parseInt()
     * function. This is because the method is used internally for casting.
     * Not ideal, but the name was chosen before that clarification was made.
     */
    final public int parseInt(String what, int otherwise) {
        try {
            int offset = what.indexOf('.');
            if (offset == -1) {
                return Integer.parseInt(what);
            } else {
                return Integer.parseInt(what.substring(0, offset));
            }
        } catch (NumberFormatException e) {
            return otherwise;
        }
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    final public int[] parseInt(boolean[] what) {
        int[] list = new int[what.length];
        for (int i = 0; i < what.length; i++) {
            list[i] = what[i] ? 1 : 0;
        }
        return list;
    }

    final public int[] parseInt(byte[] what) {  // note this un-signs
        int[] list = new int[what.length];
        for (int i = 0; i < what.length; i++) {
            list[i] = (what[i] & 0xff);
        }
        return list;
    }

    final public int[] parseInt(char[] what) {
        int[] list = new int[what.length];
        for (int i = 0; i < what.length; i++) {
            list[i] = what[i];
        }
        return list;
    }

    public int[] parseInt(double[] what) {
        int[] inties = new int[what.length];
        for (int i = 0; i < what.length; i++) {
            inties[i] = (int) what[i];
        }
        return inties;
    }

    /**
     * Make an array of int elements from an array of String objects.
     * If the String can't be parsed as a number, it will be set to zero.
     * <p>
     * String s[] = { "1", "300", "44" };
     * int numbers[] = parseInt(s);
     * <p>
     * numbers will contain { 1, 300, 44 }
     */
    public int[] parseInt(String[] what) {
        return parseInt(what, 0);
    }

    /**
     * Make an array of int elements from an array of String objects.
     * If the String can't be parsed as a number, its entry in the
     * array will be set to the value of the "missing" parameter.
     * <p>
     * String s[] = { "1", "300", "apple", "44" };
     * int numbers[] = parseInt(s, 9999);
     * <p>
     * numbers will contain { 1, 300, 9999, 44 }
     */
    public int[] parseInt(String[] what, int missing) {
        int[] output = new int[what.length];
        for (int i = 0; i < what.length; i++) {
            try {
                output[i] = Integer.parseInt(what[i]);
            } catch (NumberFormatException e) {
                output[i] = missing;
            }
        }
        return output;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    /**
     * Convert an int to a float value. Also handles bytes because of
     * Java's rules for upgrading values.
     */
    final public double parseDouble(int what) {  // also handles byte
        return what;
    }

    final public double parseDouble(String what) {
        return parseDouble(what, Double.NaN);
    }

    final public double parseDouble(String what, double otherwise) {
        try {
            return Double.parseDouble(what);
        } catch (NumberFormatException ignored) {
        }

        return otherwise;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    final public double[] parseDouble(byte[] what) {
        double[] doublies = new double[what.length];
        for (int i = 0; i < what.length; i++) {
            doublies[i] = what[i];
        }
        return doublies;
    }

    final public double[] parseDouble(int[] what) {
        double[] doublies = new double[what.length];
        for (int i = 0; i < what.length; i++) {
            doublies[i] = what[i];
        }
        return doublies;
    }

    final public double[] parseDouble(String[] what) {
        return parseDouble(what, Double.NaN);
    }

    final public double[] parseDouble(String[] what, double missing) {
        double[] output = new double[what.length];
        for (int i = 0; i < what.length; i++) {
            try {
                output[i] = Double.parseDouble(what[i]);
            } catch (NumberFormatException e) {
                output[i] = missing;
            }
        }
        return output;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    final public String str(boolean x) {
        return String.valueOf(x);
    }

    final public String str(byte x) {
        return String.valueOf(x);
    }

    final public String str(char x) {
        return String.valueOf(x);
    }

    final public String str(int x) {
        return String.valueOf(x);
    }

    final public String str(double x) {
        return String.valueOf(x);
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    final public String[] str(boolean[] x) {
        String[] s = new String[x.length];
        for (int i = 0; i < x.length; i++) s[i] = String.valueOf(x[i]);
        return s;
    }

    final public String[] str(byte[] x) {
        String[] s = new String[x.length];
        for (int i = 0; i < x.length; i++) s[i] = String.valueOf(x[i]);
        return s;
    }

    final public String[] str(char[] x) {
        String[] s = new String[x.length];
        for (int i = 0; i < x.length; i++) s[i] = String.valueOf(x[i]);
        return s;
    }

    final public String[] str(int[] x) {
        String[] s = new String[x.length];
        for (int i = 0; i < x.length; i++) s[i] = String.valueOf(x[i]);
        return s;
    }

    final public String[] str(double[] x) {
        String[] s = new String[x.length];
        for (int i = 0; i < x.length; i++) s[i] = String.valueOf(x[i]);
        return s;
    }


    //////////////////// INT NUMBER FORMATTING ////////////////////

    public String nf(double num) {
        int inum = (int) num;
        if (num == inum) {
            return str(inum);
        }
        return str(num);
    }


    public String[] nf(double[] nums) {
        String[] outgoing = new String[nums.length];
        for (int i = 0; i < nums.length; i++) {
            outgoing[i] = nf(nums[i]);
        }
        return outgoing;
    }


    /**
     * Integer number formatter.
     */
    public NumberFormat int_nf;
    public int int_nf_digits;
    public boolean int_nf_commas;


    /**
     * Utility function for formatting numbers into strings. There are two
     * versions: one for formatting floates, and one for formatting ints. The
     * values for the digits and right parameters should always be
     * positive integers. The left parameter should be positive or 0. If it
     * is zero, only the right side is formatted.
     * <p>
     * As shown in the above example, nf() is used to add zeros to the left
     * and/or right of a number. This is typically for aligning a list of numbers.
     * To remove digits from a floating-point number, use the
     * int(), ceil(), floor(), or round() functions.
     *
     * @param nums   the numbers to format
     * @param digits number of digits to pad with zero
     * @webref data:string_functions
     * @webBrief Utility function for formatting numbers into strings
     */
    public String[] nf(int[] nums, int digits) {
        String[] formatted = new String[nums.length];
        for (int i = 0; i < formatted.length; i++) {
            formatted[i] = nf(nums[i], digits);
        }
        return formatted;
    }


    /**
     * @param num the number to format
     */
    public String nf(int num, int digits) {
        if ((int_nf != null) &&
                (int_nf_digits == digits) &&
                !int_nf_commas) {
            return int_nf.format(num);
        }

        int_nf = NumberFormat.getInstance();
        int_nf.setGroupingUsed(false); // no commas
        int_nf_commas = false;
        int_nf.setMinimumIntegerDigits(digits);
        int_nf_digits = digits;
        return int_nf.format(num);
    }


    /**
     * @param nums the numbers to format
     * @LOL Utility function for formatting numbers into strings and placing
     * appropriate commas to mark units of 1000. There are four versions: one for
     * formatting ints, one for formatting an array of ints, one for formatting
     * doubles, and one for formatting an array of doubles.
     * <p>
     * The value for the right parameter should always be a positive
     * integer.
     * <p>
     * For a non-US locale, this will insert periods instead of commas,
     * or whatever is appropriate for that region.
     * @webref data:string_functions
     * @webBrief Utility function for formatting numbers into strings and placing
     * appropriate commas to mark units of 1000
     */
    public String[] nfc(int[] nums) {
        String[] formatted = new String[nums.length];
        for (int i = 0; i < formatted.length; i++) {
            formatted[i] = nfc(nums[i]);
        }
        return formatted;
    }


    /**
     * @param num the number to format
     */
    public String nfc(int num) {
        if ((int_nf != null) &&
                (int_nf_digits == 0) &&
                int_nf_commas) {
            return int_nf.format(num);
        }

        int_nf = NumberFormat.getInstance();
        int_nf.setGroupingUsed(true);
        int_nf_commas = true;
        int_nf.setMinimumIntegerDigits(0);
        int_nf_digits = 0;
        return int_nf.format(num);
    }


    /**
     * Utility function for formatting numbers into strings. Similar to
     * nf() but leaves a blank space in front of positive numbers so
     * they align with negative numbers in spite of the minus symbol. There are
     * two versions, one for formatting floats and one for formatting ints. The
     * values for the digits, left, and right parameters
     * should always be positive integers.
     *
     * @param num    the number to format
     * @param digits number of digits to pad with zeroes
     * @webref data:string_functions
     * @webBrief Utility function for formatting numbers into strings
     */
    public String nfs(int num, int digits) {
        return (num < 0) ? nf(num, digits) : (' ' + nf(num, digits));
    }


    /**
     * @param nums the numbers to format
     */
    public String[] nfs(int[] nums, int digits) {
        String[] formatted = new String[nums.length];
        for (int i = 0; i < formatted.length; i++) {
            formatted[i] = nfs(nums[i], digits);
        }
        return formatted;
    }


    /**
     * Utility function for formatting numbers into strings. Similar to nf()
     * but puts a "+" in front of positive numbers and a "-" in front of negative
     * numbers. There are two versions: one for formatting floats, and one for
     * formatting ints. The values for the digits, left, and
     * right parameters should always be positive integers.
     *
     * @param num    the number to format
     * @param digits number of digits to pad with zeroes
     * @webref data:string_functions
     * @webBrief Utility function for formatting numbers into strings
     */
    public String nfp(int num, int digits) {
        return (num < 0) ? nf(num, digits) : ('+' + nf(num, digits));
    }


    /**
     * @param nums the numbers to format
     */
    public String[] nfp(int[] nums, int digits) {
        String[] formatted = new String[nums.length];
        for (int i = 0; i < formatted.length; i++) {
            formatted[i] = nfp(nums[i], digits);
        }
        return formatted;
    }


    //////////////////////////////////////////////////////////////

    // DOUBLE NUMBER FORMATTING

    public NumberFormat double_nf;
    public int double_nf_left, double_nf_right;
    public boolean double_nf_commas;

    /**
     * @param left  number of digits to the left of the decimal point
     * @param right number of digits to the right of the decimal point
     */
    public String[] nf(double[] nums, int left, int right) {
        String[] formatted = new String[nums.length];
        for (int i = 0; i < formatted.length; i++) {
            formatted[i] = nf(nums[i], left, right);
        }
        return formatted;
    }

    public String nf(double num, int left, int right) {
        if ((double_nf != null) &&
                (double_nf_left == left) &&
                (double_nf_right == right) &&
                !double_nf_commas) {
            return double_nf.format(num);
        }

        double_nf = NumberFormat.getInstance();
        double_nf.setGroupingUsed(false);
        double_nf_commas = false;

        if (left != 0) double_nf.setMinimumIntegerDigits(left);
        if (right != 0) {
            double_nf.setMinimumFractionDigits(right);
            double_nf.setMaximumFractionDigits(right);
        }
        double_nf_left = left;
        double_nf_right = right;
        return double_nf.format(num);
    }

    /**
     * @param right number of digits to the right of the decimal point
     */
    public String[] nfc(double[] nums, int right) {
        String[] formatted = new String[nums.length];
        for (int i = 0; i < formatted.length; i++) {
            formatted[i] = nfc(nums[i], right);
        }
        return formatted;
    }

    public String nfc(double num, int right) {
        if ((double_nf != null) &&
                (double_nf_left == 0) &&
                (double_nf_right == right) &&
                double_nf_commas) {
            return double_nf.format(num);
        }

        double_nf = NumberFormat.getInstance();
        double_nf.setGroupingUsed(true);
        double_nf_commas = true;

        if (right != 0) {
            double_nf.setMinimumFractionDigits(right);
            double_nf.setMaximumFractionDigits(right);
        }
        double_nf_left = 0;
        double_nf_right = right;
        return double_nf.format(num);
    }


    /**
     * @param left  the number of digits to the left of the decimal point
     * @param right the number of digits to the right of the decimal point
     */
    public String[] nfs(double[] nums, int left, int right) {
        String[] formatted = new String[nums.length];
        for (int i = 0; i < formatted.length; i++) {
            formatted[i] = nfs(nums[i], left, right);
        }
        return formatted;
    }

    public String nfs(double num, int left, int right) {
        return (num < 0) ? nf(num, left, right) : (' ' + nf(num, left, right));
    }

    /**
     * @param left  the number of digits to the left of the decimal point
     * @param right the number of digits to the right of the decimal point
     */
    public String[] nfp(double[] nums, int left, int right) {
        String[] formatted = new String[nums.length];
        for (int i = 0; i < formatted.length; i++) {
            formatted[i] = nfp(nums[i], left, right);
        }
        return formatted;
    }

    public String nfp(double num, int left, int right) {
        return (num < 0) ? nf(num, left, right) : ('+' + nf(num, left, right));
    }


    //////////////////// HEX / BINARY CONVERSION ////////////////////

    /**
     * Converts an int, byte, char, or color to a
     * String containing the equivalent hexadecimal notation. For example,
     * the color value produced by color(0, 102, 153) will convert
     * to the String value "FF006699". This function can help make
     * your geeky debugging sessions much happier.
     * <p>
     * Note that the maximum number of digits is 8, because an int value
     * can only represent up to 32 bits. Specifying more than 8 digits will not
     * increase the length of the String further.
     *
     * @webref data:conversion
     * @webBrief Converts a byte, char, int, or color to a String containing the
     * equivalent hexadecimal notation
     */
    final public String hex(byte value) {
        return hex(value, 2);
    }

    final public String hex(char value) {
        return hex(value, 4);
    }

    final public String hex(int value) {
        return hex(value, 8);
    }

    final public String hex(int value, int digits) {
        String stuff = Integer.toHexString(value).toUpperCase();
        if (digits > 8) {
            digits = 8;
        }

        int length = stuff.length();
        if (length > digits) {
            return stuff.substring(length - digits);
        } else if (length < digits) {
            return "00000000".substring(8 - (digits - length)) + stuff;
        }
        return stuff;
    }

    /**
     * Converts a String representation of a hexadecimal number to its
     * equivalent integer value.
     */
    final public int unhex(String value) {
        return (int) (Long.parseLong(value, 16));
    }

    /**
     * Returns a String that contains the binary value of a byte.
     * The returned value will always have 8 digits.
     */
    final public String binary(byte value) {
        return binary(value, 8);
    }

    /**
     * Returns a String that contains the binary value of a char.
     * The returned value will always have 16 digits because chars
     * are two bytes long.
     */
    final public String binary(char value) {
        return binary(value, 16);
    }

    /**
     * Returns a String that contains the binary value of an int. The length
     * depends on the size of the number itself. If you want a specific number
     * of digits use binary(int what, int digits) to specify how many.
     */
    final public String binary(int value) {
        return binary(value, 32);
    }

    /*
     * Returns a String that contains the binary value of an int.
     * The digits parameter determines how many digits will be used.
     */

    /**
     * Converts an int, byte, char, or color to a
     * String containing the equivalent binary notation. For example, the
     * color value produced by color(0, 102, 153, 255) will convert
     * to the String value "11111111000000000110011010011001". This
     * function can help make your geeky debugging sessions much happier.
     * <p>
     * Note that the maximum number of digits is 32, because an int value
     * can only represent up to 32 bits. Specifying more than 32 digits will have
     * no effect.
     */
    final public String binary(int value, int digits) {
        String stuff = Integer.toBinaryString(value);
        if (digits > 32) {
            digits = 32;
        }

        int length = stuff.length();
        if (length > digits) {
            return stuff.substring(length - digits);
        } else if (length < digits) {
            int offset = 32 - (digits - length);
            return "00000000000000000000000000000000".substring(offset) + stuff;
        }
        return stuff;
    }


    /**
     * Converts a String representation of a binary number to its equivalent
     * integer value. For example, unbinary("00001000") will return
     * 8.
     *
     * @webref data:conversion
     * @webBrief Converts a String representation of a binary number to its
     * equivalent integer value
     */
    final public int unbinary(String value) {
        return Integer.parseInt(value, 2);
    }


    public Template() {
        generateSinCos();
    }

}