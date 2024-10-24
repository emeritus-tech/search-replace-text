package org.emeritus.search.dto;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ItemReplaceDto {

  private List<String> courseIds;

  private Map<Long, String> itemIdsMap;

  private String sourceText;

  private String textToBeReplace;

}
