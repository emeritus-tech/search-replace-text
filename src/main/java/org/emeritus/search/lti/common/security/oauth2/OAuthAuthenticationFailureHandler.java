package org.emeritus.search.lti.common.security.oauth2;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 * The Class OAuthAuthenticationFailureHandler.
 */
public class OAuthAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  /**
   * On authentication failure.
   *
   * @param request the request
   * @param response the response
   * @param exception the exception
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws ServletException the servlet exception
   */
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {
    if (exception instanceof OAuth2AuthenticationException) {
      response.sendError(HttpStatus.UNAUTHORIZED.value(),
          ((OAuth2AuthenticationException) exception).getError().getErrorCode() + " : "
              + exception.getMessage());
    } else {
      super.onAuthenticationFailure(request, response, exception);
    }
  }
}
