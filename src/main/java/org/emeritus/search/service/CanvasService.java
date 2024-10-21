package org.emeritus.search.service;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.emeritus.search.exception.AccessDeniedException;
import org.emeritus.search.exception.CustomIOException;
import org.emeritus.search.exception.ResourceNotFoundException;
import org.emeritus.search.lti.common.lti.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class CanvasService.
 */
@Service
public class CanvasService {

  /** The Constant LAUNCH_PRESENTATION_LOCALE. */
  private static final String LAUNCH_PRESENTATION_LOCALE = "launch_presentation_locale";

  /** The Constant USER_TIME_ZONE. */
  private static final String USER_TIME_ZONE = "user_time_zone";

  /** The Constant CANVAS_USER_ID. */
  private static final String CANVAS_USER_ID = "canvas_user_id";

  /** The Constant CANVAS_EMAIL_ID. */
  private static final String CANVAS_EMAIL_ID = "lis_person_contact_email_primary";

  /** The Constant CANVAS_COURSE_ID. */
  private static final String CANVAS_COURSE_ID = "canvas_course_id";

  /** The Constant CANVAS_COURSE_SIS_ID. */
  private static final String CANVAS_COURSE_SIS_ID = "canvas_course_sis_id";

  /** The Constant CANVAS_USER_UUID. */
  private static final String CANVAS_USER_UUID = "canvas_user_uuid";

  /** The Constant CANVAS_BRANDCONFIG_JSON. */
  private static final String CANVAS_BRANDCONFIG_JSON = "brandconfig_json";

  /** The Constant CANVAS_BRANDCONFIG_JSON_URL. */
  private static final String CANVAS_BRANDCONFIG_JSON_URL = "brandconfig_json_url";

  /** The Constant ZERO. */
  private static final int ZERO = 0;

  /** The logger. */
  private Logger logger = LoggerFactory.getLogger(CanvasService.class);

  /** The object mapper. */
  @Autowired
  private ObjectMapper objectMapper;

  /** The base url. */
  @Value("${canvas.baseurl}")
  public String canvasBaseUrl;

  private JsonNode getCanvasClaimsAsJson() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Object principal = authentication.getPrincipal();
    if (principal instanceof OidcUser) {
      try {
        return objectMapper.readTree(((OidcUser) principal).getClaimAsString(Claims.CUSTOM));
      } catch (JsonProcessingException e) {
        logger.error("Error while getting LTI claims: ", e);
      }
    }
    throw new AccessDeniedException("Canvas user not authenticated");
  }

  /**
   * Gets the course id.
   *
   * @return the canvas id
   */
  public String getCourseId() {
    JsonNode claims = getCanvasClaimsAsJson();
    if (claims != null) {
      List<String> courseId = claims.findValuesAsText(CANVAS_COURSE_ID);
      return courseId.get(ZERO);
    }
    throw new ResourceNotFoundException(
        "Resource from LTI context not found : " + CANVAS_COURSE_ID);
  }

  /**
   * Gets the course sis id.
   *
   * @return the course sis id
   */
  public String getCourseSisId() {
    JsonNode claims = getCanvasClaimsAsJson();
    if (claims != null) {
      List<String> courseSisId = claims.findValuesAsText(CANVAS_COURSE_SIS_ID);
      return courseSisId.get(ZERO);
    }
    throw new ResourceNotFoundException(
        "Resource from LTI context not found : " + CANVAS_COURSE_SIS_ID);
  }

  /**
   * Gets the canvas user id.
   *
   * @return the canvas user id
   */
  public String getCanvasUserId() {
    JsonNode claims = getCanvasClaimsAsJson();
    if (claims != null) {
      List<String> userId = claims.findValuesAsText(CANVAS_USER_ID);
      return userId.get(ZERO);
    }
    throw new ResourceNotFoundException("Resource from LTI context not found : " + CANVAS_USER_ID);
  }

  /**
   * Gets the canvas email id.
   *
   * @return the canvas email id
   */
  public String getCanvasEmailId() {
    JsonNode claims = getCanvasClaimsAsJson();
    if (claims != null) {
      List<String> emailId = claims.findValuesAsText(CANVAS_EMAIL_ID);
      return emailId.get(ZERO);
    }
    throw new ResourceNotFoundException("Resource from LTI context not found : " + CANVAS_EMAIL_ID);
  }

  /**
   * Gets the course run code.
   *
   * @param courseSisId the course sis id
   * @return the course run code
   */
  public String getCourseRunCode(String courseSisId) {
    if (StringUtils.isEmpty(courseSisId))
      throw new CustomIOException(
          "CourseSisId for this course is null. CourseSisId:" + courseSisId);
    String[] splitSidId = courseSisId.split("/");
    return splitSidId[ZERO].trim();
  }

  /**
   * Gets the user time zone.
   *
   * @return the user time zone
   */
  public String getUserTimeZone() {
    JsonNode claims = getCanvasClaimsAsJson();
    if (claims != null) {
      List<String> userTimeZone = claims.findValuesAsText(USER_TIME_ZONE);
      return userTimeZone.get(ZERO);
    }
    throw new ResourceNotFoundException("Resource from LTI context not found : " + USER_TIME_ZONE);
  }

  /**
   * Gets the user locale.
   *
   * @return the user locale
   */
  public String getUserLocale() {
    JsonNode claims = getCanvasClaimsAsJson();
    if (claims != null) {
      List<String> userLocale = claims.findValuesAsText(LAUNCH_PRESENTATION_LOCALE);
      return userLocale.get(ZERO);
    }
    throw new ResourceNotFoundException(
        "Resource from LTI context not found : " + LAUNCH_PRESENTATION_LOCALE);
  }

  /**
   * Gets the brand colors from json.
   *
   */
  public Map<String, Object> getBrandColorsFromJSON() {
    JsonNode claims = getCanvasClaimsAsJson();
    Map<String, Object> brandColors = null;
    if (claims != null) {
      try {
        JsonNode brandConfigJsonNode = claims.get(CANVAS_BRANDCONFIG_JSON);
        if (!brandConfigJsonNode.isNull()) {
          JsonNode brandColorsJson = objectMapper.readTree(brandConfigJsonNode.textValue());
          brandColors = objectMapper.convertValue(brandColorsJson, Map.class);
        }
      } catch (Exception e) {
        return null;
      }
    }
    return brandColors;
  }

  /**
   * Gets the brand colors from json.
   *
   */
  public Map<String, Object> getBrandColorsFromJSONUrl() {
    JsonNode claims = getCanvasClaimsAsJson();
    Map<String, Object> brandColors = null;
    if (claims != null) {
      try {
        List<String> urls = claims.findValuesAsText(CANVAS_BRANDCONFIG_JSON_URL);
        if (urls.size() > 0) {
          URL url = new URL(canvasBaseUrl.concat(urls.get(ZERO)));
          String content = IOUtils.toString(url, Charset.forName("UTF-8"));
          JsonNode brandColorsJson = objectMapper.readTree(content);
          brandColors = objectMapper.convertValue(brandColorsJson, Map.class);
        }
      } catch (Exception e) {
        return null;
      }
    }
    return brandColors;
  }

  /**
   * Gets the brand colors.
   *
   * @return the brand colors
   */
  public Map<String, Object> getBrandColors() {
    Map<String, Object> brandColors = getBrandColorsFromJSON();
    if (brandColors == null) {
      brandColors = getBrandColorsFromJSONUrl();
    }
    return brandColors;
  }

  /**
   * Gets the canvas user UUID.
   *
   * @return the canvas user UUID
   */
  public String getCanvasUserUuid() {
    JsonNode claims = getCanvasClaimsAsJson();
    if (claims != null) {
      List<String> canvasUserUuid = claims.findValuesAsText(CANVAS_USER_UUID);
      return canvasUserUuid.get(ZERO);
    }
    throw new ResourceNotFoundException(
        "Resource from LTI context not found : " + CANVAS_USER_UUID);
  }
}
