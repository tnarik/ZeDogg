package uk.co.lecafeautomatique.zedogg.jms.provider;

public class ActiveMQ extends Provider {
  static {
    Provider.register(ActiveMQ.class);
  }
  
  @Override
  public String getTopicConnectionFactoryClassName() {
    return "org.apache.activemq.ActiveMQConnectionFactory";
  }
  
  @Override
  public String getJMSMessageClassName() {
    return "org.apache.activemq.command.ActiveMQMessage";
  }

}
