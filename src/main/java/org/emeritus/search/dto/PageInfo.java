package org.emeritus.search.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageInfo {

  private String pageTitle;

  private Integer occurences;

}
