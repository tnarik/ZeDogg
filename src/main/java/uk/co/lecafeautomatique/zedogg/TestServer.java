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

public class TestServer {

  public class PeriodicProducer extends Thread {
    private MessageProducer producer = null;
    private Session session = null;

    public PeriodicProducer(Session s, MessageProducer p) {
      producer = p;
      session = s;
    }

    @Override
    public void run() {
      while (true) {
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

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
      }
    }

  }

  public TestServer() {
    BrokerService broker = new BrokerService();

    try {
      broker.addConnector("tcp://localhost:2222");
      broker.setUseMirroredQueues(true);
      broker.start();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public void doProducer() {
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:2222");
    Connection connection;
    try {
      connection = connectionFactory.createConnection();
      connection.start();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      Destination adminQueue = session.createQueue("test.queue.name");

      MessageProducer producer = session.createProducer(adminQueue);
      producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

      Thread t = new PeriodicProducer(session, producer);
      t.start();
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }

  /*
   * private EJBTestModule ejbModule; private ServletTestModule servletModule;
   * private MockQueue queue;
   * 
   * protected void setUp() throws Exception { super.setUp(); ejbModule =
   * createEJBTestModule(); ejbModule.bindToContext("java:ConnectionFactory",
   * getJMSMockObjectFactory().getMockQueueConnectionFactory()); queue =
   * getDestinationManager().createQueue("testQueue");
   * ejbModule.bindToContext("queue/testQueue", queue); servletModule =
   * createServletTestModule();
   * servletModule.createServlet(PrintMessageServlet.class); }
   * 
   * public void testInitPrintMessageReceiver() throws Exception {
   * verifyQueueConnectionStarted(); verifyNumberQueueSessions(1);
   * verifyNumberQueueReceivers(0, "testQueue", 1); QueueReceiver receiver =
   * getQueueTransmissionManager(0). getQueueReceiver("testQueue");
   * assertTrue(receiver.getMessageListener() instanceof PrintMessageListener);
   * }
   * 
   * public void testSendAndReceive() throws Exception {
   * servletModule.addRequestParameter("customerId", "1");
   * servletModule.doGet(); servletModule.addRequestParameter("customerId",
   * "2"); servletModule.doGet();
   * servletModule.addRequestParameter("customerId", "3");
   * servletModule.doGet(); verifyNumberOfReceivedQueueMessages("testQueue", 3);
   * verifyAllReceivedQueueMessagesAcknowledged("testQueue");
   * verifyReceivedQueueMessageEquals("testQueue", 0, new MockTextMessage("1"));
   * verifyReceivedQueueMessageEquals("testQueue", 1, new MockTextMessage("2"));
   * verifyReceivedQueueMessageEquals("testQueue", 2, new MockTextMessage("3"));
   * QueueSender sender = getQueueTransmissionManager(0).
   * createQueueSender(queue); sender.send(new MockObjectMessage(new
   * Integer(3))); verifyNumberOfReceivedQueueMessages("testQueue", 4);
   * verifyReceivedQueueMessageAcknowledged("testQueue", 3);
   * verifyNumberOfCurrentQueueMessages("testQueue", 0); }
   * 
   * public void testServletResponse() throws Exception {
   * servletModule.setCaseSensitive(false);
   * servletModule.addRequestParameter("customerId", "1");
   * servletModule.doGet(); servletModule.verifyOutputContains("successfully");
   * servletModule.clearOutput();
   * getJMSMockObjectFactory().getMockQueueConnectionFactory().
   * setJMSException(new JMSException("TestException")); servletModule.doGet();
   * servletModule.verifyOutputContains("error"); }
   */
}
