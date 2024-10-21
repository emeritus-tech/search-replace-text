package org.emeritus.search.lti.common.nrps;

import java.util.Map;
import org.emeritus.search.lti.common.lti.Claims;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is the additional message that comes back you pass through a resource link ID.
 */
public class Message {

  /** The locale. */
  private String locale;

  /** The custom. */
  @JsonProperty(Claims.CUSTOM)
  private Map<String, String> custom;

  /** The message type. */
  @JsonProperty(Claims.MESSAGE_TYPE)
  private String messageType;

  /** The canvas user id. */
  @JsonProperty("https://www.instructure.com/canvas_user_id")
  private Integer canvasUserId;

  /** The canvas login id. */
  @JsonProperty("https://www.instructure.com/canvas_user_login_id")
  private String canvasLoginId;

  /**
   * Gets the locale.
   *
   * @return the locale
   */
  public String getLocale() {
    return locale;
  }

  /**
   * Sets the locale.
   *
   * @param locale the new locale
   */
  public void setLocale(String locale) {
    this.locale = locale;
  }

  /**
   * Gets the custom.
   *
   * @return the custom
   */
  public Map<String, String> getCustom() {
    return custom;
  }

  /**
   * Sets the custom.
   *
   * @param custom the custom
   */
  public void setCustom(Map<String, String> custom) {
    this.custom = custom;
  }

  /**
   * Gets the message type.
   *
   * @return the message type
   */
  public String getMessageType() {
    return messageType;
  }

  /**
   * Sets the message type.
   *
   * @param messageType the new message type
   */
  public void setMessageType(String messageType) {
    this.messageType = messageType;
  }

  /**
   * Gets the canvas user id.
   *
   * @return the canvas user id
   */
  public Integer getCanvasUserId() {
    return canvasUserId;
  }

  /**
   * Sets the canvas user id.
   *
   * @param canvasUserId the new canvas user id
   */
  public void setCanvasUserId(Integer canvasUserId) {
    this.canvasUserId = canvasUserId;
  }

  /**
   * Gets the canvas login id.
   *
   * @return the canvas login id
   */
  public String getCanvasLoginId() {
    return canvasLoginId;
  }

  /**
   * Sets the canvas login id.
   *
   * @param canvasLoginId the new canvas login id
   */
  public void setCanvasLoginId(String canvasLoginId) {
    this.canvasLoginId = canvasLoginId;
  }
}
