/*     */ package uk.co.lecafeautomatique.zedogg;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class EventActionType
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 3977580307551565621L;
/*  44 */   public static final EventActionType RECEIVE = new EventActionType("RECEIVE", 0);
/*  45 */   public static final EventActionType SEND = new EventActionType("SEND", 1);
/*  46 */   public static final EventActionType ACKNOWLEDGE = new EventActionType("ACKNOWLEDGE", 2);
/*  47 */   public static final EventActionType UNKNOWN = new EventActionType("UNKNOWN", 3);
/*     */ 
/*  49 */   public static final EventActionType CONNECT = new EventActionType("CONNECT", 4);
/*  50 */   public static final EventActionType ACCEPT = new EventActionType("ACCEPT", 5);
/*  51 */   public static final EventActionType DISCONNECT = new EventActionType("DISCONNECT", 6);
/*  52 */   public static final EventActionType INTEREST = new EventActionType("INTEREST", 7);
/*  53 */   public static final EventActionType CREATE = new EventActionType("CREATE", 8);
/*  54 */   public static final EventActionType DELETE = new EventActionType("DELETE", 9);
/*  55 */   public static final EventActionType MODIFY = new EventActionType("MODIFY", 10);
/*  56 */   public static final EventActionType ADD = new EventActionType("ADD", 11);
/*  57 */   public static final EventActionType REMOVE = new EventActionType("REMOVE", 12);
/*  58 */   public static final EventActionType GRANT = new EventActionType("GRANT", 13);
/*  59 */   public static final EventActionType REVOKE = new EventActionType("REVOKE", 14);
/*  60 */   public static final EventActionType PURGE = new EventActionType("PURGE", 15);
/*  61 */   public static final EventActionType COMMIT = new EventActionType("COMMIT", 16);
/*  62 */   public static final EventActionType ROLLBACK = new EventActionType("ROLLBACK", 17);
/*  63 */   public static final EventActionType ROTATELOG = new EventActionType("ROTATELOG", 18);
/*     */   protected String _label;
/*     */   protected int _precedence;
/*     */   private static EventActionType[] _allDefaultLevels;
/*     */   private static Map<String, EventActionType> _eventActionTypeMap;
/*     */   private static Map<EventActionType, Color> _EventActionTypeColorMap;
/*  77 */   private static Map<String, EventActionType> _registeredEventActionTypeMap = new HashMap();
/*     */ 
/*     */   public EventActionType(String label, int precedence)
/*     */   {
/*  99 */     this._label = label;
/* 100 */     this._precedence = precedence;
/*     */   }
/*     */ 
/*     */   public String getLabel()
/*     */   {
/* 111 */     return this._label;
/*     */   }
/*     */ 
/*     */   public boolean encompasses(EventActionType level)
/*     */   {
/* 121 */     if (level.getPrecedence() <= getPrecedence()) {
/* 122 */       return true;
/*     */     }
/*     */ 
/* 125 */     return false;
/*     */   }
/*     */ 
/*     */   public static EventActionType valueOf(String level)
/*     */     throws MsgTypeFormatException
/*     */   {
/* 138 */     EventActionType logLevel = null;
/* 139 */     if (level != null) {
/* 140 */       level = level.trim().toUpperCase();
/* 141 */       logLevel = (EventActionType)_eventActionTypeMap.get(level);
/*     */     }
/*     */ 
/* 145 */     if ((logLevel == null) && (_registeredEventActionTypeMap.size() > 0)) {
/* 146 */       logLevel = (EventActionType)_registeredEventActionTypeMap.get(level);
/*     */     }
/*     */ 
/* 149 */     if (logLevel == null) {
/* 150 */       StringBuffer buf = new StringBuffer();
/* 151 */       buf.append("Error while trying to parse (" + level + ") into");
/* 152 */       buf.append(" a MsgType.");
/* 153 */       throw new MsgTypeFormatException(buf.toString());
/*     */     }
/* 155 */     return logLevel;
/*     */   }
/*     */ 
/*     */   public static EventActionType register(EventActionType logLevel)
/*     */   {
/* 165 */     if (logLevel == null) return null;
/*     */ 
/* 168 */     if (_eventActionTypeMap.get(logLevel.getLabel()) == null) {
/* 169 */       return (EventActionType)_registeredEventActionTypeMap.put(logLevel.getLabel(), logLevel);
/*     */     }
/*     */ 
/* 172 */     return null;
/*     */   }
/*     */ 
/*     */   public static void register(EventActionType[] MsgTypes) {
/* 176 */     if (MsgTypes != null)
/* 177 */       for (int i = 0; i < MsgTypes.length; i++)
/* 178 */         register(MsgTypes[i]);
/*     */   }
/*     */ 
/*     */   public static void register(List MsgTypes)
/*     */   {
/* 184 */     if (MsgTypes != null) {
/* 185 */       Iterator it = MsgTypes.iterator();
/* 186 */       while (it.hasNext())
/* 187 */         register((EventActionType)it.next());
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 193 */     boolean equals = false;
/*     */ 
/* 195 */     if (((o instanceof EventActionType)) && 
/* 196 */       (getPrecedence() == ((EventActionType)o).getPrecedence()))
/*     */     {
/* 198 */       equals = true;
/*     */     }
/*     */ 
/* 203 */     return equals;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 207 */     return this._label.hashCode();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 211 */     return this._label;
/*     */   }
/*     */ 
/*     */   public void setLogLevelColorMap(EventActionType level, Color color)
/*     */   {
/* 217 */     _EventActionTypeColorMap.remove(level);
/*     */ 
/* 219 */     if (color == null) {
/* 220 */       color = Color.BLACK;
/*     */     }
/* 222 */     _EventActionTypeColorMap.put(level, color);
/*     */   }
/*     */ 
/*     */   public static void resetLogLevelColorMap()
/*     */   {
/* 227 */     _EventActionTypeColorMap.clear();
/*     */ 
/* 230 */     for (int i = 0; i < _allDefaultLevels.length; i++)
/* 231 */       _EventActionTypeColorMap.put(_allDefaultLevels[i], Color.black);
/*     */   }
/*     */ 
/*     */   public static List getAllDefaultLevels()
/*     */   {
/* 237 */     return Arrays.asList(_allDefaultLevels);
/*     */   }
/*     */ 
/*     */   public static Map getLogLevelColorMap() {
/* 241 */     return _EventActionTypeColorMap;
/*     */   }
/*     */ 
/*     */   protected int getPrecedence()
/*     */   {
/* 249 */     return this._precedence;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  84 */     _allDefaultLevels = new EventActionType[] { RECEIVE, SEND, ACKNOWLEDGE, UNKNOWN, CONNECT, ACCEPT, DISCONNECT, INTEREST, CREATE, DELETE, MODIFY, ADD, REMOVE, GRANT, REVOKE, PURGE, COMMIT, ROLLBACK, ROTATELOG };
/*     */ 
/*  86 */     _eventActionTypeMap = new HashMap();
/*  87 */     for (int i = 0; i < _allDefaultLevels.length; i++) {
/*  88 */       _eventActionTypeMap.put(_allDefaultLevels[i].getLabel(), _allDefaultLevels[i]);
/*     */     }
/*     */ 
/*  92 */     _EventActionTypeColorMap = new HashMap();
/*  93 */     for (int i = 0; i < _allDefaultLevels.length; i++)
/*  94 */       _EventActionTypeColorMap.put(_allDefaultLevels[i], Color.BLACK);
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.EventActionType
 * JD-Core Version:    0.6.1
 */
