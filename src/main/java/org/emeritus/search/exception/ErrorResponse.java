package org.emeritus.search.exception;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Instantiates a new error response.
 *
 * @param message the message
 * @param details the details
 * @param timeStamp the time stamp
 * @param errorCode the error code
 */
@AllArgsConstructor

/**
 * Instantiates a new error response.
 */
@NoArgsConstructor

/**
 * Gets the error code.
 *
 * @return the error code
 */
@Getter

/**
 * Sets the error code.
 *
 * @param errorCode the new error code
 */
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)

/**
 * To string.
 *
 * @return the java.lang. string
 */
@Builder
public class ErrorResponse {

  /** The message. */
  private String message;

  /** The details. */
  private String details;

  /** The time stamp. */
  private Date timeStamp;

  /** The error code. */
  private Integer errorCode;
}
