package com.example.unisicma;

public class ChildList {
    public ChildList(String child_name, String child_id) {
        Child_name = child_name;
        Child_id = child_id;
    }

    public String getChild_name() {
        return Child_name;
    }

    public void setChild_name(String child_name) {
        Child_name = child_name;
    }

    public String getChild_id() {
        return Child_id;
    }

    public void setChild_id(String child_id) {
        Child_id = child_id;
    }

    private String Child_name;
    private String Child_id;
}
