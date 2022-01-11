package com.pretzel.dev.villagertradelimiter.data;

import com.pretzel.dev.villagertradelimiter.lib.Util;

public class Cooldown {
    private enum Interval {
        s(1000L),
        m(60000L),
        h(3600000L),
        d(86400000L),
        w(604800000L);

        final long factor;
        Interval(long factor) {
            this.factor = factor;
        }
    }

    /**
     * @param timeStr The cooldown time as written in config.yml (7d, 30s, 5m, etc)
     * @return The cooldown time in milliseconds
     */
    public static long parseTime(final String timeStr) {
        try {
            long time = Long.parseLong(timeStr.substring(0, timeStr.length()-1));
            String interval = timeStr.substring(timeStr.length()-1).toLowerCase();
            return time * Interval.valueOf(interval).factor;
        } catch (Exception e) {
            Util.errorMsg(e);
        }
        return 0;
    }
}
