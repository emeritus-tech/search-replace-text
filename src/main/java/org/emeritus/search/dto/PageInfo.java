package org.emeritus.search.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageInfo {

  private Long id;

  private String pageTitle;

  private String type;

  private Integer occurences;

  private String redirectUrl;

}
