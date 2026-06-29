package net.rim.device.apps.internal.browser.multipart;

import java.io.InputStream;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.internal.browser.util.Pipe;

public final class MultipartInputStream extends InputStream {
   private InputStream _subIn;
   private boolean _wap;
   private InputStream _currentIn;
   private String _rootUrl;
   private MimeMultipartParser _mimeMultipartParser;
   private FetchRequest _fetchRequest;
   private BrowserContent _browserContent;

   public MultipartInputStream(
      boolean wap, InputStream in, Pipe data, String rootUrl, int dataLen, MimeMultipartParser mimeMultipartParser, FetchRequest fetchRequest
   ) {
      this._wap = wap;
      this._subIn = in;
      this._rootUrl = rootUrl;
      this._mimeMultipartParser = mimeMultipartParser;
      this._fetchRequest = fetchRequest;
      InputStream pipeInputStream = data.getInputStream();
      this._currentIn = (InputStream)(dataLen >= 0 ? new Object(pipeInputStream, dataLen, false, false) : pipeInputStream);
   }

   @Override
   public final void close() {
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
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/browser/multipart/MultipartInputStream._subIn Ljava/io/InputStream;
      // 04: ifnonnull 0a
      // 07: goto bd
      // 0a: aload 0
      // 0b: getfield net/rim/device/apps/internal/browser/multipart/MultipartInputStream._mimeMultipartParser Lnet/rim/device/apps/internal/browser/multipart/MimeMultipartParser;
      // 0e: ifnull 1b
      // 11: aload 0
      // 12: getfield net/rim/device/apps/internal/browser/multipart/MultipartInputStream._mimeMultipartParser Lnet/rim/device/apps/internal/browser/multipart/MimeMultipartParser;
      // 15: invokevirtual net/rim/device/apps/internal/browser/multipart/MimeMultipartParser.nextPart ()Z
      // 18: ifeq 91
      // 1b: aload 0
      // 1c: getfield net/rim/device/apps/internal/browser/multipart/MultipartInputStream._wap Z
      // 1f: bipush 4
      // 21: aload 0
      // 22: getfield net/rim/device/apps/internal/browser/multipart/MultipartInputStream._mimeMultipartParser Lnet/rim/device/apps/internal/browser/multipart/MimeMultipartParser;
      // 25: aload 0
      // 26: getfield net/rim/device/apps/internal/browser/multipart/MultipartInputStream._subIn Ljava/io/InputStream;
      // 29: aload 0
      // 2a: getfield net/rim/device/apps/internal/browser/multipart/MultipartInputStream._rootUrl Ljava/lang/String;
      // 2d: aload 0
      // 2e: getfield net/rim/device/apps/internal/browser/multipart/MultipartInputStream._fetchRequest Lnet/rim/device/apps/internal/browser/stack/FetchRequest;
      // 31: aconst_null
      // 32: invokestatic net/rim/device/apps/internal/browser/multipart/MultipartConverter.processEntity (ZILnet/rim/device/apps/internal/browser/multipart/MimeMultipartParser;Ljava/io/InputStream;Ljava/lang/String;Lnet/rim/device/apps/internal/browser/stack/FetchRequest;Lnet/rim/device/apps/internal/browser/stack/CacheResult;)Ljava/lang/Object;
      // 35: checkcast net/rim/device/apps/internal/browser/stack/CachedHttpConnection
      // 38: astore 1
      // 39: aload 0
      // 3a: getfield net/rim/device/apps/internal/browser/multipart/MultipartInputStream._browserContent Lnet/rim/device/api/browser/field/BrowserContent;
      // 3d: ifnull 0a
      // 40: aload 1
      // 41: ifnull 0a
      // 44: aload 0
      // 45: getfield net/rim/device/apps/internal/browser/multipart/MultipartInputStream._fetchRequest Lnet/rim/device/apps/internal/browser/stack/FetchRequest;
      // 48: ifnull 60
      // 4b: aload 0
      // 4c: getfield net/rim/device/apps/internal/browser/multipart/MultipartInputStream._fetchRequest Lnet/rim/device/apps/internal/browser/stack/FetchRequest;
      // 4f: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.isAborted ()Z
      // 52: ifeq 60
      // 55: aload 1
      // 56: invokevirtual net/rim/device/apps/internal/browser/stack/CachedHttpConnection.close ()V
      // 59: goto 91
      // 5c: astore 2
      // 5d: goto 91
      // 60: new java/lang/Object
      // 63: dup
      // 64: aload 1
      // 65: invokevirtual net/rim/device/apps/internal/browser/stack/CachedHttpConnection.getURL ()Ljava/lang/String;
      // 68: invokespecial net/rim/device/api/browser/field/RequestedResource.<init> (Ljava/lang/String;)V
      // 6b: astore 2
      // 6c: aload 2
      // 6d: aload 1
      // 6e: invokevirtual net/rim/device/api/browser/field/RequestedResource.setHttpConnection (Ljavax/microedition/io/HttpConnection;)V
      // 71: aload 0
      // 72: getfield net/rim/device/apps/internal/browser/multipart/MultipartInputStream._browserContent Lnet/rim/device/api/browser/field/BrowserContent;
      // 75: aload 2
      // 76: invokeinterface net/rim/device/api/browser/field/BrowserContent.resourceReady (Lnet/rim/device/api/browser/field/RequestedResource;)V 2
      // 7b: aload 1
      // 7c: invokevirtual net/rim/device/apps/internal/browser/stack/CachedHttpConnection.close ()V
      // 7f: goto 0a
      // 82: astore 2
      // 83: goto 0a
      // 86: astore 3
      // 87: aload 1
      // 88: invokevirtual net/rim/device/apps/internal/browser/stack/CachedHttpConnection.close ()V
      // 8b: aload 3
      // 8c: athrow
      // 8d: astore 4
      // 8f: aload 3
      // 90: athrow
      // 91: aload 0
      // 92: getfield net/rim/device/apps/internal/browser/multipart/MultipartInputStream._subIn Ljava/io/InputStream;
      // 95: invokevirtual java/io/InputStream.close ()V
      // 98: aload 0
      // 99: aconst_null
      // 9a: putfield net/rim/device/apps/internal/browser/multipart/MultipartInputStream._subIn Ljava/io/InputStream;
      // 9d: return
      // 9e: astore 1
      // 9f: aload 0
      // a0: getfield net/rim/device/apps/internal/browser/multipart/MultipartInputStream._subIn Ljava/io/InputStream;
      // a3: invokevirtual java/io/InputStream.close ()V
      // a6: aload 0
      // a7: aconst_null
      // a8: putfield net/rim/device/apps/internal/browser/multipart/MultipartInputStream._subIn Ljava/io/InputStream;
      // ab: return
      // ac: astore 5
      // ae: aload 0
      // af: getfield net/rim/device/apps/internal/browser/multipart/MultipartInputStream._subIn Ljava/io/InputStream;
      // b2: invokevirtual java/io/InputStream.close ()V
      // b5: aload 0
      // b6: aconst_null
      // b7: putfield net/rim/device/apps/internal/browser/multipart/MultipartInputStream._subIn Ljava/io/InputStream;
      // ba: aload 5
      // bc: athrow
      // bd: return
      // try (38 -> 40): 41 null
      // try (56 -> 58): 59 null
      // try (31 -> 38): 61 null
      // try (43 -> 56): 61 null
      // try (62 -> 64): 66 null
      // try (61 -> 62): 61 null
      // try (4 -> 69): 76 null
      // try (4 -> 69): 84 null
      // try (76 -> 77): 84 null
      // try (84 -> 85): 84 null
   }

   public final void setBrowserContent(BrowserContent browserContent) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final int read() {
      return this._currentIn.read();
   }

   @Override
   public final int read(byte[] b, int off, int len) {
      return this._currentIn.read(b, off, len);
   }
}
