package com.noah;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Datapack {
    String name;
    ArrayList<MCFile> files;

    public Datapack(String name) {
        this.name = name;
        this.files = new ArrayList<>();
    }

    public void getVars(String filepath) {
        MCFile file = new MCFile(filepath);
        if (file.containsVars())
            this.files.add(file);
    }

    public boolean containsFile(String filepath) {
        return filepath.contains(this.name);
    }

    @Override
    public String toString() {
//        StringBuilder allVars = new StringBuilder();
        ArrayList<Variable> vars = new ArrayList<>();
        for (MCFile f : this.files) {
//            allVars.append(f.toString());
            vars.addAll(f.varsInFile);
        }
        vars = format(sort(vars));

        StringBuilder allVars = new StringBuilder();
        for (Variable v : vars)
            allVars.append(v.toString()).append("\n");


        return "Datapack [" + this.name + "] (" + vars.size() + " objectives) = {\n" +
                allVars +
                "}";
    }

    public static ArrayList<Variable> sort(ArrayList<Variable> vars) {
        String[] list = new String[vars.size()];
        for (int i = 0; i < vars.size(); i++)
            list[i] = vars.get(i).name;

        Arrays.sort(list);

        ArrayList<Variable> sorted = new ArrayList<>();

        for (int i = 0; i < vars.size(); i++)
            for (int j = 0; j < vars.size(); j++)
                if (Objects.equals(list[i], vars.get(j).name))
                    sorted.add(vars.get(j));

        return sorted;
    }

    public static ArrayList<Variable> format(ArrayList<Variable> vars) {
        int longestName = 0, longestType = 0;
        for (Variable var : vars) {
            int lenN = var.name.length();
            int lenT = var.type.length();
            if (lenN > longestName) longestName = lenN;
            if (lenT > longestType) longestType = lenT;
        }

        for (int i = 0; i < vars.size(); i++) {
            StringBuilder name = new StringBuilder(vars.get(i).name);
            StringBuilder type = new StringBuilder(vars.get(i).type);

            if (longestName > vars.get(i).name.length())
                for (int j = 0; j < longestName - vars.get(i).name.length(); j++)
                    name.append(" ");

            if (longestType > vars.get(i).type.length())
                for (int j = 0; j < longestType - vars.get(i).type.length(); j++)
                    type.append(" ");

            vars.get(i).name = name.toString();
            vars.get(i).type = type.toString();
        }

        return vars;
    }
}