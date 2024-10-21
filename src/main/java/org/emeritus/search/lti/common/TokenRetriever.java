package org.emeritus.search.lti.common;

import java.net.URI;
import java.security.KeyPair;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

/**
 * This gets a token to use for LTI Services. It uses the public/private key associated with a
 * client registration to sign a JWT which is then used to obtain an access token.
 *
 * @see <a href=
 *      "https://www.imsglobal.org/spec/lti/v1p3/#token-endpoint-claim-and-services">https://www.imsglobal.org/spec/lti/v1p3/#token-endpoint-claim-and-services</a>
 * @see <a href=
 *      "https://www.imsglobal.org/spec/security/v1p0/#using-json-web-tokens-with-oauth-2-0-client-credentials-grant">https://www.imsglobal.org/spec/security/v1p0/#using-json-web-tokens-with-oauth-2-0-client-credentials-grant</a>
 */
public class TokenRetriever {

  /** The log. */
  private final Logger log = LoggerFactory.getLogger(TokenRetriever.class);

  /** The jwt lifetime. */
  // Lifetime of our JWT in seconds
  private int jwtLifetime = 86400;

  /** The key pair service. */
  private final KeyPairService keyPairService;

  /** The rest template. */
  private final RestTemplate restTemplate;

  /**
   * Instantiates a new token retriever.
   *
   * @param keyPairService the key pair service
   */
  public TokenRetriever(KeyPairService keyPairService) {
    this.keyPairService = keyPairService;
    restTemplate = new RestTemplate(Arrays.asList(new FormHttpMessageConverter(),
        new OAuth2AccessTokenResponseHttpMessageConverter()));
    restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
  }

  /**
   * Sets the jwt lifetime.
   *
   * @param jwtLifetime the new jwt lifetime
   */
  public void setJwtLifetime(int jwtLifetime) {
    this.jwtLifetime = jwtLifetime;
  }

  /**
   * Gets the token.
   *
   * @param clientRegistration the client registration
   * @param scopes the scopes
   * @return the token
   * @throws JOSEException the JOSE exception
   */
  public OAuth2AccessTokenResponse getToken(ClientRegistration clientRegistration, String... scopes)
      throws JOSEException {
    if (scopes.length == 0) {
      throw new IllegalArgumentException("You must supply some scopes to request.");
    }
    Objects.requireNonNull(clientRegistration, "You must supply a clientRegistration.");

    SignedJWT signedJWT = createJWT(clientRegistration);
    MultiValueMap<String, String> formData = buildFormData(signedJWT, scopes);
    // We are using RestTemplate here as that's what the existing OAuth2 code in Spring uses at the
    // moment.
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
    RequestEntity<MultiValueMap<String, String>> requestEntity =
        new RequestEntity<>(formData, headers, HttpMethod.POST,
            URI.create(clientRegistration.getProviderDetails().getTokenUri()));
    ResponseEntity<OAuth2AccessTokenResponse> exchange =
        restTemplate.exchange(requestEntity, OAuth2AccessTokenResponse.class);
    return exchange.getBody();
  }

  /**
   * Creates the JWT.
   *
   * @param clientRegistration the client registration
   * @return the signed JWT
   * @throws JOSEException the JOSE exception
   */
  private SignedJWT createJWT(ClientRegistration clientRegistration) throws JOSEException {
    KeyPair keyPair = keyPairService.getKeyPair(clientRegistration.getRegistrationId());
    if (keyPair == null) {
      throw new NullPointerException("Failed to get keypair for client registration: "
          + clientRegistration.getRegistrationId());
    }
    String keyId = keyPairService.getKeyId(clientRegistration.getRegistrationId());

    RSASSASigner signer = new RSASSASigner(keyPair.getPrivate());

    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        // Both must be set to client ID according to spec.
        .issuer(clientRegistration.getClientId()).subject(clientRegistration.getClientId())
        .audience(clientRegistration.getProviderDetails().getTokenUri())
        .issueTime(Date.from(Instant.now())).jwtID(UUID.randomUUID().toString())
        // 60 Seconds
        .expirationTime(Date.from(Instant.now().plusSeconds(jwtLifetime))).build();

    // We don't have to include a key ID, however if we don't then when you use a JWK file the
    // consuming application
    // won't know which key to use to verify the signature
    final JWSHeader.Builder builder = new JWSHeader.Builder(JWSAlgorithm.RS256);
    builder.type(JOSEObjectType.JWT);
    if (keyId != null) {
      builder.keyID(keyId);
    }
    JWSHeader jwt = builder.build();
    SignedJWT signedJWT = new SignedJWT(jwt, claimsSet);
    signedJWT.sign(signer);

    if (log.isDebugEnabled()) {
      log.debug("Created signed token: {}", signedJWT.serialize());
    }

    return signedJWT;
  }

  /**
   * Builds the form data.
   *
   * @param signedJWT the signed JWT
   * @param scopes the scopes
   * @return the multi value map
   */
  private MultiValueMap<String, String> buildFormData(SignedJWT signedJWT, String[] scopes) {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("grant_type", "client_credentials");
    formData.add("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
    formData.add("scope", String.join(" ", scopes));
    formData.add("client_assertion", signedJWT.serialize());
    return formData;
  }

}
