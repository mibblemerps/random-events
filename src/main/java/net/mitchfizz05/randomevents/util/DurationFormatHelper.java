package net.mitchfizz05.randomevents.util;

public class DurationFormatHelper
{
    private static String padNumber(int number)
    {
        String str = Integer.toString(number);

        if (str.length() == 1) { // Hacky, but works
            return "0" + str;
        } else if (str.length() == 0) {
            return "00";
        }
        return str;
    }

    public static String formatSeconds(int seconds)
    {
        int minutes = (int) Math.floor(seconds / 60);
        int hours = (int) Math.floor(minutes / 60);

        if (hours > 0) {
            return String.format("%s:%s:%s %s", hours, padNumber(minutes % 60), padNumber(seconds % 60), (seconds / 60 / 60 == 1) ? "hour" : "hours");
        } else if (minutes > 0) {
            return String.format("%s:%s %s", minutes, padNumber(seconds % 60), (seconds / 60 == 1) ? "minute" : "minutes");
        } else {
            return String.format("%s %s", seconds, (seconds == 1) ? "second" : "seconds");
        }
    }
}
