package uk.co.lecafeautomatique.zedogg.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import uk.co.lecafeautomatique.zedogg.TestServer;
import uk.co.lecafeautomatique.zedogg.Zedogg;
import uk.co.lecafeautomatique.zedogg.gui.GUI;
import uk.co.lecafeautomatique.zedogg.jms.JMSParameters;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import jline.console.ConsoleReader;

public class CLI implements MessageListener {

  @Parameter(names = { "-g", "--gui" }, description = "Starts in GUI mode")
  private boolean gui;

  @Parameter(names = { "-t", "--testServer" }, description = "Start Test server")
  private boolean testServer;

  @Parameter(names = { "-v", "--version" }, description = "Displays the version")
  private boolean version;

  @Parameter(names = { "-h", "--help" }, description = "Displays this help", help = true)
  private boolean help;

  @Parameter(names = { "-p", "--provider" }, description = "Use this server type [tibco|activemq]") 
  private String provider = "activemq";
  
  // receives other command line parameters than options
  @Parameter
  private List<String> arguments = new ArrayList<String>();

  public static final void main(String[] args) {
    new CLI().doMain(args);
  }

  public void doMain(String[] args) {
    // Parse the command line arguments and options
    JCommander parser = new JCommander(this);
    parser.setProgramName("CLI");
    try {
      parser.parse(args);
    } catch (ParameterException e) {
      System.err.println(e.getMessage());
      System.err.println(help(parser));
      System.exit(1);
    }

    Set<JMSParameters> setListenersParam = new HashSet<JMSParameters>();
    for (String s : arguments) {
      JMSParameters p = new JMSParameters();
      p.configure(s);
      setListenersParam.add(p);
    }

    if (!isValidJavaVersion()) {
      System.err.println("Warning: Java JRE Version 1.6.x or higher is required");
    }

    // Process options
    if (help) {
      System.err.println(help(parser));
      System.exit(1);
    }

    if (version) {
      printBanner();
    }

    TestServer ts  = null;
    if (testServer) {
      ts = new TestServer();
      ts.doProducer();
    }

    Zedogg zeDogg = new Zedogg(provider);

    if (gui) {
      GUI gui = new GUI(zeDogg);
      try {
        zeDogg.attach(gui);
        zeDogg.listen(setListenersParam);
      } catch (JMSException e) {
        e.printStackTrace();
      }
      gui.show();
    } else {
      try {
        zeDogg.attach(this);
        zeDogg.listen(setListenersParam);
        try {
          ConsoleReader reader = new ConsoleReader();
          reader.setPrompt("zeDogg> ");
          String line = null;

          int character = 0;

          while ( (character = reader.readCharacter()) != -1 ) {
//          while ((line = reader.readLine()) != null) {
            if (character == 'n') {
              System.out.println(zeDogg.recordCount());
            }
            if (character == 'q') {
              zeDogg.shutdown();
              break;
            }
            if ((line != null) && line.equalsIgnoreCase("quit")) {
              zeDogg.shutdown();
              break;
            }
          }
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      } catch (JMSException e) {
        e.printStackTrace();
      }
    }
    
    if (testServer) {
      ts.close();
    }
  }

  protected static StringBuilder help(JCommander parser) {
    StringBuilder sb = new StringBuilder();
    parser.usage(sb);
    sb.append("\nExample: CLI \"tcp://localhost:7222|admin||a.>,c.x\" \"tcp:7500|7501||b.>,q.b\"  \n");
    return sb;
  }

  protected static void printBanner() {
    System.out.print(" on " + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version"));
    System.out.print(" " + System.getProperty("os.name") + " " + System.getProperty("os.arch"));
    System.out.println(" " + System.getProperty("os.version"));
  }

  protected static boolean isValidJavaVersion() {
    String ver = System.getProperty("java.version");
    StringTokenizer st = new StringTokenizer(ver, "._-");
    int a = Integer.parseInt(st.nextToken());
    int b = Integer.parseInt(st.nextToken());
    return (a >= 1) && (b >= 6);
  }

  public void onMessage(Message msg) {
    try {
      System.out.println(provider);
      System.out.println(msg.getJMSMessageID()+" " +msg.getJMSCorrelationID());
      
    } catch (JMSException e) {
      System.err.println(e.getMessage());
    }
  }
}
