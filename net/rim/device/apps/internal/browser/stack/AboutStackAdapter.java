package net.rim.device.apps.internal.browser.stack;

import javax.microedition.io.InputConnection;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.cldc.io.utility.SessionStats;

public final class AboutStackAdapter implements NetworkPageFetcher {
   private AboutStackAdapter() {
   }

   public static final void registerOnStartup() {
      StackManager stackManager = StackManager.getInstance();
      stackManager.registerNetworkPageFetcher(new AboutStackAdapter(), new String[]{"about"});
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
      // 00e: bipush 0
      // 00f: istore 6
      // 011: aload 4
      // 013: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.getURLWithoutFragment ()Ljava/lang/String;
      // 016: astore 7
      // 018: new java/lang/Object
      // 01b: dup
      // 01c: invokespecial net/rim/device/api/io/http/HttpHeaders.<init> ()V
      // 01f: astore 8
      // 021: aload 7
      // 023: ldc_w "about:cache"
      // 026: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 029: ifne 02f
      // 02c: goto 0d5
      // 02f: aload 8
      // 031: ldc_w "content-type"
      // 034: ldc_w "text/html"
      // 037: invokevirtual net/rim/device/api/io/http/HttpHeaders.addProperty (Ljava/lang/String;Ljava/lang/String;)V
      // 03a: invokestatic net/rim/device/apps/internal/browser/core/BrowserDaemonRegistry.getInstance ()Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 03d: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getRawDataCache ()Lnet/rim/device/apps/internal/browser/stack/RawDataCache;
      // 040: astore 9
      // 042: new java/lang/Object
      // 045: dup
      // 046: ldc_w "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
      // 049: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 04c: astore 10
      // 04e: aload 10
      // 050: ldc_w "<head><title>Cache Info</title></head><body><div><h2>Short term cache</h2><br/><table>"
      // 053: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 056: pop
      // 057: aload 10
      // 059: ldc_w "<tr><td><b>Number of entries:</b></td><td>"
      // 05c: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 05f: pop
      // 060: aload 10
      // 062: aload 9
      // 064: invokevirtual net/rim/device/apps/internal/browser/stack/RawDataCache.getShortTermCacheCount ()I
      // 067: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 06a: pop
      // 06b: aload 10
      // 06d: ldc_w "</td></tr><tr><td><b>Storage used:</b></td><td>"
      // 070: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 073: pop
      // 074: aload 10
      // 076: aload 9
      // 078: bipush 1
      // 079: invokevirtual net/rim/device/apps/internal/browser/stack/RawDataCache.getShortTermCacheSize (Z)I
      // 07c: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 07f: pop
      // 080: aload 10
      // 082: ldc_w "</tr></table><br/><hr><div><h2>Long term cache</h2><br/><table>"
      // 085: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 088: pop
      // 089: aload 10
      // 08b: ldc_w "<tr><td><b>Number of entries:</b></td><td>"
      // 08e: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 091: pop
      // 092: aload 10
      // 094: aload 9
      // 096: invokevirtual net/rim/device/apps/internal/browser/stack/RawDataCache.getLongTermCacheCount ()I
      // 099: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 09c: pop
      // 09d: aload 10
      // 09f: ldc_w "</td></tr><tr><td><b>Storage used:</b></td><td>"
      // 0a2: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0a5: pop
      // 0a6: aload 10
      // 0a8: aload 9
      // 0aa: invokevirtual net/rim/device/apps/internal/browser/stack/RawDataCache.getLongTermCacheSize ()I
      // 0ad: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 0b0: pop
      // 0b1: aload 10
      // 0b3: ldc_w "</tr></table></body></html>"
      // 0b6: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0b9: pop
      // 0ba: new java/lang/Object
      // 0bd: dup
      // 0be: aload 7
      // 0c0: aload 10
      // 0c2: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0c5: ldc_w "UTF-8"
      // 0c8: invokevirtual java/lang/String.getBytes (Ljava/lang/String;)[B
      // 0cb: aload 8
      // 0cd: invokespecial net/rim/device/api/browser/util/StaticHttpConnection.<init> (Ljava/lang/String;[BLnet/rim/device/api/io/http/HttpHeaders;)V
      // 0d0: astore 5
      // 0d2: goto 0dd
      // 0d5: new java/lang/Object
      // 0d8: dup
      // 0d9: invokespecial java/io/IOException.<init> ()V
      // 0dc: athrow
      // 0dd: aload 4
      // 0df: aload 8
      // 0e1: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setResponseHeaders (Lnet/rim/device/api/io/http/HttpHeaders;)V
      // 0e4: aload 4
      // 0e6: ldc_w "OK"
      // 0e9: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setResponseMessage (Ljava/lang/String;)V
      // 0ec: aload 4
      // 0ee: sipush 200
      // 0f1: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setStatus (I)V
      // 0f4: aload 4
      // 0f6: bipush 1
      // 0f7: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setLocalContent (Z)V
      // 0fa: bipush 1
      // 0fb: istore 6
      // 0fd: aload 5
      // 0ff: astore 9
      // 101: iload 6
      // 103: ifne 115
      // 106: aload 5
      // 108: ifnull 115
      // 10b: aload 5
      // 10d: invokevirtual net/rim/device/api/browser/util/StaticHttpConnection.close ()V
      // 110: goto 115
      // 113: astore 10
      // 115: aload 9
      // 117: areturn
      // 118: astore 7
      // 11a: aload 4
      // 11c: sipush 400
      // 11f: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setStatus (I)V
      // 122: aload 7
      // 124: athrow
      // 125: astore 7
      // 127: aload 4
      // 129: sipush 400
      // 12c: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setStatus (I)V
      // 12f: new java/lang/Object
      // 132: dup
      // 133: invokespecial net/rim/device/cldc/io/utility/MalformedURLException.<init> ()V
      // 136: athrow
      // 137: astore 11
      // 139: iload 6
      // 13b: ifne 14d
      // 13e: aload 5
      // 140: ifnull 14d
      // 143: aload 5
      // 145: invokevirtual net/rim/device/api/browser/util/StaticHttpConnection.close ()V
      // 148: goto 14d
      // 14b: astore 12
      // 14d: aload 11
      // 14f: athrow
      // try (118 -> 120): 121 null
      // try (10 -> 114): 124 null
      // try (10 -> 114): 130 null
      // try (10 -> 114): 138 null
      // try (143 -> 145): 146 null
      // try (124 -> 139): 138 null
   }

   @Override
   public final SessionStats getSessionStats() {
      return null;
   }
}
