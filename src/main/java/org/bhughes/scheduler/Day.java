package org.bhughes.scheduler;

import java.util.List;

public record Day (
    String day,
    List<Person> attendees,
    List<Person> waitList
){
    @Override
    public String toString() {
        return "Day{name='" + day + "'}";
    }
}