package uk.co.lecafeautomatique.zedogg.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import uk.co.lecafeautomatique.zedogg.gui.GUI;
import uk.co.lecafeautomatique.zedogg.util.ems.EMSParameters;
import uk.co.lecafeautomatique.zedogg.util.ems.EMSController;
import javax.jms.JMSException;


public class CLI {

  @Parameter(names = { "-g", "--gui" }, description = "Starts in GUI mode")
  private boolean gui;
  
  @Parameter(names = { "-t", "--title" }, description = "Title")
  private String title;

  @Parameter(names = { "-v", "--version" }, description = "Displays the version")
  private boolean version;
  
  @Parameter(names = { "-h", "--help" }, description = "Displays this help", help = true)
  private boolean help;
  
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
    try{
      parser.parse(args);
    } catch (ParameterException e) {
      System.err.println(e.getMessage());
      System.err.println(help(parser));
      System.exit(1);
    }

    Set<EMSParameters> setListenersParam = new HashSet<EMSParameters>();
    for (String s : arguments) {
      EMSParameters p = new EMSParameters();
      p.configure(s);
      setListenersParam.add(p);
    }

    if (!checkJavaVersion()) {
      System.err.println("Warning: Java JRE Version 1.6.x or higher is required");
    }

    // Process options
    if ( help ) {
      System.err.println(help(parser));
      System.exit(1);
    }
    
    if ( version ) {
      printBanner();
    }
    

    if ( gui ) {
      GUI gui = new GUI(title);
      try {
        EMSController.startListeners(setListenersParam, gui);
      } catch (JMSException e) {
        e.printStackTrace();
      }
      gui.show();
    } else {
      System.out.println("Starting in command line mode");
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

  protected static boolean checkJavaVersion() {
    String ver = System.getProperty("java.version");
    StringTokenizer st = new StringTokenizer(ver, "._-");
    int a = Integer.parseInt(st.nextToken());
    int b = Integer.parseInt(st.nextToken());
    return (a >= 1) && (b >= 6);
  }
}
