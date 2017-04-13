package ru.epam.spring.cinema.repository.exception;

public class RepositoryException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RepositoryException(String msg) {
    	super(msg);
    }

}
