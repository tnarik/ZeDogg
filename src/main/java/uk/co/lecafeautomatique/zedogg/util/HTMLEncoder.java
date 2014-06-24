package uk.co.lecafeautomatique.zedogg.util;

public class HTMLEncoder {
  public static final String AMP_HTML_STRING = "&amp;";
  public static final String GT_HTML_STRING = "&gt;";
  public static final String LT_HTML_STRING = "&lt;";
  public static final String BR_HTML_STRING = "<BR>";
  public static final String NON_BREAKING_SPACE_HTML_STRING = "&nbsp;";
  public static final char AMP_CHAR = '&';
  public static final char BR_CHAR = '\n';
  public static final char GT_CHAR = '>';
  public static final char LT_CHAR = '<';
  public static final char NON_BREAKING_CHAR = ' ';

  public static String encodeString(String sEncode) {
    if (sEncode == null) {
      return null;
    }
    char[] c = sEncode.toCharArray();
    StringBuffer out = new StringBuffer();
    for (int i = 0; i < c.length; i++) {
      String enc = encodeChar(c[i]);
      if (enc != null)
        out.append(enc);
      else {
        out.append(c[i]);
      }
    }
    return out.toString();
  }

  public static void encodeStringBuffer(StringBuffer sEncode) {
    if (sEncode == null) {
      return;
    }
    for (int i = 0; i < sEncode.length(); i++) {
      String enc = encodeChar(sEncode.charAt(i));
      if (enc != null) {
        sEncode.replace(i, i + 1, enc);
        i += enc.length();
        i--;
      }
    }
  }

  protected static String encodeChar(char c) {
    switch (c) {
    case ' ':
      return "&nbsp;";
    case '>':
      return "&gt;";
    case '<':
      return "&lt;";
    case '&':
      return "&amp;";
    case '\n':
      return "<BR>";
    default:
    }
    return null;
  }
}
