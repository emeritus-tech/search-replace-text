package org.emeritus.search.lti.base.controller;

import org.emeritus.search.lti.base.model.ConsumerDTO;
import org.emeritus.search.lti.common.lti.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class ConsumerController.
 */
@RestController
public class ConsumerController {

  /** The object mapper. */
  @Autowired
  ObjectMapper objectMapper;

  /**
   * Gets the consumer details.
   *
   * @return the consumer details
   * @throws JsonMappingException the json mapping exception
   * @throws JsonProcessingException the json processing exception
   */
  @GetMapping("/consumer/details")
  public ConsumerDTO getConsumerDetails() throws JsonMappingException, JsonProcessingException {

    ConsumerDTO consumerResponse = ConsumerDTO.builder().build();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication.getPrincipal() instanceof OidcUser) {
      OidcUser principal = ((OidcUser) authentication.getPrincipal());

      String userFullName = principal.getIdToken().getFullName();
      String userEmail = principal.getIdToken().getEmail();

      JsonNode context =
          objectMapper.readTree(principal.getIdToken().getClaimAsString(Claims.CONTEXT));
      JsonNode custom =
          objectMapper.readTree(principal.getIdToken().getClaimAsString(Claims.CUSTOM));

      consumerResponse = ConsumerDTO.builder().userFullName(userFullName).userEmail(userEmail)
          .context(context).custom(custom).build();
    }
    return consumerResponse;

  }

}
