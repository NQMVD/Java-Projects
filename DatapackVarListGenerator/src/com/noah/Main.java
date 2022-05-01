package com.noah;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        String pathToWorld = "C:\\Users\\noah1\\Desktop\\NewMCServer\\datapacks";

        File parentFile = new File(pathToWorld);
        String[] datapacksList = parentFile.list();
        ArrayList<String> functionFiles = new ArrayList<>();
        ArrayList<Datapack> completeList = new ArrayList<>();


        try (Stream<Path> walk = Files.walk(Paths.get(pathToWorld))) {
            List<String> result = walk.map(Path::toString).filter(f -> f.endsWith(".mcfunction")).toList();
            functionFiles.addAll(result);

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < Objects.requireNonNull(datapacksList).length; i++) {
            completeList.add(new Datapack(datapacksList[i]));
        }

        for (int i = 0; i < functionFiles.size(); i++) {
            for (Datapack pack : completeList) {
                if (pack.containsFile(functionFiles.get(i)))
                    pack.getVars(functionFiles.get(i));
            }
        }

        System.out.println();
        System.out.println(toString(completeList));

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String toString(ArrayList list) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < list.size()-1; i++) {
            result.append(list.get(i).toString()).append(",\n");
        }
        result.append(list.get(list.size()-1).toString()).append("\n");
        return result.toString();
    }

    public static String toString(ArrayList list, String prefix) {
        StringBuilder result = new StringBuilder();
        result.append(prefix);
        for (int i = 0; i < list.size()-1; i++) {
            result.append(list.get(i).toString()).append(",\n").append(prefix);
        }
        result.append(list.get(list.size()-1).toString()).append("\n");
        return result.toString();
    }

    public static String toString(ArrayList list, String prefix, String suffix) {
        StringBuilder result = new StringBuilder();
        result.append(prefix);
        for (int i = 0; i < list.size()-1; i++) {
            result.append(list.get(i).toString()).append(suffix).append("\n").append(prefix);
        }
        result.append(list.get(list.size()-1).toString()).append(suffix).append("\n");
        return result.toString();
    }

    public static String loadString(String filename) {
        String[] lines = loadStrings(filename);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            result.append(lines[i]).append("\n");
        }
        return result.toString();
    }

    public static String[] loadStrings(String filename) {
        InputStream is = createInput(filename);
        if (is != null) {
            String[] strArr = loadStrings(is);
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return strArr;
        }

        return null;
    }

    public static String[] loadStrings(InputStream input) {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        return loadStrings(reader);
    }


    public static String[] loadStrings(BufferedReader reader) {
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

            String[] output = new String[lineCount];
            System.arraycopy(lines, 0, output, 0, lineCount);
            return output;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream createInput(String filename) {
        InputStream input = createInputRaw(filename);
        if (input != null) {
            return new BufferedInputStream(input);
        }
        return null;
    }


    public static InputStream createInputRaw(String filename) {
        if (filename == null) return null;
        InputStream stream;
        try {
            File file = new File(filename);
            if (!file.exists()) {
                System.out.println("File doesnt exist");
                System.exit(0);
            }

            if (file.isDirectory()) {
                return null;
            }
            if (file.exists()) {
                try {
                    String filePath = file.getCanonicalPath();
                    String filenameActual = new File(filePath).getName();
                    String filenameShort = new File(filename).getName();
                    if (!filenameActual.equals(filenameShort)) {
                        throw new RuntimeException("This file is named " +
                                filenameActual + " not " +
                                filename + ". Rename the file " +
                                "or change your code.");
                    }
                } catch (IOException ignored) {
                }
            }

            return new FileInputStream(file);

        } catch (IOException | SecurityException ignored) {
        }

        ClassLoader cl = Main.class.getClassLoader();

        stream = cl.getResourceAsStream("data/" + filename);
        if (stream != null) {
            String cn = stream.getClass().getName();
            if (!cn.equals("sun.plugin.cache.EmptyInputStream")) {
                return stream;
            }
        }

        stream = cl.getResourceAsStream(filename);
        if (stream != null) {
            String cn = stream.getClass().getName();
            if (!cn.equals("sun.plugin.cache.EmptyInputStream")) {
                return stream;
            }
        }

        try {
            try {
                try {
                    return new FileInputStream(filename);
                } catch (IOException ignored) {
                }

                try {
                    return new FileInputStream(filename);
                } catch (Exception ignored) {
                }

                try {
                    return new FileInputStream(filename);
                } catch (IOException ignored) {
                }
            } catch (SecurityException ignored) {
            }  // online, whups
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
