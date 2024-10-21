package org.emeritus.search.lti.base.controller.lti13;

import java.util.Map;

/**
 * The Class Canvas13PlacementBuilder.
 */
public class Canvas13PlacementBuilder {

  /** The text. */
  private String text;

  /** The enabled. */
  private boolean enabled;

  /** The icon url. */
  private String iconUrl;

  /** The placement. */
  private Canvas13Placement.Placement placement;

  /** The message type. */
  private Canvas13Placement.MessageType messageType;

  /** The target link uri. */
  private String targetLinkUri;

  /** The canvas icon class. */
  private String canvasIconClass;

  /** The custom fields. */
  private Map<String, String> customFields;

  /**
   * Text.
   *
   * @param text the text
   * @return the canvas 13 placement builder
   */
  public Canvas13PlacementBuilder text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Enabled.
   *
   * @param enabled the enabled
   * @return the canvas 13 placement builder
   */
  public Canvas13PlacementBuilder enabled(boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  /**
   * Icon url.
   *
   * @param iconUrl the icon url
   * @return the canvas 13 placement builder
   */
  public Canvas13PlacementBuilder iconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
    return this;
  }

  /**
   * Placement.
   *
   * @param placement the placement
   * @return the canvas 13 placement builder
   */
  public Canvas13PlacementBuilder placement(Canvas13Placement.Placement placement) {
    this.placement = placement;
    return this;
  }

  /**
   * Message type.
   *
   * @param messageType the message type
   * @return the canvas 13 placement builder
   */
  public Canvas13PlacementBuilder messageType(Canvas13Placement.MessageType messageType) {
    this.messageType = messageType;
    return this;
  }

  /**
   * Target link uri.
   *
   * @param targetLinkUri the target link uri
   * @return the canvas 13 placement builder
   */
  public Canvas13PlacementBuilder targetLinkUri(String targetLinkUri) {
    this.targetLinkUri = targetLinkUri;
    return this;
  }

  /**
   * Canvas icon class.
   *
   * @param canvasIconClass the canvas icon class
   * @return the canvas 13 placement builder
   */
  public Canvas13PlacementBuilder canvasIconClass(String canvasIconClass) {
    this.canvasIconClass = canvasIconClass;
    return this;
  }

  /**
   * Custom fields.
   *
   * @param customFields the custom fields
   * @return the canvas 13 placement builder
   */
  public Canvas13PlacementBuilder customFields(Map<String, String> customFields) {
    this.customFields = customFields;
    return this;
  }

  /**
   * Creates the canvas 13 placement.
   *
   * @return the canvas 13 placement
   */
  public Canvas13Placement createCanvas13Placement() {
    return new Canvas13Placement(text, enabled, iconUrl, placement, messageType, targetLinkUri,
        canvasIconClass, customFields);
  }
}
