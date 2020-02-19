package com.clevel.kudu.model;

public enum Screen {
    S0000("Test Page","Teat Page",0),
    S0001("Customer Management","Customer Management",10),
    S0002("Rate Management","Task Management",20),
    S0003("Task Management","Task Management",30),
    S0004("Project Management","Project Management",40),
    S0005("User Management","User Management",50),
    S0006("Role Management","Role Management",60),
    S0007("TimeSheet","TimeSheet",60),
    S0008("Holiday","Holiday",70),
    S0009("Reports","Reports",80);

    String nameEn;
    String nameTh;
    int order;

    Screen(String nameEn, String nameTh, int order) {
        this.nameEn = nameEn;
        this.nameTh = nameTh;
        this.order = order;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getNameTh() {
        return nameTh;
    }

    public int getOrder() {
        return order;
    }

    public static Screen fromNameTh(String nameTh) {
        for (Screen s: Screen.values()) {
            if (s.getNameTh().equalsIgnoreCase(nameTh)) {
                return s;
            }
        }
        return null;
    }

    public static Screen fromNameEn(String nameEn) {
        for (Screen s: Screen.values()) {
            if (s.getNameEn().equalsIgnoreCase(nameEn)) {
                return s;
            }
        }
        return null;
    }

}