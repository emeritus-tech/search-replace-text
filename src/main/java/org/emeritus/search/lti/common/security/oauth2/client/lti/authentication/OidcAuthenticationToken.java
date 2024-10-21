package org.emeritus.search.lti.common.security.oauth2.client.lti.authentication;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * We are validating the state on the client (browser) so we need to be able to return the state
 * back to the client and so it needs to exist outside of just the authentication method.
 */
public class OidcAuthenticationToken extends OAuth2AuthenticationToken {

  /** The state. */
  private final String state;

  /**
   * Instantiates a new oidc authentication token.
   *
   * @param principal the principal
   * @param authorities the authorities
   * @param authorizedClientRegistrationId the authorized client registration id
   * @param state the state
   */
  public OidcAuthenticationToken(OAuth2User principal,
      Collection<? extends GrantedAuthority> authorities, String authorizedClientRegistrationId,
      String state) {
    super(principal, authorities, authorizedClientRegistrationId);
    this.state = state;
  }

  /**
   * Gets the state.
   *
   * @return the state
   */
  public String getState() {
    return state;
  }
}
