/*     */ package emssn00p;
/*     */ 
/*     */ import emssn00p.util.ems.IMarshalJMSToString;
/*     */ import java.io.Serializable;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.Message;
/*     */ 
/*     */ public class LogRecord
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 3257003254792337720L;
/*  43 */   protected static long _seqCount = 0L;
/*     */   protected EventActionType _type;
/*     */   protected long _sequenceNumber;
/*     */   protected long _millis;
/*  51 */   protected StringBuffer _sJMSDestination = new StringBuffer();
/*     */   protected Message _message;
/*  53 */   protected String _JMSReplyTo = new String();
/*     */ 
/*  55 */   protected long _msgSeq = 0L;
/*  56 */   protected String _targetDestType = new String();
/*  57 */   protected String _targetName = new String();
/*  58 */   protected long _msgTimeStamp = 0L;
/*  59 */   protected String _eventAction = new String();
/*  60 */   protected String _connType = new String();
/*  61 */   protected String _eventClass = new String();
/*  62 */   protected String _targetObject = new String();
/*  63 */   protected String _connUserName = new String();
/*  64 */   protected long _connConnID = 0L;
/*  65 */   protected String _connHostName = new String();
/*  66 */   protected String _eventReason = new String();
/*  67 */   protected String _server = new String();
/*  68 */   protected String _JMSMessageID = new String();
/*  69 */   protected String _JMSCorrelationID = new String();
/*     */ 
/* 279 */   protected String _JMSDeliveryMode = new String();
/* 280 */   protected String _JMSPriority = new String();
/* 281 */   protected long _JMSExpiration = 0L;
/* 282 */   protected long _JMSTimeStamp = 0L;
/*     */   private static final int MAX_FREE_POOL_SIZE = 2000;
/* 287 */   private static LogRecord[] _freeStack = new LogRecord[2000];
/* 288 */   private static int _countFree = 0;
/* 289 */   private static long lastAllocationTime = System.currentTimeMillis() - 3000L;
/*     */ 
/*     */   public long getConnConnID()
/*     */   {
/*  74 */     return this._connConnID;
/*     */   }
/*     */ 
/*     */   public void setConnConnID(long connID)
/*     */   {
/*  80 */     this._connConnID = connID;
/*     */   }
/*     */ 
/*     */   public String getConnHostName()
/*     */   {
/*  86 */     return this._connHostName;
/*     */   }
/*     */ 
/*     */   public void setConnHostName(String hostName)
/*     */   {
/*  92 */     this._connHostName = hostName;
/*     */   }
/*     */ 
/*     */   public String getConnType()
/*     */   {
/*  98 */     return this._connType;
/*     */   }
/*     */ 
/*     */   public void setConnType(String type)
/*     */   {
/* 104 */     this._connType = type;
/*     */   }
/*     */ 
/*     */   public String getConnUserName()
/*     */   {
/* 110 */     return this._connUserName;
/*     */   }
/*     */ 
/*     */   public void setConnUserName(String userName)
/*     */   {
/* 116 */     this._connUserName = userName;
/*     */   }
/*     */ 
/*     */   public String getEventClass()
/*     */   {
/* 123 */     return this._eventClass;
/*     */   }
/*     */ 
/*     */   public void setEventClass(String class1)
/*     */   {
/* 129 */     this._eventClass = class1;
/*     */   }
/*     */ 
/*     */   public String getEventReason()
/*     */   {
/* 135 */     return this._eventReason;
/*     */   }
/*     */ 
/*     */   public void setEventReason(String reason)
/*     */   {
/* 141 */     this._eventReason = reason;
/*     */   }
/*     */ 
/*     */   public String getJMSCorrelationID()
/*     */   {
/* 148 */     return this._JMSCorrelationID;
/*     */   }
/*     */ 
/*     */   public void setJMSCorrelationID(String correlationID)
/*     */   {
/* 154 */     this._JMSCorrelationID = correlationID;
/*     */   }
/*     */ 
/*     */   public String getJMSDeliveryMode()
/*     */   {
/* 160 */     return this._JMSDeliveryMode;
/*     */   }
/*     */ 
/*     */   public void setJMSDeliveryMode(String deliveryMode)
/*     */   {
/* 166 */     this._JMSDeliveryMode = deliveryMode;
/*     */   }
/*     */ 
/*     */   public long getJMSExpiration()
/*     */   {
/* 172 */     return this._JMSExpiration;
/*     */   }
/*     */ 
/*     */   public void setJMSExpiration(long expiration)
/*     */   {
/* 178 */     this._JMSExpiration = expiration;
/*     */   }
/*     */ 
/*     */   public String getJMSMessageID()
/*     */   {
/* 184 */     return this._JMSMessageID;
/*     */   }
/*     */ 
/*     */   public void setJMSMessageID(String messageID)
/*     */   {
/* 189 */     this._JMSMessageID = messageID;
/*     */   }
/*     */ 
/*     */   public String getJMSPriority()
/*     */   {
/* 195 */     return this._JMSPriority;
/*     */   }
/*     */ 
/*     */   public void setJMSPriority(String priority)
/*     */   {
/* 201 */     this._JMSPriority = priority;
/*     */   }
/*     */ 
/*     */   public long getJMSTimeStamp()
/*     */   {
/* 207 */     return this._JMSTimeStamp;
/*     */   }
/*     */ 
/*     */   public void setJMSTimeStamp(long timeStamp)
/*     */   {
/* 213 */     this._JMSTimeStamp = timeStamp;
/*     */   }
/*     */ 
/*     */   public long getMsgTimeStamp()
/*     */   {
/* 220 */     return this._msgTimeStamp;
/*     */   }
/*     */ 
/*     */   public void setMsgTimeStamp(long timeStamp)
/*     */   {
/* 226 */     this._msgTimeStamp = timeStamp;
/*     */   }
/*     */ 
/*     */   public String getServer()
/*     */   {
/* 233 */     return this._server;
/*     */   }
/*     */ 
/*     */   public void setServer(String _server)
/*     */   {
/* 239 */     this._server = _server;
/*     */   }
/*     */ 
/*     */   public String getTargetDestType()
/*     */   {
/* 245 */     return this._targetDestType;
/*     */   }
/*     */ 
/*     */   public void setTargetDestType(String destType)
/*     */   {
/* 251 */     this._targetDestType = destType;
/*     */   }
/*     */ 
/*     */   public String getTargetName()
/*     */   {
/* 257 */     return this._targetName;
/*     */   }
/*     */ 
/*     */   public void setTargetName(String name)
/*     */   {
/* 263 */     this._targetName = name;
/*     */   }
/*     */ 
/*     */   public String getTargetObject()
/*     */   {
/* 269 */     return this._targetObject;
/*     */   }
/*     */ 
/*     */   public void setTargetObject(String object)
/*     */   {
/* 275 */     this._targetObject = object;
/*     */   }
/*     */ 
/*     */   public static synchronized LogRecord getInstance()
/*     */   {
/* 308 */     if (_countFree == 0)
/*     */     {
/* 310 */       long timeSinceLastAlloc = System.currentTimeMillis() - lastAllocationTime;
/*     */ 
/* 312 */       int noToAlloc = 0;
/* 313 */       if (timeSinceLastAlloc < 200L)
/* 314 */         noToAlloc = 2000;
/* 315 */       else if (timeSinceLastAlloc < 1000L)
/* 316 */         noToAlloc = 1000;
/* 317 */       else if (timeSinceLastAlloc < 3000L)
/* 318 */         noToAlloc = 500;
/*     */       else {
/* 320 */         noToAlloc = 100;
/*     */       }
/*     */ 
/* 324 */       for (int i = 0; i < noToAlloc; i++) {
/* 325 */         freeInstance(new LogRecord());
/*     */       }
/*     */ 
/* 328 */       lastAllocationTime = System.currentTimeMillis();
/*     */     }
/*     */ 
/* 332 */     LogRecord result = _freeStack[(--_countFree)];
/*     */ 
/* 335 */     result._millis = System.currentTimeMillis();
/* 336 */     result._type = EventActionType.UNKNOWN;
/* 337 */     result._sequenceNumber = getNextId();
/* 338 */     return result;
/*     */   }
/*     */ 
/*     */   public static synchronized void freeInstance(LogRecord lr)
/*     */   {
/* 347 */     if (_countFree < 2000)
/* 348 */       _freeStack[(_countFree++)] = lr;
/*     */   }
/*     */ 
/*     */   public EventActionType getType()
/*     */   {
/* 360 */     return this._type;
/*     */   }
/*     */ 
/*     */   public void setType(EventActionType type)
/*     */   {
/* 370 */     this._type = type;
/*     */   }
/*     */ 
/*     */   public boolean isSevereType()
/*     */   {
/* 382 */     boolean isSevere = false;
/*     */ 
/* 384 */     if ((EventActionType.RECEIVE.equals(getType())) || (EventActionType.RECEIVE.equals(getType())))
/*     */     {
/* 386 */       isSevere = true;
/*     */     }
/*     */ 
/* 389 */     return isSevere;
/*     */   }
/*     */ 
/*     */   public boolean isFatal()
/*     */   {
/* 397 */     return isSevereType();
/*     */   }
/*     */ 
/*     */   public String getJMSDestination()
/*     */   {
/* 407 */     return this._sJMSDestination.toString();
/*     */   }
/*     */ 
/*     */   public StringBuffer getJMSDestinationAsStringBuffer() {
/* 411 */     return this._sJMSDestination;
/*     */   }
/*     */ 
/*     */   public void setJMSDestination(String subject)
/*     */   {
/* 423 */     this._sJMSDestination.setLength(0);
/* 424 */     this._sJMSDestination.append(subject);
/*     */   }
/*     */ 
/*     */   public Message getMessage()
/*     */   {
/* 434 */     return this._message;
/*     */   }
/*     */ 
/*     */   public StringBuffer getMessageAsStringBuffer() {
/* 438 */     return new StringBuffer(this._message.toString());
/*     */   }
/*     */ 
/*     */   public void setMessage(Message message)
/*     */   {
/* 448 */     this._message = message;
/*     */   }
/*     */ 
/*     */   public long getSequenceNumber()
/*     */   {
/* 461 */     return this._sequenceNumber;
/*     */   }
/*     */ 
/*     */   public void setSequenceNumber(long number)
/*     */   {
/* 473 */     this._sequenceNumber = number;
/*     */   }
/*     */ 
/*     */   public long getMillis()
/*     */   {
/* 485 */     return this._millis;
/*     */   }
/*     */ 
/*     */   public void setMillis(long millis)
/*     */   {
/* 496 */     this._millis = millis;
/*     */   }
/*     */ 
/*     */   public String toString(IMarshalJMSToString marshaller)
/*     */   {
/* 505 */     StringBuffer buf = new StringBuffer();
/* 506 */     buf.append("LogRecord: [");
/* 507 */     buf.append(this._type);
/* 508 */     buf.append(",");
/*     */     try {
/* 510 */       buf.append(marshaller.JMSMsgToString(this._message, ""));
/*     */     }
/*     */     catch (JMSException e) {
/* 513 */       buf.append(e.getMessage());
/*     */     }
/* 515 */     buf.append("]");
/* 516 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public void setJMSReplyTo(String replysub)
/*     */   {
/* 526 */     this._JMSReplyTo = replysub;
/*     */   }
/*     */ 
/*     */   public static synchronized void resetSequenceNumber()
/*     */   {
/* 534 */     _seqCount = 0L;
/*     */   }
/*     */ 
/*     */   protected static synchronized long getNextId()
/*     */   {
/* 542 */     _seqCount += 1L;
/* 543 */     return _seqCount;
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.LogRecord
 * JD-Core Version:    0.6.1
 */