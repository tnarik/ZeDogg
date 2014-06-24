package uk.co.lecafeautomatique.zedogg.jms.provider;

public class ActiveMQ extends Provider {
  @Override
  public String getTopicConnectionFactoryClassName() {
    return "org.apache.activemq.ActiveMQConnectionFactory";
  }
}
