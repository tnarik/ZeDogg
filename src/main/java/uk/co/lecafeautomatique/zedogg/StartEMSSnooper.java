/*     */ package uk.co.lecafeautomatique.zedogg;
/*     */ 
/*     */ import uk.co.lecafeautomatique.zedogg.util.ems.EMSParameters;
/*     */ import uk.co.lecafeautomatique.zedogg.viewer.RvSnooperGUI;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Toolkit;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class StartEMSSnooper
/*     */ {
/*     */   public static final void main(String[] args)
/*     */   {
/*  55 */     int startAt = 0;
/*     */ 
/*  57 */     String title = null;
/*     */ 
/*  60 */     if (args.length != 0) {
/*  61 */       if (args[0].compareToIgnoreCase("-h") == 0) {
/*  62 */         System.err.print("EMSSn00p v2.0.3");
/*  63 */         System.err.println(" " + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version"));
/*  64 */         System.err.println("Usage: emssn00p.StartEMSSnooper [-title t] [ServerURL|User|Password|Topic1,Topic2] ...  ");
/*  65 */         System.err.println("Example: emssn00p.StartEMSSnooper \"tcp://localhost:7222|admin||a.>,c.x\" \"tcp:7500|7501||b.>,q.b\"  ");
/*  66 */         System.exit(-1);
/*  67 */       } else if (args[0].compareToIgnoreCase("-title") == 0) {
/*  68 */         title = args[1];
/*  69 */         startAt = 2;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  74 */     Set setRvListenersParam = new HashSet();
/*  75 */     if (args.length > 0) {
/*  76 */       for (int iarg = startAt; args.length > iarg; iarg++) {
/*  77 */         EMSParameters p = new EMSParameters();
/*  78 */         p.configureByLineString(args[iarg]);
/*  79 */         setRvListenersParam.add(p);
/*     */       }
/*     */ 
/*  82 */       System.out.print("EMSSn00p v2.0.3");
/*  83 */       System.out.print(" on " + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version"));
/*  84 */       System.out.print(" " + System.getProperty("os.name") + " " + System.getProperty("os.arch"));
/*  85 */       System.out.println(" " + System.getProperty("os.version"));
/*     */     }
/*     */ 
/*  88 */     if (!checkJavaVersion()) {
/*  89 */       System.err.println("Warning: Java JRE Version 1.4.1 or higher is required");
/*     */     }
/*     */ 
/*  92 */     RvSnooperGUI gui = new RvSnooperGUI(EventActionType.getAllDefaultLevels(), setRvListenersParam, title);
/*     */ 
/*  95 */     gui.show();
/*     */   }
/*     */ 
/*     */   protected static int getDefaultMonitorWidth()
/*     */   {
/* 104 */     return 3 * getScreenWidth() / 4;
/*     */   }
/*     */ 
/*     */   protected static int getDefaultMonitorHeight() {
/* 108 */     return 3 * getScreenHeight() / 4;
/*     */   }
/*     */ 
/*     */   protected static int getScreenWidth()
/*     */   {
/*     */     try
/*     */     {
/* 118 */       return Toolkit.getDefaultToolkit().getScreenSize().width; } catch (Throwable t) {
/*     */     }
/* 120 */     return 800;
/*     */   }
/*     */ 
/*     */   protected static int getScreenHeight()
/*     */   {
/*     */     try
/*     */     {
/* 131 */       return Toolkit.getDefaultToolkit().getScreenSize().height; } catch (Throwable t) {
/*     */     }
/* 133 */     return 600;
/*     */   }
/*     */ 
/*     */   protected static boolean checkJavaVersion()
/*     */   {
/* 145 */     String ver = System.getProperty("java.version");
/* 146 */     boolean retVal = false;
/*     */     try {
/* 148 */       StringTokenizer st = new StringTokenizer(ver, "._-");
/*     */ 
/* 150 */       int a = -1;
/*     */ 
/* 152 */       a = Integer.parseInt(st.nextToken());
/* 153 */       int b = Integer.parseInt(st.nextToken());
/*     */ 
/* 155 */       retVal = (a >= 1) && (b >= 4);
/* 156 */       if ((a == 1) && (b == 4))
/* 157 */         if (st.hasMoreTokens() == true)
/*     */         {
/* 159 */           int c = Integer.parseInt(st.nextToken());
/* 160 */           if (Integer.parseInt(st.nextToken()) >= 1)
/* 161 */             retVal = true;
/*     */           else
/* 163 */             retVal = false;
/*     */         }
/*     */         else {
/* 166 */           retVal = false;
/*     */         }
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 171 */       retVal = true;
/*     */     }
/*     */ 
/* 174 */     return retVal;
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.StartEMSSnooper
 * JD-Core Version:    0.6.1
 */
