package com.noah;

import java.util.ArrayList;
import java.util.Objects;

import static com.noah.Main.loadStrings;

public class MCFile {
    public String subFolderName;
    public ArrayList<Variable> varsInFile = new ArrayList<>();

    public MCFile(String path) {
        this.subFolderName = path.split("\\\\")[8];
        this.getVars(path);
    }

    public void getVars(String path) {
        String[] lines = loadStrings(path);

        for (int j = 0; j < Objects.requireNonNull(lines).length; j++) {
            if (lines[j].startsWith("scoreboard objectives add ")) {
                String sub = lines[j].substring(26);
                String[] splitted = sub.split("\\s+", 3);
                varsInFile.add(new Variable(splitted));
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder vars = new StringBuilder();
        for (Variable v : this.varsInFile) {
            vars.append(v.toString()).append('\n');
        }
        return vars.toString();
    }

    public boolean containsVars() {
        return !this.varsInFile.isEmpty();
    }
}
