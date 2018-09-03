package com.boss.template;

public class Test_class {
    private String res;

    public void set(String data) {
        this.res = data;
    }

    public String get_result() {
        return this.res;
    }

    public byte[] get_bytes() {
        String str = "hello";
        byte[] bytes = str.getBytes();

        return bytes;
    }
}