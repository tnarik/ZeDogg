/*     */ package uk.co.lecafeautomatique.zedogg.viewer.configure;
/*     */ 
/*     */ import uk.co.lecafeautomatique.zedogg.util.ems.EMSParameters;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.net.URL;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ 
/*     */ public class MRUListnerManager
/*     */ {
/*     */   private static final String CONFIG_FILE_NAME = "mru_listner_manager";
/*     */   private static final int DEFAULT_MAX_SIZE = 3;
/*  48 */   private int _maxSize = 0;
/*     */   private LinkedList<EMSParameters> _mruListnerList;
/*     */ 
/*     */   public MRUListnerManager()
/*     */   {
/*  56 */     load();
/*  57 */     setMaxSize(3);
/*     */   }
/*     */ 
/*     */   public MRUListnerManager(int maxSize) {
/*  61 */     load();
/*  62 */     setMaxSize(maxSize);
/*     */   }
/*     */ 
/*     */   public void save()
/*     */   {
/*  72 */     File file = new File(getFilename());
/*     */     try
/*     */     {
/*  75 */       ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
/*     */ 
/*  77 */       oos.writeObject(this._mruListnerList);
/*  78 */       oos.flush();
/*  79 */       oos.close();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  83 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  91 */     return this._mruListnerList.size();
/*     */   }
/*     */ 
/*     */   public Object getListner(int index)
/*     */   {
/*  99 */     if (index < size()) {
/* 100 */       return this._mruListnerList.get(index);
/*     */     }
/*     */ 
/* 103 */     return null;
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream(int index)
/*     */     throws IOException, FileNotFoundException
/*     */   {
/* 111 */     if (index < size()) {
/* 112 */       Object o = getListner(index);
/* 113 */       if ((o instanceof File)) {
/* 114 */         return getInputStream((File)o);
/*     */       }
/* 116 */       return getInputStream((URL)o);
/*     */     }
/*     */ 
/* 119 */     return null;
/*     */   }
/*     */ 
/*     */   public void set(EMSParameters p)
/*     */   {
/* 126 */     setMRU(p);
/*     */   }
/*     */ 
/*     */   public String[] getMRUFileList()
/*     */   {
/* 134 */     if (size() == 0) {
/* 135 */       return null;
/*     */     }
/*     */ 
/* 138 */     String[] ss = new String[size()];
/*     */ 
/* 141 */     for (int i = 0; i < size(); i++) {
/* 142 */       String Tstring = new String();
/* 143 */       EMSParameters p = (EMSParameters)getListner(i);
/* 144 */       Tstring = Tstring + p.getServerURL();
/* 145 */       Tstring = Tstring + "|";
/* 146 */       Tstring = Tstring + p.getTopic();
/*     */ 
/* 148 */       ss[i] = Tstring;
/*     */     }
/*     */ 
/* 153 */     return ss;
/*     */   }
/*     */ 
/*     */   public void moveToTop(int index)
/*     */   {
/* 162 */     this._mruListnerList.add(0, this._mruListnerList.remove(index));
/*     */   }
/*     */ 
/*     */   public static void createConfigurationDirectory()
/*     */   {
/* 172 */     String home = System.getProperty("user.home");
/* 173 */     String sep = System.getProperty("file.separator");
/* 174 */     File f = new File(home + sep + ".emssnoop");
/* 175 */     if (!f.exists())
/*     */       try {
/* 177 */         f.mkdir();
/*     */       }
/*     */       catch (SecurityException e) {
/* 180 */         e.printStackTrace();
/*     */       }
/*     */   }
/*     */ 
/*     */   protected InputStream getInputStream(File file)
/*     */     throws IOException, FileNotFoundException
/*     */   {
/* 196 */     BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
/*     */ 
/* 199 */     return reader;
/*     */   }
/*     */ 
/*     */   protected InputStream getInputStream(URL url)
/*     */     throws IOException
/*     */   {
/* 209 */     return url.openStream();
/*     */   }
/*     */ 
/*     */   protected void setMRU(EMSParameters emso)
/*     */   {
/* 216 */     int index = this._mruListnerList.indexOf(emso);
/*     */ 
/* 218 */     if (index == -1) {
/* 219 */       this._mruListnerList.add(0, emso);
/* 220 */       setMaxSize(this._maxSize);
/*     */     } else {
/* 222 */       moveToTop(index);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void load()
/*     */   {
/* 231 */     createConfigurationDirectory();
/* 232 */     File file = new File(getFilename());
/* 233 */     ObjectInputStream ois = null;
/* 234 */     if (file.exists())
/*     */       try {
/* 236 */         ois = new ObjectInputStream(new FileInputStream(file));
/*     */ 
/* 239 */         Object oList = ois.readObject();
/* 240 */         Collection coll = (Collection)oList;
/*     */ 
/* 243 */         Iterator it = coll.iterator();
/* 244 */         while (it.hasNext()) {
/* 245 */           Object oItem = it.next();
/* 246 */           if ((oItem instanceof EMSParameters))
/* 247 */             this._mruListnerList.add((EMSParameters)oItem);
/*     */         }
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 252 */         this._mruListnerList = new LinkedList();
/*     */       } finally {
/* 254 */         if (ois != null)
/*     */           try {
/* 256 */             ois.close();
/*     */           }
/*     */           catch (IOException e1) {
/*     */           }
/*     */       }
/*     */     else
/* 262 */       this._mruListnerList = new LinkedList();
/*     */   }
/*     */ 
/*     */   protected String getFilename()
/*     */   {
/* 268 */     String home = System.getProperty("user.home");
/* 269 */     String sep = System.getProperty("file.separator");
/*     */ 
/* 271 */     return home + sep + ".emssnoop" + sep + "mru_listner_manager";
/*     */   }
/*     */ 
/*     */   protected void setMaxSize(int maxSize)
/*     */   {
/* 278 */     if (maxSize < this._mruListnerList.size()) {
/* 279 */       for (int i = 0; i < this._mruListnerList.size() - maxSize; i++) {
/* 280 */         this._mruListnerList.removeLast();
/*     */       }
/*     */     }
/*     */ 
/* 284 */     this._maxSize = maxSize;
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.configure.MRUListnerManager
 * JD-Core Version:    0.6.1
 */
