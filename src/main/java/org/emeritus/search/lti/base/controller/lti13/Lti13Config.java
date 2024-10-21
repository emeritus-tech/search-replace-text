package org.emeritus.search.lti.base.controller.lti13;

import java.util.Collection;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class Lti13Config.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Lti13Config {

  /**
   * The Enum PrivacyLevel.
   */
  public enum PrivacyLevel {

    /** The anonymous. */
    @JsonProperty("anonymous")
    ANONYMOUS,
    /** The public. */
    @JsonProperty("public")
    PUBLIC
  };

  /** The title. */
  @NotEmpty
  private String title;

  /** The description. */
  @NotEmpty
  private String description;

  /** The privacy level. */
  @NotNull
  private PrivacyLevel privacyLevel;

  /** The oidc initiation url. */
  @NotEmpty
  private String oidcInitiationUrl;

  /** The target link uri. */
  @NotEmpty
  private String targetLinkUri;

  /** The scopes. */
  private Collection<String> scopes;

  /** The extensions. */
  private Collection<Canvas13Extension> extensions;

  /** The public jwk. */
  private Object publicJwk;

  /** The public jwk url. */
  private String publicJwkUrl;

  /** The custom fields. */
  private Map<String, String> customFields;

  /**
   * Instantiates a new lti 13 config.
   *
   * @param title the title
   * @param description the description
   * @param privacyLevel the privacy level
   * @param oidcInitiationUrl the oidc initiation url
   * @param targetLinkUri the target link uri
   * @param scopes the scopes
   * @param extensions the extensions
   * @param publicJwk the public jwk
   * @param publicJwkUrl the public jwk url
   * @param customFields the custom fields
   */
  public Lti13Config(@NotEmpty String title, @NotEmpty String description,
      @NotNull PrivacyLevel privacyLevel, @NotEmpty String oidcInitiationUrl,
      @NotEmpty String targetLinkUri, Collection<String> scopes,
      Collection<Canvas13Extension> extensions, Object publicJwk, String publicJwkUrl,
      Map<String, String> customFields) {
    this.title = title;
    this.description = description;
    this.privacyLevel = privacyLevel;
    this.oidcInitiationUrl = oidcInitiationUrl;
    this.targetLinkUri = targetLinkUri;
    this.scopes = scopes;
    this.extensions = extensions;
    this.publicJwk = publicJwk;
    this.publicJwkUrl = publicJwkUrl;
    this.customFields = customFields;
  }

  /**
   * Gets the title.
   *
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Gets the description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the privacy level.
   *
   * @return the privacy level
   */
  public PrivacyLevel getPrivacyLevel() {
    return privacyLevel;
  }

  /**
   * Gets the oidc initiation url.
   *
   * @return the oidc initiation url
   */
  public String getOidcInitiationUrl() {
    return oidcInitiationUrl;
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
   * Gets the scopes.
   *
   * @return the scopes
   */
  public Collection<String> getScopes() {
    return scopes;
  }

  /**
   * Gets the extensions.
   *
   * @return the extensions
   */
  public Collection<Canvas13Extension> getExtensions() {
    return extensions;
  }

  /**
   * Gets the public jwk.
   *
   * @return the public jwk
   */
  public Object getPublicJwk() {
    return publicJwk;
  }

  /**
   * Gets the public jwk url.
   *
   * @return the public jwk url
   */
  public String getPublicJwkUrl() {
    return publicJwkUrl;
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
