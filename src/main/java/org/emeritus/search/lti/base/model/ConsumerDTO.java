package org.emeritus.search.lti.base.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

/**
 * To string.
 *
 * @return the java.lang. string
 */
@Data

/**
 * To string.
 *
 * @return the java.lang. string
 */
@Builder
public class ConsumerDTO {

  /** The user full name. */
  String userFullName;

  /** The user email. */
  String userEmail;

  /** The context. */
  JsonNode context;

  /** The custom. */
  JsonNode custom;

}
