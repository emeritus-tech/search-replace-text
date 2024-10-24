package org.emeritus.search.dto;

import java.util.Map;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class CourseItem {

  private String courseId;

  private Map<Long, String> itemIdsMap;

}
