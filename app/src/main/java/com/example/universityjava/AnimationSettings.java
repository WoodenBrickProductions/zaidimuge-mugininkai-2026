package com.example.universityjava;

public class AnimationSettings {

    private static boolean enableAnimations = true;

    public static boolean areAnimationsEnabled() {
        return enableAnimations;
    }

    public static void setEnableAnimations(boolean enabled) {
        enableAnimations = enabled;
    }
}
