package net.rim.device.cldc.io.dns;

import java.util.Vector;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioException;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLAN;
import net.rim.device.internal.system.RadioInternal;

public class DNSRequest {
   private DNSListener _listener;
   private DNSMessageIPv4 _message;
   private DNSMessageIPv4 _answer;
   private int _timestamp;
   private int _status;
   private int _queryType;
   private int _apnId;
   private byte[] _curDnsIP;
   private String _curDnsName;
   private int _dnsPort;
   private Vector _result;
   private String _name;
   private int _flags;
   private boolean _done;
   private DNSRequest _prevRequest;
   private int _timeout;
   private byte[] _primaryDnsIP;
   private byte[] _secondaryDnsIP;
   private int _cnames;
   private int _referrals;
   boolean _doSimulatorHack;
   public static final int QUERY_FOR_IP_ADDRESS;
   public static final int QUERY_FOR_HOSTNAME;
   public static final int DEFAULT_SRC_PORT;
   public static final int FLAG_APPEND_APN_TO_NAME;
   public static final int FLAG_USE_SECONDARY_DNS;
   public static final int DEFAULT_TIMEOUT;

   public DNSRequest(String hostname, DNSListener listener, int apnId) {
      this(hostname, listener, apnId, getDefaultServerAddress(apnId, 1), getDefaultServerAddress(apnId, 2), 19780);
   }

   public DNSRequest(String hostname, DNSListener listener, int apnId, byte[] primaryDnsAddr, byte[] secondaryDnsAddr, int srcPort) {
      this._queryType = 1;
      this._timeout = 10000;
      this.init(hostname, listener, apnId, primaryDnsAddr, secondaryDnsAddr, srcPort);
   }

   public DNSRequest(byte[] ipAddr, DNSListener listener, int apnId) {
      this(ipAddr, listener, apnId, getDefaultServerAddress(apnId, 1), getDefaultServerAddress(apnId, 2), 19780);
   }

   public DNSRequest(byte[] ipAddr, DNSListener listener, int apnId, byte[] primaryDnsAddr, byte[] secondaryDnsAddr, int srcPort) {
      this._queryType = 12;
      this._timeout = 10000;
      this.init(makeInverseQueryHostname(ipAddr), listener, apnId, primaryDnsAddr, secondaryDnsAddr, srcPort);
   }

   public DNSListener getListener() {
      return this._listener;
   }

   DNSMessageIPv4 getQuery() {
      return this._message;
   }

   public DNSMessageIPv4 getAnswer() {
      return this._answer;
   }

   void setAnswer(DNSMessageIPv4 answer) {
      this._answer = answer;
   }

   public int getTimestamp() {
      return this._timestamp;
   }

   void setTimestamp(int timestamp) {
      this._timestamp = timestamp;
   }

   public int getStatus() {
      return this._status;
   }

   void setStatus(int status) {
      this._status = status;
   }

   public int getQueryType() {
      return this._queryType;
   }

   public int getApnId() {
      return this._apnId;
   }

   public byte[] getCurrentIp() {
      return this._curDnsIP;
   }

   public String getCurrentNsName() {
      return this._curDnsName;
   }

   void setCurrentIpSettings(byte[] nsIp, String nsName) {
      this._curDnsIP = nsIp;
      this._curDnsName = nsName;
   }

   public int getSrcPort() {
      return this._dnsPort;
   }

   public Vector getResult() {
      return this._result;
   }

   void setResult(Vector vec) {
      this._result = vec;
   }

   public String getQueryString() {
      return this._name;
   }

   public int getFlags() {
      return this._flags;
   }

   public boolean isFlagSet(int flag) {
      return (this._flags & flag) != 0;
   }

   void setFlag(int flag) {
      this._flags |= flag;
   }

   void clearFlag(int flag) {
      this._flags &= ~flag;
   }

   public boolean isDone() {
      return this._done;
   }

   void setDone(boolean done) {
      this._done = done;
   }

   public DNSRequest getPreviousRequest() {
      return this._prevRequest;
   }

   void setPreviousRequest(DNSRequest req) {
      this._prevRequest = req;
   }

   public int getPacketId() {
      return this._message.getID();
   }

   void setPacketId(int id) {
      this._message.setID(id);
   }

   public byte[] getPrimaryDnsIp() {
      return this._primaryDnsIP;
   }

   public void setPrimaryDnsIp(byte[] ip) {
      this._primaryDnsIP = ip;
   }

   public byte[] getSecondaryDnsIp() {
      return this._secondaryDnsIP;
   }

   public void setSecondaryDnsIp(byte[] ip) {
      this._secondaryDnsIP = ip;
   }

   public int getTimeout() {
      return this._timeout;
   }

   public void setTimeout(int to) {
      this._timeout = to;
   }

   int getCnameAttempts() {
      return this._cnames;
   }

   void setCnameAttempts(int newAttempts) {
      this._cnames = newAttempts;
   }

   int getReferralAttempts() {
      return this._referrals;
   }

   void setReferralAttempts(int newAttempts) {
      this._referrals = newAttempts;
   }

   private void init(String queryStr, DNSListener listener, int apnId, byte[] primaryDnsAddr, byte[] secondaryDnsAddr, int port) {
      DNSMessageIPv4 query = new DNSMessageIPv4();
      query.setQR(0);
      query.setOpcode(0);
      DNSMessageIPv4Question question = new DNSMessageIPv4Question();
      question.setQtype(this._queryType);
      question.setQclass(1);
      query.addQuestion(question);
      this._listener = listener;
      this._message = query;
      this._apnId = apnId;
      this._dnsPort = port;
      if (queryStr.indexOf(46) == -1 && (RadioInfo.getSupportedWAFs() & 4) != 0) {
         try {
            String apn = RadioInfo.getAccessPointName(apnId);
            if (apn != null && WLAN.WLAN_PSEUDO_APN.equals(apn)) {
               byte[] data = RadioInternal.getNetworkParameter(apnId, 15, 0);
               if (data != null && data.length > 0) {
                  String domainName = new String(data);
                  if (domainName.length() > 0) {
                     StringBuffer sb = new StringBuffer(queryStr.length() + 1 + domainName.length());
                     sb.append(queryStr);
                     sb.append('.');
                     sb.append(domainName);
                     queryStr = sb.toString();
                  }
               }
            }
         } catch (RadioException var13) {
         }
      }

      this._name = queryStr;
      this._primaryDnsIP = primaryDnsAddr;
      this._secondaryDnsIP = secondaryDnsAddr;
   }

   private static String makeInverseQueryHostname(byte[] ipAddr) {
      StringBuffer tmp = new StringBuffer(28);

      for (int i = 3; i >= 0; i--) {
         tmp.append(ipAddr[i] & 255);
         tmp.append('.');
      }

      tmp.append("IN-ADDR.ARPA");
      return tmp.toString();
   }

   public static byte[] getDefaultServerAddress(int apnId, int type) {
      byte[] server = RadioInternal.getDNSIPAddress(apnId, type);
      if (server == null) {
         if (DeviceInfo.isSimulator()) {
            if (type == 1) {
               return new byte[]{10, 101, 20, 21};
            }

            return new byte[]{10, 101, 21, 21};
         }

         if (type == 1) {
            server = new byte[]{-84, 16, 50, 100};
         }
      }

      return server;
   }
}
