package com.neon.newton.core.theme;

public record Theme(String id, String name, String cssPath, boolean isAtlantaFX) {
    @Override
    public String toString() {
        return name;
    }
}
