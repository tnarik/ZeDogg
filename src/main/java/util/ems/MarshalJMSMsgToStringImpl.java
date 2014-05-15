/*    */ package emssn00p.util.ems;
/*    */ 
/*    */ import java.text.SimpleDateFormat;
/*    */ import javax.jms.JMSException;
/*    */ import javax.jms.Message;
/*    */ 
/*    */ public class MarshalJMSMsgToStringImpl
/*    */   implements IMarshalJMSToString
/*    */ {
/* 20 */   protected SimpleDateFormat _dfXML = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
/*    */ 
/*    */   public String JMSMsgToString(Message message, String name)
/*    */     throws JMSException
/*    */   {
/* 28 */     return message.toString();
/*    */   }
/*    */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.util.ems.MarshalJMSMsgToStringImpl
 * JD-Core Version:    0.6.1
 */