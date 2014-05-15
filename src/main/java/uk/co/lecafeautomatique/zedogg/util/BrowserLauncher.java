/*     */ package uk.co.lecafeautomatique.zedogg.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public class BrowserLauncher
/*     */ {
/*     */   private static int jvm;
/*     */   private static Object browser;
/* 201 */   private static boolean loadedWithoutErrors = true;
/*     */   private static Class mrjFileUtilsClass;
/*     */   private static Class mrjOSTypeClass;
/*     */   private static Class aeDescClass;
/*     */   private static Constructor aeTargetConstructor;
/*     */   private static Constructor appleEventConstructor;
/*     */   private static Constructor aeDescConstructor;
/*     */   private static Method findFolder;
/*     */   private static Method getFileCreator;
/*     */   private static Method getFileType;
/*     */   private static Method openURL;
/*     */   private static Method makeOSType;
/*     */   private static Method putParameter;
/*     */   private static Method sendNoReply;
/*     */   private static Object kSystemFolderType;
/*     */   private static Integer keyDirectObject;
/*     */   private static Integer kAutoGenerateReturnID;
/*     */   private static Integer kAnyTransactionID;
/*     */   private static Object linkage;
/*     */   private static final String JDirect_MacOSX = "/System/Library/Frameworks/Carbon.framework/Frameworks/HIToolbox.framework/HIToolbox";
/*     */   private static final int MRJ_2_0 = 0;
/*     */   private static final int MRJ_2_1 = 1;
/*     */   private static final int MRJ_3_0 = 3;
/*     */   private static final int MRJ_3_1 = 4;
/*     */   private static final int WINDOWS_NT = 5;
/*     */   private static final int WINDOWS_9x = 6;
/*     */   private static final int OTHER = -1;
/*     */   private static final String FINDER_TYPE = "FNDR";
/*     */   private static final String FINDER_CREATOR = "MACS";
/*     */   private static final String GURL_EVENT = "GURL";
/*     */   private static final String FIRST_WINDOWS_PARAMETER = "/c";
/*     */   private static final String SECOND_WINDOWS_PARAMETER = "start";
/*     */   private static final String THIRD_WINDOWS_PARAMETER = "\"\"";
/*     */   private static final String NETSCAPE_REMOTE_PARAMETER = "-remote";
/*     */   private static final String NETSCAPE_OPEN_PARAMETER_START = "'openURL(";
/*     */   private static final String NETSCAPE_OPEN_PARAMETER_END = ")'";
/*     */   private static String errorMessage;
/*     */ 
/*     */   private static boolean loadClasses()
/*     */   {
/* 255 */     switch (jvm) {
/*     */     case 0:
/*     */       try {
/* 258 */         Class aeTargetClass = Class.forName("com.apple.MacOS.AETarget");
/* 259 */         Class osUtilsClass = Class.forName("com.apple.MacOS.OSUtils");
/* 260 */         Class appleEventClass = Class.forName("com.apple.MacOS.AppleEvent");
/* 261 */         Class aeClass = Class.forName("com.apple.MacOS.ae");
/* 262 */         aeDescClass = Class.forName("com.apple.MacOS.AEDesc");
/*     */ 
/* 264 */         aeTargetConstructor = aeTargetClass.getDeclaredConstructor(new Class[] { Integer.TYPE });
/* 265 */         appleEventConstructor = appleEventClass.getDeclaredConstructor(new Class[] { Integer.TYPE, Integer.TYPE, aeTargetClass, Integer.TYPE, Integer.TYPE });
/* 266 */         aeDescConstructor = aeDescClass.getDeclaredConstructor(new Class[] { String.class });
/*     */ 
/* 268 */         makeOSType = osUtilsClass.getDeclaredMethod("makeOSType", new Class[] { String.class });
/* 269 */         putParameter = appleEventClass.getDeclaredMethod("putParameter", new Class[] { Integer.TYPE, aeDescClass });
/* 270 */         sendNoReply = appleEventClass.getDeclaredMethod("sendNoReply", new Class[0]);
/*     */ 
/* 272 */         Field keyDirectObjectField = aeClass.getDeclaredField("keyDirectObject");
/* 273 */         keyDirectObject = (Integer)keyDirectObjectField.get(null);
/* 274 */         Field autoGenerateReturnIDField = appleEventClass.getDeclaredField("kAutoGenerateReturnID");
/* 275 */         kAutoGenerateReturnID = (Integer)autoGenerateReturnIDField.get(null);
/* 276 */         Field anyTransactionIDField = appleEventClass.getDeclaredField("kAnyTransactionID");
/* 277 */         kAnyTransactionID = (Integer)anyTransactionIDField.get(null);
/*     */       } catch (ClassNotFoundException cnfe) {
/* 279 */         errorMessage = cnfe.getMessage();
/* 280 */         return false;
/*     */       } catch (NoSuchMethodException nsme) {
/* 282 */         errorMessage = nsme.getMessage();
/* 283 */         return false;
/*     */       } catch (NoSuchFieldException nsfe) {
/* 285 */         errorMessage = nsfe.getMessage();
/* 286 */         return false;
/*     */       } catch (IllegalAccessException iae) {
/* 288 */         errorMessage = iae.getMessage();
/* 289 */         return false;
/*     */       }
/*     */     case 1:
/*     */       try
/*     */       {
/* 294 */         mrjFileUtilsClass = Class.forName("com.apple.mrj.MRJFileUtils");
/* 295 */         mrjOSTypeClass = Class.forName("com.apple.mrj.MRJOSType");
/* 296 */         Field systemFolderField = mrjFileUtilsClass.getDeclaredField("kSystemFolderType");
/* 297 */         kSystemFolderType = systemFolderField.get(null);
/* 298 */         findFolder = mrjFileUtilsClass.getDeclaredMethod("findFolder", new Class[] { mrjOSTypeClass });
/* 299 */         getFileCreator = mrjFileUtilsClass.getDeclaredMethod("getFileCreator", new Class[] { File.class });
/* 300 */         getFileType = mrjFileUtilsClass.getDeclaredMethod("getFileType", new Class[] { File.class });
/*     */       } catch (ClassNotFoundException cnfe) {
/* 302 */         errorMessage = cnfe.getMessage();
/* 303 */         return false;
/*     */       } catch (NoSuchFieldException nsfe) {
/* 305 */         errorMessage = nsfe.getMessage();
/* 306 */         return false;
/*     */       } catch (NoSuchMethodException nsme) {
/* 308 */         errorMessage = nsme.getMessage();
/* 309 */         return false;
/*     */       } catch (SecurityException se) {
/* 311 */         errorMessage = se.getMessage();
/* 312 */         return false;
/*     */       } catch (IllegalAccessException iae) {
/* 314 */         errorMessage = iae.getMessage();
/* 315 */         return false;
/*     */       }
/*     */     case 3:
/*     */       try
/*     */       {
/* 320 */         Class linker = Class.forName("com.apple.mrj.jdirect.Linker");
/* 321 */         Constructor constructor = linker.getConstructor(new Class[] { Class.class });
/* 322 */         linkage = constructor.newInstance(new Object[] { BrowserLauncher.class });
/*     */       } catch (ClassNotFoundException cnfe) {
/* 324 */         errorMessage = cnfe.getMessage();
/* 325 */         return false;
/*     */       } catch (NoSuchMethodException nsme) {
/* 327 */         errorMessage = nsme.getMessage();
/* 328 */         return false;
/*     */       } catch (InvocationTargetException ite) {
/* 330 */         errorMessage = ite.getMessage();
/* 331 */         return false;
/*     */       } catch (InstantiationException ie) {
/* 333 */         errorMessage = ie.getMessage();
/* 334 */         return false;
/*     */       } catch (IllegalAccessException iae) {
/* 336 */         errorMessage = iae.getMessage();
/* 337 */         return false;
/*     */       }
/*     */     case 4:
/*     */       try
/*     */       {
/* 342 */         mrjFileUtilsClass = Class.forName("com.apple.mrj.MRJFileUtils");
/* 343 */         openURL = mrjFileUtilsClass.getDeclaredMethod("openURL", new Class[] { String.class });
/*     */       } catch (ClassNotFoundException cnfe) {
/* 345 */         errorMessage = cnfe.getMessage();
/* 346 */         return false;
/*     */       } catch (NoSuchMethodException nsme) {
/* 348 */         errorMessage = nsme.getMessage();
/* 349 */         return false;
/*     */       }
/*     */ 
/*     */     case 2:
/*     */     }
/*     */ 
/* 355 */     return true;
/*     */   }
/*     */ 
/*     */   private static Object locateBrowser()
/*     */   {
/* 367 */     if (browser != null) {
/* 368 */       return browser;
/*     */     }
/* 370 */     switch (jvm) {
/*     */     case 0:
/*     */       try {
/* 373 */         Integer finderCreatorCode = (Integer)makeOSType.invoke(null, new Object[] { "MACS" });
/* 374 */         Object aeTarget = aeTargetConstructor.newInstance(new Object[] { finderCreatorCode });
/* 375 */         Integer gurlType = (Integer)makeOSType.invoke(null, new Object[] { "GURL" });
/* 376 */         return appleEventConstructor.newInstance(new Object[] { gurlType, gurlType, aeTarget, kAutoGenerateReturnID, kAnyTransactionID });
/*     */       }
/*     */       catch (IllegalAccessException iae)
/*     */       {
/* 384 */         browser = null;
/* 385 */         errorMessage = iae.getMessage();
/* 386 */         return browser;
/*     */       } catch (InstantiationException ie) {
/* 388 */         browser = null;
/* 389 */         errorMessage = ie.getMessage();
/* 390 */         return browser;
/*     */       } catch (InvocationTargetException ite) {
/* 392 */         browser = null;
/* 393 */         errorMessage = ite.getMessage();
/* 394 */         return browser;
/*     */       }
/*     */     case 1:
/*     */       File systemFolder;
/*     */       try {
/* 399 */         systemFolder = (File)findFolder.invoke(null, new Object[] { kSystemFolderType });
/*     */       } catch (IllegalArgumentException iare) {
/* 401 */         browser = null;
/* 402 */         errorMessage = iare.getMessage();
/* 403 */         return browser;
/*     */       } catch (IllegalAccessException iae) {
/* 405 */         browser = null;
/* 406 */         errorMessage = iae.getMessage();
/* 407 */         return browser;
/*     */       } catch (InvocationTargetException ite) {
/* 409 */         browser = null;
/* 410 */         errorMessage = ite.getTargetException().getClass() + ": " + ite.getTargetException().getMessage();
/* 411 */         return browser;
/*     */       }
/* 413 */       String[] systemFolderFiles = systemFolder.list();
/*     */ 
/* 415 */       for (int i = 0; i < systemFolderFiles.length; i++) {
/*     */         try {
/* 417 */           File file = new File(systemFolder, systemFolderFiles[i]);
/* 418 */           if (file.isFile())
/*     */           {
/* 426 */             Object fileType = getFileType.invoke(null, new Object[] { file });
/* 427 */             if ("FNDR".equals(fileType.toString())) {
/* 428 */               Object fileCreator = getFileCreator.invoke(null, new Object[] { file });
/* 429 */               if ("MACS".equals(fileCreator.toString())) {
/* 430 */                 browser = file.toString();
/* 431 */                 return browser;
/*     */               }
/*     */             }
/*     */           }
/*     */         } catch (IllegalArgumentException iare) {
/* 436 */           errorMessage = iare.getMessage();
/* 437 */           return null;
/*     */         } catch (IllegalAccessException iae) {
/* 439 */           browser = null;
/* 440 */           errorMessage = iae.getMessage();
/* 441 */           return browser;
/*     */         } catch (InvocationTargetException ite) {
/* 443 */           browser = null;
/* 444 */           errorMessage = ite.getTargetException().getClass() + ": " + ite.getTargetException().getMessage();
/* 445 */           return browser;
/*     */         }
/*     */       }
/* 448 */       browser = null;
/* 449 */       break;
/*     */     case 3:
/*     */     case 4:
/* 452 */       browser = "";
/* 453 */       break;
/*     */     case 5:
/* 455 */       browser = "cmd.exe";
/* 456 */       break;
/*     */     case 6:
/* 458 */       browser = "command.com";
/* 459 */       break;
/*     */     case -1:
/*     */     case 2:
/* 462 */     }browser = "netscape";
/*     */ 
/* 465 */     return browser;
/*     */   }
/*     */ 
/*     */   public static void openURL(String url)
/*     */     throws IOException
/*     */   {
/* 474 */     if (!loadedWithoutErrors) {
/* 475 */       throw new IOException("Exception in finding browser: " + errorMessage);
/*     */     }
/* 477 */     Object browser = locateBrowser();
/* 478 */     if (browser == null)
/* 479 */       throw new IOException("Unable to locate browser: " + errorMessage);
/*     */     Process process;
/* 482 */     switch (jvm) {
/*     */     case 0:
/* 484 */       Object aeDesc = null;
/*     */       try {
/* 486 */         aeDesc = aeDescConstructor.newInstance(new Object[] { url });
/* 487 */         putParameter.invoke(browser, new Object[] { keyDirectObject, aeDesc });
/* 488 */         sendNoReply.invoke(browser, new Object[0]);
/*     */       } catch (InvocationTargetException ite) {
/* 490 */         throw new IOException("InvocationTargetException while creating AEDesc: " + ite.getMessage());
/*     */       } catch (IllegalAccessException iae) {
/* 492 */         throw new IOException("IllegalAccessException while building AppleEvent: " + iae.getMessage());
/*     */       } catch (InstantiationException ie) {
/* 494 */         throw new IOException("InstantiationException while creating AEDesc: " + ie.getMessage());
/*     */       } finally {
/* 496 */         aeDesc = null;
/* 497 */         browser = null;
/*     */       }
/* 499 */       break;
/*     */     case 1:
/* 501 */       Runtime.getRuntime().exec(new String[] { (String)browser, url });
/* 502 */       break;
/*     */     case 3:
/* 504 */       int[] instance = new int[1];
/* 505 */       int result = ICStart(instance, 0);
/* 506 */       if (result == 0) {
/* 507 */         int[] selectionStart = { 0 };
/* 508 */         byte[] urlBytes = url.getBytes();
/* 509 */         int[] selectionEnd = { urlBytes.length };
/* 510 */         result = ICLaunchURL(instance[0], new byte[] { 0 }, urlBytes, urlBytes.length, selectionStart, selectionEnd);
/*     */ 
/* 513 */         if (result == 0)
/*     */         {
/* 516 */           ICStop(instance);
/*     */         }
/* 518 */         else throw new IOException("Unable to launch URL: " + result); 
/*     */       }
/*     */       else
/*     */       {
/* 521 */         throw new IOException("Unable to create an Internet Config instance: " + result);
/*     */       }
/*     */       break;
/*     */     case 4:
/*     */       try {
/* 526 */         openURL.invoke(null, new Object[] { url });
/*     */       } catch (InvocationTargetException ite) {
/* 528 */         throw new IOException("InvocationTargetException while calling openURL: " + ite.getMessage());
/*     */       } catch (IllegalAccessException iae) {
/* 530 */         throw new IOException("IllegalAccessException while calling openURL: " + iae.getMessage());
/*     */       }
/*     */ 
/*     */     case 5:
/*     */     case 6:
/* 537 */       process = Runtime.getRuntime().exec(new String[] { (String)browser, "/c", "start", "\"\"", '"' + url + '"' });
/*     */       try
/*     */       {
/* 545 */         process.waitFor();
/* 546 */         process.exitValue();
/*     */       } catch (InterruptedException ie) {
/* 548 */         throw new IOException("InterruptedException while launching browser: " + ie.getMessage());
/*     */       }
/*     */ 
/*     */     case -1:
/* 555 */       process = Runtime.getRuntime().exec(new String[] { (String)browser, "-remote", "'openURL(" + url + ")'" });
/*     */       try
/*     */       {
/* 561 */         int exitCode = process.waitFor();
/* 562 */         if (exitCode != 0)
/* 563 */           Runtime.getRuntime().exec(new String[] { (String)browser, url });
/*     */       }
/*     */       catch (InterruptedException ie) {
/* 566 */         throw new IOException("InterruptedException while launching browser: " + ie.getMessage());
/*     */       }
/*     */ 
/*     */     case 2:
/*     */     default:
/* 571 */       Runtime.getRuntime().exec(new String[] { (String)browser, url });
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native int ICStart(int[] paramArrayOfInt, int paramInt);
/*     */ 
/*     */   private static native int ICStop(int[] paramArrayOfInt);
/*     */ 
/*     */   private static native int ICLaunchURL(int paramInt1, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2);
/*     */ 
/*     */   static
/*     */   {
/* 202 */     String osName = System.getProperty("os.name");
/* 203 */     if (osName.startsWith("Mac OS")) {
/* 204 */       String mrjVersion = System.getProperty("mrj.version");
/* 205 */       String majorMRJVersion = mrjVersion.substring(0, 3);
/*     */       try {
/* 207 */         double version = Double.valueOf(majorMRJVersion).doubleValue();
/* 208 */         if (version == 2.0D) {
/* 209 */           jvm = 0;
/* 210 */         } else if ((version >= 2.1D) && (version < 3.0D))
/*     */         {
/* 214 */           jvm = 1;
/* 215 */         } else if (version == 3.0D) {
/* 216 */           jvm = 3;
/* 217 */         } else if (version >= 3.1D)
/*     */         {
/* 219 */           jvm = 4;
/*     */         } else {
/* 221 */           loadedWithoutErrors = false;
/* 222 */           errorMessage = "Unsupported MRJ version: " + version;
/*     */         }
/*     */       } catch (NumberFormatException nfe) {
/* 225 */         loadedWithoutErrors = false;
/* 226 */         errorMessage = "Invalid MRJ version: " + mrjVersion;
/*     */       }
/* 228 */     } else if (osName.startsWith("Windows")) {
/* 229 */       if (osName.indexOf("9") != -1)
/* 230 */         jvm = 6;
/*     */       else
/* 232 */         jvm = 5;
/*     */     }
/*     */     else {
/* 235 */       jvm = -1;
/*     */     }
/*     */ 
/* 238 */     if (loadedWithoutErrors)
/* 239 */       loadedWithoutErrors = loadClasses();
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.util.BrowserLauncher
 * JD-Core Version:    0.6.1
 */
