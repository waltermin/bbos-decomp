package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;
import net.rim.device.api.io.http.HttpProtocolConstants;

public final class HTMLRenderingConverter extends BrowserContentProvider implements HttpProtocolConstants {
   public static final String CONTENT_TYPE_APPLICATION_VND_RIM_HTML = "application/vnd.rim.html";
   private static final String TEXT_PLAIN_WITH_Q = "text/plain;q=0.3";
   private static final String[] ACCEPT_WML_ONLY = new String[]{"text/plain;q=0.3"};
   private static final String[] ACCEPT_HTML = new String[]{
      "application/vnd.rim.html", "text/html", "text/plain;q=0.3", "application/xhtml+xml", "application/vnd.wap.xhtml+xml"
   };
   private static final String[] ACCEPT_MIN = new String[]{"text/plain"};

   @Override
   public final String[] getSupportedMimeTypes() {
      return ACCEPT_HTML;
   }

   @Override
   public final String[] getAccept(RenderingOptions renderingOptions) {
      if (renderingOptions == null) {
         return ACCEPT_MIN;
      } else if (renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 22, true)) {
         return ACCEPT_HTML;
      } else {
         return renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 21, true) ? ACCEPT_WML_ONLY : ACCEPT_MIN;
      }
   }

   @Override
   public final BrowserContent getBrowserContent(BrowserContentProviderContext param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getRenderingApplication ()Lnet/rim/device/api/browser/field/RenderingApplication;
      // 004: astore 2
      // 005: aload 1
      // 006: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getRenderingSession ()Lnet/rim/device/api/browser/field/RenderingSession;
      // 009: astore 3
      // 00a: aload 1
      // 00b: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getInputConnection ()Ljavax/microedition/io/InputConnection;
      // 00e: astore 4
      // 010: aload 1
      // 011: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getInputStream ()Ljava/io/InputStream;
      // 014: astore 5
      // 016: aload 1
      // 017: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getFlags ()I
      // 01a: istore 6
      // 01c: aload 1
      // 01d: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getEvent ()Lnet/rim/device/api/browser/field/Event;
      // 020: astore 7
      // 022: aload 7
      // 024: ifnull 02f
      // 027: aload 7
      // 029: invokevirtual net/rim/device/api/browser/field/Event.getSourceURL ()Ljava/lang/String;
      // 02c: goto 030
      // 02f: aconst_null
      // 030: astore 8
      // 032: aconst_null
      // 033: astore 9
      // 035: aconst_null
      // 036: astore 10
      // 038: aload 1
      // 039: dup
      // 03a: instanceof net/rim/device/apps/internal/browser/core/BrowserContentProviderRenderingContext
      // 03d: ifne 044
      // 040: pop
      // 041: goto 06d
      // 044: checkcast net/rim/device/apps/internal/browser/core/BrowserContentProviderRenderingContext
      // 047: astore 11
      // 049: aload 11
      // 04b: invokevirtual net/rim/device/apps/internal/browser/core/BrowserContentProviderRenderingContext.getContext ()Ljava/lang/Object;
      // 04e: astore 12
      // 050: aload 12
      // 052: dup
      // 053: instanceof net/rim/device/apps/internal/browser/html/HTMLContext
      // 056: ifne 05d
      // 059: pop
      // 05a: goto 063
      // 05d: checkcast net/rim/device/apps/internal/browser/html/HTMLContext
      // 060: goto 064
      // 063: aconst_null
      // 064: astore 9
      // 066: aload 11
      // 068: invokevirtual net/rim/device/apps/internal/browser/core/BrowserContentProviderRenderingContext.getTarget ()Lnet/rim/device/apps/internal/browser/util/Frame;
      // 06b: astore 10
      // 06d: aload 4
      // 06f: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getUrl (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 072: astore 11
      // 074: aload 4
      // 076: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getContentType (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 079: astore 12
      // 07b: aload 12
      // 07d: ifnull 088
      // 080: aload 12
      // 082: invokevirtual java/lang/String.length ()I
      // 085: ifne 08d
      // 088: ldc_w "text/html"
      // 08b: astore 12
      // 08d: aload 4
      // 08f: ifnull 096
      // 092: aload 3
      // 093: ifnonnull 0a1
      // 096: new java/lang/Object
      // 099: dup
      // 09a: ldc_w "InputConnection or RenderingOptions is not set"
      // 09d: invokespecial net/rim/device/api/browser/field/RenderingException.<init> (Ljava/lang/String;)V
      // 0a0: athrow
      // 0a1: aload 3
      // 0a2: invokevirtual net/rim/device/api/browser/field/RenderingSession.getRenderingOptions ()Lnet/rim/device/api/browser/field/RenderingOptions;
      // 0a5: astore 13
      // 0a7: aconst_null
      // 0a8: astore 14
      // 0aa: aload 4
      // 0ac: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getContentLength (Ljavax/microedition/io/InputConnection;)J
      // 0af: ldc_w 5242880
      // 0b2: i2l
      // 0b3: lcmp
      // 0b4: iflt 0bf
      // 0b7: new java/lang/Object
      // 0ba: dup
      // 0bb: invokespecial net/rim/device/api/browser/field/RenderingException.<init> ()V
      // 0be: athrow
      // 0bf: aload 5
      // 0c1: ifnonnull 0cd
      // 0c4: aload 4
      // 0c6: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 0cb: astore 5
      // 0cd: aload 12
      // 0cf: ldc_w "application/vnd.rim.html"
      // 0d2: ldc_w 1701707776
      // 0d5: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 0d8: ifeq 0de
      // 0db: goto 227
      // 0de: aconst_null
      // 0df: astore 15
      // 0e1: bipush 0
      // 0e2: istore 16
      // 0e4: aload 2
      // 0e5: ifnull 0fd
      // 0e8: new net/rim/device/apps/internal/browser/api/DeviceDataConversionRequestEvent
      // 0eb: dup
      // 0ec: aload 4
      // 0ee: invokespecial net/rim/device/apps/internal/browser/api/DeviceDataConversionRequestEvent.<init> (Ljava/lang/Object;)V
      // 0f1: astore 17
      // 0f3: aload 2
      // 0f4: aload 17
      // 0f6: invokeinterface net/rim/device/api/browser/field/RenderingApplication.eventOccurred (Lnet/rim/device/api/browser/field/Event;)Ljava/lang/Object; 2
      // 0fb: astore 15
      // 0fd: aload 15
      // 0ff: instanceof [Ljava/lang/Object;
      // 102: ifeq 12e
      // 105: aload 15
      // 107: checkcast [Ljava/lang/Object;
      // 10a: checkcast [Ljava/lang/Object;
      // 10d: astore 17
      // 10f: aload 17
      // 111: arraylength
      // 112: bipush 2
      // 114: if_icmpne 12e
      // 117: aload 17
      // 119: bipush 1
      // 11a: aaload
      // 11b: dup
      // 11c: instanceof java/lang/Object
      // 11f: ifne 126
      // 122: pop
      // 123: goto 12e
      // 126: checkcast java/lang/Object
      // 129: astore 5
      // 12b: bipush 1
      // 12c: istore 16
      // 12e: iload 16
      // 130: ifeq 136
      // 133: goto 227
      // 136: aload 13
      // 138: ldc2_w 4550690918222697397
      // 13b: bipush 8
      // 13d: ldc_w "us-ascii"
      // 140: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithStringValue (JILjava/lang/String;)Ljava/lang/String;
      // 143: astore 17
      // 145: aload 4
      // 147: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getCharacterEncoding (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 14a: astore 18
      // 14c: aload 18
      // 14e: ifnonnull 16b
      // 151: aload 4
      // 153: dup
      // 154: instanceof java/lang/Object
      // 157: ifne 15e
      // 15a: pop
      // 15b: goto 16b
      // 15e: checkcast java/lang/Object
      // 161: ldc_w "Accept-Charset"
      // 164: invokeinterface javax/microedition/io/HttpConnection.getHeaderField (Ljava/lang/String;)Ljava/lang/String; 2
      // 169: astore 18
      // 16b: aload 13
      // 16d: ldc2_w 4550690918222697397
      // 170: bipush 1
      // 171: bipush 0
      // 172: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithBooleanValue (JIZ)Z
      // 175: ifeq 17c
      // 178: aload 17
      // 17a: astore 18
      // 17c: bipush 2
      // 17e: newarray 8
      // 180: astore 19
      // 182: aload 19
      // 184: bipush 0
      // 185: aload 13
      // 187: ldc2_w 4550690918222697397
      // 18a: bipush 2
      // 18c: bipush 0
      // 18d: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithBooleanValue (JIZ)Z
      // 190: ifeq 197
      // 193: bipush 1
      // 194: goto 198
      // 197: bipush 0
      // 198: bastore
      // 199: aload 19
      // 19b: bipush 1
      // 19c: aload 13
      // 19e: ldc2_w 4550690918222697397
      // 1a1: bipush 18
      // 1a3: bipush 1
      // 1a4: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithBooleanValue (JIZ)Z
      // 1a7: ifeq 1ae
      // 1aa: bipush 1
      // 1ab: goto 1af
      // 1ae: bipush 0
      // 1af: bastore
      // 1b0: aload 5
      // 1b2: invokevirtual java/io/InputStream.markSupported ()Z
      // 1b5: ifne 1c3
      // 1b8: new net/rim/device/apps/internal/browser/stack/MarkableInputStream
      // 1bb: dup
      // 1bc: aload 5
      // 1be: invokespecial net/rim/device/apps/internal/browser/stack/MarkableInputStream.<init> (Ljava/io/InputStream;)V
      // 1c1: astore 5
      // 1c3: aload 12
      // 1c5: aload 18
      // 1c7: aload 17
      // 1c9: aload 5
      // 1cb: getstatic net/rim/device/apps/internal/browser/markup/HTMLUtilities.STATE_TABLE [I
      // 1ce: aload 19
      // 1d0: getstatic net/rim/device/apps/internal/browser/markup/HTMLUtilities._T_NAMES [Ljava/lang/String;
      // 1d3: getstatic net/rim/device/apps/internal/browser/markup/HTMLUtilities._T_TOKENS [I
      // 1d6: getstatic net/rim/device/apps/internal/browser/markup/HTMLUtilities._T_INDICIES [I
      // 1d9: getstatic net/rim/device/apps/internal/browser/markup/MarkupBinaryConstants._E_NAMES [Ljava/lang/String;
      // 1dc: getstatic net/rim/device/apps/internal/browser/markup/MarkupBinaryConstants._E_TOKENS [I
      // 1df: getstatic net/rim/device/apps/internal/browser/markup/MarkupBinaryConstants._E_INDICIES [I
      // 1e2: getstatic net/rim/device/apps/internal/browser/markup/HTMLUtilities._A_NAMES [Ljava/lang/String;
      // 1e5: getstatic net/rim/device/apps/internal/browser/markup/HTMLUtilities._A_TOKENS [I
      // 1e8: getstatic net/rim/device/apps/internal/browser/markup/HTMLUtilities._A_INDICIES [I
      // 1eb: getstatic net/rim/device/apps/internal/browser/markup/HTMLUtilities._AV_NAMES [Ljava/lang/String;
      // 1ee: getstatic net/rim/device/apps/internal/browser/markup/HTMLUtilities._AV_TOKENS [I
      // 1f1: getstatic net/rim/device/apps/internal/browser/markup/HTMLUtilities._AV_INDICIES [I
      // 1f4: invokestatic net/rim/device/internal/browser/markup/MarkupInputStream.getConvertedInputStream (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/io/InputStream;[I[B[Ljava/lang/String;[I[I[Ljava/lang/String;[I[I[Ljava/lang/String;[I[I[Ljava/lang/String;[I[I)Lnet/rim/device/internal/browser/markup/MarkupInputStream;
      // 1f7: astore 20
      // 1f9: aload 2
      // 1fa: ifnull 211
      // 1fd: new net/rim/device/apps/internal/browser/api/DeviceDataConversionEvent
      // 200: dup
      // 201: aload 4
      // 203: aload 20
      // 205: invokevirtual net/rim/device/internal/browser/markup/MarkupInputStream.getPipe ()Lnet/rim/device/internal/browser/util/Pipe;
      // 208: ldc_w "application/vnd.rim.html"
      // 20b: aconst_null
      // 20c: invokespecial net/rim/device/apps/internal/browser/api/DeviceDataConversionEvent.<init> (Ljava/lang/Object;Lnet/rim/device/internal/browser/util/Pipe;Ljava/lang/String;Ljava/lang/String;)V
      // 20f: astore 14
      // 211: aload 20
      // 213: astore 5
      // 215: goto 227
      // 218: astore 15
      // 21a: new java/lang/Object
      // 21d: dup
      // 21e: aload 15
      // 220: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 223: invokespecial net/rim/device/api/browser/field/RenderingException.<init> (Ljava/lang/String;)V
      // 226: athrow
      // 227: new net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow
      // 22a: dup
      // 22b: aload 4
      // 22d: aload 5
      // 22f: aload 11
      // 231: aload 3
      // 232: aload 2
      // 233: aload 8
      // 235: aload 9
      // 237: iload 6
      // 239: aload 10
      // 23b: aload 14
      // 23d: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.<init> (Ljavax/microedition/io/InputConnection;Ljava/io/InputStream;Ljava/lang/String;Lnet/rim/device/api/browser/field/RenderingSession;Lnet/rim/device/api/browser/field/RenderingApplication;Ljava/lang/String;Lnet/rim/device/apps/internal/browser/html/HTMLContext;ILnet/rim/device/apps/internal/browser/util/Frame;Lnet/rim/device/apps/internal/browser/api/DeviceDataConversionEvent;)V
      // 240: astore 15
      // 242: aload 15
      // 244: invokevirtual net/rim/device/apps/internal/browser/page/Renderer.processData ()Lnet/rim/device/api/browser/field/BrowserContent;
      // 247: areturn
      // 248: astore 15
      // 24a: new java/lang/Object
      // 24d: dup
      // 24e: aload 15
      // 250: invokevirtual java/io/IOException.toString ()Ljava/lang/String;
      // 253: invokespecial net/rim/device/api/browser/field/RenderingException.<init> (Ljava/lang/String;)V
      // 256: athrow
      // 257: astore 15
      // 259: new java/lang/Object
      // 25c: dup
      // 25d: aload 15
      // 25f: invokevirtual java/lang/IllegalArgumentException.toString ()Ljava/lang/String;
      // 262: invokespecial net/rim/device/api/browser/field/RenderingException.<init> (Ljava/lang/String;)V
      // 265: athrow
      // 266: astore 15
      // 268: new java/lang/Object
      // 26b: dup
      // 26c: aload 15
      // 26e: invokevirtual net/rim/device/apps/api/utility/serialization/SerializationException.toString ()Ljava/lang/String;
      // 271: invokespecial net/rim/device/api/browser/field/RenderingException.<init> (Ljava/lang/String;)V
      // 274: athrow
      // try (80 -> 239): 240 null
      // try (247 -> 263): 264 null
      // try (247 -> 263): 271 null
      // try (247 -> 263): 278 null
   }
}
