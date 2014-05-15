/*     */ package emssn00p.util;
/*     */ 
/*     */ import java.text.DateFormat;
/*     */ import java.text.FieldPosition;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class DateFormatManager
/*     */ {
/*  69 */   private TimeZone _timeZone = null;
/*  70 */   private Locale _locale = null;
/*     */ 
/*  72 */   private String _pattern = null;
/*  73 */   private static final DateFormat _dateFormat = SimpleDateFormat.getDateTimeInstance(0, 0, Locale.getDefault());
/*     */ 
/*  76 */   private static final FieldPosition _fieldPosition = new FieldPosition(0);
/*     */ 
/*     */   public DateFormatManager()
/*     */   {
/*  84 */     configure();
/*     */   }
/*     */ 
/*     */   public DateFormatManager(TimeZone timeZone)
/*     */   {
/*  90 */     this._timeZone = timeZone;
/*  91 */     configure();
/*     */   }
/*     */ 
/*     */   public DateFormatManager(Locale locale)
/*     */   {
/*  97 */     this._locale = locale;
/*  98 */     configure();
/*     */   }
/*     */ 
/*     */   public DateFormatManager(String pattern)
/*     */   {
/* 104 */     this._pattern = pattern;
/* 105 */     configure();
/*     */   }
/*     */ 
/*     */   public DateFormatManager(TimeZone timeZone, Locale locale)
/*     */   {
/* 111 */     this._timeZone = timeZone;
/* 112 */     this._locale = locale;
/* 113 */     configure();
/*     */   }
/*     */ 
/*     */   public DateFormatManager(TimeZone timeZone, String pattern)
/*     */   {
/* 119 */     this._timeZone = timeZone;
/* 120 */     this._pattern = pattern;
/* 121 */     configure();
/*     */   }
/*     */ 
/*     */   public DateFormatManager(Locale locale, String pattern)
/*     */   {
/* 127 */     this._locale = locale;
/* 128 */     this._pattern = pattern;
/* 129 */     configure();
/*     */   }
/*     */ 
/*     */   public DateFormatManager(TimeZone timeZone, Locale locale, String pattern)
/*     */   {
/* 135 */     this._timeZone = timeZone;
/* 136 */     this._locale = locale;
/* 137 */     this._pattern = pattern;
/* 138 */     configure();
/*     */   }
/*     */ 
/*     */   public synchronized TimeZone getTimeZone()
/*     */   {
/* 146 */     if (this._timeZone == null) {
/* 147 */       return TimeZone.getDefault();
/*     */     }
/* 149 */     return this._timeZone;
/*     */   }
/*     */ 
/*     */   public synchronized void setTimeZone(TimeZone timeZone)
/*     */   {
/* 154 */     this._timeZone = timeZone;
/* 155 */     configure();
/*     */   }
/*     */ 
/*     */   public synchronized String getPattern()
/*     */   {
/* 172 */     return this._pattern;
/*     */   }
/*     */ 
/*     */   public synchronized void setPattern(String pattern)
/*     */   {
/* 179 */     this._pattern = pattern;
/* 180 */     configure();
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public synchronized String getOutputFormat()
/*     */   {
/* 189 */     return this._pattern;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public synchronized void setOutputFormat(String pattern)
/*     */   {
/* 197 */     this._pattern = pattern;
/* 198 */     configure();
/*     */   }
/*     */ 
/*     */   public synchronized DateFormat getDateFormatInstance() {
/* 202 */     return _dateFormat;
/*     */   }
/*     */ 
/*     */   public String format(Date date)
/*     */   {
/* 211 */     return getDateFormatInstance().format(date);
/*     */   }
/*     */ 
/*     */   public StringBuffer format(Date date, StringBuffer toAppendTo) {
/* 215 */     return getDateFormatInstance().format(date, toAppendTo, _fieldPosition);
/*     */   }
/*     */ 
/*     */   public String format(Date date, String pattern) {
/* 219 */     DateFormat formatter = null;
/* 220 */     formatter = getDateFormatInstance();
/* 221 */     if ((formatter instanceof SimpleDateFormat)) {
/* 222 */       formatter = (SimpleDateFormat)formatter.clone();
/* 223 */       ((SimpleDateFormat)formatter).applyPattern(pattern);
/*     */     }
/* 225 */     return formatter.format(date);
/*     */   }
/*     */ 
/*     */   public Date parse(String date)
/*     */     throws ParseException
/*     */   {
/* 232 */     return getDateFormatInstance().parse(date);
/*     */   }
/*     */ 
/*     */   public Date parse(String date, String pattern)
/*     */     throws ParseException
/*     */   {
/* 239 */     DateFormat formatter = null;
/* 240 */     formatter = getDateFormatInstance();
/* 241 */     if ((formatter instanceof SimpleDateFormat)) {
/* 242 */       formatter = (SimpleDateFormat)formatter.clone();
/* 243 */       ((SimpleDateFormat)formatter).applyPattern(pattern);
/*     */     }
/* 245 */     return formatter.parse(date);
/*     */   }
/*     */ 
/*     */   private synchronized void configure()
/*     */   {
/* 257 */     _dateFormat.setTimeZone(getTimeZone());
/*     */ 
/* 259 */     if (this._pattern != null)
/* 260 */       ((SimpleDateFormat)_dateFormat).applyPattern(this._pattern);
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.util.DateFormatManager
 * JD-Core Version:    0.6.1
 */