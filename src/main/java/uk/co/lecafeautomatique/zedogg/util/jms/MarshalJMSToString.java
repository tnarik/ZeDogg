package uk.co.lecafeautomatique.zedogg.util.jms;

import javax.jms.JMSException;
import javax.jms.Message;

public abstract interface MarshalJMSToString {
  public abstract String JMSMsgToString(Message paramMessage, String paramString) throws JMSException;
}
