package ru.epam.spring.cinema.ui.console.state;

import ru.epam.spring.cinema.domain.DomainObject;
import ru.epam.spring.cinema.service.AbstractService;

/**
 * Abstract state that manages basic actions that are defined in
 * {@link AbstractService}
 *
 * @author Yuriy_Tkach/Alex_Yamskov
 *
 * @param <T>
 *            DomainObject class
 * @param <S>
 *            Service to work with DomainObject
 */
public abstract class AbstractDomainObjectManageState<T extends DomainObject, S extends AbstractService<T>>
        extends AbstractState {

    protected S service;

    public AbstractDomainObjectManageState(S service) {
        this.service = service;
    }

    @Override
    protected void printDefaultInformation() {
        System.out
                .println("Currently there are " + service.getAll().size() + " " + getObjectName() + "s in the system");
    }

    @Override
    protected final int printMainActions() {
        System.out.println(" 1) Add " + getObjectName());
        System.out.println(" 2) View all " + getObjectName() + "s");
        System.out.println(" 3) Find " + getObjectName() + " by id");
        System.out.println(" 4) Remove " + getObjectName());
        int subActions = printSubActions(4);
        return 4 + subActions;
    }

    protected abstract T createObject();

    protected abstract String getObjectName();

    protected abstract void printObject(T object);

    protected abstract int printSubActions(int maxDefaultActions);

    protected abstract void runSubAction(int action, int maxDefaultActions);

    @Override
    protected final void runAction(int action) {
        switch (action) {
        case 1:
            addObject();
            break;
        case 2:
            printAllObjects();
            break;
        case 3:
            findObjectById();
            break;
        case 4:
            removeObject();
            break;
        default:
            runSubAction(action, 4);
            break;
        }
    }

    private void removeObject() {
        int id = readIntInput("Input id: ");
        T obj = service.getById(Long.valueOf(id));
        if (obj == null) {
            System.out.println("Not found (searched for " + id + ")");
        } else {
            service.remove(obj);
            System.out.println("Removed");
            printDefaultInformation();
        }
    }

    private void findObjectById() {
        int id = readIntInput("Input id: ");
        T obj = service.getById(Long.valueOf(id));
        if (obj == null) {
            System.out.println("Not found (searched for " + id + ")");
        } else {
            printObject(obj);
        }
    }

    private void printAllObjects() {
    	for (T obj : service.getAll()) {
    		printObject(obj);
    	}
    }

    private void addObject() {
        T obj = createObject();

        T newObj = service.save(obj);

        System.out.println("Added");
        printObject(newObj);
    }
}
