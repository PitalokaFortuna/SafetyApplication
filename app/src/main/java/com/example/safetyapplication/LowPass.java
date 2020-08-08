package com.example.safetyapplication;

public class LowPass {
    private static final float ALPHA = 0.1f;
    private LowPass(){

    }
    public static float[] filter(float[] input, float[] prev) {
        if (input == null || prev == null) throw new NullPointerException("input and prev float arrays must be non-NULL");
        if (input.length != prev.length) throw new IllegalArgumentException("input and prev must be the same length");

        for (int i = 0; i < input.length; i++) {
            prev[i] = prev[i] + ALPHA * (input[i] - prev[i]);
            //System.out.println("Input : " +input[i] + "Prev Filter : " +prev[i]);
        }
        return prev;
    }
}
