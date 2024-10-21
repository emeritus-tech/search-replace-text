package org.emeritus.search.lti.base.controller.lti13;

/**
 * The Class Canvas13ExtensionBuilder.
 */
public class Canvas13ExtensionBuilder {

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
   * Domain.
   *
   * @param domain the domain
   * @return the canvas 13 extension builder
   */
  public Canvas13ExtensionBuilder domain(String domain) {
    this.domain = domain;
    return this;
  }

  /**
   * Tool id.
   *
   * @param toolId the tool id
   * @return the canvas 13 extension builder
   */
  public Canvas13ExtensionBuilder toolId(String toolId) {
    this.toolId = toolId;
    return this;
  }

  /**
   * Platform.
   *
   * @param platform the platform
   * @return the canvas 13 extension builder
   */
  public Canvas13ExtensionBuilder platform(String platform) {
    this.platform = platform;
    return this;
  }

  /**
   * Privacy level.
   *
   * @param privacyLevel the privacy level
   * @return the canvas 13 extension builder
   */
  public Canvas13ExtensionBuilder privacyLevel(Lti13Config.PrivacyLevel privacyLevel) {
    this.privacyLevel = privacyLevel;
    return this;
  }

  /**
   * Settings.
   *
   * @param settings the settings
   * @return the canvas 13 extension builder
   */
  public Canvas13ExtensionBuilder settings(Canvas13Settings settings) {
    this.settings = settings;
    return this;
  }

  /**
   * Creates the canvas 13 extension.
   *
   * @return the canvas 13 extension
   */
  public Canvas13Extension createCanvas13Extension() {
    return new Canvas13Extension(domain, toolId, platform, privacyLevel, settings);
  }
}
