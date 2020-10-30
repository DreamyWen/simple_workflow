package com.saith.workflow;

public class PrintFunction extends Operation<String, String> {

    public PrintFunction(String name) {
        this.name = name;
        this.type = "func";
    }

    @Override
    public String expand(String s) {
        if (s != null) {
            System.out.println(s);
        }
        return s;
    }
}
