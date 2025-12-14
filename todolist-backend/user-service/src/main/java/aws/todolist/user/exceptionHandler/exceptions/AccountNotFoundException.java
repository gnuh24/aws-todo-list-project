package aws.todolist.user.exceptionHandler.exceptions;

import jakarta.persistence.EntityNotFoundException;

public class AccountNotFoundException extends EntityNotFoundException {
	public AccountNotFoundException(String message) {
		super(message);
	}
}
