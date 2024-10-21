package org.emeritus.search.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The Class CustomIOException.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CustomIOException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new custom IO exception.
	 *
	 * @param message the message
	 */
	public CustomIOException(String message) {
		super(message);
	}

    public CustomIOException(String message, Throwable cause) {
      super(message, cause);
    }
}
