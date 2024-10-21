package org.emeritus.search.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

  private static final long serialVersionUID = -5915114530878341497L;

  /**
   * Instantiates a new record not found exception.
   *
   * @param message the message
   */
  public ResourceNotFoundException(String message) {
    super(message);
  }
  
  public ResourceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
