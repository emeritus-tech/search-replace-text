package org.emeritus.search.utils;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The Class ParserUtils.
 */
@Component
public class ParserUtils {
  /** The log. */
  private Logger log = LoggerFactory.getLogger(ParserUtils.class);

  /**
   * Html 2 text.
   *
   * @param html the html
   * @return the string
   */
  public String html2text(String html) {
    return Jsoup.parse(html).text();
  }
}
