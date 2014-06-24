package uk.co.lecafeautomatique.zedogg;

import java.util.ArrayList;

import java.util.List;

import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;




import uk.co.lecafeautomatique.zedogg.jms.JMSController;
import uk.co.lecafeautomatique.zedogg.jms.JMSParameters;
import uk.co.lecafeautomatique.zedogg.jms.LogRecord;

public class Zedogg implements MessageListener {
  private JMSController jmsController;
  
  private List<MessageListener> messageListeners = new ArrayList(5);
  
  protected List<LogRecord> records = new ArrayList(1000);

  public Zedogg(String provider) {
    jmsController = new JMSController(provider);
  }

  public void onMessage(Message msg) {
    if (isPaused()) return;

    LogRecord r = LogRecord.create(msg);
    records.add(r);
    
    for(MessageListener ml : messageListeners) {
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
