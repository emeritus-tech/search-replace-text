package org.emeritus.search.lti.common;

import java.time.Duration;
import java.util.Collections;
import org.emeritus.search.lti.common.security.oauth2.OAuthAuthenticationFailureHandler;
import org.emeritus.search.lti.common.security.oauth2.client.lti.authentication.OidcLaunchFlowAuthenticationProvider;
import org.emeritus.search.lti.common.security.oauth2.client.lti.authentication.TargetLinkUriAuthenticationSuccessHandler;
import org.emeritus.search.lti.common.security.oauth2.client.lti.web.OAuth2AuthorizationRequestRedirectFilter;
import org.emeritus.search.lti.common.security.oauth2.client.lti.web.OAuth2LoginAuthenticationFilter;
import org.emeritus.search.lti.common.security.oauth2.client.lti.web.OIDCInitiatingLoginRequestResolver;
import org.emeritus.search.lti.common.security.oauth2.client.lti.web.StateAuthorizationRequestRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;


/**
 * <h2>Shared Objects Used</h2>
 * 
 * The following shared objects are used:
 * 
 * <ul>
 * <li>{@link ClientRegistrationRepository}</li>
 * </ul>
 * .
 */
public class Lti13Configurer extends AbstractHttpConfigurer<Lti13Configurer, HttpSecurity> {

  /** The lti path. */
  private String ltiPath = "/lti";

  /** The login path. */
  private String loginPath = "/login";

  /** The login initiation path. */
  private String loginInitiationPath = "/login_initiation";

  /** The application event publisher. */
  private ApplicationEventPublisher applicationEventPublisher;

  /** The granted authorities mapper. */
  private GrantedAuthoritiesMapper grantedAuthoritiesMapper;

  /** The use state. */
  private boolean useState;

  /** The limit ip addresses. */
  private boolean limitIpAddresses;

  /**
   * Lti path.
   *
   * @param ltiPath the lti path
   * @return the lti 13 configurer
   */
  public Lti13Configurer ltiPath(String ltiPath) {
    this.ltiPath = ltiPath;
    return this;
  }

  /**
   * Login path.
   *
   * @param loginPath the login path
   * @return the lti 13 configurer
   */
  public Lti13Configurer loginPath(String loginPath) {
    this.loginPath = loginPath;
    return this;
  }

  /**
   * Login initiation path.
   *
   * @param loginInitiationPath the login initiation path
   * @return the lti 13 configurer
   */
  public Lti13Configurer loginInitiationPath(String loginInitiationPath) {
    this.loginInitiationPath = loginInitiationPath;
    return this;
  }

  /**
   * Application event publisher.
   *
   * @param applicationEventPublisher the application event publisher
   * @return the lti 13 configurer
   */
  public Lti13Configurer applicationEventPublisher(
      ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
    return this;
  }

  /**
   * Granted authorities mapper.
   *
   * @param grantedAuthoritiesMapper the granted authorities mapper
   * @return the lti 13 configurer
   */
  public Lti13Configurer grantedAuthoritiesMapper(
      GrantedAuthoritiesMapper grantedAuthoritiesMapper) {
    this.grantedAuthoritiesMapper = grantedAuthoritiesMapper;
    return this;
  }

  /**
   * This allows the login to not use cookies but instead use the state parameter and local storage
   * to handle the login. However if the application isn't using cookies then it will need to store
   * a session identifier on the client, this is most useful when building Single Page Applications
   * (SPA).
   *
   * @param useState if true then we don't use cookies, but use the state to track logins between
   *        requests.
   * @return the lti 13 configurer
   */
  public Lti13Configurer useState(boolean useState) {
    this.useState = useState;
    return this;
  }

  /**
   * Using this may cause problems for users who are behind a proxy or NAT setup that uses different
   * IP addresses for different requests, even if they are close together in time.
   *
   * @param limitIpAddresses if true then ensure that all the OAuth requests for a LTI launch come
   *        from the same IP
   * @return the lti 13 configurer
   */
  public Lti13Configurer limitIpAddresses(boolean limitIpAddresses) {
    this.limitIpAddresses = limitIpAddresses;
    return this;
  }

  /**
   * Inits the.
   *
   * @param http the http
   */
  @SuppressWarnings("unchecked")
  @Override
  public void init(HttpSecurity http) {
    // Allow LTI launches to bypass CSRF protection
    CsrfConfigurer<HttpSecurity> configurer = http.getConfigurer(CsrfConfigurer.class);
    if (configurer != null) {
      configurer.ignoringAntMatchers(ltiPath + "/**");
    }
    // In the future we should use CSP to limit the domains that can embed this tool
    HeadersConfigurer<HttpSecurity> headersConfigurer = http.getConfigurer(HeadersConfigurer.class);
    if (headersConfigurer != null) {
      headersConfigurer.frameOptions().disable();
    }
  }

  /**
   * Configure.
   *
   * @param http the http
   */
  @Override
  public void configure(HttpSecurity http) {
    ClientRegistrationRepository clientRegistrationRepository =
        Lti13ConfigurerUtils.getClientRegistrationRepository(http);

    OidcLaunchFlowAuthenticationProvider oidcLaunchFlowAuthenticationProvider =
        configureAuthenticationProvider(http);
    AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository =
        configureRequestRepository();
    // This handles step 1 of the IMS SEC
    // https://www.imsglobal.org/spec/security/v1p0/#step-1-third-party-initiated-login
    http.addFilterAfter(
        configureInitiationFilter(clientRegistrationRepository, authorizationRequestRepository),
        LogoutFilter.class);
    // This handles step 3 of the IMS SEC
    // https://www.imsglobal.org/spec/security/v1p0/#step-3-authentication-response
    http.addFilterAfter(configureLoginFilter(clientRegistrationRepository,
        oidcLaunchFlowAuthenticationProvider, authorizationRequestRepository),
        AbstractPreAuthenticatedProcessingFilter.class);
  }

  /**
   * Configure request repository.
   *
   * @return the authorization request repository
   */
  protected AuthorizationRequestRepository<OAuth2AuthorizationRequest> configureRequestRepository() {
    if (useState) {
      StateAuthorizationRequestRepository stateRepository =
          new StateAuthorizationRequestRepository(Duration.ofMinutes(1));
      stateRepository.setLimitIpAddress(limitIpAddresses);
      return stateRepository;
    }
    return new HttpSessionOAuth2AuthorizationRequestRepository();
  }

  /**
   * Configure authentication provider.
   *
   * @param http the http
   * @return the oidc launch flow authentication provider
   */
  protected OidcLaunchFlowAuthenticationProvider configureAuthenticationProvider(
      HttpSecurity http) {
    OidcLaunchFlowAuthenticationProvider oidcLaunchFlowAuthenticationProvider =
        new OidcLaunchFlowAuthenticationProvider();

    http.authenticationProvider(oidcLaunchFlowAuthenticationProvider);
    if (grantedAuthoritiesMapper != null) {
      oidcLaunchFlowAuthenticationProvider.setAuthoritiesMapper(grantedAuthoritiesMapper);
    }
    return oidcLaunchFlowAuthenticationProvider;
  }

  /**
   * Configure initiation filter.
   *
   * @param clientRegistrationRepository the client registration repository
   * @param authorizationRequestRepository the authorization request repository
   * @return the o auth 2 authorization request redirect filter
   */
  protected OAuth2AuthorizationRequestRedirectFilter configureInitiationFilter(
      ClientRegistrationRepository clientRegistrationRepository,
      AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository) {
    OIDCInitiatingLoginRequestResolver resolver = new OIDCInitiatingLoginRequestResolver(
        clientRegistrationRepository, ltiPath + loginInitiationPath);
    OAuth2AuthorizationRequestRedirectFilter filter =
        new OAuth2AuthorizationRequestRedirectFilter(resolver);
    filter.setAuthorizationRequestRepository(authorizationRequestRepository);
    filter.setUseState(this.useState);
    return filter;
  }

  /**
   * Configure login filter.
   *
   * @param clientRegistrationRepository the client registration repository
   * @param oidcLaunchFlowAuthenticationProvider the oidc launch flow authentication provider
   * @param authorizationRequestRepository the authorization request repository
   * @return the o auth 2 login authentication filter
   */
  protected OAuth2LoginAuthenticationFilter configureLoginFilter(
      ClientRegistrationRepository clientRegistrationRepository,
      OidcLaunchFlowAuthenticationProvider oidcLaunchFlowAuthenticationProvider,
      AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository) {
    // This filter handles the actual authentication and behaviour of errors
    OAuth2LoginAuthenticationFilter loginFilter =
        new OAuth2LoginAuthenticationFilter(clientRegistrationRepository, ltiPath + loginPath);
    // This is to find the URL that we should redirect the user to.
    TargetLinkUriAuthenticationSuccessHandler successHandler =
        new TargetLinkUriAuthenticationSuccessHandler(this.useState);
    loginFilter.setAuthenticationSuccessHandler(successHandler);
    // This is just so that you can get better error messages when something goes wrong.
    OAuthAuthenticationFailureHandler failureHandler = new OAuthAuthenticationFailureHandler();
    loginFilter.setAuthenticationFailureHandler(failureHandler);
    loginFilter.setAuthorizationRequestRepository(authorizationRequestRepository);
    ProviderManager authenticationManager =
        new ProviderManager(Collections.singletonList(oidcLaunchFlowAuthenticationProvider));
    if (applicationEventPublisher != null) {
      authenticationManager.setAuthenticationEventPublisher(
          new DefaultAuthenticationEventPublisher(applicationEventPublisher));
    }
    loginFilter.setAuthenticationManager(authenticationManager);
    return loginFilter;
  }

}
