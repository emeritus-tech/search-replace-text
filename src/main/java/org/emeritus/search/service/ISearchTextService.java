package org.emeritus.search.service;

import java.io.IOException;
import java.util.List;
import org.emeritus.search.dto.CoursePageInfo;
import org.emeritus.search.dto.SearchReplaceDto;

public interface ISearchTextService {

  Boolean searchTextAndReplaceAcrossCourses(SearchReplaceDto searchReplaceDto) throws IOException;

  List<CoursePageInfo> getMatchingPages(SearchReplaceDto searchReplaceDto) throws IOException;

}
