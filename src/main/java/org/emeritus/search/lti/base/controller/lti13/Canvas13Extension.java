package org.emeritus.search.lti.base.controller.lti13;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * The Class Canvas13Extension.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Canvas13Extension {

  /** The Constant INSTRUCTURE. */
  // Constant for the instructure platform
  public static final String INSTRUCTURE = "canvas.instructure.com";

  /** The domain. */
  private String domain;

  /** The tool id. */
  private String toolId;

  /** The platform. */
  private String platform;

  /** The privacy level. */
  private Lti13Config.PrivacyLevel privacyLevel;

  /** The settings. */
  private Canvas13Settings settings;

  /**
   * Instantiates a new canvas 13 extension.
   *
   * @param domain the domain
   * @param toolId the tool id
   * @param platform the platform
   * @param privacyLevel the privacy level
   * @param settings the settings
   */
  public Canvas13Extension(String domain, String toolId, String platform,
      Lti13Config.PrivacyLevel privacyLevel, Canvas13Settings settings) {
    this.domain = domain;
    this.toolId = toolId;
    this.platform = platform;
    this.privacyLevel = privacyLevel;
    this.settings = settings;
  }

  /**
   * Gets the domain.
   *
   * @return the domain
   */
  public String getDomain() {
    return domain;
  }

  /**
   * Gets the tool id.
   *
   * @return the tool id
   */
  public String getToolId() {
    return toolId;
  }

  /**
   * Gets the platform.
   *
   * @return the platform
   */
  public String getPlatform() {
    return platform;
  }

  /**
   * Gets the privacy level.
   *
   * @return the privacy level
   */
  public Lti13Config.PrivacyLevel getPrivacyLevel() {
    return privacyLevel;
  }

  /**
   * Gets the settings.
   *
   * @return the settings
   */
  public Canvas13Settings getSettings() {
    return settings;
  }

}
