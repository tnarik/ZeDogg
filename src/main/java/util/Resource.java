/*     */ package emssn00p.util;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.net.URL;
/*     */ 
/*     */ public class Resource
/*     */ {
/*     */   protected String _name;
/*     */ 
/*     */   public Resource()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Resource(String name)
/*     */   {
/*  54 */     this._name = name;
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/*  75 */     this._name = name;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  85 */     return this._name;
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream()
/*     */   {
/*  96 */     InputStream in = ResourceUtils.getResourceAsStream(this, this);
/*     */ 
/*  98 */     return in;
/*     */   }
/*     */ 
/*     */   public InputStreamReader getInputStreamReader()
/*     */   {
/* 109 */     InputStream in = ResourceUtils.getResourceAsStream(this, this);
/*     */ 
/* 111 */     if (in == null) {
/* 112 */       return null;
/*     */     }
/*     */ 
/* 115 */     InputStreamReader reader = new InputStreamReader(in);
/*     */ 
/* 117 */     return reader;
/*     */   }
/*     */ 
/*     */   public URL getURL()
/*     */   {
/* 126 */     return ResourceUtils.getResourceAsURL(this, this);
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.util.Resource
 * JD-Core Version:    0.6.1
 */