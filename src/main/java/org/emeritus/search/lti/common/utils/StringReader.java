package org.emeritus.search.lti.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * The Class StringReader.
 */
public class StringReader {

  /**
   * Read a InputStream into a String. Can use readAll() when we are on Java 9 or newer.
   *
   * @param inputStream the input stream
   * @return the string
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static String readString(InputStream inputStream) throws IOException {
    StringBuilder textBuilder = new StringBuilder();
    try (Reader reader = new BufferedReader(
        new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
      char[] buffer = new char[1024];
      while (reader.read(buffer) != -1) {
        textBuilder.append(buffer);
      }
    }
    return textBuilder.toString();
  }
}
