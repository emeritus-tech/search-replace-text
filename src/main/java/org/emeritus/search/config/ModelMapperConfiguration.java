package org.emeritus.search.config;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NamingConventions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Class ModelMapperConfiguration.
 */
@Configuration
public class ModelMapperConfiguration {

  /**
   * Model mapper.
   *
   * @return the model mapper
   */
  @Bean
  public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setFieldMatchingEnabled(true)
        .setFieldAccessLevel(AccessLevel.PRIVATE)
        .setSourceNamingConvention(NamingConventions.JAVABEANS_MUTATOR)
        .setMatchingStrategy(MatchingStrategies.STRICT).setSkipNullEnabled(true);
    return modelMapper;
  }
}
