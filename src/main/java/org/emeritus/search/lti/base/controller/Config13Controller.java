package org.emeritus.search.lti.base.controller;

import static org.emeritus.search.lti.base.controller.lti13.Canvas13Extension.INSTRUCTURE;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.emeritus.search.lti.base.controller.lti13.Canvas13Extension;
import org.emeritus.search.lti.base.controller.lti13.Canvas13ExtensionBuilder;
import org.emeritus.search.lti.base.controller.lti13.Canvas13Placement;
import org.emeritus.search.lti.base.controller.lti13.Canvas13PlacementBuilder;
import org.emeritus.search.lti.base.controller.lti13.Canvas13Settings;
import org.emeritus.search.lti.base.controller.lti13.Canvas13SettingsBuilder;
import org.emeritus.search.lti.base.controller.lti13.Lti13Config;
import org.emeritus.search.lti.base.controller.lti13.Lti13ConfigBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.nimbusds.jose.jwk.JWKSet;

/**
 * The Class Config13Controller.
 */
@RestController
public class Config13Controller {

  /** The title. */
  @Value("${spring.application.name:LTI Tool}")
  private String title;

  /** The description. */
  @Value("${lti.application.description:Tool description.}")
  private String description;

  /** The jwt id. */
  @Value("${lti.jwk.id:lti-jwt-id}")
  private String jwtId;

  /** The jwk set. */
  private final JWKSet jwkSet;

  /**
   * Instantiates a new config 13 controller.
   *
   * @param jwkSet the jwk set
   */
  public Config13Controller(JWKSet jwkSet) {
    this.jwkSet = jwkSet;
  }

  /**
   * Gets the config.
   *
   * @param request the request
   * @return the config
   */
  @GetMapping("/config.json")
  public Lti13Config getConfig(HttpServletRequest request) {
    String urlPrefix = ServletUriComponentsBuilder.fromContextPath(request).toUriString();
    Canvas13Placement coursePlacement =
        new Canvas13PlacementBuilder().placement(Canvas13Placement.Placement.COURSE_NAVIGATION)
            .enabled(false).messageType(Canvas13Placement.MessageType.LtiResourceLinkRequest)
            .createCanvas13Placement();
    Canvas13Placement accountPlacement =
        new Canvas13PlacementBuilder().placement(Canvas13Placement.Placement.ACCOUNT_NAVIGATION)
            .enabled(true).messageType(Canvas13Placement.MessageType.LtiResourceLinkRequest)
            .createCanvas13Placement();
    List<Canvas13Placement> placements = Arrays.asList(coursePlacement, accountPlacement);
    Canvas13Settings canvas13Settings =
        new Canvas13SettingsBuilder().placements(placements).createCanvas13Settings();
    Collection<Canvas13Extension> extensions =
        Collections.singleton(new Canvas13ExtensionBuilder().platform(INSTRUCTURE)
            .domain(request.getServerName()).privacyLevel(Lti13Config.PrivacyLevel.PUBLIC)
            .settings(canvas13Settings).createCanvas13Extension());
    Map<String, String> customFields = new HashMap<>();
    customFields.put("canvas_css_common", "$Canvas.css.common");
    customFields.put("com_instructure_brand_config_json_url", "$com.instructure.brandConfigJS.url");
    return new Lti13ConfigBuilder().title(title).description(description)
        .oidcInitiaionUrl(urlPrefix + "/lti/login_initiation/canvas").targetLinkUri(urlPrefix)
        .extensions(extensions).publicJwk(jwkSet.getKeyByKeyId(jwtId).toPublicJWK().toJSONObject())
        .customFields(customFields).createLti13Config();
  }

}
