package uk.co.lecafeautomatique.zedogg.jms;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

public class JMSParameters implements Cloneable, Serializable {
  protected int _hashCode = 0;
  protected String _serverURL;
  protected String _userName;
  protected String _password;
  protected boolean _displayEMSParameters;
  protected Set<String> _topics;

  public JMSParameters() {
    // to simplify testing
    _serverURL = "tcp://localhost:2222";
    _userName = "";
    _password = "";
    _topics = new HashSet();;

    calcHashCode();
  }

  public JMSParameters(String serverurl, String name, String password,
      boolean displayEMSParameters, Set<String> topics) {
    _serverURL = serverurl;
    _userName = name;
    _password = password;
    _displayEMSParameters = displayEMSParameters;
    _topics = topics;
  }

  public JMSParameters(Set<String> topics, String serverurl, String name, boolean displayParameters, String password,
      String clientid) {
    _serverURL = serverurl;
    _userName = name;
    _password = password;
    _displayEMSParameters = displayParameters;
    _topics = new HashSet<String>(topics);
    calcHashCode();
  }

  protected void calcHashCode() {
    String hcstr = new String();

    if (_serverURL != null)  hcstr = hcstr + _serverURL;
    if (_userName != null) hcstr = hcstr + _userName;
    if (_password != null) hcstr = hcstr + _password;

    _hashCode = hcstr.hashCode();
  }

  public void setDisplayEMSParameters(boolean parameters) {
    _displayEMSParameters = parameters;
  }

  public String getPassword() {
    return _password;
  }

  public void setPassword(String _password) {
    _password = _password;
  }

  public String getServerURL() {
    return _serverURL;
  }

  public void setServerURL(String _serverurl) {
    _serverURL = _serverurl;
  }

  public Set<String> getTopics() {
    return _topics;
  }

  public void setTopics(Set<String> _topics) {
    _topics = _topics;
  }

  public String getUserName() {
    return _userName;
  }

  public void setUserName(String name) {
    _userName = name;
  }

  public int getHashCode() {
    return _hashCode;
  }

  public void configure(String lineString) {
    String[] results = new String[4];

    StringTokenizer st = new StringTokenizer(lineString, "|", true);

    int i = 0;
    while (st.hasMoreTokens()) {
      String s = st.nextToken();
      if ("|".equals(s)) {
        if (i++ >= 4) {
          throw new IllegalArgumentException("Input line " + lineString + " has too many fields");
        }
      } else {
        results[i] = (s != null) ? s : " ";
      }
    }

    _serverURL = results[0];
    _userName = results[1];
    _password = results[2];
    String topics = results[3];

    StringTokenizer subjectTokenizer = new StringTokenizer(topics, ",", true);

    while (subjectTokenizer.hasMoreTokens()) {
      String sto = subjectTokenizer.nextToken();
      if (!",".equals(sto)) {
        _topics.add(sto);
      }
    }

    calcHashCode();
  }

  public String toString() {
    String sRetval = new String(_serverURL);

    sRetval = sRetval + "|";

    if (_userName != null) {
      sRetval = sRetval + _userName;
    }

    sRetval = sRetval + "|";
    if (_topics != null) {
      boolean first = true;
      Iterator i = _topics.iterator();
      while (i.hasNext()) {
        if (!first) {
          sRetval = sRetval + ",";
        }
        sRetval = sRetval + (String) i.next();
        first = false;
      }
    }

    return sRetval;
  }

  public void setDisplayParameters(boolean displayParameters) {
    _displayEMSParameters = displayParameters;
  }

  public String getTopic() {
    String sRetVal = new String();

    boolean first = true;
    Iterator i = _topics.iterator();
    while (i.hasNext()) {
      if (!first) {
        sRetVal = sRetVal + ",";
      }
      sRetVal = sRetVal + (String) i.next();
      first = false;
    }
    return sRetVal;
  }

  public void setTopics(String subjects) {
    if (_topics == null) {
      throw new IllegalArgumentException("Topics may not be null");
    }

    StringTokenizer subjectTokenizer = new StringTokenizer(subjects, ",", true);

    while (subjectTokenizer.hasMoreTokens()) {
      String sto = subjectTokenizer.nextToken();
      if (!",".equals(sto)) {
        _topics.add(sto);
      }
    }
  }

  public Object clone() throws CloneNotSupportedException {
    return new JMSParameters(_serverURL, _userName, _password, _displayEMSParameters, _topics);
  }

  public void addTopic(String _subject) {
    _topics.add(_subject);
    calcHashCode();
  }

  public int hashCode() {
    return _hashCode;
  }

  public boolean equals(Object o) {
    if ( this == o) return true;
    if (((o instanceof JMSParameters)) && (hashCode() == ((JMSParameters)o).hashCode())) {
      return true;
    }
    return false;
  }
}
