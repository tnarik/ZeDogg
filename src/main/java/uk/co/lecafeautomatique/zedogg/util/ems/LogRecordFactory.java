package uk.co.lecafeautomatique.zedogg.util.ems;

// WIP import com.tibco.tibjms.Tibjms;
import uk.co.lecafeautomatique.zedogg.EventActionType;
import uk.co.lecafeautomatique.zedogg.LogRecord;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.swing.JLabel;

public class LogRecordFactory
{
  public static LogRecord createLogRecordFromJMSMessage(JLabel _statusLabel, Message msg)
  {
    LogRecord r = LogRecord.getInstance();
    String strJMSDestination;
    try
    {
      Destination dest = msg.getJMSDestination();
      if (Topic.class.isInstance(dest)) {
        strJMSDestination = ((Topic)dest).getTopicName();
      }
      else
      {
        if (Queue.class.isInstance(dest)) {
          strJMSDestination = ((Queue)dest).getQueueName();
        }
        else
          strJMSDestination = null;
      }
    } catch (JMSException e1) {
      strJMSDestination = null;
      _statusLabel.setText(e1.getMessage());
    }

    if (strJMSDestination != null) {
      if (strJMSDestination.startsWith("$sys.monitor."))
        createLogRecordMonitor(r, _statusLabel, strJMSDestination, msg);
      else {
        createLogRecordNormal(r, _statusLabel, strJMSDestination, msg);
      }

    }

    return r;
  }

  private static void createLogRecordNormal(LogRecord r, JLabel _statusLabel, String strJMSDestination, Message msg)
  {
    setJMSParameters(r, msg);
    try
    {
      r.setMessage(msg);
    } catch (Exception ex) {
      ex.printStackTrace();
      _statusLabel.setText(ex.getMessage());
    }
    try
    {
      r.setJMSCorrelationID(msg.getJMSCorrelationID());
    }
    catch (JMSException e) {
    }
    r.setJMSDestination(strJMSDestination);
    try
    {
      String strJMSReplyTo;
      if (Topic.class.isInstance(msg.getJMSReplyTo())) {
        strJMSReplyTo = ((Topic)msg.getJMSReplyTo()).getTopicName();
      }
      else
      {
        if (Queue.class.isInstance(msg.getJMSReplyTo()))
          strJMSReplyTo = ((Queue)msg.getJMSReplyTo()).getQueueName();
        else {
          strJMSReplyTo = null;
        }
      }
      r.setJMSReplyTo(strJMSReplyTo);
    }
    catch (JMSException e2) {
      _statusLabel.setText(e2.getMessage());
    }

    r.setType(EventActionType.UNKNOWN);
  }

  private static void createLogRecordMonitor(LogRecord r, JLabel _statusLabel, String strJMSDestination, Message msg)
  {
    MapMessage mapMsg = (MapMessage)msg;
    Message realMsg = null;
    try
    {
      if (mapMsg.itemExists("message_bytes")) {
        realMsg = null; // WIP Tibjms.createFromBytes(mapMsg.getBytes("message_bytes"));
      } else {
        createLogRecordNormal(r, _statusLabel, strJMSDestination, msg);
        return;
      }
    } catch (JMSException e1) {
      _statusLabel.setText(e1.getMessage());
      e1.printStackTrace();
      return;
    }

    setJMSParameters(r, realMsg);
    setMonitoringParameters(r, mapMsg);
    try
    {
      if (strJMSDestination != null) {
        int ix = strJMSDestination.indexOf(".") + 1;
        ix = strJMSDestination.indexOf(".", ix) + 1;
        ix = strJMSDestination.indexOf(".", ix) + 1;
        ix = strJMSDestination.indexOf(".", ix) + 1;

        strJMSDestination = strJMSDestination.substring(ix, strJMSDestination.length());
      }
    }
    catch (Exception e)
    {
      _statusLabel.setText(e.getMessage());
    }

    r.setJMSDestination(strJMSDestination);
    try
    {
      r.setMessage(realMsg);
    } catch (Exception ex) {
      ex.printStackTrace();
      _statusLabel.setText(ex.getMessage());
    }
    try
    {
      r.setJMSCorrelationID(realMsg.getJMSCorrelationID());
    }
    catch (JMSException e)
    {
    }
    try
    {
      String strJMSReplyTo;
      if (Topic.class.isInstance(realMsg.getJMSReplyTo())) {
        strJMSReplyTo = ((Topic)realMsg.getJMSReplyTo()).getTopicName();
      }
      else
      {
        if (Queue.class.isInstance(msg.getJMSReplyTo()))
          strJMSReplyTo = ((Queue)realMsg.getJMSReplyTo()).getQueueName();
        else {
          strJMSReplyTo = null;
        }
      }
      r.setJMSReplyTo(strJMSReplyTo);
    }
    catch (JMSException e2) {
      _statusLabel.setText(e2.getMessage());
    }
  }

  private static void setMonitoringParameters(LogRecord r, MapMessage msg)
  {
    try
    {
      r.setEventReason(msg.getStringProperty("event_reason"));
    } catch (JMSException e) {
      e.printStackTrace();
    }

    try
    {
      r.setTargetDestType(msg.getStringProperty("target_dest_type"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try
    {
      r.setTargetName(msg.getStringProperty("target_name"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try
    {
      String sEventAction = msg.getStringProperty("event_action");
      if (sEventAction.compareTo("receive") == 0)
        r.setType(EventActionType.RECEIVE);
      else if (sEventAction.compareTo("acknowledge") == 0)
        r.setType(EventActionType.ACKNOWLEDGE);
      else if (sEventAction.compareTo("send") == 0)
        r.setType(EventActionType.SEND);
    }
    catch (JMSException e)
    {
      e.printStackTrace();
    }
    try
    {
      r.setConnType(msg.getStringProperty("conn_type"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try
    {
      r.setEventClass(msg.getStringProperty("event_class"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try
    {
      r.setTargetObject(msg.getStringProperty("target_object"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try
    {
      r.setConnUserName(msg.getStringProperty("conn_username"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try
    {
      r.setEventReason(msg.getStringProperty("event_reason"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try
    {
      r.setConnHostName(msg.getStringProperty("conn_hostname"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try
    {
      r.setServer(msg.getStringProperty("server"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }

  private static void setJMSParameters(LogRecord r, Message msg)
  {
    try
    {
      r.setJMSMessageID(msg.getJMSMessageID());
    } catch (JMSException e3) {
      e3.printStackTrace();
    }

    try
    {
      String strJMSCorrelationID = msg.getJMSCorrelationID();
    }
    catch (JMSException e4)
    {
      String strJMSCorrelationID;
      e4.printStackTrace();
    }
    try
    {
      String strJMSDeliveryMode = String.valueOf(msg.getJMSDeliveryMode());
    }
    catch (JMSException e5)
    {
      String strJMSDeliveryMode;
      e5.printStackTrace();
    }
    try
    {
      String strJMSPriority = String.valueOf(msg.getJMSPriority());
    }
    catch (JMSException e6)
    {
      String strJMSPriority;
      e6.printStackTrace();
    }
    try
    {
      String strJMSType = msg.getJMSType() == null ? null : msg.getJMSType().toString();
    }
    catch (JMSException e7)
    {
      String strJMSType;
      e7.printStackTrace();
    }
    try
    {
      String strJMSExpiration = msg.getJMSExpiration() == 0L ? null : String.valueOf(msg.getJMSExpiration());
    }
    catch (JMSException e8)
    {
      String strJMSExpiration;
      e8.printStackTrace();
    }
    try
    {
      String strJMSTimestamp = msg.getJMSTimestamp() == 0L ? null : String.valueOf(msg.getJMSTimestamp());
    }
    catch (JMSException e9)
    {
      String strJMSTimestamp;
      e9.printStackTrace();
    }
  }
}
