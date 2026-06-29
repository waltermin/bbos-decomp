package net.rim.device.apps.internal.browser.stack;

import javax.microedition.io.InputConnection;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.cldc.io.utility.SessionStats;

public final class FileStackAdapter implements NetworkPageFetcher {
   private FileStackAdapter() {
   }

   public static final void registerOnStartup() {
      StackManager stackManager = StackManager.getInstance();
      stackManager.registerNetworkPageFetcher(new FileStackAdapter(), new String[]{"file"});
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
      // 00: aload 1
      // 01: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.getModelResult ()Lnet/rim/device/apps/internal/browser/stack/ModelResult;
      // 04: astore 3
      // 05: aload 3
      // 06: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getCacheResult ()Lnet/rim/device/apps/internal/browser/stack/CacheResult;
      // 09: astore 4
      // 0b: aconst_null
      // 0c: astore 5
      // 0e: bipush 0
      // 0f: istore 6
      // 11: aload 4
      // 13: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.getURLWithoutFragment ()Ljava/lang/String;
      // 16: astore 7
      // 18: aload 7
      // 1a: bipush 3
      // 1c: bipush 1
      // 1d: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;IZ)Ljavax/microedition/io/Connection;
      // 20: checkcast java/lang/Object
      // 23: astore 5
      // 25: aload 5
      // 27: ifnonnull 32
      // 2a: new java/lang/Object
      // 2d: dup
      // 2e: invokespecial java/io/IOException.<init> ()V
      // 31: athrow
      // 32: new java/lang/Object
      // 35: dup
      // 36: invokespecial net/rim/device/api/io/http/HttpHeaders.<init> ()V
      // 39: astore 8
      // 3b: aload 4
      // 3d: aload 8
      // 3f: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setResponseHeaders (Lnet/rim/device/api/io/http/HttpHeaders;)V
      // 42: aload 4
      // 44: ldc_w "OK"
      // 47: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setResponseMessage (Ljava/lang/String;)V
      // 4a: aload 4
      // 4c: sipush 200
      // 4f: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setStatus (I)V
      // 52: aload 4
      // 54: bipush 1
      // 55: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setLocalContent (Z)V
      // 58: bipush 1
      // 59: istore 6
      // 5b: aload 5
      // 5d: astore 9
      // 5f: iload 6
      // 61: ifne 75
      // 64: aload 5
      // 66: ifnull 75
      // 69: aload 5
      // 6b: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 70: goto 75
      // 73: astore 10
      // 75: aload 9
      // 77: areturn
      // 78: astore 7
      // 7a: aload 4
      // 7c: sipush 400
      // 7f: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setStatus (I)V
      // 82: aload 7
      // 84: athrow
      // 85: astore 7
      // 87: aload 4
      // 89: sipush 400
      // 8c: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.setStatus (I)V
      // 8f: new java/lang/Object
      // 92: dup
      // 93: invokespecial net/rim/device/cldc/io/utility/MalformedURLException.<init> ()V
      // 96: athrow
      // 97: astore 11
      // 99: iload 6
      // 9b: ifne af
      // 9e: aload 5
      // a0: ifnull af
      // a3: aload 5
      // a5: invokeinterface javax/microedition/io/Connection.close ()V 1
      // aa: goto af
      // ad: astore 12
      // af: aload 11
      // b1: athrow
      // try (49 -> 51): 52 null
      // try (10 -> 45): 55 null
      // try (10 -> 45): 61 null
      // try (10 -> 45): 69 null
      // try (74 -> 76): 77 null
      // try (55 -> 70): 69 null
   }

   @Override
   public final SessionStats getSessionStats() {
      return null;
   }
}
