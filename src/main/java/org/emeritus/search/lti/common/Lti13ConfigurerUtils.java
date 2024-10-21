package org.emeritus.search.lti.common;

import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

/**
 * The Class Lti13ConfigurerUtils.
 */
class Lti13ConfigurerUtils {

  /**
   * Gets the client registration repository.
   *
   * @param <B> the generic type
   * @param builder the builder
   * @return the client registration repository
   */
  static <B extends HttpSecurityBuilder<B>> ClientRegistrationRepository getClientRegistrationRepository(
      B builder) {
    ClientRegistrationRepository clientRegistrationRepository =
        builder.getSharedObject(ClientRegistrationRepository.class);
    if (clientRegistrationRepository == null) {
      clientRegistrationRepository = getClientRegistrationRepositoryBean(builder);
      builder.setSharedObject(ClientRegistrationRepository.class, clientRegistrationRepository);
    }
    return clientRegistrationRepository;
  }

  /**
   * Gets the client registration repository bean.
   *
   * @param <B> the generic type
   * @param builder the builder
   * @return the client registration repository bean
   */
  private static <B extends HttpSecurityBuilder<B>> ClientRegistrationRepository getClientRegistrationRepositoryBean(
      B builder) {
    return builder.getSharedObject(ApplicationContext.class)
        .getBean(ClientRegistrationRepository.class);
  }

}
