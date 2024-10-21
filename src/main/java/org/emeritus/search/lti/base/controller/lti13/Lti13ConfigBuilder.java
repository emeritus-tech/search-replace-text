package org.emeritus.search.lti.base.controller.lti13;

import java.util.Collection;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * The Class Lti13ConfigBuilder.
 */
public class Lti13ConfigBuilder {

  /** The title. */
  private @NotEmpty String title;

  /** The description. */
  private @NotEmpty String description;

  /** The privacy level. */
  private Lti13Config.@NotNull PrivacyLevel privacyLevel;

  /** The oidc initiaion url. */
  private @NotEmpty String oidcInitiaionUrl;

  /** The target link uri. */
  private @NotEmpty String targetLinkUri;

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
   * Title.
   *
   * @param title the title
   * @return the lti 13 config builder
   */
  public Lti13ConfigBuilder title(@NotEmpty String title) {
    this.title = title;
    return this;
  }

  /**
   * Description.
   *
   * @param description the description
   * @return the lti 13 config builder
   */
  public Lti13ConfigBuilder description(@NotEmpty String description) {
    this.description = description;
    return this;
  }

  /**
   * It appears that this doesn't control anything, although it is returned in responses we get
   * back.
   *
   * @param privacyLevel the privacy level
   * @return the lti 13 config builder
   * @see Canvas13ExtensionBuilder#privacyLevel(Lti13Config.PrivacyLevel)
   */
  public Lti13ConfigBuilder privacyLevel(Lti13Config.@NotNull PrivacyLevel privacyLevel) {
    this.privacyLevel = privacyLevel;
    return this;
  }

  /**
   * Oidc initiaion url.
   *
   * @param oidcInitiaionUrl the oidc initiaion url
   * @return the lti 13 config builder
   */
  public Lti13ConfigBuilder oidcInitiaionUrl(@NotEmpty String oidcInitiaionUrl) {
    this.oidcInitiaionUrl = oidcInitiaionUrl;
    return this;
  }

  /**
   * Target link uri.
   *
   * @param targetLinkUri the target link uri
   * @return the lti 13 config builder
   */
  public Lti13ConfigBuilder targetLinkUri(@NotEmpty String targetLinkUri) {
    this.targetLinkUri = targetLinkUri;
    return this;
  }

  /**
   * Scopes.
   *
   * @param scopes the scopes
   * @return the lti 13 config builder
   */
  public Lti13ConfigBuilder scopes(Collection<String> scopes) {
    this.scopes = scopes;
    return this;
  }

  /**
   * Extensions.
   *
   * @param extensions the extensions
   * @return the lti 13 config builder
   */
  public Lti13ConfigBuilder extensions(Collection<Canvas13Extension> extensions) {
    this.extensions = extensions;
    return this;
  }

  /**
   * Public jwk.
   *
   * @param publicJwk the public jwk
   * @return the lti 13 config builder
   */
  public Lti13ConfigBuilder publicJwk(Object publicJwk) {
    this.publicJwk = publicJwk;
    return this;
  }

  /**
   * Public jwk url.
   *
   * @param publicJwkUrl the public jwk url
   * @return the lti 13 config builder
   */
  public Lti13ConfigBuilder publicJwkUrl(String publicJwkUrl) {
    this.publicJwkUrl = publicJwkUrl;
    return this;
  }

  /**
   * Custom fields.
   *
   * @param customFields the custom fields
   * @return the lti 13 config builder
   */
  public Lti13ConfigBuilder customFields(Map<String, String> customFields) {
    this.customFields = customFields;
    return this;
  }

  /**
   * Creates the lti 13 config.
   *
   * @return the lti 13 config
   */
  public Lti13Config createLti13Config() {
    return new Lti13Config(title, description, privacyLevel, oidcInitiaionUrl, targetLinkUri,
        scopes, extensions, publicJwk, publicJwkUrl, customFields);
  }
}
