package org.emeritus.search.lti.common.security.oauth2.client.lti.web;

import java.time.Duration;
import java.util.function.BiConsumer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.Assert;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * This store uses the state value in the initial request to lookup the request when the client
 * returns. Normally this would expose the login to a CSRF attack but we also check that the remote
 * IP address is the same in an attempt to limit this.
 */
public final class StateAuthorizationRequestRepository
    implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

  /**
   * The key we use to store the remote IP in attributes.
   */
  public static final String REMOTE_IP = "remote_ip";

  /** The store. */
  // The cache of request in flight
  private final Cache<String, OAuth2AuthorizationRequest> store;

  // Should we limit the login to a single IP address.
  /** The limit ip address. */
  // This may cause problems when users are on mobile devices and subsequent requests don't use the
  // same IP address.
  private boolean limitIpAddress = true;

  // The handler to be called when an IP address mismatch is detected, by default this doesn't do
  // anything.
  /** The ip mismatch handler. */
  // It is expected that this will do something like logging of the mismatch.
  private BiConsumer<String, String> ipMismatchHandler = (a, b) -> {
  };

  /**
   * Instantiates a new state authorization request repository.
   *
   * @param duration the duration
   */
  public StateAuthorizationRequestRepository(Duration duration) {
    store = CacheBuilder.newBuilder().expireAfterAccess(duration).build();
  }

  /**
   * Sets the limit ip address.
   *
   * @param limitIpAddress the new limit ip address
   */
  public void setLimitIpAddress(boolean limitIpAddress) {
    this.limitIpAddress = limitIpAddress;
  }

  /**
   * Sets the ip mismatch handler.
   *
   * @param ipMismatchHandler the ip mismatch handler
   */
  public void setIpMismatchHandler(BiConsumer<String, String> ipMismatchHandler) {
    this.ipMismatchHandler = ipMismatchHandler;
  }

  /**
   * Load authorization request.
   *
   * @param request the request
   * @return the o auth 2 authorization request
   */
  @Override
  public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
    Assert.notNull(request, "request cannot be null");
    String stateParameter = request.getParameter(OAuth2ParameterNames.STATE);
    if (stateParameter == null) {
      return null;
    }
    OAuth2AuthorizationRequest oAuth2AuthorizationRequest = store.getIfPresent(stateParameter);
    if (oAuth2AuthorizationRequest != null) {
      // The IP address from the initial request
      String initialIp = oAuth2AuthorizationRequest.getAttribute(REMOTE_IP);
      if (initialIp != null) {
        String requestIp = request.getRemoteAddr();
        if (!initialIp.equals(request.getRemoteAddr())) {
          // Even if we aren't limiting IP address we call the consumer.
          ipMismatchHandler.accept(initialIp, requestIp);
          if (limitIpAddress) {
            return null;
          }
        }
      }
    }
    return oAuth2AuthorizationRequest;
  }

  /**
   * Save authorization request.
   *
   * @param authorizationRequest the authorization request
   * @param request the request
   * @param response the response
   */
  @Override
  public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
      HttpServletRequest request, HttpServletResponse response) {
    Assert.notNull(request, "request cannot be null");
    Assert.notNull(response, "response cannot be null");
    if (authorizationRequest == null) {
      this.removeAuthorizationRequest(request, response);
      return;
    }
    String state = authorizationRequest.getState();
    Assert.hasText(state, "authorizationRequest.state cannot be empty");
    store.put(state, authorizationRequest);
  }

  /**
   * Removes the authorization request.
   *
   * @param request the request
   * @return the o auth 2 authorization request
   */
  @Override
  public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
    return removeAuthorizationRequest(request, null);
  }

  /**
   * Removes the authorization request.
   *
   * @param request the request
   * @param response the response
   * @return the o auth 2 authorization request
   */
  @Override
  public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
      HttpServletResponse response) {
    OAuth2AuthorizationRequest authorizationRequest = loadAuthorizationRequest(request);
    if (authorizationRequest != null) {
      String stateParameter = request.getParameter(OAuth2ParameterNames.STATE);
      store.invalidate(stateParameter);
    }
    return authorizationRequest;
  }
}
