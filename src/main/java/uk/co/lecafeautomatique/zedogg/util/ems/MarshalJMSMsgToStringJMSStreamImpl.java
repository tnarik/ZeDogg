package uk.co.lecafeautomatique.zedogg.util.ems;

import uk.co.lecafeautomatique.zedogg.util.Base64;
import uk.co.lecafeautomatique.zedogg.util.HTMLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;

public class MarshalJMSMsgToStringJMSStreamImpl
  implements IMarshalJMSToString
{
  protected SimpleDateFormat _dfXML = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

  public String JMSMsgToString(Message message, String name)
    throws JMSException
  {
    String strTextBuffer = "";
    StringBuffer strXmlBuffer = new StringBuffer();
    StringBuffer strHeader = new StringBuffer();
    String strCsvFields = "";

    Destination destReplyTo = message.getJMSReplyTo();
    String strJMSReplyTo;
    if (Topic.class.isInstance(destReplyTo)) {
      strJMSReplyTo = ((Topic)destReplyTo).getTopicName();
    }
    else
    {
      if (Queue.class.isInstance(destReplyTo)) {
        strJMSReplyTo = ((Queue)destReplyTo).getQueueName();
      }
      else {
        strJMSReplyTo = null;
      }
    }
    String strJMSMessageID = message.getJMSMessageID();
    String strJMSDestination = message.getJMSDestination().toString().substring(6, message.getJMSDestination().toString().length() - 1);
    String strJMSCorrelationID = message.getJMSCorrelationID();
    String strJMSDeliveryMode = String.valueOf(message.getJMSDeliveryMode());
    String strJMSPriority = String.valueOf(message.getJMSPriority());
    String strJMSType = message.getJMSType() == null ? null : message.getJMSType().toString();
    String strJMSExpiration = message.getJMSExpiration() == 0L ? null : String.valueOf(message.getJMSExpiration());
    String strJMSTimestamp = message.getJMSTimestamp() == 0L ? null : String.valueOf(message.getJMSTimestamp());
    Enumeration map = message.getPropertyNames();

    strXmlBuffer.append("<message type=\"");
    strXmlBuffer.append(message.getClass().getName().replaceFirst("com.tibco.tibjms.", ""));

    strXmlBuffer.append(" originationTime=\"" + this._dfXML.format(new Date(message.getJMSTimestamp())));
    strXmlBuffer.append(" receiveTime=\"");
    strXmlBuffer.append(this._dfXML.format(new Date()));
    strXmlBuffer.append("\">\n");

    strHeader.append("<header ");
    if ((strJMSMessageID != null) && (!strJMSMessageID.equals(""))) {
      strHeader.append("\n\tJMSMessageID=\"");
      strHeader.append(strJMSMessageID);
      strHeader.append("\" ");
    }

    if ((strJMSDestination != null) && (!strJMSDestination.equals(""))) {
      strHeader.append("\n\tJMSDestination=\"");
      strHeader.append(strJMSDestination);
      strHeader.append("\" ");
    }

    if ((strJMSReplyTo != null) && (!strJMSReplyTo.equals(""))) {
      strHeader.append("\n\tJMSReplyTo=\"");
      strHeader.append(strJMSReplyTo);
      strHeader.append("\" ");
    }

    if ((strJMSCorrelationID != null) && (!strJMSCorrelationID.equals(""))) {
      strHeader.append("\n\tJMSCorrelationID=\"");
      strHeader.append(strJMSCorrelationID);
      strHeader.append("\" ");
    }

    if ((strJMSDeliveryMode != null) && (!strJMSDeliveryMode.equals(""))) {
      strHeader.append("\n\tJMSDeliveryMode=\"");
      strHeader.append(strJMSDeliveryMode);
      strHeader.append("\" ");
    }

    if ((strJMSPriority != null) && (!strJMSPriority.equals(""))) {
      strHeader.append("\n\tJMSPriority=\"");
      strHeader.append(strJMSPriority);
      strHeader.append("\" ");
    }
    if ((strJMSType != null) && (!strJMSType.equals(""))) {
      strHeader.append("\n\tJMSType=\"");
      strHeader.append(strJMSType);
      strHeader.append("\" ");
    }

    if ((strJMSExpiration != null) && (!strJMSExpiration.equals(""))) {
      strHeader.append("\n\tJMSExpiration=\"");
      strHeader.append(strJMSExpiration);
      strHeader.append("\" ");
    }
    if ((strJMSTimestamp != null) && (!strJMSTimestamp.equals(""))) {
      strHeader.append("\n\tJMSTimestamp=\"");
      strHeader.append(strJMSTimestamp);
      strHeader.append("\" ");
    }
    strHeader.append("/>\n");

    strXmlBuffer.append(strHeader);

    strXmlBuffer.append("<properties>");
    while (map.hasMoreElements()) {
      String pname = map.nextElement().toString();
      Object element = message.getObjectProperty(pname);
      if (element != null) {
        Class cls1 = element.getClass();
        if (cls1 != null) {
          String className = cls1.getName().replaceFirst("java.lang.", "");
          strXmlBuffer.append("\n\t<property name=\"");
          strXmlBuffer.append(HTMLEncoder.encodeString(pname));
          strXmlBuffer.append("\" type=\"");
          strXmlBuffer.append(className);
          strXmlBuffer.append("\">");
          strXmlBuffer.append(HTMLEncoder.encodeString(element.toString()));
          strXmlBuffer.append("</property>");
        }
      }
    }
    strXmlBuffer.append("\n</properties>\n");

    strXmlBuffer.append("<body>");

    if (MapMessage.class.isInstance(message)) {
      MapMessage msg = (MapMessage)message;
      map = msg.getMapNames();
      while (map.hasMoreElements()) {
        String mname = map.nextElement().toString();

        Object element = msg.getObject(mname);
        if (element != null) {
          Class cls = element.getClass();
          if (cls != null) {
            String className = cls.getName().replaceFirst("java.lang.", "");
            strXmlBuffer.append("\n\t<node name=\"");
            strXmlBuffer.append(HTMLEncoder.encodeString(mname));
            strXmlBuffer.append("\" type=\"");
            strXmlBuffer.append(className);
            strXmlBuffer.append("\">");
            strXmlBuffer.append(HTMLEncoder.encodeString(element.toString()));
            strXmlBuffer.append("</node>");
          }
        }
      }

    }
    else if (TextMessage.class.isInstance(message)) {
      TextMessage msg = (TextMessage)message;

      strXmlBuffer.append("\n\t<node name=\"Text\" type=\"string\">");
      strXmlBuffer.append(HTMLEncoder.encodeString(msg.getText()));
      strXmlBuffer.append("</node>");
    }
    else if (BytesMessage.class.isInstance(message)) {
      BytesMessage msg = (BytesMessage)message;
      Base64 base64encoder = new Base64();
      byte[] byteBuffer = new byte[(int)msg.getBodyLength()];
      msg.readBytes(byteBuffer);
      strXmlBuffer.append("\n\t<node name=\"base64\" type=\"String\">");
      strXmlBuffer.append(HTMLEncoder.encodeString(base64encoder.encode(byteBuffer)));
      strXmlBuffer.append("</node>");
    }

    strXmlBuffer.append("\n</body>");
    strXmlBuffer.append("\n</message>");
    return strXmlBuffer.toString();
  }
}
