package org.emeritus.search.lti.common.nrps;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class Member.
 */
public class Member {

  /** The status. */
  private String status;

  /** The name. */
  private String name;

  /** The picture. */
  // URL of Avatar
  private String picture;

  /** The given name. */
  @JsonProperty("given_name")
  private String givenName;

  /** The family name. */
  @JsonProperty("family_name")
  private String familyName;

  /** The email. */
  private String email;

  /** The lis person sourcedid. */
  @JsonProperty("lis_person_sourcedid")
  private String lisPersonSourcedid;

  /** The user id. */
  @JsonProperty("user_id")
  private String userId;

  /** The roles. */
  private List<String> roles;

  /** The message. */
  private List<Message> message;

  /**
   * Gets the status.
   *
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * Sets the status.
   *
   * @param status the new status
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the picture.
   *
   * @return the picture
   */
  public String getPicture() {
    return picture;
  }

  /**
   * Sets the picture.
   *
   * @param picture the new picture
   */
  public void setPicture(String picture) {
    this.picture = picture;
  }

  /**
   * Gets the given name.
   *
   * @return the given name
   */
  public String getGivenName() {
    return givenName;
  }

  /**
   * Sets the given name.
   *
   * @param givenName the new given name
   */
  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  /**
   * Gets the family name.
   *
   * @return the family name
   */
  public String getFamilyName() {
    return familyName;
  }

  /**
   * Sets the family name.
   *
   * @param familyName the new family name
   */
  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }

  /**
   * Gets the email.
   *
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email.
   *
   * @param email the new email
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets the lis person sourcedid.
   *
   * @return the lis person sourcedid
   */
  public String getLisPersonSourcedid() {
    return lisPersonSourcedid;
  }

  /**
   * Sets the lis person sourcedid.
   *
   * @param lisPersonSourcedid the new lis person sourcedid
   */
  public void setLisPersonSourcedid(String lisPersonSourcedid) {
    this.lisPersonSourcedid = lisPersonSourcedid;
  }

  /**
   * Gets the user id.
   *
   * @return the user id
   */
  public String getUserId() {
    return userId;
  }

  /**
   * Sets the user id.
   *
   * @param userId the new user id
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   * Gets the roles.
   *
   * @return the roles
   */
  public List<String> getRoles() {
    return roles;
  }

  /**
   * Sets the roles.
   *
   * @param roles the new roles
   */
  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  /**
   * Gets the message.
   *
   * @return the message
   */
  public List<Message> getMessage() {
    return message;
  }

  /**
   * Sets the message.
   *
   * @param message the new message
   */
  public void setMessage(List<Message> message) {
    this.message = message;
  }
}
