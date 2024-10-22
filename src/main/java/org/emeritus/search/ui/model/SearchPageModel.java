package org.emeritus.search.ui.model;

import java.util.List;
import java.util.Map;
import org.emeritus.search.dto.CoursePageInfo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchPageModel {

  /** The course id. */
  private String courseId;

  /** The logged in user id. */
  private String loggedInUserId;

  /** The base url. */
  private String baseUrl;

  /** The matching pages. */
  private List<CoursePageInfo> matchingPages;

  /** The brand colors. */
  private Map<String, Object> brandColors;

}
