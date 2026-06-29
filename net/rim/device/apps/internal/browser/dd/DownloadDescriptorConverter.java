package net.rim.device.apps.internal.browser.dd;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RenderingException;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;

public final class DownloadDescriptorConverter extends BrowserContentProvider {
   private static final String[] ACCEPT = new String[]{"application/vnd.oma.dd+xml"};

   @Override
   public final String[] getAccept(RenderingOptions context) {
      return ACCEPT;
   }

   @Override
   public final String[] getSupportedMimeTypes() {
      return ACCEPT;
   }

   @Override
   public final BrowserContent getBrowserContent(BrowserContentProviderContext param1) throws RenderingException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getEvent ()Lnet/rim/device/api/browser/field/Event;
      // 04: astore 2
      // 05: aload 1
      // 06: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getFlags ()I
      // 09: istore 3
      // 0a: iload 3
      // 0b: bipush 32
      // 0d: iand
      // 0e: ifne 1f
      // 11: new net/rim/device/api/browser/field/RenderingException
      // 14: dup
      // 15: sipush 789
      // 18: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 1b: invokespecial net/rim/device/api/browser/field/RenderingException.<init> (Ljava/lang/String;)V
      // 1e: athrow
      // 1f: new net/rim/device/apps/internal/browser/dd/DownloadDescriptorRenderer
      // 22: dup
      // 23: aload 1
      // 24: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getHttpConnection ()Ljavax/microedition/io/HttpConnection;
      // 27: aload 1
      // 28: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getRenderingSession ()Lnet/rim/device/api/browser/field/RenderingSession;
      // 2b: aload 1
      // 2c: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getRenderingApplication ()Lnet/rim/device/api/browser/field/RenderingApplication;
      // 2f: aload 2
      // 30: ifnull 3a
      // 33: aload 2
      // 34: invokevirtual net/rim/device/api/browser/field/Event.getSourceURL ()Ljava/lang/String;
      // 37: goto 3b
      // 3a: aconst_null
      // 3b: iload 3
      // 3c: invokespecial net/rim/device/apps/internal/browser/dd/DownloadDescriptorRenderer.<init> (Ljavax/microedition/io/HttpConnection;Lnet/rim/device/api/browser/field/RenderingSession;Lnet/rim/device/api/browser/field/RenderingApplication;Ljava/lang/String;I)V
      // 3f: invokevirtual net/rim/device/apps/internal/browser/dd/DownloadDescriptorRenderer.processData ()Lnet/rim/device/api/browser/field/BrowserContent;
      // 42: areturn
      // 43: astore 3
      // 44: new net/rim/device/api/browser/field/RenderingException
      // 47: dup
      // 48: sipush 614
      // 4b: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 4e: invokespecial net/rim/device/api/browser/field/RenderingException.<init> (Ljava/lang/String;)V
      // 51: athrow
      // 52: astore 3
      // 53: new net/rim/device/api/browser/field/RenderingException
      // 56: dup
      // 57: sipush 235
      // 5a: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 5d: invokespecial net/rim/device/api/browser/field/RenderingException.<init> (Ljava/lang/String;)V
      // 60: athrow
      // try (3 -> 33): 34 null
      // try (3 -> 33): 41 null
   }
}
