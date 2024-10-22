package org.emeritus.search.ui.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.emeritus.search.dto.CoursePageInfo;
import org.emeritus.search.dto.SearchReplaceDto;
import org.emeritus.search.exception.AccessDeniedException;
import org.emeritus.search.service.CanvasService;
import org.emeritus.search.service.ISearchTextService;
import org.emeritus.search.ui.model.SearchPageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * The Class SearchPageController.
 */
@Controller
@Api(tags = "Search page controller")
@Tag(name = "Search page controller", description = "This controller provides Search data APIs.")
public class SearchPageController {

  /** The Constant logger. */
  private static final Logger logger = LoggerFactory.getLogger(SearchPageController.class);

  /** The canvas service. */
  @Autowired
  private CanvasService canvasService;

  @Autowired
  private ISearchTextService searchTextService;

  /** The base url. */
  @Value("${canvas.baseurl}")
  public String baseUrl;

  @Operation(summary = "Get search page", description = "Get search page")
  @GetMapping("/")
  public String viewSearchPage(Model model) throws Exception {
    String courseId = canvasService.getCourseId();
    String canvasUserId = canvasService.getCanvasUserId();
    SearchPageModel pageModel = buildSearchPageModel(courseId);
    model.addAttribute("model", pageModel);
    model.addAttribute("baseUrl", baseUrl);
    return "search.html";
  }

  @Operation(summary = "Get search page", description = "Get search page")
  @GetMapping("/ui/v1/page.json")
  public String viewSearchPageModel(Model model,
      @RequestParam(value = "courseId", required = false) Long pageRefreshCourseId)
      throws Exception {
    String courseId = null;
    if (pageRefreshCourseId != null) {
      courseId = String.valueOf(pageRefreshCourseId);
    } else {
      courseId = canvasService.getCourseId();
    }
    SearchPageModel pageModel = buildSearchPageModel(courseId);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    model.addAttribute("model",
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pageModel));
    return "index.html";
  }

  private SearchPageModel buildSearchPageModel(String courseId)
      throws NumberFormatException, IOException {
    SearchPageModel pageModel = SearchPageModel.builder().build();
    String canvasUserId = canvasService.getCanvasUserId();
    String userTimeZone = canvasService.getUserTimeZone();
    String userLocaleStr = canvasService.getUserLocale();
    String canvasUserUuid = canvasService.getCanvasUserUuid();
    Locale userLocale = Locale.ENGLISH;
    // Strings.isEmpty(userLocaleStr) ? Locale.ENGLISH : LocaleUtils.toLocale(userLocaleStr);

    pageModel.setCourseId(courseId);
    pageModel.setLoggedInUserId(canvasUserId);
    pageModel.setBaseUrl(baseUrl);
    pageModel.setBrandColors(canvasService.getBrandColors());
    SearchReplaceDto searchReplaceDto = SearchReplaceDto.builder().courseIds(Arrays.asList("1800"))
        .sourceText("Establishing security-1").textToBeReplace("Establishing security").build();
    Boolean isFound = searchTextService.searchTextAndReplaceAcrossCourses(searchReplaceDto);
    logger.info("isFound : {} ", isFound);
    List<CoursePageInfo> matchingPages = searchTextService.getMatchingPages(searchReplaceDto);
    logger.info("matchesPages : {} ", matchingPages.get(0));
    pageModel.setMatchingPages(matchingPages);
    return pageModel;
  }

  /**
   * UI exception.
   *
   * @param exception the exception
   * @param model the model
   * @return the string
   */
  @ExceptionHandler(RuntimeException.class)
  public String UIException(Exception exception, Model model) {
    logger.error("Exception", exception);
    if (exception instanceof AccessDeniedException) {
      model.addAttribute("isReload", true);
      model.addAttribute("baseUrl", baseUrl);
    }
    model.addAttribute("exception", exception);
    model.addAttribute("trace", ExceptionUtils.getStackTrace(exception));
    model.addAttribute("message", exception.getMessage());
    return "error";
  }

}
