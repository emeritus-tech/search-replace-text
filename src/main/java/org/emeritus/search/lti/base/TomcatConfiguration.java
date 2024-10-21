package org.emeritus.search.lti.base;

import org.emeritus.search.lti.base.utils.RequestHeaderValve;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfiguration {

  /**
   * Cookie processor customizer.
   *
   * @return the web server factory customizer
   */
  @Bean
  WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieProcessorCustomizer() {
    return tomcatServletWebServerFactory -> {
      tomcatServletWebServerFactory.addContextValves(new RequestHeaderValve());
    };
  }
}
