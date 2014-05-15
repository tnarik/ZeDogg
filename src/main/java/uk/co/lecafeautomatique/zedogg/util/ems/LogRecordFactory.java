/*     */ package uk.co.lecafeautomatique.zedogg.util.ems;
/*     */ 
/*     */ // WIP import com.tibco.tibjms.Tibjms;
/*     */ import uk.co.lecafeautomatique.zedogg.EventActionType;
/*     */ import uk.co.lecafeautomatique.zedogg.LogRecord;
/*     */ import javax.jms.Destination;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.MapMessage;
/*     */ import javax.jms.Message;
/*     */ import javax.jms.Queue;
/*     */ import javax.jms.Topic;
/*     */ import javax.swing.JLabel;
/*     */ 
/*     */ public class LogRecordFactory
/*     */ {
/*     */   public static LogRecord createLogRecordFromJMSMessage(JLabel _statusLabel, Message msg)
/*     */   {
/*  25 */     LogRecord r = LogRecord.getInstance();
/*     */     String strJMSDestination;
/*     */     try
/*     */     {
/*  29 */       Destination dest = msg.getJMSDestination();
/*  30 */       if (Topic.class.isInstance(dest)) {
/*  31 */         strJMSDestination = ((Topic)dest).getTopicName();
/*     */       }
/*     */       else
/*     */       {
/*  33 */         if (Queue.class.isInstance(dest)) {
/*  34 */           strJMSDestination = ((Queue)dest).getQueueName();
/*     */         }
/*     */         else
/*  37 */           strJMSDestination = null;
/*     */       }
/*     */     } catch (JMSException e1) {
/*  40 */       strJMSDestination = null;
/*  41 */       _statusLabel.setText(e1.getMessage());
/*     */     }
/*     */ 
/*  46 */     if (strJMSDestination != null) {
/*  47 */       if (strJMSDestination.startsWith("$sys.monitor."))
/*  48 */         createLogRecordMonitor(r, _statusLabel, strJMSDestination, msg);
/*     */       else {
/*  50 */         createLogRecordNormal(r, _statusLabel, strJMSDestination, msg);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  55 */     return r;
/*     */   }
/*     */ 
/*     */   private static void createLogRecordNormal(LogRecord r, JLabel _statusLabel, String strJMSDestination, Message msg)
/*     */   {
/*  65 */     setJMSParameters(r, msg);
/*     */     try
/*     */     {
/*  68 */       r.setMessage(msg);
/*     */     } catch (Exception ex) {
/*  70 */       ex.printStackTrace();
/*  71 */       _statusLabel.setText(ex.getMessage());
/*     */     }
/*     */     try
/*     */     {
/*  75 */       r.setJMSCorrelationID(msg.getJMSCorrelationID());
/*     */     }
/*     */     catch (JMSException e) {
/*     */     }
/*  79 */     r.setJMSDestination(strJMSDestination);
/*     */     try
/*     */     {
/*     */       String strJMSReplyTo;
/*  83 */       if (Topic.class.isInstance(msg.getJMSReplyTo())) {
/*  84 */         strJMSReplyTo = ((Topic)msg.getJMSReplyTo()).getTopicName();
/*     */       }
/*     */       else
/*     */       {
/*  85 */         if (Queue.class.isInstance(msg.getJMSReplyTo()))
/*  86 */           strJMSReplyTo = ((Queue)msg.getJMSReplyTo()).getQueueName();
/*     */         else {
/*  88 */           strJMSReplyTo = null;
/*     */         }
/*     */       }
/*  91 */       r.setJMSReplyTo(strJMSReplyTo);
/*     */     }
/*     */     catch (JMSException e2) {
/*  94 */       _statusLabel.setText(e2.getMessage());
/*     */     }
/*     */ 
/*  97 */     r.setType(EventActionType.UNKNOWN);
/*     */   }
/*     */ 
/*     */   private static void createLogRecordMonitor(LogRecord r, JLabel _statusLabel, String strJMSDestination, Message msg)
/*     */   {
/* 111 */     MapMessage mapMsg = (MapMessage)msg;
/* 112 */     Message realMsg = null;
/*     */     try
/*     */     {
/* 115 */       if (mapMsg.itemExists("message_bytes")) {
/* 116 */         realMsg = null; // WIP Tibjms.createFromBytes(mapMsg.getBytes("message_bytes"));
/*     */       } else {
/* 118 */         createLogRecordNormal(r, _statusLabel, strJMSDestination, msg);
/* 119 */         return;
/*     */       }
/*     */     } catch (JMSException e1) {
/* 122 */       _statusLabel.setText(e1.getMessage());
/* 123 */       e1.printStackTrace();
/* 124 */       return;
/*     */     }
/*     */ 
/* 128 */     setJMSParameters(r, realMsg);
/* 129 */     setMonitoringParameters(r, mapMsg);
/*     */     try
/*     */     {
/* 133 */       if (strJMSDestination != null) {
/* 134 */         int ix = strJMSDestination.indexOf(".") + 1;
/* 135 */         ix = strJMSDestination.indexOf(".", ix) + 1;
/* 136 */         ix = strJMSDestination.indexOf(".", ix) + 1;
/* 137 */         ix = strJMSDestination.indexOf(".", ix) + 1;
/*     */ 
/* 139 */         strJMSDestination = strJMSDestination.substring(ix, strJMSDestination.length());
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 144 */       _statusLabel.setText(e.getMessage());
/*     */     }
/*     */ 
/* 148 */     r.setJMSDestination(strJMSDestination);
/*     */     try
/*     */     {
/* 151 */       r.setMessage(realMsg);
/*     */     } catch (Exception ex) {
/* 153 */       ex.printStackTrace();
/* 154 */       _statusLabel.setText(ex.getMessage());
/*     */     }
/*     */     try
/*     */     {
/* 158 */       r.setJMSCorrelationID(realMsg.getJMSCorrelationID());
/*     */     }
/*     */     catch (JMSException e)
/*     */     {
/*     */     }
/*     */     try
/*     */     {
/*     */       String strJMSReplyTo;
/* 164 */       if (Topic.class.isInstance(realMsg.getJMSReplyTo())) {
/* 165 */         strJMSReplyTo = ((Topic)realMsg.getJMSReplyTo()).getTopicName();
/*     */       }
/*     */       else
/*     */       {
/* 166 */         if (Queue.class.isInstance(msg.getJMSReplyTo()))
/* 167 */           strJMSReplyTo = ((Queue)realMsg.getJMSReplyTo()).getQueueName();
/*     */         else {
/* 169 */           strJMSReplyTo = null;
/*     */         }
/*     */       }
/* 172 */       r.setJMSReplyTo(strJMSReplyTo);
/*     */     }
/*     */     catch (JMSException e2) {
/* 175 */       _statusLabel.setText(e2.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void setMonitoringParameters(LogRecord r, MapMessage msg)
/*     */   {
/*     */     try
/*     */     {
/* 188 */       r.setEventReason(msg.getStringProperty("event_reason"));
/*     */     } catch (JMSException e) {
/* 190 */       e.printStackTrace();
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 195 */       r.setTargetDestType(msg.getStringProperty("target_dest_type"));
/*     */     } catch (JMSException e) {
/* 197 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 201 */       r.setTargetName(msg.getStringProperty("target_name"));
/*     */     } catch (JMSException e) {
/* 203 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 207 */       String sEventAction = msg.getStringProperty("event_action");
/* 208 */       if (sEventAction.compareTo("receive") == 0)
/* 209 */         r.setType(EventActionType.RECEIVE);
/* 210 */       else if (sEventAction.compareTo("acknowledge") == 0)
/* 211 */         r.setType(EventActionType.ACKNOWLEDGE);
/* 212 */       else if (sEventAction.compareTo("send") == 0)
/* 213 */         r.setType(EventActionType.SEND);
/*     */     }
/*     */     catch (JMSException e)
/*     */     {
/* 217 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 221 */       r.setConnType(msg.getStringProperty("conn_type"));
/*     */     } catch (JMSException e) {
/* 223 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 227 */       r.setEventClass(msg.getStringProperty("event_class"));
/*     */     } catch (JMSException e) {
/* 229 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 233 */       r.setTargetObject(msg.getStringProperty("target_object"));
/*     */     } catch (JMSException e) {
/* 235 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 239 */       r.setConnUserName(msg.getStringProperty("conn_username"));
/*     */     } catch (JMSException e) {
/* 241 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 245 */       r.setEventReason(msg.getStringProperty("event_reason"));
/*     */     } catch (JMSException e) {
/* 247 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 251 */       r.setConnHostName(msg.getStringProperty("conn_hostname"));
/*     */     } catch (JMSException e) {
/* 253 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 257 */       r.setServer(msg.getStringProperty("server"));
/*     */     } catch (JMSException e) {
/* 259 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void setJMSParameters(LogRecord r, Message msg)
/*     */   {
/*     */     try
/*     */     {
/* 272 */       r.setJMSMessageID(msg.getJMSMessageID());
/*     */     } catch (JMSException e3) {
/* 274 */       e3.printStackTrace();
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 279 */       String strJMSCorrelationID = msg.getJMSCorrelationID();
/*     */     }
/*     */     catch (JMSException e4)
/*     */     {
/*     */       String strJMSCorrelationID;
/* 281 */       e4.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 285 */       String strJMSDeliveryMode = String.valueOf(msg.getJMSDeliveryMode());
/*     */     }
/*     */     catch (JMSException e5)
/*     */     {
/*     */       String strJMSDeliveryMode;
/* 287 */       e5.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 291 */       String strJMSPriority = String.valueOf(msg.getJMSPriority());
/*     */     }
/*     */     catch (JMSException e6)
/*     */     {
/*     */       String strJMSPriority;
/* 293 */       e6.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 297 */       String strJMSType = msg.getJMSType() == null ? null : msg.getJMSType().toString();
/*     */     }
/*     */     catch (JMSException e7)
/*     */     {
/*     */       String strJMSType;
/* 300 */       e7.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 304 */       String strJMSExpiration = msg.getJMSExpiration() == 0L ? null : String.valueOf(msg.getJMSExpiration());
/*     */     }
/*     */     catch (JMSException e8)
/*     */     {
/*     */       String strJMSExpiration;
/* 307 */       e8.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 311 */       String strJMSTimestamp = msg.getJMSTimestamp() == 0L ? null : String.valueOf(msg.getJMSTimestamp());
/*     */     }
/*     */     catch (JMSException e9)
/*     */     {
/*     */       String strJMSTimestamp;
/* 314 */       e9.printStackTrace();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.util.ems.LogRecordFactory
 * JD-Core Version:    0.6.1
 */
