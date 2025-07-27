package org.bhughes.scheduler;

import java.util.Date;
import java.util.List;

public record Person (
    String name,
    List<Day> availableDays,
    List<Day> scheduledDays,
    List<Day> waitlistedDays,
    Date submissionDate,
    boolean multiSession
) {
    @Override
    public String toString() {
        return name;
    }
}