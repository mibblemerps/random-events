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
    public static int secsToTicks(float seconds)
    {
        return (int) Math.ceil(seconds * 20);
    }

    /**
     * Convert minutes to ticks.
     *
     * @param minutes Minutes
     * @return Ticks
     */
    public static int minsToTicks(float minutes)
    {
        return secsToTicks(minutes * 60);
    }

    /**
     * Convert hours to ticks.
     *
     * @param hours Hours
     * @return Ticks
     */
    public static int hrsToTicks(float hours)
    {
        return minsToTicks(hours * 60);
    }
}
