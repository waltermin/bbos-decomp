package net.rim.device.apps.internal.browser.dd;

import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.stack.FetchRequest;

final class DownloadDescriptorStatusReport extends Thread {
   private String _url;
   private int _statusCode;
   private DownloadDescriptorField _listener;
   private String _referer;
   private BrowserConfigRecord _config;
   private boolean _aborted;
   private FetchRequest _fetchRequest;
   private Object _waitingForResponseLock = new Object();
   public static final int SUCCESS = 900;
   public static final int INSUFFICIENT_MEMORY = 901;
   public static final int USER_CANCELLED = 902;
   public static final int LOSS_OF_SERVICE = 903;
   public static final int ATTRIBUTE_MISMATCH = 905;
   public static final int INVALID_DESCRIPTOR = 906;
   public static final int INVALID_DD_VERSION = 951;
   public static final int DEVICE_ABORTED = 952;
   public static final int NON_ACCEPTABLE_CONTENT = 953;
   public static final int LOADER_ERROR = 954;

   public DownloadDescriptorStatusReport(String url, int statusCode, DownloadDescriptorField listener, String referer) {
      if (url == null) {
         throw new NullPointerException();
      }

      this._url = url;
      this._statusCode = statusCode;
      this._listener = listener;
      this._referer = referer;

      try {
         BrowserSession session = BrowserSession.getCurrentSession();
         if (session != null) {
            this._config = session.getConfig();
            return;
         }
      } finally {
         return;
      }
   }

   public final void abort() {
      synchronized (this._waitingForResponseLock) {
         if (this._aborted) {
            return;
         }

         this._aborted = true;
      }

      new DownloadDescriptorStatusReport$1(this).start();
   }

   @Override
   public final void run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: sipush 400
      // 003: istore 1
      // 004: aconst_null
      // 005: astore 2
      // 006: invokestatic net/rim/device/apps/internal/browser/core/BrowserDaemonRegistry.getInstance ()Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 009: astore 3
      // 00a: aload 3
      // 00b: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getRawDataCache ()Lnet/rim/device/apps/internal/browser/stack/RawDataCache;
      // 00e: astore 4
      // 010: aload 0
      // 011: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._url Ljava/lang/String;
      // 014: astore 5
      // 016: new java/lang/StringBuffer
      // 019: dup
      // 01a: invokespecial java/lang/StringBuffer.<init> ()V
      // 01d: aload 0
      // 01e: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._statusCode I
      // 021: invokestatic java/lang/Integer.toString (I)Ljava/lang/String;
      // 024: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 027: bipush 32
      // 029: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 02c: aload 0
      // 02d: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._statusCode I
      // 030: invokestatic net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport.getStatusMessage (I)Ljava/lang/String;
      // 033: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 036: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 039: invokevirtual java/lang/String.getBytes ()[B
      // 03c: astore 6
      // 03e: bipush 0
      // 03f: istore 7
      // 041: aload 5
      // 043: ifnonnull 049
      // 046: goto 27e
      // 049: iload 7
      // 04b: bipush 10
      // 04d: if_icmplt 053
      // 050: goto 27e
      // 053: new net/rim/device/api/io/http/HttpHeaders
      // 056: dup
      // 057: invokespecial net/rim/device/api/io/http/HttpHeaders.<init> ()V
      // 05a: astore 8
      // 05c: aload 8
      // 05e: bipush 0
      // 05f: invokestatic net/rim/device/apps/internal/browser/common/RenderingUtilities.setTranscodeHeader (Lnet/rim/device/api/io/http/HttpHeaders;Z)V
      // 062: aload 3
      // 063: aload 8
      // 065: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.addStandardRequestHeaders (Lnet/rim/device/api/io/http/HttpHeaders;)V
      // 068: aload 8
      // 06a: aload 0
      // 06b: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._referer Ljava/lang/String;
      // 06e: invokestatic net/rim/device/apps/internal/browser/common/RenderingUtilities.setReferrer (Lnet/rim/device/api/io/http/HttpHeaders;Ljava/lang/String;)V
      // 071: aload 8
      // 073: aload 0
      // 074: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._config Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;
      // 077: invokestatic net/rim/device/apps/internal/browser/cod/OTAStatusReportSender.addCallingLineID (Lnet/rim/device/api/io/http/HttpHeaders;Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;)V
      // 07a: new net/rim/device/apps/internal/browser/stack/ModelResult
      // 07d: dup
      // 07e: aload 5
      // 080: bipush 3
      // 082: aload 8
      // 084: invokespecial net/rim/device/apps/internal/browser/stack/ModelResult.<init> (Ljava/lang/String;ILnet/rim/device/api/io/http/HttpHeaders;)V
      // 087: astore 9
      // 089: aconst_null
      // 08a: astore 5
      // 08c: aload 9
      // 08e: aload 6
      // 090: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.setPostData ([B)V
      // 093: aload 0
      // 094: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._waitingForResponseLock Ljava/lang/Object;
      // 097: dup
      // 098: astore 10
      // 09a: monitorenter
      // 09b: aload 0
      // 09c: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._aborted Z
      // 09f: ifeq 0e5
      // 0a2: iload 7
      // 0a4: ifne 0d1
      // 0a7: ldc2_w 1907089860548946979
      // 0aa: ldc_w 1145335395
      // 0ad: invokestatic net/rim/device/api/system/EventLogger.logEvent (JI)Z
      // 0b0: pop
      // 0b1: new net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport
      // 0b4: dup
      // 0b5: aload 0
      // 0b6: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._url Ljava/lang/String;
      // 0b9: sipush 902
      // 0bc: aconst_null
      // 0bd: aload 0
      // 0be: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._referer Ljava/lang/String;
      // 0c1: invokespecial net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport.<init> (Ljava/lang/String;ILnet/rim/device/apps/internal/browser/dd/DownloadDescriptorField;Ljava/lang/String;)V
      // 0c4: invokevirtual java/lang/Thread.start ()V
      // 0c7: sipush 902
      // 0ca: istore 1
      // 0cb: aload 10
      // 0cd: monitorexit
      // 0ce: goto 27e
      // 0d1: ldc2_w 1907089860548946979
      // 0d4: ldc_w 1145335410
      // 0d7: invokestatic net/rim/device/api/system/EventLogger.logEvent (JI)Z
      // 0da: pop
      // 0db: sipush 200
      // 0de: istore 1
      // 0df: aload 10
      // 0e1: monitorexit
      // 0e2: goto 27e
      // 0e5: aload 0
      // 0e6: new net/rim/device/apps/internal/browser/stack/FetchRequest
      // 0e9: dup
      // 0ea: aload 9
      // 0ec: aload 0
      // 0ed: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._config Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;
      // 0f0: invokespecial net/rim/device/apps/internal/browser/stack/FetchRequest.<init> (Lnet/rim/device/apps/internal/browser/stack/ModelResult;Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;)V
      // 0f3: putfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._fetchRequest Lnet/rim/device/apps/internal/browser/stack/FetchRequest;
      // 0f6: aload 10
      // 0f8: monitorexit
      // 0f9: goto 104
      // 0fc: astore 11
      // 0fe: aload 10
      // 100: monitorexit
      // 101: aload 11
      // 103: athrow
      // 104: aload 4
      // 106: aload 0
      // 107: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._fetchRequest Lnet/rim/device/apps/internal/browser/stack/FetchRequest;
      // 10a: invokevirtual net/rim/device/apps/internal/browser/stack/RawDataCache.get (Lnet/rim/device/apps/internal/browser/stack/FetchRequest;)Ljavax/microedition/io/InputConnection;
      // 10d: checkcast javax/microedition/io/HttpConnection
      // 110: astore 2
      // 111: aload 2
      // 112: ifnonnull 148
      // 115: ldc2_w 1907089860548946979
      // 118: ldc_w 1145335406
      // 11b: invokestatic net/rim/device/api/system/EventLogger.logEvent (JI)Z
      // 11e: pop
      // 11f: aload 0
      // 120: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._fetchRequest Lnet/rim/device/apps/internal/browser/stack/FetchRequest;
      // 123: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.getModelResult ()Lnet/rim/device/apps/internal/browser/stack/ModelResult;
      // 126: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getCacheResult ()Lnet/rim/device/apps/internal/browser/stack/CacheResult;
      // 129: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.getExceptionDetail ()Ljava/lang/String;
      // 12c: ifnonnull 132
      // 12f: goto 27e
      // 132: sipush 200
      // 135: istore 1
      // 136: ldc2_w 1907089860548946979
      // 139: ldc_w 1145335393
      // 13c: invokestatic net/rim/device/api/system/EventLogger.logEvent (JI)Z
      // 13f: pop
      // 140: goto 27e
      // 143: astore 10
      // 145: goto 27e
      // 148: aload 2
      // 149: invokeinterface javax/microedition/io/HttpConnection.getResponseCode ()I 1
      // 14e: istore 1
      // 14f: ldc2_w 1907089860548946979
      // 152: new java/lang/StringBuffer
      // 155: dup
      // 156: ldc_w "DD nrc "
      // 159: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 15c: iload 1
      // 15d: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 160: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 163: invokevirtual java/lang/String.getBytes ()[B
      // 166: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[B)Z
      // 169: pop
      // 16a: aload 0
      // 16b: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._waitingForResponseLock Ljava/lang/Object;
      // 16e: dup
      // 16f: astore 10
      // 171: monitorenter
      // 172: aload 0
      // 173: aconst_null
      // 174: putfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._fetchRequest Lnet/rim/device/apps/internal/browser/stack/FetchRequest;
      // 177: aload 0
      // 178: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._waitingForResponseLock Ljava/lang/Object;
      // 17b: invokevirtual java/lang/Object.notify ()V
      // 17e: aload 10
      // 180: monitorexit
      // 181: goto 18c
      // 184: astore 12
      // 186: aload 10
      // 188: monitorexit
      // 189: aload 12
      // 18b: athrow
      // 18c: iload 1
      // 18d: sipush 200
      // 190: if_icmplt 19d
      // 193: iload 1
      // 194: sipush 300
      // 197: if_icmpge 19d
      // 19a: goto 27e
      // 19d: iload 1
      // 19e: sipush 300
      // 1a1: if_icmpge 1a7
      // 1a4: goto 278
      // 1a7: iload 1
      // 1a8: sipush 400
      // 1ab: if_icmplt 1b1
      // 1ae: goto 278
      // 1b1: iload 1
      // 1b2: sipush 305
      // 1b5: if_icmpne 1bb
      // 1b8: goto 278
      // 1bb: aload 0
      // 1bc: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._waitingForResponseLock Ljava/lang/Object;
      // 1bf: dup
      // 1c0: astore 10
      // 1c2: monitorenter
      // 1c3: aload 0
      // 1c4: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._aborted Z
      // 1c7: ifeq 1de
      // 1ca: ldc2_w 1907089860548946979
      // 1cd: ldc_w 1145335410
      // 1d0: invokestatic net/rim/device/api/system/EventLogger.logEvent (JI)Z
      // 1d3: pop
      // 1d4: sipush 200
      // 1d7: istore 1
      // 1d8: aload 10
      // 1da: monitorexit
      // 1db: goto 27e
      // 1de: aload 10
      // 1e0: monitorexit
      // 1e1: goto 1ec
      // 1e4: astore 13
      // 1e6: aload 10
      // 1e8: monitorexit
      // 1e9: aload 13
      // 1eb: athrow
      // 1ec: iload 1
      // 1ed: sipush 307
      // 1f0: if_icmpeq 1f6
      // 1f3: aconst_null
      // 1f4: astore 6
      // 1f6: aload 2
      // 1f7: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getRedirectURL (Ljavax/microedition/io/HttpConnection;)Ljava/lang/String;
      // 1fa: astore 5
      // 1fc: aload 5
      // 1fe: ifnull 278
      // 201: aload 2
      // 202: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 207: astore 10
      // 209: sipush 512
      // 20c: newarray 8
      // 20e: astore 11
      // 210: aload 0
      // 211: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._aborted Z
      // 214: ifne 226
      // 217: aload 10
      // 219: aload 11
      // 21b: invokevirtual java/io/InputStream.read ([B)I
      // 21e: bipush -1
      // 220: if_icmpeq 226
      // 223: goto 210
      // 226: aload 10
      // 228: invokevirtual java/io/InputStream.close ()V
      // 22b: goto 230
      // 22e: astore 11
      // 230: aload 2
      // 231: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 236: goto 23b
      // 239: astore 11
      // 23b: aconst_null
      // 23c: astore 2
      // 23d: goto 278
      // 240: astore 11
      // 242: aload 10
      // 244: invokevirtual java/io/InputStream.close ()V
      // 247: goto 24c
      // 24a: astore 11
      // 24c: aload 2
      // 24d: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 252: goto 257
      // 255: astore 11
      // 257: aconst_null
      // 258: astore 2
      // 259: goto 278
      // 25c: astore 14
      // 25e: aload 10
      // 260: invokevirtual java/io/InputStream.close ()V
      // 263: goto 268
      // 266: astore 15
      // 268: aload 2
      // 269: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 26e: goto 273
      // 271: astore 15
      // 273: aconst_null
      // 274: astore 2
      // 275: aload 14
      // 277: athrow
      // 278: iinc 7 1
      // 27b: goto 041
      // 27e: aload 0
      // 27f: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._waitingForResponseLock Ljava/lang/Object;
      // 282: dup
      // 283: astore 18
      // 285: monitorenter
      // 286: aload 0
      // 287: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._fetchRequest Lnet/rim/device/apps/internal/browser/stack/FetchRequest;
      // 28a: ifnull 299
      // 28d: aload 0
      // 28e: aconst_null
      // 28f: putfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._fetchRequest Lnet/rim/device/apps/internal/browser/stack/FetchRequest;
      // 292: aload 0
      // 293: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._waitingForResponseLock Ljava/lang/Object;
      // 296: invokevirtual java/lang/Object.notify ()V
      // 299: aload 18
      // 29b: monitorexit
      // 29c: goto 2a7
      // 29f: astore 19
      // 2a1: aload 18
      // 2a3: monitorexit
      // 2a4: aload 19
      // 2a6: athrow
      // 2a7: aload 2
      // 2a8: ifnull 2b6
      // 2ab: aload 2
      // 2ac: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 2b1: goto 2b6
      // 2b4: astore 18
      // 2b6: aload 0
      // 2b7: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._listener Lnet/rim/device/apps/internal/browser/dd/DownloadDescriptorField;
      // 2ba: ifnonnull 2c0
      // 2bd: goto 3d5
      // 2c0: aload 0
      // 2c1: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._listener Lnet/rim/device/apps/internal/browser/dd/DownloadDescriptorField;
      // 2c4: iload 1
      // 2c5: invokevirtual net/rim/device/apps/internal/browser/dd/DownloadDescriptorField.finishedStatusReport (I)V
      // 2c8: return
      // 2c9: astore 18
      // 2cb: aload 18
      // 2cd: bipush 0
      // 2ce: invokestatic net/rim/device/apps/internal/browser/util/QuincyUtil.sendQuincy (Ljava/lang/Throwable;Z)V
      // 2d1: return
      // 2d2: astore 3
      // 2d3: aload 0
      // 2d4: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._waitingForResponseLock Ljava/lang/Object;
      // 2d7: dup
      // 2d8: astore 18
      // 2da: monitorenter
      // 2db: aload 0
      // 2dc: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._fetchRequest Lnet/rim/device/apps/internal/browser/stack/FetchRequest;
      // 2df: ifnull 2ee
      // 2e2: aload 0
      // 2e3: aconst_null
      // 2e4: putfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._fetchRequest Lnet/rim/device/apps/internal/browser/stack/FetchRequest;
      // 2e7: aload 0
      // 2e8: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._waitingForResponseLock Ljava/lang/Object;
      // 2eb: invokevirtual java/lang/Object.notify ()V
      // 2ee: aload 18
      // 2f0: monitorexit
      // 2f1: goto 2fc
      // 2f4: astore 19
      // 2f6: aload 18
      // 2f8: monitorexit
      // 2f9: aload 19
      // 2fb: athrow
      // 2fc: aload 2
      // 2fd: ifnull 30b
      // 300: aload 2
      // 301: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 306: goto 30b
      // 309: astore 18
      // 30b: aload 0
      // 30c: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._listener Lnet/rim/device/apps/internal/browser/dd/DownloadDescriptorField;
      // 30f: ifnonnull 315
      // 312: goto 3d5
      // 315: aload 0
      // 316: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._listener Lnet/rim/device/apps/internal/browser/dd/DownloadDescriptorField;
      // 319: iload 1
      // 31a: invokevirtual net/rim/device/apps/internal/browser/dd/DownloadDescriptorField.finishedStatusReport (I)V
      // 31d: return
      // 31e: astore 18
      // 320: aload 18
      // 322: bipush 0
      // 323: invokestatic net/rim/device/apps/internal/browser/util/QuincyUtil.sendQuincy (Ljava/lang/Throwable;Z)V
      // 326: return
      // 327: astore 3
      // 328: aload 3
      // 329: bipush 0
      // 32a: invokestatic net/rim/device/apps/internal/browser/util/QuincyUtil.sendQuincy (Ljava/lang/Throwable;Z)V
      // 32d: aload 0
      // 32e: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._waitingForResponseLock Ljava/lang/Object;
      // 331: dup
      // 332: astore 18
      // 334: monitorenter
      // 335: aload 0
      // 336: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._fetchRequest Lnet/rim/device/apps/internal/browser/stack/FetchRequest;
      // 339: ifnull 348
      // 33c: aload 0
      // 33d: aconst_null
      // 33e: putfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._fetchRequest Lnet/rim/device/apps/internal/browser/stack/FetchRequest;
      // 341: aload 0
      // 342: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._waitingForResponseLock Ljava/lang/Object;
      // 345: invokevirtual java/lang/Object.notify ()V
      // 348: aload 18
      // 34a: monitorexit
      // 34b: goto 356
      // 34e: astore 19
      // 350: aload 18
      // 352: monitorexit
      // 353: aload 19
      // 355: athrow
      // 356: aload 2
      // 357: ifnull 365
      // 35a: aload 2
      // 35b: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 360: goto 365
      // 363: astore 18
      // 365: aload 0
      // 366: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._listener Lnet/rim/device/apps/internal/browser/dd/DownloadDescriptorField;
      // 369: ifnull 3d5
      // 36c: aload 0
      // 36d: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._listener Lnet/rim/device/apps/internal/browser/dd/DownloadDescriptorField;
      // 370: iload 1
      // 371: invokevirtual net/rim/device/apps/internal/browser/dd/DownloadDescriptorField.finishedStatusReport (I)V
      // 374: return
      // 375: astore 18
      // 377: aload 18
      // 379: bipush 0
      // 37a: invokestatic net/rim/device/apps/internal/browser/util/QuincyUtil.sendQuincy (Ljava/lang/Throwable;Z)V
      // 37d: return
      // 37e: astore 16
      // 380: aload 0
      // 381: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._waitingForResponseLock Ljava/lang/Object;
      // 384: dup
      // 385: astore 18
      // 387: monitorenter
      // 388: aload 0
      // 389: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._fetchRequest Lnet/rim/device/apps/internal/browser/stack/FetchRequest;
      // 38c: ifnull 39b
      // 38f: aload 0
      // 390: aconst_null
      // 391: putfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._fetchRequest Lnet/rim/device/apps/internal/browser/stack/FetchRequest;
      // 394: aload 0
      // 395: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._waitingForResponseLock Ljava/lang/Object;
      // 398: invokevirtual java/lang/Object.notify ()V
      // 39b: aload 18
      // 39d: monitorexit
      // 39e: goto 3a9
      // 3a1: astore 19
      // 3a3: aload 18
      // 3a5: monitorexit
      // 3a6: aload 19
      // 3a8: athrow
      // 3a9: aload 2
      // 3aa: ifnull 3b8
      // 3ad: aload 2
      // 3ae: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 3b3: goto 3b8
      // 3b6: astore 18
      // 3b8: aload 0
      // 3b9: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._listener Lnet/rim/device/apps/internal/browser/dd/DownloadDescriptorField;
      // 3bc: ifnull 3d2
      // 3bf: aload 0
      // 3c0: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorStatusReport._listener Lnet/rim/device/apps/internal/browser/dd/DownloadDescriptorField;
      // 3c3: iload 1
      // 3c4: invokevirtual net/rim/device/apps/internal/browser/dd/DownloadDescriptorField.finishedStatusReport (I)V
      // 3c7: goto 3d2
      // 3ca: astore 18
      // 3cc: aload 18
      // 3ce: bipush 0
      // 3cf: invokestatic net/rim/device/apps/internal/browser/util/QuincyUtil.sendQuincy (Ljava/lang/Throwable;Z)V
      // 3d2: aload 16
      // 3d4: athrow
      // 3d5: return
      // try (72 -> 95): 116 null
      // try (96 -> 104): 116 null
      // try (105 -> 115): 116 null
      // try (116 -> 119): 116 null
      // try (133 -> 146): 147 null
      // try (168 -> 176): 177 null
      // try (177 -> 180): 177 null
      // try (206 -> 217): 221 null
      // try (218 -> 220): 221 null
      // try (221 -> 224): 221 null
      // try (251 -> 253): 254 null
      // try (255 -> 257): 258 null
      // try (239 -> 251): 262 null
      // try (263 -> 265): 266 null
      // try (267 -> 269): 270 null
      // try (239 -> 251): 274 null
      // try (262 -> 263): 274 null
      // try (275 -> 277): 278 null
      // try (279 -> 281): 282 null
      // try (274 -> 275): 274 null
      // try (4 -> 289): 331 null
      // try (4 -> 289): 374 null
      // try (4 -> 289): 419 null
      // try (331 -> 332): 419 null
      // try (374 -> 378): 419 null
      // try (419 -> 420): 419 null
      // try (425 -> 436): 437 null
      // try (383 -> 394): 395 null
      // try (337 -> 348): 349 null
      // try (294 -> 305): 306 null
      // try (437 -> 440): 437 null
      // try (395 -> 398): 395 null
      // try (349 -> 352): 349 null
      // try (306 -> 309): 306 null
      // try (444 -> 446): 447 null
      // try (402 -> 404): 405 null
      // try (356 -> 358): 359 null
      // try (313 -> 315): 316 null
      // try (420 -> 455): 456 null
      // try (378 -> 413): 414 null
      // try (332 -> 368): 369 null
      // try (289 -> 325): 326 null
   }

   private static final String getStatusMessage(int statusCode) {
      switch (statusCode) {
         case 900:
            return "Success";
         case 901:
            return "Insufficient memory";
         case 902:
            return "User Cancelled";
         case 903:
            return "Loss of Service";
         case 905:
            return "Attribute mismatch";
         case 906:
            return "Invalid descriptor";
         case 951:
            return "Invalid DDVersion";
         case 952:
            return "Device Aborted";
         case 953:
            return "Non-Acceptable Content";
         case 954:
            return "Loader Error";
         default:
            return "Unknown Error";
      }
   }
}
