package uk.co.lecafeautomatique.zedogg;

import java.awt.Color;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EventActionType
  implements Serializable
{
  private static final long serialVersionUID = 3977580307551565621L;
  public static final EventActionType RECEIVE = new EventActionType("RECEIVE", 0);
  public static final EventActionType SEND = new EventActionType("SEND", 1);
  public static final EventActionType ACKNOWLEDGE = new EventActionType("ACKNOWLEDGE", 2);
  public static final EventActionType UNKNOWN = new EventActionType("UNKNOWN", 3);

  public static final EventActionType CONNECT = new EventActionType("CONNECT", 4);
  public static final EventActionType ACCEPT = new EventActionType("ACCEPT", 5);
  public static final EventActionType DISCONNECT = new EventActionType("DISCONNECT", 6);
  public static final EventActionType INTEREST = new EventActionType("INTEREST", 7);
  public static final EventActionType CREATE = new EventActionType("CREATE", 8);
  public static final EventActionType DELETE = new EventActionType("DELETE", 9);
  public static final EventActionType MODIFY = new EventActionType("MODIFY", 10);
  public static final EventActionType ADD = new EventActionType("ADD", 11);
  public static final EventActionType REMOVE = new EventActionType("REMOVE", 12);
  public static final EventActionType GRANT = new EventActionType("GRANT", 13);
  public static final EventActionType REVOKE = new EventActionType("REVOKE", 14);
  public static final EventActionType PURGE = new EventActionType("PURGE", 15);
  public static final EventActionType COMMIT = new EventActionType("COMMIT", 16);
  public static final EventActionType ROLLBACK = new EventActionType("ROLLBACK", 17);
  public static final EventActionType ROTATELOG = new EventActionType("ROTATELOG", 18);
  protected String _label;
  protected int _precedence;
  private static EventActionType[] _allDefaultLevels;
  private static Map<String, EventActionType> _eventActionTypeMap;
  private static Map<EventActionType, Color> _EventActionTypeColorMap;
  private static Map<String, EventActionType> _registeredEventActionTypeMap = new HashMap();

  public EventActionType(String label, int precedence)
  {
    this._label = label;
    this._precedence = precedence;
  }

  public String getLabel()
  {
    return this._label;
  }

  public boolean encompasses(EventActionType level)
  {
    if (level.getPrecedence() <= getPrecedence()) {
      return true;
    }

    return false;
  }

  public static EventActionType valueOf(String level)
    throws MsgTypeFormatException
  {
    EventActionType logLevel = null;
    if (level != null) {
      level = level.trim().toUpperCase();
      logLevel = (EventActionType)_eventActionTypeMap.get(level);
    }

    if ((logLevel == null) && (_registeredEventActionTypeMap.size() > 0)) {
      logLevel = (EventActionType)_registeredEventActionTypeMap.get(level);
    }

    if (logLevel == null) {
      StringBuffer buf = new StringBuffer();
      buf.append("Error while trying to parse (" + level + ") into");
      buf.append(" a MsgType.");
      throw new MsgTypeFormatException(buf.toString());
    }
    return logLevel;
  }

  public static EventActionType register(EventActionType logLevel)
  {
    if (logLevel == null) return null;

    if (_eventActionTypeMap.get(logLevel.getLabel()) == null) {
      return (EventActionType)_registeredEventActionTypeMap.put(logLevel.getLabel(), logLevel);
    }

    return null;
  }

  public static void register(EventActionType[] MsgTypes) {
    if (MsgTypes != null)
      for (int i = 0; i < MsgTypes.length; i++)
        register(MsgTypes[i]);
  }

  public static void register(List MsgTypes)
  {
    if (MsgTypes != null) {
      Iterator it = MsgTypes.iterator();
      while (it.hasNext())
        register((EventActionType)it.next());
    }
  }

  public boolean equals(Object o)
  {
    boolean equals = false;

    if (((o instanceof EventActionType)) && 
      (getPrecedence() == ((EventActionType)o).getPrecedence()))
    {
      equals = true;
    }

    return equals;
  }

  public int hashCode() {
    return this._label.hashCode();
  }

  public String toString() {
    return this._label;
  }

  public void setLogLevelColorMap(EventActionType level, Color color)
  {
    _EventActionTypeColorMap.remove(level);

    if (color == null) {
      color = Color.BLACK;
    }
    _EventActionTypeColorMap.put(level, color);
  }

  public static void resetLogLevelColorMap()
  {
    _EventActionTypeColorMap.clear();

    for (int i = 0; i < _allDefaultLevels.length; i++)
      _EventActionTypeColorMap.put(_allDefaultLevels[i], Color.black);
  }

  public static List getAllDefaultLevels()
  {
    return Arrays.asList(_allDefaultLevels);
  }

  public static Map getLogLevelColorMap() {
    return _EventActionTypeColorMap;
  }

  protected int getPrecedence()
  {
    return this._precedence;
  }

  static
  {
    _allDefaultLevels = new EventActionType[] { RECEIVE, SEND, ACKNOWLEDGE, UNKNOWN, CONNECT, ACCEPT, DISCONNECT, INTEREST, CREATE, DELETE, MODIFY, ADD, REMOVE, GRANT, REVOKE, PURGE, COMMIT, ROLLBACK, ROTATELOG };

    _eventActionTypeMap = new HashMap();
    for (int i = 0; i < _allDefaultLevels.length; i++) {
      _eventActionTypeMap.put(_allDefaultLevels[i].getLabel(), _allDefaultLevels[i]);
    }

    _EventActionTypeColorMap = new HashMap();
    for (int i = 0; i < _allDefaultLevels.length; i++)
      _EventActionTypeColorMap.put(_allDefaultLevels[i], Color.BLACK);
  }
}