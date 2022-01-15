package com.pretzel.dev.villagertradelimiter.data;

import com.pretzel.dev.villagertradelimiter.lib.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Cooldown {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    private enum Interval {
        s(1L),
        m(60L),
        h(3600L),
        d(86400L),
        w(604800L);

        final long factor;
        Interval(long factor) {
            this.factor = factor;
        }
    }

    /**
     * @param cooldownStr The cooldown time as written in config.yml (7d, 30s, 5m, etc)
     * @return The cooldown time in seconds
     */
    public static long parseCooldown(final String cooldownStr) {
        if(cooldownStr.equals("0")) return 0;
        try {
            long time = Long.parseLong(cooldownStr.substring(0, cooldownStr.length()-1));
            String interval = cooldownStr.substring(cooldownStr.length()-1).toLowerCase();
            return time * Interval.valueOf(interval).factor;
        } catch (Exception e) {
            Util.errorMsg(e);
        }
        return 0;
    }

    public static String formatTime(Date date) {
        return FORMAT.format(date);
    }

    public static Date parseTime(final String timeStr) {
        try {
            return FORMAT.parse(timeStr);
        } catch (ParseException e) {
            Util.errorMsg(e);
        }
        return null;
    }
}
