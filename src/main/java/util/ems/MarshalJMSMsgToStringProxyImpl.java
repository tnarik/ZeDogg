/*    */ package emssn00p.util.ems;
/*    */ 
/*    */ import javax.jms.JMSException;
/*    */ import javax.jms.Message;
/*    */ 
/*    */ public class MarshalJMSMsgToStringProxyImpl
/*    */   implements IMarshalJMSToString
/*    */ {
/* 22 */   private static IMarshalJMSToString _impl = null;
/*    */ 
/*    */   public void setImpl(IMarshalJMSToString _impl)
/*    */   {
/* 29 */     _impl = _impl;
/*    */   }
/*    */ 
/*    */   public String JMSMsgToString(Message message, String name) throws JMSException {
/* 33 */     if (_impl == null) {
/* 34 */       return message.toString();
/*    */     }
/*    */ 
/* 37 */     return _impl.JMSMsgToString(message, name);
/*    */   }
/*    */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.util.ems.MarshalJMSMsgToStringProxyImpl
 * JD-Core Version:    0.6.1
 */