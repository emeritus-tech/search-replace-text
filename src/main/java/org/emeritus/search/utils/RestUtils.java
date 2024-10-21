package org.emeritus.search.utils;

import org.emeritus.search.dto.GlobalApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


/**
 * The Class RestUtils.
 */
public final class RestUtils {

  /**
   * Gets the success response.
   *
   * @param <T> the generic type
   * @param data the data
   * @param satusCode the satus code
   * @param httpStatus the http status
   * @return the success response
   */
  public static <T> ResponseEntity<GlobalApiResponse<T>> getSuccessResponse(T data,
      String satusCode, HttpStatus httpStatus) {
    return new ResponseEntity<GlobalApiResponse<T>>(new GlobalApiResponse<>(data, satusCode),
        httpStatus);
  }

  /**
   * Success response.
   *
   * @param <T> the generic type
   * @param data the data
   * @param statusCode the status code
   * @return the response entity
   */
  public static <T> ResponseEntity<GlobalApiResponse<T>> successResponse(T data,
      HttpStatus statusCode) {
    return getSuccessResponse(data, "SUCCESS", statusCode);
  }



}
