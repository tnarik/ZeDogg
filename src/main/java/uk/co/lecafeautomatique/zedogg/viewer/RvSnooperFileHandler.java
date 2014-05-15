package uk.co.lecafeautomatique.zedogg.viewer;

import uk.co.lecafeautomatique.zedogg.util.DateFormatManager;
import java.awt.FileDialog;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class RvSnooperFileHandler
{
  static void saveMsgAsTextFile(String sSubject, String sMsg, String infostr, JFrame jdBase, JLabel statusLabel)
  {
    File f = null;
    FileWriter writer = null;
    BufferedWriter buf_writer = null;
    try {
      FileDialog fd = new FileDialog(jdBase, "Save text File", 1);

      sSubject.lastIndexOf(".");

      String sFileName = sSubject.substring(sSubject.lastIndexOf(".") + 1);
      fd.setFile(sFileName + ".txt");
      fd.setVisible(true);
      String filename = fd.getDirectory() + fd.getFile();

      if (fd.getFile() != null) {
        f = new File(filename);
        f.createNewFile();

        writer = new FileWriter(f);
        buf_writer = new BufferedWriter(writer);
        buf_writer.write(sMsg);
        statusLabel.setText("Saved text file " + f.toString());
      }
    } catch (Exception ex) {
      RvSnooperErrorDialog error = new RvSnooperErrorDialog(jdBase, "File save error " + ex.getMessage());
    }
    finally
    {
      RvSnooperErrorDialog error;
      if (buf_writer != null)
        try {
          buf_writer.close();
        }
        catch (IOException e1)
        {
        }
      if (writer != null)
        try {
          writer.close();
        }
        catch (IOException e1)
        {
        }
      if (f != null)
        f = null;
    }
  }

  static void saveTableToTextFile(String infostr, JFrame jdBase, JLabel statusLabel, LogTable table)
  {
    File f = null;
    FileWriter writer = null;
    BufferedWriter buf_writer = null;
    try {
      FileDialog fd = new FileDialog(jdBase, "Save text File", 1);

      fd.setFile("*.txt");
      fd.setVisible(true);
      String filename = fd.getDirectory() + fd.getFile();

      if (fd.getFile() != null)
      {
        f = new File(filename);

        f.createNewFile();

        writer = new FileWriter(f);
        buf_writer = new BufferedWriter(writer);

        buf_writer.write(table.getFilteredLogTableModel().createFilteredTextFromMsg().toString());
        statusLabel.setText("Saved text file " + f.toString());
      }
    }
    catch (Exception ex) {
      RvSnooperErrorDialog error = new RvSnooperErrorDialog(jdBase, "File save error " + ex.getMessage());
    }
    finally
    {
      RvSnooperErrorDialog error;
      if (buf_writer != null)
        try {
          buf_writer.close();
        }
        catch (IOException e1)
        {
        }
      if (writer != null) {
        try {
          writer.close();
        }
        catch (IOException e1)
        {
        }
      }
      if (f != null)
        f = null;
    }
  }

  static void saveTableToHtml(String sVersion, String sURL, JFrame jfBase, JLabel statusLabel, LogTable table)
  {
    File f = null;
    FileWriter writer = null;
    BufferedWriter buf_writer = null;
    try {
      FileDialog fd = new FileDialog(jfBase, "Save HTML File", 1);

      fd.setFile("*.html");
      fd.setVisible(true);
      String filename = fd.getDirectory() + fd.getFile();

      if (fd.getFile() != null)
      {
        f = new File(filename);

        f.createNewFile();

        writer = new FileWriter(f);
        buf_writer = new BufferedWriter(writer);
        DateFormatManager dfm = new DateFormatManager("yyyy-MM-dd HH:mm:ss.S");
        buf_writer.write("<html><head>\n");
        buf_writer.write("<title>EMSSn00p HTML Output Page </title>\n");
        buf_writer.write("<META http-equiv=\"content-type\" content=\"text/html;\" charset=" + System.getProperty("file.encoding") + "\">");
        buf_writer.write("\n<META NAME=\"description\" CONTENT=\"rvsn00p html output file.\">");
        buf_writer.write("\n<META NAME=\"keywords\" CONTENT=\"rvsn00p,tibco,rendezvous\">");
        buf_writer.write("\n<META NAME=\"Author\" CONTENT=\"" + System.getProperty("user.name", "unknown") + "\">");
        buf_writer.write("\n<META NAME=\"Creation_Date\" CONTENT=\"" + new Date() + "\">");
        buf_writer.write("</head>\n<body>\n");
        buf_writer.write("Generated by <a href=\"" + sURL + "\">" + sVersion + "</a> on " + new Date());

        buf_writer.write(table.getFilteredLogTableModel().createFilteredHTMLTable(dfm).toString());

        buf_writer.write("\n</body>\n</html>");
        statusLabel.setText("Saved HTML file " + f.toString());
      }
    } catch (Exception ex) {
      RvSnooperErrorDialog error = new RvSnooperErrorDialog(jfBase, "File save error " + ex.getMessage());
    }
    finally
    {
      RvSnooperErrorDialog error;
      if (buf_writer != null)
        try {
          buf_writer.close();
        }
        catch (IOException e1)
        {
        }
      if (writer != null) {
        try {
          writer.close();
        }
        catch (IOException e1)
        {
        }
      }
      if (f != null)
        f = null;
    }
  }
}
