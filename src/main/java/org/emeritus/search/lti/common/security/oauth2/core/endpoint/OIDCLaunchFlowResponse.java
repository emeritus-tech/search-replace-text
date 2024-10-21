/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.emeritus.search.lti.common.security.oauth2.core.endpoint;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * A representation of an IMS 1.0 Security Launch Flow response.
 *
 * @author Matthew Buckett
 * @see OAuth2Error
 * @see <a target="_blank" href=
 *      "https://www.imsglobal.org/spec/security/v1p0/#step-3-authentication-response">5.1.1.3 Step
 *      3: Authentication Response</a>
 */
public final class OIDCLaunchFlowResponse {

  /** The state. */
  private String state;

  /** The id token. */
  private String idToken;

  /** The error. */
  private OAuth2Error error;

  /**
   * Instantiates a new OIDC launch flow response.
   */
  private OIDCLaunchFlowResponse() {}

  /**
   * Gets the id token.
   *
   * @return the id token
   */
  public String getIdToken() {
    return idToken;
  }

  /**
   * Gets the state.
   *
   * @return the state
   */
  public String getState() {
    return this.state;
  }

  /**
   * Gets the error.
   *
   * @return the error
   */
  public OAuth2Error getError() {
    return this.error;
  }

  /**
   * Returns {@code true} if the Authorization Request succeeded, otherwise {@code false}.
   *
   * @return {@code true} if the Authorization Request succeeded, otherwise {@code false}
   */
  public boolean statusOk() {
    return !this.statusError();
  }

  /**
   * Returns {@code true} if the Authorization Request failed, otherwise {@code false}.
   *
   * @return {@code true} if the Authorization Request failed, otherwise {@code false}
   */
  public boolean statusError() {
    return (this.error != null && this.error.getErrorCode() != null);
  }

  /**
   * Returns a new {@link Builder}, initialized with the ID Token.
   *
   * @param idToken The ID Token
   * @return the {@link Builder}
   */
  public static Builder success(String idToken) {
    Assert.hasText(idToken, "code cannot be empty");
    return new Builder().idToken(idToken);
  }

  /**
   * Returns a new {@link Builder}, initialized with the error code.
   *
   * @param errorCode the error code
   * @return the {@link Builder}
   */
  public static Builder error(String errorCode) {
    Assert.hasText(errorCode, "errorCode cannot be empty");
    return new Builder().errorCode(errorCode);
  }

  /**
   * A builder for {@link OIDCLaunchFlowResponse}.
   */
  public static class Builder {

    /** The state. */
    private String state;

    /** The id token. */
    private String idToken;

    /** The error code. */
    private String errorCode;

    /** The error description. */
    private String errorDescription;

    /** The error uri. */
    private String errorUri;

    /**
     * Instantiates a new builder.
     */
    private Builder() {}

    /**
     * Sets the ID Token.
     *
     * @param idToken the ID Token.
     * @return the {@link Builder}
     */
    public Builder idToken(String idToken) {
      this.idToken = idToken;
      return this;
    }

    /**
     * Sets the state.
     *
     * @param state the state
     * @return the {@link Builder}
     */
    public Builder state(String state) {
      this.state = state;
      return this;
    }

    /**
     * Sets the error code.
     *
     * @param errorCode the error code
     * @return the {@link Builder}
     */
    public Builder errorCode(String errorCode) {
      this.errorCode = errorCode;
      return this;
    }

    /**
     * Sets the error description.
     *
     * @param errorDescription the error description
     * @return the {@link Builder}
     */
    public Builder errorDescription(String errorDescription) {
      this.errorDescription = errorDescription;
      return this;
    }

    /**
     * Sets the error uri.
     *
     * @param errorUri the error uri
     * @return the {@link Builder}
     */
    public Builder errorUri(String errorUri) {
      this.errorUri = errorUri;
      return this;
    }

    /**
     * Builds a new {@link OIDCLaunchFlowResponse}.
     *
     * @return a {@link OIDCLaunchFlowResponse}
     */
    public OIDCLaunchFlowResponse build() {
      if (StringUtils.hasText(this.idToken) && StringUtils.hasText(this.errorCode)) {
        throw new IllegalArgumentException("code and errorCode cannot both be set");
      }

      OIDCLaunchFlowResponse authorizationResponse = new OIDCLaunchFlowResponse();
      authorizationResponse.state = this.state;
      if (StringUtils.hasText(this.idToken)) {
        authorizationResponse.idToken = this.idToken;
      } else {
        authorizationResponse.error =
            new OAuth2Error(this.errorCode, this.errorDescription, this.errorUri);
      }
      return authorizationResponse;
    }
  }
}
