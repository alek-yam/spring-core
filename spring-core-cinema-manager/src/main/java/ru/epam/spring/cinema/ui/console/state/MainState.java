package ru.epam.spring.cinema.ui.console.state;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;

/**
 * Main state that handles main application actions
 *
 * @author Yuriy_Tkach/Alex_Yamskov
 */
public class MainState extends AbstractState {

    private final Map<Integer, AbstractState> childStates = new HashMap<>();

    public MainState(ApplicationContext context) {
        childStates.put(1, new AuditoriumManageState(context));
        childStates.put(2, new EventManageState(context));
        childStates.put(3, new UserManageState(context));
        childStates.put(4, new BookingState(context));
    }

    @Override
    protected void runAction(int action) {
        AbstractState state = childStates.get(action);
        if (state != null) {
            state.run();
        } else {
            System.err.println("No state configured for selected action :(");
        }
    }

    @Override
    protected int printMainActions() {
        System.out.println(" 1) View auditoriums");
        System.out.println(" 2) Manage events");
        System.out.println(" 3) Manage users");
        System.out.println(" 4) Book tickets");
        return 4;
    }

    @Override
    protected void printDefaultInformation() {
        // Doing nothing here
    }

}
