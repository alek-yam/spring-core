package ru.epam.spring.cinema.ui.console.state;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

import ru.epam.spring.cinema.ui.console.convertor.AbstractConverter;
import ru.epam.spring.cinema.ui.console.convertor.DateConverter;
import ru.epam.spring.cinema.ui.console.convertor.DoubleConverter;
import ru.epam.spring.cinema.ui.console.convertor.IntegerConverter;
import ru.epam.spring.cinema.ui.console.convertor.LongConverter;
import ru.epam.spring.cinema.ui.console.convertor.SetLongConverter;


/**
 * Abstract state class that defined basic methods to get user input and call
 * for actions
 *
 * @author Yuriy_Tkach/Alex_Yamskov
 */
public abstract class AbstractState {
    public static final String DATE_TIME_INPUT_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String ONLY_DATE_INPUT_PATTERN = "yyyy-MM-dd";

    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_INPUT_PATTERN);
    private static SimpleDateFormat onlyDateFormat = new SimpleDateFormat(ONLY_DATE_INPUT_PATTERN);

    protected static final IntegerConverter integerConverter = new IntegerConverter();
    protected static final LongConverter longConverter = new LongConverter();
    protected static final DoubleConverter doubleConverter = new DoubleConverter();
    protected static final DateConverter dateTimeConverter = new DateConverter(DATE_TIME_INPUT_PATTERN);
    protected static final DateConverter onlyDateConverter = new DateConverter(ONLY_DATE_INPUT_PATTERN);
    protected static final SetLongConverter setLongConverter = new SetLongConverter();

    private static Scanner scanner = new Scanner(System.in, "UTF-8");

    public void run() {
        printDefaultInformation();
        int action = 0;
        do {
            printDelimiter();
            System.out.println("What would you like to do?");
            int maxInput = printMainActions();
            System.out.println(" 0) Return");
            action = readUserActionInput(maxInput);
            System.out.println("");
            if (action > 0) {
                runAction(action);
            }
        } while (action > 0);
    }

    public void printDelimiter() {
        System.out.println("--------------------------------------------");
    }

    protected String readStringInput(String prefix) {
        System.out.print(prefix);
        String line = scanner.nextLine();
        return line;
    }

    protected <R> R readInput(String prefix, AbstractConverter<R> converter) {
        R input = null;
        do {
            String str = readStringInput(prefix);
            try {
                input = converter.convert(str);
            } catch (Exception e) {
                System.err.println("Failed to convert: " + e.getMessage());
                input = null;
            }
        } while (input == null);
        return input;
    }

    protected int readIntInput(String prefix) {
        return readInput(prefix, integerConverter);
    }

    protected int readIntInput(final String prefix, final int max) {
    	return readInput(prefix, new IntegerConverter(max));
    }

    protected long readLongInput(String prefix) {
        return readInput(prefix, longConverter);
    }

    protected double readDoubleInput(String prefix) {
        return readInput(prefix, doubleConverter);
    }

    protected Calendar readDateTimeInput(String prefix) {
    	Calendar dt = new GregorianCalendar();
    	dt.setTime(readInput(prefix, dateTimeConverter));
    	return dt;
    }

    protected Calendar readDateInput(String prefix) {
    	Calendar dt = new GregorianCalendar();
    	dt.setTime(readInput(prefix, onlyDateConverter));
    	return dt;
    }

    protected String formatDateTime(Calendar dt) {
    	return dateTimeFormat.format(dt.getTime());
    }

    protected String formatDate(Calendar dt) {
    	return onlyDateFormat.format(dt.getTime());
    }

    protected abstract void printDefaultInformation();

    protected abstract int printMainActions();

    protected abstract void runAction(int action);

    private int readUserActionInput(int maxInput) {
        do {
            int action = readIntInput("Please, input desired action: ");
            if (action >= 0 && action <= maxInput) {
                return action;
            }
        } while (true);
    }

}
