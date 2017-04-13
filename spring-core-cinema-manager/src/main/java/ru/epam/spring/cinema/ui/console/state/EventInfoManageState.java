package ru.epam.spring.cinema.ui.console.state;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map.Entry;

import ru.epam.spring.cinema.domain.Auditorium;
import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.service.AuditoriumService;
import ru.epam.spring.cinema.service.EventService;


public class EventInfoManageState extends AbstractState {

    private final Event event;
    private final EventService eventService;
    private final AuditoriumService auditoriumService;

    public EventInfoManageState(Event event, EventService eventService, AuditoriumService auditoriumService) {
        this.event = event;
        this.eventService = eventService;
        this.auditoriumService = auditoriumService;
    }

    @Override
    protected void printDefaultInformation() {
        System.out.println("Information about Event: " + event.getName());
    }

    @Override
    protected int printMainActions() {
        System.out.println(" 1) View air dates");
        System.out.println(" 2) Add air date");
        System.out.println(" 3) View assigned auditoriums");
        System.out.println(" 4) Assign auditorium");
        return 4;
    }

    @Override
    protected void runAction(int action) {
        switch (action) {
        case 1:
            viewAirDates();
            break;
        case 2:
            addAirDate();
            break;
        case 3:
            viewAssignedAuditoriums();
            break;
        case 4:
            assignAuditorium();
            break;
        default:
            System.err.println("Unknown action");
        }
    }

    private void assignAuditorium() {
        System.out.println("Select auditorium:");
        List<Auditorium> list = new ArrayList<Auditorium>(auditoriumService.getAll());
        for (int i = 0; i < list.size(); i++) {
            System.out.println("[" + (i+1) + "] " + list.get(i).getName());
        }

        int auditoriumIndex = readIntInput("Input index: ", list.size()) - 1;

        Auditorium aud = list.get(auditoriumIndex);
        System.out.println("Assigning auditorium: " + aud.getName());
        List<Calendar> datesList = new ArrayList<Calendar>(event.getAirDates());
        for (int i = 0; i < datesList.size(); i++) {
            System.out.println("[" + (i+1) + "] " + formatDateTime(datesList.get(i)));
        }

        int dateTimeIndex = readIntInput("Input air dateTime index: ", datesList.size()) - 1;

        Calendar dt = datesList.get(dateTimeIndex);
        if (event.assignAuditorium(dt, aud)) {
        	eventService.save(event);
            System.out.println("Assigned auditorium for air dateTime: " + formatDateTime(dt));
        } else {
            System.err.println("Failed to assign for air dateTime: " + formatDateTime(dt));
        }
    }

    private void viewAssignedAuditoriums() {
        System.out.println("Event airs in: ");
        for (Entry<Calendar,String> e : event.getAuditoriums().entrySet()) {
        	System.out.println(formatDateTime(e.getKey()) + " " + e.getValue());
        }
    }

    private void addAirDate() {
        Calendar airDate = readDateTimeInput("Air date (" + DATE_TIME_INPUT_PATTERN + "): ");
        event.addAirDateTime(airDate);
        //eventService.save(event);
    }

    private void viewAirDates() {
        System.out.println("Event airs on: ");
        for (Calendar dt : event.getAirDates()) {
        	System.out.println(formatDateTime(dt));
        }
    }
}
