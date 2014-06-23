package uk.co.lecafeautomatique.zedogg;

import java.util.Random;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.ServiceStopper;

public class TestServer {

  private PeriodicProducer threadProducer;
  private BrokerService broker;
  private Connection connection;

  public class PeriodicProducer extends Thread {
    private MessageProducer producer = null;
    private Session session = null;

    public PeriodicProducer(Session s, MessageProducer p) {
      producer = p;
      session = s;
    }

    @Override
    public void run() {
      while (session != null) {
        try {
          TextMessage txtMessage = session.createTextMessage();
          txtMessage.setText("MyProtocolMessage");
          Random random = new Random(System.currentTimeMillis());
          long randomLong = random.nextLong();
          String correlationId = Long.toHexString(randomLong);
          txtMessage.setJMSCorrelationID(correlationId);
          System.out.println("Thread is doing something");
          producer.send(txtMessage);
        } catch (Exception e) {
          e.printStackTrace();
        }
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    public void close() {
      try {
        session.close();
        producer.close();
        session = null;
      } catch (JMSException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public TestServer() {
    broker = new BrokerService();

    try {
      broker.addConnector("tcp://localhost:2222");
      broker.setUseMirroredQueues(true);
      broker.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void close() {
    try {
      if (threadProducer != null) threadProducer.close();
      if (connection != null) connection.close();
      if (broker != null) {
        broker.stopAllConnectors(new ServiceStopper());
        broker.stop();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void doProducer() {
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:2222");
    try {
      connection = connectionFactory.createConnection();
      connection.start();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      Destination adminQueue = session.createQueue("test.queue.name");

      MessageProducer producer = session.createProducer(adminQueue);
      producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

      threadProducer = new PeriodicProducer(session, producer);
      threadProducer.start();
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }

}
