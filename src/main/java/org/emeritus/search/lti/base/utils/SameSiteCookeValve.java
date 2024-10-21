package org.emeritus.search.lti.base.utils;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.SessionConfig;
import org.apache.catalina.valves.ValveBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This valve adds an additional session cookie for clients that ignore all cookies with
 * SameSite=None (iOS 12). We can't just add another cookie to the request in this valve because the
 * session has already been linked with the request by CoyoteAdaptor earlier in the call chain. I
 * couldn't easily find a way to have something like a valve execute before CoyoteAdaptor so we link
 * the session here ourselves.
 *
 * This class doesn't use a proper parse for the Set-Cookie header as we know the code building the
 * header (Tomcat) and I don't believe the extra complexity is worth it.
 */
public class SameSiteCookeValve extends ValveBase {

  /** The suffix. */
  // The suffix we want to append to the normal session cookie name for old clients.
  private final String suffix;

  private Logger logger = (Logger) LoggerFactory.getLogger(SameSiteCookeValve.class);

  /**
   * Instantiates a new same site cooke valve.
   */
  public SameSiteCookeValve() {
    this("-legacy");
  }

  /**
   * Instantiates a new same site cooke valve.
   *
   * @param suffix the suffix
   */
  public SameSiteCookeValve(String suffix) {
    this.suffix = suffix;
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
    String sessionCookieName = SessionConfig.getSessionCookieName(request.getContext());

    // Check for legacy cookie only if we haven't already found a valid session ID.
    if (!request.isRequestedSessionIdFromURL() && !request.isRequestedSessionIdFromCookie()) {
      Cookie[] cookies = request.getCookies();
      // Check if we've got a legacy cookie here.
      if (cookies != null) {
        for (Cookie cookie : cookies) {
          if ((sessionCookieName + suffix).equals(cookie.getName())) {
            request.setRequestedSessionId(cookie.getValue());
            request.setRequestedSessionCookie(true);
            request.setRequestedSessionURL(false);
            break;
          }
        }
      }
    }
    modifyRequestHeaders(request);
    getNext().invoke(request, response);

    // Find all the cookies set on this request and add a duplicate if they are a SameSite one.
    Collection<String> headers = response.getHeaders("Set-Cookie");
    for (String header : headers) {
      // Rather than parsing the cookie we just knock out the bit we don't want.
      String nonSameSiteHeader = header.replaceAll("; SameSite=None", "");
      // We only do this for the main session cookie set by Tomcat
      if (!header.equals(nonSameSiteHeader) && header.contains(sessionCookieName + "=")) {
        nonSameSiteHeader = nonSameSiteHeader.replaceFirst("=", suffix + "=");
        response.addHeader("Set-Cookie", nonSameSiteHeader);
      }
    }

  }

  /**
   * Modify request headers.
   *
   * @param request the request
   */
  private void modifyRequestHeaders(Request request) {
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
