package uk.co.lecafeautomatique.zedogg.util.ems;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

public class EMSController {
  protected static final Map<EMSParameters, TopicConnection> _mapEMSConnections = new HashMap();

  public static synchronized TopicConnection getTopicConnection(EMSParameters p) throws JMSException {
    try {
      if (_mapEMSConnections.containsKey(p)) {
        return (TopicConnection) _mapEMSConnections.get(p);
      }

      TopicConnectionFactory factory = null;

      Class topicConnectionFactoryClass = Class.forName("com.tibco.tibjms.TibjmsTopicConnectionFactory");
      Constructor constructors[] = topicConnectionFactoryClass.getDeclaredConstructors();
      for (Constructor ctor : constructors) {
        Class<?>[] pType = ctor.getParameterTypes();
        if ((pType.length == 1) && (pType[0].equals(java.lang.String.class))) {
          System.out.println(ctor);
          factory = (TopicConnectionFactory) ctor.newInstance(p.getServerURL());
          break;
        }
      }

      return factory.createTopicConnection(p.getUserName(), p.getPassword());
    } catch (JMSException ex) {
      throw ex;
    } catch (ClassNotFoundException ex) {
      return null;
    } catch (Exception ex) {
      return null;
    }
  }

  public static synchronized void startListener(EMSParameters p, MessageListener callback) throws JMSException {
    if (_mapEMSConnections.containsKey(p)) {
      Iterator i = _mapEMSConnections.keySet().iterator();

      while (i.hasNext()) {
        EMSParameters par = (EMSParameters) i.next();
        if (par.equals(p)) {
          par.setTopics(p.getTopics());
        }
      }
    }

    Set s = p.getTopics();
    Iterator i = s.iterator();
    while (i.hasNext()) {
      String n = (String) i.next();
      String id = String.valueOf(getTopicConnection(p).hashCode() + n);

      if (!_mapEMSConnections.containsKey(id)) {
        TopicConnection connection = getTopicConnection(p);

        TopicSession session = connection.createTopicSession(false, 1);

        TopicSubscriber subscriber = session.createSubscriber(session.createTopic(n));
        subscriber.setMessageListener(callback);
        connection.start();

        _mapEMSConnections.put(p, connection);
      }
    }
  }

  public static synchronized Set getListeners() {
    return _mapEMSConnections.keySet();
  }

  public static synchronized Set getTransports() {
    return _mapEMSConnections.keySet();
  }

  public static synchronized void stopListener(EMSParameters p) throws JMSException {
    if (_mapEMSConnections.containsKey(p)) {
      TopicSubscriber lsnr = (TopicSubscriber) _mapEMSConnections.get(p);
      lsnr.close();

      _mapEMSConnections.remove(p.getTopic());
    }
  }

  public static synchronized void shutdownAll() throws JMSException {
    Iterator i = _mapEMSConnections.values().iterator();

    while (i.hasNext()) {
      if (i != null) {
        TopicConnection tc = (TopicConnection) i.next();

        tc.stop();
        tc.close();
      }

    }

    _mapEMSConnections.clear();
  }

  public static synchronized void pauseAll() throws JMSException {
    Iterator i = _mapEMSConnections.values().iterator();

    while (i.hasNext())
      if (i != null) {
        TopicConnection tc = (TopicConnection) i.next();

        tc.stop();
      }
  }

  public static synchronized void resumeAll() throws JMSException {
    Iterator i = _mapEMSConnections.values().iterator();

    while (i.hasNext())
      if (i != null)
        ((TopicConnection) i.next()).start();
  }
}
