package uk.co.lecafeautomatique.zedogg.jms;

import javax.jms.JMSException;
import javax.jms.Message;

public class MarshalJMSMsgToStringProxyImpl implements MarshalJMSToString {
  private MarshalJMSToString _impl = null;

  public void setImpl(MarshalJMSToString impl) {
    _impl = impl;
  }

  public String JMSMsgToString(Message message, String name) throws JMSException {
    if (_impl == null) {
      return message.toString();
    }

    return _impl.JMSMsgToString(message, name);
  }
}
