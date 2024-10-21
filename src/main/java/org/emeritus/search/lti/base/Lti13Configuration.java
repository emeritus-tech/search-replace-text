package org.emeritus.search.lti.base;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import org.emeritus.search.lti.common.KeyPairService;
import org.emeritus.search.lti.common.SingleKeyPairService;
import org.emeritus.search.lti.common.TokenRetriever;
import org.emeritus.search.lti.common.nrps.NamesRoleService;
import org.emeritus.search.lti.common.utils.KeyStoreKeyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;

/**
 * The Class Lti13Configuration.
 */
@Configuration
public class Lti13Configuration {

  /** The log. */
  private final Logger log = LoggerFactory.getLogger(Lti13Configuration.class);

  /**
   * The location of the JWK key file.
   */
  @Value("${lti.jwk.location:config/jwk.jks}")
  private String location;

  /**
   * The password for the JWK key file.
   */
  @Value("${lti.jwk.password:store-pass}")
  private String storePassword;

  /***
   * The ID of the key in the JKS file.
   */
  @Value("${lti.jwk.id:lti-jwt-id}")
  private String jwtId;

  /**
   * Names role service.
   *
   * @param clientRegistrationRepository the client registration repository
   * @param tokenRetriever the token retriever
   * @return the names role service
   */
  @Bean
  public NamesRoleService namesRoleService(
      ClientRegistrationRepository clientRegistrationRepository, TokenRetriever tokenRetriever) {
    return new NamesRoleService(clientRegistrationRepository, tokenRetriever);
  }

  /**
   * Token retriever.
   *
   * @param keyPairService the key pair service
   * @return the token retriever
   */
  @Bean
  public TokenRetriever tokenRetriever(KeyPairService keyPairService) {
    return new TokenRetriever(keyPairService);
  }

  /**
   * Key pair service.
   *
   * @param keyPair the key pair
   * @return the key pair service
   */
  @Bean
  public KeyPairService keyPairService(KeyPair keyPair) {
    return new SingleKeyPairService(keyPair, jwtId);
  }

  /**
   * Key pair.
   *
   * @return the key pair
   * @throws IOException
   */
  @Bean
  public KeyPair keyPair() throws IOException {
    InputStream resource = null;
    try {
      resource = new ClassPathResource("/config/jwk.jks").getInputStream();
      if (resource != null) {
        KeyStoreKeyFactory ksFactory =
            new KeyStoreKeyFactory(resource, storePassword.toCharArray());
        log.info("Loaded key from " + location);
        return ksFactory.getKeyPair("jwt");
      } else {
        log.info("Generated a keypair, this shouldn't be used in production.");
        return KeyPairGenerator.getInstance("RSA").generateKeyPair();
      }
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Failed to generate keypair");
    }
  }

  /**
   * Jwk set.
   *
   * @return the JWK set
   * @throws IOException
   */
  @Bean
  public JWKSet jwkSet() throws IOException {
    RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey) keyPair().getPublic())
        .keyUse(KeyUse.SIGNATURE).algorithm(JWSAlgorithm.RS256).keyID(jwtId);
    return new JWKSet(builder.build());
  }
}
