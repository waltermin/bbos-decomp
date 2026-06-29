package net.rim.device.apps.internal.browser.plugin.media.field;

import net.rim.plazmic.internal.mediaengine.ResourceContext;
import net.rim.plazmic.mediaengine.MediaManager;

public final class MediaBrowserField$MyMediaManager extends MediaManager {
   private final MediaBrowserField this$0;

   public MediaBrowserField$MyMediaManager(MediaBrowserField _1) {
      this.this$0 = _1;
   }

   @Override
   public final Object createResource(String param1, Object param2, ResourceContext param3, Object param4) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ifnull 4f
      // 04: aload 1
      // 05: ldc_w "x-object:"
      // 08: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 0b: ifeq 4f
      // 0e: aload 1
      // 0f: aload 1
      // 10: bipush 47
      // 12: invokevirtual java/lang/String.indexOf (I)I
      // 15: bipush 1
      // 16: iadd
      // 17: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 1a: astore 5
      // 1c: aload 5
      // 1e: ldc_w "CustomFocusOrder"
      // 21: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 24: ifeq 4f
      // 27: aload 0
      // 28: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$MyMediaManager.this$0 Lnet/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField;
      // 2b: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._focusManager Ljava/lang/Object;
      // 2e: ifnull 4f
      // 31: new java/io/ByteArrayInputStream
      // 34: dup
      // 35: aload 2
      // 36: checkcast java/lang/String
      // 39: invokevirtual java/lang/String.getBytes ()[B
      // 3c: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 3f: astore 6
      // 41: aload 0
      // 42: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$MyMediaManager.this$0 Lnet/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField;
      // 45: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._focusManager Ljava/lang/Object;
      // 48: aload 6
      // 4a: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/CustomFocusOrder.parseXml (Ljava/io/InputStream;)V
      // 4d: aconst_null
      // 4e: areturn
      // 4f: aload 2
      // 50: dup
      // 51: instanceof java/lang/String
      // 54: ifne 5b
      // 57: pop
      // 58: goto be
      // 5b: checkcast java/lang/String
      // 5e: astore 5
      // 60: new java/lang/StringBuffer
      // 63: dup
      // 64: ldc_w "<?xml version=\"1.0\"?><body>"
      // 67: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 6a: aload 5
      // 6c: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 6f: ldc_w "</body>"
      // 72: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 75: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 78: astore 5
      // 7a: new net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$MyXMLHandler
      // 7d: dup
      // 7e: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$MyXMLHandler.<init> ()V
      // 81: astore 6
      // 83: new java/io/ByteArrayInputStream
      // 86: dup
      // 87: aload 5
      // 89: ldc_w "UTF8"
      // 8c: invokevirtual java/lang/String.getBytes (Ljava/lang/String;)[B
      // 8f: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 92: astore 7
      // 94: new net/rim/device/api/xml/jaxp/SAXParserImpl
      // 97: dup
      // 98: invokespecial net/rim/device/api/xml/jaxp/SAXParserImpl.<init> ()V
      // 9b: new org/xml/sax/InputSource
      // 9e: dup
      // 9f: aload 7
      // a1: invokespecial org/xml/sax/InputSource.<init> (Ljava/io/InputStream;)V
      // a4: aload 6
      // a6: invokevirtual net/rim/device/api/xml/jaxp/SAXParserImpl.parse (Lorg/xml/sax/InputSource;Lorg/xml/sax/helpers/DefaultHandler;)V
      // a9: aload 0
      // aa: aload 6
      // ac: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$MyXMLHandler.getParsedString ()Ljava/lang/String;
      // af: aload 1
      // b0: aload 3
      // b1: aload 4
      // b3: invokevirtual net/rim/plazmic/mediaengine/MediaManager.createResourceFromURI (Ljava/lang/String;Ljava/lang/String;Lnet/rim/plazmic/internal/mediaengine/ResourceContext;Ljava/lang/Object;)Ljava/lang/Object;
      // b6: areturn
      // b7: astore 7
      // b9: goto be
      // bc: astore 7
      // be: aload 0
      // bf: aload 1
      // c0: aload 2
      // c1: aload 3
      // c2: aload 4
      // c4: invokespecial net/rim/plazmic/mediaengine/MediaManager.createResource (Ljava/lang/String;Ljava/lang/Object;Lnet/rim/plazmic/internal/mediaengine/ResourceContext;Ljava/lang/Object;)Ljava/lang/Object;
      // c7: areturn
      // try (58 -> 81): 82 null
      // try (58 -> 81): 84 null
   }
}
