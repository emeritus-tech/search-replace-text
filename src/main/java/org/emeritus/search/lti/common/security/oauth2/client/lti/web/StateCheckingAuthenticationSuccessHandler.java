package org.emeritus.search.lti.common.security.oauth2.client.lti.web;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.emeritus.search.lti.common.security.oauth2.client.lti.authentication.OidcAuthenticationToken;
import org.emeritus.search.lti.common.utils.StringReader;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/*
 * Copyright 2002-2016 the original author or authors.
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

import com.fasterxml.jackson.core.io.JsonStringEncoder;

/**
 * This is needed so that we can pass the state value to the client(browser) to allow it to check if
 * it matches the value saved at the start of the login.
 *
 * @author Matthew Buckett
 * @see StateAuthorizationRedirectHandler
 */
public class StateCheckingAuthenticationSuccessHandler
    extends AbstractAuthenticationTargetUrlRequestHandler implements AuthenticationSuccessHandler {

  /** The use state. */
  private final boolean useState;

  /** The encoder. */
  private final JsonStringEncoder encoder = JsonStringEncoder.getInstance();

  /** The html template. */
  private final String htmlTemplate;

  /** The name. */
  private String name = "/org/emeritus/search/step-3-redirect.html";

  /**
   * Instantiates a new state checking authentication success handler.
   *
   * @param useState if true then use the state parameter for tracking logins.
   */
  public StateCheckingAuthenticationSuccessHandler(boolean useState) {
    this.useState = useState;
    try {
      htmlTemplate = StringReader.readString(getClass().getResourceAsStream(name));
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read " + name, e);
    }
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
   * Calls the parent class {@code handle()} method to forward or redirect to the target URL, and
   * then calls {@code clearAuthenticationAttributes()} to remove any leftover session data.
   *
   * @param request the request
   * @param response the response
   * @param authentication the authentication
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws ServletException the servlet exception
   */
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    handle(request, response, authentication);
    clearAuthenticationAttributes(request);
  }

  /**
   * Handle.
   *
   * @param request the request
   * @param response the response
   * @param authentication the authentication
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws ServletException the servlet exception
   */
  protected void handle(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    if (!useState) {
      super.handle(request, response, authentication);
      return;
    }
    String targetUrl = determineTargetUrl(request, response, authentication);

    if (response.isCommitted()) {
      logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
      return;
    }

    if (!(authentication instanceof OidcAuthenticationToken)) {
      logger.debug(
          "Authentication should be OidcAuthenticationToken. Unable to redirect to " + targetUrl);
      return;
    }
    OidcAuthenticationToken oidcAuthenticationToken = (OidcAuthenticationToken) authentication;
    String state = oidcAuthenticationToken.getState();


    response.setContentType("text/html;charset=UTF-8");
    PrintWriter writer = response.getWriter();
    writer.append(htmlTemplate.replaceFirst("@@state@@", state).replaceFirst("@@url@@", targetUrl));
  }

  /**
   * Removes temporary authentication-related data which may have been stored in the session during
   * the authentication process.
   *
   * @param request the request
   */
  protected final void clearAuthenticationAttributes(HttpServletRequest request) {
    HttpSession session = request.getSession(false);

    if (session == null) {
      return;
    }

    session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
  }
}
