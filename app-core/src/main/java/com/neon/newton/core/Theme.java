package com.neon.newton.core;

public record Theme(String id, String name, String cssPath) {
    @Override
    public String toString() {
        return name;
    }
}
