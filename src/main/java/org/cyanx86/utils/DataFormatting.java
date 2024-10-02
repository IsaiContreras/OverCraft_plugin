package org.cyanx86.utils;

public class DataFormatting {

    public static String formatSecondsToTime(int timeseconds) {
        int minutes = (int)Math.floor((double)timeseconds / 60);
        int seconds = timeseconds % 60;

        return (
            (minutes < 10 ? "0" : "") +
            minutes +
            ":" +
            (seconds < 10 ? "0" : "") +
            seconds
        );
    }

    public static String formatMaterialToString(String mat) {
        String modString = mat.toLowerCase();

        return modString.replace("_", " ");
    }

    public static String repeate(int i, String s) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(s).repeat(Math.max(0, i)));
        return sb.toString();
    }
}
