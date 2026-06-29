package net.rim.device.apps.internal.browser.stack;

import javax.microedition.io.InputConnection;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.cldc.io.utility.SessionStats;

public final class LocalStackAdapter implements NetworkPageFetcher {
   private LocalStackAdapter() {
   }

   public static final void registerOnStartup() {
      StackManager stackManager = StackManager.getInstance();
      stackManager.registerNetworkPageFetcher(new LocalStackAdapter(), new String[]{"cod", "data", "store", "local"});
   }

   @Override
   public final void reinitialize() {
   }

   @Override
   public final InputConnection fetchPage(FetchRequest param1, BrowserConfigRecord param2) {
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
      // 00b: aconst_null
      // 00c: astore 5
      // 00e: aconst_null
      // 00f: astore 6
      // 011: aload 4
      // 013: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.getURLWithoutFragment ()Ljava/lang/String;
      // 016: astore 7
      // 018: aload 7
      // 01a: bipush 3
      // 01c: bipush 1
      // 01d: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;IZ)Ljavax/microedition/io/Connection;
      // 020: checkcast javax/microedition/io/HttpConnection
      // 023: astore 5
      // 025: aload 5
      // 027: ifnonnull 032
      // 02a: new java/io/IOException
      // 02d: dup
      // 02e: invokespecial java/io/IOException.<init> ()V
      // 031: athrow
      // 032: aload 5
      // 034: ldc_w "GET"
      // 037: invokeinterface javax/microedition/io/HttpConnection.setRequestMethod (Ljava/lang/String;)V 2
      // 03c: aload 5
      // 03e: invokeinterface javax/microedition/io/InputConnection.openDataInputStream ()Ljava/io/DataInputStream; 1
      // 043: astore 6
      // 045: aload 3
      // 046: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getRequestHeaders ()Lnet/rim/device/api/io/http/HttpHeaders;
      // 049: astore 8
      // 04b: aload 8
      // 04d: ifnull 080
      // 050: aload 8
      // 052: invokevirtual net/rim/device/api/io/http/HttpHeaders.size ()I
      // 055: istore 9
      // 057: bipush 0
      // 058: istore 10
      // 05a: iload 10
      // 05c: iload 9
      // 05e: if_icmpge 080
      // 061: aload 8
      // 063: iload 10
      // 065: invokevirtual net/rim/device/api/io/http/HttpHeaders.getPropertyKey (I)Ljava/lang/String;
      // 068: astore 11
      // 06a: aload 5
      // 06c: aload 11
      // 06e: aload 8
      // 070: iload 10
      // 072: invokevirtual net/rim/device/api/io/http/HttpHeaders.getPropertyValue (I)Ljava/lang/String;
      // 075: invokeinterface javax/microedition/io/HttpConnection.setRequestProperty (Ljava/lang/String;Ljava/lang/String;)V 3
      // 07a: iinc 10 1
      // 07d: goto 05a
      // 080: new net/rim/device/api/io/http/HttpHeaders
      // 083: dup
      // 084: invokespecial net/rim/device/api/io/http/HttpHeaders.<init> ()V
      // 087: astore 9
      // 089: bipush 0
      // 08a: istore 10
      // 08c: aload 5
      // 08e: iload 10
      // 090: invokeinterface javax/microedition/io/HttpConnection.getHeaderFieldKey (I)Ljava/lang/String; 2
      // 095: astore 11
      // 097: aload 11
      // 099: ifnonnull 09f
      // 09c: goto 0b5
      // 09f: aload 9
      // 0a1: aload 11
      // 0a3: aload 5
      // 0a5: iload 10
      // 0a7: invokeinterface javax/microedition/io/HttpConnection.getHeaderField (I)Ljava/lang/String; 2
      // 0ac: invokevirtual net/rim/device/api/io/http/HttpHeaders.addProperty (Ljava/lang/String;Ljava/lang/String;)V
      // 0af: iinc 10 1
      // 0b2: goto 08c
      // 0b5: aload 4
      // 0b7: bipush 1
      // 0b8: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setLocalContent (Z)V
      // 0bb: aload 4
      // 0bd: aload 9
      // 0bf: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setResponseHeaders (Lnet/rim/device/api/io/http/HttpHeaders;)V
      // 0c2: aload 6
      // 0c4: invokestatic net/rim/device/api/io/IOUtilities.streamToBytes (Ljava/io/InputStream;)[B
      // 0c7: astore 10
      // 0c9: aload 10
      // 0cb: ifnull 0e0
      // 0ce: aload 4
      // 0d0: new net/rim/device/internal/browser/util/Pipe
      // 0d3: dup
      // 0d4: aload 10
      // 0d6: aload 10
      // 0d8: arraylength
      // 0d9: bipush 0
      // 0da: invokespecial net/rim/device/internal/browser/util/Pipe.<init> ([BIZ)V
      // 0dd: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setData (Lnet/rim/device/internal/browser/util/Pipe;)V
      // 0e0: aload 4
      // 0e2: aload 5
      // 0e4: invokeinterface javax/microedition/io/HttpConnection.getResponseMessage ()Ljava/lang/String; 1
      // 0e9: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setResponseMessage (Ljava/lang/String;)V
      // 0ec: aload 4
      // 0ee: aload 5
      // 0f0: invokeinterface javax/microedition/io/HttpConnection.getResponseCode ()I 1
      // 0f5: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setStatus (I)V
      // 0f8: new net/rim/device/apps/internal/browser/stack/CachedHttpConnection
      // 0fb: dup
      // 0fc: aload 3
      // 0fd: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getURL ()Ljava/lang/String;
      // 100: aload 4
      // 102: aload 9
      // 104: invokespecial net/rim/device/apps/internal/browser/stack/CachedHttpConnection.<init> (Ljava/lang/String;Lnet/rim/device/apps/internal/browser/stack/CacheResult;Lnet/rim/device/api/io/http/HttpHeaders;)V
      // 107: astore 11
      // 109: aload 6
      // 10b: ifnull 118
      // 10e: aload 6
      // 110: invokevirtual java/io/InputStream.close ()V
      // 113: goto 118
      // 116: astore 12
      // 118: aload 5
      // 11a: ifnull 129
      // 11d: aload 5
      // 11f: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 124: goto 129
      // 127: astore 12
      // 129: aload 11
      // 12b: areturn
      // 12c: astore 7
      // 12e: aload 4
      // 130: sipush 400
      // 133: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setStatus (I)V
      // 136: aload 7
      // 138: athrow
      // 139: astore 7
      // 13b: aload 4
      // 13d: sipush 400
      // 140: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setStatus (I)V
      // 143: new net/rim/device/cldc/io/utility/MalformedURLException
      // 146: dup
      // 147: invokespecial net/rim/device/cldc/io/utility/MalformedURLException.<init> ()V
      // 14a: athrow
      // 14b: astore 13
      // 14d: aload 6
      // 14f: ifnull 15c
      // 152: aload 6
      // 154: invokevirtual java/io/InputStream.close ()V
      // 157: goto 15c
      // 15a: astore 14
      // 15c: aload 5
      // 15e: ifnull 16d
      // 161: aload 5
      // 163: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 168: goto 16d
      // 16b: astore 14
      // 16d: aload 13
      // 16f: athrow
      // try (115 -> 117): 118 null
      // try (121 -> 123): 124 null
      // try (10 -> 113): 127 null
      // try (10 -> 113): 133 null
      // try (10 -> 113): 141 null
      // try (144 -> 146): 147 null
      // try (150 -> 152): 153 null
      // try (127 -> 142): 141 null
   }

   @Override
   public final SessionStats getSessionStats() {
      return null;
   }
}
