package com.ejs.affirmations.model;

public class categoriaMeditacionClass {
    private int id;
    private String string;

    public categoriaMeditacionClass(int id, String string) {
        this.id = id;
        this.string = string;
    }

    public categoriaMeditacionClass() {

    }

    public int getid() {
        return id;
    }
    public void setid(int id) {
        this.id = id;
    }

    public String getstring() {
        return string;
    }
    public void setstring(String string) {
        this.string = string;
    }
}
