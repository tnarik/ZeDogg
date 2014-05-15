/*     */ package uk.co.lecafeautomatique.zedogg.util.ems;
/*     */ 
/*     */ import uk.co.lecafeautomatique.zedogg.util.Base64;
/*     */ import uk.co.lecafeautomatique.zedogg.util.HTMLEncoder;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.Enumeration;
/*     */ import javax.jms.BytesMessage;
/*     */ import javax.jms.Destination;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.MapMessage;
/*     */ import javax.jms.Message;
/*     */ import javax.jms.Queue;
/*     */ import javax.jms.TextMessage;
/*     */ import javax.jms.Topic;
/*     */ 
/*     */ public class MarshalJMSMsgToStringJMSStreamImpl
/*     */   implements IMarshalJMSToString
/*     */ {
/*  33 */   protected SimpleDateFormat _dfXML = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
/*     */ 
/*     */   public String JMSMsgToString(Message message, String name)
/*     */     throws JMSException
/*     */   {
/*  41 */     String strTextBuffer = "";
/*  42 */     StringBuffer strXmlBuffer = new StringBuffer();
/*  43 */     StringBuffer strHeader = new StringBuffer();
/*  44 */     String strCsvFields = "";
/*     */ 
/*  50 */     Destination destReplyTo = message.getJMSReplyTo();
/*     */     String strJMSReplyTo;
/*  51 */     if (Topic.class.isInstance(destReplyTo)) {
/*  52 */       strJMSReplyTo = ((Topic)destReplyTo).getTopicName();
/*     */     }
/*     */     else
/*     */     {
/*  54 */       if (Queue.class.isInstance(destReplyTo)) {
/*  55 */         strJMSReplyTo = ((Queue)destReplyTo).getQueueName();
/*     */       }
/*     */       else {
/*  58 */         strJMSReplyTo = null;
/*     */       }
/*     */     }
/*  61 */     String strJMSMessageID = message.getJMSMessageID();
/*  62 */     String strJMSDestination = message.getJMSDestination().toString().substring(6, message.getJMSDestination().toString().length() - 1);
/*  63 */     String strJMSCorrelationID = message.getJMSCorrelationID();
/*  64 */     String strJMSDeliveryMode = String.valueOf(message.getJMSDeliveryMode());
/*  65 */     String strJMSPriority = String.valueOf(message.getJMSPriority());
/*  66 */     String strJMSType = message.getJMSType() == null ? null : message.getJMSType().toString();
/*  67 */     String strJMSExpiration = message.getJMSExpiration() == 0L ? null : String.valueOf(message.getJMSExpiration());
/*  68 */     String strJMSTimestamp = message.getJMSTimestamp() == 0L ? null : String.valueOf(message.getJMSTimestamp());
/*  69 */     Enumeration map = message.getPropertyNames();
/*     */ 
/*  72 */     strXmlBuffer.append("<message type=\"");
/*  73 */     strXmlBuffer.append(message.getClass().getName().replaceFirst("com.tibco.tibjms.", ""));
/*     */ 
/*  75 */     strXmlBuffer.append(" originationTime=\"" + this._dfXML.format(new Date(message.getJMSTimestamp())));
/*  76 */     strXmlBuffer.append(" receiveTime=\"");
/*  77 */     strXmlBuffer.append(this._dfXML.format(new Date()));
/*  78 */     strXmlBuffer.append("\">\n");
/*     */ 
/*  80 */     strHeader.append("<header ");
/*  81 */     if ((strJMSMessageID != null) && (!strJMSMessageID.equals(""))) {
/*  82 */       strHeader.append("\n\tJMSMessageID=\"");
/*  83 */       strHeader.append(strJMSMessageID);
/*  84 */       strHeader.append("\" ");
/*     */     }
/*     */ 
/*  87 */     if ((strJMSDestination != null) && (!strJMSDestination.equals(""))) {
/*  88 */       strHeader.append("\n\tJMSDestination=\"");
/*  89 */       strHeader.append(strJMSDestination);
/*  90 */       strHeader.append("\" ");
/*     */     }
/*     */ 
/*  93 */     if ((strJMSReplyTo != null) && (!strJMSReplyTo.equals(""))) {
/*  94 */       strHeader.append("\n\tJMSReplyTo=\"");
/*  95 */       strHeader.append(strJMSReplyTo);
/*  96 */       strHeader.append("\" ");
/*     */     }
/*     */ 
/*  99 */     if ((strJMSCorrelationID != null) && (!strJMSCorrelationID.equals(""))) {
/* 100 */       strHeader.append("\n\tJMSCorrelationID=\"");
/* 101 */       strHeader.append(strJMSCorrelationID);
/* 102 */       strHeader.append("\" ");
/*     */     }
/*     */ 
/* 105 */     if ((strJMSDeliveryMode != null) && (!strJMSDeliveryMode.equals(""))) {
/* 106 */       strHeader.append("\n\tJMSDeliveryMode=\"");
/* 107 */       strHeader.append(strJMSDeliveryMode);
/* 108 */       strHeader.append("\" ");
/*     */     }
/*     */ 
/* 111 */     if ((strJMSPriority != null) && (!strJMSPriority.equals(""))) {
/* 112 */       strHeader.append("\n\tJMSPriority=\"");
/* 113 */       strHeader.append(strJMSPriority);
/* 114 */       strHeader.append("\" ");
/*     */     }
/* 116 */     if ((strJMSType != null) && (!strJMSType.equals(""))) {
/* 117 */       strHeader.append("\n\tJMSType=\"");
/* 118 */       strHeader.append(strJMSType);
/* 119 */       strHeader.append("\" ");
/*     */     }
/*     */ 
/* 122 */     if ((strJMSExpiration != null) && (!strJMSExpiration.equals(""))) {
/* 123 */       strHeader.append("\n\tJMSExpiration=\"");
/* 124 */       strHeader.append(strJMSExpiration);
/* 125 */       strHeader.append("\" ");
/*     */     }
/* 127 */     if ((strJMSTimestamp != null) && (!strJMSTimestamp.equals(""))) {
/* 128 */       strHeader.append("\n\tJMSTimestamp=\"");
/* 129 */       strHeader.append(strJMSTimestamp);
/* 130 */       strHeader.append("\" ");
/*     */     }
/* 132 */     strHeader.append("/>\n");
/*     */ 
/* 134 */     strXmlBuffer.append(strHeader);
/*     */ 
/* 137 */     strXmlBuffer.append("<properties>");
/* 138 */     while (map.hasMoreElements()) {
/* 139 */       String pname = map.nextElement().toString();
/* 140 */       Object element = message.getObjectProperty(pname);
/* 141 */       if (element != null) {
/* 142 */         Class cls1 = element.getClass();
/* 143 */         if (cls1 != null) {
/* 144 */           String className = cls1.getName().replaceFirst("java.lang.", "");
/* 145 */           strXmlBuffer.append("\n\t<property name=\"");
/* 146 */           strXmlBuffer.append(HTMLEncoder.encodeString(pname));
/* 147 */           strXmlBuffer.append("\" type=\"");
/* 148 */           strXmlBuffer.append(className);
/* 149 */           strXmlBuffer.append("\">");
/* 150 */           strXmlBuffer.append(HTMLEncoder.encodeString(element.toString()));
/* 151 */           strXmlBuffer.append("</property>");
/*     */         }
/*     */       }
/*     */     }
/* 155 */     strXmlBuffer.append("\n</properties>\n");
/*     */ 
/* 158 */     strXmlBuffer.append("<body>");
/*     */ 
/* 162 */     if (MapMessage.class.isInstance(message)) {
/* 163 */       MapMessage msg = (MapMessage)message;
/* 164 */       map = msg.getMapNames();
/* 165 */       while (map.hasMoreElements()) {
/* 166 */         String mname = map.nextElement().toString();
/*     */ 
/* 168 */         Object element = msg.getObject(mname);
/* 169 */         if (element != null) {
/* 170 */           Class cls = element.getClass();
/* 171 */           if (cls != null) {
/* 172 */             String className = cls.getName().replaceFirst("java.lang.", "");
/* 173 */             strXmlBuffer.append("\n\t<node name=\"");
/* 174 */             strXmlBuffer.append(HTMLEncoder.encodeString(mname));
/* 175 */             strXmlBuffer.append("\" type=\"");
/* 176 */             strXmlBuffer.append(className);
/* 177 */             strXmlBuffer.append("\">");
/* 178 */             strXmlBuffer.append(HTMLEncoder.encodeString(element.toString()));
/* 179 */             strXmlBuffer.append("</node>");
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/* 185 */     else if (TextMessage.class.isInstance(message)) {
/* 186 */       TextMessage msg = (TextMessage)message;
/*     */ 
/* 188 */       strXmlBuffer.append("\n\t<node name=\"Text\" type=\"string\">");
/* 189 */       strXmlBuffer.append(HTMLEncoder.encodeString(msg.getText()));
/* 190 */       strXmlBuffer.append("</node>");
/*     */     }
/* 193 */     else if (BytesMessage.class.isInstance(message)) {
/* 194 */       BytesMessage msg = (BytesMessage)message;
/* 195 */       Base64 base64encoder = new Base64();
/* 196 */       byte[] byteBuffer = new byte[(int)msg.getBodyLength()];
/* 197 */       msg.readBytes(byteBuffer);
/* 198 */       strXmlBuffer.append("\n\t<node name=\"base64\" type=\"String\">");
/* 199 */       strXmlBuffer.append(HTMLEncoder.encodeString(base64encoder.encode(byteBuffer)));
/* 200 */       strXmlBuffer.append("</node>");
/*     */     }
/*     */ 
/* 203 */     strXmlBuffer.append("\n</body>");
/* 204 */     strXmlBuffer.append("\n</message>");
/* 205 */     return strXmlBuffer.toString();
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.util.ems.MarshalJMSMsgToStringJMSStreamImpl
 * JD-Core Version:    0.6.1
 */
