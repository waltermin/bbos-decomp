package net.rim.device.apps.internal.browser.core;

final class Loader {
   public static final void libMain(String[] param0) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: invokestatic net/rim/device/api/system/ApplicationRegistry.getApplicationRegistry ()Lnet/rim/device/api/system/ApplicationRegistry;
      // 03: astore 1
      // 04: invokestatic net/rim/device/apps/internal/browser/core/BrowserContentProviderRegistryImpl.registerOnStartup ()V
      // 07: invokestatic net/rim/device/api/browser/plugin/BrowserContentProviderRegistry.getInstance ()Lnet/rim/device/api/browser/plugin/BrowserContentProviderRegistry;
      // 0a: astore 2
      // 0b: aload 2
      // 0c: new java/lang/Object
      // 0f: dup
      // 10: invokespecial net/rim/device/apps/internal/browser/html/HTMLRenderingConverter.<init> ()V
      // 13: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderRegistry.register (Lnet/rim/device/api/browser/plugin/BrowserContentProvider;)V
      // 16: aload 2
      // 17: new java/lang/Object
      // 1a: dup
      // 1b: invokespecial net/rim/device/apps/internal/browser/wml/WMLRenderingConverter.<init> ()V
      // 1e: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderRegistry.register (Lnet/rim/device/api/browser/plugin/BrowserContentProvider;)V
      // 21: ldc_w "net.rim.device.apps.internal.browser.wml.WMLScriptRenderingConverter"
      // 24: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 27: astore 3
      // 28: aload 3
      // 29: ifnull 43
      // 2c: aload 2
      // 2d: aload 3
      // 2e: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 31: checkcast java/lang/Object
      // 34: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderRegistry.register (Lnet/rim/device/api/browser/plugin/BrowserContentProvider;)V
      // 37: goto 43
      // 3a: astore 3
      // 3b: goto 43
      // 3e: astore 3
      // 3f: goto 43
      // 42: astore 3
      // 43: aload 2
      // 44: new java/lang/Object
      // 47: dup
      // 48: invokespecial net/rim/device/apps/internal/browser/img/ImageRenderingConverter.<init> ()V
      // 4b: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderRegistry.register (Lnet/rim/device/api/browser/plugin/BrowserContentProvider;)V
      // 4e: invokestatic net/rim/device/apps/internal/browser/multipart/MultipartConverterDescriptor.registerOnStartup ()V
      // 51: invokestatic net/rim/device/apps/internal/browser/stack/HTTPStackAdapter.registerOnStartup ()V
      // 54: invokestatic net/rim/device/apps/internal/browser/stack/LocalStackAdapter.registerOnStartup ()V
      // 57: invokestatic net/rim/device/apps/internal/browser/stack/FileStackAdapter.registerOnStartup ()V
      // 5a: invokestatic net/rim/device/apps/internal/browser/stack/SMBStackAdapter.registerOnStartup ()V
      // 5d: invokestatic net/rim/device/apps/internal/browser/stack/RTSPStackAdapter.registerOnStartup ()V
      // 60: invokestatic net/rim/device/apps/internal/browser/stack/AboutStackAdapter.registerOnStartup ()V
      // 63: aload 2
      // 64: new net/rim/device/apps/internal/browser/cod/JADConverterDescriptor
      // 67: dup
      // 68: invokespecial net/rim/device/apps/internal/browser/cod/JADConverterDescriptor.<init> ()V
      // 6b: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderRegistry.register (Lnet/rim/device/api/browser/plugin/BrowserContentProvider;)V
      // 6e: invokestatic net/rim/device/apps/internal/browser/push/RIMPushlet.getInstance ()Lnet/rim/device/apps/internal/browser/push/RIMPushlet;
      // 71: pop
      // 72: invokestatic net/rim/device/apps/internal/browser/core/RenderingSessionFactory.registerOnStartup ()V
      // 75: aload 1
      // 76: ldc2_w 4307171400805038204
      // 79: invokevirtual net/rim/device/api/system/ApplicationRegistry.waitFor (J)Ljava/lang/Object;
      // 7c: checkcast net/rim/device/apps/internal/browser/core/BrowserDaemonRegistry
      // 7f: astore 3
      // 80: aload 3
      // 81: dup
      // 82: astore 4
      // 84: monitorenter
      // 85: aload 3
      // 86: invokevirtual net/rim/device/apps/internal/browser/core/BrowserDaemonRegistry.cardReady ()V
      // 89: aload 3
      // 8a: invokevirtual java/lang/Object.notifyAll ()V
      // 8d: aload 4
      // 8f: monitorexit
      // 90: goto 9b
      // 93: astore 5
      // 95: aload 4
      // 97: monitorexit
      // 98: aload 5
      // 9a: athrow
      // 9b: invokestatic net/rim/device/apps/internal/browser/content/ContentHandlerRenderingManagerImpl.registerOnStartup ()V
      // 9e: return
      // try (15 -> 25): 26 null
      // try (15 -> 25): 28 null
      // try (15 -> 25): 30 null
      // try (60 -> 66): 67 null
      // try (67 -> 70): 67 null
   }
}
