package uk.co.lecafeautomatique.zedogg.jms;

import java.io.Serializable;
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
  
  
  protected static long _seqCount = 0L;
  protected EventActionType _type;
  protected long _sequenceNumber;
  protected long _millis;
  protected StringBuffer _sJMSDestination = new StringBuffer();
  protected Message _message;

  protected long _msgSeq = 0L;
  protected String _targetDestType = new String();
  protected String _targetName = new String();
  protected long _msgTimeStamp = 0L;
  protected String _eventAction = new String();
  protected String _connType = new String();
  protected String _eventClass = new String();
  protected String _targetObject = new String();
  protected String _connUserName = new String();
  protected long _connConnID = 0L;
  protected String _connHostName = new String();
  protected String _eventReason = new String();
  protected String _server = new String();
  protected String _JMSMessageID = new String();
  protected String _JMSCorrelationID = new String();

  protected String _JMSDeliveryMode = new String();
  protected String _JMSPriority = new String();
  protected long _JMSExpiration = 0L;
  protected long _JMSTimeStamp = 0L;
  private static LogRecord[] _freeStack = new LogRecord[2000];
  private static int _countFree = 0;
  private static long lastAllocationTime = System.currentTimeMillis() - 3000L;

  public long getConnConnID() {
    return this._connConnID;
  }

  public void setConnConnID(long connID) {
    this._connConnID = connID;
  }

  public String getConnHostName() {
    return this._connHostName;
  }

  public void setConnHostName(String hostName) {
    this._connHostName = hostName;
  }

  public String getConnType() {
    return this._connType;
  }

  public void setConnType(String type) {
    this._connType = type;
  }

  public String getConnUserName() {
    return this._connUserName;
  }

  public void setConnUserName(String userName) {
    this._connUserName = userName;
  }

  public String getEventClass() {
    return this._eventClass;
  }

  public void setEventClass(String class1) {
    this._eventClass = class1;
  }

  public String getEventReason() {
    return this._eventReason;
  }

  public void setEventReason(String reason) {
    this._eventReason = reason;
  }

  public String getJMSCorrelationID() {
    return this._JMSCorrelationID;
  }

  public void setJMSCorrelationID(String correlationID) {
    this._JMSCorrelationID = correlationID;
  }

  public String getJMSDeliveryMode() {
    return this._JMSDeliveryMode;
  }

  public void setJMSDeliveryMode(String deliveryMode) {
    this._JMSDeliveryMode = deliveryMode;
  }

  public long getJMSExpiration() {
    return this._JMSExpiration;
  }

  public void setJMSExpiration(long expiration) {
    this._JMSExpiration = expiration;
  }

  public String getJMSMessageID() {
    return this._JMSMessageID;
  }

  public void setJMSMessageID(String messageID) {
    this._JMSMessageID = messageID;
  }

  public String getJMSPriority() {
    return this._JMSPriority;
  }

  public void setJMSPriority(String priority) {
    this._JMSPriority = priority;
  }

  public long getJMSTimeStamp() {
    return this._JMSTimeStamp;
  }

  public void setJMSTimeStamp(long timeStamp) {
    this._JMSTimeStamp = timeStamp;
  }

  public long getMsgTimeStamp() {
    return this._msgTimeStamp;
  }

  public void setMsgTimeStamp(long timeStamp) {
    this._msgTimeStamp = timeStamp;
  }

  public String getServer() {
    return this._server;
  }

  public void setServer(String _server) {
    this._server = _server;
  }

  public String getTargetDestType() {
    return this._targetDestType;
  }

  public void setTargetDestType(String destType) {
    this._targetDestType = destType;
  }

  public String getTargetName() {
    return this._targetName;
  }

  public void setTargetName(String name) {
    this._targetName = name;
  }

  public String getTargetObject() {
    return this._targetObject;
  }

  public void setTargetObject(String object) {
    this._targetObject = object;
  }

  public static synchronized LogRecord getInstance() {
    if (_countFree == 0) {
      long timeSinceLastAlloc = System.currentTimeMillis() - lastAllocationTime;

      int noToAlloc = 0;
      if (timeSinceLastAlloc < 200L)
        noToAlloc = 2000;
      else if (timeSinceLastAlloc < 1000L)
        noToAlloc = 1000;
      else if (timeSinceLastAlloc < 3000L)
        noToAlloc = 500;
      else {
        noToAlloc = 100;
      }

      for (int i = 0; i < noToAlloc; i++) {
        freeInstance(new LogRecord());
      }

      lastAllocationTime = System.currentTimeMillis();
    }

    LogRecord result = _freeStack[(--_countFree)];

    result._millis = System.currentTimeMillis();
    result._type = EventActionType.UNKNOWN;
    result._sequenceNumber = getNextId();
    return result;
  }

  public static synchronized void freeInstance(LogRecord lr) {
    if (_countFree < 2000)
      _freeStack[(_countFree++)] = lr;
  }

  public EventActionType getType() {
    return this._type;
  }

  public void setType(EventActionType type) {
    this._type = type;
  }

  public boolean isSevereType() {
    boolean isSevere = false;

    if ((EventActionType.RECEIVE.equals(getType())) || (EventActionType.RECEIVE.equals(getType()))) {
      isSevere = true;
    }

    return isSevere;
  }

  public boolean isFatal() {
    return isSevereType();
  }

  public String getJMSDestination() {
    return this._sJMSDestination.toString();
  }

  public StringBuffer getJMSDestinationAsStringBuffer() {
    return this._sJMSDestination;
  }

  public void setJMSDestination(String subject) {
    this._sJMSDestination.setLength(0);
    this._sJMSDestination.append(subject);
  }

  public Message getMessage() {
    return this._message;
  }

  public StringBuffer getMessageAsStringBuffer() {
    return new StringBuffer(this._message.toString());
  }

  public void setMessage(Message message) {
    this._message = message;
  }

  public long getSequenceNumber() {
    return this._sequenceNumber;
  }

  public void setSequenceNumber(long number) {
    this._sequenceNumber = number;
  }

  public long getMillis() {
    return this._millis;
  }

  public void setMillis(long millis) {
    this._millis = millis;
  }

  public String toString(MarshalJMSToString marshaller) {
    StringBuffer buf = new StringBuffer();
    buf.append("LogRecord: [");
    buf.append(this._type);
    buf.append(',');
    try {
      buf.append(marshaller.JMSMsgToString(this._message, ""));
    } catch (JMSException e) {
      buf.append(e.getMessage());
    }
    buf.append(']');
    return buf.toString();
  }

  public static synchronized void resetSequenceNumber() {
    _seqCount = 0L;
  }

  protected static synchronized long getNextId() {
    _seqCount += 1L;
    return _seqCount;
  }
  
  
  // Factory Methods
  public static LogRecord create(Message msg) {
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
      //_statusLabel.setText(e1.getMessage());
    }

    if (strJMSDestination != null) {
      if (strJMSDestination.startsWith("$sys.monitor.") || strJMSDestination.startsWith("VirtualTopic.Mirror.")) {
        System.err.println("MONITOR record");
        createLogRecordMonitor(r, strJMSDestination, msg);
      } else {
        System.err.println("NORMAL record");
        createLogRecordNormal(r, strJMSDestination, msg);
      }

    }

    return r;
  }

  private static void createLogRecordNormal(LogRecord r, String strJMSDestination, Message msg) {
    setJMSParameters(r, msg);
    try {
      r.setMessage(msg);
    } catch (Exception ex) {
      ex.printStackTrace();
      //_statusLabel.setText(ex.getMessage());
    }
    try {
      r.setJMSCorrelationID(msg.getJMSCorrelationID());
    } catch (JMSException e) {
    }
    r.setJMSDestination(strJMSDestination);

    r.setType(EventActionType.UNKNOWN);
  }

  private static void createLogRecordMonitor(LogRecord r, String strJMSDestination, Message msg) {
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
        createLogRecordNormal(r, strJMSDestination, msg);
        return;
      }
    } catch (JMSException e1) {
      //_statusLabel.setText(e1.getMessage());
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
      //_statusLabel.setText(e.getMessage());
    }

    r.setJMSDestination(strJMSDestination);
    try {
      r.setMessage(realMsg);
    } catch (Exception ex) {
      ex.printStackTrace();
      //_statusLabel.setText(ex.getMessage());
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
