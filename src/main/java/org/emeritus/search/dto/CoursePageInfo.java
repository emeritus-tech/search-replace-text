package org.emeritus.search.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CoursePageInfo {

  private String courseId;

  private String courseName;

  private String sourceText;

  private List<PageInfo> pageInfoList;

}
