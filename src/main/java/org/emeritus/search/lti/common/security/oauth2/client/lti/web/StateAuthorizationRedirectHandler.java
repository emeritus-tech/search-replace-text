package org.emeritus.search.lti.common.security.oauth2.client.lti.web;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.emeritus.search.lti.common.utils.StringReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import com.fasterxml.jackson.core.io.JsonStringEncoder;

/**
 * The Class StateAuthorizationRedirectHandler.
 *
 * @see StateCheckingAuthenticationSuccessHandler
 */
public class StateAuthorizationRedirectHandler implements AuthorizationRedirectHandler {

  /** The logger. */
  private Logger logger = LoggerFactory.getLogger(StateAuthorizationRedirectHandler.class);

  /** The encoder. */
  private final JsonStringEncoder encoder = JsonStringEncoder.getInstance();

  /** The html template. */
  private final String htmlTemplate;

  /** The name. */
  private String name = "/org/emeritus/search/step-1-redirect.html";

  /**
   * Instantiates a new state authorization redirect handler.
   */
  public StateAuthorizationRedirectHandler() {
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
   * This sends the user off, but before that it saves data in the user's browser's sessionStorage
   * so that when they come back we can check that noting malicious is going on.
   *
   * @param request the request
   * @param response the response
   * @param authorizationRequest the authorization request
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void sendRedirect(HttpServletRequest request, HttpServletResponse response,
      OAuth2AuthorizationRequest authorizationRequest) throws IOException {
    String url = authorizationRequest.getAuthorizationRequestUri();
    if (response.isCommitted()) {
      logger.debug("Response has already been committed. Unable to redirect to " + url);
      return;
    }
    String state = new String(encoder.quoteAsString(authorizationRequest.getState()));
    response.setContentType("text/html;charset=UTF-8");
    PrintWriter writer = response.getWriter();
    writer.append(htmlTemplate.replaceFirst("@@state@@", state).replaceFirst("@@url@@", url));
  }
}
