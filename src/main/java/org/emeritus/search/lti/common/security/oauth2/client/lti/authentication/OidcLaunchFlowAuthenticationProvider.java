/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.emeritus.search.lti.common.security.oauth2.client.lti.authentication;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.emeritus.search.lti.common.security.oauth2.core.endpoint.OIDCLaunchFlowResponse;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoderJwkSupport;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;

/**
 * An implementation of an {@link AuthenticationProvider} for the IMS SEC 1.0 OpenID Connect Launch
 * Flow
 * <p>
 * This {@link AuthenticationProvider} is responsible for authenticating an ID Token in an OpenID
 * Implicit flow.
 * <p>
 * It will create a {@code Principal} in the form of an {@link OidcUser}. The {@code OidcUser} is
 * then associated to the {@link OAuth2LoginAuthenticationToken} to complete the authentication.
 *
 * @author Joe Grandja
 * @see OAuth2LoginAuthenticationToken
 * @see OAuth2AccessTokenResponseClient
 * @see OidcUserService
 * @see OidcUser
 * @see <a target="_blank" href=
 *      "https://openid.net/specs/openid-connect-core-1_0.html#ImplicitFlowAuth">Section 3.2
 *      Authentication using the Implicit Flow</a>
 * @see <a target="_blank" href=
 *      "https://openid.net/specs/openid-connect-core-1_0.html#ImplicitAuthRequest">3.2.2.1.
 *      Authentication Request</a>
 * @see <a target="_blank" href=
 *      "https://openid.net/specs/openid-connect-core-1_0.html#ImplicitAuthResponse">3.2.2.5.
 *      Successful Authentication Response</a>
 * @since 5.0
 */
public class OidcLaunchFlowAuthenticationProvider implements AuthenticationProvider {

  /** The Constant INVALID_STATE_PARAMETER_ERROR_CODE. */
  private static final String INVALID_STATE_PARAMETER_ERROR_CODE = "invalid_state_parameter";

  /** The Constant MISSING_SIGNATURE_VERIFIER_ERROR_CODE. */
  private static final String MISSING_SIGNATURE_VERIFIER_ERROR_CODE = "missing_signature_verifier";

  /** The jwt decoders. */
  private final Map<String, JwtDecoder> jwtDecoders = new ConcurrentHashMap<>();

  /** The authorities mapper. */
  private GrantedAuthoritiesMapper authoritiesMapper = (authorities -> authorities);

  /** The rest operations. */
  private RestOperations restOperations;

  /**
   * Authenticate.
   *
   * @param authentication the authentication
   * @return the authentication
   * @throws AuthenticationException the authentication exception
   */
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    OidcLaunchFlowToken authorizationCodeAuthentication = (OidcLaunchFlowToken) authentication;

    // Section 3.1.2.1 Authentication Request -
    // https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest
    // scope
    // REQUIRED. OpenID Connect requests MUST contain the "openid" scope value.
    if (!authorizationCodeAuthentication.getAuthorizationExchange().getAuthorizationRequest()
        .getScopes().contains(OidcScopes.OPENID)) {
      // This is NOT an OpenID Connect Authentication Request so return null
      // and let OAuth2LoginAuthenticationProvider handle it instead
      return null;
    }

    OAuth2AuthorizationRequest authorizationRequest =
        authorizationCodeAuthentication.getAuthorizationExchange().getAuthorizationRequest();
    OIDCLaunchFlowResponse authorizationResponse =
        authorizationCodeAuthentication.getAuthorizationExchange().getAuthorizationResponse();

    if (authorizationResponse.statusError()) {
      throw new OAuth2AuthenticationException(authorizationResponse.getError(),
          authorizationResponse.getError().toString());
    }

    if (!authorizationResponse.getState().equals(authorizationRequest.getState())) {
      OAuth2Error oauth2Error = new OAuth2Error(INVALID_STATE_PARAMETER_ERROR_CODE);
      throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
    }

    ClientRegistration clientRegistration = authorizationCodeAuthentication.getClientRegistration();

    OidcIdToken idToken = createOidcToken(clientRegistration, authorizationResponse.getIdToken());

    // We don't have a userinfo endpoint so just construct our user from the claims in the ID Token
    Set<GrantedAuthority> authorities = new HashSet<>();
    OidcUserAuthority authority = new OidcUserAuthority(idToken, null);
    authorities.add(authority);
    DefaultOidcUser oidcUser = new DefaultOidcUser(authorities, idToken);

    Collection<? extends GrantedAuthority> mappedAuthorities =
        this.authoritiesMapper.mapAuthorities(oidcUser.getAuthorities());

    OidcLaunchFlowToken authenticationResult = new OidcLaunchFlowToken(
        authorizationCodeAuthentication.getClientRegistration(),
        authorizationCodeAuthentication.getAuthorizationExchange(), oidcUser, mappedAuthorities);
    authenticationResult.setDetails(authorizationCodeAuthentication.getDetails());

    return authenticationResult;
  }

  /**
   * Sets the authorities mapper.
   *
   * @param authoritiesMapper the new authorities mapper
   */
  public final void setAuthoritiesMapper(GrantedAuthoritiesMapper authoritiesMapper) {
    Assert.notNull(authoritiesMapper, "authoritiesMapper cannot be null");
    this.authoritiesMapper = authoritiesMapper;
  }

  /**
   * Sets the rest operations.
   *
   * @param restOperations the new rest operations
   */
  public final void setRestOperations(RestOperations restOperations) {
    this.restOperations = restOperations;
  }

  /**
   * Supports.
   *
   * @param authentication the authentication
   * @return true, if successful
   */
  @Override
  public boolean supports(Class<?> authentication) {
    return OidcLaunchFlowToken.class.isAssignableFrom(authentication);
  }

  /**
   * Creates the oidc token.
   *
   * @param clientRegistration the client registration
   * @param idToken the id token
   * @return the oidc id token
   */
  private OidcIdToken createOidcToken(ClientRegistration clientRegistration, String idToken) {
    JwtDecoder jwtDecoder = getJwtDecoder(clientRegistration);
    Jwt jwt = jwtDecoder.decode(idToken);
    OidcIdToken oidcIdToken = new OidcIdToken(jwt.getTokenValue(), jwt.getIssuedAt(),
        jwt.getExpiresAt(), jwt.getClaims());
    OidcTokenValidator.validateIdToken(oidcIdToken, clientRegistration);
    return oidcIdToken;
  }

  /**
   * Gets the jwt decoder.
   *
   * @param clientRegistration the client registration
   * @return the jwt decoder
   */
  private JwtDecoder getJwtDecoder(ClientRegistration clientRegistration) {
    JwtDecoder jwtDecoder = this.jwtDecoders.get(clientRegistration.getRegistrationId());
    if (jwtDecoder == null) {
      if (!StringUtils.hasText(clientRegistration.getProviderDetails().getJwkSetUri())) {
        OAuth2Error oauth2Error = new OAuth2Error(MISSING_SIGNATURE_VERIFIER_ERROR_CODE,
            "Failed to find a Signature Verifier for Client Registration: '"
                + clientRegistration.getRegistrationId()
                + "'. Check to ensure you have configured the JwkSet URI.",
            null);
        throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
      }
      // TODO This should look at the Cache-Control header so to expire old jwtDecoders.
      // Canvas looks to rotate it's keys monthly.
      NimbusJwtDecoderJwkSupport nimbusJwtDecoderJwkSupport =
          new NimbusJwtDecoderJwkSupport(clientRegistration.getProviderDetails().getJwkSetUri());
      if (restOperations != null) {
        nimbusJwtDecoderJwkSupport.setRestOperations(restOperations);
      }
      jwtDecoder = nimbusJwtDecoderJwkSupport;
      this.jwtDecoders.put(clientRegistration.getRegistrationId(), jwtDecoder);
    }
    return jwtDecoder;
  }
}
