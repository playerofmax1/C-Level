package com.clevel.kudu.model;

public enum Function {
    F0000("Test Function", "ทดสอบ Function", 0),

    F0001("Add/Edit", "Add/Edit", 10),
    F0002("View other person time sheet", "View other person time sheet", 20),
    F0003("Edit other person time sheet", "Edit other person time sheet", 30),
    F0004("Lock/unlock time sheet", "Lock/unlock time sheet", 40),
    ;

    String nameEn;
    String nameTh;
    int order;

    Function(String nameEn, String nameTh, int order) {
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

    public static Function fromNameTh(String nameTh) {
        for (Function f : Function.values()) {
            if (f.getNameTh().equalsIgnoreCase(nameTh)) {
                return f;
            }
        }
        return null;
    }

    public static Function fromNameEn(String nameEn) {
        for (Function f : Function.values()) {
            if (f.getNameEn().equalsIgnoreCase(nameEn)) {
                return f;
            }
        }
        return null;
    }

}
