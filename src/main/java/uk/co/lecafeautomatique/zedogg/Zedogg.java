package uk.co.lecafeautomatique.zedogg;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import com.google.gson.Gson;

import uk.co.lecafeautomatique.zedogg.jms.JMSController;
import uk.co.lecafeautomatique.zedogg.jms.JMSParameters;
import uk.co.lecafeautomatique.zedogg.jms.LogRecord;
import uk.co.lecafeautomatique.zedogg.jms.provider.Provider;

public class Zedogg implements MessageListener {
  private JMSController jmsController;
  private List<MessageListener> messageListeners = new ArrayList<MessageListener>(5);

  protected List<LogRecord> records = new ArrayList<LogRecord>(1000);

  private Map<String, Provider> jmsProviders = new HashMap();
  
  class Data0 {
    String symbol;
    String className;
  }

  public Zedogg(String provider) {
    Gson gson = new Gson();
    Data0[] data = gson.fromJson(
        new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("providers.json")), Data0[].class);
    for (Data0 d : data) {
      System.out.println("this is " + d.symbol + " for class: " + d.className);
      // JMS Providers loading loop
      try {
        jmsProviders.put(d.symbol, (Provider)Class.forName(d.className).newInstance());
      } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }

    jmsController = new JMSController(this, provider);
  }

  public Provider getProvider(String provider) {
    return jmsProviders.get(provider);
  }

  public void onMessage(Message msg) {
    if (isPaused())
      return;

    LogRecord r = LogRecord.create(msg);
    records.add(r);

    for (MessageListener ml : messageListeners) {
      ml.onMessage(msg);
    }
  }

  public int recordCount() {
    return records.size();
  }

  public void attach(MessageListener ml) {
    messageListeners.add(ml);
  }

  public void listen(Set p) throws JMSException {
    jmsController.startListeners(p, this);
  }

  public void listen(JMSParameters p) throws JMSException {
    jmsController.startListener(p, this);
  }

  public boolean isPaused() {
    return jmsController.isPaused();
  }

  public void resume() throws JMSException {
    jmsController.resumeAll();
  }

  public void pause() throws JMSException {
    jmsController.pauseAll();
  }

  public void shutdown() throws JMSException {
    jmsController.shutdownAll();
  }

  public Set getTransports() {
    return jmsController.getTransports();
  }
}
