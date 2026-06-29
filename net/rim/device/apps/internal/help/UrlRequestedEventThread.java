package net.rim.device.apps.internal.help;

import net.rim.device.api.browser.field.UrlRequestedEvent;

final class UrlRequestedEventThread extends Thread {
   private UrlRequestedEvent _event;
   private HelpScreen _helpScreen;

   UrlRequestedEventThread(UrlRequestedEvent event, HelpScreen helpScreen) {
      this._event = event;
      this._helpScreen = helpScreen;
   }

   @Override
   public final void run() {
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
      // 000: aload 0
      // 001: getfield net/rim/device/apps/internal/help/UrlRequestedEventThread._event Lnet/rim/device/api/browser/field/UrlRequestedEvent;
      // 004: astore 1
      // 005: aload 1
      // 006: invokevirtual net/rim/device/api/browser/field/UrlRequestedEvent.getURL ()Ljava/lang/String;
      // 009: bipush 1
      // 00a: invokestatic net/rim/device/apps/internal/help/HelpScreen.getURL (Ljava/lang/String;Z)Ljava/lang/String;
      // 00d: astore 2
      // 00e: aload 2
      // 00f: ldc_w "cod:"
      // 012: ldc_w 1701707776
      // 015: invokestatic net/rim/device/api/util/StringUtilities.startsWithIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 018: ifeq 01e
      // 01b: goto 0ab
      // 01e: aload 2
      // 01f: ldc_w "data:"
      // 022: ldc_w 1701707776
      // 025: invokestatic net/rim/device/api/util/StringUtilities.startsWithIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 028: ifeq 02e
      // 02b: goto 0ab
      // 02e: aload 2
      // 02f: ldc_w "vsm:"
      // 032: ldc_w 1701707776
      // 035: invokestatic net/rim/device/api/util/StringUtilities.startsWithIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 038: ifne 0ab
      // 03b: new java/lang/Object
      // 03e: dup
      // 03f: bipush 3
      // 041: ldc2_w -6124962742482799009
      // 044: ldc_w "net.rim.device.apps.internal.resource.Help"
      // 047: invokestatic net/rim/device/api/i18n/ResourceBundle.getBundle (JLjava/lang/String;)Lnet/rim/device/api/i18n/ResourceBundleFamily;
      // 04a: bipush 7
      // 04c: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 04f: bipush 1
      // 050: anewarray 104
      // 053: dup
      // 054: bipush 0
      // 055: aload 2
      // 056: aastore
      // 057: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 05a: aconst_null
      // 05b: aconst_null
      // 05c: bipush 4
      // 05e: invokespecial net/rim/device/apps/api/ui/RunnableDialog.<init> (ILjava/lang/String;[Ljava/lang/Object;[II)V
      // 061: astore 3
      // 062: invokestatic net/rim/device/api/system/Application.getApplication ()Lnet/rim/device/api/system/Application;
      // 065: aload 3
      // 066: invokevirtual net/rim/device/api/system/Application.invokeAndWait (Ljava/lang/Runnable;)V
      // 069: aload 3
      // 06a: invokevirtual net/rim/device/apps/api/ui/RunnableDialog.getResult ()I
      // 06d: bipush 4
      // 06f: if_icmpeq 075
      // 072: goto 178
      // 075: aconst_null
      // 076: astore 4
      // 078: aload 0
      // 079: getfield net/rim/device/apps/internal/help/UrlRequestedEventThread._helpScreen Lnet/rim/device/apps/internal/help/HelpScreen;
      // 07c: getfield net/rim/device/apps/internal/help/HelpScreen._configToUse Ljava/util/Hashtable;
      // 07f: ifnull 092
      // 082: aload 0
      // 083: getfield net/rim/device/apps/internal/help/UrlRequestedEventThread._helpScreen Lnet/rim/device/apps/internal/help/HelpScreen;
      // 086: getfield net/rim/device/apps/internal/help/HelpScreen._configToUse Ljava/util/Hashtable;
      // 089: aload 2
      // 08a: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 08d: checkcast java/lang/Object
      // 090: astore 4
      // 092: aload 2
      // 093: aload 4
      // 095: ifnull 09d
      // 098: aload 4
      // 09a: goto 0a4
      // 09d: aload 0
      // 09e: getfield net/rim/device/apps/internal/help/UrlRequestedEventThread._helpScreen Lnet/rim/device/apps/internal/help/HelpScreen;
      // 0a1: getfield net/rim/device/apps/internal/help/HelpScreen._currentBrowserConfigUID Ljava/lang/String;
      // 0a4: invokestatic net/rim/device/apps/api/browser/BrowserServices.loadUrl (Ljava/lang/String;Ljava/lang/String;)Z
      // 0a7: pop
      // 0a8: goto 178
      // 0ab: aconst_null
      // 0ac: astore 3
      // 0ad: aconst_null
      // 0ae: astore 4
      // 0b0: aload 2
      // 0b1: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 0b4: checkcast java/lang/Object
      // 0b7: astore 3
      // 0b8: aload 1
      // 0b9: invokevirtual net/rim/device/api/browser/field/UrlRequestedEvent.getHeaders ()Lnet/rim/device/api/io/http/HttpHeaders;
      // 0bc: astore 5
      // 0be: aload 5
      // 0c0: ifnull 0ee
      // 0c3: aload 5
      // 0c5: invokevirtual net/rim/device/api/io/http/HttpHeaders.size ()I
      // 0c8: istore 6
      // 0ca: bipush 0
      // 0cb: istore 7
      // 0cd: iload 7
      // 0cf: iload 6
      // 0d1: if_icmpge 0ee
      // 0d4: aload 3
      // 0d5: aload 5
      // 0d7: iload 7
      // 0d9: invokevirtual net/rim/device/api/io/http/HttpHeaders.getPropertyKey (I)Ljava/lang/String;
      // 0dc: aload 5
      // 0de: iload 7
      // 0e0: invokevirtual net/rim/device/api/io/http/HttpHeaders.getPropertyValue (I)Ljava/lang/String;
      // 0e3: invokeinterface javax/microedition/io/HttpConnection.setRequestProperty (Ljava/lang/String;Ljava/lang/String;)V 3
      // 0e8: iinc 7 1
      // 0eb: goto 0cd
      // 0ee: aload 1
      // 0ef: invokevirtual net/rim/device/api/browser/field/UrlRequestedEvent.getPostData ()[B
      // 0f2: astore 6
      // 0f4: aload 6
      // 0f6: ifnonnull 105
      // 0f9: aload 3
      // 0fa: ldc_w "GET"
      // 0fd: invokeinterface javax/microedition/io/HttpConnection.setRequestMethod (Ljava/lang/String;)V 2
      // 102: goto 12c
      // 105: aload 3
      // 106: ldc_w "POST"
      // 109: invokeinterface javax/microedition/io/HttpConnection.setRequestMethod (Ljava/lang/String;)V 2
      // 10e: aload 3
      // 10f: ldc_w "Content-Length"
      // 112: aload 6
      // 114: arraylength
      // 115: invokestatic java/lang/String.valueOf (I)Ljava/lang/String;
      // 118: invokeinterface javax/microedition/io/HttpConnection.setRequestProperty (Ljava/lang/String;Ljava/lang/String;)V 3
      // 11d: aload 3
      // 11e: invokeinterface javax/microedition/io/OutputConnection.openOutputStream ()Ljava/io/OutputStream; 1
      // 123: astore 4
      // 125: aload 4
      // 127: aload 6
      // 129: invokevirtual java/io/OutputStream.write ([B)V
      // 12c: aload 4
      // 12e: ifnull 166
      // 131: aload 4
      // 133: invokevirtual java/io/OutputStream.close ()V
      // 136: goto 166
      // 139: astore 5
      // 13b: goto 166
      // 13e: astore 5
      // 140: aload 4
      // 142: ifnull 166
      // 145: aload 4
      // 147: invokevirtual java/io/OutputStream.close ()V
      // 14a: goto 166
      // 14d: astore 5
      // 14f: goto 166
      // 152: astore 8
      // 154: aload 4
      // 156: ifnull 163
      // 159: aload 4
      // 15b: invokevirtual java/io/OutputStream.close ()V
      // 15e: goto 163
      // 161: astore 9
      // 163: aload 8
      // 165: athrow
      // 166: aload 0
      // 167: getfield net/rim/device/apps/internal/help/UrlRequestedEventThread._helpScreen Lnet/rim/device/apps/internal/help/HelpScreen;
      // 16a: aload 0
      // 16b: getfield net/rim/device/apps/internal/help/UrlRequestedEventThread._helpScreen Lnet/rim/device/apps/internal/help/HelpScreen;
      // 16e: aload 3
      // 16f: aload 1
      // 170: invokevirtual net/rim/device/apps/internal/help/HelpScreen.getBrowserContent (Ljavax/microedition/io/HttpConnection;Lnet/rim/device/api/browser/field/Event;)Lnet/rim/device/api/browser/field/BrowserContent;
      // 173: invokevirtual net/rim/device/apps/internal/help/HelpScreen.displayBrowserContent (Lnet/rim/device/api/browser/field/BrowserContent;)V
      // 176: return
      // 177: astore 2
      // 178: return
      // try (134 -> 136): 137 null
      // try (81 -> 132): 139 null
      // try (142 -> 144): 145 null
      // try (81 -> 132): 147 null
      // try (139 -> 140): 147 null
      // try (150 -> 152): 153 null
      // try (147 -> 148): 147 null
      // try (3 -> 164): 165 null
   }
}
