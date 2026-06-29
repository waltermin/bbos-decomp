package net.rim.device.apps.internal.browser.wml;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;
import net.rim.device.api.io.http.HttpProtocolConstants;

public final class WMLRenderingConverter extends BrowserContentProvider implements HttpProtocolConstants {
   private static String APPLICATION__VND_WAP_WMLC = "application/vnd.wap.wmlc";
   private static final String TEXT_WML;
   private static final String[] ACCEPT = new String[]{"application/vnd.wap.wmlc", "text/vnd.wap.wml;q=0.5"};
   private static final String[] ACCEPT_WHEN_HTML_SUPPORTED = new String[]{"application/vnd.wap.wmlc;q=0.7", "text/vnd.wap.wml;q=0.5"};

   @Override
   public final String[] getSupportedMimeTypes() {
      return ACCEPT;
   }

   @Override
   public final String[] getAccept(RenderingOptions renderingOptions) {
      if (renderingOptions == null || !renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 21, true)) {
         return null;
      } else {
         return renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 22, true) ? ACCEPT_WHEN_HTML_SUPPORTED : ACCEPT;
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
      // 017: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getEvent ()Lnet/rim/device/api/browser/field/Event;
      // 01a: astore 6
      // 01c: aload 6
      // 01e: ifnull 029
      // 021: aload 6
      // 023: invokevirtual net/rim/device/api/browser/field/Event.getSourceURL ()Ljava/lang/String;
      // 026: goto 02a
      // 029: aconst_null
      // 02a: astore 7
      // 02c: aload 1
      // 02d: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getFlags ()I
      // 030: istore 8
      // 032: aconst_null
      // 033: astore 9
      // 035: aload 1
      // 036: dup
      // 037: instanceof net/rim/device/apps/internal/browser/core/BrowserContentProviderRenderingContext
      // 03a: ifne 041
      // 03d: pop
      // 03e: goto 063
      // 041: checkcast net/rim/device/apps/internal/browser/core/BrowserContentProviderRenderingContext
      // 044: astore 10
      // 046: aload 10
      // 048: invokevirtual net/rim/device/apps/internal/browser/core/BrowserContentProviderRenderingContext.getContext ()Ljava/lang/Object;
      // 04b: astore 11
      // 04d: aload 11
      // 04f: dup
      // 050: instanceof net/rim/device/apps/internal/browser/wml/WMLContext
      // 053: ifne 05a
      // 056: pop
      // 057: goto 060
      // 05a: checkcast net/rim/device/apps/internal/browser/wml/WMLContext
      // 05d: goto 061
      // 060: aconst_null
      // 061: astore 9
      // 063: aload 4
      // 065: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getContentType (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 068: astore 10
      // 06a: aload 10
      // 06c: ifnull 077
      // 06f: aload 10
      // 071: invokevirtual java/lang/String.length ()I
      // 074: ifne 07c
      // 077: ldc_w "text/vnd.wap.wml"
      // 07a: astore 10
      // 07c: aload 4
      // 07e: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getUrl (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 081: astore 11
      // 083: aload 4
      // 085: ifnull 08c
      // 088: aload 3
      // 089: ifnonnull 097
      // 08c: new java/lang/Object
      // 08f: dup
      // 090: ldc_w "InputConnection or RenderingOptions is not set"
      // 093: invokespecial net/rim/device/api/browser/field/RenderingException.<init> (Ljava/lang/String;)V
      // 096: athrow
      // 097: aload 3
      // 098: invokevirtual net/rim/device/api/browser/field/RenderingSession.getRenderingOptions ()Lnet/rim/device/api/browser/field/RenderingOptions;
      // 09b: astore 12
      // 09d: aconst_null
      // 09e: astore 13
      // 0a0: aconst_null
      // 0a1: astore 14
      // 0a3: aload 5
      // 0a5: ifnonnull 0b1
      // 0a8: aload 4
      // 0aa: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 0af: astore 5
      // 0b1: aload 5
      // 0b3: invokevirtual java/io/InputStream.markSupported ()Z
      // 0b6: ifeq 0c0
      // 0b9: aload 5
      // 0bb: astore 13
      // 0bd: goto 0cf
      // 0c0: new net/rim/device/apps/internal/browser/stack/MarkableInputStream
      // 0c3: dup
      // 0c4: aload 5
      // 0c6: invokespecial net/rim/device/apps/internal/browser/stack/MarkableInputStream.<init> (Ljava/io/InputStream;)V
      // 0c9: astore 13
      // 0cb: aload 13
      // 0cd: astore 5
      // 0cf: aload 10
      // 0d1: getstatic net/rim/device/apps/internal/browser/wml/WMLRenderingConverter.APPLICATION__VND_WAP_WMLC Ljava/lang/String;
      // 0d4: ldc_w 1701707776
      // 0d7: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 0da: ifeq 0e0
      // 0dd: goto 21a
      // 0e0: aconst_null
      // 0e1: astore 15
      // 0e3: bipush 0
      // 0e4: istore 16
      // 0e6: aload 2
      // 0e7: ifnull 0ff
      // 0ea: new net/rim/device/apps/internal/browser/api/DeviceDataConversionRequestEvent
      // 0ed: dup
      // 0ee: aload 4
      // 0f0: invokespecial net/rim/device/apps/internal/browser/api/DeviceDataConversionRequestEvent.<init> (Ljava/lang/Object;)V
      // 0f3: astore 17
      // 0f5: aload 2
      // 0f6: aload 17
      // 0f8: invokeinterface net/rim/device/api/browser/field/RenderingApplication.eventOccurred (Lnet/rim/device/api/browser/field/Event;)Ljava/lang/Object; 2
      // 0fd: astore 15
      // 0ff: aload 15
      // 101: instanceof [Ljava/lang/Object;
      // 104: ifeq 130
      // 107: aload 15
      // 109: checkcast [Ljava/lang/Object;
      // 10c: checkcast [Ljava/lang/Object;
      // 10f: astore 17
      // 111: aload 17
      // 113: arraylength
      // 114: bipush 2
      // 116: if_icmpne 130
      // 119: aload 17
      // 11b: bipush 1
      // 11c: aaload
      // 11d: dup
      // 11e: instanceof java/lang/Object
      // 121: ifne 128
      // 124: pop
      // 125: goto 130
      // 128: checkcast java/lang/Object
      // 12b: astore 5
      // 12d: bipush 1
      // 12e: istore 16
      // 130: iload 16
      // 132: ifeq 138
      // 135: goto 21a
      // 138: aconst_null
      // 139: astore 17
      // 13b: aload 4
      // 13d: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getCharacterEncoding (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 140: astore 18
      // 142: aload 12
      // 144: ifnull 156
      // 147: aload 12
      // 149: ldc2_w 4550690918222697397
      // 14c: bipush 8
      // 14e: ldc_w "us-ascii"
      // 151: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithStringValue (JILjava/lang/String;)Ljava/lang/String;
      // 154: astore 17
      // 156: aload 18
      // 158: ifnonnull 175
      // 15b: aload 4
      // 15d: dup
      // 15e: instanceof java/lang/Object
      // 161: ifne 168
      // 164: pop
      // 165: goto 175
      // 168: checkcast java/lang/Object
      // 16b: ldc_w "Accept-Charset"
      // 16e: invokeinterface javax/microedition/io/HttpConnection.getHeaderField (Ljava/lang/String;)Ljava/lang/String; 2
      // 173: astore 18
      // 175: aload 12
      // 177: ifnull 18b
      // 17a: aload 12
      // 17c: ldc2_w 4550690918222697397
      // 17f: bipush 1
      // 180: bipush 0
      // 181: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithBooleanValue (JIZ)Z
      // 184: ifeq 18b
      // 187: aload 17
      // 189: astore 18
      // 18b: aload 10
      // 18d: aload 18
      // 18f: aload 17
      // 191: aload 5
      // 193: getstatic net/rim/device/apps/internal/browser/markup/HTMLUtilities.STATE_TABLE [I
      // 196: aconst_null
      // 197: getstatic net/rim/device/apps/internal/browser/markup/WML1xBinaryConstants._T_NAMES [Ljava/lang/String;
      // 19a: getstatic net/rim/device/apps/internal/browser/markup/WML1xBinaryConstants._T_TOKENS [I
      // 19d: getstatic net/rim/device/apps/internal/browser/markup/WML1xBinaryConstants._T_INDICIES [I
      // 1a0: getstatic net/rim/device/apps/internal/browser/markup/MarkupBinaryConstants._E_NAMES [Ljava/lang/String;
      // 1a3: getstatic net/rim/device/apps/internal/browser/markup/MarkupBinaryConstants._E_TOKENS [I
      // 1a6: getstatic net/rim/device/apps/internal/browser/markup/MarkupBinaryConstants._E_INDICIES [I
      // 1a9: getstatic net/rim/device/apps/internal/browser/markup/WML1xBinaryConstants._A_NAMES [Ljava/lang/String;
      // 1ac: getstatic net/rim/device/apps/internal/browser/markup/WML1xBinaryConstants._A_TOKENS [I
      // 1af: getstatic net/rim/device/apps/internal/browser/markup/WML1xBinaryConstants._A_INDICIES [I
      // 1b2: getstatic net/rim/device/apps/internal/browser/markup/WML1xBinaryConstants._A_NAMES [Ljava/lang/String;
      // 1b5: getstatic net/rim/device/apps/internal/browser/markup/WML1xBinaryConstants._A_TOKENS [I
      // 1b8: getstatic net/rim/device/apps/internal/browser/markup/WML1xBinaryConstants._A_INDICIES [I
      // 1bb: invokestatic net/rim/device/internal/browser/markup/MarkupInputStream.getConvertedInputStream (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/io/InputStream;[I[B[Ljava/lang/String;[I[I[Ljava/lang/String;[I[I[Ljava/lang/String;[I[I[Ljava/lang/String;[I[I)Lnet/rim/device/internal/browser/markup/MarkupInputStream;
      // 1be: astore 19
      // 1c0: aload 18
      // 1c2: ifnonnull 1ca
      // 1c5: ldc_w "iso-8859-1"
      // 1c8: astore 18
      // 1ca: new net/rim/device/apps/internal/browser/stack/AccumulatorInputStream
      // 1cd: dup
      // 1ce: aload 4
      // 1d0: aload 19
      // 1d2: aconst_null
      // 1d3: bipush 1
      // 1d4: invokespecial net/rim/device/apps/internal/browser/stack/AccumulatorInputStream.<init> (Ljavax/microedition/io/InputConnection;Ljava/io/InputStream;Lnet/rim/device/cldc/io/utility/SessionStats;Z)V
      // 1d7: astore 5
      // 1d9: aload 2
      // 1da: ifnull 21a
      // 1dd: new net/rim/device/apps/internal/browser/api/DeviceDataConversionEvent
      // 1e0: dup
      // 1e1: aload 4
      // 1e3: aload 19
      // 1e5: invokevirtual net/rim/device/internal/browser/markup/MarkupInputStream.getPipe ()Lnet/rim/device/internal/browser/util/Pipe;
      // 1e8: getstatic net/rim/device/apps/internal/browser/wml/WMLRenderingConverter.APPLICATION__VND_WAP_WMLC Ljava/lang/String;
      // 1eb: aload 18
      // 1ed: invokespecial net/rim/device/apps/internal/browser/api/DeviceDataConversionEvent.<init> (Ljava/lang/Object;Lnet/rim/device/internal/browser/util/Pipe;Ljava/lang/String;Ljava/lang/String;)V
      // 1f0: astore 14
      // 1f2: goto 21a
      // 1f5: astore 15
      // 1f7: new java/lang/Object
      // 1fa: dup
      // 1fb: new java/lang/Object
      // 1fe: dup
      // 1ff: invokespecial java/lang/StringBuffer.<init> ()V
      // 202: sipush 551
      // 205: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 208: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 20b: aload 15
      // 20d: invokevirtual java/io/IOException.toString ()Ljava/lang/String;
      // 210: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 213: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 216: invokespecial net/rim/device/api/browser/field/RenderingException.<init> (Ljava/lang/String;)V
      // 219: athrow
      // 21a: aconst_null
      // 21b: astore 15
      // 21d: new net/rim/device/apps/internal/browser/wml/WMLRenderer
      // 220: dup
      // 221: aload 4
      // 223: aload 5
      // 225: aload 11
      // 227: aload 3
      // 228: aload 2
      // 229: aload 7
      // 22b: aload 9
      // 22d: iload 8
      // 22f: aload 14
      // 231: invokespecial net/rim/device/apps/internal/browser/wml/WMLRenderer.<init> (Ljavax/microedition/io/InputConnection;Ljava/io/InputStream;Ljava/lang/String;Lnet/rim/device/api/browser/field/RenderingSession;Lnet/rim/device/api/browser/field/RenderingApplication;Ljava/lang/String;Lnet/rim/device/apps/internal/browser/wml/WMLContext;ILnet/rim/device/apps/internal/browser/api/DeviceDataConversionEvent;)V
      // 234: astore 16
      // 236: aload 16
      // 238: invokevirtual net/rim/device/apps/internal/browser/wml/WMLRenderer.processData ()Lnet/rim/device/api/browser/field/BrowserContent;
      // 23b: astore 15
      // 23d: aload 14
      // 23f: ifnonnull 245
      // 242: goto 328
      // 245: aload 2
      // 246: ifnonnull 24c
      // 249: goto 328
      // 24c: aload 14
      // 24e: invokevirtual net/rim/device/apps/internal/browser/api/DeviceDataConversionEvent.getPipe ()Lnet/rim/device/internal/browser/util/Pipe;
      // 251: astore 17
      // 253: aload 17
      // 255: ifnonnull 25b
      // 258: goto 328
      // 25b: aload 17
      // 25d: invokevirtual net/rim/device/internal/browser/util/Pipe.isAborted ()Z
      // 260: ifeq 266
      // 263: goto 328
      // 266: aload 17
      // 268: invokevirtual net/rim/device/internal/browser/util/Pipe.waitUntilClosed ()V
      // 26b: aload 2
      // 26c: aload 14
      // 26e: invokeinterface net/rim/device/api/browser/field/RenderingApplication.eventOccurred (Lnet/rim/device/api/browser/field/Event;)Ljava/lang/Object; 2
      // 273: pop
      // 274: goto 328
      // 277: astore 16
      // 279: aload 13
      // 27b: invokevirtual java/io/InputStream.reset ()V
      // 27e: new java/lang/Object
      // 281: dup
      // 282: aload 4
      // 284: aload 13
      // 286: aload 2
      // 287: aload 3
      // 288: iload 8
      // 28a: aload 6
      // 28c: invokespecial net/rim/device/api/browser/plugin/BrowserContentProviderContext.<init> (Ljavax/microedition/io/InputConnection;Ljava/io/InputStream;Lnet/rim/device/api/browser/field/RenderingApplication;Lnet/rim/device/api/browser/field/RenderingSession;ILnet/rim/device/api/browser/field/Event;)V
      // 28f: astore 17
      // 291: new net/rim/device/apps/internal/browser/api/DeviceDataWrongContentTypeEvent
      // 294: dup
      // 295: aload 4
      // 297: aload 16
      // 299: invokevirtual net/rim/device/internal/browser/markup/MarkupWrongMIMEType.getNewMIMEType ()Ljava/lang/String;
      // 29c: invokespecial net/rim/device/apps/internal/browser/api/DeviceDataWrongContentTypeEvent.<init> (Ljava/lang/Object;Ljava/lang/String;)V
      // 29f: astore 18
      // 2a1: aload 2
      // 2a2: aload 18
      // 2a4: invokeinterface net/rim/device/api/browser/field/RenderingApplication.eventOccurred (Lnet/rim/device/api/browser/field/Event;)Ljava/lang/Object; 2
      // 2a9: pop
      // 2aa: aload 16
      // 2ac: invokevirtual net/rim/device/internal/browser/markup/MarkupWrongMIMEType.getNewMIMEType ()Ljava/lang/String;
      // 2af: aload 17
      // 2b1: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.renderBrowserContent (Ljava/lang/String;Lnet/rim/device/api/browser/plugin/BrowserContentProviderContext;)Lnet/rim/device/api/browser/field/BrowserContent;
      // 2b4: astore 15
      // 2b6: goto 328
      // 2b9: astore 16
      // 2bb: new java/lang/Object
      // 2be: dup
      // 2bf: new java/lang/Object
      // 2c2: dup
      // 2c3: invokespecial java/lang/StringBuffer.<init> ()V
      // 2c6: sipush 551
      // 2c9: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 2cc: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2cf: aload 16
      // 2d1: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 2d4: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2d7: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 2da: invokespecial net/rim/device/api/browser/field/RenderingException.<init> (Ljava/lang/String;)V
      // 2dd: athrow
      // 2de: astore 16
      // 2e0: new java/lang/Object
      // 2e3: dup
      // 2e4: new java/lang/Object
      // 2e7: dup
      // 2e8: invokespecial java/lang/StringBuffer.<init> ()V
      // 2eb: sipush 551
      // 2ee: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 2f1: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2f4: aload 16
      // 2f6: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 2f9: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2fc: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 2ff: invokespecial net/rim/device/api/browser/field/RenderingException.<init> (Ljava/lang/String;)V
      // 302: athrow
      // 303: astore 16
      // 305: new java/lang/Object
      // 308: dup
      // 309: new java/lang/Object
      // 30c: dup
      // 30d: invokespecial java/lang/StringBuffer.<init> ()V
      // 310: sipush 551
      // 313: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 316: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 319: aload 16
      // 31b: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 31e: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 321: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 324: invokespecial net/rim/device/api/browser/field/RenderingException.<init> (Ljava/lang/String;)V
      // 327: athrow
      // 328: aload 15
      // 32a: areturn
      // try (77 -> 220): 221 null
      // try (238 -> 276): 277 null
      // try (238 -> 306): 307 null
      // try (238 -> 306): 322 null
      // try (238 -> 306): 337 null
   }
}
