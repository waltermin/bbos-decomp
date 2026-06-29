package net.rim.device.apps.internal.browser.sbloader;

import java.io.ByteArrayInputStream;
import net.rim.device.api.browser.util.UserAgent;
import net.rim.device.api.compress.GZIPInputStream;
import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.browser.wap.WPTCPServiceRecord;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;
import net.rim.vm.Array;
import net.rim.vm.EventLog;
import org.apache.oro.text.regexp.Pattern;
import org.apache.oro.text.regexp.PatternCompiler;
import org.apache.oro.text.regexp.PatternMatcher;
import org.apache.oro.text.regexp.Perl5Compiler;
import org.apache.oro.text.regexp.Perl5Matcher;

public class SBHelper {
   public static int BUFFER_SIZE = 1024;
   public static String SB_EXTENSION = ".sbc";
   private static SBHelper _instance = new SBHelper();
   private static String _deviceName;
   private static String _platformVersion;

   private SBHelper() {
   }

   static String getDeviceName() {
      if (_deviceName != null) {
         return _deviceName;
      }

      String name = DeviceInfo.getDeviceName();
      if (name != null && InternalServices.getFormFactor() != 10) {
         name = "Rim" + name;
      }

      byte[] data = Branding.getData(12292);
      if (data != null) {
         name = name + '/' + new String(data);
      }

      _deviceName = name;
      return name;
   }

   static String getPlatformVersion() {
      if (_platformVersion != null) {
         return _platformVersion;
      }

      _platformVersion = DeviceInfo.getPlatformVersion();
      return _platformVersion;
   }

   static String getApplicationVersion() {
      return UserAgent.getBrowserVersion();
   }

   public boolean quitInstallation() {
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord[] wapRecs = sb.findRecordsByCid("WAP");
      if (wapRecs != null && wapRecs.length > 0) {
         return true;
      }

      ServiceRecord[] recs = sb.findRecordsByCid(WPTCPServiceRecord.SERVICE_CID);
      if (recs == null) {
         return false;
      }

      for (ServiceRecord rec : recs) {
         WPTCPServiceRecord record = WPTCPServiceRecord.getRecord(rec);
         if (record != null && !StringUtilities.strEqualIgnoreCase(record.getPropertyAsString(19), "wifi", 1701707776)) {
            return true;
         }
      }

      return false;
   }

   public static SBHelper getInstance() {
      return _instance;
   }

   static String[] split(String s, char delim) {
      String[] elements = new String[0];
      StringBuffer sb = new StringBuffer("");
      int tokencount = 0;
      int len = s.length();

      for (int i = 0; i < len; i++) {
         if (s.charAt(i) != delim && i != len - 1) {
            sb.append(s.charAt(i));
         } else {
            Array.resize(elements, elements.length + 1);
            if (i == len - 1) {
               sb.append(s.charAt(i));
            }

            elements[tokencount] = sb.toString();
            sb = new StringBuffer("");
            tokencount++;
         }
      }

      return elements;
   }

   static String[] split(String s, String delim) {
      String[] elements = new String[0];
      int tokencount = 0;
      int curPos = 0;
      boolean done = false;

      while (!done) {
         int index = s.indexOf(delim, curPos);
         if (index != -1) {
            Array.resize(elements, elements.length + 1);
            elements[tokencount] = s.substring(curPos, index);
            curPos = index + 3;
            tokencount++;
         } else {
            if (curPos < s.length() - 1) {
               Array.resize(elements, elements.length + 1);
               elements[tokencount] = s.substring(curPos, s.length());
            }

            done = true;
         }
      }

      return elements;
   }

   private SbInfo[] getServiceBookRecords(String record) {
      String[] records = split(record, '#');
      int len = records.length;
      SbInfo[] recs = new SbInfo[len];

      for (int i = 0; i < len; i++) {
         String t1 = records[i];
         String[] content = split(t1, ':');
         String efgid = content[0];
         String region = content[1];
         String books = content[2];
         String devices = content[3];
         String service = content[4];
         String sapsoldto = content[5];
         String regex = content[6];
         SbInfo sbinfo = new SbInfo(efgid, region, books, devices, service, sapsoldto, regex);
         recs[i] = sbinfo;
      }

      return recs;
   }

   private DataBuffer readHomeNpcData(long homenpc) {
      DataBuffer sbRecordBuff = null;

      label75:
      try {
         if (SIMCard.isSupported()) {
            int primaryNetwork = RadioInternal.getPrimaryWAF();
            if (primaryNetwork != 2 && !SIMCard.is3DigitMNC()) {
               byte[] imsi = SIMCard.getIMSI();
               int mcc = SIMCard.getMCCFromIMSI(imsi);
               int mnc = SIMCard.getMNCFromIMSI(imsi);

               for (int i = 0; i < 10; i++) {
                  StringBuffer hnpcStr = new StringBuffer("");
                  hnpcStr.append(mcc);
                  if (mnc < 10) {
                     hnpcStr.append("0" + mnc % 100);
                  } else {
                     hnpcStr.append(mnc % 100);
                  }

                  hnpcStr.append(i);
                  switch (primaryNetwork) {
                     case 1:
                        hnpcStr.append("30");
                        break;
                     case 8:
                        hnpcStr.append("50");
                  }

                  homenpc = Long.parseLong(hnpcStr.toString(), 16);
                  sbRecordBuff = this.getCompressedResourceAsDataBuffer(homenpc + ".sbr.gz");
                  if (sbRecordBuff != null) {
                     return sbRecordBuff;
                  }
               }
            }
         }
      } finally {
         break label75;
      }

      if (sbRecordBuff == null) {
         sbRecordBuff = this.getCompressedResourceAsDataBuffer(homenpc + ".sbr.gz");
      }

      return sbRecordBuff;
   }

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void injectServiceBooks() {
      if (!this.quitInstallation()) {
         try {
            long start = System.currentTimeMillis();
            long homeNpc = HRUtils.getNpcForHomeNetwork(RadioInternal.getPrimaryWAF());
            String efgid = "";
            String efspn = "";
            if (SIMCard.isSupported()) {
               boolean var22 = false /* VF: Semaphore variable */;

               label175:
               try {
                  var22 = true;
                  efgid = readEFAsString(9);
                  efspn = readEFAsString(11);
                  efgid = efgid + efspn;
                  var22 = false;
               } finally {
                  if (var22) {
                     EventLog.logEvent(
                        1907089860548946979L,
                        System.currentTimeMillis(),
                        (byte)2,
                        ("SBHelper___ERROR:injectServiceBooks()" + homeNpc + ".sbr.gz EFGID could not be read").getBytes()
                     );
                     break label175;
                  }
               }
            }

            label172:
            try {
               DataBuffer sbRecordBuff = this.readHomeNpcData(homeNpc);
               EventLog.logEvent(
                  1907089860548946979L, System.currentTimeMillis(), (byte)4, ("SBHelper___INFO:injectServiceBooks()HOMENPC=" + homeNpc).getBytes()
               );
               if (sbRecordBuff == null) {
                  EventLog.logEvent(
                     1907089860548946979L,
                     System.currentTimeMillis(),
                     (byte)2,
                     ("SBHelper___ERROR:injectServiceBooks()" + homeNpc + ".sbr.gz not found").getBytes()
                  );
                  return;
               }

               String sbRecord = new String(sbRecordBuff.getArray());
               if (sbRecord == null) {
                  EventLog.logEvent(
                     1907089860548946979L,
                     System.currentTimeMillis(),
                     (byte)2,
                     ("SBHelper___ERROR:injectServiceBooks()" + homeNpc + ".sbr.gz does not have any data.").getBytes()
                  );
                  return;
               }

               SbInfo[] sbrecs = this.getServiceBookRecords(sbRecord);
               if (efgid != null && efgid.length() > 0) {
                  SbInfo[] withefgid = this.getListOfRecordsWithEfgid(sbrecs, efgid);
                  if (withefgid != null && withefgid.length > 0) {
                     SortedList list = new SortedList(new SbComparator(), withefgid);
                     if (list.size() > 0) {
                        this.injectServiceBooks((SbInfo)list.getAt(0));
                     }
                  } else {
                     SbInfo[] withoutefgid = this.getListOfRecordsWithEfgid(sbrecs, "");
                     if (withoutefgid != null && withoutefgid.length > 0) {
                        SortedList list = new SortedList(new SbComparator(), withoutefgid);
                        if (list.size() > 0) {
                           this.injectServiceBooks((SbInfo)list.getAt(0));
                        }
                     }
                  }
               } else {
                  SbInfo[] withoutefgid = this.getListOfRecordsWithEfgid(sbrecs, "");
                  if (withoutefgid != null && withoutefgid.length > 0) {
                     SortedList list = new SortedList(new SbComparator(), withoutefgid);
                     if (list.size() > 0) {
                        this.injectServiceBooks((SbInfo)list.getAt(0));
                     }
                  }
               }
            } catch (Throwable var23) {
               EventLog.logEvent(
                  1907089860548946979L, System.currentTimeMillis(), (byte)2, ("SBHelper:ERROR__injectServiceBooks()" + e.getMessage()).getBytes()
               );
               e.printStackTrace();
               break label172;
            }

            long end = System.currentTimeMillis();
            EventLog.logEvent(
               1907089860548946979L,
               System.currentTimeMillis(),
               (byte)4,
               ("SBHelper:INFO__Took " + (end - start) / 1000 + "seconds to inject service books.").getBytes()
            );
         } catch (Throwable var25) {
            EventLog.logEvent(1907089860548946979L, System.currentTimeMillis(), (byte)2, ("SBHelper___ERROR:injectServiceBooks()" + ex.getMessage()).getBytes());
            return;
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private boolean isServiceBookAllowed(String book, String regex) {
      String[] regexTokens = split(regex, "___");

      try {
         int len = regexTokens.length;

         for (int i = 0; i < len; i += 3) {
            boolean dev_matches = false;
            boolean os_matches = false;
            boolean app_matches = false;
            String osRegex = regexTokens[i];
            String appRegex = regexTokens[i + 1];
            String devRegex = regexTokens[i + 2];
            PatternCompiler compiler = new Perl5Compiler();
            Pattern os_pattern = compiler.compile(osRegex);
            Pattern app_pattern = compiler.compile(appRegex);
            Pattern dev_pattern = compiler.compile(devRegex);
            PatternMatcher matcher = new Perl5Matcher();
            if (matcher.matches(getDeviceName(), dev_pattern)) {
               dev_matches = true;
            }

            if (matcher.matches(getPlatformVersion(), os_pattern)) {
               os_matches = true;
            }

            if (matcher.matches(getApplicationVersion(), app_pattern)) {
               app_matches = true;
            }

            if (dev_matches && os_matches && app_matches) {
               return true;
            }
         }
      } catch (Throwable var18) {
         EventLog.logEvent(
            1907089860548946979L,
            System.currentTimeMillis(),
            (byte)2,
            ("SBHelper:ERROR__isServiceBookAllowed() book =" + book + " ; regex = " + regex + ":" + ex.getMessage()).getBytes()
         );
         ex.printStackTrace();
         return false;
      }

      return false;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void injectServiceBooks(SbInfo info) {
      if (!this.isOptedOut(info.getSapSoldTo())) {
         String[] regexes = split(info.getRegex(), ';');
         String[] books = split(info.getBooks(), ';');
         StringBuffer sbinfoSB = new StringBuffer("SBHelper___INFO: SAP_SOLD_TO=" + info.getSapSoldTo());
         ServiceBook sb = ServiceBook.getSB();
         int size = books.length;

         for (int i = 0; i < size; i++) {
            String servicebook = books[i];
            String regex = regexes[i];
            if (this.isServiceBookAllowed(servicebook, regex)) {
               DataBuffer databuf = new DataBuffer();
               StringBuffer filePath = new StringBuffer("out/sbooks");
               if (info.getRegion().equalsIgnoreCase("NA")) {
                  filePath.append("/naprv");
               } else if (info.getRegion().equalsIgnoreCase("EU")) {
                  filePath.append("/euprv");
               } else if (info.getRegion().equalsIgnoreCase("AP")) {
                  filePath.append("/apprv");
               }

               filePath.append("/" + servicebook + SB_EXTENSION);
               byte[] bytes = this.getResourceAsBytes(filePath.toString());
               if (bytes != null) {
                  databuf.setData(bytes, 0, bytes.length);

                  try {
                     ServiceRecord[] recs = ServiceRecord.parse(databuf, 3);
                     if (recs == null) {
                        return;
                     }

                     int len = recs.length;

                     for (int j = 0; j < len; j++) {
                        recs[j].setType(0);
                        if (sb.addRecord(recs[j]) != null) {
                           sb.commit();
                           sbinfoSB.append(" ;book=" + servicebook);
                        }
                     }
                  } catch (Throwable var17) {
                     EventLog.logEvent(
                        1907089860548946979L,
                        System.currentTimeMillis(),
                        (byte)2,
                        ("SBHelper:ERROR__injectServiceBooks(sbinfo)" + e.getMessage() + " for " + servicebook).getBytes()
                     );
                     e.printStackTrace();
                     continue;
                  }
               }
            }
         }

         EventLog.logEvent(
            1907089860548946979L, System.currentTimeMillis(), (byte)4, ("SBHelper:INFO__injectServiceBooks(sbinfo)" + sbinfoSB.toString()).getBytes()
         );
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private boolean isOptedOut(String sapSoldTo) {
      try {
         String contents = new String(this.getCompressedResourceAsDataBuffer("optout.txt.gz").toArray());
         if (contents != null) {
            String[] optoutCarriers = split(contents, ';');
            int len = optoutCarriers.length;

            for (int i = 0; i < len; i++) {
               if (optoutCarriers[i].equals(sapSoldTo)) {
                  return true;
               }
            }
         }
      } catch (Throwable var7) {
         e.printStackTrace();
         return false;
      }

      return false;
   }

   private byte[] getResourceAsBytes(String resourceName) {
      Resource resource = Resource$Internal.getResourceClass("net_rim_bb_sbinjector_lib");
      byte[] data = null;
      if (resource != null) {
         data = resource.getResource(resourceName);
      }

      return data;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private DataBuffer getCompressedResourceAsDataBuffer(String resourceName) {
      byte[] resource = this.getResourceAsBytes(resourceName);
      GZIPInputStream gin = null;
      DataBuffer db = null;
      boolean var28 = false /* VF: Semaphore variable */;

      label170: {
         try {
            label168:
            try {
               var28 = true;
               gin = new GZIPInputStream(new ByteArrayInputStream(resource));
               byte[] e = IOUtilities.streamToBytes(gin);
               db = new DataBuffer();
               db.setData(e, 0, e.length);
               var28 = false;
               break label170;
            } catch (Throwable var37) {
               e.printStackTrace();
               var28 = false;
               break label168;
            }
         } finally {
            if (var28) {
               label158:
               try {
                  if (gin != null) {
                     gin.close();
                  }
               } finally {
                  break label158;
               }
            }
         }

         try {
            if (gin != null) {
               gin.close();
            }

            return db;
         } finally {
            return db;
         }
      }

      try {
         if (gin != null) {
            gin.close();
         }
      } finally {
         return db;
      }

      return db;
   }

   private SbInfo[] getListOfRecordsWithEfgid(SbInfo[] records, String efgid) {
      SbInfo[] list = new SbInfo[0];

      for (SbInfo info : records) {
         if (info.getEfgid().equalsIgnoreCase(efgid)) {
            Array.resize(list, list.length + 1);
            list[list.length - 1] = info;
         }
      }

      return list;
   }

   private static String readEFAsString(int id) {
      StringBuffer sb = new StringBuffer("");
      byte[] bytes = readEF(id);
      if (bytes == null) {
         return sb.toString();
      }

      if (bytes.length == 0) {
         return sb.toString();
      }

      for (int i = 0; i < bytes.length; i++) {
         char ch = (char)bytes[i];
         if (ch == '\uffff') {
            return sb.toString();
         }

         if (ch > '\t') {
            sb.append(ch);
         } else {
            ch = (char)(ch + '0');
            sb.append(ch);
         }
      }

      return sb.toString();
   }

   private static byte[] readEF(int id) {
      byte[] data = new byte[128];

      try {
         if (SIMCard.isReady() && SIMCard.isValid()) {
            int i = 0;

            while (true) {
               int length = SIMCard.requestEFRead(id, 0, 0, data);
               if (length >= 0) {
                  Array.resize(data, length);
                  return data;
               }

               if (++i >= 5) {
                  return null;
               }

               try {
                  Thread.sleep(500);
               } finally {
                  continue;
               }
            }
         } else {
            return null;
         }
      } finally {
         ;
      }
   }
}
