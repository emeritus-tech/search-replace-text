package org.emeritus.search.lti.common.nrps;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import org.emeritus.search.lti.common.OAuth2Interceptor;
import org.emeritus.search.lti.common.TokenRetriever;
import org.emeritus.search.lti.common.lti.Claims;
import org.emeritus.search.lti.common.security.oauth2.client.lti.authentication.OidcLaunchFlowToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.client.RestTemplate;
import com.nimbusds.jose.JOSEException;
import net.minidev.json.JSONObject;

/**
 * The Class NamesRoleService.
 */
public class NamesRoleService {

  /** The client registration repository. */
  private final ClientRegistrationRepository clientRegistrationRepository;

  /** The token retriever. */
  private final TokenRetriever tokenRetriever;

  /**
   * Instantiates a new names role service.
   *
   * @param clientRegistrationRepository the client registration repository
   * @param tokenRetriever the token retriever
   */
  public NamesRoleService(ClientRegistrationRepository clientRegistrationRepository,
      TokenRetriever tokenRetriever) {
    this.clientRegistrationRepository = clientRegistrationRepository;
    this.tokenRetriever = tokenRetriever;
  }

  /**
   * Gets the members.
   *
   * @param oAuth2AuthenticationToken the o auth 2 authentication token
   * @param includeResourceLink the include resource link
   * @return the members
   */
  public NRPSResponse getMembers(OidcLaunchFlowToken oAuth2AuthenticationToken,
      boolean includeResourceLink) {
    OidcUser principal = oAuth2AuthenticationToken.getPrincipal();
    if (principal != null) {
      Object o = principal.getClaims().get(LtiScopes.LTI_NRPS_CLAIM);
      if (o instanceof JSONObject) {
        JSONObject json = (JSONObject) o;
        String contextMembershipsUrl = json.getAsString("context_memberships_url");
        if (contextMembershipsUrl != null && !contextMembershipsUrl.isEmpty()) {
          // Got a URL to go to.
          Object r = principal.getClaims().get(Claims.RESOURCE_LINK);
          String resourceLinkId = null;
          if (includeResourceLink && r instanceof JSONObject) {
            JSONObject resourceJson = (JSONObject) r;
            resourceLinkId = resourceJson.getAsString("id");
          }
          return loadMembers(contextMembershipsUrl, resourceLinkId,
              oAuth2AuthenticationToken.getClientRegistration().getRegistrationId());
        }
      }
    }
    return null;
  }

  /**
   * Load members.
   *
   * @param contextMembershipsUrl the context memberships url
   * @param resourceLinkId the resource link id
   * @param clientRegistrationId the client registration id
   * @return the NRPS response
   */
  private NRPSResponse loadMembers(String contextMembershipsUrl, String resourceLinkId,
      String clientRegistrationId) {

    ClientRegistration clientRegistration =
        clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
    if (clientRegistration == null) {
      throw new IllegalStateException(
          "Failed to find client registration for: " + clientRegistrationId);
    }
    try {
      OAuth2AccessTokenResponse token =
          tokenRetriever.getToken(clientRegistration, LtiScopes.LTI_NRPS_SCOPE);

      String url = contextMembershipsUrl;
      if (resourceLinkId != null) {
        url = url + "?rlid=" + URLEncoder.encode(resourceLinkId, "UTF-8");
      }
      RestTemplate client = new RestTemplate();
      client.setInterceptors(
          Collections.singletonList(new OAuth2Interceptor(token.getAccessToken())));
      // TODO Needs to set accept header to:
      // application/vnd.ims.lti-nrps.v2.membershipcontainer+json
      // TODO Needs to handle Link headers
      NRPSResponse response = client.getForObject(url, NRPSResponse.class);
      return response;
    } catch (JOSEException e) {
      throw new RuntimeException("Failed to sign JWT", e);
    } catch (UnsupportedEncodingException e) {
      // This should never happen
      throw new RuntimeException("Unable to find encoding.", e);
    }
  }
}
