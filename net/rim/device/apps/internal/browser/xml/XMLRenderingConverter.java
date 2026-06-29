package net.rim.device.apps.internal.browser.xml;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RenderingException;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;

public final class XMLRenderingConverter extends BrowserContentProvider {
   private static final String[] ACCEPT = new String[]{"text/xml", "application/xml", "application/vnd.wap.wbxml"};

   @Override
   public final String[] getSupportedMimeTypes() {
      return ACCEPT;
   }

   @Override
   public final String[] getAccept(RenderingOptions renderingOptions) {
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
      // 01: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getHttpConnection ()Ljavax/microedition/io/HttpConnection;
      // 04: astore 2
      // 05: aload 2
      // 06: dup
      // 07: instanceof net/rim/device/apps/internal/browser/stack/CachedHttpConnection
      // 0a: ifne 11
      // 0d: pop
      // 0e: goto 95
      // 11: checkcast net/rim/device/apps/internal/browser/stack/CachedHttpConnection
      // 14: astore 3
      // 15: aload 3
      // 16: invokevirtual net/rim/device/apps/internal/browser/stack/CachedHttpConnection.openMarkableInputStream ()Ljava/io/InputStream;
      // 19: astore 4
      // 1b: aload 4
      // 1d: ifnonnull 28
      // 20: new net/rim/device/api/browser/field/RenderingException
      // 23: dup
      // 24: invokespecial net/rim/device/api/browser/field/RenderingException.<init> ()V
      // 27: athrow
      // 28: aload 4
      // 2a: sipush 1024
      // 2d: invokevirtual java/io/InputStream.mark (I)V
      // 30: new net/rim/device/apps/internal/browser/xml/XMLRenderingConverter$HelperHandler
      // 33: dup
      // 34: aconst_null
      // 35: invokespecial net/rim/device/apps/internal/browser/xml/XMLRenderingConverter$HelperHandler.<init> (Lnet/rim/device/apps/internal/browser/xml/XMLRenderingConverter$1;)V
      // 38: astore 5
      // 3a: invokestatic net/rim/device/api/xml/parsers/SAXParserFactory.newInstance ()Lnet/rim/device/api/xml/parsers/SAXParserFactory;
      // 3d: astore 6
      // 3f: aload 6
      // 41: invokevirtual net/rim/device/api/xml/parsers/SAXParserFactory.newSAXParser ()Lnet/rim/device/api/xml/parsers/SAXParser;
      // 44: astore 7
      // 46: aload 7
      // 48: bipush 1
      // 49: invokevirtual net/rim/device/api/xml/parsers/SAXParser.setAllowUndefinedNamespaces (Z)V
      // 4c: aload 7
      // 4e: aload 4
      // 50: aload 5
      // 52: invokevirtual net/rim/device/api/xml/parsers/SAXParser.parse (Ljava/io/InputStream;Lorg/xml/sax/helpers/DefaultHandler;)V
      // 55: goto 5f
      // 58: astore 6
      // 5a: goto 5f
      // 5d: astore 6
      // 5f: aload 5
      // 61: getfield net/rim/device/apps/internal/browser/xml/XMLRenderingConverter$HelperHandler._contentType Ljava/lang/String;
      // 64: ifnull 95
      // 67: aload 4
      // 69: invokevirtual java/io/InputStream.reset ()V
      // 6c: aload 3
      // 6d: invokevirtual net/rim/device/apps/internal/browser/stack/CachedHttpConnection.getCacheResult ()Lnet/rim/device/apps/internal/browser/stack/CacheResult;
      // 70: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.getResponseHeaders ()Lnet/rim/device/api/io/http/HttpHeaders;
      // 73: ldc_w "content-type"
      // 76: aload 5
      // 78: getfield net/rim/device/apps/internal/browser/xml/XMLRenderingConverter$HelperHandler._contentType Ljava/lang/String;
      // 7b: invokevirtual net/rim/device/api/io/http/HttpHeaders.setProperty (Ljava/lang/String;Ljava/lang/String;)V
      // 7e: aload 1
      // 7f: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getRenderingSession ()Lnet/rim/device/api/browser/field/RenderingSession;
      // 82: aload 2
      // 83: aconst_null
      // 84: aconst_null
      // 85: aload 1
      // 86: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getRenderingApplication ()Lnet/rim/device/api/browser/field/RenderingApplication;
      // 89: aload 1
      // 8a: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getFlags ()I
      // 8d: aconst_null
      // 8e: aconst_null
      // 8f: aconst_null
      // 90: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.renderBrowserContent (Lnet/rim/device/api/browser/field/RenderingSession;Ljavax/microedition/io/InputConnection;Ljava/io/InputStream;Ljava/lang/String;Lnet/rim/device/api/browser/field/RenderingApplication;ILnet/rim/device/api/browser/field/Event;Ljava/lang/Object;Lnet/rim/device/apps/internal/browser/util/Frame;)Lnet/rim/device/api/browser/field/BrowserContent;
      // 93: areturn
      // 94: astore 3
      // 95: new net/rim/device/api/browser/field/RenderingException
      // 98: dup
      // 99: invokespecial net/rim/device/api/browser/field/RenderingException.<init> ()V
      // 9c: athrow
      // try (28 -> 40): 41 null
      // try (28 -> 40): 43 null
      // try (3 -> 69): 70 null
   }
}
