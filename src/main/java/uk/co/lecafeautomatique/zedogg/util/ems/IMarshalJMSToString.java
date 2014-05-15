package uk.co.lecafeautomatique.zedogg.util.ems;

import javax.jms.JMSException;
import javax.jms.Message;

public abstract interface IMarshalJMSToString
{
  public abstract String JMSMsgToString(Message paramMessage, String paramString)
    throws JMSException;
}

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.util.ems.IMarshalJMSToString
 * JD-Core Version:    0.6.1
 */
