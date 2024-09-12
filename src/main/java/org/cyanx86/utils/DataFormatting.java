package org.cyanx86.utils;

public class DataFormatting {

    public static String formatSecondsToTime(int timeseconds) {
        int minutes = (int)Math.floor((double)timeseconds / 60);
        int seconds = timeseconds % 60;

        return (
            (minutes < 10 ? "0" : "") +
            minutes +
            ":" +
            seconds
        );
    }

}
