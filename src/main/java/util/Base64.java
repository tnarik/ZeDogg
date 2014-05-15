/*     */ package emssn00p.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class Base64
/*     */ {
/*  66 */   private String lineSeparator = System.getProperty("line.separator");
/*     */ 
/*  71 */   private int lineLength = 72;
/*     */ 
/* 330 */   static final char[] valueToChar = new char[64];
/*     */ 
/* 335 */   static final int[] charToValue = new int[256];
/*     */   static final int IGNORE = -1;
/*     */   static final int PAD = -2;
/*     */   private static final boolean debug = false;
/*     */ 
/*     */   public String encode(byte[] b)
/*     */   {
/*  89 */     int outputLength = (b.length + 2) / 3 * 4;
/*     */ 
/*  92 */     if (this.lineLength != 0)
/*     */     {
/*  94 */       int lines = (outputLength + this.lineLength - 1) / this.lineLength - 1;
/*  95 */       if (lines > 0)
/*     */       {
/*  98 */         outputLength += lines * this.lineSeparator.length();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 103 */     StringBuffer sb = new StringBuffer(outputLength);
/*     */ 
/* 106 */     int linePos = 0;
/*     */ 
/* 109 */     int len = b.length / 3 * 3;
/* 110 */     int leftover = b.length - len;
/* 111 */     for (int i = 0; i < len; i += 3)
/*     */     {
/* 116 */       linePos += 4;
/* 117 */       if (linePos > this.lineLength)
/*     */       {
/* 119 */         if (this.lineLength != 0)
/*     */         {
/* 121 */           sb.append(this.lineSeparator);
/*     */         }
/* 123 */         linePos = 4;
/*     */       }
/*     */ 
/* 128 */       int combined = b[(i + 0)] & 0xFF;
/* 129 */       combined <<= 8;
/* 130 */       combined |= b[(i + 1)] & 0xFF;
/* 131 */       combined <<= 8;
/* 132 */       combined |= b[(i + 2)] & 0xFF;
/*     */ 
/* 136 */       int c3 = combined & 0x3F;
/* 137 */       combined >>>= 6;
/* 138 */       int c2 = combined & 0x3F;
/* 139 */       combined >>>= 6;
/* 140 */       int c1 = combined & 0x3F;
/* 141 */       combined >>>= 6;
/* 142 */       int c0 = combined & 0x3F;
/*     */ 
/* 146 */       sb.append(valueToChar[c0]);
/* 147 */       sb.append(valueToChar[c1]);
/* 148 */       sb.append(valueToChar[c2]);
/* 149 */       sb.append(valueToChar[c3]);
/*     */     }
/*     */ 
/* 153 */     switch (leftover)
/*     */     {
/*     */     case 0:
/*     */     default:
/* 158 */       break;
/*     */     case 1:
/* 163 */       linePos += 4;
/* 164 */       if (linePos > this.lineLength)
/*     */       {
/* 167 */         if (this.lineLength != 0)
/*     */         {
/* 169 */           sb.append(this.lineSeparator);
/*     */         }
/* 171 */         linePos = 4;
/*     */       }
/*     */ 
/* 176 */       sb.append(encode(new byte[] { b[len], 0, 0 }).substring(0, 2));
/*     */ 
/* 178 */       sb.append("==");
/* 179 */       break;
/*     */     case 2:
/* 184 */       linePos += 4;
/* 185 */       if (linePos > this.lineLength)
/*     */       {
/* 187 */         if (this.lineLength != 0)
/*     */         {
/* 189 */           sb.append(this.lineSeparator);
/*     */         }
/* 191 */         linePos = 4;
/*     */       }
/*     */ 
/* 195 */       sb.append(encode(new byte[] { b[len], b[(len + 1)], 0 }).substring(0, 3));
/*     */ 
/* 197 */       sb.append("=");
/*     */     }
/*     */ 
/* 202 */     if (outputLength != sb.length())
/*     */     {
/* 204 */       System.out.println("oops: minor program flaw: output length mis-estimated");
/* 205 */       System.out.println("estimate:" + outputLength);
/* 206 */       System.out.println("actual:" + sb.length());
/*     */     }
/* 208 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public byte[] decode(String s)
/*     */   {
/* 220 */     byte[] b = new byte[s.length() / 4 * 3];
/*     */ 
/* 223 */     int cycle = 0;
/*     */ 
/* 226 */     int combined = 0;
/*     */ 
/* 229 */     int j = 0;
/*     */ 
/* 231 */     int len = s.length();
/* 232 */     int dummies = 0;
/* 233 */     for (int i = 0; i < len; i++)
/*     */     {
/* 236 */       int c = s.charAt(i);
/* 237 */       int value = c <= 255 ? charToValue[c] : -1;
/*     */ 
/* 239 */       switch (value)
/*     */       {
/*     */       case -1:
/* 243 */         break;
/*     */       case -2:
/* 246 */         value = 0;
/* 247 */         dummies++;
/*     */       default:
/* 251 */         switch (cycle)
/*     */         {
/*     */         case 0:
/* 254 */           combined = value;
/* 255 */           cycle = 1;
/* 256 */           break;
/*     */         case 1:
/* 259 */           combined <<= 6;
/* 260 */           combined |= value;
/* 261 */           cycle = 2;
/* 262 */           break;
/*     */         case 2:
/* 265 */           combined <<= 6;
/* 266 */           combined |= value;
/* 267 */           cycle = 3;
/* 268 */           break;
/*     */         case 3:
/* 271 */           combined <<= 6;
/* 272 */           combined |= value;
/*     */ 
/* 278 */           b[(j + 2)] = (byte)combined;
/* 279 */           combined >>>= 8;
/* 280 */           b[(j + 1)] = (byte)combined;
/* 281 */           combined >>>= 8;
/* 282 */           b[j] = (byte)combined;
/* 283 */           j += 3;
/* 284 */           cycle = 0;
/*     */         }
/*     */         break;
/*     */       }
/*     */     }
/*     */ 
/* 290 */     if (cycle != 0)
/*     */     {
/* 292 */       throw new ArrayIndexOutOfBoundsException("Input to decode not an even multiple of 4 characters; pad with =.");
/*     */     }
/* 294 */     j -= dummies;
/* 295 */     if (b.length != j)
/*     */     {
/* 297 */       byte[] b2 = new byte[j];
/* 298 */       System.arraycopy(b, 0, b2, 0, j);
/* 299 */       b = b2;
/*     */     }
/* 301 */     return b;
/*     */   }
/*     */ 
/*     */   public void setLineLength(int length)
/*     */   {
/* 312 */     this.lineLength = (length / 4 * 4);
/*     */   }
/*     */ 
/*     */   public void setLineSeparator(String lineSeparator)
/*     */   {
/* 324 */     this.lineSeparator = lineSeparator;
/*     */   }
/*     */ 
/*     */   public static void show(byte[] b)
/*     */   {
/* 386 */     for (int i = 0; i < b.length; i++)
/*     */     {
/* 388 */       System.out.print(Integer.toHexString(b[i] & 0xFF) + " ");
/*     */     }
/* 390 */     System.out.println();
/*     */   }
/*     */ 
/*     */   public static void display(byte[] b)
/*     */   {
/* 398 */     for (int i = 0; i < b.length; i++)
/*     */     {
/* 400 */       System.out.print((char)b[i]);
/*     */     }
/* 402 */     System.out.println();
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 351 */     for (int i = 0; i <= 25; i++) {
/* 352 */       valueToChar[i] = (char)(65 + i);
/*     */     }
/* 354 */     for (int i = 0; i <= 25; i++) {
/* 355 */       valueToChar[(i + 26)] = (char)(97 + i);
/*     */     }
/* 357 */     for (int i = 0; i <= 9; i++)
/* 358 */       valueToChar[(i + 52)] = (char)(48 + i);
/* 359 */     valueToChar[62] = '+';
/* 360 */     valueToChar[63] = '/';
/*     */ 
/* 363 */     for (int i = 0; i < 256; i++)
/*     */     {
/* 365 */       charToValue[i] = -1;
/*     */     }
/*     */ 
/* 368 */     for (int i = 0; i < 64; i++)
/*     */     {
/* 370 */       charToValue[valueToChar[i]] = i;
/*     */     }
/*     */ 
/* 373 */     charToValue[61] = -2;
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.util.Base64
 * JD-Core Version:    0.6.1
 */