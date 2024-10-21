package org.emeritus.search.lti.common;

import java.io.IOException;
import java.time.Instant;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

/**
 * The Class OAuth2Interceptor.
 */
public class OAuth2Interceptor implements ClientHttpRequestInterceptor {

  /** The access token. */
  private OAuth2AccessToken accessToken;

  /**
   * Instantiates a new o auth 2 interceptor.
   *
   * @param accessToken the access token
   */
  public OAuth2Interceptor(OAuth2AccessToken accessToken) {
    this.accessToken = accessToken;
  }

  /**
   * Gets the access token value.
   *
   * @return the access token value
   */
  public String getAccessTokenValue() {
    return accessToken.getTokenValue();
  }

  /**
   * Checks if is valid.
   *
   * @return true, if is valid
   */
  public boolean isValid() {
    return accessToken.getExpiresAt() != null && Instant.now().isBefore(accessToken.getExpiresAt());
  }

  /**
   * Intercept.
   *
   * @param request the request
   * @param body the body
   * @param execution the execution
   * @return the client http response
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body,
      ClientHttpRequestExecution execution) throws IOException {
    String accessToken = getAccessTokenValue();
    request.getHeaders().setBearerAuth(accessToken);
    return execution.execute(request, body);
  }
}
