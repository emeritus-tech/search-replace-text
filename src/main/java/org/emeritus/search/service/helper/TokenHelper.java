package org.emeritus.search.service.helper;

import org.emeritus.canvas.CanvasApiFactory;
import org.emeritus.canvas.oauth.NonRefreshableOauthToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * The Class TokenHelper.
 */
@Component
public final class TokenHelper {

  /** The admin token. */
  @Value("${canvas.admin-token}")
  private String adminToken;

  /** The base url. */
  @Value("${canvas.baseurl}")
  private String baseUrl;

  /**
   * Gets the api factory.
   *
   * @return the api factory
   */
  public CanvasApiFactory getApiFactory() {
    CanvasApiFactory apiFactory = new CanvasApiFactory(baseUrl);
    return apiFactory;
  }

  /**
   * Gets the token.
   *
   * @return the token
   */
  public NonRefreshableOauthToken getToken() {
    NonRefreshableOauthToken oauthToken = new NonRefreshableOauthToken(adminToken);
    return oauthToken;
  }

}
