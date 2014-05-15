package uk.co.lecafeautomatique.zedogg.util.ems;

import javax.jms.JMSException;
import javax.jms.Message;

public abstract interface IMarshalJMSToString
{
  public abstract String JMSMsgToString(Message paramMessage, String paramString)
    throws JMSException;
}
