package net.rim.device.apps.internal.browser.cod;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RenderingException;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;

public final class JADConverterDescriptor extends BrowserContentProvider {
   private static final String DEFAULT_JAD_CHARACTER_ENCODING = "UTF-8";
   public static final String[] ACCEPT = new String[]{"text/vnd.sun.j2me.app-descriptor", "application/java-archive", "application/java"};

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
      // 000: bipush 1
      // 001: invokestatic net/rim/device/api/system/ControlledAccess.verifyRRISignatures (Z)Z
      // 004: ifne 00f
      // 007: new net/rim/device/api/browser/field/RenderingException
      // 00a: dup
      // 00b: invokespecial net/rim/device/api/browser/field/RenderingException.<init> ()V
      // 00e: athrow
      // 00f: aload 1
      // 010: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getRenderingApplication ()Lnet/rim/device/api/browser/field/RenderingApplication;
      // 013: astore 2
      // 014: aload 1
      // 015: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getRenderingSession ()Lnet/rim/device/api/browser/field/RenderingSession;
      // 018: invokevirtual net/rim/device/api/browser/field/RenderingSession.getRenderingOptions ()Lnet/rim/device/api/browser/field/RenderingOptions;
      // 01b: astore 3
      // 01c: aload 1
      // 01d: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getInputConnection ()Ljavax/microedition/io/InputConnection;
      // 020: astore 4
      // 022: aload 1
      // 023: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getFlags ()I
      // 026: istore 5
      // 028: aload 4
      // 02a: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getUrl (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 02d: astore 6
      // 02f: aload 1
      // 030: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getInputStream ()Ljava/io/InputStream;
      // 033: astore 7
      // 035: aload 4
      // 037: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getContentType (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 03a: astore 8
      // 03c: aload 4
      // 03e: ifnonnull 049
      // 041: new net/rim/device/api/browser/field/RenderingException
      // 044: dup
      // 045: invokespecial net/rim/device/api/browser/field/RenderingException.<init> ()V
      // 048: athrow
      // 049: aload 7
      // 04b: ifnonnull 064
      // 04e: aload 4
      // 050: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 055: astore 7
      // 057: goto 064
      // 05a: astore 9
      // 05c: new net/rim/device/api/browser/field/RenderingException
      // 05f: dup
      // 060: invokespecial net/rim/device/api/browser/field/RenderingException.<init> ()V
      // 063: athrow
      // 064: aload 7
      // 066: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.readBytesFromInputStream (Ljava/io/InputStream;)[B
      // 069: astore 9
      // 06b: new net/rim/device/apps/internal/browser/cod/JADAttributeParser
      // 06e: dup
      // 06f: aload 6
      // 071: invokespecial net/rim/device/apps/internal/browser/cod/JADAttributeParser.<init> (Ljava/lang/String;)V
      // 074: astore 10
      // 076: aconst_null
      // 077: astore 11
      // 079: aconst_null
      // 07a: astore 12
      // 07c: aload 8
      // 07e: ifnull 088
      // 081: aload 8
      // 083: invokestatic net/rim/device/api/io/MIMETypeAssociations.getNormalizedType (Ljava/lang/String;)Ljava/lang/String;
      // 086: astore 8
      // 088: ldc_w "application/java-archive"
      // 08b: aload 8
      // 08d: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 090: ifne 0a1
      // 093: ldc_w "application/java"
      // 096: aload 8
      // 098: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 09b: ifne 0a1
      // 09e: goto 14f
      // 0a1: aconst_null
      // 0a2: astore 13
      // 0a4: ldc_w "net.rim.device.apps.internal.browser.cod.Harness"
      // 0a7: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 0aa: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 0ad: checkcast net/rim/device/apps/internal/browser/cod/JARCompiler
      // 0b0: astore 13
      // 0b2: goto 0c1
      // 0b5: astore 14
      // 0b7: goto 0c1
      // 0ba: astore 14
      // 0bc: goto 0c1
      // 0bf: astore 14
      // 0c1: aload 13
      // 0c3: ifnonnull 0c9
      // 0c6: goto 149
      // 0c9: aload 9
      // 0cb: ifnonnull 0d1
      // 0ce: goto 149
      // 0d1: aload 10
      // 0d3: aload 6
      // 0d5: invokevirtual net/rim/device/apps/internal/browser/cod/JADAttributeParser.setJarURL (Ljava/lang/String;)V
      // 0d8: new java/lang/StringBuffer
      // 0db: dup
      // 0dc: aload 13
      // 0de: new java/io/ByteArrayInputStream
      // 0e1: dup
      // 0e2: aload 9
      // 0e4: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 0e7: invokeinterface net/rim/device/apps/internal/browser/cod/JARCompiler.extractManifest (Ljava/io/InputStream;)Ljava/lang/String; 2
      // 0ec: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 0ef: astore 14
      // 0f1: aload 14
      // 0f3: bipush 10
      // 0f5: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 0f8: ldc_w "MIDlet-Jar-URL"
      // 0fb: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0fe: ldc_w ": "
      // 101: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 104: aload 6
      // 106: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 109: pop
      // 10a: aload 10
      // 10c: aload 9
      // 10e: arraylength
      // 10f: invokevirtual net/rim/device/apps/internal/browser/cod/JADAttributeParser.setJarSize (I)V
      // 112: aload 14
      // 114: bipush 10
      // 116: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 119: ldc_w "MIDlet-Jar-Size"
      // 11c: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 11f: ldc_w ": "
      // 122: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 125: aload 9
      // 127: arraylength
      // 128: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 12b: pop
      // 12c: aload 14
      // 12e: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 131: astore 12
      // 133: new java/io/ByteArrayInputStream
      // 136: dup
      // 137: aload 9
      // 139: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 13c: astore 11
      // 13e: goto 190
      // 141: astore 14
      // 143: aconst_null
      // 144: astore 10
      // 146: goto 190
      // 149: aconst_null
      // 14a: astore 10
      // 14c: goto 190
      // 14f: aload 4
      // 151: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getCharacterEncoding (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 154: astore 13
      // 156: aload 13
      // 158: ifnonnull 160
      // 15b: ldc_w "UTF-8"
      // 15e: astore 13
      // 160: new java/lang/String
      // 163: dup
      // 164: aload 9
      // 166: aload 13
      // 168: invokespecial java/lang/String.<init> ([BLjava/lang/String;)V
      // 16b: astore 12
      // 16d: goto 190
      // 170: astore 13
      // 172: new java/lang/String
      // 175: dup
      // 176: aload 9
      // 178: ldc_w "UTF-8"
      // 17b: invokespecial java/lang/String.<init> ([BLjava/lang/String;)V
      // 17e: astore 12
      // 180: goto 190
      // 183: astore 14
      // 185: new java/lang/String
      // 188: dup
      // 189: aload 9
      // 18b: invokespecial java/lang/String.<init> ([B)V
      // 18e: astore 12
      // 190: aload 6
      // 192: astore 13
      // 194: aload 3
      // 195: ifnull 1e7
      // 198: aload 3
      // 199: ldc2_w 4550690918222697397
      // 19c: bipush 49
      // 19e: bipush 0
      // 19f: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithBooleanValue (JIZ)Z
      // 1a2: ifeq 1e7
      // 1a5: aload 4
      // 1a7: dup
      // 1a8: instanceof javax/microedition/io/HttpConnection
      // 1ab: ifne 1b2
      // 1ae: pop
      // 1af: goto 1e7
      // 1b2: checkcast javax/microedition/io/HttpConnection
      // 1b5: astore 14
      // 1b7: aload 14
      // 1b9: ldc_w "content-location"
      // 1bc: invokeinterface javax/microedition/io/HttpConnection.getHeaderField (Ljava/lang/String;)Ljava/lang/String; 2
      // 1c1: astore 15
      // 1c3: aload 15
      // 1c5: ifnonnull 1d4
      // 1c8: aload 14
      // 1ca: ldc_w "location"
      // 1cd: invokeinterface javax/microedition/io/HttpConnection.getHeaderField (Ljava/lang/String;)Ljava/lang/String; 2
      // 1d2: astore 15
      // 1d4: aload 15
      // 1d6: ifnull 1e7
      // 1d9: aload 15
      // 1db: aload 6
      // 1dd: invokestatic net/rim/device/apps/api/utility/general/URI.getAbsoluteURL (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
      // 1e0: astore 13
      // 1e2: goto 1e7
      // 1e5: astore 15
      // 1e7: aload 10
      // 1e9: ifnull 20b
      // 1ec: aload 10
      // 1ee: aload 12
      // 1f0: aload 13
      // 1f2: invokevirtual net/rim/device/apps/internal/browser/cod/JADAttributeParser.parse (Ljava/lang/String;Ljava/lang/String;)Z
      // 1f5: ifne 20b
      // 1f8: new net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager
      // 1fb: dup
      // 1fc: aconst_null
      // 1fd: aconst_null
      // 1fe: aload 10
      // 200: aconst_null
      // 201: aconst_null
      // 202: invokespecial net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager.<init> (Lnet/rim/device/api/browser/field/RenderingApplication;Lnet/rim/device/apps/internal/browser/cod/ApplicationDownloadListener;Lnet/rim/device/apps/internal/browser/cod/JADAttributeParser;Ljava/io/InputStream;Ljava/lang/String;)V
      // 205: sipush 906
      // 208: invokevirtual net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager.sendStatusReport (I)V
      // 20b: new net/rim/device/api/browser/field/BrowserContentBaseImpl
      // 20e: dup
      // 20f: aload 6
      // 211: aconst_null
      // 212: aload 2
      // 213: aload 3
      // 214: iload 5
      // 216: invokespecial net/rim/device/api/browser/field/BrowserContentBaseImpl.<init> (Ljava/lang/String;Lnet/rim/device/api/ui/Field;Lnet/rim/device/api/browser/field/RenderingApplication;Lnet/rim/device/api/browser/field/RenderingOptions;I)V
      // 219: astore 14
      // 21b: new net/rim/device/apps/internal/browser/cod/JADBrowserField
      // 21e: dup
      // 21f: aload 14
      // 221: aload 10
      // 223: aload 11
      // 225: invokespecial net/rim/device/apps/internal/browser/cod/JADBrowserField.<init> (Lnet/rim/device/api/browser/field/BrowserContentBaseImpl;Lnet/rim/device/apps/internal/browser/cod/JADAttributeParser;Ljava/io/InputStream;)V
      // 228: astore 15
      // 22a: aload 14
      // 22c: aload 15
      // 22e: invokevirtual net/rim/device/api/browser/field/BrowserContentBaseImpl.setContent (Lnet/rim/device/api/ui/Field;)V
      // 231: aload 14
      // 233: areturn
      // try (37 -> 40): 41 null
      // try (74 -> 79): 80 null
      // try (74 -> 79): 82 null
      // try (74 -> 79): 84 null
      // try (94 -> 137): 138 null
      // try (145 -> 158): 159 null
      // try (160 -> 166): 167 null
      // try (191 -> 207): 208 null
   }
}
