package uk.co.lecafeautomatique.zedogg.jms;

import java.lang.reflect.Method;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.swing.JLabel;

public class LogRecordFactory {
  protected static Class<?> jmsClass;
  static {
    try {
      // tibjmsClass = Class.forName("com.tibco.tibjms.Tibjms");
      jmsClass = Class.forName("org.apache.activemq.command.ActiveMQMessage");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public static LogRecord createLogRecordFromJMSMessage(JLabel _statusLabel, Message msg) {
    LogRecord r = LogRecord.getInstance();
    String strJMSDestination;
    try {
      Destination dest = msg.getJMSDestination();
      if (Topic.class.isInstance(dest)) {
        strJMSDestination = ((Topic) dest).getTopicName();
      } else {
        if (Queue.class.isInstance(dest)) {
          strJMSDestination = ((Queue) dest).getQueueName();
        } else
          strJMSDestination = null;
      }
    } catch (JMSException e1) {
      strJMSDestination = null;
      _statusLabel.setText(e1.getMessage());
    }

    if (strJMSDestination != null) {
      if (strJMSDestination.startsWith("$sys.monitor.") || strJMSDestination.startsWith("VirtualTopic.Mirror.")) {
        System.err.println("MONITOR record");
        createLogRecordMonitor(r, _statusLabel, strJMSDestination, msg);
      } else {
        System.err.println("NORMAL record");
        createLogRecordNormal(r, _statusLabel, strJMSDestination, msg);
      }

    }

    return r;
  }

  private static void createLogRecordNormal(LogRecord r, JLabel _statusLabel, String strJMSDestination, Message msg) {
    setJMSParameters(r, msg);
    try {
      r.setMessage(msg);
    } catch (Exception ex) {
      ex.printStackTrace();
      _statusLabel.setText(ex.getMessage());
    }
    try {
      r.setJMSCorrelationID(msg.getJMSCorrelationID());
    } catch (JMSException e) {
    }
    r.setJMSDestination(strJMSDestination);

    r.setType(EventActionType.UNKNOWN);
  }

  private static void createLogRecordMonitor(LogRecord r, JLabel _statusLabel, String strJMSDestination, Message msg) {
    System.err.println("DEBUG : creating Monitor event " + msg);
    MapMessage mapMsg = null;
    TextMessage txtMsg = null;
    if (msg instanceof MapMessage) {
      mapMsg = (MapMessage) msg;
    }
    if (msg instanceof TextMessage) {
      txtMsg = (TextMessage) msg;
    }

    System.err.println("DEBUG : mapped Monitor event");
    Message realMsg = null;
    try {
      System.err.println("DEBUG : got a Monitor event");

      Method createFromBytesMethod = null;
      try {
        createFromBytesMethod = jmsClass.getMethod("createFromBytes", byte[].class);
      } catch (NoSuchMethodException ex) {
        realMsg = null;
      }

      if ((mapMsg != null) && mapMsg.itemExists("message_bytes")) {
        try {
          System.err.println("DEBUG : extracting the real message (map)");
          realMsg = (Message) createFromBytesMethod.invoke(null, mapMsg.getBytes("message_bytes"));
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else if ((txtMsg != null)) {
        try {
          System.err.println("DEBUG : extracting the real message (text)");
          realMsg = (Message)txtMsg;
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        createLogRecordNormal(r, _statusLabel, strJMSDestination, msg);
        return;
      }
    } catch (JMSException e1) {
      _statusLabel.setText(e1.getMessage());
      e1.printStackTrace();
      return;
    }

    System.err.println("DEBUG : to set JMS Parameters");
    setJMSParameters(r, realMsg);
    System.err.println("DEBUG : set JMS Parameters");
    if (mapMsg != null) setMonitoringParameters(r, mapMsg);
    try {
    if (txtMsg != null) setMonitoringParameters(r, txtMsg);
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.err.println("DEBUG : set Monitoring Parameters");
    try {
      if (strJMSDestination != null) {
        int ix = strJMSDestination.indexOf(".") + 1;
        ix = strJMSDestination.indexOf(".", ix) + 1;
        // DESTINATION should be based on the type of message
        //ix = strJMSDestination.indexOf(".", ix) + 1;
        //ix = strJMSDestination.indexOf(".", ix) + 1;

        strJMSDestination = strJMSDestination.substring(ix, strJMSDestination.length());
      }
    } catch (Exception e) {
      _statusLabel.setText(e.getMessage());
    }

    r.setJMSDestination(strJMSDestination);
    try {
      r.setMessage(realMsg);
    } catch (Exception ex) {
      ex.printStackTrace();
      _statusLabel.setText(ex.getMessage());
    }
    try {
      r.setJMSCorrelationID(realMsg.getJMSCorrelationID());
    } catch (JMSException e) {
    }
  }

  private static void setMonitoringParameters(LogRecord r, TextMessage msg) {
    try {
      r.setEventReason(msg.getStringProperty("event_reason"));
    } catch (JMSException e) {
      e.printStackTrace();
    }

    try {
      r.setTargetDestType(msg.getStringProperty("target_dest_type"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try {
      r.setTargetName(msg.getStringProperty("target_name"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    r.setType(EventActionType.RECEIVE);
    try {
      r.setConnType(msg.getStringProperty("conn_type"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try {
      r.setEventClass(msg.getStringProperty("event_class"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try {
      r.setTargetObject(msg.getStringProperty("target_object"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try {
      r.setConnUserName(msg.getStringProperty("conn_username"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try {
      r.setEventReason(msg.getStringProperty("event_reason"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try {
      r.setConnHostName(msg.getStringProperty("conn_hostname"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try {
      r.setServer(msg.getStringProperty("server"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }

  private static void setMonitoringParameters(LogRecord r, MapMessage msg) {
    try {
      r.setEventReason(msg.getStringProperty("event_reason"));
    } catch (JMSException e) {
      e.printStackTrace();
    }

    try {
      r.setTargetDestType(msg.getStringProperty("target_dest_type"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try {
      r.setTargetName(msg.getStringProperty("target_name"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try {
      String sEventAction = msg.getStringProperty("event_action");
      if (sEventAction.compareTo("receive") == 0)
        r.setType(EventActionType.RECEIVE);
      else if (sEventAction.compareTo("acknowledge") == 0)
        r.setType(EventActionType.ACKNOWLEDGE);
      else if (sEventAction.compareTo("send") == 0)
        r.setType(EventActionType.SEND);
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try {
      r.setConnType(msg.getStringProperty("conn_type"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try {
      r.setEventClass(msg.getStringProperty("event_class"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try {
      r.setTargetObject(msg.getStringProperty("target_object"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try {
      r.setConnUserName(msg.getStringProperty("conn_username"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try {
      r.setEventReason(msg.getStringProperty("event_reason"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try {
      r.setConnHostName(msg.getStringProperty("conn_hostname"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
    try {
      r.setServer(msg.getStringProperty("server"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }

  private static void setJMSParameters(LogRecord r, Message msg) {
    try {
      r.setJMSMessageID(msg.getJMSMessageID());
    } catch (JMSException e3) {
      e3.printStackTrace();
    }

    try {
      r.setJMSCorrelationID(msg.getJMSCorrelationID());
    } catch (JMSException e4) {
      e4.printStackTrace();
    }
    try {
      r.setJMSDeliveryMode(String.valueOf(msg.getJMSDeliveryMode()));
    } catch (JMSException e5) {
      e5.printStackTrace();
    }
    try {
      r.setJMSPriority(String.valueOf(msg.getJMSPriority()));
    } catch (JMSException e6) {
      e6.printStackTrace();
    }
    try {
      String strJMSType = msg.getJMSType() == null ? null : msg.getJMSType().toString();
    } catch (JMSException e7) {
      e7.printStackTrace();
    }
    try {
      r.setJMSExpiration(msg.getJMSExpiration());
    } catch (JMSException e8) {
      e8.printStackTrace();
    }
    try {
      r.setJMSTimeStamp(msg.getJMSTimestamp());
    } catch (JMSException e9) {
      e9.printStackTrace();
    }
  }
}
