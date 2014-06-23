package uk.co.lecafeautomatique.zedogg.jms;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

public class JMSController {
 
  private boolean paused = false;
  private Map<JMSParameters, TopicConnection> JMSConnections = new HashMap();

  public JMSController() {  
  }
  
  public synchronized TopicConnection getTopicConnection(JMSParameters p) throws JMSException {
    try {
      if (JMSConnections.containsKey(p)) return JMSConnections.get(p);

      TopicConnectionFactory factory = null;

      //Class topicConnectionFactoryClass = Class.forName("com.tibco.tibjms.TibjmsTopicConnectionFactory");
      Class topicConnectionFactoryClass = Class.forName("org.apache.activemq.ActiveMQConnectionFactory");
      Constructor constructors[] = topicConnectionFactoryClass.getDeclaredConstructors();
      for (Constructor ctor : constructors) {
        Class<?>[] pType = ctor.getParameterTypes();
        if ((pType.length == 1) && (pType[0].equals(java.lang.String.class))) {
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

  public synchronized void startListener(JMSParameters p, MessageListener callback) throws JMSException {
    if (JMSConnections.containsKey(p)) {
      Iterator i = JMSConnections.keySet().iterator();

      while (i.hasNext()) {
        JMSParameters par = (JMSParameters) i.next();
        if (par.equals(p)) par.setTopics(p.getTopics());
      }
    }

    Set s = p.getTopics();
    Iterator i = s.iterator();
    while (i.hasNext()) {
      String n = (String) i.next();
      TopicConnection connection = getTopicConnection(p);
      String id = String.valueOf(connection.hashCode() + n); //this leaks one

      if (!JMSConnections.containsKey(id)) {
        TopicSession session = connection.createTopicSession(false, 1);
        TopicSubscriber subscriber = session.createSubscriber(session.createTopic(n));
        subscriber.setMessageListener(callback);
        connection.start();
        JMSConnections.put(p, connection);
      }
    }
  }

  public synchronized Set getTransports() {
    return JMSConnections.keySet();
  }

  public synchronized void stopListener(JMSParameters p) throws JMSException {
    if (JMSConnections.containsKey(p)) {
      TopicSubscriber lsnr = (TopicSubscriber) JMSConnections.get(p);
      lsnr.close();
      JMSConnections.remove(p);
    }
  }

  public synchronized void shutdownAll() throws JMSException {
    Iterator<TopicConnection> i = JMSConnections.values().iterator();
    while ((i!=null) && i.hasNext()) {
        TopicConnection tc = i.next();
        tc.stop();
        tc.close();
    }

    JMSConnections.clear();
  }
  
  public synchronized void pauseAll() throws JMSException {
    Iterator<TopicConnection> i = JMSConnections.values().iterator();

    while ((i!=null) && i.hasNext()) {
        TopicConnection tc = i.next();
        tc.stop();
    }
    pause();
  }
  
  public synchronized void resumeAll() throws JMSException {
    Iterator<TopicConnection> i = JMSConnections.values().iterator();

    while ((i!=null) && i.hasNext()) {
      (i.next()).start();
    }
    resume();
  }
  
  public void pause() {
    paused = true;
  }
  
  public void resume() {
    paused = false;
  }
  
  public boolean isPaused() {
    return paused;
  }
  
  public void startListeners(Set listeners, MessageListener callback) throws JMSException {
    Iterator itrl = listeners.iterator();
    while (itrl.hasNext()) {
      try {
        JMSParameters p = (JMSParameters) itrl.next();
        startListener(p, callback);
      } catch (ClassCastException ex) {
        throw ex;
      } catch (JMSException ex) {
        throw ex;
      }

    }
  }

}
