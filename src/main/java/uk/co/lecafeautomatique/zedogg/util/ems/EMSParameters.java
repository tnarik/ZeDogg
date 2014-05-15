/*     */ package uk.co.lecafeautomatique.zedogg.util.ems;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.HashSet;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class EMSParameters
/*     */   implements Cloneable, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 3257001077244114230L;
/*  35 */   protected int _hashCode = 0;
/*     */   protected String _serverURL;
/*     */   protected String _userName;
/*     */   protected String _password;
/*     */   protected Hashtable _sslParameters;
/*     */   protected boolean _displayEMSParameters;
/*     */   protected Set<String> _topics;
/*     */   protected String _clientId;
/*     */ 
/*     */   public EMSParameters()
/*     */   {
/*  57 */     this._serverURL = "tcp://localhost:7222";
/*  58 */     this._userName = "";
/*  59 */     this._password = "";
/*  60 */     this._sslParameters = new Hashtable();
/*  61 */     this._clientId = "";
/*  62 */     this._topics = new HashSet();
/*     */ 
/*  64 */     calcHashCode();
/*     */   }
/*     */ 
/*     */   public EMSParameters(String serverurl, String name, String password, Hashtable SSLparameters, boolean displayEMSParameters, Set<String> topics, String clientid)
/*     */   {
/*  81 */     this._serverURL = serverurl;
/*  82 */     this._userName = name;
/*  83 */     this._password = password;
/*  84 */     this._sslParameters = SSLparameters;
/*  85 */     this._displayEMSParameters = displayEMSParameters;
/*  86 */     this._topics = topics;
/*  87 */     this._clientId = clientid;
/*     */   }
/*     */ 
/*     */   public EMSParameters(Set<String> topics, String serverurl, String name, boolean displayRvParameters, String password, String description)
/*     */   {
/*  95 */     this._topics = new HashSet();
/*  96 */     this._topics.addAll(topics);
/*     */ 
/*  98 */     this._serverURL = serverurl;
/*  99 */     this._userName = name;
/* 100 */     this._password = password;
/*     */ 
/* 102 */     this._displayEMSParameters = displayRvParameters;
/*     */ 
/* 104 */     this._clientId = description;
/*     */ 
/* 106 */     calcHashCode();
/*     */   }
/*     */ 
/*     */   public String getDescription()
/*     */   {
/* 116 */     return this._clientId;
/*     */   }
/*     */ 
/*     */   public void setDescription(String description) {
/* 120 */     this._clientId = description;
/*     */   }
/*     */ 
/*     */   protected void calcHashCode()
/*     */   {
/* 128 */     String hcstr = new String();
/*     */ 
/* 130 */     if (this._serverURL != null) {
/* 131 */       hcstr = hcstr + this._serverURL;
/*     */     }
/*     */ 
/* 134 */     if (this._userName != null) {
/* 135 */       hcstr = hcstr + this._userName;
/*     */     }
/*     */ 
/* 138 */     if (this._password != null) {
/* 139 */       hcstr = hcstr + this._password;
/*     */     }
/*     */ 
/* 142 */     this._hashCode = hcstr.hashCode();
/*     */   }
/*     */ 
/*     */   public String getClientId()
/*     */   {
/* 148 */     return this._clientId;
/*     */   }
/*     */   public void setClientId(String id) {
/* 151 */     this._clientId = id;
/*     */   }
/*     */   public boolean isDisplayEMSParameters() {
/* 154 */     return this._displayEMSParameters;
/*     */   }
/*     */   public void setDisplayEMSParameters(boolean parameters) {
/* 157 */     this._displayEMSParameters = parameters;
/*     */   }
/*     */   public String getPassword() {
/* 160 */     return this._password;
/*     */   }
/*     */   public void setPassword(String _password) {
/* 163 */     this._password = _password;
/*     */   }
/*     */   public String getServerURL() {
/* 166 */     return this._serverURL;
/*     */   }
/*     */   public void setServerURL(String _serverurl) {
/* 169 */     this._serverURL = _serverurl;
/*     */   }
/*     */   public Hashtable getSSLParameters() {
/* 172 */     return this._sslParameters;
/*     */   }
/*     */   public void setSSLParameters(Hashtable parameters) {
/* 175 */     this._sslParameters = parameters;
/*     */   }
/*     */   public Set<String> getTopics() {
/* 178 */     return this._topics;
/*     */   }
/*     */   public void setTopics(Set<String> _topics) {
/* 181 */     this._topics = _topics;
/*     */   }
/*     */   public String getUserName() {
/* 184 */     return this._userName;
/*     */   }
/*     */   public void setUserName(String name) {
/* 187 */     this._userName = name;
/*     */   }
/*     */   public int getHashCode() {
/* 190 */     return this._hashCode;
/*     */   }
/*     */ 
/*     */   public void configureByLineString(String lineString) {
/* 194 */     int where = 0;
/*     */ 
/* 196 */     String[] results = new String[4];
/*     */ 
/* 198 */     StringTokenizer st = new StringTokenizer(lineString, "|", true);
/*     */ 
/* 200 */     int i = 0;
/* 201 */     while (st.hasMoreTokens()) {
/* 202 */       String s = st.nextToken();
/* 203 */       if ("|".equals(s)) {
/* 204 */         if (i++ >= 4) {
/* 205 */           throw new IllegalArgumentException("Input line " + lineString + " has too many fields");
/*     */         }
/*     */ 
/*     */       }
/* 209 */       else if (s != null)
/* 210 */         results[i] = s;
/*     */       else {
/* 212 */         results[i] = " ";
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 217 */     this._serverURL = results[0];
/* 218 */     this._userName = results[1];
/* 219 */     this._password = results[2];
/* 220 */     String topics = results[3];
/*     */ 
/* 223 */     StringTokenizer subjectTokenizer = new StringTokenizer(topics, ",", true);
/*     */ 
/* 226 */     while (subjectTokenizer.hasMoreTokens()) {
/* 227 */       String sto = subjectTokenizer.nextToken();
/* 228 */       if (!",".equals(sto))
/*     */       {
/* 232 */         this._topics.add(sto);
/*     */       }
/*     */     }
/*     */ 
/* 236 */     calcHashCode();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 240 */     String sRetval = new String(this._serverURL);
/*     */ 
/* 242 */     sRetval = sRetval + "|";
/*     */ 
/* 244 */     if (this._userName != null) {
/* 245 */       sRetval = sRetval + this._userName;
/*     */     }
/*     */ 
/* 248 */     sRetval = sRetval + "|";
/* 249 */     if (this._topics != null) {
/* 250 */       boolean first = true;
/* 251 */       Iterator i = this._topics.iterator();
/* 252 */       while (i.hasNext()) {
/* 253 */         if (!first) {
/* 254 */           sRetval = sRetval + ",";
/*     */         }
/* 256 */         sRetval = sRetval + (String)i.next();
/* 257 */         first = false;
/*     */       }
/*     */     }
/*     */ 
/* 261 */     return sRetval;
/*     */   }
/*     */ 
/*     */   public boolean isDisplayRvParameters() {
/* 265 */     return this._displayEMSParameters;
/*     */   }
/*     */ 
/*     */   public void setDisplayRvParameters(boolean displayRvParameters) {
/* 269 */     this._displayEMSParameters = displayRvParameters;
/*     */   }
/*     */ 
/*     */   public String getTopic() {
/* 273 */     String sRetVal = new String();
/*     */ 
/* 275 */     boolean first = true;
/* 276 */     Iterator i = this._topics.iterator();
/* 277 */     while (i.hasNext()) {
/* 278 */       if (!first) {
/* 279 */         sRetVal = sRetVal + ",";
/*     */       }
/* 281 */       sRetVal = sRetVal + (String)i.next();
/* 282 */       first = false;
/*     */     }
/* 284 */     return sRetVal;
/*     */   }
/*     */ 
/*     */   public void setTopics(String subjects)
/*     */   {
/* 295 */     if (this._topics == null) {
/* 296 */       throw new IllegalArgumentException("Topics may not be null");
/*     */     }
/*     */ 
/* 301 */     StringTokenizer subjectTokenizer = new StringTokenizer(subjects, ",", true);
/*     */ 
/* 304 */     while (subjectTokenizer.hasMoreTokens()) {
/* 305 */       String sto = subjectTokenizer.nextToken();
/* 306 */       if (!",".equals(sto))
/*     */       {
/* 310 */         this._topics.add(sto);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object clone() throws CloneNotSupportedException
/*     */   {
/* 317 */     return new EMSParameters(this._serverURL, this._userName, this._password, this._sslParameters, this._displayEMSParameters, this._topics, this._clientId);
/*     */   }
/*     */ 
/*     */   public void addTopic(String _subject)
/*     */   {
/* 324 */     this._topics.add(_subject);
/* 325 */     calcHashCode();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 332 */     return this._hashCode;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 339 */     boolean equals = false;
/*     */ 
/* 341 */     if (((o instanceof EMSParameters)) && 
/* 342 */       (hashCode() == o.hashCode())) {
/* 343 */       equals = true;
/*     */     }
/*     */ 
/* 347 */     return equals;
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.util.ems.EMSParameters
 * JD-Core Version:    0.6.1
 */
