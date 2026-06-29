package net.rim.device.apps.internal.browser.dd;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.io.file.FileIOException;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.download.DownloadManager;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.util.RendererControl;

final class OmaDownloadManager extends DownloadManager {
   private int _otaStatus = 900;
   private String _otaError;
   private String _objectURI;
   private String _expectedContentType;
   private String _filename;
   private String _referer;
   private String _contentType;
   private DownloadDescriptorField _listener;
   private boolean _allowOverwrite;
   private DownloadDescriptorBrowserContent _ddBrowserContent;
   private long _maxDownloadSize;
   private int _numTypes;

   public OmaDownloadManager(
      String objectURI,
      String expectedContentType,
      int expectedSize,
      String filename,
      DownloadDescriptorField ddField,
      String referer,
      boolean allowOverwrite,
      DownloadDescriptorBrowserContent ddBrowserContent
   ) {
      super(null);
      this._objectURI = objectURI;
      this._expectedContentType = expectedContentType;
      this._filename = filename;
      this._listener = ddField;
      this._referer = referer;
      this._allowOverwrite = allowOverwrite;
      this._ddBrowserContent = ddBrowserContent;
      this._maxDownloadSize = (long)expectedSize + (expectedSize >> 1);
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
      // 000: aconst_null
      // 001: astore 1
      // 002: aconst_null
      // 003: astore 2
      // 004: aconst_null
      // 005: astore 3
      // 006: aconst_null
      // 007: astore 4
      // 009: aload 0
      // 00a: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._ddBrowserContent Lnet/rim/device/apps/internal/browser/dd/DownloadDescriptorBrowserContent;
      // 00d: aload 0
      // 00e: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._objectURI Ljava/lang/String;
      // 011: invokevirtual net/rim/device/apps/internal/browser/dd/DownloadDescriptorBrowserContent.getCodelivery (Ljava/lang/String;)Lnet/rim/device/apps/internal/browser/dd/CodeliveredMediaObject;
      // 014: astore 5
      // 016: aload 5
      // 018: ifnull 072
      // 01b: aload 0
      // 01c: aload 5
      // 01e: invokevirtual net/rim/device/apps/internal/browser/dd/CodeliveredMediaObject.getContentType ()Ljava/lang/String;
      // 021: putfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._contentType Ljava/lang/String;
      // 024: aload 0
      // 025: aload 0
      // 026: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._contentType Ljava/lang/String;
      // 029: invokevirtual net/rim/device/apps/internal/browser/dd/OmaDownloadManager.checkContentType (Ljava/lang/String;)V
      // 02c: aload 5
      // 02e: invokevirtual net/rim/device/apps/internal/browser/dd/CodeliveredMediaObject.getData ()[B
      // 031: astore 4
      // 033: new java/lang/Object
      // 036: dup
      // 037: aload 4
      // 039: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 03c: astore 6
      // 03e: aload 0
      // 03f: aload 6
      // 041: aload 0
      // 042: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._contentType Ljava/lang/String;
      // 045: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.unwrapDRM (Ljava/io/InputStream;Ljava/lang/String;)Ljava/io/InputStream;
      // 048: astore 7
      // 04a: aload 7
      // 04c: aload 6
      // 04e: if_acmpeq 0ce
      // 051: aload 0
      // 052: aload 7
      // 054: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.readStream (Ljava/io/InputStream;)[B
      // 057: astore 4
      // 059: goto 0ce
      // 05c: astore 6
      // 05e: aload 0
      // 05f: aload 6
      // 061: invokevirtual net/rim/device/apps/internal/browser/dd/OmaDownloadManager.setError (Ljava/lang/Throwable;)V
      // 064: goto 0ce
      // 067: astore 6
      // 069: aload 0
      // 06a: aload 6
      // 06c: invokevirtual net/rim/device/apps/internal/browser/dd/OmaDownloadManager.setError (Ljava/lang/Throwable;)V
      // 06f: goto 0ce
      // 072: new java/lang/Object
      // 075: dup
      // 076: invokespecial net/rim/device/api/io/http/HttpHeaders.<init> ()V
      // 079: astore 6
      // 07b: aload 6
      // 07d: ldc_w "Accept"
      // 080: new java/lang/Object
      // 083: dup
      // 084: invokespecial java/lang/StringBuffer.<init> ()V
      // 087: aload 0
      // 088: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._expectedContentType Ljava/lang/String;
      // 08b: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 08e: ldc_w ", "
      // 091: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 094: ldc_w "application/vnd.oma.drm.message"
      // 097: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 09a: ldc_w ";q=0.9"
      // 09d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0a0: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0a3: invokevirtual net/rim/device/api/io/http/HttpHeaders.setProperty (Ljava/lang/String;Ljava/lang/String;)V
      // 0a6: aload 6
      // 0a8: bipush 0
      // 0a9: invokestatic net/rim/device/apps/internal/browser/common/RenderingUtilities.setTranscodeHeader (Lnet/rim/device/api/io/http/HttpHeaders;Z)V
      // 0ac: invokestatic net/rim/device/apps/internal/browser/core/BrowserDaemonRegistry.getInstance ()Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 0af: astore 7
      // 0b1: aload 7
      // 0b3: aload 6
      // 0b5: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.addStandardRequestHeaders (Lnet/rim/device/api/io/http/HttpHeaders;)V
      // 0b8: aload 6
      // 0ba: aload 0
      // 0bb: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._referer Ljava/lang/String;
      // 0be: invokestatic net/rim/device/apps/internal/browser/common/RenderingUtilities.setReferrer (Lnet/rim/device/api/io/http/HttpHeaders;Ljava/lang/String;)V
      // 0c1: aload 0
      // 0c2: aload 0
      // 0c3: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._objectURI Ljava/lang/String;
      // 0c6: aload 6
      // 0c8: aconst_null
      // 0c9: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.requestResource (Ljava/lang/String;Lnet/rim/device/api/io/http/HttpHeaders;Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;)[B
      // 0cc: astore 4
      // 0ce: aload 0
      // 0cf: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaStatus I
      // 0d2: sipush 900
      // 0d5: if_icmpeq 0db
      // 0d8: goto 31e
      // 0db: aload 0
      // 0dc: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.isAborted ()Z
      // 0df: ifeq 0e5
      // 0e2: goto 31e
      // 0e5: aload 4
      // 0e7: ifnonnull 0f2
      // 0ea: new java/lang/Object
      // 0ed: dup
      // 0ee: invokespecial java/lang/NullPointerException.<init> ()V
      // 0f1: athrow
      // 0f2: aload 0
      // 0f3: aload 4
      // 0f5: arraylength
      // 0f6: i2l
      // 0f7: invokevirtual net/rim/device/apps/internal/browser/dd/OmaDownloadManager.checkDownloadLength (J)V
      // 0fa: aload 0
      // 0fb: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._contentType Ljava/lang/String;
      // 0fe: aload 4
      // 100: invokestatic net/rim/device/apps/internal/browser/dd/OmaDownloadManager.canRenderMediaObject (Ljava/lang/String;[B)Z
      // 103: ifeq 109
      // 106: goto 1a7
      // 109: aload 0
      // 10a: sipush 953
      // 10d: putfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaStatus I
      // 110: aload 0
      // 111: sipush 784
      // 114: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 117: putfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaError Ljava/lang/String;
      // 11a: aload 3
      // 11b: ifnull 135
      // 11e: aload 3
      // 11f: invokevirtual java/io/OutputStream.close ()V
      // 122: goto 133
      // 125: astore 10
      // 127: aload 10
      // 129: bipush 0
      // 12a: invokestatic net/rim/device/apps/internal/browser/util/QuincyUtil.sendQuincy (Ljava/lang/Throwable;Z)V
      // 12d: aload 0
      // 12e: aload 10
      // 130: invokevirtual net/rim/device/apps/internal/browser/dd/OmaDownloadManager.setError (Ljava/lang/Throwable;)V
      // 133: aconst_null
      // 134: astore 3
      // 135: aload 0
      // 136: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.isAborted ()Z
      // 139: ifeq 148
      // 13c: aload 0
      // 13d: sipush 902
      // 140: putfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaStatus I
      // 143: aload 0
      // 144: aconst_null
      // 145: putfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaError Ljava/lang/String;
      // 148: aload 1
      // 149: ifnull 17b
      // 14c: aload 0
      // 14d: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaStatus I
      // 150: sipush 900
      // 153: if_icmpeq 17b
      // 156: aload 1
      // 157: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 15c: aconst_null
      // 15d: astore 1
      // 15e: aconst_null
      // 15f: astore 2
      // 160: goto 17b
      // 163: astore 10
      // 165: aload 10
      // 167: bipush 0
      // 168: invokestatic net/rim/device/apps/internal/browser/util/QuincyUtil.sendQuincy (Ljava/lang/Throwable;Z)V
      // 16b: aconst_null
      // 16c: astore 1
      // 16d: aconst_null
      // 16e: astore 2
      // 16f: goto 17b
      // 172: astore 11
      // 174: aconst_null
      // 175: astore 1
      // 176: aconst_null
      // 177: astore 2
      // 178: aload 11
      // 17a: athrow
      // 17b: aload 0
      // 17c: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._listener Lnet/rim/device/apps/internal/browser/dd/DownloadDescriptorField;
      // 17f: aload 0
      // 180: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaStatus I
      // 183: aload 0
      // 184: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaError Ljava/lang/String;
      // 187: aload 1
      // 188: ifnull 194
      // 18b: aload 1
      // 18c: invokeinterface javax/microedition/io/file/FileConnection.getURL ()Ljava/lang/String; 1
      // 191: goto 195
      // 194: aconst_null
      // 195: aload 2
      // 196: ifnull 1a2
      // 199: aload 2
      // 19a: invokeinterface javax/microedition/io/file/FileConnection.getURL ()Ljava/lang/String; 1
      // 19f: goto 1a3
      // 1a2: aconst_null
      // 1a3: invokevirtual net/rim/device/apps/internal/browser/dd/DownloadDescriptorField.finishedDownload (ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
      // 1a6: return
      // 1a7: aload 0
      // 1a8: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._filename Ljava/lang/String;
      // 1ab: ldc_w "file:///store/"
      // 1ae: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 1b1: ifne 1b7
      // 1b4: goto 261
      // 1b7: aload 4
      // 1b9: arraylength
      // 1ba: invokestatic net/rim/device/apps/internal/browser/download/DownloadManager.ensureAvailableFlash (I)Z
      // 1bd: ifeq 1c3
      // 1c0: goto 261
      // 1c3: aload 0
      // 1c4: sipush 901
      // 1c7: putfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaStatus I
      // 1ca: aload 0
      // 1cb: sipush 765
      // 1ce: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 1d1: putfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaError Ljava/lang/String;
      // 1d4: aload 3
      // 1d5: ifnull 1ef
      // 1d8: aload 3
      // 1d9: invokevirtual java/io/OutputStream.close ()V
      // 1dc: goto 1ed
      // 1df: astore 10
      // 1e1: aload 10
      // 1e3: bipush 0
      // 1e4: invokestatic net/rim/device/apps/internal/browser/util/QuincyUtil.sendQuincy (Ljava/lang/Throwable;Z)V
      // 1e7: aload 0
      // 1e8: aload 10
      // 1ea: invokevirtual net/rim/device/apps/internal/browser/dd/OmaDownloadManager.setError (Ljava/lang/Throwable;)V
      // 1ed: aconst_null
      // 1ee: astore 3
      // 1ef: aload 0
      // 1f0: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.isAborted ()Z
      // 1f3: ifeq 202
      // 1f6: aload 0
      // 1f7: sipush 902
      // 1fa: putfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaStatus I
      // 1fd: aload 0
      // 1fe: aconst_null
      // 1ff: putfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaError Ljava/lang/String;
      // 202: aload 1
      // 203: ifnull 235
      // 206: aload 0
      // 207: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaStatus I
      // 20a: sipush 900
      // 20d: if_icmpeq 235
      // 210: aload 1
      // 211: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 216: aconst_null
      // 217: astore 1
      // 218: aconst_null
      // 219: astore 2
      // 21a: goto 235
      // 21d: astore 10
      // 21f: aload 10
      // 221: bipush 0
      // 222: invokestatic net/rim/device/apps/internal/browser/util/QuincyUtil.sendQuincy (Ljava/lang/Throwable;Z)V
      // 225: aconst_null
      // 226: astore 1
      // 227: aconst_null
      // 228: astore 2
      // 229: goto 235
      // 22c: astore 11
      // 22e: aconst_null
      // 22f: astore 1
      // 230: aconst_null
      // 231: astore 2
      // 232: aload 11
      // 234: athrow
      // 235: aload 0
      // 236: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._listener Lnet/rim/device/apps/internal/browser/dd/DownloadDescriptorField;
      // 239: aload 0
      // 23a: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaStatus I
      // 23d: aload 0
      // 23e: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaError Ljava/lang/String;
      // 241: aload 1
      // 242: ifnull 24e
      // 245: aload 1
      // 246: invokeinterface javax/microedition/io/file/FileConnection.getURL ()Ljava/lang/String; 1
      // 24b: goto 24f
      // 24e: aconst_null
      // 24f: aload 2
      // 250: ifnull 25c
      // 253: aload 2
      // 254: invokeinterface javax/microedition/io/file/FileConnection.getURL ()Ljava/lang/String; 1
      // 259: goto 25d
      // 25c: aconst_null
      // 25d: invokevirtual net/rim/device/apps/internal/browser/dd/DownloadDescriptorField.finishedDownload (ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
      // 260: return
      // 261: aload 0
      // 262: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._filename Ljava/lang/String;
      // 265: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 268: checkcast java/lang/Object
      // 26b: astore 1
      // 26c: aload 1
      // 26d: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 272: ifeq 2df
      // 275: aload 0
      // 276: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._allowOverwrite Z
      // 279: ifeq 285
      // 27c: aload 1
      // 27d: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 282: goto 2df
      // 285: aload 1
      // 286: astore 2
      // 287: aconst_null
      // 288: astore 1
      // 289: aconst_null
      // 28a: astore 6
      // 28c: bipush 1
      // 28d: istore 7
      // 28f: iload 7
      // 291: sipush 1024
      // 294: if_icmple 29f
      // 297: new java/lang/Object
      // 29a: dup
      // 29b: invokespecial java/io/IOException.<init> ()V
      // 29e: athrow
      // 29f: new java/lang/Object
      // 2a2: dup
      // 2a3: invokespecial java/lang/StringBuffer.<init> ()V
      // 2a6: aload 0
      // 2a7: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._filename Ljava/lang/String;
      // 2aa: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2ad: bipush 46
      // 2af: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 2b2: iload 7
      // 2b4: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 2b7: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 2ba: astore 6
      // 2bc: aload 6
      // 2be: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 2c1: checkcast java/lang/Object
      // 2c4: astore 1
      // 2c5: aload 1
      // 2c6: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 2cb: ifne 2d1
      // 2ce: goto 2df
      // 2d1: aload 1
      // 2d2: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 2d7: aconst_null
      // 2d8: astore 1
      // 2d9: iinc 7 1
      // 2dc: goto 28f
      // 2df: aload 0
      // 2e0: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.isDrmProtected ()Z
      // 2e3: ifeq 30b
      // 2e6: aload 1
      // 2e7: dup
      // 2e8: instanceof java/lang/Object
      // 2eb: ifne 2f2
      // 2ee: pop
      // 2ef: goto 30b
      // 2f2: checkcast java/lang/Object
      // 2f5: astore 6
      // 2f7: aload 6
      // 2f9: bipush 51
      // 2fb: invokestatic net/rim/device/api/system/CodeSigningKey.getBuiltInKey (I)Lnet/rim/device/api/system/CodeSigningKey;
      // 2fe: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.setControlledAccess (Lnet/rim/device/api/system/CodeSigningKey;)Z 2
      // 303: pop
      // 304: aload 6
      // 306: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.enableDRMForwardLock ()V 1
      // 30b: aload 1
      // 30c: invokeinterface javax/microedition/io/file/FileConnection.create ()V 1
      // 311: aload 1
      // 312: invokeinterface javax/microedition/io/file/FileConnection.openOutputStream ()Ljava/io/OutputStream; 1
      // 317: astore 3
      // 318: aload 3
      // 319: aload 4
      // 31b: invokevirtual java/io/OutputStream.write ([B)V
      // 31e: aload 3
      // 31f: ifnull 339
      // 322: aload 3
      // 323: invokevirtual java/io/OutputStream.close ()V
      // 326: goto 337
      // 329: astore 10
      // 32b: aload 10
      // 32d: bipush 0
      // 32e: invokestatic net/rim/device/apps/internal/browser/util/QuincyUtil.sendQuincy (Ljava/lang/Throwable;Z)V
      // 331: aload 0
      // 332: aload 10
      // 334: invokevirtual net/rim/device/apps/internal/browser/dd/OmaDownloadManager.setError (Ljava/lang/Throwable;)V
      // 337: aconst_null
      // 338: astore 3
      // 339: aload 0
      // 33a: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.isAborted ()Z
      // 33d: ifeq 34c
      // 340: aload 0
      // 341: sipush 902
      // 344: putfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaStatus I
      // 347: aload 0
      // 348: aconst_null
      // 349: putfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaError Ljava/lang/String;
      // 34c: aload 1
      // 34d: ifnull 37f
      // 350: aload 0
      // 351: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaStatus I
      // 354: sipush 900
      // 357: if_icmpeq 37f
      // 35a: aload 1
      // 35b: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 360: aconst_null
      // 361: astore 1
      // 362: aconst_null
      // 363: astore 2
      // 364: goto 37f
      // 367: astore 10
      // 369: aload 10
      // 36b: bipush 0
      // 36c: invokestatic net/rim/device/apps/internal/browser/util/QuincyUtil.sendQuincy (Ljava/lang/Throwable;Z)V
      // 36f: aconst_null
      // 370: astore 1
      // 371: aconst_null
      // 372: astore 2
      // 373: goto 37f
      // 376: astore 11
      // 378: aconst_null
      // 379: astore 1
      // 37a: aconst_null
      // 37b: astore 2
      // 37c: aload 11
      // 37e: athrow
      // 37f: aload 0
      // 380: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._listener Lnet/rim/device/apps/internal/browser/dd/DownloadDescriptorField;
      // 383: aload 0
      // 384: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaStatus I
      // 387: aload 0
      // 388: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaError Ljava/lang/String;
      // 38b: aload 1
      // 38c: ifnull 398
      // 38f: aload 1
      // 390: invokeinterface javax/microedition/io/file/FileConnection.getURL ()Ljava/lang/String; 1
      // 395: goto 399
      // 398: aconst_null
      // 399: aload 2
      // 39a: ifnull 3a6
      // 39d: aload 2
      // 39e: invokeinterface javax/microedition/io/file/FileConnection.getURL ()Ljava/lang/String; 1
      // 3a3: goto 3a7
      // 3a6: aconst_null
      // 3a7: invokevirtual net/rim/device/apps/internal/browser/dd/DownloadDescriptorField.finishedDownload (ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
      // 3aa: return
      // 3ab: astore 4
      // 3ad: aload 4
      // 3af: bipush 0
      // 3b0: invokestatic net/rim/device/apps/internal/browser/util/QuincyUtil.sendQuincy (Ljava/lang/Throwable;Z)V
      // 3b3: aload 0
      // 3b4: aload 4
      // 3b6: invokevirtual net/rim/device/apps/internal/browser/dd/OmaDownloadManager.setError (Ljava/lang/Throwable;)V
      // 3b9: aload 3
      // 3ba: ifnull 3d4
      // 3bd: aload 3
      // 3be: invokevirtual java/io/OutputStream.close ()V
      // 3c1: goto 3d2
      // 3c4: astore 10
      // 3c6: aload 10
      // 3c8: bipush 0
      // 3c9: invokestatic net/rim/device/apps/internal/browser/util/QuincyUtil.sendQuincy (Ljava/lang/Throwable;Z)V
      // 3cc: aload 0
      // 3cd: aload 10
      // 3cf: invokevirtual net/rim/device/apps/internal/browser/dd/OmaDownloadManager.setError (Ljava/lang/Throwable;)V
      // 3d2: aconst_null
      // 3d3: astore 3
      // 3d4: aload 0
      // 3d5: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.isAborted ()Z
      // 3d8: ifeq 3e7
      // 3db: aload 0
      // 3dc: sipush 902
      // 3df: putfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaStatus I
      // 3e2: aload 0
      // 3e3: aconst_null
      // 3e4: putfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaError Ljava/lang/String;
      // 3e7: aload 1
      // 3e8: ifnull 41a
      // 3eb: aload 0
      // 3ec: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaStatus I
      // 3ef: sipush 900
      // 3f2: if_icmpeq 41a
      // 3f5: aload 1
      // 3f6: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 3fb: aconst_null
      // 3fc: astore 1
      // 3fd: aconst_null
      // 3fe: astore 2
      // 3ff: goto 41a
      // 402: astore 10
      // 404: aload 10
      // 406: bipush 0
      // 407: invokestatic net/rim/device/apps/internal/browser/util/QuincyUtil.sendQuincy (Ljava/lang/Throwable;Z)V
      // 40a: aconst_null
      // 40b: astore 1
      // 40c: aconst_null
      // 40d: astore 2
      // 40e: goto 41a
      // 411: astore 11
      // 413: aconst_null
      // 414: astore 1
      // 415: aconst_null
      // 416: astore 2
      // 417: aload 11
      // 419: athrow
      // 41a: aload 0
      // 41b: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._listener Lnet/rim/device/apps/internal/browser/dd/DownloadDescriptorField;
      // 41e: aload 0
      // 41f: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaStatus I
      // 422: aload 0
      // 423: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaError Ljava/lang/String;
      // 426: aload 1
      // 427: ifnull 433
      // 42a: aload 1
      // 42b: invokeinterface javax/microedition/io/file/FileConnection.getURL ()Ljava/lang/String; 1
      // 430: goto 434
      // 433: aconst_null
      // 434: aload 2
      // 435: ifnull 441
      // 438: aload 2
      // 439: invokeinterface javax/microedition/io/file/FileConnection.getURL ()Ljava/lang/String; 1
      // 43e: goto 442
      // 441: aconst_null
      // 442: invokevirtual net/rim/device/apps/internal/browser/dd/DownloadDescriptorField.finishedDownload (ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
      // 445: return
      // 446: astore 8
      // 448: aload 3
      // 449: ifnull 463
      // 44c: aload 3
      // 44d: invokevirtual java/io/OutputStream.close ()V
      // 450: goto 461
      // 453: astore 10
      // 455: aload 10
      // 457: bipush 0
      // 458: invokestatic net/rim/device/apps/internal/browser/util/QuincyUtil.sendQuincy (Ljava/lang/Throwable;Z)V
      // 45b: aload 0
      // 45c: aload 10
      // 45e: invokevirtual net/rim/device/apps/internal/browser/dd/OmaDownloadManager.setError (Ljava/lang/Throwable;)V
      // 461: aconst_null
      // 462: astore 3
      // 463: aload 0
      // 464: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.isAborted ()Z
      // 467: ifeq 476
      // 46a: aload 0
      // 46b: sipush 902
      // 46e: putfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaStatus I
      // 471: aload 0
      // 472: aconst_null
      // 473: putfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaError Ljava/lang/String;
      // 476: aload 1
      // 477: ifnull 4a9
      // 47a: aload 0
      // 47b: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaStatus I
      // 47e: sipush 900
      // 481: if_icmpeq 4a9
      // 484: aload 1
      // 485: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 48a: aconst_null
      // 48b: astore 1
      // 48c: aconst_null
      // 48d: astore 2
      // 48e: goto 4a9
      // 491: astore 10
      // 493: aload 10
      // 495: bipush 0
      // 496: invokestatic net/rim/device/apps/internal/browser/util/QuincyUtil.sendQuincy (Ljava/lang/Throwable;Z)V
      // 499: aconst_null
      // 49a: astore 1
      // 49b: aconst_null
      // 49c: astore 2
      // 49d: goto 4a9
      // 4a0: astore 11
      // 4a2: aconst_null
      // 4a3: astore 1
      // 4a4: aconst_null
      // 4a5: astore 2
      // 4a6: aload 11
      // 4a8: athrow
      // 4a9: aload 0
      // 4aa: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._listener Lnet/rim/device/apps/internal/browser/dd/DownloadDescriptorField;
      // 4ad: aload 0
      // 4ae: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaStatus I
      // 4b1: aload 0
      // 4b2: getfield net/rim/device/apps/internal/browser/dd/OmaDownloadManager._otaError Ljava/lang/String;
      // 4b5: aload 1
      // 4b6: ifnull 4c2
      // 4b9: aload 1
      // 4ba: invokeinterface javax/microedition/io/file/FileConnection.getURL ()Ljava/lang/String; 1
      // 4bf: goto 4c3
      // 4c2: aconst_null
      // 4c3: aload 2
      // 4c4: ifnull 4d0
      // 4c7: aload 2
      // 4c8: invokeinterface javax/microedition/io/file/FileConnection.getURL ()Ljava/lang/String; 1
      // 4cd: goto 4d1
      // 4d0: aconst_null
      // 4d1: invokevirtual net/rim/device/apps/internal/browser/dd/DownloadDescriptorField.finishedDownload (ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
      // 4d4: aload 8
      // 4d6: athrow
      // try (16 -> 45): 46 null
      // try (16 -> 45): 51 null
      // try (6 -> 128): 443 null
      // try (200 -> 218): 443 null
      // try (290 -> 371): 443 null
      // try (6 -> 128): 522 null
      // try (200 -> 218): 522 null
      // try (290 -> 371): 522 null
      // try (443 -> 450): 522 null
      // try (522 -> 523): 522 null
      // try (525 -> 527): 528 null
      // try (452 -> 454): 455 null
      // try (373 -> 375): 376 null
      // try (220 -> 222): 223 null
      // try (130 -> 132): 133 null
      // try (552 -> 554): 559 null
      // try (479 -> 481): 486 null
      // try (400 -> 402): 407 null
      // try (247 -> 249): 254 null
      // try (157 -> 159): 164 null
      // try (552 -> 554): 568 null
      // try (479 -> 481): 495 null
      // try (400 -> 402): 416 null
      // try (247 -> 249): 263 null
      // try (157 -> 159): 173 null
      // try (559 -> 563): 568 null
      // try (486 -> 490): 495 null
      // try (407 -> 411): 416 null
      // try (254 -> 258): 263 null
      // try (164 -> 168): 173 null
      // try (568 -> 569): 568 null
      // try (495 -> 496): 495 null
      // try (416 -> 417): 416 null
      // try (263 -> 264): 263 null
      // try (173 -> 174): 173 null
   }

   @Override
   public final void abort() {
      super.abort();
      this._ddBrowserContent.abort();
   }

   @Override
   protected final void progressUpdate(long totalSizeDownloaded) {
      this.checkDownloadLength(totalSizeDownloaded);
      this._listener.progressUpdate((int)totalSizeDownloaded);
   }

   @Override
   protected final void setError(String errorMessage) {
      this._otaStatus = 954;
      this._otaError = errorMessage;
   }

   @Override
   protected final void setError(int httpResponseCode) {
      if (httpResponseCode == 400) {
         this._otaStatus = 903;
         this._otaError = DownloadManager.formatHttpStatusMessage(httpResponseCode);
      } else {
         this._otaStatus = 954;
         this._otaError = DownloadManager.formatHttpStatusMessage(httpResponseCode);
      }
   }

   @Override
   protected final void setError(Throwable t) {
      if (t instanceof Object) {
         this._otaStatus = 901;
         this._otaError = BrowserResources.getString(588);
      } else if (t instanceof Object || t instanceof Object) {
         this._otaStatus = 905;
         this._otaError = t.getMessage();
      } else if (!(t instanceof Object)) {
         this.setError(t.getMessage());
      } else {
         int errorCode = ((FileIOException)t).getErrorCode();
         switch (errorCode) {
            case 9:
               this._otaStatus = 901;
               this._otaError = t.getMessage();
               return;
            default:
               this.setError(t.getMessage());
         }
      }
   }

   @Override
   protected final void checkContentType(String contentType) {
      this._contentType = contentType;
      this._numTypes++;
      contentType = RendererControl.stripContentTypeParameters(contentType);
      if (contentType == null
         || !contentType.equalsIgnoreCase(this._expectedContentType)
            && (this._numTypes != 1 || !contentType.equalsIgnoreCase("application/vnd.oma.drm.message"))) {
         throw new Object(MessageFormat.format(BrowserResources.getString(785), new Object[]{contentType, this._expectedContentType}));
      }
   }

   @Override
   protected final void checkDownloadLength(long downloadLength) {
      if (downloadLength > 0 && downloadLength > this._maxDownloadSize) {
         throw new Object(MessageFormat.format(BrowserResources.getString(786), new Object[]{Long.toString(downloadLength)}));
      }
   }

   private static final boolean canRenderMediaObject(String contentType, byte[] mediaObject) {
      if (StringUtilities.startsWithIgnoreCase(contentType, "image/", 1701707776)) {
         try {
            EncodedImage.createEncodedImage(mediaObject, 0, mediaObject.length, RendererControl.stripContentTypeParameters(contentType));
            return true;
         } finally {
            ;
         }
      } else {
         return true;
      }
   }
}
