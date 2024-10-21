package org.emeritus.search.lti.base.controller.lti13;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class Canvas13Placement.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Canvas13Placement {

  /**
   * The Enum MessageType.
   */
  public enum MessageType {
    /** The Lti resource link request. */
    LtiResourceLinkRequest,
    /** The Lti deep linking request. */
    LtiDeepLinkingRequest
  };

  /**
   * The Enum Placement.
   */
  public enum Placement {

    /** The link selection. */
    @JsonProperty("link_selection")
    LINK_SELECTION,

    /** The assignment selection. */
    @JsonProperty("assignment_selection")
    ASSIGNMENT_SELECTION,

    /** The course navigation. */
    @JsonProperty("course_navigation")
    COURSE_NAVIGATION,

    /** The account navigation. */
    @JsonProperty("account_navigation")
    ACCOUNT_NAVIGATION,

    /** The user navigation. */
    @JsonProperty("user_navigation")
    USER_NAVIGATION,

    /** The editor button. */
    @JsonProperty("editor_button")
    EDITOR_BUTTON,

    /** The migration selection. */
    @JsonProperty("migration_selection")
    MIGRATION_SELECTION,

    // These aren't in the API documentation, but are in the list in the UI
    // similarity_detection
    // assignment_edit
    // assignment_menu
    // assignment_view
    // collaboration
    // course_assignments_menu
    // course_home_sub_navigation
    // course_settings_sub_navigation
    // discussion_topic_menu
    // file_menu
    // global_navigation
    // homework_submission
    // module_menu
    // module_index_menu
    // post_grades
    // quiz_menu
    // resource_selection
    // student_context_card
    // tool_configuration
    // wiki_index_menu
    // wiki_page_menu

  }

  /** The text. */
  private String text;

  /** The enabled. */
  private boolean enabled;

  /** The icon url. */
  private String iconUrl;

  /** The placement. */
  private Placement placement;

  /** The message type. */
  private MessageType messageType;

  /** The target link uri. */
  private String targetLinkUri;

  /** The canvas icon class. */
  private String canvasIconClass;

  /** The custom fields. */
  private Map<String, String> customFields;

  /**
   * Instantiates a new canvas 13 placement.
   *
   * @param text the text
   * @param enabled the enabled
   * @param iconUrl the icon url
   * @param placement the placement
   * @param messageType the message type
   * @param targetLinkUri the target link uri
   * @param canvasIconClass the canvas icon class
   * @param customFields the custom fields
   */
  public Canvas13Placement(String text, boolean enabled, String iconUrl, Placement placement,
      MessageType messageType, String targetLinkUri, String canvasIconClass,
      Map<String, String> customFields) {
    this.text = text;
    this.enabled = enabled;
    this.iconUrl = iconUrl;
    this.placement = placement;
    this.messageType = messageType;
    this.targetLinkUri = targetLinkUri;
    this.canvasIconClass = canvasIconClass;
    this.customFields = customFields;
  }

  /**
   * Gets the text.
   *
   * @return the text
   */
  public String getText() {
    return text;
  }

  /**
   * Checks if is enabled.
   *
   * @return true, if is enabled
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Gets the icon url.
   *
   * @return the icon url
   */
  public String getIconUrl() {
    return iconUrl;
  }

  /**
   * Gets the placement.
   *
   * @return the placement
   */
  public Placement getPlacement() {
    return placement;
  }

  /**
   * Gets the message type.
   *
   * @return the message type
   */
  public MessageType getMessageType() {
    return messageType;
  }

  /**
   * Gets the target link uri.
   *
   * @return the target link uri
   */
  public String getTargetLinkUri() {
    return targetLinkUri;
  }

  /**
   * Gets the canvas icon class.
   *
   * @return the canvas icon class
   */
  public String getCanvasIconClass() {
    return canvasIconClass;
  }

  /**
   * Gets the custom fields.
   *
   * @return the custom fields
   */
  public Map<String, String> getCustomFields() {
    return customFields;
  }
}
