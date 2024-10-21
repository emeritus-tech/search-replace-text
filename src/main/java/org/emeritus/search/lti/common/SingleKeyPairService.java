package org.emeritus.search.lti.common;

import java.security.KeyPair;

/**
 * Just uses the same keypair for all operations.
 */
public class SingleKeyPairService implements KeyPairService {

  /** The key pair. */
  private final KeyPair keyPair;

  /** The key id. */
  private final String keyId;

  /**
   * Instantiates a new single key pair service.
   *
   * @param keyPair the key pair
   * @param keyId the key id
   */
  public SingleKeyPairService(KeyPair keyPair, String keyId) {
    this.keyPair = keyPair;
    this.keyId = keyId;
  }

  /**
   * Gets the key pair.
   *
   * @param clientRegistrationId the client registration id
   * @return the key pair
   */
  @Override
  public KeyPair getKeyPair(String clientRegistrationId) {
    return keyPair;
  }

  /**
   * Gets the key id.
   *
   * @param clientRegistration the client registration
   * @return the key id
   */
  @Override
  public String getKeyId(String clientRegistration) {
    return keyId;
  }
}
