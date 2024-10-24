/*
 * Copyright 20013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.emeritus.search.lti.common.utils;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPublicKeySpec;
import org.springframework.core.io.Resource;

/**
 * Factory for RSA key pairs from a JKS keystore file. User provides a {@link Resource} location of
 * a keystore file and the password to unlock it, and the factory grabs the keypairs from the store
 * by name (and optionally password).
 *
 * This is taken out of the old spring-security oauth2 codebase.
 * 
 * @author Dave Syer
 *
 */
public class KeyStoreKeyFactory {

  /** The resource. */
  private InputStream resource;

  /** The password. */
  private char[] password;

  /** The store. */
  private KeyStore store;

  /** The lock. */
  private Object lock = new Object();

  /**
   * Instantiates a new key store key factory.
   *
   * @param resource the resource
   * @param password the password
   */
  public KeyStoreKeyFactory(InputStream resource, char[] password) {
    this.resource = resource;
    this.password = password;
  }

  /**
   * Gets the key pair.
   *
   * @param alias the alias
   * @return the key pair
   */
  public KeyPair getKeyPair(String alias) {
    return getKeyPair(alias, password);
  }

  /**
   * Gets the key pair.
   *
   * @param alias the alias
   * @param password the password
   * @return the key pair
   */
  public KeyPair getKeyPair(String alias, char[] password) {
    try {
      synchronized (lock) {
        if (store == null) {
          synchronized (lock) {
            store = KeyStore.getInstance("jks");
            store.load(resource, this.password);
          }
        }
      }
      RSAPrivateCrtKey key = (RSAPrivateCrtKey) store.getKey(alias, password);
      RSAPublicKeySpec spec = new RSAPublicKeySpec(key.getModulus(), key.getPublicExponent());
      PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(spec);
      return new KeyPair(publicKey, key);
    } catch (Exception e) {
      throw new IllegalStateException("Cannot load keys from store: " + resource, e);
    }
  }

}
