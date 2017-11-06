package ru.epam.spring.cinema.repository.filter;

/**
 * Filter to perform searching for users with specific parameters.
 *
 * @author Alex_Yamskov
 */
public class TicketFilter {

	private Long userId;
	private Long assignmentId;

	public Long getUserId() {
	    return userId;
    }

	public void setUserId(Long userId) {
	    this.userId = userId;
    }

	public Long getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(Long assignmentId) {
		this.assignmentId = assignmentId;
	}

}
