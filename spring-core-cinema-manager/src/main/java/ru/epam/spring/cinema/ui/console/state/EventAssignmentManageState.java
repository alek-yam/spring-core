package ru.epam.spring.cinema.ui.console.state;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ru.epam.spring.cinema.domain.Auditorium;
import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.domain.EventAssignment;
import ru.epam.spring.cinema.service.AuditoriumService;
import ru.epam.spring.cinema.service.EventService;


public class EventAssignmentManageState extends AbstractState {
    private final Event event;
    private final EventService eventService;
    private final AuditoriumService auditoriumService;

    public EventAssignmentManageState(Event event, EventService eventService, AuditoriumService auditoriumService) {
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
        System.out.println(" 1) View all assignments");
        System.out.println(" 2) Add new assignment");
        return 2;
    }

    @Override
    protected void runAction(int action) {
        switch (action) {
        case 1:
            viewAllAssignments();
            break;
        case 2:
        	addNewAssignment();
            break;
        default:
            System.err.println("Unknown action");
        }
    }

    private void viewAllAssignments() {
    	System.out.println("Event assignments: ");
    	event.getAssignments().forEach(a -> {
	    		Auditorium aud = auditoriumService.getById(a.getAuditoriumId());
	    		System.out.println(formatDateTime(a.getAirDate()) + " " + aud.getName());
    		});
    }

    private void addNewAssignment() {
        System.out.println("Select auditorium:");
        List<Auditorium> list = new ArrayList<Auditorium>(auditoriumService.getAll());
        for (int i = 0; i < list.size(); i++) {
            System.out.println("[" + (i+1) + "] " + list.get(i).getName());
        }

        int auditoriumIndex = readIntInput("Input index: ", list.size()) - 1;
        Auditorium aud = list.get(auditoriumIndex);
        System.out.println("Assigning auditorium: " + aud.getName());

        LocalDateTime airDate = readDateTimeInput("Enter air date (" + DATE_TIME_INPUT_PATTERN + "): ");

        EventAssignment assidnment = new EventAssignment();
        assidnment.setEventId(event.getId());
        assidnment.setAuditoriumId(aud.getId());
        assidnment.setAirDate(airDate);

        if (event.getAssignments().add(assidnment)) {
        	eventService.save(event);
            System.out.println("Assigned auditorium for air date: " + formatDateTime(airDate));
        } else {
            System.err.println("Failed to assign for air date: " + formatDateTime(airDate));
        }
    }
}
