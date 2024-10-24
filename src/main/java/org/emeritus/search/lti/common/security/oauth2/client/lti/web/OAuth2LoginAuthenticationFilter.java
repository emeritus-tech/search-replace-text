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
package org.emeritus.search.lti.common.security.oauth2.client.lti.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.emeritus.search.lti.common.security.oauth2.client.lti.authentication.OidcAuthenticationToken;
import org.emeritus.search.lti.common.security.oauth2.client.lti.authentication.OidcLaunchFlowToken;
import org.emeritus.search.lti.common.security.oauth2.core.endpoint.OIDCLaunchFlowExchange;
import org.emeritus.search.lti.common.security.oauth2.core.endpoint.OIDCLaunchFlowResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationProvider;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.Assert;

/**
 * An implementation of an {@link AbstractAuthenticationProcessingFilter} for OAuth 2.0 Login.
 *
 * <p>
 * This authentication {@code Filter} handles the processing of an OAuth 2.0 Authorization Response
 * for the authorization code grant flow and delegates an {@link OAuth2LoginAuthenticationToken} to
 * the {@link AuthenticationManager} to log in the End-User.
 *
 * <p>
 * The OAuth 2.0 Authorization Response is processed as follows:
 *
 * <ul>
 * <li>Assuming the End-User (Resource Owner) has granted access to the Client, the Authorization
 * Server will append the {@link OAuth2ParameterNames#CODE code} and
 * {@link OAuth2ParameterNames#STATE state} parameters to the
 * {@link OAuth2ParameterNames#REDIRECT_URI redirect_uri} (provided in the Authorization Request)
 * and redirect the End-User's user-agent back to this {@code Filter} (the Client).</li>
 * <li>This {@code Filter} will then create an {@link OAuth2LoginAuthenticationToken} with the
 * {@link OAuth2ParameterNames#CODE code} received and delegate it to the
 * {@link AuthenticationManager} to authenticate.</li>
 * <li>Upon a successful authentication, an {@link OAuth2AuthenticationToken} is created
 * (representing the End-User {@code Principal}) and associated to the {@link OAuth2AuthorizedClient
 * Authorized Client} using the {@link OAuth2AuthorizedClientRepository}.</li>
 * <li>Finally, the {@link OAuth2AuthenticationToken} is returned and ultimately stored in the
 * {@link SecurityContextRepository} to complete the authentication processing.</li>
 * </ul>
 *
 * @author Joe Grandja
 * @see AbstractAuthenticationProcessingFilter
 * @see OAuth2LoginAuthenticationToken
 * @see OAuth2AuthenticationToken
 * @see OAuth2LoginAuthenticationProvider
 * @see OAuth2AuthorizationRequest
 * @see OAuth2AuthorizationResponse
 * @see AuthorizationRequestRepository
 * @see OAuth2AuthorizationRequestRedirectFilter
 * @see ClientRegistrationRepository
 * @see OAuth2AuthorizedClient
 * @see OAuth2AuthorizedClientRepository
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc6749#section-4.1">Section 4.1
 *      Authorization Code Grant</a>
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc6749#section-4.1.2">Section 4.1.2
 *      Authorization Response</a>
 * @since 5.0
 */
public class OAuth2LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
  /**
   * The default {@code URI} where this {@code Filter} processes authentication requests.
   */
  private static final String AUTHORIZATION_REQUEST_NOT_FOUND_ERROR_CODE =
      "authorization_request_not_found";

  /** The Constant CLIENT_REGISTRATION_NOT_FOUND_ERROR_CODE. */
  private static final String CLIENT_REGISTRATION_NOT_FOUND_ERROR_CODE =
      "client_registration_not_found";

  /** The client registration repository. */
  private ClientRegistrationRepository clientRegistrationRepository;

  /** The authorization request repository. */
  private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository =
      new HttpSessionOAuth2AuthorizationRequestRepository();

  /**
   * Constructs an {@code OAuth2LoginAuthenticationFilter} using the provided parameters.
   *
   * @param clientRegistrationRepository the repository of client registrations
   * @param filterProcessesUrl the {@code URI} where this {@code Filter} will process the
   *        authentication requests
   * @since 5.1
   */
  public OAuth2LoginAuthenticationFilter(ClientRegistrationRepository clientRegistrationRepository,
      String filterProcessesUrl) {
    super(filterProcessesUrl);
    Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository cannot be null");
    this.clientRegistrationRepository = clientRegistrationRepository;
  }

  /**
   * Attempt authentication.
   *
   * @param request the request
   * @param response the response
   * @return the authentication
   * @throws AuthenticationException the authentication exception
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws ServletException the servlet exception
   */
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

    if (!isAuthorizationResponse(request)) {
      OAuth2Error oauth2Error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST);
      throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
    }

    OAuth2AuthorizationRequest authorizationRequest =
        this.authorizationRequestRepository.removeAuthorizationRequest(request, response);
    if (authorizationRequest == null) {
      OAuth2Error oauth2Error = new OAuth2Error(AUTHORIZATION_REQUEST_NOT_FOUND_ERROR_CODE);
      throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
    }

    String registrationId = (String) authorizationRequest.getAdditionalParameters()
        .get(OAuth2ParameterNames.REGISTRATION_ID);
    ClientRegistration clientRegistration =
        this.clientRegistrationRepository.findByRegistrationId(registrationId);
    if (clientRegistration == null) {
      OAuth2Error oauth2Error = new OAuth2Error(CLIENT_REGISTRATION_NOT_FOUND_ERROR_CODE,
          "Client Registration not found with Id: " + registrationId, null);
      throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
    }

    // TODO need to handle error as well here.
    OIDCLaunchFlowResponse authorizationResponse = OIDCLaunchFlowResponse
        .success(request.getParameter("id_token")).state(request.getParameter("state")).build();

    OidcLaunchFlowToken authenticationRequest = new OidcLaunchFlowToken(clientRegistration,
        new OIDCLaunchFlowExchange(authorizationRequest, authorizationResponse));
    authenticationRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));

    OidcLaunchFlowToken authenticationResult =
        (OidcLaunchFlowToken) this.getAuthenticationManager().authenticate(authenticationRequest);

    // This is so that we can return the state to the client.
    OidcAuthenticationToken oidcAuthenticationToken = new OidcAuthenticationToken(
        authenticationResult.getPrincipal(), authenticationResult.getAuthorities(),
        authenticationResult.getClientRegistration().getRegistrationId(),
        authorizationResponse.getState());

    return oidcAuthenticationToken;
  }

  /**
   * Checks if is authorization response.
   *
   * @param request the request
   * @return true, if is authorization response
   */
  static boolean isAuthorizationResponse(HttpServletRequest request) {
    return isAuthorizationResponseSuccess(request) || isAuthorizationResponseError(request);
  }

  /**
   * Checks if is authorization response success.
   *
   * @param request the request
   * @return true, if is authorization response success
   */
  static boolean isAuthorizationResponseSuccess(HttpServletRequest request) {
    return request.getParameter("id_token") != null && request.getParameter("state") != null;
  }

  /**
   * Checks if is authorization response error.
   *
   * @param request the request
   * @return true, if is authorization response error
   */
  static boolean isAuthorizationResponseError(HttpServletRequest request) {
    return request.getParameter("error") != null && request.getParameter("state") != null;
  }

  /**
   * Sets the authorization request repository.
   *
   * @param authorizationRequestRepository the new authorization request repository
   */
  public final void setAuthorizationRequestRepository(
      AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository) {
    Assert.notNull(authorizationRequestRepository, "authorizationRequestRepository cannot be null");
    this.authorizationRequestRepository = authorizationRequestRepository;
  }
}
