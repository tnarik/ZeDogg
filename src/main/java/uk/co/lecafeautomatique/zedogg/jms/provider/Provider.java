package uk.co.lecafeautomatique.zedogg.jms.provider;

public abstract class Provider {  
  public abstract String getTopicConnectionFactoryClassName();
  public abstract String getJMSMessageClassName();
  
  public static void register(Class<?> cl) {
    System.out.println("Register "+cl.getName());  
  }

}
