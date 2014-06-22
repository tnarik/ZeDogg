package uk.co.lecafeautomatique.zedogg;

import uk.co.lecafeautomatique.zedogg.util.jms.MarshalJMSToString;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;

public class LogRecord implements Serializable {
  protected static long _seqCount = 0L;
  protected EventActionType _type;
  protected long _sequenceNumber;
  protected long _millis;
  protected StringBuffer _sJMSDestination = new StringBuffer();
  protected Message _message;
  protected String _JMSReplyTo = new String();

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
  private static final int MAX_FREE_POOL_SIZE = 2000;
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

  public void setJMSReplyTo(String replysub) {
    this._JMSReplyTo = replysub;
  }

  public static synchronized void resetSequenceNumber() {
    _seqCount = 0L;
  }

  protected static synchronized long getNextId() {
    _seqCount += 1L;
    return _seqCount;
  }
}
