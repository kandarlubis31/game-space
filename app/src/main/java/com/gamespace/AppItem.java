package com.gamespace;

public class AppItem implements Comparable<AppItem> {
    public String name;
    public String pkg;
    
    public AppItem(String name, String pkg) {
        this.name = name;
        this.pkg = pkg;
    }
    
    @Override
    public int compareTo(AppItem o) {
        return this.name.compareToIgnoreCase(o.name);
    }
    
    @Override
    public String toString() {
        return this.name + "\n" + this.pkg;
    }
}