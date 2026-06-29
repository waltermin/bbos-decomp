package net.rim.device.apps.internal.browser.cod;

import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.system.Phone;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.browser.OTAStatusReportService;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;

public final class OTAStatusReportSender implements OTAStatusReportService, Persistable {
   private static final String CALLING_LINE_ID_HEADER = "x-up-calling-line-id";
   private static OTAStatusReportSender$OTAData _otaData;
   private static PersistentObject _persist = PersistentStore.getPersistentObject(1718628937688454370L);
   private static OTAStatusReportSender _instance;

   private OTAStatusReportSender() {
   }

   public static final OTAStatusReportSender getOTAStatusReportSender() {
      if (_instance == null) {
         _instance = new OTAStatusReportSender();
      }

      return _instance;
   }

   @Override
   public final String getStatusMessage(int status) {
      return getStatusMessage(status, false);
   }

   static final String getStatusMessage(int status, boolean isCod) {
      switch (status) {
         case 899:
            return null;
         case 900:
         default:
            return "Success";
         case 901:
            return "Insufficient Memory";
         case 902:
            return "User Cancelled";
         case 903:
            return "Loss of Service";
         case 904:
            return "JAR size mismatch";
         case 905:
            return "Attribute Mismatch";
         case 906:
            return "Invalid Descriptor";
         case 907:
            if (isCod) {
               return "Invalid COD";
            }

            return "Invalid JAR";
         case 908:
            return "Incompatible Configuration or Profile";
         case 909:
            return "Application authentication failure";
         case 910:
            return "Application authorization failure";
         case 911:
            return "Push registration failure";
         case 912:
            return "Deletion Notification";
         case 913:
            return "Required package not supported by the device";
      }
   }

   @Override
   public final synchronized void sendReport(String moduleGroupName, String url, int status, String transportServiceCID) {
      if (transportServiceCID == null) {
         BrowserSession session = BrowserSession.getCurrentSession();
         if (session != null) {
            transportServiceCID = session.getConfig().getPropertyAsString(3);
         }
      }

      this.sendReport(moduleGroupName, new OTAStatusReportSender$Report(url, status, transportServiceCID));
   }

   private final synchronized void sendReport(String moduleGroupName, OTAStatusReportSender$Report report) {
      _otaData._sendQueue.put(moduleGroupName, report);
      _persist.commit();
      Thread sendThread = new OTAStatusReportSender$1(this, moduleGroupName, report);
      sendThread.start();
   }

   @Override
   public final synchronized void resendReport(String moduleGroupName) {
      OTAStatusReportSender$Report report = (OTAStatusReportSender$Report)_otaData._sendQueue.get(moduleGroupName);
      if (report != null) {
         Thread sendThread = new OTAStatusReportSender$2(this, moduleGroupName, report);
         sendThread.start();
      }
   }

   @Override
   public final synchronized boolean hasReportQueued(String moduleGroupName) {
      return _otaData._sendQueue.containsKey(moduleGroupName);
   }

   private final void doSend(String param1, OTAStatusReportSender$Report param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 000: bipush 0
      // 001: istore 3
      // 002: invokestatic net/rim/device/apps/internal/browser/core/BrowserDaemonRegistry.getInstance ()Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 005: astore 4
      // 007: aconst_null
      // 008: getstatic net/rim/device/apps/internal/browser/options/BrowserConfigRecord.INVALID_VALUE I
      // 00b: aload 2
      // 00c: getfield net/rim/device/apps/internal/browser/cod/OTAStatusReportSender$Report._transportServiceCID Ljava/lang/String;
      // 00f: invokestatic net/rim/device/apps/internal/browser/options/BrowserConfigRecord.getDecodedConfig (Ljava/lang/String;ILjava/lang/String;)Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;
      // 012: astore 5
      // 014: new net/rim/device/api/io/http/HttpHeaders
      // 017: dup
      // 018: invokespecial net/rim/device/api/io/http/HttpHeaders.<init> ()V
      // 01b: astore 6
      // 01d: aload 6
      // 01f: bipush 0
      // 020: invokestatic net/rim/device/apps/internal/browser/common/RenderingUtilities.setTranscodeHeader (Lnet/rim/device/api/io/http/HttpHeaders;Z)V
      // 023: aload 4
      // 025: aload 6
      // 027: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.addStandardRequestHeaders (Lnet/rim/device/api/io/http/HttpHeaders;)V
      // 02a: aload 6
      // 02c: aload 5
      // 02e: invokestatic net/rim/device/apps/internal/browser/cod/OTAStatusReportSender.addCallingLineID (Lnet/rim/device/api/io/http/HttpHeaders;Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;)V
      // 031: new net/rim/device/apps/internal/browser/stack/ModelResult
      // 034: dup
      // 035: aload 2
      // 036: invokevirtual net/rim/device/apps/internal/browser/cod/OTAStatusReportSender$Report.getURL ()Ljava/lang/String;
      // 039: bipush 3
      // 03b: aload 6
      // 03d: invokespecial net/rim/device/apps/internal/browser/stack/ModelResult.<init> (Ljava/lang/String;ILnet/rim/device/api/io/http/HttpHeaders;)V
      // 040: astore 7
      // 042: aload 7
      // 044: new java/lang/StringBuffer
      // 047: dup
      // 048: invokespecial java/lang/StringBuffer.<init> ()V
      // 04b: aload 2
      // 04c: getfield net/rim/device/apps/internal/browser/cod/OTAStatusReportSender$Report._status I
      // 04f: invokestatic java/lang/String.valueOf (I)Ljava/lang/String;
      // 052: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 055: bipush 32
      // 057: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 05a: aload 0
      // 05b: aload 2
      // 05c: getfield net/rim/device/apps/internal/browser/cod/OTAStatusReportSender$Report._status I
      // 05f: invokevirtual net/rim/device/apps/internal/browser/cod/OTAStatusReportSender.getStatusMessage (I)Ljava/lang/String;
      // 062: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 065: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 068: invokevirtual java/lang/String.getBytes ()[B
      // 06b: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.setPostData ([B)V
      // 06e: sipush 400
      // 071: istore 8
      // 073: new net/rim/device/apps/internal/browser/stack/FetchRequest
      // 076: dup
      // 077: aload 7
      // 079: aload 5
      // 07b: invokespecial net/rim/device/apps/internal/browser/stack/FetchRequest.<init> (Lnet/rim/device/apps/internal/browser/stack/ModelResult;Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;)V
      // 07e: astore 9
      // 080: aconst_null
      // 081: astore 10
      // 083: aload 4
      // 085: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getRawDataCache ()Lnet/rim/device/apps/internal/browser/stack/RawDataCache;
      // 088: astore 11
      // 08a: aload 11
      // 08c: aload 9
      // 08e: invokevirtual net/rim/device/apps/internal/browser/stack/RawDataCache.get (Lnet/rim/device/apps/internal/browser/stack/FetchRequest;)Ljavax/microedition/io/InputConnection;
      // 091: astore 10
      // 093: aload 7
      // 095: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getCacheResult ()Lnet/rim/device/apps/internal/browser/stack/CacheResult;
      // 098: astore 12
      // 09a: aload 12
      // 09c: ifnull 0bd
      // 09f: aload 12
      // 0a1: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.getStatus ()I
      // 0a4: istore 8
      // 0a6: aload 12
      // 0a8: ifnull 0bd
      // 0ab: iload 8
      // 0ad: sipush 200
      // 0b0: if_icmplt 0bd
      // 0b3: iload 8
      // 0b5: sipush 300
      // 0b8: if_icmpge 0bd
      // 0bb: bipush 1
      // 0bc: istore 3
      // 0bd: aload 10
      // 0bf: ifnull 0fd
      // 0c2: aload 10
      // 0c4: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0c9: goto 0fd
      // 0cc: astore 11
      // 0ce: goto 0fd
      // 0d1: astore 11
      // 0d3: aload 10
      // 0d5: ifnull 0fd
      // 0d8: aload 10
      // 0da: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0df: goto 0fd
      // 0e2: astore 11
      // 0e4: goto 0fd
      // 0e7: astore 13
      // 0e9: aload 10
      // 0eb: ifnull 0fa
      // 0ee: aload 10
      // 0f0: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0f5: goto 0fa
      // 0f8: astore 14
      // 0fa: aload 13
      // 0fc: athrow
      // 0fd: new java/lang/StringBuffer
      // 100: dup
      // 101: ldc_w "OTAr:"
      // 104: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 107: aload 2
      // 108: getfield net/rim/device/apps/internal/browser/cod/OTAStatusReportSender$Report._status I
      // 10b: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 10e: bipush 58
      // 110: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 113: iload 8
      // 115: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 118: astore 11
      // 11a: aload 11
      // 11c: bipush 10
      // 11e: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 121: aload 1
      // 122: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 125: pop
      // 126: aload 11
      // 128: bipush 10
      // 12a: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 12d: aload 2
      // 12e: invokevirtual net/rim/device/apps/internal/browser/cod/OTAStatusReportSender$Report.getURL ()Ljava/lang/String;
      // 131: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 134: pop
      // 135: ldc2_w 1907089860548946979
      // 138: aload 11
      // 13a: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 13d: invokevirtual java/lang/String.getBytes ()[B
      // 140: bipush 5
      // 142: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 145: pop
      // 146: iload 3
      // 147: ifeq 16a
      // 14a: getstatic net/rim/device/apps/internal/browser/cod/OTAStatusReportSender._otaData Lnet/rim/device/apps/internal/browser/cod/OTAStatusReportSender$OTAData;
      // 14d: getfield net/rim/device/apps/internal/browser/cod/OTAStatusReportSender$OTAData._sendQueue Ljava/util/Hashtable;
      // 150: aload 1
      // 151: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 154: aload 2
      // 155: if_acmpne 18e
      // 158: getstatic net/rim/device/apps/internal/browser/cod/OTAStatusReportSender._otaData Lnet/rim/device/apps/internal/browser/cod/OTAStatusReportSender$OTAData;
      // 15b: getfield net/rim/device/apps/internal/browser/cod/OTAStatusReportSender$OTAData._sendQueue Ljava/util/Hashtable;
      // 15e: aload 1
      // 15f: invokevirtual java/util/Hashtable.remove (Ljava/lang/Object;)Ljava/lang/Object;
      // 162: pop
      // 163: getstatic net/rim/device/apps/internal/browser/cod/OTAStatusReportSender._persist Lnet/rim/device/api/system/PersistentObject;
      // 166: invokevirtual net/rim/device/api/system/PersistentObject.commit ()V
      // 169: return
      // 16a: aload 2
      // 16b: dup
      // 16c: getfield net/rim/device/apps/internal/browser/cod/OTAStatusReportSender$Report._numAttempts I
      // 16f: bipush 1
      // 170: iadd
      // 171: putfield net/rim/device/apps/internal/browser/cod/OTAStatusReportSender$Report._numAttempts I
      // 174: aload 2
      // 175: getfield net/rim/device/apps/internal/browser/cod/OTAStatusReportSender$Report._numAttempts I
      // 178: bipush 5
      // 17a: if_icmplt 188
      // 17d: getstatic net/rim/device/apps/internal/browser/cod/OTAStatusReportSender._otaData Lnet/rim/device/apps/internal/browser/cod/OTAStatusReportSender$OTAData;
      // 180: getfield net/rim/device/apps/internal/browser/cod/OTAStatusReportSender$OTAData._sendQueue Ljava/util/Hashtable;
      // 183: aload 1
      // 184: invokevirtual java/util/Hashtable.remove (Ljava/lang/Object;)Ljava/lang/Object;
      // 187: pop
      // 188: getstatic net/rim/device/apps/internal/browser/cod/OTAStatusReportSender._persist Lnet/rim/device/api/system/PersistentObject;
      // 18b: invokevirtual net/rim/device/api/system/PersistentObject.commit ()V
      // 18e: return
      // try (86 -> 88): 89 null
      // try (59 -> 84): 91 null
      // try (94 -> 96): 97 null
      // try (59 -> 84): 99 null
      // try (91 -> 92): 99 null
      // try (102 -> 104): 105 null
      // try (99 -> 100): 99 null
   }

   @Override
   public final synchronized void addDeleteNotifyApp(String moduleGroupName, String url, String transportServiceCID) {
      if (transportServiceCID == null) {
         BrowserSession session = BrowserSession.getCurrentSession();
         if (session != null) {
            transportServiceCID = session.getConfig().getPropertyAsString(3);
         }
      }

      _otaData._deleteNotifyApps.put(moduleGroupName, new OTAStatusReportSender$Report(url, 912, transportServiceCID));
      _persist.commit();
   }

   @Override
   public final void appDeleted(String moduleGroupName) {
      OTAStatusReportSender$Report report = (OTAStatusReportSender$Report)_otaData._deleteNotifyApps.remove(moduleGroupName);
      if (report != null) {
         this.sendReport(moduleGroupName, report);
      }
   }

   @Override
   public final synchronized void sendAllReports() {
      if (!_otaData._sendQueue.isEmpty()) {
         Thread sendThread = new OTAStatusReportSender$3(this);
         sendThread.start();
      }
   }

   public static final void addCallingLineID(HttpHeaders requestHeaders, BrowserConfigRecord config) {
      if (config != null && config.getPropertyAsInt(23) == 1) {
         String callingLineId = getCallingLineId();
         if (callingLineId != null) {
            requestHeaders.setProperty("x-up-calling-line-id", callingLineId);
         }
      }
   }

   private static final String getCallingLineId() {
      String phoneNumber = null;

      try {
         return Phone.getInstance().getNumber(0);
      } finally {
         ;
      }
   }

   static {
      synchronized (_persist) {
         if (_persist.getContents() == null) {
            _otaData = new OTAStatusReportSender$OTAData();
            _persist.setContents(_otaData, 51);
            _persist.commit();
         } else {
            _otaData = (OTAStatusReportSender$OTAData)_persist.getContents();
         }
      }
   }
}
