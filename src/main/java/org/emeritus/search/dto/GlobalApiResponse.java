package org.emeritus.search.dto;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class GlobalApiResponse<T> implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The status. */
  private String status;

  /** The message. */
  private String message;

  /** The data. */
  private T data;

  /** The errors. */
  private Object errors;

  /** The status code. */
  private Integer statusCode;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public Object getErrors() {
    return errors;
  }

  public void setErrors(Object errors) {
    this.errors = errors;
  }

  public Integer getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(Integer statusCode) {
    this.statusCode = statusCode;
  }

  /**
   * Instantiates a new global api response.
   *
   * @param data the data
   * @param status the status
   */
  public GlobalApiResponse(T data, String status) {
    this.status = status;
    this.data = data;
  }

  public GlobalApiResponse() {}

}
