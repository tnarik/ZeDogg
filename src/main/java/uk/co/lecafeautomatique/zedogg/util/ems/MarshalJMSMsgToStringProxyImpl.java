package uk.co.lecafeautomatique.zedogg.util.ems;

import javax.jms.JMSException;
import javax.jms.Message;

public class MarshalJMSMsgToStringProxyImpl implements IMarshalJMSToString {
  private IMarshalJMSToString _impl = null;

  public void setImpl(IMarshalJMSToString impl) {
    _impl = impl;
  }

  public String JMSMsgToString(Message message, String name) throws JMSException {
    if (_impl == null) {
      return message.toString();
    }

    return _impl.JMSMsgToString(message, name);
  }
}
