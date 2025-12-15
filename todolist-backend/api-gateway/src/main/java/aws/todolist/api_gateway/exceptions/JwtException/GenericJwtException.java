package aws.todolist.api_gateway.exceptions.JwtException;

import lombok.Getter;

public abstract class GenericJwtException extends RuntimeException {
	
	@Getter
	private final String errorCode;
	
	protected GenericJwtException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}
	
}
