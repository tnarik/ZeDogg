package uk.co.lecafeautomatique.zedogg.jms;

import java.lang.reflect.Constructor;

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

public class JMSController {
  protected static final Map<JMSParameters, TopicConnection> _mapEMSConnections = new HashMap();

  protected static boolean _isPaused = false;

  public static synchronized TopicConnection getTopicConnection(JMSParameters p) throws JMSException {
    try {
      if (_mapEMSConnections.containsKey(p)) {
        return _mapEMSConnections.get(p);
      }

      TopicConnectionFactory factory = null;

      //Class topicConnectionFactoryClass = Class.forName("com.tibco.tibjms.TibjmsTopicConnectionFactory");
      Class topicConnectionFactoryClass = Class.forName("org.apache.activemq.ActiveMQConnectionFactory");
      Constructor constructors[] = topicConnectionFactoryClass.getDeclaredConstructors();
      for (Constructor ctor : constructors) {
        Class<?>[] pType = ctor.getParameterTypes();
        if ((pType.length == 1) && (pType[0].equals(java.lang.String.class))) {
//          System.out.println("DEBUG create factory "+ctor);
          factory = (TopicConnectionFactory) ctor.newInstance(p.getServerURL());
          break;
        }
      }

      return factory.createTopicConnection(p.getUserName(), p.getPassword());
    } catch (JMSException ex) {
      throw ex;
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
      return null;
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static synchronized void startListener(JMSParameters p, MessageListener callback) throws JMSException {
    System.out.println("DEBUG startListener");

    if (_mapEMSConnections.containsKey(p)) {
      Iterator i = _mapEMSConnections.keySet().iterator();

      while (i.hasNext()) {
        JMSParameters par = (JMSParameters) i.next();
        if (par.equals(p)) {
          System.out.println("DEBUG startListener : "+p);
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
        System.out.println("DEBUG startListener new : "+p);
        System.out.println("DEBUG startListener callback is : "+callback);

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

  public static synchronized void stopListener(JMSParameters p) throws JMSException {
    if (_mapEMSConnections.containsKey(p)) {
      TopicSubscriber lsnr = (TopicSubscriber) _mapEMSConnections.get(p);
      lsnr.close();

      _mapEMSConnections.remove(p);
    }
  }

  public static synchronized void shutdownAll() throws JMSException {
    Iterator<TopicConnection> i = _mapEMSConnections.values().iterator();
    while ((i!=null) && i.hasNext()) {
        TopicConnection tc = i.next();
        tc.stop();
        tc.close();
    }

    _mapEMSConnections.clear();
  }

  public static synchronized void pauseAll() throws JMSException {
    Iterator<TopicConnection> i = _mapEMSConnections.values().iterator();

    while ((i!=null) && i.hasNext()) {
        TopicConnection tc = i.next();
        tc.stop();
    }
    pause();
  }

  public static synchronized void resumeAll() throws JMSException {
    Iterator<TopicConnection> i = _mapEMSConnections.values().iterator();

    while ((i!=null) && i.hasNext()) {
      (i.next()).start();
    }
    resume();
  }

  public static void pause() {
    _isPaused = true;
  }

  public static void resume() {
    _isPaused = false;
  }

  public static boolean isPaused() {
    return _isPaused;
  }

  public static void startListeners(Set listeners, MessageListener callback) throws JMSException {
    Iterator itrl = listeners.iterator();
    while (itrl.hasNext()) {
      try {
        JMSParameters p = (JMSParameters) itrl.next();

        p.setDescription(" <a href=\"" + uk.co.lecafeautomatique.zedogg.gui.GUI.URL + "\">" + 
            uk.co.lecafeautomatique.zedogg.gui.GUI.NAME + " " + uk.co.lecafeautomatique.zedogg.gui.GUI.VERSION + "</a> ");

        JMSController.startListener(p, callback);
      } catch (ClassCastException ex) {
        throw ex;
      } catch (JMSException ex) {
        throw ex;
      }

    }
  }

}
