package org.emeritus.search.controller;

import java.io.IOException;
import java.util.List;
import org.emeritus.search.constant.URLConstants;
import org.emeritus.search.dto.CoursePageInfo;
import org.emeritus.search.dto.GlobalApiResponse;
import org.emeritus.search.dto.SearchReplaceDto;
import org.emeritus.search.service.ISearchTextService;
import org.emeritus.search.utils.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * The Class SearchTextController.
 */
@RestController
@Api(tags = "Search text- Controller")
@Tag(name = "Search text - Controller",
    description = "This controller provides Search text - related API.")
@RequestMapping(URLConstants.CANVAS_SEARCH_API_URL)
public class SearchTextController {

  /** The logger. */
  private Logger logger = LoggerFactory.getLogger(SearchTextController.class);

  /** The search text service. */
  @Autowired
  private ISearchTextService searchTextService;

  @Operation(summary = "Search text and replace acrross courses",
      description = "Search text and replace acrross courses and returns success or failure ")
  @PostMapping(URLConstants.SEARCH_TEXT_AND_REPLACE)
  public ResponseEntity<GlobalApiResponse<Boolean>> searchTextAndReplaceAcrossCourses(
      @RequestBody SearchReplaceDto searchReplaceDto) throws IOException {
    return RestUtils.successResponse(
        searchTextService.searchTextAndReplaceAcrossCourses(searchReplaceDto), HttpStatus.OK);
  }

  @Operation(summary = "Get matching pages acrross courses",
      description = "Get matching pages acrross courses and returns success or failure ")
  @PostMapping(URLConstants.GET_REPLACED_TEXT_PAGE)
  public ResponseEntity<GlobalApiResponse<List<CoursePageInfo>>> getMatchingPages(
      @RequestBody SearchReplaceDto searchReplaceDto) throws IOException {
    return RestUtils.successResponse(searchTextService.getMatchingPages(searchReplaceDto),
        HttpStatus.OK);
  }

}
