package net.rim.device.apps.internal.blackberryemail.folder;

import java.util.Vector;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.apps.api.ribbon.EntryPointDescriptor;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.internal.io.file.FileUtilities;

final class IconFetcher {
   private static final String ICON_FOLDER;
   private static final String CONNECTION_UID;
   private static final String ICON_WIDTH;
   private static final String ICON_HEIGHT;
   private static final String ITEM;
   private static final String PROFILE;
   private static final String THEME;
   private static final String USER_AGENT;
   private static final String VERSION;
   private static final String RESP_ICON_WIDTH;
   private static final String RESP_ICON_HEIGHT;
   private static final int CONNECTION_PUBLIC_MDS;
   private static final int CONNECTION_PRIVATE_MDS;
   private static final int CONNECTION_NO_MDS;
   private static Vector _requests;
   private static IconFetcher _instance;
   private static IconFetcher$Worker _worker;

   private IconFetcher() {
   }

   public static final IconFetcher getInstance() {
      if (_instance == null) {
         _instance = new IconFetcher();
      }

      return _instance;
   }

   private static final String getIconFilename(int version, int width, int height, String fileType) {
      StringBuffer iconFilename = (StringBuffer)(new Object());
      iconFilename.append(version);
      iconFilename.append('-').append(width);
      iconFilename.append('-').append(height);
      iconFilename.append(fileType);
      return iconFilename.toString();
   }

   private static final String getIconFileType() {
      return ".png";
   }

   private static final String getIconContentType() {
      return "image/png";
   }

   private static final void getIconParameters(String fileName, IconFetcher$IconParameters params) {
      fileName = fileName.substring(0, fileName.indexOf(46));
      StringTokenizer tokenizer = (StringTokenizer)(new Object(fileName, '-'));
      params._version = Integer.parseInt(tokenizer.nextToken());
      params._width = Integer.parseInt(tokenizer.nextToken());
      params._height = Integer.parseInt(tokenizer.nextToken());
   }

   private static final String getIconFolder(String serviceUid) {
      StringBuffer iconFileName = (StringBuffer)(new Object("/store/appdata/rim/bda/icons/"));
      iconFileName.append(serviceUid);
      iconFileName.append('/');
      return iconFileName.toString();
   }

   public final void fetchIcons(String param1, String param2, String param3, int param4) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 0
      // 001: istore 5
      // 003: aload 1
      // 004: invokestatic net/rim/device/apps/internal/blackberryemail/folder/IconFetcher.getIconFolder (Ljava/lang/String;)Ljava/lang/String;
      // 007: astore 6
      // 009: invokestatic net/rim/device/apps/internal/blackberryemail/folder/IconFetcher.getIconFileType ()Ljava/lang/String;
      // 00c: astore 7
      // 00e: aconst_null
      // 00f: astore 8
      // 011: new java/lang/Object
      // 014: dup
      // 015: ldc_w "file://"
      // 018: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 01b: aload 6
      // 01d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 020: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 023: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 026: checkcast java/lang/Object
      // 029: astore 8
      // 02b: aload 8
      // 02d: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 032: ifne 038
      // 035: goto 11f
      // 038: invokestatic net/rim/device/api/ui/theme/ThemeManager.getActiveTheme ()Lnet/rim/device/api/ui/theme/Theme;
      // 03b: astore 9
      // 03d: aload 9
      // 03f: invokevirtual net/rim/device/api/ui/theme/Theme.getRibbonIconWidth ()I
      // 042: istore 10
      // 044: aload 9
      // 046: invokevirtual net/rim/device/api/ui/theme/Theme.getRibbonIconHeight ()I
      // 049: istore 11
      // 04b: aload 8
      // 04d: invokeinterface javax/microedition/io/file/FileConnection.list ()Ljava/util/Enumeration; 1
      // 052: astore 12
      // 054: aload 12
      // 056: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 05b: ifne 061
      // 05e: goto 11f
      // 061: aload 12
      // 063: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 068: checkcast java/lang/Object
      // 06b: astore 13
      // 06d: aload 13
      // 06f: aload 7
      // 071: invokevirtual java/lang/String.endsWith (Ljava/lang/String;)Z
      // 074: ifne 07a
      // 077: goto 054
      // 07a: new net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$IconParameters
      // 07d: dup
      // 07e: invokespecial net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$IconParameters.<init> ()V
      // 081: astore 14
      // 083: aload 13
      // 085: aload 14
      // 087: invokestatic net/rim/device/apps/internal/blackberryemail/folder/IconFetcher.getIconParameters (Ljava/lang/String;Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher$IconParameters;)V
      // 08a: aload 14
      // 08c: getfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$IconParameters._version I
      // 08f: iload 4
      // 091: if_icmpeq 09a
      // 094: bipush 1
      // 095: istore 5
      // 097: goto 054
      // 09a: iload 10
      // 09c: aload 14
      // 09e: getfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$IconParameters._width I
      // 0a1: if_icmpne 054
      // 0a4: iload 11
      // 0a6: aload 14
      // 0a8: getfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$IconParameters._height I
      // 0ab: if_icmpne 054
      // 0ae: new java/lang/Object
      // 0b1: dup
      // 0b2: ldc_w "file://"
      // 0b5: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 0b8: aload 6
      // 0ba: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0bd: aload 13
      // 0bf: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0c2: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0c5: invokestatic net/rim/device/internal/io/file/FileUtilities.getEncodedImage (Ljava/lang/String;)Lnet/rim/device/api/system/EncodedImage;
      // 0c8: astore 15
      // 0ca: aload 15
      // 0cc: ifnull 054
      // 0cf: aload 0
      // 0d0: aload 15
      // 0d2: aload 14
      // 0d4: getfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$IconParameters._width I
      // 0d7: aload 14
      // 0d9: getfield net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$IconParameters._height I
      // 0dc: invokevirtual net/rim/device/apps/internal/blackberryemail/folder/IconFetcher.extractIconBitmaps (Lnet/rim/device/api/system/EncodedImage;II)[Lnet/rim/device/api/system/Bitmap;
      // 0df: astore 16
      // 0e1: aload 16
      // 0e3: arraylength
      // 0e4: bipush 1
      // 0e5: if_icmpge 0eb
      // 0e8: goto 054
      // 0eb: aload 16
      // 0ed: bipush 0
      // 0ee: aaload
      // 0ef: astore 17
      // 0f1: aload 16
      // 0f3: arraylength
      // 0f4: bipush 2
      // 0f6: if_icmplt 102
      // 0f9: aload 16
      // 0fb: bipush 1
      // 0fc: aaload
      // 0fd: astore 18
      // 0ff: goto 106
      // 102: aload 17
      // 104: astore 18
      // 106: aload 0
      // 107: aload 2
      // 108: aload 18
      // 10a: aload 17
      // 10c: invokespecial net/rim/device/apps/internal/blackberryemail/folder/IconFetcher.UpdateAppIconOnRibbon (Ljava/lang/String;Lnet/rim/device/api/system/Bitmap;Lnet/rim/device/api/system/Bitmap;)V
      // 10f: aload 8
      // 111: ifnull 11e
      // 114: aload 8
      // 116: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 11b: return
      // 11c: astore 19
      // 11e: return
      // 11f: aload 8
      // 121: ifnull 175
      // 124: aload 8
      // 126: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 12b: goto 175
      // 12e: astore 9
      // 130: goto 175
      // 133: astore 9
      // 135: aload 8
      // 137: ifnull 175
      // 13a: aload 8
      // 13c: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 141: goto 175
      // 144: astore 9
      // 146: goto 175
      // 149: astore 9
      // 14b: aload 8
      // 14d: ifnull 175
      // 150: aload 8
      // 152: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 157: goto 175
      // 15a: astore 9
      // 15c: goto 175
      // 15f: astore 20
      // 161: aload 8
      // 163: ifnull 172
      // 166: aload 8
      // 168: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 16d: goto 172
      // 170: astore 21
      // 172: aload 20
      // 174: athrow
      // 175: iload 5
      // 177: ifeq 17e
      // 17a: aload 1
      // 17b: invokestatic net/rim/device/apps/internal/blackberryemail/folder/IconFetcher.deleteIcons (Ljava/lang/String;)V
      // 17e: new java/lang/Object
      // 181: dup
      // 182: aload 3
      // 183: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 186: astore 9
      // 188: new net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Request
      // 18b: dup
      // 18c: aload 2
      // 18d: aconst_null
      // 18e: aload 3
      // 18f: iload 4
      // 191: aload 6
      // 193: aload 7
      // 195: invokespecial net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Request.<init> (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)V
      // 198: astore 10
      // 19a: getstatic net/rim/device/apps/internal/blackberryemail/folder/IconFetcher._requests Ljava/util/Vector;
      // 19d: ifnonnull 1aa
      // 1a0: new java/lang/Object
      // 1a3: dup
      // 1a4: invokespecial java/util/Vector.<init> ()V
      // 1a7: putstatic net/rim/device/apps/internal/blackberryemail/folder/IconFetcher._requests Ljava/util/Vector;
      // 1aa: getstatic net/rim/device/apps/internal/blackberryemail/folder/IconFetcher._requests Ljava/util/Vector;
      // 1ad: aload 10
      // 1af: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 1b2: getstatic net/rim/device/apps/internal/blackberryemail/folder/IconFetcher._worker Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker;
      // 1b5: ifnonnull 1ca
      // 1b8: new net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker
      // 1bb: dup
      // 1bc: aload 0
      // 1bd: invokespecial net/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker.<init> (Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher;)V
      // 1c0: putstatic net/rim/device/apps/internal/blackberryemail/folder/IconFetcher._worker Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker;
      // 1c3: getstatic net/rim/device/apps/internal/blackberryemail/folder/IconFetcher._worker Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker;
      // 1c6: invokevirtual java/lang/Thread.start ()V
      // 1c9: return
      // 1ca: getstatic net/rim/device/apps/internal/blackberryemail/folder/IconFetcher._worker Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker;
      // 1cd: dup
      // 1ce: astore 11
      // 1d0: monitorenter
      // 1d1: getstatic net/rim/device/apps/internal/blackberryemail/folder/IconFetcher._worker Lnet/rim/device/apps/internal/blackberryemail/folder/IconFetcher$Worker;
      // 1d4: invokevirtual java/lang/Object.notify ()V
      // 1d7: aload 11
      // 1d9: monitorexit
      // 1da: return
      // 1db: astore 22
      // 1dd: aload 11
      // 1df: monitorexit
      // 1e0: aload 22
      // 1e2: athrow
      // try (117 -> 119): 120 null
      // try (124 -> 126): 127 null
      // try (9 -> 115): 129 null
      // try (132 -> 134): 135 null
      // try (9 -> 115): 137 null
      // try (140 -> 142): 143 null
      // try (9 -> 115): 145 null
      // try (129 -> 130): 145 null
      // try (137 -> 138): 145 null
      // try (148 -> 150): 151 null
      // try (145 -> 146): 145 null
      // try (196 -> 200): 201 null
      // try (201 -> 204): 201 null
   }

   public final Bitmap[] extractIconBitmaps(EncodedImage imageSet, int width, int height) {
      int numIcons = imageSet.getWidth() / width;
      Bitmap[] iconBitmaps = new Object[numIcons];

      for (int i = 0; i < numIcons; i++) {
         Bitmap target = (Bitmap)(new Object(width, height));
         int[] argb = new int[width * height];
         imageSet.getBitmap().getARGB(argb, 0, width, i * width, 0, width, height);
         target.setARGB(argb, 0, width, 0, 0, width, height);
         iconBitmaps[i] = target;
      }

      return iconBitmaps;
   }

   private final void UpdateAppIconOnRibbon(String appName, Bitmap iconInFocus, Bitmap iconOutOfFocus) {
      RibbonLauncher ribbon = RibbonLauncher.getInstance();
      if (ribbon != null) {
         EntryPointDescriptor entryPointDesc = ribbon.getRegisteredAction(appName);
         if (entryPointDesc instanceof Object) {
            ApplicationEntryPoint appEntryPoint = (ApplicationEntryPoint)entryPointDesc;
            appEntryPoint.set(10, iconInFocus);
            appEntryPoint.set(4, iconOutOfFocus);
            ribbon.updateRegisteredAction(appName);
         }
      }
   }

   static final void deleteIcons(String serviceUid) {
      try {
         FileUtilities.deleteDirectory(getIconFolder(serviceUid));
      } finally {
         return;
      }
   }

   static final void deleteUnusedIcons() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aconst_null
      // 01: astore 0
      // 02: ldc_w "/store/appdata/rim/bda/icons/"
      // 05: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 08: checkcast java/lang/Object
      // 0b: astore 0
      // 0c: aload 0
      // 0d: invokeinterface javax/microedition/io/file/FileConnection.list ()Ljava/util/Enumeration; 1
      // 12: astore 1
      // 13: invokestatic net/rim/device/api/servicebook/ServiceBook.getSB ()Lnet/rim/device/api/servicebook/ServiceBook;
      // 16: astore 2
      // 17: aload 1
      // 18: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 1d: ifeq 5c
      // 20: aload 1
      // 21: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 26: checkcast java/lang/Object
      // 29: astore 4
      // 2b: aload 4
      // 2d: invokestatic net/rim/device/internal/io/file/FileUtilities.isDirectory (Ljava/lang/String;)Z
      // 30: ifeq 17
      // 33: aload 2
      // 34: aload 4
      // 36: bipush 0
      // 37: aload 4
      // 39: invokevirtual java/lang/String.length ()I
      // 3c: bipush 1
      // 3d: isub
      // 3e: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 41: ldc_w "CMIME"
      // 44: invokevirtual net/rim/device/api/servicebook/ServiceBook.getRecordByUidAndCid (Ljava/lang/String;Ljava/lang/String;)Lnet/rim/device/api/servicebook/ServiceRecord;
      // 47: astore 3
      // 48: aload 3
      // 49: ifnonnull 17
      // 4c: aload 4
      // 4e: invokestatic net/rim/device/apps/internal/blackberryemail/folder/IconFetcher.getIconFolder (Ljava/lang/String;)Ljava/lang/String;
      // 51: invokestatic net/rim/device/internal/io/file/FileUtilities.deleteDirectory (Ljava/lang/String;)V
      // 54: goto 17
      // 57: astore 5
      // 59: goto 17
      // 5c: aload 0
      // 5d: ifnull 99
      // 60: aload 0
      // 61: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 66: return
      // 67: astore 1
      // 68: return
      // 69: astore 1
      // 6a: aload 0
      // 6b: ifnull 99
      // 6e: aload 0
      // 6f: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 74: return
      // 75: astore 1
      // 76: return
      // 77: astore 1
      // 78: aload 0
      // 79: ifnull 99
      // 7c: aload 0
      // 7d: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 82: return
      // 83: astore 1
      // 84: return
      // 85: astore 6
      // 87: aload 0
      // 88: ifnull 96
      // 8b: aload 0
      // 8c: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 91: goto 96
      // 94: astore 7
      // 96: aload 6
      // 98: athrow
      // 99: return
      // try (34 -> 37): 38 null
      // try (42 -> 44): 45 null
      // try (2 -> 40): 47 null
      // try (50 -> 52): 53 null
      // try (2 -> 40): 55 null
      // try (58 -> 60): 61 null
      // try (2 -> 40): 63 null
      // try (47 -> 48): 63 null
      // try (55 -> 56): 63 null
      // try (66 -> 68): 69 null
      // try (63 -> 64): 63 null
   }

   static final IconFetcher$Worker access$602(IconFetcher$Worker x0) {
      _worker = x0;
      return x0;
   }
}
