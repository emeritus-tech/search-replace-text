package org.emeritus.search.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ItemReplaceDto {

  private String sourceText;

  private String textToBeReplace;

  private List<CourseItem> courseItems;

}
