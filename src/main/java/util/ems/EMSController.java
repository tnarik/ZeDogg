/*     */ package emssn00p.util.ems;
/*     */ 
/*     */ //WIP import com.tibco.tibjms.TibjmsTopicConnectionFactory;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.MessageListener;
/*     */ import javax.jms.TopicConnection;
/*     */ import javax.jms.TopicConnectionFactory;
/*     */ import javax.jms.TopicSession;
/*     */ import javax.jms.TopicSubscriber;
/*     */ 
/*     */ public class EMSController
/*     */ {
/*  32 */   protected static final Map<EMSParameters, TopicConnection> _mapEMSConnections = new HashMap();
/*     */ 
/*     */   public static synchronized TopicConnection getTopicConnection(EMSParameters p)
/*     */     throws JMSException
/*     */   {
/*     */     try
/*     */     {
/*  61 */       if (_mapEMSConnections.containsKey(p)) {
/*  62 */         return (TopicConnection)_mapEMSConnections.get(p);
/*     */       }
/*     */ 
/*  65 */       TopicConnectionFactory factory = null; // WIP new TibjmsTopicConnectionFactory(p.getServerURL());
/*     */ 
/*  67 */       return factory.createTopicConnection(p.getUserName(), p.getPassword());
/*     */     }
/*     */     catch (JMSException ex)
/*     */     {
/*  74 */       throw ex;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static synchronized void startListener(EMSParameters p, MessageListener callback)
/*     */     throws JMSException
/*     */   {
/*  82 */     if (_mapEMSConnections.containsKey(p))
/*     */     {
/*  84 */       Iterator i = _mapEMSConnections.keySet().iterator();
/*     */ 
/*  86 */       while (i.hasNext())
/*     */       {
/*  88 */         EMSParameters par = (EMSParameters)i.next();
/*  89 */         if (par.equals(p)) {
/*  90 */           par.setTopics(p.getTopics());
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  95 */     Set s = p.getTopics();
/*  96 */     Iterator i = s.iterator();
/*  97 */     while (i.hasNext()) {
/*  98 */       String n = (String)i.next();
/*  99 */       String id = String.valueOf(getTopicConnection(p).hashCode() + n);
/*     */ 
/* 101 */       if (!_mapEMSConnections.containsKey(id))
/*     */       {
/* 104 */         TopicConnection connection = getTopicConnection(p);
/*     */ 
/* 107 */         TopicSession session = connection.createTopicSession(false, 1);
/*     */ 
/* 109 */         TopicSubscriber subscriber = session.createSubscriber(session.createTopic(n));
/* 110 */         subscriber.setMessageListener(callback);
/* 111 */         connection.start();
/*     */ 
/* 114 */         _mapEMSConnections.put(p, connection);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static synchronized Set getListeners() {
/* 120 */     return _mapEMSConnections.keySet();
/*     */   }
/*     */ 
/*     */   public static synchronized Set getTransports() {
/* 124 */     return _mapEMSConnections.keySet();
/*     */   }
/*     */ 
/*     */   public static synchronized void stopListener(EMSParameters p) throws JMSException
/*     */   {
/* 129 */     if (_mapEMSConnections.containsKey(p))
/*     */     {
/* 131 */       TopicSubscriber lsnr = (TopicSubscriber)_mapEMSConnections.get(p);
/* 132 */       lsnr.close();
/*     */ 
/* 135 */       _mapEMSConnections.remove(p.getTopic());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static synchronized void shutdownAll() throws JMSException
/*     */   {
/* 141 */     Iterator i = _mapEMSConnections.values().iterator();
/*     */ 
/* 143 */     while (i.hasNext()) {
/* 144 */       if (i != null) {
/* 145 */         TopicConnection tc = (TopicConnection)i.next();
/*     */ 
/* 147 */         tc.stop();
/* 148 */         tc.close();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 156 */     _mapEMSConnections.clear();
/*     */   }
/*     */ 
/*     */   public static synchronized void pauseAll()
/*     */     throws JMSException
/*     */   {
/* 162 */     Iterator i = _mapEMSConnections.values().iterator();
/*     */ 
/* 164 */     while (i.hasNext())
/* 165 */       if (i != null) {
/* 166 */         TopicConnection tc = (TopicConnection)i.next();
/*     */ 
/* 168 */         tc.stop();
/*     */       }
/*     */   }
/*     */ 
/*     */   public static synchronized void resumeAll()
/*     */     throws JMSException
/*     */   {
/* 179 */     Iterator i = _mapEMSConnections.values().iterator();
/*     */ 
/* 181 */     while (i.hasNext())
/* 182 */       if (i != null)
/* 183 */         ((TopicConnection)i.next()).start();
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.util.ems.EMSController
 * JD-Core Version:    0.6.1
 */