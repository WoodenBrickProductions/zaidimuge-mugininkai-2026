package com.example.universityjava;

public class AnimationSettings {

    private static boolean enableAnimationsPower = true;
    private static boolean enableAnimationsSettings = true;

    public static boolean areAnimationsEnabled() {
        return enableAnimationsPower && enableAnimationsSettings;
    }

    public static void setEnableAnimations(boolean enabled) {
        enableAnimationsPower = enabled;
    }
    public static void setEnableAnimationsSettings(boolean enabled) {
        enableAnimationsSettings = enabled;
    }
}
