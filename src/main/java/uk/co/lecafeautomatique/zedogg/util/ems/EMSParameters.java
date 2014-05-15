package uk.co.lecafeautomatique.zedogg.util.ems;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

public class EMSParameters
  implements Cloneable, Serializable
{
  private static final long serialVersionUID = 3257001077244114230L;
  protected int _hashCode = 0;
  protected String _serverURL;
  protected String _userName;
  protected String _password;
  protected Hashtable _sslParameters;
  protected boolean _displayEMSParameters;
  protected Set<String> _topics;
  protected String _clientId;

  public EMSParameters()
  {
    this._serverURL = "tcp://localhost:7222";
    this._userName = "";
    this._password = "";
    this._sslParameters = new Hashtable();
    this._clientId = "";
    this._topics = new HashSet();

    calcHashCode();
  }

  public EMSParameters(String serverurl, String name, String password, Hashtable SSLparameters, boolean displayEMSParameters, Set<String> topics, String clientid)
  {
    this._serverURL = serverurl;
    this._userName = name;
    this._password = password;
    this._sslParameters = SSLparameters;
    this._displayEMSParameters = displayEMSParameters;
    this._topics = topics;
    this._clientId = clientid;
  }

  public EMSParameters(Set<String> topics, String serverurl, String name, boolean displayRvParameters, String password, String description)
  {
    this._topics = new HashSet();
    this._topics.addAll(topics);

    this._serverURL = serverurl;
    this._userName = name;
    this._password = password;

    this._displayEMSParameters = displayRvParameters;

    this._clientId = description;

    calcHashCode();
  }

  public String getDescription()
  {
    return this._clientId;
  }

  public void setDescription(String description) {
    this._clientId = description;
  }

  protected void calcHashCode()
  {
    String hcstr = new String();

    if (this._serverURL != null) {
      hcstr = hcstr + this._serverURL;
    }

    if (this._userName != null) {
      hcstr = hcstr + this._userName;
    }

    if (this._password != null) {
      hcstr = hcstr + this._password;
    }

    this._hashCode = hcstr.hashCode();
  }

  public String getClientId()
  {
    return this._clientId;
  }
  public void setClientId(String id) {
    this._clientId = id;
  }
  public boolean isDisplayEMSParameters() {
    return this._displayEMSParameters;
  }
  public void setDisplayEMSParameters(boolean parameters) {
    this._displayEMSParameters = parameters;
  }
  public String getPassword() {
    return this._password;
  }
  public void setPassword(String _password) {
    this._password = _password;
  }
  public String getServerURL() {
    return this._serverURL;
  }
  public void setServerURL(String _serverurl) {
    this._serverURL = _serverurl;
  }
  public Hashtable getSSLParameters() {
    return this._sslParameters;
  }
  public void setSSLParameters(Hashtable parameters) {
    this._sslParameters = parameters;
  }
  public Set<String> getTopics() {
    return this._topics;
  }
  public void setTopics(Set<String> _topics) {
    this._topics = _topics;
  }
  public String getUserName() {
    return this._userName;
  }
  public void setUserName(String name) {
    this._userName = name;
  }
  public int getHashCode() {
    return this._hashCode;
  }

  public void configureByLineString(String lineString) {
    int where = 0;

    String[] results = new String[4];

    StringTokenizer st = new StringTokenizer(lineString, "|", true);

    int i = 0;
    while (st.hasMoreTokens()) {
      String s = st.nextToken();
      if ("|".equals(s)) {
        if (i++ >= 4) {
          throw new IllegalArgumentException("Input line " + lineString + " has too many fields");
        }

      }
      else if (s != null)
        results[i] = s;
      else {
        results[i] = " ";
      }

    }

    this._serverURL = results[0];
    this._userName = results[1];
    this._password = results[2];
    String topics = results[3];

    StringTokenizer subjectTokenizer = new StringTokenizer(topics, ",", true);

    while (subjectTokenizer.hasMoreTokens()) {
      String sto = subjectTokenizer.nextToken();
      if (!",".equals(sto))
      {
        this._topics.add(sto);
      }
    }

    calcHashCode();
  }

  public String toString() {
    String sRetval = new String(this._serverURL);

    sRetval = sRetval + "|";

    if (this._userName != null) {
      sRetval = sRetval + this._userName;
    }

    sRetval = sRetval + "|";
    if (this._topics != null) {
      boolean first = true;
      Iterator i = this._topics.iterator();
      while (i.hasNext()) {
        if (!first) {
          sRetval = sRetval + ",";
        }
        sRetval = sRetval + (String)i.next();
        first = false;
      }
    }

    return sRetval;
  }

  public boolean isDisplayRvParameters() {
    return this._displayEMSParameters;
  }

  public void setDisplayRvParameters(boolean displayRvParameters) {
    this._displayEMSParameters = displayRvParameters;
  }

  public String getTopic() {
    String sRetVal = new String();

    boolean first = true;
    Iterator i = this._topics.iterator();
    while (i.hasNext()) {
      if (!first) {
        sRetVal = sRetVal + ",";
      }
      sRetVal = sRetVal + (String)i.next();
      first = false;
    }
    return sRetVal;
  }

  public void setTopics(String subjects)
  {
    if (this._topics == null) {
      throw new IllegalArgumentException("Topics may not be null");
    }

    StringTokenizer subjectTokenizer = new StringTokenizer(subjects, ",", true);

    while (subjectTokenizer.hasMoreTokens()) {
      String sto = subjectTokenizer.nextToken();
      if (!",".equals(sto))
      {
        this._topics.add(sto);
      }
    }
  }

  public Object clone() throws CloneNotSupportedException
  {
    return new EMSParameters(this._serverURL, this._userName, this._password, this._sslParameters, this._displayEMSParameters, this._topics, this._clientId);
  }

  public void addTopic(String _subject)
  {
    this._topics.add(_subject);
    calcHashCode();
  }

  public int hashCode()
  {
    return this._hashCode;
  }

  public boolean equals(Object o)
  {
    boolean equals = false;

    if (((o instanceof EMSParameters)) && 
      (hashCode() == o.hashCode())) {
      equals = true;
    }

    return equals;
  }
}
