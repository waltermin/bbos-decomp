package net.rim.device.apps.internal.blackberryemail.folder;

class IconFetcher$Worker extends Thread {
   private final IconFetcher this$0;

   IconFetcher$Worker(IconFetcher _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.processRequests();
   }

   private void processRequests() {
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
      // 000: ldc_w 1200000
      // 003: i2l
      // 004: lstore 1
      // 005: bipush 0
      // 006: istore 3
      // 007: iload 3
      // 008: getstatic net/rim/device/apps/internal/blackberryemail/folder/IconFetcher._requests Ljava/util/Vector;
      // 00b: invokevirtual java/util/Vector.size ()I
      // 00e: if_icmplt 014
      // 011: goto 279
      // 014: getstatic net/rim/device/apps/internal/blackberryemail/folder/IconFetcher._requests Ljava/util/Vector;
      // 017: iload 3
      // 018: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 01b: checkcast net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Request
      // 01e: astore 4
      // 020: aload 4
      // 022: ifnonnull 026
      // 025: return
      // 026: invokestatic java/lang/System.currentTimeMillis ()J
      // 029: aload 4
      // 02b: getfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Request._nextRetryTime J
      // 02e: lsub
      // 02f: lstore 5
      // 031: lload 5
      // 033: bipush 0
      // 034: i2l
      // 035: lcmp
      // 036: ifgt 04e
      // 039: lload 5
      // 03b: lneg
      // 03c: lstore 5
      // 03e: lload 1
      // 03f: lload 5
      // 041: lcmp
      // 042: ifgt 048
      // 045: goto 273
      // 048: lload 5
      // 04a: lstore 1
      // 04b: goto 273
      // 04e: bipush 0
      // 04f: istore 7
      // 051: aload 4
      // 053: invokestatic net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Request.access$208 (Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Request;)B
      // 056: bipush 3
      // 058: if_icmplt 05e
      // 05b: goto 267
      // 05e: aconst_null
      // 05f: astore 8
      // 061: invokestatic net/rim/device/api/ui/theme/ThemeManager.getActiveTheme ()Lnet/rim/device/api/ui/theme/Theme;
      // 064: astore 9
      // 066: aload 9
      // 068: invokevirtual net/rim/device/api/ui/theme/Theme.getRibbonIconWidth ()I
      // 06b: istore 10
      // 06d: aload 9
      // 06f: invokevirtual net/rim/device/api/ui/theme/Theme.getRibbonIconHeight ()I
      // 072: istore 11
      // 074: new net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$URL
      // 077: dup
      // 078: aload 0
      // 079: aload 4
      // 07b: getfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Request._iconURL Ljava/lang/String;
      // 07e: invokespecial net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$URL.<init> (Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker;Ljava/lang/String;)V
      // 081: astore 12
      // 083: aload 12
      // 085: ldc_w "item"
      // 088: ldc_w "icon"
      // 08b: invokevirtual net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$URL.addParam (Ljava/lang/String;Ljava/lang/String;)V
      // 08e: aload 12
      // 090: ldc_w "version"
      // 093: aload 4
      // 095: getfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Request._version I
      // 098: invokevirtual net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$URL.addParam (Ljava/lang/String;I)V
      // 09b: aload 12
      // 09d: ldc_w "icon_x"
      // 0a0: iload 10
      // 0a2: invokevirtual net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$URL.addParam (Ljava/lang/String;I)V
      // 0a5: aload 12
      // 0a7: ldc_w "icon_y"
      // 0aa: iload 11
      // 0ac: invokevirtual net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$URL.addParam (Ljava/lang/String;I)V
      // 0af: aload 0
      // 0b0: aload 12
      // 0b2: aload 4
      // 0b4: bipush 0
      // 0b5: iload 10
      // 0b7: iload 11
      // 0b9: invokespecial net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker.fetchIconData (Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$URL;Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Request;III)Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$FetchedIconData;
      // 0bc: astore 8
      // 0be: aload 8
      // 0c0: ifnonnull 0d2
      // 0c3: aload 0
      // 0c4: aload 12
      // 0c6: aload 4
      // 0c8: bipush 1
      // 0c9: iload 10
      // 0cb: iload 11
      // 0cd: invokespecial net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker.fetchIconData (Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$URL;Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Request;III)Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$FetchedIconData;
      // 0d0: astore 8
      // 0d2: aload 8
      // 0d4: ifnonnull 0e7
      // 0d7: aload 0
      // 0d8: aload 12
      // 0da: aload 4
      // 0dc: bipush 2
      // 0de: iload 10
      // 0e0: iload 11
      // 0e2: invokespecial net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker.fetchIconData (Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$URL;Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Request;III)Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$FetchedIconData;
      // 0e5: astore 8
      // 0e7: aload 8
      // 0e9: ifnonnull 0ef
      // 0ec: goto 24e
      // 0ef: bipush 1
      // 0f0: istore 7
      // 0f2: getstatic net/rim/device/apps/internal/blackberryemail/folder/IconFetcher._requests Ljava/util/Vector;
      // 0f5: aload 4
      // 0f7: invokevirtual java/util/Vector.removeElement (Ljava/lang/Object;)Z
      // 0fa: pop
      // 0fb: iinc 3 -1
      // 0fe: aload 8
      // 100: getfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$FetchedIconData._iconData [B
      // 103: astore 13
      // 105: aload 8
      // 107: getfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$FetchedIconData._version I
      // 10a: aload 8
      // 10c: getfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$FetchedIconData._width I
      // 10f: aload 8
      // 111: getfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$FetchedIconData._height I
      // 114: aload 4
      // 116: getfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Request._iconFileType Ljava/lang/String;
      // 119: invokestatic net/rim/device/apps/internal/blackberryemail/folder/IconFetcher.getIconFilename (IIILjava/lang/String;)Ljava/lang/String;
      // 11c: astore 14
      // 11e: new java/lang/StringBuffer
      // 121: dup
      // 122: ldc_w "file://"
      // 125: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 128: astore 15
      // 12a: aload 15
      // 12c: aload 4
      // 12e: getfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Request._iconFolder Ljava/lang/String;
      // 131: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 134: pop
      // 135: aload 15
      // 137: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 13a: invokestatic net/rim/device/internal/io/file/FileUtilities.ensureDirectoryExists (Ljava/lang/String;)Z
      // 13d: pop
      // 13e: aload 15
      // 140: aload 14
      // 142: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 145: pop
      // 146: aconst_null
      // 147: astore 16
      // 149: aconst_null
      // 14a: astore 17
      // 14c: aload 15
      // 14e: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 151: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 154: checkcast javax/microedition/io/file/FileConnection
      // 157: astore 16
      // 159: aload 16
      // 15b: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 160: ifeq 16a
      // 163: aload 16
      // 165: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 16a: aload 16
      // 16c: invokeinterface javax/microedition/io/file/FileConnection.create ()V 1
      // 171: aload 16
      // 173: invokeinterface javax/microedition/io/file/FileConnection.openOutputStream ()Ljava/io/OutputStream; 1
      // 178: astore 17
      // 17a: aload 17
      // 17c: aload 13
      // 17e: invokevirtual java/io/OutputStream.write ([B)V
      // 181: aload 17
      // 183: ifnull 190
      // 186: aload 17
      // 188: invokevirtual java/io/OutputStream.close ()V
      // 18b: goto 190
      // 18e: astore 18
      // 190: aload 16
      // 192: ifnull 1ee
      // 195: aload 16
      // 197: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 19c: goto 1ee
      // 19f: astore 18
      // 1a1: goto 1ee
      // 1a4: astore 18
      // 1a6: aload 17
      // 1a8: ifnull 1b5
      // 1ab: aload 17
      // 1ad: invokevirtual java/io/OutputStream.close ()V
      // 1b0: goto 1b5
      // 1b3: astore 18
      // 1b5: aload 16
      // 1b7: ifnull 1ee
      // 1ba: aload 16
      // 1bc: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1c1: goto 1ee
      // 1c4: astore 18
      // 1c6: goto 1ee
      // 1c9: astore 19
      // 1cb: aload 17
      // 1cd: ifnull 1da
      // 1d0: aload 17
      // 1d2: invokevirtual java/io/OutputStream.close ()V
      // 1d5: goto 1da
      // 1d8: astore 20
      // 1da: aload 16
      // 1dc: ifnull 1eb
      // 1df: aload 16
      // 1e1: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1e6: goto 1eb
      // 1e9: astore 20
      // 1eb: aload 19
      // 1ed: athrow
      // 1ee: aload 13
      // 1f0: bipush 0
      // 1f1: aload 13
      // 1f3: arraylength
      // 1f4: invokestatic net/rim/device/api/system/EncodedImage.createEncodedImage ([BII)Lnet/rim/device/api/system/EncodedImage;
      // 1f7: astore 18
      // 1f9: aload 0
      // 1fa: getfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker.this$0 Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher;
      // 1fd: aload 18
      // 1ff: aload 8
      // 201: getfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$FetchedIconData._width I
      // 204: aload 8
      // 206: getfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$FetchedIconData._height I
      // 209: invokevirtual net/rim/device/apps/internal/blackberryemail/folder/IconFetcher.extractIconBitmaps (Lnet/rim/device/api/system/EncodedImage;II)[Lnet/rim/device/api/system/Bitmap;
      // 20c: astore 19
      // 20e: aload 19
      // 210: arraylength
      // 211: bipush 1
      // 212: if_icmplt 245
      // 215: aload 19
      // 217: bipush 0
      // 218: aaload
      // 219: astore 20
      // 21b: aload 19
      // 21d: arraylength
      // 21e: bipush 2
      // 220: if_icmplt 22c
      // 223: aload 19
      // 225: bipush 1
      // 226: aaload
      // 227: astore 21
      // 229: goto 230
      // 22c: aload 20
      // 22e: astore 21
      // 230: aload 0
      // 231: getfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker.this$0 Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher;
      // 234: aload 4
      // 236: getfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Request._appName Ljava/lang/String;
      // 239: aload 21
      // 23b: aload 20
      // 23d: invokespecial net/rim/device/apps/internal/blackberryemail/folder/IconFetcher.UpdateAppIconOnRibbon (Ljava/lang/String;Lnet/rim/device/api/system/Bitmap;Lnet/rim/device/api/system/Bitmap;)V
      // 240: goto 245
      // 243: astore 13
      // 245: aload 8
      // 247: aconst_null
      // 248: putfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$FetchedIconData._iconData [B
      // 24b: aconst_null
      // 24c: astore 8
      // 24e: iload 7
      // 250: ifne 273
      // 253: aload 4
      // 255: invokestatic java/lang/System.currentTimeMillis ()J
      // 258: aload 4
      // 25a: getfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Request._retryCount B
      // 25d: i2l
      // 25e: lload 1
      // 25f: lmul
      // 260: ladd
      // 261: putfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Request._nextRetryTime J
      // 264: goto 273
      // 267: getstatic net/rim/device/apps/internal/blackberryemail/folder/IconFetcher._requests Ljava/util/Vector;
      // 26a: aload 4
      // 26c: invokevirtual java/util/Vector.removeElement (Ljava/lang/Object;)Z
      // 26f: pop
      // 270: iinc 3 -1
      // 273: iinc 3 1
      // 276: goto 007
      // 279: aload 0
      // 27a: aload 0
      // 27b: astore 3
      // 27c: monitorenter
      // 27d: getstatic net/rim/device/apps/internal/blackberryemail/folder/IconFetcher._requests Ljava/util/Vector;
      // 280: invokevirtual java/util/Vector.size ()I
      // 283: ifle 28e
      // 286: aload 0
      // 287: lload 1
      // 288: invokevirtual java/lang/Object.wait (J)V
      // 28b: goto 29b
      // 28e: aload 0
      // 28f: sipush 1000
      // 292: i2l
      // 293: invokevirtual java/lang/Object.wait (J)V
      // 296: goto 29b
      // 299: astore 4
      // 29b: aload 3
      // 29c: monitorexit
      // 29d: goto 2a7
      // 2a0: astore 22
      // 2a2: aload 3
      // 2a3: monitorexit
      // 2a4: aload 22
      // 2a6: athrow
      // 2a7: getstatic net/rim/device/apps/internal/blackberryemail/folder/IconFetcher._requests Ljava/util/Vector;
      // 2aa: invokevirtual java/util/Vector.size ()I
      // 2ad: ifle 2b3
      // 2b0: goto 000
      // 2b3: aconst_null
      // 2b4: invokestatic net/rim/device/apps/internal/blackberryemail/folder/IconFetcher.access$602 (Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker;)Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker;
      // 2b7: pop
      // 2b8: return
      // try (173 -> 175): 176 null
      // try (179 -> 181): 182 null
      // try (153 -> 171): 184 null
      // try (187 -> 189): 190 null
      // try (193 -> 195): 196 null
      // try (153 -> 171): 198 null
      // try (184 -> 185): 198 null
      // try (201 -> 203): 204 null
      // try (207 -> 209): 210 null
      // try (198 -> 199): 198 null
      // try (118 -> 254): 255 null
      // try (284 -> 295): 296 null
      // try (284 -> 299): 300 null
      // try (300 -> 303): 300 null
   }

   private IconFetcher$Worker$FetchedIconData fetchIconData(IconFetcher$Worker$URL param1, IconFetcher$Request param2, int param3, int param4, int param5) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aconst_null
      // 001: astore 6
      // 003: aconst_null
      // 004: astore 7
      // 006: aconst_null
      // 007: astore 8
      // 009: iload 3
      // 00a: lookupswitch 82 2 0 26 2 54
      // 024: new java/lang/StringBuffer
      // 027: dup
      // 028: invokespecial java/lang/StringBuffer.<init> ()V
      // 02b: aload 1
      // 02c: invokevirtual net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$URL.toString ()Ljava/lang/String;
      // 02f: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 032: ldc_w ";connectionType=mds-public;deviceside=false"
      // 035: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 038: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 03b: astore 9
      // 03d: goto 075
      // 040: new java/lang/StringBuffer
      // 043: dup
      // 044: invokespecial java/lang/StringBuffer.<init> ()V
      // 047: aload 1
      // 048: invokevirtual net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$URL.toString ()Ljava/lang/String;
      // 04b: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 04e: ldc_w ";deviceside=true"
      // 051: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 054: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 057: astore 9
      // 059: goto 075
      // 05c: new java/lang/StringBuffer
      // 05f: dup
      // 060: invokespecial java/lang/StringBuffer.<init> ()V
      // 063: aload 1
      // 064: invokevirtual net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$URL.toString ()Ljava/lang/String;
      // 067: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 06a: ldc_w ";deviceside=false"
      // 06d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 070: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 073: astore 9
      // 075: aload 9
      // 077: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 07a: checkcast javax/microedition/io/HttpConnection
      // 07d: astore 6
      // 07f: new java/lang/StringBuffer
      // 082: dup
      // 083: ldc_w "BlackBerry"
      // 086: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 089: invokestatic net/rim/device/api/system/DeviceInfo.getDeviceName ()Ljava/lang/String;
      // 08c: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 08f: bipush 47
      // 091: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 094: invokestatic net/rim/device/api/system/ApplicationDescriptor.currentApplicationDescriptor ()Lnet/rim/device/api/system/ApplicationDescriptor;
      // 097: invokevirtual net/rim/device/api/system/ApplicationDescriptor.getVersion ()Ljava/lang/String;
      // 09a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 09d: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0a0: astore 10
      // 0a2: aload 6
      // 0a4: ldc_w "user-agent"
      // 0a7: aload 10
      // 0a9: invokeinterface javax/microedition/io/HttpConnection.setRequestProperty (Ljava/lang/String;Ljava/lang/String;)V 3
      // 0ae: aload 6
      // 0b0: ldc_w "profile"
      // 0b3: invokestatic net/rim/device/api/browser/util/UAProf.getDefaultUAProfURI ()Ljava/lang/String;
      // 0b6: invokeinterface javax/microedition/io/HttpConnection.setRequestProperty (Ljava/lang/String;Ljava/lang/String;)V 3
      // 0bb: aload 2
      // 0bc: getfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Request._serviceUID Ljava/lang/String;
      // 0bf: ifnull 0d0
      // 0c2: aload 6
      // 0c4: ldc_w "connectionuid"
      // 0c7: aload 2
      // 0c8: getfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Request._serviceUID Ljava/lang/String;
      // 0cb: invokeinterface javax/microedition/io/HttpConnection.setRequestProperty (Ljava/lang/String;Ljava/lang/String;)V 3
      // 0d0: aload 6
      // 0d2: invokeinterface javax/microedition/io/HttpConnection.getResponseCode ()I 1
      // 0d7: sipush 200
      // 0da: if_icmpeq 0e0
      // 0dd: goto 166
      // 0e0: aload 6
      // 0e2: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 0e7: astore 7
      // 0e9: aload 7
      // 0eb: invokestatic net/rim/device/api/io/IOUtilities.streamToBytes (Ljava/io/InputStream;)[B
      // 0ee: astore 11
      // 0f0: aload 11
      // 0f2: ifnull 166
      // 0f5: new net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$FetchedIconData
      // 0f8: dup
      // 0f9: aload 0
      // 0fa: aload 11
      // 0fc: bipush 1
      // 0fd: iload 4
      // 0ff: iload 5
      // 101: invokespecial net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$FetchedIconData.<init> (Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker;[BIII)V
      // 104: astore 8
      // 106: aload 6
      // 108: ldc_w "version"
      // 10b: invokeinterface javax/microedition/io/HttpConnection.getHeaderField (Ljava/lang/String;)Ljava/lang/String; 2
      // 110: astore 12
      // 112: aload 12
      // 114: ifnull 126
      // 117: aload 8
      // 119: aload 12
      // 11b: invokestatic java/lang/Integer.parseInt (Ljava/lang/String;)I
      // 11e: putfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$FetchedIconData._version I
      // 121: goto 126
      // 124: astore 13
      // 126: aload 6
      // 128: ldc_w "X-Rim-Icon-Width"
      // 12b: invokeinterface javax/microedition/io/HttpConnection.getHeaderField (Ljava/lang/String;)Ljava/lang/String; 2
      // 130: astore 12
      // 132: aload 12
      // 134: ifnull 146
      // 137: aload 8
      // 139: aload 12
      // 13b: invokestatic java/lang/Integer.parseInt (Ljava/lang/String;)I
      // 13e: putfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$FetchedIconData._width I
      // 141: goto 146
      // 144: astore 13
      // 146: aload 6
      // 148: ldc_w "X-Rim-Icon-Height"
      // 14b: invokeinterface javax/microedition/io/HttpConnection.getHeaderField (Ljava/lang/String;)Ljava/lang/String; 2
      // 150: astore 12
      // 152: aload 12
      // 154: ifnull 166
      // 157: aload 8
      // 159: aload 12
      // 15b: invokestatic java/lang/Integer.parseInt (Ljava/lang/String;)I
      // 15e: putfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker$FetchedIconData._height I
      // 161: goto 166
      // 164: astore 13
      // 166: aload 7
      // 168: ifnull 175
      // 16b: aload 7
      // 16d: invokevirtual java/io/InputStream.close ()V
      // 170: goto 175
      // 173: astore 9
      // 175: aload 6
      // 177: ifnull 1f8
      // 17a: aload 6
      // 17c: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 181: goto 1f8
      // 184: astore 9
      // 186: goto 1f8
      // 189: astore 9
      // 18b: aload 7
      // 18d: ifnull 19a
      // 190: aload 7
      // 192: invokevirtual java/io/InputStream.close ()V
      // 195: goto 19a
      // 198: astore 9
      // 19a: aload 6
      // 19c: ifnull 1f8
      // 19f: aload 6
      // 1a1: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1a6: goto 1f8
      // 1a9: astore 9
      // 1ab: goto 1f8
      // 1ae: astore 9
      // 1b0: aload 7
      // 1b2: ifnull 1bf
      // 1b5: aload 7
      // 1b7: invokevirtual java/io/InputStream.close ()V
      // 1ba: goto 1bf
      // 1bd: astore 9
      // 1bf: aload 6
      // 1c1: ifnull 1f8
      // 1c4: aload 6
      // 1c6: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1cb: goto 1f8
      // 1ce: astore 9
      // 1d0: goto 1f8
      // 1d3: astore 14
      // 1d5: aload 7
      // 1d7: ifnull 1e4
      // 1da: aload 7
      // 1dc: invokevirtual java/io/InputStream.close ()V
      // 1df: goto 1e4
      // 1e2: astore 15
      // 1e4: aload 6
      // 1e6: ifnull 1f5
      // 1e9: aload 6
      // 1eb: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1f0: goto 1f5
      // 1f3: astore 15
      // 1f5: aload 14
      // 1f7: athrow
      // 1f8: aload 8
      // 1fa: areturn
      // try (101 -> 105): 106 null
      // try (113 -> 117): 118 null
      // try (125 -> 129): 130 null
      // try (131 -> 135): 136 null
      // try (137 -> 141): 142 null
      // try (6 -> 131): 144 null
      // try (145 -> 149): 150 null
      // try (151 -> 155): 156 null
      // try (6 -> 131): 158 null
      // try (159 -> 163): 164 null
      // try (165 -> 169): 170 null
      // try (6 -> 131): 172 null
      // try (144 -> 145): 172 null
      // try (158 -> 159): 172 null
      // try (173 -> 177): 178 null
      // try (179 -> 183): 184 null
      // try (172 -> 173): 172 null
   }
}
