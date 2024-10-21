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
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * This {@code Filter} initiates the authorization code grant or implicit grant flow by redirecting
 * the End-User's user-agent to the Authorization Server's Authorization Endpoint.
 * 
 * <p>
 * It builds the OAuth 2.0 Authorization Request, which is used as the redirect {@code URI} to the
 * Authorization Endpoint. The redirect {@code URI} will include the client identifier, requested
 * scope(s), state, response type, and a redirection URI which the authorization server will send
 * the user-agent back to once access is granted (or denied) by the End-User (Resource Owner).
 * 
 * <p>
 * By default, this {@code Filter} responds to authorization requests at the {@code URI}
 * {@code /oauth2/authorization/{registrationId}} using the default
 * {@link OAuth2AuthorizationRequestResolver}. The {@code URI} template variable
 * {@code {registrationId}} represents the {@link ClientRegistration#getRegistrationId()
 * registration identifier} of the client that is used for initiating the OAuth 2.0 Authorization
 * Request.
 * 
 * <p>
 * The default base {@code URI} {@code /oauth2/authorization} may be overridden via the constructor
 * {@link #OAuth2AuthorizationRequestRedirectFilter(ClientRegistrationRepository, String)}, or
 * alternatively, an {@code OAuth2AuthorizationRequestResolver} may be provided to the constructor
 * {@link #OAuth2AuthorizationRequestRedirectFilter(OAuth2AuthorizationRequestResolver)} to override
 * the resolving of authorization requests.
 *
 * @author Joe Grandja
 * @author Rob Winch
 * @see OAuth2AuthorizationRequest
 * @see OAuth2AuthorizationRequestResolver
 * @see AuthorizationRequestRepository
 * @see ClientRegistration
 * @see ClientRegistrationRepository
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc6749#section-4.1">Section 4.1
 *      Authorization Code Grant</a>
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc6749#section-4.1.1">Section 4.1.1
 *      Authorization Request (Authorization Code)</a>
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc6749#section-4.2">Section 4.2
 *      Implicit Grant</a>
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc6749#section-4.2.1">Section 4.2.1
 *      Authorization Request (Implicit)</a>
 * @since 5.0
 */
public class OAuth2AuthorizationRequestRedirectFilter extends OncePerRequestFilter {

  /** The throwable analyzer. */
  private final ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();

  /** The authorization redirect strategy. */
  private final RedirectStrategy authorizationRedirectStrategy = new DefaultRedirectStrategy();

  /** The authorization request resolver. */
  private OAuth2AuthorizationRequestResolver authorizationRequestResolver;

  /** The authorization request repository. */
  private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository =
      new HttpSessionOAuth2AuthorizationRequestRepository();

  /** The state authorization redirect handler. */
  private AuthorizationRedirectHandler stateAuthorizationRedirectHandler =
      new StateAuthorizationRedirectHandler();

  /** The use state. */
  private boolean useState = false;

  /**
   * Constructs an {@code OAuth2AuthorizationRequestRedirectFilter} using the provided parameters.
   *
   * @param clientRegistrationRepository the repository of client registrations
   * @param authorizationRequestBaseUri the base {@code URI} used for authorization requests
   */
  public OAuth2AuthorizationRequestRedirectFilter(
      ClientRegistrationRepository clientRegistrationRepository,
      String authorizationRequestBaseUri) {
    Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository cannot be null");
    Assert.hasText(authorizationRequestBaseUri, "authorizationRequestBaseUri cannot be empty");
    this.authorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(
        clientRegistrationRepository, authorizationRequestBaseUri);
  }

  /**
   * Constructs an {@code OAuth2AuthorizationRequestRedirectFilter} using the provided parameters.
   *
   * @param authorizationRequestResolver the resolver used for resolving authorization requests
   * @since 5.1
   */
  public OAuth2AuthorizationRequestRedirectFilter(
      OAuth2AuthorizationRequestResolver authorizationRequestResolver) {
    Assert.notNull(authorizationRequestResolver, "authorizationRequestResolver cannot be null");
    this.authorizationRequestResolver = authorizationRequestResolver;
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

  /**
   * Sets the use state.
   *
   * @param useState the new use state
   */
  public void setUseState(boolean useState) {
    this.useState = useState;
  }

  /**
   * Do filter internal.
   *
   * @param request the request
   * @param response the response
   * @param filterChain the filter chain
   * @throws ServletException the servlet exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    try {
      OAuth2AuthorizationRequest authorizationRequest =
          this.authorizationRequestResolver.resolve(request);
      if (authorizationRequest != null) {
        this.sendRedirectForAuthorization(request, response, authorizationRequest);
        return;
      }
    } catch (Exception failed) {
      this.unsuccessfulRedirectForAuthorization(request, response, failed);
      return;
    }

    try {
      filterChain.doFilter(request, response);
    } catch (IOException ex) {
      throw ex;
    } catch (Exception ex) {
      // Check to see if we need to handle ClientAuthorizationRequiredException
      Throwable[] causeChain = this.throwableAnalyzer.determineCauseChain(ex);
      ClientAuthorizationRequiredException authzEx =
          (ClientAuthorizationRequiredException) this.throwableAnalyzer
              .getFirstThrowableOfType(ClientAuthorizationRequiredException.class, causeChain);
      if (authzEx != null) {
        try {
          OAuth2AuthorizationRequest authorizationRequest =
              this.authorizationRequestResolver.resolve(request, authzEx.getClientRegistrationId());
          if (authorizationRequest == null) {
            throw authzEx;
          }
          this.sendRedirectForAuthorization(request, response, authorizationRequest);
        } catch (Exception failed) {
          this.unsuccessfulRedirectForAuthorization(request, response, failed);
        }
        return;
      }

      if (ex instanceof ServletException) {
        throw (ServletException) ex;
      } else if (ex instanceof RuntimeException) {
        throw (RuntimeException) ex;
      } else {
        throw new RuntimeException(ex);
      }
    }
  }

  /**
   * Send redirect for authorization.
   *
   * @param request the request
   * @param response the response
   * @param authorizationRequest the authorization request
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws ServletException the servlet exception
   */
  private void sendRedirectForAuthorization(HttpServletRequest request,
      HttpServletResponse response, OAuth2AuthorizationRequest authorizationRequest)
      throws IOException, ServletException {

    // We also want implicit because we want to log the user in as the result of an Implicit Grant
    // and need to keep
    // the authorization request.
    if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(authorizationRequest.getGrantType())
        || AuthorizationGrantType.IMPLICIT.equals(authorizationRequest.getGrantType())) {
      this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request,
          response);
    }

    if (this.useState) {
      // Want to pass in the authorizationRequest so we can pass the state to the browser.
      this.stateAuthorizationRedirectHandler.sendRedirect(request, response, authorizationRequest);
    } else {
      // Standard session based usage so we just do a normal browser redirect.
      this.authorizationRedirectStrategy.sendRedirect(request, response,
          authorizationRequest.getAuthorizationRequestUri());
    }
    // We don't need to save the request as the final URL to redirect to is in the claims normally
    // we would save
    // the current request here.
  }

  /**
   * Unsuccessful redirect for authorization.
   *
   * @param request the request
   * @param response the response
   * @param failed the failed
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws ServletException the servlet exception
   */
  private void unsuccessfulRedirectForAuthorization(HttpServletRequest request,
      HttpServletResponse response, Exception failed) throws IOException, ServletException {

    if (logger.isErrorEnabled()) {
      logger.error("Authorization Request failed: " + failed.toString(), failed);
    }
    response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
  }

  /**
   * The Class DefaultThrowableAnalyzer.
   */
  private static final class DefaultThrowableAnalyzer extends ThrowableAnalyzer {

    /**
     * Inits the extractor map.
     */
    protected void initExtractorMap() {
      super.initExtractorMap();
      registerExtractor(ServletException.class, throwable -> {
        ThrowableAnalyzer.verifyThrowableHierarchy(throwable, ServletException.class);
        return ((ServletException) throwable).getRootCause();
      });
    }
  }
}
