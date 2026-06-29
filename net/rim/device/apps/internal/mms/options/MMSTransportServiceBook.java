package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.browser.push.WAPPushSource;
import net.rim.device.api.hrt.GprsHRI;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.hrt.IPv4UdpDAC;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Phone;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.browser.options.GeneralProperty;
import net.rim.device.internal.browser.wap.WAPServiceRecord;
import net.rim.device.internal.browser.wap.WPTCPServiceRecord;

public final class MMSTransportServiceBook {
   public static final boolean hasMMSServiceRecord() {
      return getServiceRecord() != null;
   }

   public static final String getUserAgentName() {
      return GeneralProperty.getEmulationModeString(0);
   }

   static final String getAPN() {
      HostRoutingInfo activeHri = getHostRoutingInfo(getServiceRecord());
      if (!(activeHri instanceof GprsHRI)) {
         return null;
      }

      GprsHRI hri = (GprsHRI)activeHri;
      return hri.getApn();
   }

   static final void setAPN(String apn) {
      HostRoutingInfo activeHri = getHostRoutingInfo(getServiceRecord());
      if (activeHri instanceof GprsHRI) {
         GprsHRI hri = (GprsHRI)activeHri;
         hri.setApn(apn);
      }
   }

   static final String getHostIP() {
      HostRoutingInfo hri = getHostRoutingInfo(getServiceRecord());
      if (hri != null) {
         long[] dacs = ((IPv4UdpDAC)hri.getDac()).getAddresses();
         return IPv4UdpDAC.addr2String(dacs[0]);
      } else {
         return null;
      }
   }

   static final void setHostIP(String hostIP) {
      HostRoutingInfo hri = getHostRoutingInfo(getServiceRecord());
      if (hri != null) {
         try {
            long[] dacs = new long[]{IPv4UdpDAC.string2Addr(hostIP)};
            ((IPv4UdpDAC)hri.getDac()).setAddresses(dacs);
         } finally {
            String message = "Invalid IP/Port\nFormat is ###.###.###.###:Dst_Port[:Src_Port].";
            Dialog.alert(message);
            return;
         }
      }
   }

   public static final String getProxyAddress() {
      Object o = getServiceRecord();
      String url = null;
      if (o instanceof WPTCPServiceRecord) {
         WPTCPServiceRecord rec = (WPTCPServiceRecord)o;
         url = rec.getPropertyAsString(1);
      }

      return url;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void setProxyAddress(String url) {
      Object record = getServiceRecord();

      try {
         if (record instanceof WPTCPServiceRecord) {
            WPTCPServiceRecord rec = (WPTCPServiceRecord)record;
            rec.setProperty(1, url);
            rec.saveData();
            return;
         }
      } catch (Throwable var4) {
         System.out.println("MMSC.setProxyAddress " + e.toString());
         return;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void setMMSCUrl(String url) {
      Object record = getServiceRecord();

      try {
         if (!(record instanceof WAPServiceRecord)) {
            if (record instanceof WPTCPServiceRecord) {
               WPTCPServiceRecord rec = (WPTCPServiceRecord)record;
               rec.setProperty(13, url);
               rec.saveData();
               return;
            }
         } else {
            WAPServiceRecord rec = (WAPServiceRecord)record;
            rec.setMMSCUrl(url);
            rec.saveData();
         }
      } catch (Throwable var4) {
         System.out.println("MMSC.setMMSCUrl " + e.toString());
         return;
      }
   }

   public static final String getMMSCUrl() {
      Object record = getServiceRecord();
      String url = null;
      if (!(record instanceof WAPServiceRecord)) {
         if (record instanceof WPTCPServiceRecord) {
            WPTCPServiceRecord rec = (WPTCPServiceRecord)record;
            url = rec.getPropertyAsString(13);
         }
      } else {
         WAPServiceRecord rec = (WAPServiceRecord)record;
         url = rec.getMMSCUrl();
      }

      if (isInternalMMSC(url) && url.endsWith("/")) {
         try {
            String number = Phone.getInstance().getNumber(0);
            if (number != null && number.length() > 0 && number.charAt(0) != '+') {
               if (number.charAt(0) != '1') {
                  number = "1" + number;
               }

               number = "+" + number;
            }

            return url + number;
         } finally {
            return url;
         }
      } else {
         return url;
      }
   }

   public static final String getMMSCConnectionParameters() {
      if (useGMETransport(getMMSCUrl())) {
         return "";
      }

      if (isWAPServiceRecord()) {
         String wapUid = getWAPTransportUID();
         String params = ";retrynocontext=true;WAPGatewayIP=";
         if (wapUid != null && wapUid.length() > 0) {
            params = params + ";ConnectionUID=" + wapUid;
         }

         int timeout = MMSClientServiceBook.getConnectionTimeout();
         if (timeout > 0) {
            params = params + ";ConnectionTimeout=" + Integer.toString(timeout * 1000);
         }

         return params;
      } else {
         return ";retrynocontext=true;DeviceSide=true;ConnectionUID=" + getWPTCPTransportUID();
      }
   }

   public static final String getMMSCUsername() {
      return getMMSCUsername(getServiceRecord());
   }

   private static final String getMMSCUsername(Object record) {
      if (!(record instanceof WAPServiceRecord)) {
         if (!(record instanceof WPTCPServiceRecord)) {
            return null;
         }

         WPTCPServiceRecord rec = (WPTCPServiceRecord)record;
         return rec.getPropertyAsString(11);
      } else {
         WAPServiceRecord rec = (WAPServiceRecord)record;
         return rec.getAuthUsername();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void setMMSCUsername(String username) {
      Object record = getServiceRecord();

      try {
         if (username != null && getMMSCPassword(record) == null) {
            setMMSCPassword(record, "");
         }

         if (!(record instanceof WAPServiceRecord)) {
            if (record instanceof WPTCPServiceRecord) {
               WPTCPServiceRecord rec = (WPTCPServiceRecord)record;
               rec.setProperty(11, username);
               rec.saveData();
               return;
            }
         } else {
            WAPServiceRecord rec = (WAPServiceRecord)record;
            rec.setAuthUsername(username);
            rec.saveData();
         }
      } catch (Throwable var4) {
         System.out.println("MMSC.setMMSCUsername " + e.toString());
         return;
      }
   }

   public static final String getMMSCPassword() {
      return getMMSCPassword(getServiceRecord());
   }

   private static final String getMMSCPassword(Object record) {
      if (!(record instanceof WAPServiceRecord)) {
         if (!(record instanceof WPTCPServiceRecord)) {
            return null;
         }

         WPTCPServiceRecord rec = (WPTCPServiceRecord)record;
         return rec.getPropertyAsString(12);
      } else {
         WAPServiceRecord rec = (WAPServiceRecord)record;
         return rec.getAuthPassword();
      }
   }

   public static final void setMMSCPassword(String password) {
      setMMSCPassword(getServiceRecord(), password);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static final void setMMSCPassword(Object record, String password) {
      try {
         if (getMMSCUsername(record) != null && password == null) {
            password = "";
         }

         if (!(record instanceof WAPServiceRecord)) {
            if (record instanceof WPTCPServiceRecord) {
               WPTCPServiceRecord rec = (WPTCPServiceRecord)record;
               rec.setProperty(12, password);
               rec.saveData();
               return;
            }
         } else {
            WAPServiceRecord rec = (WAPServiceRecord)record;
            rec.setAuthPassword(password);
            rec.saveData();
         }
      } catch (Throwable var4) {
         System.out.println("MMSC.setMMSCPassword " + e.toString());
         return;
      }
   }

   public static final String getMessageUrlPrefix() {
      Object record = getServiceRecord();
      if (!(record instanceof WAPServiceRecord)) {
         if (!(record instanceof WPTCPServiceRecord)) {
            return null;
         }

         WPTCPServiceRecord rec = (WPTCPServiceRecord)record;
         return rec.getPropertyAsString(15);
      } else {
         WAPServiceRecord rec = (WAPServiceRecord)record;
         return rec.getMMSMessageUrlPrefix();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void setMessageUrlPrefix(String prefix) {
      Object record = getServiceRecord();

      try {
         if (!(record instanceof WAPServiceRecord)) {
            if (record instanceof WPTCPServiceRecord) {
               WPTCPServiceRecord rec = (WPTCPServiceRecord)record;
               rec.setProperty(15, prefix);
               rec.saveData();
               return;
            }
         } else {
            WAPServiceRecord rec = (WAPServiceRecord)record;
            rec.setMMSMessageUrlPrefix(prefix);
            rec.saveData();
         }
      } catch (Throwable var4) {
         System.out.println("MMSC.setMMSCPassword " + e.toString());
         return;
      }
   }

   public static final String getAuthenticationHeader() {
      Object record = getServiceRecord();
      if (!(record instanceof WAPServiceRecord)) {
         if (!(record instanceof WPTCPServiceRecord)) {
            return null;
         }

         WPTCPServiceRecord rec = (WPTCPServiceRecord)record;
         return rec.getPropertyAsString(16);
      } else {
         WAPServiceRecord rec = (WAPServiceRecord)record;
         return rec.getMMSAuthenticationHeader();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void setAuthenticationHeader(String header) {
      Object record = getServiceRecord();

      try {
         if (!(record instanceof WAPServiceRecord)) {
            if (record instanceof WPTCPServiceRecord) {
               WPTCPServiceRecord rec = (WPTCPServiceRecord)record;
               rec.setProperty(16, header);
               rec.saveData();
               return;
            }
         } else {
            WAPServiceRecord rec = (WAPServiceRecord)record;
            rec.setMMSAuthenticationHeader(header);
            rec.saveData();
         }
      } catch (Throwable var4) {
         System.out.println("MMSC.setAuthenticationHeader " + e.toString());
         return;
      }
   }

   public static final boolean isWAPServiceRecord() {
      return getServiceRecord() instanceof WAPServiceRecord;
   }

   public static final int getWAPAccessMode() {
      Object record = getServiceRecord();
      if (!(record instanceof WAPServiceRecord)) {
         return 0;
      }

      WAPServiceRecord rec = (WAPServiceRecord)record;
      return rec.getSecureAccess();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void setWAPAccessMode(int mode) {
      Object record = getServiceRecord();

      try {
         if (record instanceof WAPServiceRecord) {
            WAPServiceRecord rec = (WAPServiceRecord)record;
            rec.setSecureAccess(mode);
            rec.saveData();
            return;
         }
      } catch (Throwable var4) {
         System.out.println("MMSC.setWAPAccessMode " + e.toString());
         return;
      }
   }

   public static final int getWTLSClientType() {
      Object record = getServiceRecord();
      if (!(record instanceof WAPServiceRecord)) {
         return 0;
      }

      WAPServiceRecord rec = (WAPServiceRecord)record;
      return rec.getWtlsClientIdType();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void setWTLSClientType(int type) {
      Object record = getServiceRecord();

      try {
         if (record instanceof WAPServiceRecord) {
            WAPServiceRecord rec = (WAPServiceRecord)record;
            rec.setWtlsClientIdType(type);
            rec.saveData();
            return;
         }
      } catch (Throwable var4) {
         System.out.println("MMSC.setWTLSClientType " + e.toString());
         return;
      }
   }

   public static final int getWTLSMode() {
      Object record = getServiceRecord();
      if (!(record instanceof WAPServiceRecord)) {
         return 0;
      }

      WAPServiceRecord rec = (WAPServiceRecord)record;
      return rec.getWtlsModeValue();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void setWTLSMode(int mode) {
      Object record = getServiceRecord();

      try {
         if (record instanceof WAPServiceRecord) {
            WAPServiceRecord rec = (WAPServiceRecord)record;
            rec.setWtlsModeValue(mode);
            rec.saveData();
            return;
         }
      } catch (Throwable var4) {
         System.out.println("MMSC.setWTLSMode " + e.toString());
         return;
      }
   }

   public static final boolean getWAP20Conformance() {
      Object record = getServiceRecord();
      if (!(record instanceof WAPServiceRecord)) {
         return false;
      }

      WAPServiceRecord rec = (WAPServiceRecord)record;
      return rec.getWAP20Conformance() == 1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void setWAP20Conformance(boolean mode) {
      Object record = getServiceRecord();

      try {
         if (record instanceof WAPServiceRecord) {
            WAPServiceRecord rec = (WAPServiceRecord)record;
            rec.setWAP20Conformance(mode ? 1 : 0);
            rec.saveData();
            return;
         }
      } catch (Throwable var4) {
         System.out.println("MMSC.setWAP20Conformace " + e.toString());
         return;
      }
   }

   static final int getAutoRetrievalMode() {
      Object record = getServiceRecord();
      if (!(record instanceof WAPServiceRecord)) {
         if (!(record instanceof WPTCPServiceRecord)) {
            return -1;
         }

         WPTCPServiceRecord rec = (WPTCPServiceRecord)record;
         return rec.getPropertyAsInt(14);
      } else {
         WAPServiceRecord rec = (WAPServiceRecord)record;
         return rec.getMMSRetrievalMode();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final void setAutoRetrievalMode(int mode) {
      Object record = getServiceRecord();

      try {
         if (!(record instanceof WAPServiceRecord)) {
            if (record instanceof WPTCPServiceRecord) {
               WPTCPServiceRecord rec = (WPTCPServiceRecord)record;
               rec.setProperty(14, mode);
               rec.saveData();
               return;
            }
         } else {
            WAPServiceRecord rec = (WAPServiceRecord)record;
            rec.setMMSRetrievalMode(mode);
            rec.saveData();
         }
      } catch (Throwable var4) {
         System.out.println("MMSC.setAutoRetrievalMode " + e.toString());
         return;
      }
   }

   static final int getMMSCVersion() {
      Object record = getServiceRecord();
      if (!(record instanceof WAPServiceRecord)) {
         if (!(record instanceof WPTCPServiceRecord)) {
            return 16;
         }

         WPTCPServiceRecord rec = (WPTCPServiceRecord)record;
         return rec.getPropertyAsInt(17);
      } else {
         WAPServiceRecord rec = (WAPServiceRecord)record;
         return rec.getMMSCVersion();
      }
   }

   public static final boolean isInternalMMSC(String url) {
      return url != null && url.indexOf("mknowles") >= 0;
   }

   private static final boolean useGMETransport(String url) {
      return isInternalMMSC(url) || MMSOptions.getInstance().getOptionFlag(16);
   }

   private static final HostRoutingInfo getHostRoutingInfo(Object record) {
      HostRoutingTable hrt = getHostRoutingTable(record);
      if (hrt != null) {
         HostRoutingInfo[] hris = hrt.getHris();
         if (hris != null && hris.length > 0) {
            int idx = Math.max(0, hrt.getActiveIndex());
            return hris[idx];
         }
      }

      return null;
   }

   private static final HostRoutingTable getHostRoutingTable(Object record) {
      if (record instanceof WAPServiceRecord) {
         ServiceRecord rec = getWAPTransportServiceRecord();
         if (rec != null) {
            return rec.getAttachedHrt();
         }
      } else if (record instanceof WPTCPServiceRecord) {
         ServiceRecord rec = getWPTCPTransportServiceRecord();
         if (rec != null) {
            return rec.getAttachedHrt();
         }
      }

      return null;
   }

   private static final Object getServiceRecord() {
      String transportUID = getWAPTransportUID();
      if (transportUID != null) {
         return WAPServiceRecord.getRecord(WAPServiceRecord.SERVICE_CID, transportUID);
      }

      transportUID = getWPTCPTransportUID();
      return transportUID != null ? WPTCPServiceRecord.getRecord(WPTCPServiceRecord.SERVICE_CID, transportUID) : null;
   }

   private static final String getWAPTransportUID() {
      ServiceRecord rec = getWAPTransportServiceRecord();
      return rec != null ? rec.getUid() : null;
   }

   private static final ServiceRecord getWAPTransportServiceRecord() {
      ServiceBook sb = ServiceBook.getSB();
      if (sb != null) {
         ServiceRecord[] recs = sb.findRecordsByCid(WAPServiceRecord.SERVICE_CID);
         if (recs != null) {
            for (int idx = 0; idx < recs.length; idx++) {
               WAPServiceRecord wapRecord = WAPServiceRecord.getRecord(recs[idx]);
               if (wapRecord != null && wapRecord.getMMSCUrl() != null) {
                  return recs[idx];
               }
            }
         }
      }

      return null;
   }

   private static final String getWPTCPTransportUID() {
      ServiceRecord rec = getWPTCPTransportServiceRecord();
      return rec != null ? rec.getUid() : null;
   }

   private static final ServiceRecord getWPTCPTransportServiceRecord() {
      ServiceBook sb = ServiceBook.getSB();
      if (sb != null) {
         ServiceRecord[] recs = sb.findRecordsByCid(WPTCPServiceRecord.SERVICE_CID);
         if (recs != null) {
            for (int idx = 0; idx < recs.length; idx++) {
               WPTCPServiceRecord wptcpRecord = WPTCPServiceRecord.getRecord(recs[idx]);
               if (wptcpRecord != null && wptcpRecord.getPropertyAsString(13) != null) {
                  return recs[idx];
               }
            }
         }
      }

      return null;
   }

   private static final WAPPushSource getWAP20PushSource() {
      ServiceBook sb = ServiceBook.getSB();
      if (sb != null) {
         ServiceRecord[] recs = sb.findRecordsByCid("WAPPushConfig");
         if (recs != null) {
            for (int idx = 0; idx < recs.length; idx++) {
               WAPPushSource wapps = WAPPushSource.getService(recs[idx]);
               if (wapps != null && wapps.getConnectionType() == 8) {
                  return wapps;
               }
            }
         }
      }

      return null;
   }

   public static final String getPPGAddress() {
      WAPPushSource wapps = getWAP20PushSource();
      return wapps != null ? wapps.getPropertyAsString(17) : null;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void setPPGAddress(String url) {
      WAPPushSource wapps = getWAP20PushSource();

      try {
         if (wapps != null) {
            wapps.setProperty(17, url);
            wapps.saveData();
            return;
         }
      } catch (Throwable var4) {
         System.out.println("MMSC.setPPGAddress " + e.toString());
         return;
      }
   }
}
