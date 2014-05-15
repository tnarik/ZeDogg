/*    */ package emssn00p.util;
/*    */ 
/*    */ public class HTMLEncoder
/*    */ {
/*    */   public static final String AMP_HTML_STRING = "&amp;";
/*    */   public static final String GT_HTML_STRING = "&gt;";
/*    */   public static final String LT_HTML_STRING = "&lt;";
/*    */   public static final String BR_HTML_STRING = "<BR>";
/*    */   public static final String NON_BREAKING_SPACE_HTML_STRING = "&nbsp;";
/*    */   public static final char AMP_CHAR = '&';
/*    */   public static final char BR_CHAR = '\n';
/*    */   public static final char GT_CHAR = '>';
/*    */   public static final char LT_CHAR = '<';
/*    */   public static final char NON_BREAKING_CHAR = ' ';
/*    */ 
/*    */   public static String encodeString(String sEncode)
/*    */   {
/* 26 */     if (sEncode == null) {
/* 27 */       return null;
/*    */     }
/* 29 */     char[] c = sEncode.toCharArray();
/* 30 */     StringBuffer out = new StringBuffer();
/* 31 */     for (int i = 0; i < c.length; i++) {
/* 32 */       String enc = encodeChar(c[i]);
/* 33 */       if (enc != null)
/* 34 */         out.append(enc);
/*    */       else {
/* 36 */         out.append(c[i]);
/*    */       }
/*    */     }
/* 39 */     return out.toString();
/*    */   }
/*    */ 
/*    */   public static void encodeStringBuffer(StringBuffer sEncode) {
/* 43 */     if (sEncode == null) {
/* 44 */       return;
/*    */     }
/* 46 */     for (int i = 0; i < sEncode.length(); i++) {
/* 47 */       String enc = encodeChar(sEncode.charAt(i));
/* 48 */       if (enc != null) {
/* 49 */         sEncode.replace(i, i + 1, enc);
/* 50 */         i += enc.length();
/* 51 */         i--;
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   protected static String encodeChar(char c) {
/* 57 */     switch (c) {
/*    */     case ' ':
/* 59 */       return "&nbsp;";
/*    */     case '>':
/* 61 */       return "&gt;";
/*    */     case '<':
/* 63 */       return "&lt;";
/*    */     case '&':
/* 65 */       return "&amp;";
/*    */     case '\n':
/* 67 */       return "<BR>";
/*    */     }
/* 69 */     return null;
/*    */   }
/*    */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.util.HTMLEncoder
 * JD-Core Version:    0.6.1
 */