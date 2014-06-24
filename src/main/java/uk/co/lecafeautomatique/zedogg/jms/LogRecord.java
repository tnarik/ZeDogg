package uk.co.lecafeautomatique.zedogg.jms;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;

public class LogRecord implements Serializable {
  // For the Factory methods
  protected static Class<?> jmsClass;
  static {
    try {
      // jmsClass = Class.forName("com.tibco.tibjms.Tibjms");
      jmsClass = Class.forName("org.apache.activemq.command.ActiveMQMessage");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }
  
  
  protected static long seqCount = 0L;
  protected EventActionType _type;
  protected long _sequenceNumber;
  protected long _millis;
  protected StringBuffer _sJMSDestination = new StringBuffer();
  protected Message message;

  protected long _msgSeq = 0L;
  protected String _targetDestType = new String();
  protected String _targetName = new String();
  protected long _msgTimeStamp = 0L;
  protected String _eventAction = new String();
  protected String connectionType = new String();
  protected String eventClass = new String();
  protected String _targetObject = new String();
  protected String connectionUserName = new String();
  protected long connectionId = 0L;
  protected String connectionHostName = new String();
  protected String eventReason = new String();
  protected String _server = new String();
  protected String jmsMessageId = new String();
  protected String jmsCorrelationId = new String();

  protected String jmsDeliveryMode = new String();
  protected String jmsPriority = new String();
  protected long jmsExpiration = 0L;
  protected long jmsTimeStamp = 0L;

  public long getConnectionId() {
    return connectionId;
  }

  public void setConnectionId(long id) {
    connectionId = id;
  }

  public String getConnectionHostName() {
    return connectionHostName;
  }

  public void setConnectionHostName(String hostName) {
    connectionHostName = hostName;
  }

  public String getConnectionType() {
    return connectionType;
  }

  public void setConnectionType(String type) {
    connectionType = type;
  }

  public String getConnectionUserName() {
    return connectionUserName;
  }

  public void setConnectionUserName(String userName) {
    connectionUserName = userName;
  }

  public String getEventClass() {
    return eventClass;
  }

  public void setEventClass(String className) {
    eventClass = className;
  }

  public String getEventReason() {
    return eventReason;
  }

  public void setEventReason(String reason) {
    eventReason = reason;
  }

  public String getJMSCorrelationID() {
    return jmsCorrelationId;
  }

  public void setJMSCorrelationID(String id) {
    jmsCorrelationId = id;
  }

  public String getJMSDeliveryMode() {
    return jmsDeliveryMode;
  }

  public void setJMSDeliveryMode(String deliveryMode) {
    jmsDeliveryMode = deliveryMode;
  }

  public long getJMSExpiration() {
    return jmsExpiration;
  }

  public void setJMSExpiration(long expiration) {
    jmsExpiration = expiration;
  }

  public String getJMSMessageID() {
    return jmsMessageId;
  }

  public void setJMSMessageID(String id) {
    jmsMessageId = id;
  }

  public String getJMSPriority() {
    return jmsPriority;
  }

  public void setJMSPriority(String priority) {
    jmsPriority = priority;
  }

  public long getJMSTimeStamp() {
    return jmsTimeStamp;
  }

  public void setJMSTimeStamp(long timeStamp) {
    jmsTimeStamp = timeStamp;
  }

  public long getMsgTimeStamp() {
    return _msgTimeStamp;
  }

  public void setMsgTimeStamp(long timeStamp) {
    _msgTimeStamp = timeStamp;
  }

  public String getServer() {
    return _server;
  }

  public void setServer(String server) {
    _server = server;
  }

  public String getTargetDestType() {
    return _targetDestType;
  }

  public void setTargetDestType(String destType) {
    _targetDestType = destType;
  }

  public String getTargetName() {
    return _targetName;
  }

  public void setTargetName(String name) {
    _targetName = name;
  }

  public String getTargetObject() {
    return _targetObject;
  }

  public void setTargetObject(String object) {
    _targetObject = object;
  }

  public EventActionType getType() {
    return _type;
  }

  public void setType(EventActionType type) {
    _type = type;
  }

  public boolean isSevereType() {
    if ((EventActionType.RECEIVE.equals(getType())) || (EventActionType.RECEIVE.equals(getType()))) {
      return true;
    }

    return false;
  }

  public boolean isFatal() {
    return isSevereType();
  }

  public String getJMSDestination() {
    return _sJMSDestination.toString();
  }

  public StringBuffer getJMSDestinationAsStringBuffer() {
    return _sJMSDestination;
  }

  public void setJMSDestination(String subject) {
    _sJMSDestination.setLength(0);
    _sJMSDestination.append(subject);
  }

  public Message getMessage() {
    return message;
  }

  public StringBuffer getMessageAsStringBuffer() {
    return new StringBuffer(message.toString());
  }

  public void setMessage(Message msg) {
    message = msg;
  }

  public long getSequenceNumber() {
    return _sequenceNumber;
  }

  public void setSequenceNumber(long number) {
    _sequenceNumber = number;
  }

  public long getMillis() {
    return _millis;
  }

  public void setMillis(long millis) {
    _millis = millis;
  }

  public String toString(MarshalJMSToString marshaller) {
    StringBuffer buf = new StringBuffer();
    buf.append("LogRecord: [");
    buf.append(_type);
    buf.append(',');
    try {
      buf.append(marshaller.JMSMsgToString(message, ""));
    } catch (JMSException e) {
      buf.append(e.getMessage());
    }
    buf.append(']');
    return buf.toString();
  }

  public static synchronized void resetSequenceNumber() {
    seqCount = 0L;
  }

  protected static synchronized long getNextId() {
    seqCount += 1L;
    return seqCount;
  }
  
  
  // Factory Methods
  public static LogRecord create(Message msg) {
    LogRecord logRecord = new LogRecord();
    
    logRecord._millis = System.currentTimeMillis();
    logRecord._type = EventActionType.UNKNOWN;
    logRecord._sequenceNumber = getNextId();

    String strJMSDestination = null;
    try {
      Destination dest = msg.getJMSDestination();
      if (Topic.class.isInstance(dest)) {
        strJMSDestination = ((Topic) dest).getTopicName();
      } else if (Queue.class.isInstance(dest)) {
        strJMSDestination = ((Queue) dest).getQueueName();
      }
    } catch (JMSException e) {
      e.printStackTrace();
    }

    if (strJMSDestination != null) {
      if (strJMSDestination.startsWith("$sys.monitor.") || strJMSDestination.startsWith("VirtualTopic.Mirror.")) {
//        System.err.println("MONITOR record");
        createLogRecordMonitor(logRecord, strJMSDestination, msg);
      } else {
//        System.err.println("NORMAL record");
        createLogRecordNormal(logRecord, strJMSDestination, msg);
      }
    }

    return logRecord;
  }

  private static void createLogRecordNormal(LogRecord r, String strJMSDestination, Message msg) {
    setJMSParameters(r, msg);
    try {
      r.setMessage(msg);
      r.setJMSCorrelationID(msg.getJMSCorrelationID());
    } catch (JMSException e) {
      e.printStackTrace();
    }
    r.setJMSDestination(strJMSDestination);

    r.setType(EventActionType.UNKNOWN);
  }

  private static void createLogRecordMonitor(LogRecord r, String strJMSDestination, Message msg) {
//    System.err.println("DEBUG : creating Monitor event " + msg);
    MapMessage mapMsg = null;
    TextMessage txtMsg = null;
    Message realMsg = null;

    if (msg instanceof MapMessage) mapMsg = (MapMessage) msg;
    if (msg instanceof TextMessage) txtMsg = (TextMessage) msg;

    try {
      Method createFromBytesMethod = null;
      try {
        createFromBytesMethod = jmsClass.getMethod("createFromBytes", byte[].class);
      } catch (NoSuchMethodException e) { // createFromBytesMethod getMethod
        e.printStackTrace();
      }
      
      if ((mapMsg != null) && mapMsg.itemExists("message_bytes")) {
        System.err.println("DEBUG : extracting the real message (map)");
        realMsg = (Message) createFromBytesMethod.invoke(null, mapMsg.getBytes("message_bytes"));
      } else if ((txtMsg != null)) {
          System.err.println("DEBUG : extracting the real message (text)");
          realMsg = txtMsg;
      } else {
        createLogRecordNormal(r, strJMSDestination, msg);
        return;
      }
    } catch (JMSException e) {
      e.printStackTrace();
      return;
    } catch (IllegalAccessException e) { // createFromBytesMethod
      e.printStackTrace();
    } catch (IllegalArgumentException e) { // createFromBytesMethod
      e.printStackTrace();
    } catch (InvocationTargetException e) { // createFromBytesMethod
      e.printStackTrace();
    }

    setJMSParameters(r, realMsg);
    if (mapMsg != null) setMonitoringParameters(r, mapMsg);
    if (txtMsg != null) setMonitoringParameters(r, txtMsg);

    System.err.println("DEBUG : set Monitoring Parameters");
    if (strJMSDestination != null) {
      int ix = strJMSDestination.indexOf(".") + 1;
      ix = strJMSDestination.indexOf(".", ix) + 1;
      // DESTINATION should be based on the type of message
      //ix = strJMSDestination.indexOf(".", ix) + 1;
      //ix = strJMSDestination.indexOf(".", ix) + 1;

      strJMSDestination = strJMSDestination.substring(ix, strJMSDestination.length());
    }

    r.setJMSDestination(strJMSDestination);
    r.setMessage(realMsg);

    try {
      r.setJMSCorrelationID(realMsg.getJMSCorrelationID());
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }

  private static void setMonitoringParameters(LogRecord r, TextMessage msg) {
    try {
      r.setEventReason(msg.getStringProperty("event_reason"));
      r.setTargetDestType(msg.getStringProperty("target_dest_type"));
      r.setTargetName(msg.getStringProperty("target_name"));
      r.setType(EventActionType.RECEIVE);
      r.setConnectionType(msg.getStringProperty("conn_type"));
      r.setEventClass(msg.getStringProperty("event_class"));
      r.setTargetObject(msg.getStringProperty("target_object"));
      r.setConnectionUserName(msg.getStringProperty("conn_username"));
      r.setEventReason(msg.getStringProperty("event_reason"));
      r.setConnectionHostName(msg.getStringProperty("conn_hostname"));
      r.setServer(msg.getStringProperty("server"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }

  private static void setMonitoringParameters(LogRecord r, MapMessage msg) {
    try {
      r.setEventReason(msg.getStringProperty("event_reason"));
      r.setTargetDestType(msg.getStringProperty("target_dest_type"));
      r.setTargetName(msg.getStringProperty("target_name"));

      String sEventAction = msg.getStringProperty("event_action");
      if (sEventAction.compareTo("receive") == 0)
        r.setType(EventActionType.RECEIVE);
      else if (sEventAction.compareTo("acknowledge") == 0)
        r.setType(EventActionType.ACKNOWLEDGE);
      else if (sEventAction.compareTo("send") == 0)
        r.setType(EventActionType.SEND);

      r.setConnectionType(msg.getStringProperty("conn_type"));
      r.setEventClass(msg.getStringProperty("event_class"));
      r.setTargetObject(msg.getStringProperty("target_object"));
      r.setConnectionUserName(msg.getStringProperty("conn_username"));
      r.setEventReason(msg.getStringProperty("event_reason"));
      r.setConnectionHostName(msg.getStringProperty("conn_hostname"));
      r.setServer(msg.getStringProperty("server"));
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }

  private static void setJMSParameters(LogRecord r, Message msg) {
    try {
      r.setJMSMessageID(msg.getJMSMessageID());
      r.setJMSCorrelationID(msg.getJMSCorrelationID());
      r.setJMSDeliveryMode(String.valueOf(msg.getJMSDeliveryMode()));
      r.setJMSPriority(String.valueOf(msg.getJMSPriority()));
    // String strJMSType = msg.getJMSType() == null ? null : msg.getJMSType().toString();
      r.setJMSExpiration(msg.getJMSExpiration());
      r.setJMSTimeStamp(msg.getJMSTimestamp());
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }
}
