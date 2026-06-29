package net.rim.device.apps.internal.browser.download;

import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.system.Memory;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.multipart.MimeMultipartParser;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.util.RendererControl;

public class DownloadManager implements Runnable {
   private boolean _aborted;
   private Object _currentResourceRequest;
   private InputConnection _currentInputConnection;
   private boolean _drmProtected;
   protected long _totalDecodedSizeDownloaded;
   private RenderingApplication _renderingApp;
   protected static final String DRM_CONTENT_TYPE = "application/vnd.oma.drm.message";

   protected InputStream unwrapDRM(InputStream inputStream, String contentType) {
      if (contentType != null && StringUtilities.startsWithIgnoreCase(contentType, "application/vnd.oma.drm.message", 1701707776)) {
         this._drmProtected = true;
         MimeMultipartParser mimeMultipart = new MimeMultipartParser(inputStream, contentType);
         this.checkContentType(mimeMultipart.getPartHeaders().getPropertyValue("Content-Type"));
         return mimeMultipart.getPartContent();
      } else {
         this._drmProtected = false;
         return inputStream;
      }
   }

   public void abort() {
      if (!this._aborted) {
         this._aborted = true;
         new DownloadManager$1(this).start();
      }
   }

   public boolean isAborted() {
      return this._aborted;
   }

   public boolean isDrmProtected() {
      return this._drmProtected;
   }

   public void setDrmProtected(boolean drmProtected) {
      this._drmProtected = drmProtected;
   }

   protected void closeInputConnection() {
      if (this._currentInputConnection != null) {
         label19:
         try {
            this._currentInputConnection.close();
         } finally {
            break label19;
         }
      }

      this._currentInputConnection = null;
      this._currentResourceRequest = null;
   }

   protected void setError(String _1) {
      throw null;
   }

   protected void setError(int _1) {
      throw null;
   }

   protected void setError(Throwable _1) {
      throw null;
   }

   protected void checkContentType(String _1) {
      throw null;
   }

   protected void checkDownloadLength(long downloadLength) {
   }

   protected InputStream requestResourceStream(String param1, HttpHeaders param2, BrowserConfigRecord param3) {
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
      // 001: istore 4
      // 003: iload 4
      // 005: bipush 10
      // 007: if_icmplt 00d
      // 00a: goto 210
      // 00d: aload 0
      // 00e: getfield net/rim/device/apps/internal/browser/download/DownloadManager._renderingApp Lnet/rim/device/api/browser/field/RenderingApplication;
      // 011: ifnull 066
      // 014: new java/lang/Object
      // 017: dup
      // 018: aload 1
      // 019: aload 2
      // 01a: ifnull 024
      // 01d: aload 2
      // 01e: invokevirtual net/rim/device/api/io/http/HttpHeaders.cloneHeaders ()Lnet/rim/device/api/io/http/HttpHeaders;
      // 021: goto 025
      // 024: aconst_null
      // 025: ldc_w 65539
      // 028: invokespecial net/rim/device/api/browser/field/RequestedResource.<init> (Ljava/lang/String;Lnet/rim/device/api/io/http/HttpHeaders;I)V
      // 02b: astore 5
      // 02d: aload 0
      // 02e: aload 5
      // 030: putfield net/rim/device/apps/internal/browser/download/DownloadManager._currentResourceRequest Ljava/lang/Object;
      // 033: aload 0
      // 034: getfield net/rim/device/apps/internal/browser/download/DownloadManager._renderingApp Lnet/rim/device/api/browser/field/RenderingApplication;
      // 037: instanceof java/lang/Object
      // 03a: ifeq 053
      // 03d: aload 0
      // 03e: aload 0
      // 03f: getfield net/rim/device/apps/internal/browser/download/DownloadManager._renderingApp Lnet/rim/device/api/browser/field/RenderingApplication;
      // 042: checkcast java/lang/Object
      // 045: aload 5
      // 047: aconst_null
      // 048: invokeinterface net/rim/device/api/browser/field/ResourceProvider.getInputConnection (Lnet/rim/device/api/browser/field/RequestedResource;Lnet/rim/device/api/browser/field/BrowserContent;)Ljavax/microedition/io/InputConnection; 3
      // 04d: putfield net/rim/device/apps/internal/browser/download/DownloadManager._currentInputConnection Ljavax/microedition/io/InputConnection;
      // 050: goto 0ab
      // 053: aload 0
      // 054: aload 0
      // 055: getfield net/rim/device/apps/internal/browser/download/DownloadManager._renderingApp Lnet/rim/device/api/browser/field/RenderingApplication;
      // 058: aload 5
      // 05a: aconst_null
      // 05b: invokeinterface net/rim/device/api/browser/field/RenderingApplication.getResource (Lnet/rim/device/api/browser/field/RequestedResource;Lnet/rim/device/api/browser/field/BrowserContent;)Ljavax/microedition/io/HttpConnection; 3
      // 060: putfield net/rim/device/apps/internal/browser/download/DownloadManager._currentInputConnection Ljavax/microedition/io/InputConnection;
      // 063: goto 0ab
      // 066: invokestatic net/rim/device/apps/internal/browser/core/BrowserDaemonRegistry.getInstance ()Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 069: astore 5
      // 06b: aload 5
      // 06d: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getRawDataCache ()Lnet/rim/device/apps/internal/browser/stack/RawDataCache;
      // 070: astore 6
      // 072: new net/rim/device/apps/internal/browser/stack/ModelResult
      // 075: dup
      // 076: aload 1
      // 077: ldc_w 65539
      // 07a: aload 2
      // 07b: ifnull 085
      // 07e: aload 2
      // 07f: invokevirtual net/rim/device/api/io/http/HttpHeaders.cloneHeaders ()Lnet/rim/device/api/io/http/HttpHeaders;
      // 082: goto 086
      // 085: aconst_null
      // 086: invokespecial net/rim/device/apps/internal/browser/stack/ModelResult.<init> (Ljava/lang/String;ILnet/rim/device/api/io/http/HttpHeaders;)V
      // 089: astore 7
      // 08b: new net/rim/device/apps/internal/browser/stack/FetchRequest
      // 08e: dup
      // 08f: aload 7
      // 091: aload 3
      // 092: invokespecial net/rim/device/apps/internal/browser/stack/FetchRequest.<init> (Lnet/rim/device/apps/internal/browser/stack/ModelResult;Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;)V
      // 095: astore 8
      // 097: aload 0
      // 098: aload 8
      // 09a: putfield net/rim/device/apps/internal/browser/download/DownloadManager._currentResourceRequest Ljava/lang/Object;
      // 09d: aload 0
      // 09e: aload 6
      // 0a0: aload 8
      // 0a2: invokevirtual net/rim/device/apps/internal/browser/stack/RawDataCache.get (Lnet/rim/device/apps/internal/browser/stack/FetchRequest;)Ljavax/microedition/io/InputConnection;
      // 0a5: checkcast java/lang/Object
      // 0a8: putfield net/rim/device/apps/internal/browser/download/DownloadManager._currentInputConnection Ljavax/microedition/io/InputConnection;
      // 0ab: aload 0
      // 0ac: getfield net/rim/device/apps/internal/browser/download/DownloadManager._currentInputConnection Ljavax/microedition/io/InputConnection;
      // 0af: ifnonnull 0b5
      // 0b2: goto 1f2
      // 0b5: bipush 0
      // 0b6: istore 5
      // 0b8: aload 0
      // 0b9: getfield net/rim/device/apps/internal/browser/download/DownloadManager._currentInputConnection Ljavax/microedition/io/InputConnection;
      // 0bc: dup
      // 0bd: instanceof java/lang/Object
      // 0c0: ifne 0c7
      // 0c3: pop
      // 0c4: goto 1b9
      // 0c7: checkcast java/lang/Object
      // 0ca: astore 6
      // 0cc: aload 6
      // 0ce: invokeinterface javax/microedition/io/HttpConnection.getResponseCode ()I 1
      // 0d3: istore 7
      // 0d5: iload 7
      // 0d7: sipush 200
      // 0da: if_icmplt 0eb
      // 0dd: iload 7
      // 0df: sipush 300
      // 0e2: if_icmpge 0eb
      // 0e5: bipush 1
      // 0e6: istore 5
      // 0e8: goto 1bc
      // 0eb: iload 7
      // 0ed: sipush 300
      // 0f0: if_icmpge 0f6
      // 0f3: goto 1b0
      // 0f6: iload 7
      // 0f8: sipush 400
      // 0fb: if_icmplt 101
      // 0fe: goto 1b0
      // 101: iload 7
      // 103: sipush 305
      // 106: if_icmpne 10c
      // 109: goto 1b0
      // 10c: aload 6
      // 10e: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getRedirectURL (Ljavax/microedition/io/HttpConnection;)Ljava/lang/String;
      // 111: astore 1
      // 112: aload 0
      // 113: getfield net/rim/device/apps/internal/browser/download/DownloadManager._currentInputConnection Ljavax/microedition/io/InputConnection;
      // 116: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 11b: astore 8
      // 11d: sipush 512
      // 120: newarray 8
      // 122: astore 9
      // 124: aload 8
      // 126: aload 9
      // 128: invokevirtual java/io/InputStream.read ([B)I
      // 12b: bipush -1
      // 12d: if_icmpeq 133
      // 130: goto 124
      // 133: aload 8
      // 135: invokevirtual java/io/InputStream.close ()V
      // 138: goto 13d
      // 13b: astore 9
      // 13d: aload 0
      // 13e: getfield net/rim/device/apps/internal/browser/download/DownloadManager._currentInputConnection Ljavax/microedition/io/InputConnection;
      // 141: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 146: goto 14b
      // 149: astore 9
      // 14b: aload 0
      // 14c: aconst_null
      // 14d: putfield net/rim/device/apps/internal/browser/download/DownloadManager._currentInputConnection Ljavax/microedition/io/InputConnection;
      // 150: goto 197
      // 153: astore 9
      // 155: aload 8
      // 157: invokevirtual java/io/InputStream.close ()V
      // 15a: goto 15f
      // 15d: astore 9
      // 15f: aload 0
      // 160: getfield net/rim/device/apps/internal/browser/download/DownloadManager._currentInputConnection Ljavax/microedition/io/InputConnection;
      // 163: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 168: goto 16d
      // 16b: astore 9
      // 16d: aload 0
      // 16e: aconst_null
      // 16f: putfield net/rim/device/apps/internal/browser/download/DownloadManager._currentInputConnection Ljavax/microedition/io/InputConnection;
      // 172: goto 197
      // 175: astore 10
      // 177: aload 8
      // 179: invokevirtual java/io/InputStream.close ()V
      // 17c: goto 181
      // 17f: astore 11
      // 181: aload 0
      // 182: getfield net/rim/device/apps/internal/browser/download/DownloadManager._currentInputConnection Ljavax/microedition/io/InputConnection;
      // 185: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 18a: goto 18f
      // 18d: astore 11
      // 18f: aload 0
      // 190: aconst_null
      // 191: putfield net/rim/device/apps/internal/browser/download/DownloadManager._currentInputConnection Ljavax/microedition/io/InputConnection;
      // 194: aload 10
      // 196: athrow
      // 197: aload 1
      // 198: ifnull 1a7
      // 19b: iload 4
      // 19d: bipush 1
      // 19e: iadd
      // 19f: bipush 10
      // 1a1: if_icmpge 1a7
      // 1a4: goto 20a
      // 1a7: aload 0
      // 1a8: iload 7
      // 1aa: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.setError (I)V
      // 1ad: goto 1bc
      // 1b0: aload 0
      // 1b1: iload 7
      // 1b3: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.setError (I)V
      // 1b6: goto 1bc
      // 1b9: bipush 1
      // 1ba: istore 5
      // 1bc: iload 5
      // 1be: ifeq 208
      // 1c1: aload 0
      // 1c2: getfield net/rim/device/apps/internal/browser/download/DownloadManager._currentInputConnection Ljavax/microedition/io/InputConnection;
      // 1c5: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getContentType (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 1c8: astore 6
      // 1ca: aload 0
      // 1cb: aload 6
      // 1cd: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.checkContentType (Ljava/lang/String;)V
      // 1d0: aload 0
      // 1d1: aload 0
      // 1d2: getfield net/rim/device/apps/internal/browser/download/DownloadManager._currentInputConnection Ljavax/microedition/io/InputConnection;
      // 1d5: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getContentLength (Ljavax/microedition/io/InputConnection;)J
      // 1d8: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.checkDownloadLength (J)V
      // 1db: aload 0
      // 1dc: aload 0
      // 1dd: getfield net/rim/device/apps/internal/browser/download/DownloadManager._currentInputConnection Ljavax/microedition/io/InputConnection;
      // 1e0: aload 0
      // 1e1: getfield net/rim/device/apps/internal/browser/download/DownloadManager._currentInputConnection Ljavax/microedition/io/InputConnection;
      // 1e4: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 1e9: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getInputStreamFromContentEncoding (Ljavax/microedition/io/InputConnection;Ljava/io/InputStream;)Ljava/io/InputStream;
      // 1ec: aload 6
      // 1ee: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.unwrapDRM (Ljava/io/InputStream;Ljava/lang/String;)Ljava/io/InputStream;
      // 1f1: areturn
      // 1f2: aload 0
      // 1f3: sipush 234
      // 1f6: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 1f9: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.setError (Ljava/lang/String;)V
      // 1fc: aconst_null
      // 1fd: areturn
      // 1fe: astore 5
      // 200: aload 0
      // 201: aload 5
      // 203: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.setError (Ljava/lang/Throwable;)V
      // 206: aconst_null
      // 207: areturn
      // 208: aconst_null
      // 209: areturn
      // 20a: iinc 4 1
      // 20d: goto 003
      // 210: aconst_null
      // 211: areturn
      // try (132 -> 134): 135 null
      // try (136 -> 139): 140 null
      // try (123 -> 132): 145 null
      // try (146 -> 148): 149 null
      // try (150 -> 153): 154 null
      // try (123 -> 132): 159 null
      // try (145 -> 146): 159 null
      // try (160 -> 162): 163 null
      // try (164 -> 167): 168 null
      // try (159 -> 160): 159 null
      // try (77 -> 181): 222 null
      // try (182 -> 215): 222 null
      // try (216 -> 220): 222 null
   }

   protected void progressUpdate(long totalDecodedSizeDownloaded) {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected byte[] requestResource(String url, HttpHeaders requestHeaders, BrowserConfigRecord browserConfigRecord) {
      InputStream in = null;
      boolean var18 = false /* VF: Semaphore variable */;

      byte[] var5;
      try {
         var18 = true;
         in = this.requestResourceStream(url, requestHeaders, browserConfigRecord);
         var5 = this.readStream(in);
         var18 = false;
      } finally {
         if (var18) {
            if (in != null) {
               label73:
               try {
                  in.close();
               } finally {
                  break label73;
               }
            }

            this.closeInputConnection();
         }
      }

      if (in != null) {
         label81:
         try {
            in.close();
         } finally {
            break label81;
         }
      }

      this.closeInputConnection();
      return var5;
   }

   protected void saveStream(InputStream param1, OutputStream param2, int param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ifnull 5e
      // 04: bipush 0
      // 05: istore 4
      // 07: iload 3
      // 08: newarray 8
      // 0a: astore 5
      // 0c: aload 1
      // 0d: aload 5
      // 0f: bipush 0
      // 10: iload 3
      // 11: invokevirtual java/io/InputStream.read ([BII)I
      // 14: dup
      // 15: istore 4
      // 17: bipush -1
      // 19: if_icmpeq 5e
      // 1c: aload 0
      // 1d: getfield net/rim/device/apps/internal/browser/download/DownloadManager._aborted Z
      // 20: ifeq 24
      // 23: return
      // 24: aload 0
      // 25: aload 0
      // 26: getfield net/rim/device/apps/internal/browser/download/DownloadManager._totalDecodedSizeDownloaded J
      // 29: iload 4
      // 2b: i2l
      // 2c: ladd
      // 2d: putfield net/rim/device/apps/internal/browser/download/DownloadManager._totalDecodedSizeDownloaded J
      // 30: aload 2
      // 31: aload 5
      // 33: bipush 0
      // 34: iload 4
      // 36: invokevirtual java/io/OutputStream.write ([BII)V
      // 39: aload 0
      // 3a: aload 0
      // 3b: getfield net/rim/device/apps/internal/browser/download/DownloadManager._totalDecodedSizeDownloaded J
      // 3e: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.progressUpdate (J)V
      // 41: goto 0c
      // 44: astore 6
      // 46: aload 0
      // 47: aload 6
      // 49: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.setError (Ljava/lang/Throwable;)V
      // 4c: return
      // 4d: astore 6
      // 4f: aload 0
      // 50: aload 6
      // 52: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.setError (Ljava/lang/Throwable;)V
      // 55: return
      // 56: astore 6
      // 58: aload 0
      // 59: aload 6
      // 5b: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.setError (Ljava/lang/Throwable;)V
      // 5e: return
      // try (7 -> 19): 37 net/rim/device/apps/internal/browser/download/SizeTooLargeException
      // try (20 -> 37): 37 net/rim/device/apps/internal/browser/download/SizeTooLargeException
      // try (7 -> 19): 42 null
      // try (20 -> 37): 42 null
      // try (7 -> 19): 47 null
      // try (20 -> 37): 47 null
   }

   protected byte[] readStream(InputStream param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ifnull 7f
      // 04: sipush 2048
      // 07: istore 2
      // 08: bipush 0
      // 09: istore 3
      // 0a: bipush 0
      // 0b: istore 4
      // 0d: iload 2
      // 0e: newarray 8
      // 10: astore 5
      // 12: aload 1
      // 13: aload 5
      // 15: iload 3
      // 16: iload 2
      // 17: iload 3
      // 18: isub
      // 19: invokevirtual java/io/InputStream.read ([BII)I
      // 1c: dup
      // 1d: istore 4
      // 1f: bipush -1
      // 21: if_icmpeq 55
      // 24: aload 0
      // 25: getfield net/rim/device/apps/internal/browser/download/DownloadManager._aborted Z
      // 28: ifeq 2d
      // 2b: aconst_null
      // 2c: areturn
      // 2d: iload 3
      // 2e: iload 4
      // 30: iadd
      // 31: istore 3
      // 32: aload 0
      // 33: aload 0
      // 34: getfield net/rim/device/apps/internal/browser/download/DownloadManager._totalDecodedSizeDownloaded J
      // 37: iload 4
      // 39: i2l
      // 3a: ladd
      // 3b: putfield net/rim/device/apps/internal/browser/download/DownloadManager._totalDecodedSizeDownloaded J
      // 3e: aload 0
      // 3f: aload 0
      // 40: getfield net/rim/device/apps/internal/browser/download/DownloadManager._totalDecodedSizeDownloaded J
      // 43: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.progressUpdate (J)V
      // 46: wide iinc 2 2048
      // 4c: aload 5
      // 4e: iload 2
      // 4f: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 52: goto 12
      // 55: aload 5
      // 57: iload 3
      // 58: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 5b: goto 7c
      // 5e: astore 6
      // 60: aload 0
      // 61: aload 6
      // 63: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.setError (Ljava/lang/Throwable;)V
      // 66: aconst_null
      // 67: areturn
      // 68: astore 6
      // 6a: aload 0
      // 6b: aload 6
      // 6d: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.setError (Ljava/lang/Throwable;)V
      // 70: aconst_null
      // 71: areturn
      // 72: astore 6
      // 74: aload 0
      // 75: aload 6
      // 77: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.setError (Ljava/lang/Throwable;)V
      // 7a: aconst_null
      // 7b: areturn
      // 7c: aload 5
      // 7e: areturn
      // 7f: aconst_null
      // 80: areturn
      // try (11 -> 26): 51 net/rim/device/apps/internal/browser/download/SizeTooLargeException
      // try (27 -> 50): 51 net/rim/device/apps/internal/browser/download/SizeTooLargeException
      // try (11 -> 26): 57 null
      // try (27 -> 50): 57 null
      // try (11 -> 26): 63 null
      // try (27 -> 50): 63 null
   }

   @Override
   public void run() {
      throw null;
   }

   public DownloadManager(RenderingApplication app) {
      this._renderingApp = app;
   }

   public static String formatHttpStatusMessage(int status) {
      StringBuffer message = (StringBuffer)(new Object(BrowserResources.getString(264)));
      message.append(status);
      message.append(':');
      message.append(' ');
      message.append(RendererControl.getStatusDescription(status));
      return message.toString();
   }

   private static synchronized boolean attemptFlashRecovery(int size) {
      int originalSize = size;
      boolean retval = true;
      if (size > Memory.getFlashFree()) {
         DownloadManager$DownloadLowMemoryListener lmmListener = new DownloadManager$DownloadLowMemoryListener();
         LowMemoryManager.addLowMemoryFailedListener(lmmListener);

         for (int i = 0; i < 8; i++) {
            net.rim.vm.Memory.recoverFlash(size);
            LowMemoryManager.poll();
            if (lmmListener._lmm_failed || size <= Memory.getFlashFree()) {
               break;
            }

            size += 16384;
         }

         LowMemoryManager.removeLowMemoryFailedListener(lmmListener);
         retval = !lmmListener._lmm_failed;
         if (retval) {
            net.rim.vm.Memory.persistentGC();
            retval = originalSize <= Memory.getFlashFree();
         }
      }

      return retval;
   }

   public static synchronized boolean ensureAvailableFlash(int size) {
      boolean flashRecovered = attemptFlashRecovery(size);
      if (!flashRecovered) {
         net.rim.vm.Memory.emergencyGC();
         flashRecovered = attemptFlashRecovery(size);
      }

      return flashRecovered;
   }
}
