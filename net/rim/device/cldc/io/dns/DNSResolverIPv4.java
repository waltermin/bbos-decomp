package net.rim.device.cldc.io.dns;

import java.io.EOFException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioException;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.UDPPacketHeader;
import net.rim.device.api.system.UDPPacketListener;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.internal.io.PortAssigner;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.system.SimulatorServices;

public class DNSResolverIPv4 implements UDPPacketListener {
   private IntHashtable _packetIDtoDNSData = new IntHashtable();
   private IntHashtable _radioIDtoDNSData = new IntHashtable();
   private int _numQueries;
   private DNSResolverIPv4$DNSResolverIPv4Thread _thread;
   private DNSCache _cache;
   private int RECURSION_ENABLED;
   private static final long GUID;
   private static int _packetID;
   private static final int DNS_HEADER_SIZE;
   private static final int DNS_QUERY_UDP_PORT;
   private static DNSResolverIPv4 _instance;

   public int getAddressByHostname(String hostname, DNSListener listener) {
      return this.getAddressByHostname(hostname, listener, 0);
   }

   public Vector getAddressByHostname(String hostname) {
      return this.getAddressByHostname(hostname, 0);
   }

   public int getAddressByHostname(String hostname, DNSListener listener, String apn) {
      try {
         int apnId = RadioInfo.getAccessPointNumber(apn);
         return this.getAddressByHostname(hostname, listener, apnId);
      } catch (RadioException re) {
         throw new IllegalArgumentException("APN not registered");
      }
   }

   public Vector getAddressByHostname(String hostname, String apn) {
      try {
         int apnId = RadioInfo.getAccessPointNumber(apn);
         return this.getAddressByHostname(hostname, apnId);
      } catch (RadioException re) {
         throw new IllegalArgumentException("APN not registered");
      }
   }

   public int getAddressByHostname(String hostname, DNSListener listener, int apnId) {
      if (listener == null) {
         throw new NullPointerException();
      }

      DNSRequest req = new DNSRequest(hostname, listener, apnId);
      this.prepRequest(req, true);
      this._thread.addRequest(req);
      return req.getPacketId();
   }

   public Vector getAddressByHostname(String hostname, int apnId) {
      DNSRequest req = new DNSRequest(hostname, null, apnId);
      this.prepRequest(req, true);
      Vector result = this.executeQuery(req);
      if (req.getStatus() != 1) {
         throw new DNSException("Bad DNS Address", req.getStatus());
      } else {
         return result;
      }
   }

   public int getHostnameByAddress(byte[] ipAddr, DNSListener listener) {
      return this.getHostnameByAddress(ipAddr, listener, 0);
   }

   public Vector getHostnameByAddress(byte[] ipAddr) {
      return this.getHostnameByAddress(ipAddr, 0);
   }

   public int getHostnameByAddress(byte[] ipAddr, DNSListener listener, String apn) {
      try {
         int apnId = RadioInfo.getAccessPointNumber(apn);
         return this.getHostnameByAddress(ipAddr, listener, apnId);
      } catch (RadioException re) {
         throw new IllegalArgumentException("APN not registered");
      }
   }

   public Vector getHostnameByAddress(byte[] ipAddr, String apn) {
      try {
         int apnId = RadioInfo.getAccessPointNumber(apn);
         return this.getHostnameByAddress(ipAddr, apnId);
      } catch (RadioException re) {
         throw new IllegalArgumentException("APN not registered");
      }
   }

   public int getHostnameByAddress(byte[] ipAddr, DNSListener listener, int apnId) {
      if (listener == null) {
         throw new NullPointerException();
      }

      DNSRequest req = new DNSRequest(ipAddr, listener, apnId);
      this.prepRequest(req, true);
      this._thread.addRequest(req);
      return req.getPacketId();
   }

   public Vector getHostnameByAddress(byte[] ipAddr, int apnId) {
      DNSRequest req = new DNSRequest(ipAddr, null, apnId);
      this.prepRequest(req, true);
      Vector results = this.executeQuery(req);
      if (req.getStatus() != 11) {
         throw new DNSException(req.getStatus());
      } else {
         return results;
      }
   }

   public Vector doBlockingQuery(DNSRequest req) {
      this.prepRequest(req, false);
      Vector results = this.executeQuery(req);
      int status = req.getQueryType() == 1 ? 1 : 11;
      if (req.getStatus() != status) {
         throw new DNSException(req.getStatus());
      } else {
         return results;
      }
   }

   public int doQuery(DNSRequest req) {
      if (req.getListener() == null) {
         throw new NullPointerException();
      }

      this.prepRequest(req, false);
      this._thread.addRequest(req);
      return req.getPacketId();
   }

   public void clearResultFromCache(String queryString, Object result) {
      this._cache.removeFromCache(queryString, result);
   }

   public DNSCache getCache() {
      return this._cache;
   }

   public void setup(long guid, Object value) {
      if (guid == -3533413171973668650L) {
         this.RECURSION_ENABLED = 0;
      } else {
         if (guid == -797472996234301532L) {
            this.RECURSION_ENABLED = 256;
         }
      }
   }

   @Override
   public void packetStatus(int packetId, int status) {
   }

   @Override
   public void packetReceived(UDPPacketHeader header, byte[] data) {
      if (header.getSourcePort() == 53 && this.isMatchingPort(header.getDestinationPort())) {
         if (data.length >= 12) {
            DNSMessageIPv4 answer;
            try {
               answer = new DNSMessageIPv4();
               answer.readMessage(new DataBuffer(data, 0, data.length, true));
            } catch (EOFException e) {
               return;
            }

            DNSRequest req = (DNSRequest)this._packetIDtoDNSData.get(answer.getID() & 65535);
            if (req != null) {
               EventLogger.logEvent(1197736374800106759L, 1381528436, 4);
               synchronized (req) {
                  req.setAnswer(answer);
                  req.setStatus(12);
                  req.notify();
               }
            }
         }
      }
   }

   @Override
   public void packetNotSent(int packetId, int networkId) {
      DNSRequest req;
      synchronized (this._radioIDtoDNSData) {
         req = (DNSRequest)this._radioIDtoDNSData.remove(packetId);
      }

      if (req != null) {
         synchronized (req) {
            req.setStatus(4);
            req.notify();
         }
      }
   }

   @Override
   public void packetSent(int packetId, int networkId) {
      synchronized (this._radioIDtoDNSData) {
         this._radioIDtoDNSData.remove(packetId);
      }
   }

   private DNSRequest processResponse(DNSRequest req) {
      Vector results = null;
      switch (req.getAnswer().getRcode()) {
         case 0:
         default:
            this._cache.addToCache(req);
            results = new Vector();
            req.setStatus(10);
            boolean cnameSpecified = false;
            int desiredType = req.getQueryType() == 1 ? 1 : 12;
            Vector answers = req.getAnswer().getAnswers();
            int answersSize = answers.size();

            for (int i = 0; i < answersSize; i++) {
               DNSMessageIPv4Resource record = (DNSMessageIPv4Resource)answers.elementAt(i);
               if (record.getType() == desiredType) {
                  results.addElement(record.getData());
               } else if (record.getType() == 5) {
                  cnameSpecified = true;
               }
            }

            if (results.size() != 0) {
               EventLogger.logEvent(1197736374800106759L, 1382380625, 0);
               return this.endQuery(req, req.getQueryType() == 1 ? 1 : 11, results);
            } else {
               DNSRequest newReq = null;
               if (cnameSpecified) {
                  EventLogger.logEvent(1197736374800106759L, 1130458705, 0);
                  if (req.getCnameAttempts() < 9) {
                     req.setCnameAttempts(req.getCnameAttempts() + 1);
                     newReq = this.setupAliasQuery(req);
                     if (newReq != null) {
                        newReq.setCnameAttempts(req.getCnameAttempts() + 1);
                     } else {
                        EventLogger.logEvent(1197736374800106759L, 1130458705, 3);
                     }
                  } else {
                     EventLogger.logEvent(1197736374800106759L, 1298233425, 3);
                  }
               } else {
                  EventLogger.logEvent(1197736374800106759L, 1382377041, 0);
                  if (req.getReferralAttempts() < 21) {
                     req.setReferralAttempts(req.getReferralAttempts() + 1);
                     newReq = this.setupReferredQuery(req);
                     if (newReq != null) {
                        newReq.setReferralAttempts(req.getReferralAttempts() + 1);
                     } else {
                        EventLogger.logEvent(1197736374800106759L, 1382377041, 3);
                     }
                  } else {
                     EventLogger.logEvent(1197736374800106759L, 1298233425, 3);
                  }
               }

               if (newReq != null) {
                  return newReq;
               }

               EventLogger.logEvent(1197736374800106759L, 1165128273, 0);
               return this.endQuery(req, 10, null);
            }
         case 1:
            return this.endQuery(req, 5, null);
         case 2:
            return this.endQuery(req, 6, null);
         case 3:
            return this.endQuery(req, 7, null);
         case 4:
            return this.endQuery(req, 8, null);
         case 5:
            req = this.endQuery(req, 9, null);
         case -1:
            return req;
      }
   }

   private DNSRequest endQuery(DNSRequest req, int status, Vector results) {
      while (true) {
         int successStatus = req.getQueryType() == 1 ? 1 : 11;
         switch (status) {
            case 2:
               byte[] nsIp = req.getCurrentIp();
               String nsName = req.getCurrentNsName();
               if (nsName != null) {
                  this._cache.removeFromCache(nsName, nsIp);
               }
               break;
            case 6:
            case 7:
               if (this.setupNextAttempt(req)) {
                  return req;
               }

               if (status == 7 && req.getAnswer() != null && req.getAnswer().getAA() == 1024) {
                  this._cache.addNameError(req);
               }
               break;
            default:
               if (status == successStatus && (results == null || results.size() == 0)) {
                  status = 10;
                  results = null;
               }
         }

         req.setStatus(status);
         req.setResult(results);
         this.dispatchEvent(req, status, results);
         if (status != successStatus && this.setupForSecondary(req)) {
            return req;
         }

         this._packetIDtoDNSData.remove(req.getPacketId());
         req.setDone(true);
         if (req.getPreviousRequest() == null) {
            return req;
         }

         req = req.getPreviousRequest();
      }
   }

   private void doRawQuery(DNSRequest req) {
      while (true) {
         DataBuffer buffer = new DataBuffer(true);
         DNSMessageIPv4Question question = (DNSMessageIPv4Question)req.getQuery().getQuestions().elementAt(0);
         String name = req.getQueryString();
         if (req.isFlagSet(1)) {
            name = addAPNToName(name, req.getApnId());
         }

         question.setQname(name);

         try {
            req.getQuery().writeMessage(buffer);
            buffer.trim();
         } catch (IOException ioe) {
            this.failRequest(req, -2);
            continue;
         }

         byte[] addr = req.getCurrentIp();
         if (addr == null) {
            this.failRequest(req, -4);
         } else {
            UDPPacketHeader udpHeader = this.makeUDPPacketHeader(addr, req.getSrcPort(), req.getApnId());

            try {
               EventLogger.logEvent(1197736374800106759L, 1415082868, 4);
               req.setStatus(0);
               synchronized (this._radioIDtoDNSData) {
                  int currentPacketID = RadioInternal.sendPacket(udpHeader, buffer.getArray());
                  if (currentPacketID >= 0) {
                     this._radioIDtoDNSData.put(currentPacketID, req);
                     return;
                  }

                  EventLogger.logEvent(1197736374800106759L, 1415082868, 3);
                  this.failRequest(req, -100 + currentPacketID);
               }
            } catch (RadioException var11) {
               EventLogger.logEvent(1197736374800106759L, 1415082868, 2);
               this.failRequest(req, -3);
            }
         }
      }
   }

   private DNSRequest lookInCache(DNSRequest req) {
      Vector cached = null;
      int queryType = req.getQueryType();
      String queryStr = req.getQueryString();

      try {
         cached = this._cache.lookup(queryStr, queryType);
         if (cached != null) {
            return this.endQuery(req, queryType == 1 ? 1 : 11, cached);
         }

         while (queryStr.length() != 0) {
            cached = this._cache.lookup(queryStr, 2);
            if (cached != null) {
               while (cached.size() > 0) {
                  int i = RandomSource.getInt(cached.size());
                  Vector IPs = this._cache.lookup((String)cached.elementAt(i), 1);
                  if (IPs != null && IPs.size() > 0) {
                     req.setCurrentIpSettings((byte[])IPs.elementAt(RandomSource.getInt(IPs.size())), (String)cached.elementAt(i));
                     return req;
                  }

                  cached.removeElementAt(i);
               }
            }

            int dot = queryStr.indexOf(46);
            if (dot < 0) {
               queryStr = "";
            } else {
               queryStr = queryStr.substring(dot + 1);
            }
         }
      } catch (DNSException de) {
         this.endQuery(req, 7, null);
      }

      return req;
   }

   private void doSimulatorWorkaround(DNSRequest req) {
      String str = req.getQueryString();
      Vector v = null;
      Object[] results;
      int resultStatus;
      switch (req.getQueryType()) {
         case 1:
            results = SimulatorServices.dnsLookup(str, true);
            resultStatus = 1;
            break;
         case 12:
            results = SimulatorServices.dnsLookup(str, false);
            resultStatus = 11;
            break;
         default:
            throw new DNSException(-1);
      }

      if (results != null) {
         v = new Vector(results.length);

         for (int i = 0; i < results.length; i++) {
            v.addElement(results[i]);
         }
      } else {
         resultStatus = 7;
      }

      req.setFlag(-1);
      this.endQuery(req, resultStatus, v);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private Vector executeQuery(DNSRequest req) {
      if (DeviceInfo.isSimulator() && req._doSimulatorHack) {
         this.doSimulatorWorkaround(req);
         return req.getResult();
      }

      boolean registered = false;
      boolean var14 = false /* VF: Semaphore variable */;

      try {
         var14 = true;
         this.incrementQueryCount();

         try {
            PortAssigner.getInstance(17).registerConnection(req.getSrcPort(), RadioInfo.getAccessPointName(req.getApnId()));
            registered = true;
         } catch (Exception e) {
            EventLogger.logEvent(1197736374800106759L, 1413836914, 2);
            throw new DNSException(e.getMessage());
         }

         while (true) {
            if (req.isDone()) {
               var14 = false;
               break;
            }

            req = this.lookInCache(req);
            if (req.isDone()) {
               var14 = false;
               break;
            }

            EventLogger.logEvent(1197736374800106759L, 1148089169, req.getApnId(), 10, 0);
            this.doRawQuery(req);
            synchronized (req) {
               if (req.getStatus() == 0) {
                  try {
                     req.wait(req.getTimeout());
                  } catch (InterruptedException var17) {
                  }

                  if (req.getStatus() == 0) {
                     EventLogger.logEvent(1197736374800106759L, 1148089172, req.getApnId(), 10, 3);
                     req = this.endQuery(req, 2, null);
                     continue;
                  }

                  if (req.getStatus() == 4) {
                     this.failRequest(req, 4);
                     continue;
                  }
               }
            }

            EventLogger.logEvent(1197736374800106759L, 1148089170, req.getApnId(), 10, 0);
            req = this.processResponse(req);
         }
      } finally {
         if (var14) {
            this.decrementQueryCount();
            if (registered) {
               try {
                  PortAssigner.getInstance(17).deregisterConnection(req.getSrcPort(), RadioInfo.getAccessPointName(req.getApnId()));
               } catch (Exception var15) {
               }
            }
         }
      }

      this.decrementQueryCount();
      if (registered) {
         try {
            PortAssigner.getInstance(17).deregisterConnection(req.getSrcPort(), RadioInfo.getAccessPointName(req.getApnId()));
         } catch (Exception var16) {
         }
      }

      return req.getResult();
   }

   private boolean isMatchingPort(int port) {
      Enumeration data = this._packetIDtoDNSData.elements();

      while (data.hasMoreElements()) {
         if (((DNSRequest)data.nextElement()).getSrcPort() == port) {
            return true;
         }
      }

      return false;
   }

   private boolean setupForSecondary(DNSRequest req) {
      if (!req.isFlagSet(2)) {
         byte[] addr = req.getSecondaryDnsIp();
         if (addr != null) {
            req.clearFlag(1);
            req.setFlag(2);
            req.setCurrentIpSettings(addr, null);
            return true;
         }
      }

      return false;
   }

   private void failRequest(DNSRequest req, int status) {
      if (!req.isFlagSet(2)) {
         req.setFlag(2);
      } else {
         req.setStatus(status);
         throw new DNSException(status);
      }
   }

   private static String addAPNToName(String name, int apnID) {
      String apn = null;

      try {
         apn = RadioInfo.getAccessPointName(apnID);
      } catch (RadioException var4) {
      }

      if (apn != null && apn.length() > 0 && !WLAN.WLAN_PSEUDO_APN.equals(apn)) {
         name = name + '.' + apn;
      }

      return name;
   }

   private synchronized int getNextPacketId() {
      int id = _packetID;
      _packetID = _packetID + 1 & 65535;
      return id;
   }

   private void dispatchEvent(DNSRequest req, int type, Vector result) {
      if (req != null && req.getListener() != null) {
         req.getListener().DNSEvent(req.getPacketId(), type, result);
      }
   }

   private void prepRequest(DNSRequest req, boolean doSimHack) {
      int packetId = this.getNextPacketId();
      req.setPacketId(packetId);
      req.getQuery().setRD(this.RECURSION_ENABLED);
      req.setTimestamp(DNSCache.getCurrentTime());
      req._doSimulatorHack = doSimHack;
      byte[] addr = req.getPrimaryDnsIp();
      if (addr == null) {
         req.setFlag(2);
         addr = req.getSecondaryDnsIp();
      }

      req.setCurrentIpSettings(addr, null);
      this._packetIDtoDNSData.put(packetId, req);
   }

   private DNSRequest chainQuery(DNSRequest req, String hostname) {
      DNSRequest newReq = new DNSRequest(hostname, null, req.getApnId(), req.getCurrentIp(), null, req.getSrcPort());
      newReq.setPreviousRequest(req);
      if (!req.isFlagSet(2)) {
         newReq.setSecondaryDnsIp(req.getSecondaryDnsIp());
      }

      this.prepRequest(newReq, req._doSimulatorHack);
      return newReq;
   }

   private synchronized void incrementQueryCount() {
      if (++this._numQueries == 1) {
         ProtocolDaemon.getInstance().addRadioListener(this);
      }
   }

   private synchronized void decrementQueryCount() {
      if (--this._numQueries == 0) {
         ProtocolDaemon.getInstance().removeRadioListener(this);
      }
   }

   private UDPPacketHeader makeUDPPacketHeader(byte[] destAddr, int srcPort, int apn) {
      UDPPacketHeader udpHeader = new UDPPacketHeader();
      udpHeader.setDestinationAddress(destAddr);
      udpHeader.setSourcePort(srcPort);
      udpHeader.setDestinationPort(53);
      udpHeader.setAccessPointNumber(apn);
      return udpHeader;
   }

   public static DNSResolverIPv4 instance() {
      if (_instance != null) {
         return _instance;
      }

      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      synchronized (reg) {
         _instance = (DNSResolverIPv4)reg.get(1197736374800106759L);
         if (_instance == null) {
            _instance = new DNSResolverIPv4();
            reg.put(1197736374800106759L, _instance);
         }
      }

      return _instance;
   }

   private DNSResolverIPv4() {
      _packetID = 1;
      this._numQueries = 0;
      this.RECURSION_ENABLED = 256;
      EventLogger.register(1197736374800106759L, "net.rim.dns", 2);
      this._thread = new DNSResolverIPv4$DNSResolverIPv4Thread(this);
      ProtocolDaemon.getInstance().startThread(this._thread);
      this._cache = new DNSCache();
   }

   private DNSRequest setupReferredQuery(DNSRequest req) {
      Vector authorities = req.getAnswer().getAuthorities();
      Vector additionals = req.getAnswer().getAdditional();
      String nsName = null;
      int authSize = authorities.size();

      for (int i = 0; i < authSize; i++) {
         DNSMessageIPv4Resource authority = (DNSMessageIPv4Resource)authorities.elementAt(i);
         if (authority.getType() == 2) {
            String name = (String)authority.getData();
            if (nsName == null) {
               nsName = name;
            }

            int addSize = additionals.size();

            for (int j = 0; j < addSize; j++) {
               DNSMessageIPv4Resource additional = (DNSMessageIPv4Resource)additionals.elementAt(j);
               if (additional.getType() == 1 && name.equals(additional.getName())) {
                  req.setCurrentIpSettings((byte[])additional.getData(), additional.getName());
                  return req;
               }
            }
         }
      }

      return nsName != null ? this.chainQuery(req, nsName) : null;
   }

   private DNSRequest setupAliasQuery(DNSRequest req) {
      Vector answers = req.getAnswer().getAnswers();
      String nsName = null;
      String orgName = req.getQueryString();
      int answerSize = answers.size();
      String name = null;

      for (int i = 0; i < answerSize; i++) {
         DNSMessageIPv4Resource answer = (DNSMessageIPv4Resource)answers.elementAt(i);
         if (answer.getType() == 5) {
            name = answer.getName();
            if (!StringUtilities.strEqual(orgName, name)) {
               break;
            }

            orgName = (String)answer.getData();
            nsName = orgName;
         }
      }

      return nsName != null ? this.chainQuery(req, nsName) : null;
   }

   private boolean setupNextAttempt(DNSRequest req) {
      String name = req.getQueryString();
      if (req.getQueryType() == 1 && !req.isFlagSet(1) && name.charAt(name.length() - 1) != '.') {
         req.setFlag(1);
         if (req.isFlagSet(2)) {
            req.setCurrentIpSettings(req.getSecondaryDnsIp(), null);
            return true;
         } else {
            req.setCurrentIpSettings(req.getPrimaryDnsIp(), null);
            return true;
         }
      } else {
         return false;
      }
   }
}
