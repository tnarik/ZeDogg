package uk.co.lecafeautomatique.zedogg.util.ems;

import java.text.SimpleDateFormat;
import javax.jms.JMSException;
import javax.jms.Message;

public class MarshalJMSMsgToStringImpl
  implements IMarshalJMSToString
{
  protected SimpleDateFormat _dfXML = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

  public String JMSMsgToString(Message message, String name)
    throws JMSException
  {
    return message.toString();
  }
}
