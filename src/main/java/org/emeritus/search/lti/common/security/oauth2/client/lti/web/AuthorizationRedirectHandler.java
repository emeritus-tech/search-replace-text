package org.emeritus.search.lti.common.security.oauth2.client.lti.web;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

/**
 * The Interface AuthorizationRedirectHandler.
 */
public interface AuthorizationRedirectHandler {

  /**
   * Send a redirect to the user to start authorization but make the authorization request
   * available.
   *
   * @param request the request
   * @param response the response
   * @param authorizationRequest the authorization request
   * @throws IOException Signals that an I/O exception has occurred.
   */
  void sendRedirect(HttpServletRequest request, HttpServletResponse response,
      OAuth2AuthorizationRequest authorizationRequest) throws IOException;

}
