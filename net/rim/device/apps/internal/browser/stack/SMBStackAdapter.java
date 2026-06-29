package net.rim.device.apps.internal.browser.stack;

import javax.microedition.io.InputConnection;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.cldc.io.utility.SessionStats;

public final class SMBStackAdapter implements NetworkPageFetcher {
   private SMBStackAdapter() {
   }

   public static final void registerOnStartup() {
      StackManager stackManager = StackManager.getInstance();
      stackManager.registerNetworkPageFetcher(new SMBStackAdapter(), new String[]{"smb"});
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
      // 011: bipush 0
      // 012: istore 7
      // 014: new java/lang/StringBuffer
      // 017: dup
      // 018: aload 4
      // 01a: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.getURLWithoutFragment ()Ljava/lang/String;
      // 01d: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 020: astore 8
      // 022: aload 8
      // 024: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 027: bipush 3
      // 029: bipush 1
      // 02a: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;IZ)Ljavax/microedition/io/Connection;
      // 02d: checkcast javax/microedition/io/file/FileConnection
      // 030: astore 5
      // 032: aload 5
      // 034: ifnonnull 03f
      // 037: new java/io/IOException
      // 03a: dup
      // 03b: invokespecial java/io/IOException.<init> ()V
      // 03e: athrow
      // 03f: aload 5
      // 041: checkcast net/rim/device/internal/io/file/NetworkFileConnection
      // 044: astore 9
      // 046: aload 3
      // 047: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getRequestHeaders ()Lnet/rim/device/api/io/http/HttpHeaders;
      // 04a: astore 10
      // 04c: aload 10
      // 04e: ifnull 07d
      // 051: aload 10
      // 053: invokevirtual net/rim/device/api/io/http/HttpHeaders.size ()I
      // 056: istore 11
      // 058: bipush 0
      // 059: istore 12
      // 05b: iload 12
      // 05d: iload 11
      // 05f: if_icmpge 07d
      // 062: aload 9
      // 064: aload 10
      // 066: iload 12
      // 068: invokevirtual net/rim/device/api/io/http/HttpHeaders.getPropertyKey (I)Ljava/lang/String;
      // 06b: aload 10
      // 06d: iload 12
      // 06f: invokevirtual net/rim/device/api/io/http/HttpHeaders.getPropertyValue (I)Ljava/lang/String;
      // 072: invokeinterface net/rim/device/internal/io/file/NetworkFileConnection.setRequestProperty (Ljava/lang/String;Ljava/lang/String;)V 3
      // 077: iinc 12 1
      // 07a: goto 05b
      // 07d: aload 5
      // 07f: invokeinterface javax/microedition/io/file/FileConnection.openDataInputStream ()Ljava/io/DataInputStream; 1
      // 084: astore 6
      // 086: new net/rim/device/api/io/http/HttpHeaders
      // 089: dup
      // 08a: invokespecial net/rim/device/api/io/http/HttpHeaders.<init> ()V
      // 08d: astore 11
      // 08f: aload 4
      // 091: aload 11
      // 093: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setResponseHeaders (Lnet/rim/device/api/io/http/HttpHeaders;)V
      // 096: aload 4
      // 098: bipush 1
      // 099: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setLocalContent (Z)V
      // 09c: aload 4
      // 09e: ldc_w "OK"
      // 0a1: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setResponseMessage (Ljava/lang/String;)V
      // 0a4: aload 4
      // 0a6: sipush 200
      // 0a9: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setStatus (I)V
      // 0ac: new net/rim/device/apps/internal/browser/stack/AccumulatorInputStream
      // 0af: dup
      // 0b0: aload 5
      // 0b2: aload 6
      // 0b4: aconst_null
      // 0b5: bipush 1
      // 0b6: invokespecial net/rim/device/apps/internal/browser/stack/AccumulatorInputStream.<init> (Ljavax/microedition/io/InputConnection;Ljava/io/InputStream;Lnet/rim/device/cldc/io/utility/SessionStats;Z)V
      // 0b9: astore 12
      // 0bb: aload 4
      // 0bd: aload 12
      // 0bf: invokevirtual net/rim/device/apps/internal/browser/stack/AccumulatorInputStream.getPipe ()Lnet/rim/device/internal/browser/util/Pipe;
      // 0c2: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setData (Lnet/rim/device/internal/browser/util/Pipe;)V
      // 0c5: bipush 1
      // 0c6: istore 7
      // 0c8: new net/rim/device/apps/internal/browser/stack/CachedFileConnection
      // 0cb: dup
      // 0cc: aload 5
      // 0ce: aload 12
      // 0d0: aload 4
      // 0d2: invokespecial net/rim/device/apps/internal/browser/stack/CachedFileConnection.<init> (Ljavax/microedition/io/file/FileConnection;Ljava/io/InputStream;Lnet/rim/device/apps/internal/browser/stack/CacheResult;)V
      // 0d5: astore 13
      // 0d7: iload 7
      // 0d9: ifne 0eb
      // 0dc: aload 6
      // 0de: ifnull 0eb
      // 0e1: aload 6
      // 0e3: invokevirtual java/io/InputStream.close ()V
      // 0e6: goto 0eb
      // 0e9: astore 14
      // 0eb: iload 7
      // 0ed: ifne 101
      // 0f0: aload 5
      // 0f2: ifnull 101
      // 0f5: aload 5
      // 0f7: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0fc: goto 101
      // 0ff: astore 14
      // 101: aload 13
      // 103: areturn
      // 104: astore 8
      // 106: aload 4
      // 108: sipush 400
      // 10b: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setStatus (I)V
      // 10e: aload 8
      // 110: athrow
      // 111: astore 8
      // 113: aload 4
      // 115: sipush 400
      // 118: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setStatus (I)V
      // 11b: new net/rim/device/cldc/io/utility/MalformedURLException
      // 11e: dup
      // 11f: invokespecial net/rim/device/cldc/io/utility/MalformedURLException.<init> ()V
      // 122: athrow
      // 123: astore 15
      // 125: iload 7
      // 127: ifne 139
      // 12a: aload 6
      // 12c: ifnull 139
      // 12f: aload 6
      // 131: invokevirtual java/io/InputStream.close ()V
      // 134: goto 139
      // 137: astore 16
      // 139: iload 7
      // 13b: ifne 14f
      // 13e: aload 5
      // 140: ifnull 14f
      // 143: aload 5
      // 145: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 14a: goto 14f
      // 14d: astore 16
      // 14f: aload 15
      // 151: athrow
      // try (101 -> 103): 104 null
      // try (109 -> 111): 112 null
      // try (12 -> 97): 115 null
      // try (12 -> 97): 121 null
      // try (12 -> 97): 129 null
      // try (134 -> 136): 137 null
      // try (142 -> 144): 145 null
      // try (115 -> 130): 129 null
   }

   @Override
   public final SessionStats getSessionStats() {
      return null;
   }
}
