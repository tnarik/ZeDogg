package uk.co.lecafeautomatique.zedogg.gui.configure;

import uk.co.lecafeautomatique.zedogg.util.ems.EMSParameters;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class MRUListnerManager {
  private static final String CONFIG_FILE_NAME = "mru_listner_manager";
  private static final int DEFAULT_MAX_SIZE = 3;
  private int _maxSize = 0;
  private LinkedList<EMSParameters> _mruListnerList;

  public MRUListnerManager() {
    this(3);
  }

  public MRUListnerManager(int maxSize) {
    load();
    setMaxSize(maxSize);
  }

  public int size() {
    return this._mruListnerList.size();
  }

  public Object getListner(int index) {
    if (index < size()) {
      return this._mruListnerList.get(index);
    }

    return null;
  }

  public InputStream getInputStream(int index) throws IOException, FileNotFoundException {
    if (index < size()) {
      Object o = getListner(index);
      if ((o instanceof File)) {
        return getInputStream((File) o);
      }
      return getInputStream((URL) o);
    }

    return null;
  }

  public void set(EMSParameters p) {
    setMRU(p);
  }

  public String[] getMRUFileList() {
    if (size() == 0) {
      return null;
    }

    String[] ss = new String[size()];

    for (int i = 0; i < size(); i++) {
      String Tstring = new String();
      EMSParameters p = (EMSParameters) getListner(i);
      Tstring = Tstring + p.getServerURL();
      Tstring = Tstring + "|";
      Tstring = Tstring + p.getTopic();

      ss[i] = Tstring;
    }

    return ss;
  }

  public void moveToTop(int index) {
    this._mruListnerList.add(0, this._mruListnerList.remove(index));
  }


  protected InputStream getInputStream(File file) throws IOException, FileNotFoundException {
    BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));

    return reader;
  }

  protected InputStream getInputStream(URL url) throws IOException {
    return url.openStream();
  }

  protected void setMRU(EMSParameters emso) {
    int index = this._mruListnerList.indexOf(emso);

    if (index == -1) {
      this._mruListnerList.add(0, emso);
      setMaxSize(this._maxSize);
    } else {
      moveToTop(index);
    }
  }

  protected void load() {
    this._mruListnerList = new LinkedList();
  }


  protected void setMaxSize(int maxSize) {
    if (maxSize < this._mruListnerList.size()) {
      for (int i = 0; i < this._mruListnerList.size() - maxSize; i++) {
        this._mruListnerList.removeLast();
      }
    }

    this._maxSize = maxSize;
  }
}
