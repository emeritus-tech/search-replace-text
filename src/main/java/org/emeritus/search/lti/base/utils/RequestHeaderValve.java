package org.emeritus.search.lti.base.utils;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHeaderValve extends ValveBase {

  private Logger logger = (Logger) LoggerFactory.getLogger(RequestHeaderValve.class);

  /**
   * Instantiates a new same site cooke valve.
   */
  public RequestHeaderValve() {
    super();
  }

  /**
   * Invoke.
   *
   * @param request the request
   * @param response the response
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws ServletException the servlet exception
   */
  @Override
  public void invoke(Request request, Response response) throws IOException, ServletException {
    modifyRequestHeaders(request);
    getNext().invoke(request, response);
  }

  /**
   * Modify request headers.
   *
   * @param request the request
   */
  private void modifyRequestHeaders(Request request) {
    logger.info("Checking for valid request headernames");
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      String headerValue = request.getHeader(headerName);
      String modifiedHeaderValue = modifyHeaderValue(headerName, headerValue);
      request.getCoyoteRequest().getMimeHeaders().setValue(headerName)
          .setString(modifiedHeaderValue);
    }
  }

  /**
   * Modify header value.
   *
   * @param headerName the header name
   * @param headerValue the header value
   * @return the string
   */
  private String modifyHeaderValue(String headerName, String headerValue) {
    StringBuilder modifiedValue = new StringBuilder();
    StringBuilder unacceptableChars = new StringBuilder();
    for (int i = 0; i < headerValue.length(); i++) {
      char currentChar = headerValue.charAt(i);
      if (String.valueOf(currentChar).matches("[\\p{IsAssigned}&&[^\\p{IsControl}]]*")) {
        modifiedValue.append(currentChar);
      } else {
        unacceptableChars.append(currentChar);
      }
    }
    if (unacceptableChars.length() > 0) {
      logger.info("Incoming request headername: {}, headervalue: {} and modified headervalue: {}",
          headerName, headerValue, modifiedValue);
    }
    return modifiedValue.toString();
  }
}
