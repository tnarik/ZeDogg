package uk.co.lecafeautomatique.zedogg.jms.provider;

public class EMS extends Provider {
  static {
    Provider.register(EMS.class);
  }

  @Override
  public String getTopicConnectionFactoryClassName() {
    return "com.tibco.tibjms.TibjmsTopicConnectionFactory";
  }
  
  @Override
  public String getJMSMessageClassName() {
    return "com.tibco.tibjms.Tibjms";
  }

}
