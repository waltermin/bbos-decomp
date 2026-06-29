package net.rim.device.apps.internal.browser.stack;

import net.rim.device.api.browser.push.WAPPushSource;
import net.rim.device.api.hrt.GprsHRI;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.hrt.IPv4UdpDAC;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.options.GeneralProperty;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.cldc.io.waphttp.WAPConnectionRegistry;
import net.rim.device.internal.browser.wap.WAPServiceRecord;
import net.rim.device.internal.browser.wap.WPTCPServiceRecord;
import net.rim.vm.DebugSupport;

public final class SBInjector {
   private static final void injectServiceBooks() {
      try {
         byte[] wapInsecure = WAPServiceRecord.getEncodedData(0, null, null, 1, 0, 0, "", 0);
         if (DeviceInfo.isSimulator()) {
            injectWAPServiceBook("10.1.20.203:9201:8205", "rim.net.gprs", "WAP rimnet", "WAP Transport - Insecure (Sim)", "S 50003", wapInsecure);
         } else {
            injectWAPServiceBook("206.51.26.188:9201:8205", "blackberry.net", "WAP microcell", "WAP Transport - Insecure (RIM)", "S 50003", wapInsecure);
         }

         BrowserConfigRecord tempRecord = BrowserConfigRecord.getNewConfig(WAPServiceRecord.SERVICE_CID, "S 50003", 0);
         tempRecord.setPropertyAsString(1, "http://mknowles/wmldata/menu.wml");
         tempRecord.setPropertyAsString(11, "WAP (Insecure)");
         injectBrowserConfigServiceBook("WAP Browser Config - Insecure (Sim)", "S 50002", "Connection to WAP Gateway", tempRecord.getEncodedData());
         if (WAPConnectionRegistry.isWTLSInstalled()) {
            wapInsecure = WAPServiceRecord.getEncodedData(1, null, null, 1, 0, 0, "", 0);
            if (DeviceInfo.isSimulator()) {
               injectWAPServiceBook("10.1.20.203:9203:8205", "rim.net.gprs", "WAP rimnet", "WAP Transport - Secure (Sim)", "S 50001", wapInsecure);
            } else {
               injectWAPServiceBook("206.51.26.188:9203:8205", "blackberry.net", "WAP microcell", "WAP Transport - Secure (RIM)", "S 50001", wapInsecure);
            }

            tempRecord = BrowserConfigRecord.getNewConfig(WAPServiceRecord.SERVICE_CID, "S 50001", 0);
            tempRecord.setPropertyAsString(1, "http://mknowles/wmldata/menu.wml");
            tempRecord.setPropertyAsString(11, "WAP (Secure)");
            injectBrowserConfigServiceBook("WAP Browser Config - Secure (Sim)", "S 50000", "Connection to WAP Gateway", tempRecord.getEncodedData());
         }

         String uid = "S 00005";
         String ip = DebugSupport.getenv("MdsIP");
         if (ip == null) {
            uid = "S 00000";
            ip = "10.1.20.203";
         }

         injectIPPPServiceBook(
            ((StringBuffer)(new Object())).append(ip).append(":19781:19780").toString(), "rim.net.gprs", "mknowles", "MDS Transport (Sim)", uid, 2, null
         );
         BrowserConfigRecord tempRecordx = BrowserConfigRecord.getNewConfig(BrowserConfigRecord.IPPP_SERVICE_CID, uid, 1);
         tempRecordx.setPropertyAsString(1, "http://mknowles/wmldata/menu.wml");
         tempRecordx.setPropertyAsString(11, "Intranet");
         tempRecordx.setPropertyAsBoolean(33, true);
         tempRecordx.setPropertyAsBoolean(36, true);
         tempRecordx.setPropertyAsBoolean(38, true);
         tempRecordx.setPropertyAsInt(26, 3);
         injectBrowserConfigServiceBook("MDS Browser Config (Sim)", "S 50004", "Connection to MDS", tempRecordx.getEncodedData());
         String defaultConfigUID = GeneralProperty.getDefaultBrowserConfigServiceUID();
         if (defaultConfigUID == null || defaultConfigUID.length() == 0) {
            GeneralProperty.setDefaultBrowserConfigServiceUID("S 50004");
         }

         boolean isSim = DeviceInfo.isSimulator();
         WPTCPServiceRecord rec = (WPTCPServiceRecord)(new Object());
         if (isSim) {
            rec.setProperty(1, "browserportal:3128");
            rec.setProperty(8, "browserportal:3128");
         }

         rec.setProperty(2, true);
         rec.setProperty(3, true);
         injectTCPServiceBook(
            "10.1.20.203:19781:19780",
            isSim ? "rim.net.gprs" : "internet.com",
            isSim ? null : "wapuser1",
            isSim ? null : "wap",
            "tcp",
            "Device TCP/IP Transport (Sim)",
            "SV 50007",
            rec.getEncodedData()
         );
         tempRecordx = BrowserConfigRecord.getNewConfig(BrowserConfigRecord.TCP_SERVICE_CID, "SV 50007", 7);
         tempRecordx.setPropertyAsString(11, "Device HTTP");
         injectBrowserConfigServiceBook("Device TCP/IP Config (Sim)", "S 50008", "DEVICE HTTP", tempRecordx.getEncodedData());
      } finally {
         return;
      }
   }

   public static final void inject() {
      injectServiceBooks();
   }

   public static final void injectTCPBrowser() {
      try {
         WPTCPServiceRecord record = (WPTCPServiceRecord)(new Object());
         record.setProperty(3, true);
         record.setProperty(2, true);
         injectTCPServiceBook(null, null, null, null, "tcp", "TCP/IP Transport", "S TCP-T", record.getEncodedData());
         BrowserConfigRecord tempRecord = BrowserConfigRecord.getNewConfig(BrowserConfigRecord.TCP_SERVICE_CID, "S TCP-T", 3);
         tempRecord.setPropertyAsString(11, "Internet (TCP)");
         injectBrowserConfigServiceBook("TCP/IP Browser Config", "S TCP-BC", "TCP/IP Browser", tempRecord.getEncodedData());
      } finally {
         return;
      }
   }

   public static final void injectWLANTCPBrowser() {
      try {
         WPTCPServiceRecord record = (WPTCPServiceRecord)(new Object());
         record.setProperty(3, true);
         record.setProperty(2, true);
         record.setProperty(19, "wifi");
         injectTCPServiceBook("10.1.20.203:19781:19780", null, null, null, "tcp-wifi", "Wi-Fi TCP/IP Transport", "S TCP-WiFi", record.getEncodedData(), true);
         BrowserConfigRecord tempRecord = BrowserConfigRecord.getNewConfig(BrowserConfigRecord.TCP_SERVICE_CID, "S TCP-WiFi", 3);
         tempRecord.setPropertyAsString(1, "http://mobile.blackberry.com");
         tempRecord.setProvisionedBookmarks(
            new Object[]{BrowserConfigRecord.encodeBookmark("http://www.blackberry.com/select/wifiloginsuccess/", BrowserResources.getString(874))}
         );
         tempRecord.setPropertyAsString(24, BrowserResources.getString(873));
         tempRecord.setPropertyAsString(54, "wlan");
         tempRecord.setPropertyAsString(11, BrowserResources.getString(872));
         injectBrowserConfigServiceBook("Wi-Fi TCP/IP Browser", "S TCP-WBC", "Wi-Fi Browser", tempRecord.getEncodedData(), true);
      } finally {
         return;
      }
   }

   public static final void removeWLANTCPBrowser() {
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord configSr = sb.getRecordByUidAndCid("S TCP-WBC", BrowserConfigRecord.SERVICE_CID);
      if (configSr != null) {
         BrowserConfigRecord rec = BrowserConfigRecord.getDecodedConfig(configSr);
         if (rec != null) {
            ServiceRecord transportSr = sb.getRecordByUidAndCid(rec.getPropertyAsString(4), rec.getPropertyAsString(3));
            if (transportSr != null) {
               sb.removeRecord(transportSr);
            }
         }

         sb.removeRecord(configSr);
         sb.commit();
      }
   }

   public static final void injectSBWithBookmarks(boolean injectBookmarks) {
      try {
         String[] bookmarks = null;
         if (injectBookmarks) {
            bookmarks = new Object[40];
            int bookmarkCount = 0;

            for (int i = 1; i < 4; i++) {
               bookmarks[bookmarkCount++] = BrowserConfigRecord.encodeBookmark(
                  "http://mobile.yahoo.com/home", ((StringBuffer)(new Object("Mobile Yahoo"))).append(i).toString()
               );
               bookmarks[bookmarkCount++] = BrowserConfigRecord.encodeBookmark(
                  "http://www.google.com", ((StringBuffer)(new Object("Search - Google"))).append(i).toString()
               );
               bookmarks[bookmarkCount++] = BrowserConfigRecord.encodeBookmark(
                  "http://www.dictionary.com/wml", ((StringBuffer)(new Object("Dictionary.com"))).append(i).toString()
               );
               bookmarks[bookmarkCount++] = BrowserConfigRecord.encodeBookmark(
                  "http://mobile.globeandmail.com", ((StringBuffer)(new Object("Globe and Mail"))).append(i).toString()
               );
            }

            bookmarks[bookmarkCount++] = BrowserConfigRecord.encodeBookmark("", "Empty URL");
            bookmarks[bookmarkCount++] = BrowserConfigRecord.encodeBookmark("http://www.cnn.com", "");
         }

         byte[] wapInsecure = WAPServiceRecord.getEncodedData(0, null, null, 1, 0, 0, "", 0);
         if (DeviceInfo.isSimulator()) {
            injectWAPServiceBook(
               "10.1.20.203:9201:8205", "rim.net.gprs", "WAP rimnet", "WAP Transport - Insecure Bookmarks (Sim)", "SV 50009", wapInsecure, true
            );
         } else {
            injectWAPServiceBook(
               "206.51.26.188:9201:8205", "blackberry.net", "WAP microcell", "WAP Transport - Insecure Bookmarks (RIM)", "SV 50009", wapInsecure, true
            );
         }

         BrowserConfigRecord tempRecord = BrowserConfigRecord.getNewConfig(WAPServiceRecord.SERVICE_CID, "SV 50009", 0);
         tempRecord.setPropertyAsString(1, "http://mknowles/wmldata/menu.wml");
         tempRecord.setPropertyAsString(11, ((StringBuffer)(new Object("WAP (Insecure-Bookmarks) "))).append(injectBookmarks).toString());
         tempRecord.setProvisionedBookmarks(bookmarks);
         injectBrowserConfigServiceBook(
            "WAP Browser Config - Insecure Bookmarks (Sim)", "S 50010", "Connection to WAP Gateway", tempRecord.getEncodedData(), true
         );
      } finally {
         return;
      }
   }

   public static final void injectWAP1xPush() {
      try {
         injectInternal(
            "127.0.0.1:9002:2948",
            null,
            null,
            null,
            "WAP Push (UDP)",
            "WAP Push Config (UDP)",
            "WAPPushConfig",
            "SV 000001",
            "WAP Push UDP Bearer",
            0,
            1,
            1,
            WAPPushSource.getEncodedData(7, -1, null, -1, null, -1, null, 0, 0, 0, 0, -1, null, -1, -1, 1),
            false
         );
         injectInternal(
            "127.0.0.1:9002:2948",
            null,
            null,
            null,
            "WAP Push (SMS)",
            "WAP Push Config (SMS)",
            "WAPPushConfig",
            "SV 000002",
            "WAP Push SMS Bearer",
            0,
            1,
            1,
            WAPPushSource.getEncodedData(6, 0, "", 0, "", 0, "", 0, 0, 0, 0, -1, null, -1, -1, 1),
            false
         );
      } finally {
         return;
      }
   }

   public static final void injectWAP20Push(
      boolean secure,
      String apn,
      String apnUsername,
      String apnPassword,
      String hrtName,
      String sbName,
      String ppgAddress,
      int clientIdType,
      int threadPoolSize
   ) {
      try {
         if (!secure) {
            injectInternal(
               "127.0.0.1:9002:4035",
               apn,
               apnUsername,
               apnPassword,
               hrtName,
               sbName,
               "WAPPushConfig",
               "MMS 50010",
               "Device HTTP Server",
               0,
               1,
               1,
               WAPPushSource.getEncodedData(8, 0, "", 0, "", 0, "", 0, 0, 0, 0, clientIdType, ppgAddress, 2, Integer.MAX_VALUE, threadPoolSize),
               false
            );
            return;
         }

         injectInternal(
            "127.0.0.1:9002:4036",
            apn,
            apnUsername,
            apnPassword,
            hrtName,
            sbName,
            "WAPPushConfig",
            "MMS 50011",
            "Device HTTPS Server",
            0,
            1,
            1,
            WAPPushSource.getEncodedData(8, 0, "", 0, "", 0, "", 0, 0, 0, 1, clientIdType, ppgAddress, 2, Integer.MAX_VALUE, threadPoolSize),
            false
         );
      } finally {
         return;
      }
   }

   public static final void injectMMS() {
      if (DeviceInfo.isSimulator()) {
         injectMMS("10.1.20.203:9201:8205", "rim.net.gprs", "MMS rimnet", "MMS Transport (RIM)", null, null, "http://mknowles-temp.rim.net:82/");
      } else {
         injectMMS("206.51.26.188:9201:8205", "blackberry.net", "MMS rimnet", "MMS Transport (RIM)", null, null, "http://mknowles-temp.rim.net:82/");
      }
   }

   public static final void injectMMS(String hostIP, String apn, String hrtName, String sbName, String username, String password, String url) {
      try {
         byte[] data = WAPServiceRecord.getEncodedData(0, username, password, 1, 0, 0, "", 0, url, -1, null, null, 16);
         injectWAPServiceBook(hostIP, apn, hrtName, sbName, "MMS 50009", data, true);
      } finally {
         return;
      }
   }

   public static final void injectTCPMMS(
      String hostIP,
      String apn,
      String apnUsername,
      String apnPassword,
      String hrtName,
      String sbName,
      String username,
      String password,
      String url,
      String httpProxy
   ) {
      injectTCPMMS(hostIP, apn, apnUsername, apnPassword, hrtName, sbName, username, 0, password, 0, url, httpProxy);
   }

   public static final void injectTCPMMS(
      String hostIP,
      String apn,
      String apnUsername,
      String apnPassword,
      String hrtName,
      String sbName,
      String username,
      int usernameType,
      String password,
      int passwordType,
      String url,
      String httpProxy
   ) {
      try {
         WPTCPServiceRecord record = (WPTCPServiceRecord)(new Object());
         record.setProperty(1, httpProxy);
         record.setProperty(8, httpProxy);
         record.setProperty(2, true);
         record.setProperty(3, true);
         record.setProperty(12, password);
         record.setProperty(11, username);
         record.setProperty(9, usernameType);
         record.setProperty(10, passwordType);
         record.setProperty(13, url);
         byte[] data = record.getEncodedData();
         injectTCPServiceBook(hostIP, apn, apnUsername, apnPassword, hrtName, sbName, "MMS 50009", data, true);
      } finally {
         return;
      }
   }

   public static final boolean injectAppDownload() {
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord[] records = sb.findRecordsByCid(WAPServiceRecord.SERVICE_CID);
      if (records.length > 0) {
         try {
            BrowserConfigRecord tempRecord = BrowserConfigRecord.getNewConfig(records[0].getCid(), records[0].getUid(), 6);
            tempRecord.setPropertyAsString(1, "http://216.9.243.249/appdemo/net_rim_bb_app_download.jad");
            tempRecord.setPropertyAsString(11, "Application Download");
            tempRecord.setPropertyAsString(28, "net_rim_bb_app_download");
            tempRecord.setPropertyAsString(29, "http://216.9.243.249/appdemo/icon.gif");
            tempRecord.setPropertyAsInt(2, 1);
            injectBrowserConfigServiceBook("App Download Browser Config", "S 50020", "Connection to WAP Gateway", tempRecord.getEncodedData());
            return true;
         } finally {
            return false;
         }
      } else {
         return false;
      }
   }

   private static final void injectBrowserConfigServiceBook(String sbName, String uid, String sbDescription, byte[] appData, boolean overwrite) {
      injectInternal(null, null, null, null, null, sbName, BrowserConfigRecord.SERVICE_CID, uid, sbDescription, 0, 1, 1, appData, overwrite);
   }

   private static final void injectWAPServiceBook(String hostIP, String apn, String hrtName, String sbName, String uid, byte[] appData, boolean overwrite) {
      injectInternal(hostIP, apn, null, null, hrtName, sbName, WAPServiceRecord.SERVICE_CID, uid, "Connection to WAP Gateway", 0, 1, 1, appData, overwrite);
   }

   private static final void injectBrowserConfigServiceBook(String sbName, String uid, String sbDescription, byte[] appData) {
      injectInternal(null, null, null, null, null, sbName, BrowserConfigRecord.SERVICE_CID, uid, sbDescription, 0, 1, 1, appData, false);
   }

   private static final void injectWAPServiceBook(String hostIP, String apn, String hrtName, String sbName, String uid, byte[] appData) {
      injectInternal(hostIP, apn, null, null, hrtName, sbName, WAPServiceRecord.SERVICE_CID, uid, "Connection to WAP Gateway", 0, 1, 1, appData, false);
   }

   private static final void injectIPPPServiceBook(String hostIP, String apn, String hrtName, String sbName, String uid, int sbCompress, byte[] appData) {
      injectInternal(hostIP, apn, null, null, hrtName, sbName, BrowserConfigRecord.IPPP_SERVICE_CID, uid, "Connection to MDS", 0, 1, sbCompress, appData, false);
   }

   private static final void injectTCPServiceBook(
      String hostIP, String apn, String apnUsername, String apnPassword, String hrtName, String sbName, String uid, byte[] appData
   ) {
      injectInternal(hostIP, apn, apnUsername, apnPassword, hrtName, sbName, BrowserConfigRecord.TCP_SERVICE_CID, uid, "Device HTTP", 0, 1, 1, appData, false);
   }

   private static final void injectTCPServiceBook(
      String hostIP, String apn, String apnUsername, String apnPassword, String hrtName, String sbName, String uid, byte[] appData, boolean overwrite
   ) {
      injectInternal(
         hostIP, apn, apnUsername, apnPassword, hrtName, sbName, BrowserConfigRecord.TCP_SERVICE_CID, uid, "Device HTTP", 0, 1, 1, appData, overwrite
      );
   }

   private static final void injectInternal(
      String hostIP,
      String apn,
      String apnUsername,
      String apnPassword,
      String hrtName,
      String sbName,
      String sbCid,
      String sbUid,
      String sbDescription,
      int sbType,
      int sbEncryption,
      int sbCompression,
      byte[] sbAppData,
      boolean overwrite
   ) {
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord sr = sb.getRecordByUidAndCid(sbUid, sbCid);
      if (overwrite || sr == null) {
         HostRoutingTable hrt = null;
         if (hostIP != null) {
            hrt = (HostRoutingTable)(new Object());
            HostRoutingInfo hri = null;
            int wafs = RadioInfo.getSupportedWAFs();
            if ((wafs & 2) != 0) {
               hri = (HostRoutingInfo)(new Object());
               hri.setNpc((long)64);
            } else if ((wafs & 1) != 0) {
               GprsHRI gHri = (GprsHRI)(new Object());
               hri = gHri;
               gHri.setApn(apn);
               gHri.setApnUsername(apnUsername);
               gHri.setApnPassword(apnPassword);
               hri.setNpc((long)48);
            } else {
               hri = (HostRoutingInfo)(new Object());
               hri.setNpc((long)80);
            }

            long[] dacs = new long[]{IPv4UdpDAC.string2Addr(hostIP)};
            ((IPv4UdpDAC)hri.getDac()).setAddresses(dacs);
            hri.setName(hrtName);
            if (!hrt.addHri(hri)) {
               return;
            }

            hrt.commit();
         }

         ServiceRecord rec = (ServiceRecord)(new Object());
         rec.setType(sbType);
         rec.setName(sbName);
         rec.setUid(sbUid);
         rec.setCid(sbCid);
         rec.setEncryptionMode(sbEncryption);
         rec.setCompressionMode(sbCompression);
         rec.setDescription(sbDescription);
         rec.setApplicationData(sbAppData);
         if (hrt != null) {
            rec.setAttachedHrt(hrt);
         }

         if (sb.addRecord(rec) != null) {
            sb.commit();
         }
      }
   }
}
