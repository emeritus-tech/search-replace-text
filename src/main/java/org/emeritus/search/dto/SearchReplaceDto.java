package org.emeritus.search.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class SearchReplaceDto {

  private List<String> courseIds;

  private String sourceText;

  private String textToBeReplace;

}
