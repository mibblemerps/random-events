package net.mitchfizz05.randomevents.util;

public class TimeHelper
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

    /**
     * Format a number of seconds into a human-friendly form, formatted as a duration with appropriate units.
     *
     * @param seconds Seconds
     * @return Human-friendly duration
     */
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

    /**
     * Convert seconds to ticks.
     * (1 second = 20 ticks)
     *
     * Will be rounded <b>up</b> to the nearest tick.
     *
     * @param seconds Seconds
     * @return Ticks
     */
    public static int secsToTicks(double seconds)
    {
        return (int) Math.ceil(seconds * 20);
    }

    /**
     * Convert minutes to ticks.
     *
     * @param minutes Minutes
     * @return Ticks
     */
    public static int minsToTicks(double minutes)
    {
        return secsToTicks(minutes * 60);
    }

    /**
     * Convert hours to ticks.
     *
     * @param hours Hours
     * @return Ticks
     */
    public static int hrsToTicks(double hours)
    {
        return minsToTicks(hours * 60);
    }

    /**
     * Convert minutes to seconds.
     *
     * Will be rounded to the <b>nearest</b> second.
     *
     * @param minutes Minutes
     * @return Seconds
     */
    public static int minsToSecs(double minutes)
    {
        return (int) Math.round(minutes * 60);
    }

    /**
     * Convert hours to seconds.
     *
     * Will be rounded to the <b>nearest</b> second.
     *
     * @param hours Hours
     * @return Seconds
     */
    public static int hrsToSecs(double hours)
    {
        return minsToSecs(hours * 60);
    }
}
