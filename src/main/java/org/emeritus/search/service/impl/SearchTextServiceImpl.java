package org.emeritus.search.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.emeritus.canvas.interfaces.AssignmentReader;
import org.emeritus.canvas.interfaces.AssignmentWriter;
import org.emeritus.canvas.interfaces.CourseReader;
import org.emeritus.canvas.interfaces.DiscussionTopicReader;
import org.emeritus.canvas.interfaces.DiscussionTopicWriter;
import org.emeritus.canvas.interfaces.ModuleReader;
import org.emeritus.canvas.interfaces.PageReader;
import org.emeritus.canvas.interfaces.PageWriter;
import org.emeritus.canvas.lms.rest.api.constant.ParamConstants;
import org.emeritus.canvas.model.Course;
import org.emeritus.canvas.model.DiscussionTopic;
import org.emeritus.canvas.model.Module;
import org.emeritus.canvas.model.ModuleItem;
import org.emeritus.canvas.model.Page;
import org.emeritus.canvas.model.assignment.Assignment;
import org.emeritus.canvas.requestOptions.DiscussionTopicOptions;
import org.emeritus.canvas.requestOptions.GetSingleCourseOptions;
import org.emeritus.canvas.requestOptions.ListCourseAssignmentsOptions;
import org.emeritus.canvas.requestOptions.ListCoursePagesOptions;
import org.emeritus.canvas.requestOptions.ListModulesOptions;
import org.emeritus.search.dto.CoursePageInfo;
import org.emeritus.search.dto.ItemReplaceDto;
import org.emeritus.search.dto.PageInfo;
import org.emeritus.search.dto.SearchReplaceDto;
import org.emeritus.search.service.ISearchTextService;
import org.emeritus.search.service.helper.TokenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * The Class SearchTextServiceImpl.
 */
@Service
public class SearchTextServiceImpl implements ISearchTextService {

  /** The logger. */
  private Logger logger = LoggerFactory.getLogger(SearchTextServiceImpl.class);

  /** The base url. */
  @Value("${canvas.baseurl}")
  public String baseUrl;

  /** The token helper. */
  @Autowired
  private TokenHelper tokenHelper;

  /** The Constant PAGINATION_PAGE_SIZE. */
  public static final Integer PAGINATION_PAGE_SIZE = 100;

  /** The Constant PAGE. */
  private static final String PAGE = "Page";

  /** The Constant DISCUSSION. */
  private static final String DISCUSSION = "Discussion";

  /** The Constant ASSIGNMENT. */
  private static final String ASSIGNMENT = "Assignment";

  /**
   * Search text and replace across courses.
   *
   * @param searchReplaceDto the search replace dto
   * @return the boolean
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Override
  public Boolean searchTextAndReplaceAcrossCourses(SearchReplaceDto searchReplaceDto)
      throws IOException {
    System.out.println("searchReplaceDto" + searchReplaceDto.getSourceText());
    for (String courseId : searchReplaceDto.getCourseIds()) {
      List<Module> modules = getModules(courseId);
      extractModuleItem(modules, courseId, searchReplaceDto);
    }
    return true;
  }

  /**
   * Gets the modules.
   *
   * @param courseId the course id
   * @return the modules
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private List<Module> getModules(String courseId) throws IOException {
    ModuleReader moduleReader = tokenHelper.getApiFactory().getReader(ModuleReader.class,
        tokenHelper.getToken(), PAGINATION_PAGE_SIZE);
    ListModulesOptions listModulesOptions = new ListModulesOptions(Long.parseLong(courseId));
    List<ListModulesOptions.Include> moduleIncludes = new ArrayList<>();
    moduleIncludes.add(ListModulesOptions.Include.items);
    listModulesOptions.includes(moduleIncludes);
    return moduleReader.withCallback(this::processModules).getModulesInCourse(listModulesOptions);
  }

  /**
   * Process modules.
   *
   * @param modules the modules
   */
  private void processModules(List<Module> modules) {
    logger.info("Total modules: {}", modules.size());
  }

  /**
   * Extract module item.
   *
   * @param modules the modules
   * @param courseId the course id
   * @param searchReplaceDto the search replace dto
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private void extractModuleItem(List<Module> modules, String courseId,
      SearchReplaceDto searchReplaceDto) throws IOException {
    List<Page> pages = listPagesInCourse(courseId);
    List<DiscussionTopic> discussionTopics = getCourseAllDiscussionTopics(courseId);
    List<Assignment> assignments = listCourseAssignments(courseId);

    for (Module module : modules) {
      if (isModulePublished(module)) {
        for (ModuleItem moduleItem : module.getItems()) {
          if (isPublished(moduleItem)) {
            handleModuleItemToFindAndReplace(searchReplaceDto, moduleItem, courseId, module, pages,
                discussionTopics, assignments);
          }
        }
      }
    }
  }

  /**
   * Checks if is published.
   *
   * @param item the item
   * @return true, if is published
   */
  public boolean isPublished(ModuleItem item) {
    return (item.getPublished() != null && item.getPublished());
  }

  /**
   * Checks if is module published.
   *
   * @param module the module
   * @return true, if is module published
   */
  public boolean isModulePublished(Module module) {
    return (module.getPublished() != null && module.getPublished());
  }

  /**
   * Handle module item to find and replace.
   *
   * @param searchReplaceDto the search replace dto
   * @param moduleItem the module item
   * @param courseId the course id
   * @param module the module
   * @param pages the pages
   * @param discussionTopics the discussion topics
   * @param assignments the assignments
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private void handleModuleItemToFindAndReplace(SearchReplaceDto searchReplaceDto,
      ModuleItem moduleItem, String courseId, Module module, List<Page> pages,
      List<DiscussionTopic> discussionTopics, List<Assignment> assignments) throws IOException {
    switch (moduleItem.getType()) {
      case PAGE:
        Page page = getMatchingPage(pages, moduleItem.getPageUrl());
        if (page != null && !StringUtils.isEmpty(page.getBody())
            && (page.getBody().contains(searchReplaceDto.getSourceText()))) {

          // Replace the text
          String originalMessage = page.getBody();
          String updatedMessage =
              originalMessage.replaceAll("\\b" + searchReplaceDto.getSourceText() + "\\b",
                  searchReplaceDto.getTextToBeReplace());

          // Check if replacement occurred
          if (!originalMessage.equals(updatedMessage)) {
            // Set the updated message back to the page
            page.setBody(updatedMessage);

            // Update the page
            updateCoursePage(page, courseId);
          }
        }
        break;

      case DISCUSSION:
        DiscussionTopic discussionTopic =
            getMatchingTopic(discussionTopics, moduleItem.getContentId());

        if (discussionTopic != null && !StringUtils.isEmpty(discussionTopic.getMessage())
            && discussionTopic.getMessage().contains(searchReplaceDto.getSourceText())) {

          // Replace the text
          String originalMessage = discussionTopic.getMessage();
          String updatedMessage =
              originalMessage.replaceAll("\\b" + searchReplaceDto.getSourceText() + "\\b",
                  searchReplaceDto.getTextToBeReplace());

          // Check if replacement occurred
          if (!originalMessage.equals(updatedMessage)) {
            // Set the updated message back to the discussion topic
            discussionTopic.setMessage(updatedMessage);

            // Update the discussion topic
            updateDiscussionTopic(courseId, discussionTopic);
          }
        }
        break;

      case ASSIGNMENT:
        Assignment assignment = getMatchingAssignments(assignments, moduleItem.getContentId());
        if (assignment != null && !StringUtils.isEmpty(assignment.getDescription())
            && (assignment.getDescription().contains(searchReplaceDto.getSourceText()))) {

          // Replace the text
          String originalMessage = assignment.getDescription();
          String updatedMessage =
              originalMessage.replaceAll("\\b" + searchReplaceDto.getSourceText() + "\\b",
                  searchReplaceDto.getTextToBeReplace());

          // Check if replacement occurred
          if (!originalMessage.equals(updatedMessage)) {
            // Set the updated message back to the assignment
            assignment.setDescription(updatedMessage);

            // Update the assignment
            updateAssignments(courseId, assignment.getId(), assignment);
          }
        }
        break;

      default:
        break;
    }
  }

  /**
   * Gets the matching page.
   *
   * @param pages the pages
   * @param pageUrl the page url
   * @return the matching page
   */
  private Page getMatchingPage(List<Page> pages, String pageUrl) {
    return pages.stream().filter(page -> page.getUrl() != null && pageUrl.equals(page.getUrl()))
        .findAny().orElse(null);
  }

  /**
   * Gets the matching topic.
   *
   * @param discussionTopics the discussion topics
   * @param contentId the content id
   * @return the matching topic
   */
  private DiscussionTopic getMatchingTopic(List<DiscussionTopic> discussionTopics, Long contentId) {
    return discussionTopics.stream().filter(discussionTopic -> discussionTopic.getId() != null
        && discussionTopic.getId().equals(contentId)).findAny().orElse(null);
  }

  /**
   * Gets the matching assignments.
   *
   * @param assignments the assignments
   * @param contentId the content id
   * @return the matching assignments
   */
  private Assignment getMatchingAssignments(List<Assignment> assignments, Long contentId) {
    return assignments.stream().filter(assignment -> assignment.getId() != null
        && Long.valueOf(assignment.getId()).equals(contentId)).findAny().orElse(null);
  }

  /**
   * List pages in course.
   *
   * @param courseId the course id
   * @return the list
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private List<Page> listPagesInCourse(String courseId) throws IOException {
    PageReader pageReader = tokenHelper.getApiFactory().getReader(PageReader.class,
        tokenHelper.getToken(), PAGINATION_PAGE_SIZE);
    ListCoursePagesOptions listCoursePagesOptions = new ListCoursePagesOptions(courseId);
    listCoursePagesOptions.includes(Arrays.asList(ListCoursePagesOptions.Include.body));
    return pageReader.withCallback(this::processPages).listPagesInCourse(courseId,
        listCoursePagesOptions);
  }

  /**
   * Process pages.
   *
   * @param pages the pages
   */
  private void processPages(List<Page> pages) {
    logger.info("Total pages: : {}", pages.size());
  }

  /**
   * List course assignments.
   *
   * @param courseId the course id
   * @return the list
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public List<Assignment> listCourseAssignments(String courseId) throws IOException {
    AssignmentReader assignmentReader = tokenHelper.getApiFactory().getReader(
        AssignmentReader.class, tokenHelper.getToken(), ParamConstants.PAGINATION_PAGE_SIZE);
    ListCourseAssignmentsOptions listCourseAssignmentsOptions =
        new ListCourseAssignmentsOptions(courseId);
    return assignmentReader.withCallback(this::processAssignment)
        .listCourseAssignments(listCourseAssignmentsOptions);
  }

  /**
   * Process assignment.
   *
   * @param assignments the assignments
   */
  private void processAssignment(List<Assignment> assignments) {
    logger.info("Assignments count: {}", assignments.size());
  }

  /**
   * Gets the course all discussion topics.
   *
   * @param courseId the course id
   * @return the course all discussion topics
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public List<DiscussionTopic> getCourseAllDiscussionTopics(String courseId) throws IOException {
    DiscussionTopicReader topicReader = tokenHelper.getApiFactory().getReader(
        DiscussionTopicReader.class, tokenHelper.getToken(), ParamConstants.PAGINATION_PAGE_SIZE);
    DiscussionTopicOptions topicOptions = new DiscussionTopicOptions(courseId);
    return topicReader.withCallback(this::processDiscussions)
        .getCourseAllDiscussionTopics(topicOptions);
  }

  /**
   * Gets the single course.
   *
   * @param courseId the course id
   * @return the single course
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private Optional<Course> getSingleCourse(String courseId) throws IOException {
    CourseReader courseReader =
        tokenHelper.getApiFactory().getReader(CourseReader.class, tokenHelper.getToken());
    GetSingleCourseOptions getSingleCourseOptions = new GetSingleCourseOptions(courseId);
    return courseReader.getSingleCourse(getSingleCourseOptions);
  }

  /**
   * Process discussions.
   *
   * @param discussionTopic the discussion topic
   */
  private void processDiscussions(List<DiscussionTopic> discussionTopic) {
    logger.info("Total discussons topics: : {}", discussionTopic.size());
  }

  /**
   * Update course page.
   *
   * @param page the page
   * @param courseId the course id
   * @return the optional
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public Optional<Page> updateCoursePage(Page page, String courseId) throws IOException {
    PageWriter pageWriter =
        tokenHelper.getApiFactory().getWriter(PageWriter.class, tokenHelper.getToken());
    return pageWriter.updateCoursePage(page, courseId);
  }

  /**
   * Update assignments.
   *
   * @param courseId the course id
   * @param assignment the assignment
   * @return the optional
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public Optional<Assignment> updateAssignments(String courseId, Integer assignmentId,
      Assignment assignment) throws IOException {
    AssignmentWriter assignmentWriter =
        tokenHelper.getApiFactory().getWriter(AssignmentWriter.class, tokenHelper.getToken());
    return assignmentWriter.editAssignment(courseId, assignmentId, assignment);
  }

  /**
   * Update discussion topic.
   *
   * @param courseId the course id
   * @param topic the topic
   * @return the optional
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public Optional<DiscussionTopic> updateDiscussionTopic(String courseId, DiscussionTopic topic)
      throws IOException {
    DiscussionTopicWriter topicWriter =
        tokenHelper.getApiFactory().getWriter(DiscussionTopicWriter.class, tokenHelper.getToken());
    return topicWriter.updateDiscussionTopic(topic, courseId);
  }

  /**
   * Gets the matching pages.
   *
   * @param searchReplaceDto the search replace dto
   * @return the matching pages
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Override
  public List<CoursePageInfo> getMatchingPages(SearchReplaceDto searchReplaceDto)
      throws IOException {
    // Initialize the list to store matching pages
    List<CoursePageInfo> coursePageInfoList = new ArrayList<>();

    // Extract the course IDs and the text to be replaced
    List<String> courseIds = searchReplaceDto.getCourseIds();
    String sourceText = searchReplaceDto.getSourceText();
    String textToBeReplaced = searchReplaceDto.getTextToBeReplace();

    // Iterate through each course ID and find matching text
    for (String courseId : courseIds) {
      findText(courseId, sourceText, textToBeReplaced, coursePageInfoList);
    }

    // Return the list of matching pages
    return coursePageInfoList;
  }


  /**
   * Find text.
   *
   * @param courseId the course id
   * @param sourceText the source text
   * @param textToBeReplaced text to be replaced
   * @param coursePageInfoList the course page info list
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private void findText(String courseId, String sourceText, String textToBeReplaced,
      List<CoursePageInfo> coursePageInfoList) throws IOException {

    // Fetch pages, assignments, and discussion topics in the course
    List<Page> pages = listPagesInCourse(courseId);
    List<Assignment> assignments = listCourseAssignments(courseId);
    List<DiscussionTopic> discussionTopics = getCourseAllDiscussionTopics(courseId);
    Optional<Course> course = getSingleCourse(courseId);

    // Find pages and assignments containing the text, handling potential nulls
    List<PageInfo> pageInfoList = findPagesWithText(pages, textToBeReplaced);
    List<PageInfo> assignmentPageInfoList =
        findAssignmentsWithText(courseId, assignments, textToBeReplaced);
    List<PageInfo> discussionPageInfoList =
        findDiscussionTopicsWithText(courseId, discussionTopics, textToBeReplaced);

    // Ensure pageInfoList is not null and create a mutable list
    List<PageInfo> combinedPageInfoList =
        !isEmptyOrNull(pageInfoList) ? new ArrayList<>(pageInfoList) : new ArrayList<>();

    // Add assignmentPageInfoList to combinedPageInfoList, handling null case
    if (!isEmptyOrNull(assignmentPageInfoList)) {
      combinedPageInfoList.addAll(assignmentPageInfoList);
    }

    // Add discussionPageInfoList to combinedPageInfoList, handling null case
    if (!isEmptyOrNull(discussionPageInfoList)) {
      combinedPageInfoList.addAll(discussionPageInfoList);
    }

    // Build CoursePageInfo object only if there are matching pages or assignments
    if (!combinedPageInfoList.isEmpty()) {
      CoursePageInfo coursePageInfo =
          CoursePageInfo.builder().courseId(courseId).sourceText(sourceText)
              .courseName(course.isPresent() ? course.get().getName() : StringUtils.EMPTY)
              .pageInfoList(combinedPageInfoList).build();
      coursePageInfoList.add(coursePageInfo);
    }
  }

  /**
   * Checks if is empty or null.
   *
   * @param collection the collection
   * @return true, if is empty or null
   */
  public static boolean isEmptyOrNull(Collection<?> collection) {
    return (collection == null || collection.isEmpty());
  }


  /**
   * Find pages with text.
   *
   * @param pages the pages
   * @param textToFind the text to find
   * @return the list
   */
  // Method to return a list of PageInfo with occurrence count for each page
  public List<PageInfo> findPagesWithText(List<Page> pages, String textToBeReplaced) {

    // Validate input to avoid processing on null or empty text
    if (textToBeReplaced == null || textToBeReplaced.isEmpty()) {
      return Collections.emptyList();
    }

    // Escape special characters in textToFind for safe usage in regex
    String regex = "\\b" + Pattern.quote(textToBeReplaced) + "\\b";

    // Use Stream API to filter pages that contain the search text in the body
    return pages.stream()
        .filter(page -> page.getPublished() && containsExactText(page.getBody(), regex))
        .map(page -> {
          // Count occurrences of the search text
          Integer occurrences = countOccurrences(page.getBody(), regex);
          return PageInfo.builder().pageTitle(page.getTitle()).type(PAGE).id(page.getPageId())
              .redirectUrl(page.getHtmlUrl()).occurences(occurrences).build();
        }).toList();
  }

  /**
   * Find assignments with text.
   *
   * @param assignments the assignments
   * @param textToFind the text to find
   * @return the list
   */
  // Method to find assignments containing the specific text
  public List<PageInfo> findAssignmentsWithText(String courseId, List<Assignment> assignments,
      String textToBeReplaced) {

    // Validate input to avoid processing on null or empty text
    if (textToBeReplaced == null || textToBeReplaced.isEmpty()) {
      return Collections.emptyList();
    }


    // Escape special characters in textToFind for safe usage in regex
    String regex = "\\b" + Pattern.quote(textToBeReplaced) + "\\b";

    // Use Stream API to filter assignments that contain the search text in the body
    return assignments.stream().filter(assignment -> assignment.isPublished()
        && containsExactText(assignment.getDescription(), regex)).map(assignment -> {
          // Count occurrences of the search text
          Integer occurrences = countOccurrences(assignment.getDescription(), regex);
          String redirectUrl =
              String.format("%s/courses/%s/assignments/%s", baseUrl, courseId, assignment.getId());
          return PageInfo.builder().pageTitle(assignment.getName()).type(ASSIGNMENT)
              .id(assignment.getId().longValue()).redirectUrl(redirectUrl).occurences(occurrences)
              .build();
        }).toList();
  }

  /**
   * Find discussion topics with text.
   * 
   * @param courseId
   *
   * @param discussionTopics the discussion topics
   * @param textToBeReplaced the text to replace
   * @return the list
   */
  // Method to find discussionTopics containing the specific text
  public List<PageInfo> findDiscussionTopicsWithText(String courseId,
      List<DiscussionTopic> discussionTopics, String textToBeReplaced) {

    // Validate input to avoid processing on null or empty text
    if (textToBeReplaced == null || textToBeReplaced.isEmpty()) {
      return Collections.emptyList();
    }

    // Escape special characters in textToFind for safe usage in regex
    String regex = "\\b" + Pattern.quote(textToBeReplaced) + "\\b";

    // Use Stream API to filter discussionTopics that contain the exact search text in the body
    return discussionTopics.stream().filter(discussionTopic -> discussionTopic.isPublished()
        && containsExactText(discussionTopic.getMessage(), regex)).map(discussionTopic -> {
          // Count occurrences of the exact search text
          Integer occurrences = countOccurrences(discussionTopic.getMessage(), regex);
          String redirectUrl = String.format("%s/courses/%s/discussion_topics/%s", baseUrl,
              courseId, discussionTopic.getId());
          // Map to PageInfo with the title and occurrences
          return PageInfo.builder().pageTitle(discussionTopic.getTitle()).type(DISCUSSION)
              .id(discussionTopic.getId()).redirectUrl(redirectUrl).occurences(occurrences).build();
        }).toList();
  }

  // Helper method to check for exact word match using regex
  private boolean containsExactText(String message, String regex) {
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(message);
    return matcher.find(); // Returns true if the exact text is found
  }

  // Count occurrences of exact match in the text
  private Integer countOccurrences(String message, String regex) {
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(message);
    int count = 0;
    while (matcher.find()) {
      count++;
    }
    return count;
  }

  @Override
  public List<CoursePageInfo> findTextAcrossCourses(SearchReplaceDto searchReplaceDto)
      throws IOException {
    // Initialize the list to store matching pages
    List<CoursePageInfo> coursePageInfoList = new ArrayList<>();

    // Extract the course IDs and the text to be replaced
    List<String> courseIds = searchReplaceDto.getCourseIds();
    String textToFind = searchReplaceDto.getSourceText();

    // Iterate through each course ID and find matching text
    for (String courseId : courseIds) {
      findText(courseId, textToFind, textToFind, coursePageInfoList);
    }

    // Return the list of matching pages
    return coursePageInfoList;
  }

  @Override
  public Boolean replaceSelectedItemsForCourse(ItemReplaceDto itemReplaceDto) throws IOException {

    // Group by type and get the IDs for each type
    Map<String, List<Long>> idsByType = itemReplaceDto.getItemIdsMap().entrySet().stream()
        .collect(Collectors.groupingBy(Map.Entry::getValue, // Group by type
            Collectors.mapping(Map.Entry::getKey, Collectors.toList()) // Collect IDs for each type
        ));


    // Iterate through each course ID and find matching text
    for (String courseId : itemReplaceDto.getCourseIds()) {
      List<Page> pages = listPagesInCourse(courseId);
      List<DiscussionTopic> discussionTopics = getCourseAllDiscussionTopics(courseId);
      List<Assignment> assignments = listCourseAssignments(courseId);

      // Iterate over the map and add type checks
      for (Map.Entry<String, List<Long>> entry : idsByType.entrySet()) {
        String type = entry.getKey();
        List<Long> ids = entry.getValue();

        // Check for specific types and perform actions
        if (type.equals(PAGE)) {
          logger.info("Processing page with IDs: {}", ids);
          filterPagesMatchesWithIdsAndUpdate(pages, ids, courseId, itemReplaceDto);
        } else if (type.equals(ASSIGNMENT)) {
          logger.info("Processing assignment with IDs: {}", ids);
          filterAssignmentsMatchesWithIdsAndUpdate(assignments, ids, courseId, itemReplaceDto);
        } else if (type.equals(DISCUSSION)) {
          filterDiscussionsMatchesWithIdsAndUpdate(discussionTopics, ids, courseId, itemReplaceDto);
          logger.info("Processing discussion with IDs: {}", ids);
        }
      }
    }
    return true;
  }


  // Method to filter matching pages and call the updateCoursePage method
  public List<Page> filterPagesMatchesWithIdsAndUpdate(List<Page> pages, List<Long> ids,
      String courseId, ItemReplaceDto itemReplaceDto) {
    // Filter the pages based on matching IDs
    List<Page> matchingPages =
        pages.stream().filter(page -> ids.contains(page.getPageId())).toList();

    Optional<Page> updatedPage = java.util.Optional.empty();

    // Loop through the matching pages and update each one
    for (Page page : matchingPages) {
      try {
        if (page != null && !StringUtils.isEmpty(page.getBody())
            && (page.getBody().contains(itemReplaceDto.getSourceText()))) {

          // Replace the text
          String originalMessage = page.getBody();
          String updatedMessage = originalMessage.replaceAll(
              "\\b" + itemReplaceDto.getSourceText() + "\\b", itemReplaceDto.getTextToBeReplace());

          // Check if replacement occurred
          if (!originalMessage.equals(updatedMessage)) {
            // Set the updated message back to the page
            page.setBody(updatedMessage);

            // Update the page
            updatedPage = updateCoursePage(page, courseId);
          }
        }
        if (updatedPage.isPresent()) {
          logger.info("Successfully updated page: {}", updatedPage.get().getPageId());
        } else {
          logger.info("Failed to update page: {}", page.getPageId());
        }
      } catch (IOException e) {
        logger.error("Error updating page: {} - {}", page.getPageId(), e.getMessage());
      }
    }

    // Return the list of filtered pages
    return matchingPages;
  }


  // Method to filter matching assignments and call the updateAssignment method
  public List<Assignment> filterAssignmentsMatchesWithIdsAndUpdate(List<Assignment> assignments,
      List<Long> ids, String courseId, ItemReplaceDto itemReplaceDto) {

    Optional<Assignment> updatedAssignment = java.util.Optional.empty();

    // Filter the assignments based on matching IDs
    List<Assignment> matchingAssignments = assignments.stream()
        .filter(assignment -> ids.contains(assignment.getId().longValue())).toList();

    // Loop through the matching assignments and update each one
    for (Assignment assignment : matchingAssignments) {
      try {
        if (assignment != null && !StringUtils.isEmpty(assignment.getDescription())
            && (assignment.getDescription().contains(itemReplaceDto.getSourceText()))) {

          // Replace the text
          String originalMessage = assignment.getDescription();
          String updatedMessage = originalMessage.replaceAll(
              "\\b" + itemReplaceDto.getSourceText() + "\\b", itemReplaceDto.getTextToBeReplace());

          // Check if replacement occurred
          if (!originalMessage.equals(updatedMessage)) {
            // Set the updated message back to the assignment
            assignment.setDescription(updatedMessage);

            // Update the assignment
            updatedAssignment = updateAssignments(courseId, assignment.getId(), assignment);
          }
        }
        if (updatedAssignment.isPresent()) {
          logger.info("Successfully updated assignment: {}", updatedAssignment.get().getId());
        } else {
          logger.info("Failed to update assignment: {}", updatedAssignment.get().getId());
        }
      } catch (IOException e) {
        logger.error("Error updating assignment: {} - {}", updatedAssignment.get().getId(),
            e.getMessage());
      }
    }

    // Return the list of filtered assignments
    return matchingAssignments;
  }

  // Method to filter matching pages and call the updateCoursePage method
  public List<DiscussionTopic> filterDiscussionsMatchesWithIdsAndUpdate(
      List<DiscussionTopic> discussionTopics, List<Long> ids, String courseId,
      ItemReplaceDto itemReplaceDto) {

    Optional<DiscussionTopic> updatedDiscussions = java.util.Optional.empty();

    // Filter the DiscussionTopics based on matching IDs
    List<DiscussionTopic> matchingDiscussionsTopic = discussionTopics.stream()
        .filter(discussionTopic -> ids.contains(discussionTopic.getId())).toList();

    // Loop through the matching discussions topic and update each one
    for (DiscussionTopic discussionTopic : matchingDiscussionsTopic) {
      try {
        if (discussionTopic != null && !StringUtils.isEmpty(discussionTopic.getMessage())
            && (discussionTopic.getMessage().contains(itemReplaceDto.getSourceText()))) {

          // Replace the text
          String originalMessage = discussionTopic.getMessage();
          String updatedMessage = originalMessage.replaceAll(
              "\\b" + itemReplaceDto.getSourceText() + "\\b", itemReplaceDto.getTextToBeReplace());

          // Check if replacement occurred
          if (!originalMessage.equals(updatedMessage)) {
            // Set the updated message back to the page
            discussionTopic.setMessage(updatedMessage);

            // Update the discussions
            updatedDiscussions = updateDiscussionTopic(courseId, discussionTopic);
          }
        }
        if (updatedDiscussions.isPresent()) {
          logger.info("Successfully updated discussions: {}", updatedDiscussions.get().getId());
        } else {
          logger.info("Failed to update discussions: {}", updatedDiscussions.get().getId());
        }
      } catch (IOException e) {
        logger.error("Error updating discussions: {} - {}", updatedDiscussions.get().getId(),
            e.getMessage());
      }
    }

    // Return the list of filtered matching discussions
    return matchingDiscussionsTopic;
  }

}
