package net.rim.device.apps.internal.browser.sdp;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;

public final class SDPRenderingConverter extends BrowserContentProvider {
   private static final String[] ACCEPT = new String[]{"application/sdp"};

   @Override
   public final String[] getSupportedMimeTypes() {
      return ACCEPT;
   }

   @Override
   public final String[] getAccept(RenderingOptions renderingOptions) {
      return ACCEPT;
   }

   @Override
   public final BrowserContent getBrowserContent(BrowserContentProviderContext param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 000: aload 1
      // 001: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getInputConnection ()Ljavax/microedition/io/InputConnection;
      // 004: astore 2
      // 005: aconst_null
      // 006: astore 3
      // 007: aload 2
      // 008: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 00d: astore 3
      // 00e: aload 3
      // 00f: ifnonnull 01a
      // 012: new java/lang/Object
      // 015: dup
      // 016: invokespecial net/rim/device/api/browser/field/RenderingException.<init> ()V
      // 019: athrow
      // 01a: new java/lang/Object
      // 01d: dup
      // 01e: aload 3
      // 01f: invokespecial net/rim/device/api/io/LineReader.<init> (Ljava/io/InputStream;)V
      // 022: astore 4
      // 024: ldc_w "UTF-8"
      // 027: astore 5
      // 029: new java/lang/Object
      // 02c: dup
      // 02d: invokespecial net/rim/device/api/util/MultiMap.<init> ()V
      // 030: astore 6
      // 032: aload 4
      // 034: invokevirtual net/rim/device/api/io/LineReader.readLine ()[B
      // 037: astore 7
      // 039: new java/lang/Object
      // 03c: dup
      // 03d: aload 7
      // 03f: aload 5
      // 041: invokespecial java/lang/String.<init> ([BLjava/lang/String;)V
      // 044: astore 8
      // 046: aload 8
      // 048: invokevirtual java/lang/String.trim ()Ljava/lang/String;
      // 04b: pop
      // 04c: aload 8
      // 04e: invokevirtual java/lang/String.length ()I
      // 051: ifne 057
      // 054: goto 032
      // 057: aload 8
      // 059: bipush 61
      // 05b: invokevirtual java/lang/String.indexOf (I)I
      // 05e: istore 9
      // 060: iload 9
      // 062: bipush -1
      // 064: if_icmpne 06f
      // 067: new java/lang/Object
      // 06a: dup
      // 06b: invokespecial net/rim/device/api/browser/field/RenderingException.<init> ()V
      // 06e: athrow
      // 06f: aload 8
      // 071: bipush 0
      // 072: iload 9
      // 074: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 077: astore 10
      // 079: iload 9
      // 07b: bipush 1
      // 07c: iadd
      // 07d: aload 8
      // 07f: invokevirtual java/lang/String.length ()I
      // 082: if_icmpne 08d
      // 085: ldc_w ""
      // 088: astore 11
      // 08a: goto 098
      // 08d: aload 8
      // 08f: iload 9
      // 091: bipush 1
      // 092: iadd
      // 093: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 096: astore 11
      // 098: aload 6
      // 09a: aload 10
      // 09c: aload 11
      // 09e: invokevirtual net/rim/device/api/util/MultiMap.add (Ljava/lang/Object;Ljava/lang/Object;)Z
      // 0a1: pop
      // 0a2: goto 032
      // 0a5: astore 7
      // 0a7: aconst_null
      // 0a8: astore 7
      // 0aa: aconst_null
      // 0ab: astore 8
      // 0ad: aload 6
      // 0af: ldc_w "a"
      // 0b2: invokevirtual net/rim/device/api/util/MultiMap.elements (Ljava/lang/Object;)Ljava/util/Enumeration;
      // 0b5: astore 9
      // 0b7: aload 9
      // 0b9: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 0be: ifeq 10c
      // 0c1: aload 9
      // 0c3: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 0c8: checkcast java/lang/Object
      // 0cb: astore 10
      // 0cd: aload 10
      // 0cf: ifnull 0eb
      // 0d2: aload 7
      // 0d4: ifnonnull 0eb
      // 0d7: aload 10
      // 0d9: ldc_w "control:rtsp://"
      // 0dc: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 0df: ifeq 0eb
      // 0e2: aload 10
      // 0e4: bipush 8
      // 0e6: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 0e9: astore 7
      // 0eb: aload 10
      // 0ed: ifnull 0b7
      // 0f0: aload 8
      // 0f2: ifnonnull 0b7
      // 0f5: aload 10
      // 0f7: ldc_w "control:http://"
      // 0fa: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 0fd: ifeq 0b7
      // 100: aload 10
      // 102: bipush 8
      // 104: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 107: astore 8
      // 109: goto 0b7
      // 10c: aload 7
      // 10e: ifnonnull 13f
      // 111: aload 8
      // 113: ifnonnull 13f
      // 116: aload 6
      // 118: ldc_w "u"
      // 11b: invokevirtual net/rim/device/api/util/MultiMap.elements (Ljava/lang/Object;)Ljava/util/Enumeration;
      // 11e: astore 10
      // 120: aload 10
      // 122: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 127: ifeq 13f
      // 12a: aload 10
      // 12c: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 131: checkcast java/lang/Object
      // 134: astore 11
      // 136: aload 11
      // 138: ifnull 13f
      // 13b: aload 11
      // 13d: astore 7
      // 13f: aload 7
      // 141: ifnonnull 148
      // 144: aload 8
      // 146: astore 7
      // 148: aload 7
      // 14a: ifnull 167
      // 14d: aload 1
      // 14e: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getRenderingApplication ()Lnet/rim/device/api/browser/field/RenderingApplication;
      // 151: new java/lang/Object
      // 154: dup
      // 155: aload 2
      // 156: aload 7
      // 158: aload 1
      // 159: invokevirtual net/rim/device/api/browser/plugin/BrowserContentProviderContext.getEvent ()Lnet/rim/device/api/browser/field/Event;
      // 15c: aload 6
      // 15e: invokespecial net/rim/device/apps/internal/browser/api/SDPRedirectEvent.<init> (Ljava/lang/Object;Ljava/lang/String;Lnet/rim/device/api/browser/field/Event;Lnet/rim/device/api/util/MultiMap;)V
      // 161: invokeinterface net/rim/device/api/browser/field/RenderingApplication.eventOccurred (Lnet/rim/device/api/browser/field/Event;)Ljava/lang/Object; 2
      // 166: pop
      // 167: aconst_null
      // 168: astore 10
      // 16a: aload 3
      // 16b: ifnull 177
      // 16e: aload 3
      // 16f: invokevirtual java/io/InputStream.close ()V
      // 172: goto 177
      // 175: astore 11
      // 177: aload 10
      // 179: areturn
      // 17a: astore 4
      // 17c: aload 3
      // 17d: ifnull 19e
      // 180: aload 3
      // 181: invokevirtual java/io/InputStream.close ()V
      // 184: goto 19e
      // 187: astore 4
      // 189: goto 19e
      // 18c: astore 12
      // 18e: aload 3
      // 18f: ifnull 19b
      // 192: aload 3
      // 193: invokevirtual java/io/InputStream.close ()V
      // 196: goto 19b
      // 199: astore 13
      // 19b: aload 12
      // 19d: athrow
      // 19e: new java/lang/Object
      // 1a1: dup
      // 1a2: invokespecial net/rim/device/api/browser/field/RenderingException.<init> ()V
      // 1a5: athrow
      // try (25 -> 78): 78 null
      // try (160 -> 162): 163 null
      // try (5 -> 158): 166 null
      // try (169 -> 171): 172 null
      // try (5 -> 158): 174 null
      // try (166 -> 167): 174 null
      // try (177 -> 179): 180 null
      // try (174 -> 175): 174 null
   }
}
