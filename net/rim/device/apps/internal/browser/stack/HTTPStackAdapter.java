package net.rim.device.apps.internal.browser.stack;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import javax.microedition.io.SecurityInfo;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.common.AbortListener;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserStateListener;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.util.StatsManager;
import net.rim.device.cldc.io.utility.SessionStats;
import net.rim.device.cldc.io.waphttp.WAPConnectionRegistry;
import net.rim.device.internal.browser.wap.WAPServiceRecord;
import net.rim.device.internal.crypto.CryptoBlock;

public final class HTTPStackAdapter implements NetworkPageFetcher, AbortListener, BrowserStateListener {
   private Object _syncObject = new Object();
   private boolean _successfulConnect;
   private StatsManager _statsManager;
   private static final int TYPE_WAP = 1;
   private static final int TYPE_IPPP = 2;
   private static final int TYPE_TCP = 3;
   private static String HEADER_LENGTH = "content-length";
   private static String CONTENT_TYPE = "content-type";

   @Override
   public final void browserStateChanged(int newState) {
      if (newState == 0 || newState == 2) {
         synchronized (this._syncObject) {
            if (this._statsManager != null) {
               this._statsManager.sessionOver();
            }

            this._successfulConnect = false;
         }

         WAPConnectionRegistry.closeAllConnections();
      }
   }

   public final SessionStats getConnectionStats() {
      return this._statsManager != null ? this._statsManager.getCurrentSessionsStats() : null;
   }

   public final InputConnection fetchPageInternal(FetchRequest param1, BrowserConfigRecord param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.getModelResult ()Lnet/rim/device/apps/internal/browser/stack/ModelResult;
      // 004: astore 3
      // 005: aload 3
      // 006: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getCacheResult ()Lnet/rim/device/apps/internal/browser/stack/CacheResult;
      // 009: astore 4
      // 00b: aload 2
      // 00c: ifnonnull 024
      // 00f: aload 4
      // 011: sipush 233
      // 014: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 017: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setExceptionString (Ljava/lang/String;)V
      // 01a: aload 4
      // 01c: sipush 400
      // 01f: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setStatus (I)V
      // 022: aconst_null
      // 023: areturn
      // 024: invokestatic net/rim/device/cldc/io/fastdormancy/FastDormancyManager.getInstance ()Lnet/rim/device/cldc/io/fastdormancy/FastDormancyManager;
      // 027: bipush 0
      // 028: invokevirtual net/rim/device/cldc/io/fastdormancy/FastDormancyManager.setFastDormancy (Z)V
      // 02b: invokestatic net/rim/device/apps/api/iota/IOTAManager.getInstance ()Lnet/rim/device/apps/api/iota/IOTAManager;
      // 02e: astore 5
      // 030: aload 5
      // 032: ifnull 058
      // 035: aload 5
      // 037: invokevirtual net/rim/device/apps/api/iota/IOTAManager.currentStatus ()I
      // 03a: bipush 1
      // 03b: if_icmpne 058
      // 03e: ldc2_w 1907089860548946979
      // 041: ldc_w 1214407497
      // 044: invokestatic net/rim/device/api/system/EventLogger.logEvent (JI)Z
      // 047: pop
      // 048: aload 5
      // 04a: invokevirtual net/rim/device/apps/api/iota/IOTAManager.cancelIOTA ()Z
      // 04d: pop
      // 04e: ldc2_w 1907089860548946979
      // 051: ldc_w 1214341961
      // 054: invokestatic net/rim/device/api/system/EventLogger.logEvent (JI)Z
      // 057: pop
      // 058: aconst_null
      // 059: astore 6
      // 05b: aconst_null
      // 05c: astore 7
      // 05e: aconst_null
      // 05f: astore 8
      // 061: bipush 3
      // 063: istore 9
      // 065: bipush 0
      // 066: istore 10
      // 068: aconst_null
      // 069: astore 11
      // 06b: aload 2
      // 06c: bipush 3
      // 06e: invokevirtual net/rim/device/apps/internal/browser/options/BrowserConfigRecord.getPropertyAsString (I)Ljava/lang/String;
      // 071: astore 12
      // 073: aload 12
      // 075: getstatic net/rim/device/apps/internal/browser/options/BrowserConfigRecord.IPPP_SERVICE_CID Ljava/lang/String;
      // 078: ldc_w 1701707776
      // 07b: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 07e: ifeq 088
      // 081: bipush 2
      // 083: istore 9
      // 085: goto 099
      // 088: aload 12
      // 08a: getstatic net/rim/device/internal/browser/wap/WAPServiceRecord.SERVICE_CID Ljava/lang/String;
      // 08d: ldc_w 1701707776
      // 090: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 093: ifeq 099
      // 096: bipush 1
      // 097: istore 9
      // 099: aload 0
      // 09a: getfield net/rim/device/apps/internal/browser/stack/HTTPStackAdapter._syncObject Ljava/lang/Object;
      // 09d: dup
      // 09e: astore 13
      // 0a0: monitorenter
      // 0a1: aload 0
      // 0a2: getfield net/rim/device/apps/internal/browser/stack/HTTPStackAdapter._statsManager Lnet/rim/device/apps/internal/browser/util/StatsManager;
      // 0a5: ifnonnull 0b6
      // 0a8: aload 0
      // 0a9: new net/rim/device/apps/internal/browser/util/StatsManager
      // 0ac: dup
      // 0ad: invokespecial net/rim/device/apps/internal/browser/util/StatsManager.<init> ()V
      // 0b0: putfield net/rim/device/apps/internal/browser/stack/HTTPStackAdapter._statsManager Lnet/rim/device/apps/internal/browser/util/StatsManager;
      // 0b3: goto 0c7
      // 0b6: aload 0
      // 0b7: getfield net/rim/device/apps/internal/browser/stack/HTTPStackAdapter._statsManager Lnet/rim/device/apps/internal/browser/util/StatsManager;
      // 0ba: invokevirtual net/rim/device/apps/internal/browser/util/StatsManager.getCurrentSessionsStats ()Lnet/rim/device/cldc/io/utility/SessionStats;
      // 0bd: ifnonnull 0c7
      // 0c0: aload 0
      // 0c1: getfield net/rim/device/apps/internal/browser/stack/HTTPStackAdapter._statsManager Lnet/rim/device/apps/internal/browser/util/StatsManager;
      // 0c4: invokevirtual net/rim/device/apps/internal/browser/util/StatsManager.reinit ()V
      // 0c7: aload 13
      // 0c9: monitorexit
      // 0ca: goto 0d5
      // 0cd: astore 14
      // 0cf: aload 13
      // 0d1: monitorexit
      // 0d2: aload 14
      // 0d4: athrow
      // 0d5: aload 4
      // 0d7: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.getURLWithoutFragment ()Ljava/lang/String;
      // 0da: astore 13
      // 0dc: aload 13
      // 0de: invokevirtual java/lang/String.length ()I
      // 0e1: ldc_w 32768
      // 0e4: if_icmple 0ef
      // 0e7: new java/lang/Object
      // 0ea: dup
      // 0eb: invokespecial java/io/IOException.<init> ()V
      // 0ee: athrow
      // 0ef: ldc2_w 1907089860548946979
      // 0f2: new java/lang/Object
      // 0f5: dup
      // 0f6: ldc_w "Hbco:"
      // 0f9: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 0fc: aload 13
      // 0fe: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 101: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 104: invokevirtual java/lang/String.getBytes ()[B
      // 107: bipush 5
      // 109: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 10c: pop
      // 10d: aconst_null
      // 10e: astore 14
      // 110: new java/lang/Object
      // 113: dup
      // 114: aload 13
      // 116: invokespecial net/rim/device/apps/api/utility/general/URI.<init> (Ljava/lang/String;)V
      // 119: astore 14
      // 11b: aload 14
      // 11d: invokevirtual net/rim/device/apps/api/utility/general/URI.getAbsoluteURL ()Ljava/lang/String;
      // 120: astore 13
      // 122: goto 127
      // 125: astore 15
      // 127: aconst_null
      // 128: astore 15
      // 12a: aload 2
      // 12b: invokevirtual net/rim/device/apps/internal/browser/options/BrowserConfigRecord.getUid ()Ljava/lang/String;
      // 12e: invokestatic net/rim/device/apps/internal/browser/core/BrowserSession.getActiveSession (Ljava/lang/String;)Lnet/rim/device/apps/internal/browser/core/BrowserSession;
      // 131: astore 16
      // 133: aload 16
      // 135: ifnull 144
      // 138: aload 16
      // 13a: invokevirtual net/rim/device/apps/internal/browser/core/BrowserSession.doBSMConnect ()V
      // 13d: aload 16
      // 13f: invokevirtual net/rim/device/apps/internal/browser/core/BrowserSession.getBSMHost ()Ljava/lang/String;
      // 142: astore 15
      // 144: ldc_w 120000
      // 147: istore 17
      // 149: aload 1
      // 14a: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.getFlags ()I
      // 14d: sipush 2048
      // 150: iand
      // 151: ifeq 159
      // 154: ldc_w 45000
      // 157: istore 17
      // 159: aload 3
      // 15a: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getRenderingFlags ()I
      // 15d: istore 18
      // 15f: aload 0
      // 160: iload 9
      // 162: aload 13
      // 164: aload 15
      // 166: aload 2
      // 167: iload 17
      // 169: iload 18
      // 16b: invokespecial net/rim/device/apps/internal/browser/stack/HTTPStackAdapter.openConnection (ILjava/lang/String;Ljava/lang/String;Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;II)Ljavax/microedition/io/HttpConnection;
      // 16e: astore 6
      // 170: goto 17d
      // 173: astore 19
      // 175: new java/lang/Object
      // 178: dup
      // 179: invokespecial net/rim/device/cldc/io/utility/MalformedURLException.<init> ()V
      // 17c: athrow
      // 17d: aload 6
      // 17f: ifnonnull 18a
      // 182: new java/lang/Object
      // 185: dup
      // 186: invokespecial java/io/IOException.<init> ()V
      // 189: athrow
      // 18a: aload 1
      // 18b: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.isAborted ()Z
      // 18e: ifeq 198
      // 191: aload 6
      // 193: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 198: aload 1
      // 199: aload 0
      // 19a: aload 6
      // 19c: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.registerAbortListener (Lnet/rim/device/apps/internal/browser/common/AbortListener;Ljava/lang/Object;)V
      // 19f: invokestatic net/rim/device/apps/internal/browser/options/GeneralProperty.getDefaultCharsetValue ()Ljava/lang/String;
      // 1a2: astore 19
      // 1a4: aload 3
      // 1a5: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getRequestHeaders ()Lnet/rim/device/api/io/http/HttpHeaders;
      // 1a8: astore 20
      // 1aa: aload 20
      // 1ac: ifnonnull 1b2
      // 1af: goto 2d5
      // 1b2: aload 20
      // 1b4: getstatic net/rim/device/apps/internal/browser/stack/HeaderParser.REFERER Ljava/lang/String;
      // 1b7: invokevirtual net/rim/device/api/io/http/HttpHeaders.getPropertyValue (Ljava/lang/String;)Ljava/lang/String;
      // 1ba: astore 21
      // 1bc: bipush 1
      // 1bd: istore 22
      // 1bf: aload 21
      // 1c1: ifnull 201
      // 1c4: aload 21
      // 1c6: ldc_w "https:"
      // 1c9: ldc_w 1701707776
      // 1cc: invokestatic net/rim/device/api/util/StringUtilities.startsWithIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 1cf: ifeq 1e6
      // 1d2: aload 13
      // 1d4: ldc_w "https:"
      // 1d7: ldc_w 1701707776
      // 1da: invokestatic net/rim/device/api/util/StringUtilities.startsWithIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 1dd: ifne 1e6
      // 1e0: bipush 0
      // 1e1: istore 22
      // 1e3: goto 201
      // 1e6: aload 21
      // 1e8: bipush 35
      // 1ea: invokevirtual java/lang/String.indexOf (I)I
      // 1ed: istore 23
      // 1ef: iload 23
      // 1f1: iflt 201
      // 1f4: aload 20
      // 1f6: aload 21
      // 1f8: bipush 0
      // 1f9: iload 23
      // 1fb: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 1fe: invokestatic net/rim/device/apps/internal/browser/common/RenderingUtilities.setReferrer (Lnet/rim/device/api/io/http/HttpHeaders;Ljava/lang/String;)V
      // 201: aload 20
      // 203: invokevirtual net/rim/device/api/io/http/HttpHeaders.size ()I
      // 206: istore 23
      // 208: bipush 0
      // 209: istore 24
      // 20b: iload 24
      // 20d: iload 23
      // 20f: if_icmplt 215
      // 212: goto 2d5
      // 215: aload 20
      // 217: iload 24
      // 219: invokevirtual net/rim/device/api/io/http/HttpHeaders.getPropertyKey (I)Ljava/lang/String;
      // 21c: astore 25
      // 21e: iload 22
      // 220: ifne 231
      // 223: aload 25
      // 225: getstatic net/rim/device/apps/internal/browser/stack/HeaderParser.REFERER Ljava/lang/String;
      // 228: ldc_w 1701707776
      // 22b: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 22e: ifne 288
      // 231: iload 9
      // 233: bipush 1
      // 234: if_icmpne 245
      // 237: aload 25
      // 239: getstatic net/rim/device/apps/internal/browser/stack/HeaderParser.X_WAP_PROFILE Ljava/lang/String;
      // 23c: ldc_w 1701707776
      // 23f: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 242: ifne 288
      // 245: iload 9
      // 247: bipush 1
      // 248: if_icmpeq 260
      // 24b: iload 9
      // 24d: bipush 2
      // 24f: if_icmpeq 260
      // 252: aload 25
      // 254: getstatic net/rim/device/apps/internal/browser/stack/HeaderParser.PROFILE Ljava/lang/String;
      // 257: ldc_w 1701707776
      // 25a: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 25d: ifne 288
      // 260: iload 9
      // 262: bipush 1
      // 263: if_icmpne 274
      // 266: aload 25
      // 268: getstatic net/rim/device/apps/internal/browser/stack/HeaderParser.X_WAP_PROFILE_DIFF Ljava/lang/String;
      // 26b: ldc_w 1701707776
      // 26e: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 271: ifne 288
      // 274: iload 9
      // 276: bipush 1
      // 277: if_icmpeq 298
      // 27a: aload 25
      // 27c: getstatic net/rim/device/apps/internal/browser/stack/HeaderParser.PROFILE_DIFF Ljava/lang/String;
      // 27f: ldc_w 1701707776
      // 282: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 285: ifeq 298
      // 288: aload 20
      // 28a: iload 24
      // 28c: invokevirtual net/rim/device/api/io/http/HttpHeaders.removeProperty (I)V
      // 28f: iinc 23 -1
      // 292: iinc 24 -1
      // 295: goto 2cf
      // 298: aload 25
      // 29a: ldc_w "x-rim-default-charset"
      // 29d: ldc_w 1701707776
      // 2a0: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 2a3: ifeq 2bf
      // 2a6: aload 20
      // 2a8: iload 24
      // 2aa: invokevirtual net/rim/device/api/io/http/HttpHeaders.getPropertyValue (I)Ljava/lang/String;
      // 2ad: astore 19
      // 2af: aload 20
      // 2b1: iload 24
      // 2b3: invokevirtual net/rim/device/api/io/http/HttpHeaders.removeProperty (I)V
      // 2b6: iinc 23 -1
      // 2b9: iinc 24 -1
      // 2bc: goto 2cf
      // 2bf: aload 6
      // 2c1: aload 25
      // 2c3: aload 20
      // 2c5: iload 24
      // 2c7: invokevirtual net/rim/device/api/io/http/HttpHeaders.getPropertyValue (I)Ljava/lang/String;
      // 2ca: invokeinterface javax/microedition/io/HttpConnection.setRequestProperty (Ljava/lang/String;Ljava/lang/String;)V 3
      // 2cf: iinc 24 1
      // 2d2: goto 20b
      // 2d5: aload 16
      // 2d7: ifnull 2f4
      // 2da: aload 16
      // 2dc: invokevirtual net/rim/device/apps/internal/browser/core/BrowserSession.isBSMConnected ()Z
      // 2df: ifeq 2f4
      // 2e2: aload 6
      // 2e4: ldc_w "x-rim-bsm-id"
      // 2e7: aload 16
      // 2e9: invokevirtual net/rim/device/apps/internal/browser/core/BrowserSession.getSessionId ()I
      // 2ec: invokestatic java/lang/String.valueOf (I)Ljava/lang/String;
      // 2ef: invokeinterface javax/microedition/io/HttpConnection.setRequestProperty (Ljava/lang/String;Ljava/lang/String;)V 3
      // 2f4: iload 9
      // 2f6: bipush 2
      // 2f8: if_icmpne 31e
      // 2fb: invokestatic net/rim/device/apps/internal/browser/options/GeneralProperty.getDefaultCharsetModeValue ()I
      // 2fe: bipush 1
      // 2ff: if_icmpne 312
      // 302: aload 6
      // 304: ldc_w "x-rim-force-charset"
      // 307: invokestatic net/rim/device/apps/internal/browser/options/GeneralProperty.getDefaultCharsetValue ()Ljava/lang/String;
      // 30a: invokeinterface javax/microedition/io/HttpConnection.setRequestProperty (Ljava/lang/String;Ljava/lang/String;)V 3
      // 30f: goto 31e
      // 312: aload 6
      // 314: ldc_w "x-rim-default-charset"
      // 317: aload 19
      // 319: invokeinterface javax/microedition/io/HttpConnection.setRequestProperty (Ljava/lang/String;Ljava/lang/String;)V 3
      // 31e: aload 3
      // 31f: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getPostData ()[B
      // 322: astore 21
      // 324: aload 3
      // 325: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getMethod ()Ljava/lang/String;
      // 328: astore 22
      // 32a: aload 22
      // 32c: ifnonnull 33f
      // 32f: aload 21
      // 331: ifnonnull 33a
      // 334: ldc_w "GET"
      // 337: goto 33d
      // 33a: ldc_w "POST"
      // 33d: astore 22
      // 33f: aload 6
      // 341: aload 22
      // 343: invokeinterface javax/microedition/io/HttpConnection.setRequestMethod (Ljava/lang/String;)V 2
      // 348: goto 357
      // 34b: astore 23
      // 34d: aload 6
      // 34f: ldc_w "GET"
      // 352: invokeinterface javax/microedition/io/HttpConnection.setRequestMethod (Ljava/lang/String;)V 2
      // 357: aload 6
      // 359: instanceof java/lang/Object
      // 35c: ifeq 36e
      // 35f: new net/rim/device/apps/internal/browser/stack/HTTPStackAdapter$CheckState
      // 362: dup
      // 363: aload 6
      // 365: checkcast java/lang/Object
      // 368: aload 1
      // 369: invokespecial net/rim/device/apps/internal/browser/stack/HTTPStackAdapter$CheckState.<init> (Lnet/rim/device/cldc/io/http/HttpProtocolBase;Lnet/rim/device/apps/internal/browser/stack/FetchRequest;)V
      // 36c: astore 11
      // 36e: aload 21
      // 370: ifnull 3b3
      // 373: aload 6
      // 375: getstatic net/rim/device/apps/internal/browser/stack/HTTPStackAdapter.HEADER_LENGTH Ljava/lang/String;
      // 378: aload 21
      // 37a: arraylength
      // 37b: invokestatic java/lang/String.valueOf (I)Ljava/lang/String;
      // 37e: invokeinterface javax/microedition/io/HttpConnection.setRequestProperty (Ljava/lang/String;Ljava/lang/String;)V 3
      // 383: aload 6
      // 385: getstatic net/rim/device/apps/internal/browser/stack/HTTPStackAdapter.CONTENT_TYPE Ljava/lang/String;
      // 388: invokeinterface javax/microedition/io/HttpConnection.getRequestProperty (Ljava/lang/String;)Ljava/lang/String; 2
      // 38d: ifnonnull 3a3
      // 390: iload 9
      // 392: bipush 1
      // 393: if_icmpne 3a3
      // 396: aload 6
      // 398: getstatic net/rim/device/apps/internal/browser/stack/HTTPStackAdapter.CONTENT_TYPE Ljava/lang/String;
      // 39b: ldc_w "text/plain"
      // 39e: invokeinterface javax/microedition/io/HttpConnection.setRequestProperty (Ljava/lang/String;Ljava/lang/String;)V 3
      // 3a3: aload 6
      // 3a5: invokeinterface javax/microedition/io/OutputConnection.openOutputStream ()Ljava/io/OutputStream; 1
      // 3aa: astore 8
      // 3ac: aload 8
      // 3ae: aload 21
      // 3b0: invokevirtual java/io/OutputStream.write ([B)V
      // 3b3: iload 18
      // 3b5: ldc_w 65536
      // 3b8: iand
      // 3b9: ifne 3c0
      // 3bc: bipush 1
      // 3bd: goto 3c1
      // 3c0: bipush 0
      // 3c1: istore 23
      // 3c3: ldc2_w 1907089860548946979
      // 3c6: ldc_w 1214410611
      // 3c9: bipush 5
      // 3cb: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 3ce: pop
      // 3cf: aload 4
      // 3d1: aload 6
      // 3d3: invokeinterface javax/microedition/io/HttpConnection.getResponseCode ()I 1
      // 3d8: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setStatus (I)V
      // 3db: aload 1
      // 3dc: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.recordStartTime ()V
      // 3df: invokestatic net/rim/device/apps/internal/browser/core/BrowserDaemonRegistry.getInstance ()Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 3e2: bipush 4
      // 3e4: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.changeBrowserState (I)V
      // 3e7: ldc2_w 1907089860548946979
      // 3ea: ldc_w 1214345075
      // 3ed: bipush 5
      // 3ef: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 3f2: pop
      // 3f3: iload 23
      // 3f5: ifeq 427
      // 3f8: aload 6
      // 3fa: invokeinterface javax/microedition/io/ContentConnection.getType ()Ljava/lang/String; 1
      // 3ff: invokestatic net/rim/device/api/io/MIMETypeAssociations.getMediaTypeFromMIMEType (Ljava/lang/String;)I
      // 402: lookupswitch 37 3 2 34 3 34 7 34
      // 424: bipush 0
      // 425: istore 23
      // 427: iload 23
      // 429: ifeq 43e
      // 42c: aload 6
      // 42e: invokeinterface javax/microedition/io/ContentConnection.getLength ()J 1
      // 433: ldc_w 2097152
      // 436: i2l
      // 437: lcmp
      // 438: iflt 43e
      // 43b: bipush 0
      // 43c: istore 23
      // 43e: iload 23
      // 440: ifeq 463
      // 443: aload 6
      // 445: dup
      // 446: instanceof java/lang/Object
      // 449: ifne 450
      // 44c: pop
      // 44d: goto 45a
      // 450: checkcast java/lang/Object
      // 453: astore 24
      // 455: aload 24
      // 457: invokevirtual net/rim/device/cldc/io/proxyhttp/ClientProtocol.requestPipeInputStream ()V
      // 45a: aload 6
      // 45c: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 461: astore 7
      // 463: new java/lang/Object
      // 466: dup
      // 467: invokespecial net/rim/device/api/io/http/HttpHeaders.<init> ()V
      // 46a: astore 24
      // 46c: bipush 0
      // 46d: istore 25
      // 46f: aload 6
      // 471: iload 25
      // 473: invokeinterface javax/microedition/io/HttpConnection.getHeaderFieldKey (I)Ljava/lang/String; 2
      // 478: astore 26
      // 47a: aload 26
      // 47c: ifnonnull 482
      // 47f: goto 4c6
      // 482: aload 26
      // 484: ldc_w "x-rim-bsm-session"
      // 487: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 48a: ifeq 4b0
      // 48d: aload 6
      // 48f: iload 25
      // 491: invokeinterface javax/microedition/io/HttpConnection.getHeaderField (I)Ljava/lang/String; 2
      // 496: astore 27
      // 498: aload 27
      // 49a: ldc_w "none"
      // 49d: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 4a0: ifeq 4c0
      // 4a3: aload 16
      // 4a5: ifnull 4c0
      // 4a8: aload 16
      // 4aa: invokevirtual net/rim/device/apps/internal/browser/core/BrowserSession.notifyNoActiveBSMSession ()V
      // 4ad: goto 4c0
      // 4b0: aload 24
      // 4b2: aload 26
      // 4b4: aload 6
      // 4b6: iload 25
      // 4b8: invokeinterface javax/microedition/io/HttpConnection.getHeaderField (I)Ljava/lang/String; 2
      // 4bd: invokevirtual net/rim/device/api/io/http/HttpHeaders.addProperty (Ljava/lang/String;Ljava/lang/String;)V
      // 4c0: iinc 25 1
      // 4c3: goto 46f
      // 4c6: aload 4
      // 4c8: aload 24
      // 4ca: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setResponseHeaders (Lnet/rim/device/api/io/http/HttpHeaders;)V
      // 4cd: aload 4
      // 4cf: aload 6
      // 4d1: invokeinterface javax/microedition/io/HttpConnection.getResponseMessage ()Ljava/lang/String; 1
      // 4d6: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setResponseMessage (Ljava/lang/String;)V
      // 4d9: iload 9
      // 4db: bipush 2
      // 4dd: if_icmpeq 4fd
      // 4e0: aload 6
      // 4e2: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getContentType (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 4e5: astore 25
      // 4e7: aload 25
      // 4e9: ldc_w "application/vnd.rim.jscriptc"
      // 4ec: ldc_w 1701707776
      // 4ef: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 4f2: ifeq 4fd
      // 4f5: aload 4
      // 4f7: sipush 406
      // 4fa: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setStatus (I)V
      // 4fd: bipush 0
      // 4fe: istore 25
      // 500: aload 6
      // 502: dup
      // 503: instanceof java/lang/Object
      // 506: ifne 50d
      // 509: pop
      // 50a: goto 563
      // 50d: checkcast java/lang/Object
      // 510: astore 26
      // 512: aload 1
      // 513: aload 26
      // 515: invokeinterface net/rim/device/cldc/io/waphttp/WAPRequest.getRIMSecurityInfo ()Ljavax/microedition/io/SecurityInfo; 1
      // 51a: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.setSecurityInfo (Ljavax/microedition/io/SecurityInfo;)V
      // 51d: aload 0
      // 51e: getfield net/rim/device/apps/internal/browser/stack/HTTPStackAdapter._statsManager Lnet/rim/device/apps/internal/browser/util/StatsManager;
      // 521: ifnull 532
      // 524: aload 0
      // 525: getfield net/rim/device/apps/internal/browser/stack/HTTPStackAdapter._statsManager Lnet/rim/device/apps/internal/browser/util/StatsManager;
      // 528: aload 26
      // 52a: invokeinterface net/rim/device/cldc/io/waphttp/WAPRequest.getSessionStats ()Lnet/rim/device/cldc/io/utility/SessionStats; 1
      // 52f: invokevirtual net/rim/device/apps/internal/browser/util/StatsManager.setSessionStats (Lnet/rim/device/cldc/io/utility/SessionStats;)V
      // 532: iload 23
      // 534: ifeq 55d
      // 537: aload 4
      // 539: aload 26
      // 53b: invokeinterface net/rim/device/cldc/io/waphttp/WAPRequest.getPipe ()Lnet/rim/device/internal/browser/util/Pipe; 1
      // 540: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setData (Lnet/rim/device/internal/browser/util/Pipe;)V
      // 543: new net/rim/device/apps/internal/browser/stack/CachedHttpConnection
      // 546: dup
      // 547: aload 3
      // 548: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getURL ()Ljava/lang/String;
      // 54b: aload 26
      // 54d: aload 7
      // 54f: aload 4
      // 551: aload 3
      // 552: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getRequestHeaders ()Lnet/rim/device/api/io/http/HttpHeaders;
      // 555: invokespecial net/rim/device/apps/internal/browser/stack/CachedHttpConnection.<init> (Ljava/lang/String;Ljavax/microedition/io/HttpConnection;Ljava/io/InputStream;Lnet/rim/device/apps/internal/browser/stack/CacheResult;Lnet/rim/device/api/io/http/HttpHeaders;)V
      // 558: astore 6
      // 55a: bipush 1
      // 55b: istore 25
      // 55d: bipush 1
      // 55e: istore 10
      // 560: goto 6a2
      // 563: aload 6
      // 565: dup
      // 566: instanceof java/lang/Object
      // 569: ifne 570
      // 56c: pop
      // 56d: goto 614
      // 570: checkcast java/lang/Object
      // 573: astore 26
      // 575: aload 26
      // 577: invokevirtual net/rim/device/cldc/io/proxyhttp/ClientProtocol.getInputStream ()Ljava/io/InputStream;
      // 57a: astore 27
      // 57c: aload 0
      // 57d: getfield net/rim/device/apps/internal/browser/stack/HTTPStackAdapter._syncObject Ljava/lang/Object;
      // 580: dup
      // 581: astore 28
      // 583: monitorenter
      // 584: aload 0
      // 585: invokevirtual net/rim/device/apps/internal/browser/stack/HTTPStackAdapter.getSessionStats ()Lnet/rim/device/cldc/io/utility/SessionStats;
      // 588: astore 29
      // 58a: aload 29
      // 58c: ifnull 5aa
      // 58f: aload 29
      // 591: new java/lang/Object
      // 594: dup
      // 595: ldc_w "UID = "
      // 598: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 59b: aload 26
      // 59d: invokevirtual net/rim/device/cldc/io/proxyhttp/ClientProtocol.getGroupUID ()Ljava/lang/String;
      // 5a0: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 5a3: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 5a6: bipush 0
      // 5a7: invokevirtual net/rim/device/cldc/io/utility/SessionStats.setConnectedHost (Ljava/lang/String;I)V
      // 5aa: aload 28
      // 5ac: monitorexit
      // 5ad: goto 5b8
      // 5b0: astore 30
      // 5b2: aload 28
      // 5b4: monitorexit
      // 5b5: aload 30
      // 5b7: athrow
      // 5b8: aload 1
      // 5b9: aload 0
      // 5ba: aload 26
      // 5bc: invokevirtual net/rim/device/cldc/io/proxyhttp/ClientProtocol.getGroupUID ()Ljava/lang/String;
      // 5bf: invokespecial net/rim/device/apps/internal/browser/stack/HTTPStackAdapter.getSecurityInfo (Ljava/lang/String;)Ljavax/microedition/io/SecurityInfo;
      // 5c2: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.setSecurityInfo (Ljavax/microedition/io/SecurityInfo;)V
      // 5c5: aload 27
      // 5c7: dup
      // 5c8: instanceof java/lang/Object
      // 5cb: ifne 5d2
      // 5ce: pop
      // 5cf: goto 6a2
      // 5d2: checkcast java/lang/Object
      // 5d5: astore 28
      // 5d7: aload 0
      // 5d8: getfield net/rim/device/apps/internal/browser/stack/HTTPStackAdapter._statsManager Lnet/rim/device/apps/internal/browser/util/StatsManager;
      // 5db: ifnull 5ea
      // 5de: aload 28
      // 5e0: aload 0
      // 5e1: getfield net/rim/device/apps/internal/browser/stack/HTTPStackAdapter._statsManager Lnet/rim/device/apps/internal/browser/util/StatsManager;
      // 5e4: invokevirtual net/rim/device/apps/internal/browser/util/StatsManager.getCurrentSessionsStats ()Lnet/rim/device/cldc/io/utility/SessionStats;
      // 5e7: invokevirtual net/rim/device/cldc/io/ippp/SocketPipeInputStream.setStats (Lnet/rim/device/cldc/io/utility/SessionStats;)V
      // 5ea: aload 4
      // 5ec: aload 28
      // 5ee: invokevirtual net/rim/device/cldc/io/ippp/SocketPipeInputStream.getPipe ()Lnet/rim/device/internal/browser/util/Pipe;
      // 5f1: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setData (Lnet/rim/device/internal/browser/util/Pipe;)V
      // 5f4: new net/rim/device/apps/internal/browser/stack/CachedHttpConnection
      // 5f7: dup
      // 5f8: aload 3
      // 5f9: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getURL ()Ljava/lang/String;
      // 5fc: aload 26
      // 5fe: aload 27
      // 600: aload 4
      // 602: aload 3
      // 603: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getRequestHeaders ()Lnet/rim/device/api/io/http/HttpHeaders;
      // 606: invokespecial net/rim/device/apps/internal/browser/stack/CachedHttpConnection.<init> (Ljava/lang/String;Ljavax/microedition/io/HttpConnection;Ljava/io/InputStream;Lnet/rim/device/apps/internal/browser/stack/CacheResult;Lnet/rim/device/api/io/http/HttpHeaders;)V
      // 609: astore 6
      // 60b: bipush 1
      // 60c: istore 25
      // 60e: bipush 1
      // 60f: istore 10
      // 611: goto 6a2
      // 614: aload 6
      // 616: dup
      // 617: instanceof java/lang/Object
      // 61a: ifne 621
      // 61d: pop
      // 61e: goto 687
      // 621: checkcast java/lang/Object
      // 624: astore 26
      // 626: aload 0
      // 627: getfield net/rim/device/apps/internal/browser/stack/HTTPStackAdapter._syncObject Ljava/lang/Object;
      // 62a: dup
      // 62b: astore 27
      // 62d: monitorenter
      // 62e: aload 0
      // 62f: invokevirtual net/rim/device/apps/internal/browser/stack/HTTPStackAdapter.getSessionStats ()Lnet/rim/device/cldc/io/utility/SessionStats;
      // 632: astore 28
      // 634: aload 28
      // 636: ifnull 654
      // 639: aload 28
      // 63b: new java/lang/Object
      // 63e: dup
      // 63f: ldc_w "UID = "
      // 642: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 645: aload 26
      // 647: invokevirtual net/rim/device/cldc/io/https/ProxyHttpsConnection.getGroupUID ()Ljava/lang/String;
      // 64a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 64d: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 650: bipush 0
      // 651: invokevirtual net/rim/device/cldc/io/utility/SessionStats.setConnectedHost (Ljava/lang/String;I)V
      // 654: aload 27
      // 656: monitorexit
      // 657: goto 662
      // 65a: astore 31
      // 65c: aload 27
      // 65e: monitorexit
      // 65f: aload 31
      // 661: athrow
      // 662: aload 0
      // 663: aload 26
      // 665: invokevirtual net/rim/device/cldc/io/https/ProxyHttpsConnection.getGroupUID ()Ljava/lang/String;
      // 668: invokespecial net/rim/device/apps/internal/browser/stack/HTTPStackAdapter.getSecurityInfo (Ljava/lang/String;)Ljavax/microedition/io/SecurityInfo;
      // 66b: astore 27
      // 66d: aload 26
      // 66f: invokevirtual net/rim/device/cldc/io/https/ProxyHttpsConnection.getSecurityInfo ()Ljavax/microedition/io/SecurityInfo;
      // 672: astore 28
      // 674: aload 1
      // 675: aload 28
      // 677: ifnull 67f
      // 67a: aload 28
      // 67c: goto 681
      // 67f: aload 27
      // 681: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.setSecurityInfo (Ljavax/microedition/io/SecurityInfo;)V
      // 684: goto 6a2
      // 687: aload 6
      // 689: dup
      // 68a: instanceof java/lang/Object
      // 68d: ifne 694
      // 690: pop
      // 691: goto 6a2
      // 694: checkcast java/lang/Object
      // 697: astore 26
      // 699: aload 1
      // 69a: aload 26
      // 69c: invokevirtual net/rim/device/cldc/io/devicehttps/ClientProtocol.getSecurityInfo ()Ljavax/microedition/io/SecurityInfo;
      // 69f: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.setSecurityInfo (Ljavax/microedition/io/SecurityInfo;)V
      // 6a2: iload 23
      // 6a4: ifeq 6f3
      // 6a7: iload 25
      // 6a9: ifne 6fc
      // 6ac: new java/lang/Object
      // 6af: dup
      // 6b0: aload 6
      // 6b2: aload 7
      // 6b4: aload 0
      // 6b5: getfield net/rim/device/apps/internal/browser/stack/HTTPStackAdapter._statsManager Lnet/rim/device/apps/internal/browser/util/StatsManager;
      // 6b8: ifnull 6c5
      // 6bb: aload 0
      // 6bc: getfield net/rim/device/apps/internal/browser/stack/HTTPStackAdapter._statsManager Lnet/rim/device/apps/internal/browser/util/StatsManager;
      // 6bf: invokevirtual net/rim/device/apps/internal/browser/util/StatsManager.getCurrentSessionsStats ()Lnet/rim/device/cldc/io/utility/SessionStats;
      // 6c2: goto 6c6
      // 6c5: aconst_null
      // 6c6: bipush 1
      // 6c7: invokespecial net/rim/device/apps/internal/browser/stack/AccumulatorInputStream.<init> (Ljavax/microedition/io/InputConnection;Ljava/io/InputStream;Lnet/rim/device/cldc/io/utility/SessionStats;Z)V
      // 6ca: astore 26
      // 6cc: aload 4
      // 6ce: aload 26
      // 6d0: invokevirtual net/rim/device/apps/internal/browser/stack/AccumulatorInputStream.getPipe ()Lnet/rim/device/internal/browser/util/Pipe;
      // 6d3: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setData (Lnet/rim/device/internal/browser/util/Pipe;)V
      // 6d6: new net/rim/device/apps/internal/browser/stack/CachedHttpConnection
      // 6d9: dup
      // 6da: aload 3
      // 6db: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getURL ()Ljava/lang/String;
      // 6de: aload 6
      // 6e0: aload 26
      // 6e2: aload 4
      // 6e4: aload 3
      // 6e5: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getRequestHeaders ()Lnet/rim/device/api/io/http/HttpHeaders;
      // 6e8: invokespecial net/rim/device/apps/internal/browser/stack/CachedHttpConnection.<init> (Ljava/lang/String;Ljavax/microedition/io/HttpConnection;Ljava/io/InputStream;Lnet/rim/device/apps/internal/browser/stack/CacheResult;Lnet/rim/device/api/io/http/HttpHeaders;)V
      // 6eb: astore 6
      // 6ed: bipush 1
      // 6ee: istore 10
      // 6f0: goto 6fc
      // 6f3: aload 4
      // 6f5: bipush 1
      // 6f6: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setLocalContent (Z)V
      // 6f9: bipush 1
      // 6fa: istore 10
      // 6fc: aload 4
      // 6fe: aload 1
      // 6ff: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.getSecurityInfo ()Ljavax/microedition/io/SecurityInfo;
      // 702: ifnull 709
      // 705: bipush 1
      // 706: goto 70a
      // 709: bipush 0
      // 70a: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setSecured (Z)V
      // 70d: aload 0
      // 70e: bipush 1
      // 70f: putfield net/rim/device/apps/internal/browser/stack/HTTPStackAdapter._successfulConnect Z
      // 712: aload 11
      // 714: ifnull 71c
      // 717: aload 11
      // 719: invokevirtual net/rim/device/apps/internal/browser/stack/HTTPStackAdapter$CheckState.deregister ()V
      // 71c: aload 1
      // 71d: aload 0
      // 71e: aload 6
      // 720: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.deRegisterAbortListener (Lnet/rim/device/apps/internal/browser/common/AbortListener;Ljava/lang/Object;)V
      // 723: aload 8
      // 725: ifnull 732
      // 728: aload 8
      // 72a: invokevirtual java/io/OutputStream.close ()V
      // 72d: goto 732
      // 730: astore 12
      // 732: iload 10
      // 734: ifeq 73a
      // 737: goto e02
      // 73a: aload 6
      // 73c: ifnonnull 742
      // 73f: goto e02
      // 742: aload 6
      // 744: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 749: goto e02
      // 74c: astore 12
      // 74e: goto e02
      // 751: astore 12
      // 753: aload 4
      // 755: sipush 400
      // 758: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setStatus (I)V
      // 75b: aload 12
      // 75d: invokevirtual net/rim/device/cldc/io/ssl/TLSIOException.getException ()Lnet/rim/device/cldc/io/ssl/TLSException;
      // 760: instanceof java/lang/Object
      // 763: ifeq 76a
      // 766: aload 1
      // 767: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.abort ()V
      // 76a: aload 1
      // 76b: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.isAborted ()Z
      // 76e: ifne 789
      // 771: aload 4
      // 773: sipush 506
      // 776: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 779: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setExceptionString (Ljava/lang/String;)V
      // 77c: aload 4
      // 77e: aload 12
      // 780: invokevirtual net/rim/device/cldc/io/ssl/TLSIOException.toString ()Ljava/lang/String;
      // 783: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.addExceptionDetail (Ljava/lang/String;)V
      // 786: aload 12
      // 788: athrow
      // 789: aload 11
      // 78b: ifnull 793
      // 78e: aload 11
      // 790: invokevirtual net/rim/device/apps/internal/browser/stack/HTTPStackAdapter$CheckState.deregister ()V
      // 793: aload 1
      // 794: aload 0
      // 795: aload 6
      // 797: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.deRegisterAbortListener (Lnet/rim/device/apps/internal/browser/common/AbortListener;Ljava/lang/Object;)V
      // 79a: aload 8
      // 79c: ifnull 7a9
      // 79f: aload 8
      // 7a1: invokevirtual java/io/OutputStream.close ()V
      // 7a4: goto 7a9
      // 7a7: astore 12
      // 7a9: iload 10
      // 7ab: ifeq 7b1
      // 7ae: goto e02
      // 7b1: aload 6
      // 7b3: ifnonnull 7b9
      // 7b6: goto e02
      // 7b9: aload 6
      // 7bb: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 7c0: goto e02
      // 7c3: astore 12
      // 7c5: goto e02
      // 7c8: astore 12
      // 7ca: aload 4
      // 7cc: sipush 400
      // 7cf: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setStatus (I)V
      // 7d2: aload 12
      // 7d4: invokevirtual net/rim/device/cldc/io/waphttp/WAPIOException.getError ()I
      // 7d7: istore 13
      // 7d9: iload 13
      // 7db: sipush 1000
      // 7de: if_icmpne 7e5
      // 7e1: aload 1
      // 7e2: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.abort ()V
      // 7e5: aload 1
      // 7e6: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.isAborted ()Z
      // 7e9: ifeq 7ef
      // 7ec: goto 9cc
      // 7ef: aconst_null
      // 7f0: astore 15
      // 7f2: aload 6
      // 7f4: ifnull 806
      // 7f7: aload 6
      // 7f9: checkcast java/lang/Object
      // 7fc: invokeinterface net/rim/device/cldc/io/waphttp/WAPRequest.getParameters ()Lnet/rim/device/cldc/io/waphttp/WAPConnectionParams; 1
      // 801: astore 16
      // 803: goto 81a
      // 806: new java/lang/Object
      // 809: dup
      // 80a: invokespecial net/rim/device/cldc/io/waphttp/WAPConnectionParams.<init> ()V
      // 80d: astore 16
      // 80f: aload 16
      // 811: aload 0
      // 812: aload 2
      // 813: invokespecial net/rim/device/apps/internal/browser/stack/HTTPStackAdapter.getWAPServiceBook (Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;)Ljava/lang/String;
      // 816: invokevirtual net/rim/device/cldc/io/waphttp/WAPConnectionParams.loadFrom (Ljava/lang/String;)Z
      // 819: pop
      // 81a: aload 12
      // 81c: invokevirtual net/rim/device/cldc/io/waphttp/WAPIOException.getError ()I
      // 81f: tableswitch 53 1000 1009 391 53 64 113 279 290 303 314 332 343
      // 854: sipush 214
      // 857: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 85a: astore 14
      // 85c: goto 9ae
      // 85f: new java/lang/Object
      // 862: dup
      // 863: invokespecial java/lang/StringBuffer.<init> ()V
      // 866: sipush 215
      // 869: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 86c: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 86f: aload 0
      // 870: aload 16
      // 872: invokevirtual net/rim/device/cldc/io/waphttp/WAPConnectionParams.getServer ()I
      // 875: invokespecial net/rim/device/apps/internal/browser/stack/HTTPStackAdapter.ipv4AddrToString (I)Ljava/lang/String;
      // 878: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 87b: bipush 58
      // 87d: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 880: aload 16
      // 882: invokevirtual net/rim/device/cldc/io/waphttp/WAPConnectionParams.getDestPort ()I
      // 885: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 888: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 88b: astore 14
      // 88d: goto 9ae
      // 890: bipush 1
      // 891: invokestatic net/rim/device/api/system/RadioInfo.areWAFsSupported (I)Z
      // 894: ifeq 8ef
      // 897: new java/lang/Object
      // 89a: dup
      // 89b: invokespecial java/lang/StringBuffer.<init> ()V
      // 89e: sipush 211
      // 8a1: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 8a4: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 8a7: sipush 302
      // 8aa: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 8ad: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 8b0: aload 16
      // 8b2: invokevirtual net/rim/device/cldc/io/waphttp/WAPConnectionParams.getAPN ()Ljava/lang/String;
      // 8b5: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 8b8: sipush 303
      // 8bb: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 8be: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 8c1: aload 0
      // 8c2: aload 16
      // 8c4: invokevirtual net/rim/device/cldc/io/waphttp/WAPConnectionParams.getServer ()I
      // 8c7: invokespecial net/rim/device/apps/internal/browser/stack/HTTPStackAdapter.ipv4AddrToString (I)Ljava/lang/String;
      // 8ca: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 8cd: sipush 304
      // 8d0: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 8d3: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 8d6: aload 16
      // 8d8: invokevirtual net/rim/device/cldc/io/waphttp/WAPConnectionParams.getDestPort ()I
      // 8db: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 8de: sipush 305
      // 8e1: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 8e4: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 8e7: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 8ea: astore 14
      // 8ec: goto 9ae
      // 8ef: new java/lang/Object
      // 8f2: dup
      // 8f3: invokespecial java/lang/StringBuffer.<init> ()V
      // 8f6: sipush 211
      // 8f9: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 8fc: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 8ff: sipush 303
      // 902: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 905: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 908: aload 0
      // 909: aload 16
      // 90b: invokevirtual net/rim/device/cldc/io/waphttp/WAPConnectionParams.getServer ()I
      // 90e: invokespecial net/rim/device/apps/internal/browser/stack/HTTPStackAdapter.ipv4AddrToString (I)Ljava/lang/String;
      // 911: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 914: sipush 304
      // 917: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 91a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 91d: aload 16
      // 91f: invokevirtual net/rim/device/cldc/io/waphttp/WAPConnectionParams.getDestPort ()I
      // 922: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 925: sipush 305
      // 928: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 92b: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 92e: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 931: astore 14
      // 933: goto 9ae
      // 936: sipush 441
      // 939: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 93c: astore 14
      // 93e: goto 9ae
      // 941: aload 12
      // 943: invokevirtual net/rim/device/cldc/io/waphttp/WAPIOException.getAdditionalData ()I
      // 946: invokestatic net/rim/device/apps/internal/browser/stack/HTTPStackAdapter.wapAbortError (I)Ljava/lang/String;
      // 949: astore 14
      // 94b: goto 9ae
      // 94e: sipush 212
      // 951: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 954: astore 14
      // 956: goto 9ae
      // 959: aload 12
      // 95b: invokevirtual net/rim/device/cldc/io/waphttp/WAPIOException.toString ()Ljava/lang/String;
      // 95e: aload 12
      // 960: invokevirtual net/rim/device/cldc/io/waphttp/WAPIOException.getAdditionalData ()I
      // 963: invokestatic net/rim/device/apps/internal/browser/stack/HTTPStackAdapter.getWTLSExceptionString (Ljava/lang/String;I)Ljava/lang/String;
      // 966: astore 14
      // 968: goto 9ae
      // 96b: sipush 479
      // 96e: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 971: astore 14
      // 973: goto 9ae
      // 976: bipush 1
      // 977: anewarray 1772
      // 97a: dup
      // 97b: bipush 0
      // 97c: aload 16
      // 97e: invokevirtual net/rim/device/cldc/io/waphttp/WAPConnectionParams.getAPN ()Ljava/lang/String;
      // 981: aastore
      // 982: astore 17
      // 984: bipush 1
      // 985: invokestatic net/rim/device/api/system/RadioInfo.areWAFsSupported (I)Z
      // 988: ifeq 99b
      // 98b: sipush 423
      // 98e: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 991: aload 17
      // 993: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 996: astore 14
      // 998: goto 9ae
      // 99b: sipush 503
      // 99e: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 9a1: astore 14
      // 9a3: goto 9ae
      // 9a6: sipush 582
      // 9a9: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 9ac: astore 14
      // 9ae: aload 4
      // 9b0: aload 14
      // 9b2: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setExceptionString (Ljava/lang/String;)V
      // 9b5: aload 4
      // 9b7: aload 15
      // 9b9: ifnull 9c1
      // 9bc: aload 15
      // 9be: goto 9c6
      // 9c1: aload 12
      // 9c3: invokevirtual net/rim/device/cldc/io/waphttp/WAPIOException.toString ()Ljava/lang/String;
      // 9c6: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.addExceptionDetail (Ljava/lang/String;)V
      // 9c9: aload 12
      // 9cb: athrow
      // 9cc: aload 11
      // 9ce: ifnull 9d6
      // 9d1: aload 11
      // 9d3: invokevirtual net/rim/device/apps/internal/browser/stack/HTTPStackAdapter$CheckState.deregister ()V
      // 9d6: aload 1
      // 9d7: aload 0
      // 9d8: aload 6
      // 9da: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.deRegisterAbortListener (Lnet/rim/device/apps/internal/browser/common/AbortListener;Ljava/lang/Object;)V
      // 9dd: aload 8
      // 9df: ifnull 9ec
      // 9e2: aload 8
      // 9e4: invokevirtual java/io/OutputStream.close ()V
      // 9e7: goto 9ec
      // 9ea: astore 12
      // 9ec: iload 10
      // 9ee: ifeq 9f4
      // 9f1: goto e02
      // 9f4: aload 6
      // 9f6: ifnonnull 9fc
      // 9f9: goto e02
      // 9fc: aload 6
      // 9fe: invokeinterface javax/microedition/io/Connection.close ()V 1
      // a03: goto e02
      // a06: astore 12
      // a08: goto e02
      // a0b: astore 12
      // a0d: aload 4
      // a0f: sipush 400
      // a12: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setStatus (I)V
      // a15: aload 1
      // a16: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.isAborted ()Z
      // a19: ifne a2a
      // a1c: aload 4
      // a1e: sipush 848
      // a21: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // a24: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setExceptionString (Ljava/lang/String;)V
      // a27: aload 12
      // a29: athrow
      // a2a: aload 11
      // a2c: ifnull a34
      // a2f: aload 11
      // a31: invokevirtual net/rim/device/apps/internal/browser/stack/HTTPStackAdapter$CheckState.deregister ()V
      // a34: aload 1
      // a35: aload 0
      // a36: aload 6
      // a38: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.deRegisterAbortListener (Lnet/rim/device/apps/internal/browser/common/AbortListener;Ljava/lang/Object;)V
      // a3b: aload 8
      // a3d: ifnull a4a
      // a40: aload 8
      // a42: invokevirtual java/io/OutputStream.close ()V
      // a45: goto a4a
      // a48: astore 12
      // a4a: iload 10
      // a4c: ifeq a52
      // a4f: goto e02
      // a52: aload 6
      // a54: ifnonnull a5a
      // a57: goto e02
      // a5a: aload 6
      // a5c: invokeinterface javax/microedition/io/Connection.close ()V 1
      // a61: goto e02
      // a64: astore 12
      // a66: goto e02
      // a69: astore 12
      // a6b: aload 4
      // a6d: sipush 400
      // a70: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setStatus (I)V
      // a73: aload 1
      // a74: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.isAborted ()Z
      // a77: ifne a9d
      // a7a: aload 4
      // a7c: new java/lang/Object
      // a7f: dup
      // a80: invokespecial java/lang/StringBuffer.<init> ()V
      // a83: sipush 296
      // a86: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // a89: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // a8c: aload 4
      // a8e: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.getURLWithoutFragment ()Ljava/lang/String;
      // a91: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // a94: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // a97: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setExceptionString (Ljava/lang/String;)V
      // a9a: aload 12
      // a9c: athrow
      // a9d: aload 11
      // a9f: ifnull aa7
      // aa2: aload 11
      // aa4: invokevirtual net/rim/device/apps/internal/browser/stack/HTTPStackAdapter$CheckState.deregister ()V
      // aa7: aload 1
      // aa8: aload 0
      // aa9: aload 6
      // aab: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.deRegisterAbortListener (Lnet/rim/device/apps/internal/browser/common/AbortListener;Ljava/lang/Object;)V
      // aae: aload 8
      // ab0: ifnull abd
      // ab3: aload 8
      // ab5: invokevirtual java/io/OutputStream.close ()V
      // ab8: goto abd
      // abb: astore 12
      // abd: iload 10
      // abf: ifeq ac5
      // ac2: goto e02
      // ac5: aload 6
      // ac7: ifnonnull acd
      // aca: goto e02
      // acd: aload 6
      // acf: invokeinterface javax/microedition/io/Connection.close ()V 1
      // ad4: goto e02
      // ad7: astore 12
      // ad9: goto e02
      // adc: astore 12
      // ade: aload 4
      // ae0: sipush 400
      // ae3: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setStatus (I)V
      // ae6: aload 1
      // ae7: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.isAborted ()Z
      // aea: ifne b10
      // aed: aload 4
      // aef: new java/lang/Object
      // af2: dup
      // af3: invokespecial java/lang/StringBuffer.<init> ()V
      // af6: sipush 296
      // af9: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // afc: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // aff: aload 4
      // b01: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.getURLWithoutFragment ()Ljava/lang/String;
      // b04: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // b07: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // b0a: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setExceptionString (Ljava/lang/String;)V
      // b0d: aload 12
      // b0f: athrow
      // b10: aload 11
      // b12: ifnull b1a
      // b15: aload 11
      // b17: invokevirtual net/rim/device/apps/internal/browser/stack/HTTPStackAdapter$CheckState.deregister ()V
      // b1a: aload 1
      // b1b: aload 0
      // b1c: aload 6
      // b1e: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.deRegisterAbortListener (Lnet/rim/device/apps/internal/browser/common/AbortListener;Ljava/lang/Object;)V
      // b21: aload 8
      // b23: ifnull b30
      // b26: aload 8
      // b28: invokevirtual java/io/OutputStream.close ()V
      // b2b: goto b30
      // b2e: astore 12
      // b30: iload 10
      // b32: ifeq b38
      // b35: goto e02
      // b38: aload 6
      // b3a: ifnonnull b40
      // b3d: goto e02
      // b40: aload 6
      // b42: invokeinterface javax/microedition/io/Connection.close ()V 1
      // b47: goto e02
      // b4a: astore 12
      // b4c: goto e02
      // b4f: astore 12
      // b51: aload 4
      // b53: sipush 400
      // b56: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setStatus (I)V
      // b59: aload 1
      // b5a: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.isAborted ()Z
      // b5d: ifne bcb
      // b60: aconst_null
      // b61: astore 13
      // b63: sipush 577
      // b66: istore 14
      // b68: aload 12
      // b6a: invokevirtual net/rim/device/cldc/io/ippp/SocketBaseIOException.getExceptionCode ()I
      // b6d: istore 15
      // b6f: iload 15
      // b71: lookupswitch 42 2 130 27 255 35
      // b8c: sipush 740
      // b8f: istore 14
      // b91: goto b9b
      // b94: aload 12
      // b96: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // b99: astore 13
      // b9b: aload 13
      // b9d: ifnull bbe
      // ba0: bipush 1
      // ba1: anewarray 2084
      // ba4: dup
      // ba5: bipush 0
      // ba6: aload 13
      // ba8: aastore
      // ba9: astore 16
      // bab: aload 4
      // bad: sipush 578
      // bb0: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // bb3: aload 16
      // bb5: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // bb8: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setExceptionString (Ljava/lang/String;)V
      // bbb: goto bc8
      // bbe: aload 4
      // bc0: iload 14
      // bc2: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // bc5: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setExceptionString (Ljava/lang/String;)V
      // bc8: aload 12
      // bca: athrow
      // bcb: aload 11
      // bcd: ifnull bd5
      // bd0: aload 11
      // bd2: invokevirtual net/rim/device/apps/internal/browser/stack/HTTPStackAdapter$CheckState.deregister ()V
      // bd5: aload 1
      // bd6: aload 0
      // bd7: aload 6
      // bd9: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.deRegisterAbortListener (Lnet/rim/device/apps/internal/browser/common/AbortListener;Ljava/lang/Object;)V
      // bdc: aload 8
      // bde: ifnull beb
      // be1: aload 8
      // be3: invokevirtual java/io/OutputStream.close ()V
      // be6: goto beb
      // be9: astore 12
      // beb: iload 10
      // bed: ifeq bf3
      // bf0: goto e02
      // bf3: aload 6
      // bf5: ifnonnull bfb
      // bf8: goto e02
      // bfb: aload 6
      // bfd: invokeinterface javax/microedition/io/Connection.close ()V 1
      // c02: goto e02
      // c05: astore 12
      // c07: goto e02
      // c0a: astore 12
      // c0c: invokestatic net/rim/device/api/system/EventLogger.getMinimumLevel ()I
      // c0f: bipush 5
      // c11: if_icmplt c35
      // c14: ldc2_w 1907089860548946979
      // c17: new java/lang/Object
      // c1a: dup
      // c1b: ldc_w "HTTPStackAdapter: "
      // c1e: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // c21: aload 12
      // c23: invokevirtual java/io/IOException.toString ()Ljava/lang/String;
      // c26: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // c29: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // c2c: invokevirtual java/lang/String.getBytes ()[B
      // c2f: bipush 5
      // c31: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // c34: pop
      // c35: aload 4
      // c37: sipush 400
      // c3a: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setStatus (I)V
      // c3d: aload 1
      // c3e: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.isAborted ()Z
      // c41: ifeq c47
      // c44: goto d8e
      // c47: iload 9
      // c49: bipush 1
      // c4a: if_icmpeq c50
      // c4d: goto d24
      // c50: aload 6
      // c52: ifnull c64
      // c55: aload 6
      // c57: checkcast java/lang/Object
      // c5a: invokeinterface net/rim/device/cldc/io/waphttp/WAPRequest.getParameters ()Lnet/rim/device/cldc/io/waphttp/WAPConnectionParams; 1
      // c5f: astore 13
      // c61: goto c78
      // c64: new java/lang/Object
      // c67: dup
      // c68: invokespecial net/rim/device/cldc/io/waphttp/WAPConnectionParams.<init> ()V
      // c6b: astore 13
      // c6d: aload 13
      // c6f: aload 0
      // c70: aload 2
      // c71: invokespecial net/rim/device/apps/internal/browser/stack/HTTPStackAdapter.getWAPServiceBook (Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;)Ljava/lang/String;
      // c74: invokevirtual net/rim/device/cldc/io/waphttp/WAPConnectionParams.loadFrom (Ljava/lang/String;)Z
      // c77: pop
      // c78: bipush 1
      // c79: invokestatic net/rim/device/api/system/RadioInfo.areWAFsSupported (I)Z
      // c7c: ifeq cda
      // c7f: aload 4
      // c81: new java/lang/Object
      // c84: dup
      // c85: invokespecial java/lang/StringBuffer.<init> ()V
      // c88: sipush 211
      // c8b: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // c8e: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // c91: sipush 302
      // c94: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // c97: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // c9a: aload 13
      // c9c: invokevirtual net/rim/device/cldc/io/waphttp/WAPConnectionParams.getAPN ()Ljava/lang/String;
      // c9f: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // ca2: sipush 303
      // ca5: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // ca8: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // cab: aload 0
      // cac: aload 13
      // cae: invokevirtual net/rim/device/cldc/io/waphttp/WAPConnectionParams.getServer ()I
      // cb1: invokespecial net/rim/device/apps/internal/browser/stack/HTTPStackAdapter.ipv4AddrToString (I)Ljava/lang/String;
      // cb4: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // cb7: sipush 304
      // cba: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // cbd: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // cc0: aload 13
      // cc2: invokevirtual net/rim/device/cldc/io/waphttp/WAPConnectionParams.getDestPort ()I
      // cc5: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // cc8: sipush 305
      // ccb: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // cce: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // cd1: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // cd4: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setExceptionString (Ljava/lang/String;)V
      // cd7: goto d8b
      // cda: aload 4
      // cdc: new java/lang/Object
      // cdf: dup
      // ce0: invokespecial java/lang/StringBuffer.<init> ()V
      // ce3: sipush 211
      // ce6: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // ce9: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // cec: sipush 303
      // cef: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // cf2: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // cf5: aload 0
      // cf6: aload 13
      // cf8: invokevirtual net/rim/device/cldc/io/waphttp/WAPConnectionParams.getServer ()I
      // cfb: invokespecial net/rim/device/apps/internal/browser/stack/HTTPStackAdapter.ipv4AddrToString (I)Ljava/lang/String;
      // cfe: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // d01: sipush 304
      // d04: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // d07: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // d0a: aload 13
      // d0c: invokevirtual net/rim/device/cldc/io/waphttp/WAPConnectionParams.getDestPort ()I
      // d0f: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // d12: sipush 305
      // d15: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // d18: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // d1b: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // d1e: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setExceptionString (Ljava/lang/String;)V
      // d21: goto d8b
      // d24: sipush 582
      // d27: istore 13
      // d29: aload 2
      // d2a: bipush 12
      // d2c: invokevirtual net/rim/device/apps/internal/browser/options/BrowserConfigRecord.getPropertyAsInt (I)I
      // d2f: istore 14
      // d31: aload 0
      // d32: getfield net/rim/device/apps/internal/browser/stack/HTTPStackAdapter._successfulConnect Z
      // d35: ifeq d5d
      // d38: iload 14
      // d3a: bipush 2
      // d3c: if_icmpne d47
      // d3f: sipush 536
      // d42: istore 13
      // d44: goto d77
      // d47: iload 14
      // d49: bipush 1
      // d4a: if_icmpne d55
      // d4d: sipush 581
      // d50: istore 13
      // d52: goto d77
      // d55: sipush 611
      // d58: istore 13
      // d5a: goto d77
      // d5d: iload 14
      // d5f: bipush 2
      // d61: if_icmpne d6c
      // d64: sipush 616
      // d67: istore 13
      // d69: goto d77
      // d6c: iload 14
      // d6e: bipush 1
      // d6f: if_icmpne d77
      // d72: sipush 301
      // d75: istore 13
      // d77: aload 4
      // d79: iload 13
      // d7b: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // d7e: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setExceptionString (Ljava/lang/String;)V
      // d81: aload 4
      // d83: aload 12
      // d85: invokevirtual java/io/IOException.toString ()Ljava/lang/String;
      // d88: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.addExceptionDetail (Ljava/lang/String;)V
      // d8b: aload 12
      // d8d: athrow
      // d8e: aload 11
      // d90: ifnull d98
      // d93: aload 11
      // d95: invokevirtual net/rim/device/apps/internal/browser/stack/HTTPStackAdapter$CheckState.deregister ()V
      // d98: aload 1
      // d99: aload 0
      // d9a: aload 6
      // d9c: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.deRegisterAbortListener (Lnet/rim/device/apps/internal/browser/common/AbortListener;Ljava/lang/Object;)V
      // d9f: aload 8
      // da1: ifnull dae
      // da4: aload 8
      // da6: invokevirtual java/io/OutputStream.close ()V
      // da9: goto dae
      // dac: astore 12
      // dae: iload 10
      // db0: ifne e02
      // db3: aload 6
      // db5: ifnull e02
      // db8: aload 6
      // dba: invokeinterface javax/microedition/io/Connection.close ()V 1
      // dbf: goto e02
      // dc2: astore 12
      // dc4: goto e02
      // dc7: astore 32
      // dc9: aload 11
      // dcb: ifnull dd3
      // dce: aload 11
      // dd0: invokevirtual net/rim/device/apps/internal/browser/stack/HTTPStackAdapter$CheckState.deregister ()V
      // dd3: aload 1
      // dd4: aload 0
      // dd5: aload 6
      // dd7: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.deRegisterAbortListener (Lnet/rim/device/apps/internal/browser/common/AbortListener;Ljava/lang/Object;)V
      // dda: aload 8
      // ddc: ifnull de9
      // ddf: aload 8
      // de1: invokevirtual java/io/OutputStream.close ()V
      // de4: goto de9
      // de7: astore 33
      // de9: iload 10
      // deb: ifne dff
      // dee: aload 6
      // df0: ifnull dff
      // df3: aload 6
      // df5: invokeinterface javax/microedition/io/Connection.close ()V 1
      // dfa: goto dff
      // dfd: astore 33
      // dff: aload 32
      // e01: athrow
      // e02: aload 6
      // e04: areturn
      // try (75 -> 93): 94 null
      // try (94 -> 97): 94 null
      // try (124 -> 132): 133 null
      // try (159 -> 168): 169 null
      // try (354 -> 357): 358 null
      // try (576 -> 594): 595 null
      // try (595 -> 598): 595 null
      // try (655 -> 673): 674 null
      // try (674 -> 677): 674 null
      // try (770 -> 772): 773 null
      // try (780 -> 782): 783 null
      // try (51 -> 760): 785 null
      // try (818 -> 820): 821 null
      // try (828 -> 830): 831 null
      // try (51 -> 760): 833 null
      // try (1026 -> 1028): 1029 null
      // try (1036 -> 1038): 1039 null
      // try (51 -> 760): 1041 null
      // try (1064 -> 1066): 1067 null
      // try (1074 -> 1076): 1077 null
      // try (51 -> 760): 1079 null
      // try (1110 -> 1112): 1113 null
      // try (1120 -> 1122): 1123 null
      // try (51 -> 760): 1125 null
      // try (1156 -> 1158): 1159 null
      // try (1166 -> 1168): 1169 null
      // try (51 -> 760): 1171 null
      // try (1225 -> 1227): 1228 null
      // try (1235 -> 1237): 1238 null
      // try (51 -> 760): 1240 null
      // try (1403 -> 1405): 1406 null
      // try (1411 -> 1413): 1414 null
      // try (51 -> 760): 1416 null
      // try (785 -> 808): 1416 null
      // try (833 -> 1016): 1416 null
      // try (1041 -> 1054): 1416 null
      // try (1079 -> 1100): 1416 null
      // try (1125 -> 1146): 1416 null
      // try (1171 -> 1215): 1416 null
      // try (1240 -> 1393): 1416 null
      // try (1427 -> 1429): 1430 null
      // try (1435 -> 1437): 1438 null
      // try (1416 -> 1417): 1416 null
   }

   @Override
   public final SessionStats getSessionStats() {
      return this._statsManager != null ? this._statsManager.getCurrentSessionsStats() : null;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final InputConnection fetchPage(FetchRequest fetchRequest, BrowserConfigRecord browserConfig) {
      boolean var6 = false /* VF: Semaphore variable */;

      InputConnection var3;
      try {
         var6 = true;
         fetchRequest.registerAbortListener(this, this);
         var3 = this.fetchPageInternal(fetchRequest, browserConfig);
         var6 = false;
      } finally {
         if (var6) {
            fetchRequest.deRegisterAbortListener(this, this);
         }
      }

      fetchRequest.deRegisterAbortListener(this, this);
      return var3;
   }

   @Override
   public final void abort(Object context) {
      if (context instanceof Object) {
         try {
            ((Connection)context).close();
         } finally {
            return;
         }
      }
   }

   @Override
   public final void reinitialize() {
      synchronized (this._syncObject) {
         this._successfulConnect = false;
         if (this._statsManager == null) {
            this._statsManager = new StatsManager();
         } else if (this._statsManager.getCurrentSessionsStats() == null) {
            this._statsManager.reinit();
         }
      }
   }

   private final HttpConnection openConnection(int type, String url, String specificUid, BrowserConfigRecord browserConfig, int timeout, int renderingFlags) {
      switch (type) {
         case 0:
            return null;
         case 1:
            String uid = this.getWAPServiceBook(browserConfig);
            if (uid != null) {
               url = ((StringBuffer)(new Object()))
                  .append(url)
                  .append(";retrynocontext=true;WAPGatewayIP=;ConnectionUID=")
                  .append(uid)
                  .append(";ConnectionTimeout=")
                  .append(browserConfig.getPropertyAsInt(6) * 1000)
                  .toString();
            }

            return (HttpConnection)Connector.open(url, 3, true);
         case 2:
         default:
            url = ((StringBuffer)(new Object())).append(url).append(";DeviceSide=false;ConnectionSetup=delayed").toString();
            String uid = null;
            if (StringUtilities.strEqualIgnoreCase(browserConfig.getPropertyAsString(3), BrowserConfigRecord.IPPP_SERVICE_CID, 1701707776)) {
               uid = browserConfig.getPropertyAsString(4);
            }

            if (uid != null && (uid.length() > 1 || uid.length() == 1 && uid.charAt(0) != '-')) {
               url = ((StringBuffer)(new Object())).append(url).append(";ConnectionUID=").append(uid).toString();
            }

            if (specificUid != null) {
               url = ((StringBuffer)(new Object())).append(url).append(";SpecificUID=").append(specificUid).toString();
            }

            url = ((StringBuffer)(new Object())).append(url).append(";ConnectionTimeout=").append(timeout).toString();
            switch (browserConfig.getPropertyAsInt(26)) {
               case -1:
                  break;
               case 0:
               default:
                  url = ((StringBuffer)(new Object())).append(url).append(";EndToEndRequired").toString();
                  break;
               case 1:
                  url = ((StringBuffer)(new Object())).append(url).append(";EndToEndDesired;EncryptRequired=true").toString();
                  break;
               case 2:
                  url = ((StringBuffer)(new Object())).append(url).append(";EncryptRequired=true").toString();
            }

            synchronized (this._syncObject) {
               SessionStats stats = this.getSessionStats();
               if (stats != null) {
                  stats.addToSent(200);
               }
            }

            return (HttpConnection)Connector.open(url, 3, true);
         case 3:
            url = ((StringBuffer)(new Object())).append(url).append(";retrynocontext=true;DeviceSide=true;ConnectionSetup=delayed").toString();
            String uid = null;
            if (StringUtilities.strEqualIgnoreCase(browserConfig.getPropertyAsString(3), BrowserConfigRecord.TCP_SERVICE_CID, 1701707776)) {
               uid = browserConfig.getPropertyAsString(4);
            }

            if (uid != null && (uid.length() > 1 || uid.length() == 1 && uid.charAt(0) != '-')) {
               url = ((StringBuffer)(new Object())).append(url).append(";ConnectionUID=").append(uid).toString();
            }

            synchronized (this._syncObject) {
               SessionStats stats = this.getSessionStats();
               if (stats != null) {
                  stats.addToSent(200);
               }
            }

            return (HttpConnection)Connector.open(url, 3, true);
      }
   }

   private final String getWAPServiceBook(BrowserConfigRecord browserConfig) {
      EventLogger.logEvent(1907089860548946979L, 1467183220, 5);
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord rec = null;
      String currentUID = browserConfig.getPropertyAsString(4);
      String currentCID = browserConfig.getPropertyAsString(3);
      if (!StringUtilities.strEqualIgnoreCase(currentCID, WAPServiceRecord.SERVICE_CID, 1701707776)
         || currentUID == null
         || currentUID.length() == 0
         || currentUID.length() == 1 && currentUID.charAt(0) == '-') {
         ServiceRecord[] records = sb.findRecordsByCid(WAPServiceRecord.SERVICE_CID);
         if (records.length > 0) {
            rec = records[0];
         }
      } else {
         rec = sb.getRecordByUidAndCid(currentUID, currentCID);
      }

      if (rec == null) {
         EventLogger.logEvent(1907089860548946979L, 1467182706, 5);
         return null;
      }

      WAPServiceRecord record = WAPServiceRecord.getRecord(rec);
      if (!record.getContainsWtlsClientIdValue()) {
         record.setWtlsClientIdType(browserConfig.getPropertyAsInt(15));
         record.setWtlsModeValue(browserConfig.getPropertyAsInt(13));
         record.setWtlsClientIdValue(browserConfig.getPropertyAsString(16));

         try {
            record.saveData();
         } finally {
            return rec.getUid();
         }
      }

      return rec.getUid();
   }

   private final SecurityInfo getSecurityInfo(String uid) {
      if (uid == null) {
         return null;
      }

      byte keyAlgorithm = CryptoBlock.getKeyAlgorithmForUID(uid);
      String cipherSuite = null;
      switch (keyAlgorithm) {
         case 0:
            return null;
         case 1:
         default:
            cipherSuite = "3DES";
            break;
         case 2:
            cipherSuite = "AES 256";
      }

      return (SecurityInfo)(new Object(cipherSuite, BrowserResources.getString(522), "1", null));
   }

   public static final void registerOnStartup() {
      StackManager stackManager = StackManager.getInstance();
      stackManager.registerNetworkPageFetcher(new HTTPStackAdapter(), new String[]{"http", "https"});
   }

   private final String ipv4AddrToString(int address) {
      StringBuffer buffer = (StringBuffer)(new Object());
      buffer.append(address >> 24 & 0xFF).append('.').append(address >> 16 & 0xFF).append('.').append(address >> 8 & 0xFF).append('.').append(address & 0xFF);
      return buffer.toString();
   }

   private static final String getWTLSExceptionString(String e, int errorCode) {
      switch (errorCode) {
         case -3:
            return BrowserResources.getString(574);
         case -1:
            return BrowserResources.getString(479);
         case 5:
            return BrowserResources.getString(503);
         case 40:
            return BrowserResources.getString(478);
         case 44:
         case 45:
         case 46:
            return BrowserResources.getString(477);
         case 52:
            return BrowserResources.getString(476);
         case 54:
            return BrowserResources.getString(481);
         default:
            Object[] items = new Object[]{e, new Object(errorCode & 0xFF)};
            return MessageFormat.format(BrowserResources.getString(480), items);
      }
   }

   private static final String wapAbortError(int code) {
      switch (code) {
         case 1:
            return BrowserResources.getString(439);
         case 2:
            return BrowserResources.getString(2);
         case 3:
            return BrowserResources.getString(3);
         case 4:
            return BrowserResources.getString(4);
         case 5:
            return BrowserResources.getString(5);
         case 6:
            return BrowserResources.getString(6);
         case 7:
            return BrowserResources.getString(7);
         case 8:
            return BrowserResources.getString(8);
         case 9:
            return BrowserResources.getString(9);
         case 230:
            return BrowserResources.getString(212);
         default:
            return BrowserResources.getString(214);
      }
   }

   public HTTPStackAdapter() {
      BrowserDaemonRegistry.addBrowserStateListener(this);
   }
}
