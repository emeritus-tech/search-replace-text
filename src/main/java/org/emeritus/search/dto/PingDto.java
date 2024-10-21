package org.emeritus.search.dto;

import lombok.Builder;
import lombok.Data;

/**
 * PingDto.
 *
 * @return the java.lang. string
 */
@Data
@Builder
public class PingDto {

  /** The context. */
  private String context;

  /** The userId. */
  private String userId;

}
