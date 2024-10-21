package org.emeritus.search.lti.base.controller.lti13;

import java.util.Collection;

/**
 * The Class Canvas13SettingsBuilder.
 */
public class Canvas13SettingsBuilder {

  /** The text. */
  private String text;

  /** The icon url. */
  private String iconUrl;

  /** The selection height. */
  private String selectionHeight;

  /** The selection width. */
  private String selectionWidth;

  /** The privacy level. */
  private Lti13Config.PrivacyLevel privacyLevel;

  /** The placements. */
  private Collection<Canvas13Placement> placements;

  /**
   * Text.
   *
   * @param text the text
   * @return the canvas 13 settings builder
   */
  public Canvas13SettingsBuilder text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Icon url.
   *
   * @param iconUrl the icon url
   * @return the canvas 13 settings builder
   */
  public Canvas13SettingsBuilder iconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
    return this;
  }

  /**
   * Selection height.
   *
   * @param selectionHeight the selection height
   * @return the canvas 13 settings builder
   */
  public Canvas13SettingsBuilder selectionHeight(String selectionHeight) {
    this.selectionHeight = selectionHeight;
    return this;
  }

  /**
   * Selection width.
   *
   * @param selectionWidth the selection width
   * @return the canvas 13 settings builder
   */
  public Canvas13SettingsBuilder selectionWidth(String selectionWidth) {
    this.selectionWidth = selectionWidth;
    return this;
  }

  /**
   * It appears that this doesn't control anything, although it is returned in responses we get
   * back.
   *
   * @param privacyLevel the privacy level
   * @return the canvas 13 settings builder
   * @see Canvas13ExtensionBuilder#privacyLevel(Lti13Config.PrivacyLevel)
   */
  public Canvas13SettingsBuilder privacyLevel(Lti13Config.PrivacyLevel privacyLevel) {
    this.privacyLevel = privacyLevel;
    return this;
  }

  /**
   * Placements.
   *
   * @param placements the placements
   * @return the canvas 13 settings builder
   */
  public Canvas13SettingsBuilder placements(Collection<Canvas13Placement> placements) {
    this.placements = placements;
    return this;
  }

  /**
   * Creates the canvas 13 settings.
   *
   * @return the canvas 13 settings
   */
  public Canvas13Settings createCanvas13Settings() {
    return new Canvas13Settings(text, iconUrl, selectionHeight, selectionWidth, privacyLevel,
        placements);
  }
}
