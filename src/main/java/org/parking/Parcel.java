package org.parking;

public class Parcel {
    private final String[] lines;

    public Parcel(String packageStr) {
        this.lines = packageStr.split("\n");
    }

    public int getWidth() {
        return lines[0].length();
    }

    public int getHeight() {
        return lines.length;
    }

    public String[] getLines() {
        return lines;
    }
}