package uk.co.lecafeautomatique.zedogg;

import uk.co.lecafeautomatique.zedogg.util.ems.EMSParameters;
import uk.co.lecafeautomatique.zedogg.viewer.RvSnooperGUI;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class StartEMSSnooper {
  public static final void main(String[] args) {
    int startAt = 0;

    String title = null;

    if (args.length != 0) {
      if (args[0].compareToIgnoreCase("-h") == 0) {
        System.err.print("EMSSn00p v2.0.3");
        System.err.println(" " + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version"));
        System.err.println("Usage: emssn00p.StartEMSSnooper [-title t] [ServerURL|User|Password|Topic1,Topic2] ...  ");
        System.err
            .println("Example: emssn00p.StartEMSSnooper \"tcp://localhost:7222|admin||a.>,c.x\" \"tcp:7500|7501||b.>,q.b\"  ");
        System.exit(-1);
      } else if (args[0].compareToIgnoreCase("-title") == 0) {
        title = args[1];
        startAt = 2;
      }

    }

    Set setRvListenersParam = new HashSet();
    if (args.length > 0) {
      for (int iarg = startAt; args.length > iarg; iarg++) {
        EMSParameters p = new EMSParameters();
        p.configureByLineString(args[iarg]);
        setRvListenersParam.add(p);
      }

      System.out.print("EMSSn00p v2.0.3");
      System.out.print(" on " + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version"));
      System.out.print(" " + System.getProperty("os.name") + " " + System.getProperty("os.arch"));
      System.out.println(" " + System.getProperty("os.version"));
    }

    if (!checkJavaVersion()) {
      System.err.println("Warning: Java JRE Version 1.4.1 or higher is required");
    }

    RvSnooperGUI gui = new RvSnooperGUI(EventActionType.getAllDefaultLevels(), setRvListenersParam, title);

    gui.show();
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
        if (st.hasMoreTokens() == true) {
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
