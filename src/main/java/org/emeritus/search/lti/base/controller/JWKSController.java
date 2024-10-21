package org.emeritus.search.lti.base.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nimbusds.jose.jwk.JWKSet;

/**
 * The Class JWKSController.
 */
@RestController
public class JWKSController {

  /** The jwk set. */
  private final JWKSet jwkSet;

  /**
   * Instantiates a new JWKS controller.
   *
   * @param jwkSet the jwk set
   */
  public JWKSController(JWKSet jwkSet) {
    this.jwkSet = jwkSet;
  }

  /**
   * Keys.
   *
   * @return the map
   */
  @GetMapping("/.well-known/jwks.json")
  public Map<String, Object> keys() {
    return this.jwkSet.toJSONObject();
  }

}
