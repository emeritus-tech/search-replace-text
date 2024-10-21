package org.emeritus.search.lti.base.controller.lti13;

import java.util.Collection;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * The Class Canvas13Settings.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Canvas13Settings {

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
   * Instantiates a new canvas 13 settings.
   *
   * @param text the text
   * @param iconUrl the icon url
   * @param selectionHeight the selection height
   * @param selectionWidth the selection width
   * @param privacyLevel the privacy level
   * @param placements the placements
   */
  public Canvas13Settings(String text, String iconUrl, String selectionHeight,
      String selectionWidth, Lti13Config.PrivacyLevel privacyLevel,
      Collection<Canvas13Placement> placements) {
    this.text = text;
    this.iconUrl = iconUrl;
    this.selectionHeight = selectionHeight;
    this.selectionWidth = selectionWidth;
    this.privacyLevel = privacyLevel;
    this.placements = placements;
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
   * Gets the icon url.
   *
   * @return the icon url
   */
  public String getIconUrl() {
    return iconUrl;
  }

  /**
   * Gets the selection height.
   *
   * @return the selection height
   */
  public String getSelectionHeight() {
    return selectionHeight;
  }

  /**
   * Gets the selection width.
   *
   * @return the selection width
   */
  public String getSelectionWidth() {
    return selectionWidth;
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
   * Gets the placements.
   *
   * @return the placements
   */
  public Collection<Canvas13Placement> getPlacements() {
    return placements;
  }
}
