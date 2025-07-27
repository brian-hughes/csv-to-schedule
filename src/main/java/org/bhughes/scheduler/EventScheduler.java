package org.bhughes.scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventScheduler {
    public static int MAX_PEOPLE_PER_DAY = 6;

    public static void printSchedule(Collection<Day> days) {
        printSchedule(days, "");
    }

    public static void printSchedule(Collection<Day>days, String header) {
        System.out.println("\n=====\n" + header + "\n=====");
        days.forEach(d -> {
            System.out.println("\n" + d.day() + "\n---------------");

            System.out.println("\nAttendees");
            d.attendees().forEach(a -> System.out.println(a));

            System.out.println("\nWaitlist");
            d.waitList().forEach(a -> System.out.println(a));
        });
    }

    public static void scheduleEvents(List<Person> people, HashMap<String, Day>days) {

        assignOneSession(people);
        printSchedule(days.values(), "First Pass: Assigned everyone we can get in at least once.");
        
        // Second pass: fill remaining slots with unassigned people who have multiple options
        assignMultiDayAttendees(people, new ArrayList<>(days.values()));
        printSchedule(days.values(), "Second Pass: Assigned multi-session attendees");


        //TODO: This hasn't been needed yet so I haven't re-implemented it in the refactored design
        //Leaving the old code commented out so it can be a guide if it's needed
        // Third pass: try to balance the group
        //balanceSchedule(schedule);
        //printSchedule(days.values(), "Third Pass: Balance changes if a day is under-attended");
        
        // Fourth pass: Add in any who can be waitlisted
        fillWaitList(people);
        printSchedule(days.values(), "Final Pass: Create waitlists");

        System.out.println("\n\nAttendees that cannot be placed: " + people.stream().filter(attendee -> attendee.scheduledDays().isEmpty()).toList());
    }

    static void assignOneSession(List<Person> attendees) {
        List<Person> sortedAttendees = new ArrayList<>(attendees);
        sortedAttendees.sort(Comparator.comparingInt(p -> p.availableDays().size()));
        for (Person person : sortedAttendees) {
            System.out.println(person.name());
            for (Day day : person.availableDays()) {
                if (day.attendees().size() < MAX_PEOPLE_PER_DAY) {
                    System.out.println("Scheduling " + person + " for " + day);
                    day.attendees().add(person);
                    person.scheduledDays().add(day);
                    break;
                }
            }
        }
    }

    static void fillWaitList(List<Person> attendees) {
        List<Person> sortedAttendees = new ArrayList<>(attendees);
        for (Person person : sortedAttendees) {
            for (Day day : person.availableDays()) {
                if(!person.scheduledDays().contains(day)) {
                    person.waitlistedDays().add(day);
                    day.waitList().add(person);
                }
            }
        }
    }

    static void assignMultiDayAttendees(List<Person> attendees, List<Day> days) {
        final AtomicBoolean filledSomeone = new AtomicBoolean(true);

        //Loop until we can't assign anybody else (either all days are full, or everyone got assgined every day)
        while(filledSomeone.get()) {
            filledSomeone.set(false);
            for(Person attendee: attendees) {
                if(!attendee.multiSession()) {
                    continue;
                }
                //Look at each day the attendee is available
                attendee.availableDays()
                    .stream()
                    //Don't consider days that are already full
                    .filter(d -> d.attendees().size() < MAX_PEOPLE_PER_DAY)
                    //Skip days they are already scheduled
                    .filter(d -> !attendee.scheduledDays().contains(d))
                    //Find the most "empty" day so we fill those first
                    .sorted((o1, o2) -> o1.attendees().size() > o2.attendees().size() ? -1 : 0)
                    //We only assign this person to 1 extra spot, then goto the next person
                    //if we go through all the people we will loop back and start over and give this person another 
                    //shot at an entry
                    .findFirst()
                    //if we find one we assign them 
                    .ifPresent(d -> {
                        filledSomeone.set(true);
                        d.attendees().add(attendee);
                        attendee.scheduledDays().add(d);
                    });
            }
        }
    }

    // static boolean fillMultiDayAttendees(List<Person> people, Map<String, List<Person>>schedule) {
    //     boolean filledSomeone = false;
    //     for (Person person : people) {
    //         if(person.multiSession()) {
    //             for(Entry<String, List<Person>> e : sortAscScheduleByAttendeeSize(schedule).entrySet()) {
    //                 //we havn't maxed this day already 
    //                 //and this person has this day on thier list 
    //                 //and the person isn't already scheduled on this day
    //                 if(e.getValue().size() < MAX_PEOPLE_PER_DAY
    //                     && person.availableDays().contains(e.getKey()) 
    //                     && !e.getValue().contains(person)) {
                            
    //                         System.out.println("Adding bonus day " + e.getKey() + " for attendee: " + person.name());
    //                         e.getValue().add(person);
    //                         filledSomeone = true; //only allow one at a time
    //                         break;
    //                 }
    //             }
    //         }
    //     }
    //     return filledSomeone;
    // }

    // static boolean isBalanced(Map<String, List<Person>> scheduleMap) {
    //     int highCount = 0;
    //     int lowCount = 1000;
    //     for(Entry<String,List<Person>> e : scheduleMap.entrySet()) {
    //         if(e.getValue().size() > highCount) {
    //             highCount = e.getValue().size();
    //         }
    //         if(e.getValue().size() < lowCount) {
    //             lowCount = e.getValue().size();
    //         }
    //     }
    //     //If there is 1 person differnece we are balanced, otherwise we want to try to balance it.
    //     return highCount - lowCount < 2;
    // }

    // static void balanceSchedule(Map<String, List<Person>> scheduleMap) {
    //     while(!isBalanced(scheduleMap)) {
    //         if(!performBalance(scheduleMap)) {
    //             break;
    //         }
    //     }
        
    // }



    // static Map<String, List<Person>> sortAscScheduleByAttendeeSize(Map<String, List<Person>> map) {
    //     List<Entry<String, List<Person>>> list = new ArrayList<>(map.entrySet());
    //     list.sort((o1, o2) -> o1.getValue().size() < o2.getValue().size() ? -1 : 0);
    //     Map<String, List<Person>> result = new LinkedHashMap<>();
    //     list.forEach(e -> result.put(e.getKey(), e.getValue()));
    //     return result;
    // }

    // static void balanceSchedule(List<Day> days) {

    // }

    // static boolean performBalance(Map<String, List<Person>> scheduleMap) {
    //     Map<String, List<Person>> result = sortAscScheduleByAttendeeSize(scheduleMap);
    //     List<Entry<String, List<Person>>> list = new ArrayList<>(result.entrySet());
    //     Entry<String, List<Person>> underBalanced = list.get(0);

    //     //Sanity check that we are not going to go over our max per day
    //     if(underBalanced.getValue().size() >= MAX_PEOPLE_PER_DAY) {
    //         return false;
    //     }
    //     ListIterator<Entry<String, List<Person>>> listIterator = list.listIterator(list.size());

    //     while(listIterator.hasPrevious()) {
    //         Entry<String, List<Person>> overBalanced = listIterator.previous();
    //         Optional<Person> gotOne = overBalanced.getValue().stream().filter(e -> e.availableDays().contains(underBalanced.getKey())).findFirst();
    //         if(gotOne.isPresent()) {
    //             overBalanced.getValue().remove(gotOne.get());
    //             underBalanced.getValue().add(gotOne.get());
    //             System.out.println("Balancing: " + underBalanced.getKey() + " and " + overBalanced.getKey() 
    //             + "- Moving " + gotOne.get().name());
    //             return true;
    //         }
    //         else {
    //             System.out.println("Nobody available to move from: " + underBalanced.getKey() + " and " + overBalanced.getKey());
    //         }
    //     }


    //     return false;
    // }
}
