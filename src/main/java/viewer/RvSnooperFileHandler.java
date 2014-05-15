/*     */ package emssn00p.viewer;
/*     */ 
/*     */ import emssn00p.util.DateFormatManager;
/*     */ import java.awt.FileDialog;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.util.Date;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ 
/*     */ public class RvSnooperFileHandler
/*     */ {
/*     */   static void saveMsgAsTextFile(String sSubject, String sMsg, String infostr, JFrame jdBase, JLabel statusLabel)
/*     */   {
/*  36 */     File f = null;
/*  37 */     FileWriter writer = null;
/*  38 */     BufferedWriter buf_writer = null;
/*     */     try {
/*  40 */       FileDialog fd = new FileDialog(jdBase, "Save text File", 1);
/*     */ 
/*  44 */       sSubject.lastIndexOf(".");
/*     */ 
/*  46 */       String sFileName = sSubject.substring(sSubject.lastIndexOf(".") + 1);
/*  47 */       fd.setFile(sFileName + ".txt");
/*  48 */       fd.setVisible(true);
/*  49 */       String filename = fd.getDirectory() + fd.getFile();
/*     */ 
/*  52 */       if (fd.getFile() != null) {
/*  53 */         f = new File(filename);
/*  54 */         f.createNewFile();
/*     */ 
/*  56 */         writer = new FileWriter(f);
/*  57 */         buf_writer = new BufferedWriter(writer);
/*  58 */         buf_writer.write(sMsg);
/*  59 */         statusLabel.setText("Saved text file " + f.toString());
/*     */       }
/*     */     } catch (Exception ex) {
/*  62 */       RvSnooperErrorDialog error = new RvSnooperErrorDialog(jdBase, "File save error " + ex.getMessage());
/*     */     }
/*     */     finally
/*     */     {
/*     */       RvSnooperErrorDialog error;
/*  66 */       if (buf_writer != null)
/*     */         try {
/*  68 */           buf_writer.close();
/*     */         }
/*     */         catch (IOException e1)
/*     */         {
/*     */         }
/*  73 */       if (writer != null)
/*     */         try {
/*  75 */           writer.close();
/*     */         }
/*     */         catch (IOException e1)
/*     */         {
/*     */         }
/*  80 */       if (f != null)
/*  81 */         f = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   static void saveTableToTextFile(String infostr, JFrame jdBase, JLabel statusLabel, LogTable table)
/*     */   {
/*  94 */     File f = null;
/*  95 */     FileWriter writer = null;
/*  96 */     BufferedWriter buf_writer = null;
/*     */     try {
/*  98 */       FileDialog fd = new FileDialog(jdBase, "Save text File", 1);
/*     */ 
/* 100 */       fd.setFile("*.txt");
/* 101 */       fd.setVisible(true);
/* 102 */       String filename = fd.getDirectory() + fd.getFile();
/*     */ 
/* 104 */       if (fd.getFile() != null)
/*     */       {
/* 106 */         f = new File(filename);
/*     */ 
/* 108 */         f.createNewFile();
/*     */ 
/* 110 */         writer = new FileWriter(f);
/* 111 */         buf_writer = new BufferedWriter(writer);
/*     */ 
/* 113 */         buf_writer.write(table.getFilteredLogTableModel().createFilteredTextFromMsg().toString());
/* 114 */         statusLabel.setText("Saved text file " + f.toString());
/*     */       }
/*     */     }
/*     */     catch (Exception ex) {
/* 118 */       RvSnooperErrorDialog error = new RvSnooperErrorDialog(jdBase, "File save error " + ex.getMessage());
/*     */     }
/*     */     finally
/*     */     {
/*     */       RvSnooperErrorDialog error;
/* 122 */       if (buf_writer != null)
/*     */         try {
/* 124 */           buf_writer.close();
/*     */         }
/*     */         catch (IOException e1)
/*     */         {
/*     */         }
/* 129 */       if (writer != null) {
/*     */         try {
/* 131 */           writer.close();
/*     */         }
/*     */         catch (IOException e1)
/*     */         {
/*     */         }
/*     */       }
/* 137 */       if (f != null)
/* 138 */         f = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   static void saveTableToHtml(String sVersion, String sURL, JFrame jfBase, JLabel statusLabel, LogTable table)
/*     */   {
/* 151 */     File f = null;
/* 152 */     FileWriter writer = null;
/* 153 */     BufferedWriter buf_writer = null;
/*     */     try {
/* 155 */       FileDialog fd = new FileDialog(jfBase, "Save HTML File", 1);
/*     */ 
/* 157 */       fd.setFile("*.html");
/* 158 */       fd.setVisible(true);
/* 159 */       String filename = fd.getDirectory() + fd.getFile();
/*     */ 
/* 161 */       if (fd.getFile() != null)
/*     */       {
/* 163 */         f = new File(filename);
/*     */ 
/* 165 */         f.createNewFile();
/*     */ 
/* 167 */         writer = new FileWriter(f);
/* 168 */         buf_writer = new BufferedWriter(writer);
/* 169 */         DateFormatManager dfm = new DateFormatManager("yyyy-MM-dd HH:mm:ss.S");
/* 170 */         buf_writer.write("<html><head>\n");
/* 171 */         buf_writer.write("<title>EMSSn00p HTML Output Page </title>\n");
/* 172 */         buf_writer.write("<META http-equiv=\"content-type\" content=\"text/html;\" charset=" + System.getProperty("file.encoding") + "\">");
/* 173 */         buf_writer.write("\n<META NAME=\"description\" CONTENT=\"rvsn00p html output file.\">");
/* 174 */         buf_writer.write("\n<META NAME=\"keywords\" CONTENT=\"rvsn00p,tibco,rendezvous\">");
/* 175 */         buf_writer.write("\n<META NAME=\"Author\" CONTENT=\"" + System.getProperty("user.name", "unknown") + "\">");
/* 176 */         buf_writer.write("\n<META NAME=\"Creation_Date\" CONTENT=\"" + new Date() + "\">");
/* 177 */         buf_writer.write("</head>\n<body>\n");
/* 178 */         buf_writer.write("Generated by <a href=\"" + sURL + "\">" + sVersion + "</a> on " + new Date());
/*     */ 
/* 180 */         buf_writer.write(table.getFilteredLogTableModel().createFilteredHTMLTable(dfm).toString());
/*     */ 
/* 182 */         buf_writer.write("\n</body>\n</html>");
/* 183 */         statusLabel.setText("Saved HTML file " + f.toString());
/*     */       }
/*     */     } catch (Exception ex) {
/* 186 */       RvSnooperErrorDialog error = new RvSnooperErrorDialog(jfBase, "File save error " + ex.getMessage());
/*     */     }
/*     */     finally
/*     */     {
/*     */       RvSnooperErrorDialog error;
/* 190 */       if (buf_writer != null)
/*     */         try {
/* 192 */           buf_writer.close();
/*     */         }
/*     */         catch (IOException e1)
/*     */         {
/*     */         }
/* 197 */       if (writer != null) {
/*     */         try {
/* 199 */           writer.close();
/*     */         }
/*     */         catch (IOException e1)
/*     */         {
/*     */         }
/*     */       }
/* 205 */       if (f != null)
/* 206 */         f = null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.RvSnooperFileHandler
 * JD-Core Version:    0.6.1
 */