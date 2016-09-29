package com.dy.health;

import java.time.LocalTime;

public final class TimeRange {
    private final LocalTime start;
    private final LocalTime end;

    TimeRange(LocalTime startExclusive, LocalTime endInclusive) {
        this.start = startExclusive;
        this.end = endInclusive;
    }

    public boolean isWithinTimeRange(LocalTime time) {
        return isTimeRangeUnbound() || time.isAfter(start) && (time.isBefore(end) || time.equals(end));
    }

    private boolean isTimeRangeUnbound() {
        return start.equals(end);
    }
}
