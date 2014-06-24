package uk.co.lecafeautomatique.zedogg.jms.provider;

public class EMS extends Provider {
  @Override
  public String getTopicConnectionFactoryClassName() {
    return "com.tibco.tibjms.TibjmsTopicConnectionFactory";
  }
}
