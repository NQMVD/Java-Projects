package com.noah;

public class Variable {
    public String name, type, display;
    public boolean hasDisplay;

    public Variable(String[] splitted) {
        this.fromArray(splitted);
    }

    public void fromArray(String[] arr) {
        this.name = arr[0];
        this.type = arr[1];
        if (arr.length > 2) {
            this.hasDisplay = true;
            this.display = arr[2];
        }
    }

    @Override
    public String toString() {
        return "    name = " + this.name +
                " | type = " + this.type +
                (this.hasDisplay ? " | display = " + this.display : "");
    }
}