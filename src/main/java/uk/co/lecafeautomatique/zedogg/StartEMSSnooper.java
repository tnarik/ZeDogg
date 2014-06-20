package uk.co.lecafeautomatique.zedogg;

import uk.co.lecafeautomatique.zedogg.gui.GUI;
import uk.co.lecafeautomatique.zedogg.util.ems.EMSParameters;

import java.awt.Toolkit;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class StartEMSSnooper {
  public static final void main(String[] args) {
    int startAt = 0;

    String title = null;

    if (args.length != 0) {
      if (args[0].compareToIgnoreCase("-h") == 0) {
        System.err.println(" " + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version"));
        System.err.println("Usage: StartEMSSnooper [-title t] [ServerURL|User|Password|Topic1,Topic2] ...  ");
        System.err.println("Example: emssn00p.StartEMSSnooper \"tcp://localhost:7222|admin||a.>,c.x\" \"tcp:7500|7501||b.>,q.b\"  ");
        System.exit(-1);
      } else if (args[0].compareToIgnoreCase("-title") == 0) {
        title = args[1];
        startAt = 2;
      }
    }

    Set setListenersParam = new HashSet();
    if (args.length > 0) {
      for (int i = startAt; i < args.length; i++) {
        EMSParameters p = new EMSParameters();
        p.configure(args[i]);
        setListenersParam.add(p);
      }

      printBanner();
    }

    if (!checkJavaVersion()) {
      System.err.println("Warning: Java JRE Version 1.4.1 or higher is required");
    }

    GUI gui = new GUI(EventActionType.getAllDefaultLevels(), setListenersParam, title);

    gui.show();
  }

  protected static void printBanner() {
    System.out.print(" on " + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version"));
    System.out.print(" " + System.getProperty("os.name") + " " + System.getProperty("os.arch"));
    System.out.println(" " + System.getProperty("os.version"));
  }
  
  protected static int getDefaultMonitorWidth() {
    return 3 * getScreenWidth() / 4;
  }

  protected static int getDefaultMonitorHeight() {
    return 3 * getScreenHeight() / 4;
  }

  protected static int getScreenWidth() {
    try {
      return Toolkit.getDefaultToolkit().getScreenSize().width;
    } catch (Throwable t) {
    }
    return 800;
  }

  protected static int getScreenHeight() {
    try {
      return Toolkit.getDefaultToolkit().getScreenSize().height;
    } catch (Throwable t) {
    }
    return 600;
  }

  protected static boolean checkJavaVersion() {
    String ver = System.getProperty("java.version");
    boolean retVal = false;
    try {
      StringTokenizer st = new StringTokenizer(ver, "._-");

      int a = -1;

      a = Integer.parseInt(st.nextToken());
      int b = Integer.parseInt(st.nextToken());

      retVal = (a >= 1) && (b >= 4);
      if ((a == 1) && (b == 4))
        if (st.hasMoreTokens()) {
          int c = Integer.parseInt(st.nextToken());
          if (Integer.parseInt(st.nextToken()) >= 1)
            retVal = true;
          else
            retVal = false;
        } else {
          retVal = false;
        }
    } catch (Exception ex) {
      retVal = true;
    }

    return retVal;
  }
}
