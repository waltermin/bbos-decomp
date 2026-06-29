package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.PopupScreen;

final class PropertiesDialog extends PopupScreen {
   static int[][] tagToLabel = new int[][]{
      {3, 4, 1, 51, -804651005, 131, 132, 130, 712179968, 1864070467, 16826221, 1701539702},
      {131, 132, 130, 712179968, 1864070467, 16826221, 1701539702, 1870004480, 1849779563, 56711012, 1870004480, 290219371}
   };

   public PropertiesDialog(Object field) {
      super(new DialogFieldManager(299067162755072L), 299067162755072L);
      this.setItem(field);
   }

   public final void setItem(Object param1) {
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
      // 000: bipush 0
      // 001: istore 2
      // 002: ldc_w ""
      // 005: astore 3
      // 006: ldc_w ""
      // 009: astore 4
      // 00b: ldc_w ""
      // 00e: astore 5
      // 010: bipush -1
      // 012: istore 6
      // 014: aload 1
      // 015: dup
      // 016: instanceof net/rim/device/apps/internal/explorer/file/FileItemField
      // 019: ifne 020
      // 01c: pop
      // 01d: goto 049
      // 020: checkcast net/rim/device/apps/internal/explorer/file/FileItemField
      // 023: astore 7
      // 025: aload 7
      // 027: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.isDirectory ()Z
      // 02a: istore 2
      // 02b: aload 7
      // 02d: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getFullPath ()Ljava/lang/String;
      // 030: astore 3
      // 031: aload 7
      // 033: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getPath ()Ljava/lang/String;
      // 036: astore 4
      // 038: aload 7
      // 03a: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getName ()Ljava/lang/String;
      // 03d: astore 5
      // 03f: aload 7
      // 041: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getMediaType ()I
      // 044: istore 6
      // 046: goto 089
      // 049: aload 1
      // 04a: dup
      // 04b: instanceof net/rim/device/apps/internal/explorer/MediaLibrary/MediaInfo
      // 04e: ifne 055
      // 051: pop
      // 052: goto 089
      // 055: checkcast net/rim/device/apps/internal/explorer/MediaLibrary/MediaInfo
      // 058: astore 7
      // 05a: aload 7
      // 05c: invokeinterface net/rim/device/apps/internal/explorer/MediaLibrary/MediaInfo.getLocation ()Ljava/lang/String; 1
      // 061: astore 3
      // 062: aload 3
      // 063: bipush 47
      // 065: invokevirtual java/lang/String.lastIndexOf (I)I
      // 068: istore 8
      // 06a: iload 8
      // 06c: iflt 078
      // 06f: aload 3
      // 070: bipush 0
      // 071: iload 8
      // 073: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 076: astore 4
      // 078: aload 3
      // 079: invokestatic net/rim/device/internal/io/file/FileUtilities.isDirectory (Ljava/lang/String;)Z
      // 07c: istore 2
      // 07d: aload 3
      // 07e: invokestatic net/rim/device/internal/io/file/FileUtilities.getName (Ljava/lang/String;)Ljava/lang/String;
      // 081: astore 5
      // 083: aload 3
      // 084: invokestatic net/rim/device/api/io/MIMETypeAssociations.getMediaType (Ljava/lang/String;)I
      // 087: istore 6
      // 089: aload 0
      // 08a: invokevirtual net/rim/device/api/ui/Screen.getDelegate ()Lnet/rim/device/api/ui/Manager;
      // 08d: checkcast net/rim/device/api/ui/container/DialogFieldManager
      // 090: astore 7
      // 092: aload 7
      // 094: new net/rim/device/api/ui/component/NullField
      // 097: dup
      // 098: invokespecial net/rim/device/api/ui/component/NullField.<init> ()V
      // 09b: invokevirtual net/rim/device/api/ui/container/DialogFieldManager.addCustomField (Lnet/rim/device/api/ui/Field;)V
      // 09e: new net/rim/device/api/ui/component/LabelField
      // 0a1: dup
      // 0a2: aload 3
      // 0a3: invokestatic net/rim/device/internal/io/file/FileUtilities.getDisplayName (Ljava/lang/String;)Ljava/lang/String;
      // 0a6: invokespecial net/rim/device/api/ui/component/LabelField.<init> (Ljava/lang/Object;)V
      // 0a9: astore 8
      // 0ab: aload 7
      // 0ad: aload 8
      // 0af: invokevirtual net/rim/device/api/ui/container/DialogFieldManager.addCustomField (Lnet/rim/device/api/ui/Field;)V
      // 0b2: new net/rim/device/api/ui/component/LabelField
      // 0b5: dup
      // 0b6: invokespecial net/rim/device/api/ui/component/LabelField.<init> ()V
      // 0b9: astore 9
      // 0bb: aload 7
      // 0bd: aload 9
      // 0bf: invokevirtual net/rim/device/api/ui/container/DialogFieldManager.addCustomField (Lnet/rim/device/api/ui/Field;)V
      // 0c2: bipush 0
      // 0c3: istore 10
      // 0c5: bipush 0
      // 0c6: istore 11
      // 0c8: iload 2
      // 0c9: ifeq 0e5
      // 0cc: aload 3
      // 0cd: invokestatic net/rim/device/internal/io/file/FileUtilities.isRoot (Ljava/lang/String;)Z
      // 0d0: istore 11
      // 0d2: iload 11
      // 0d4: ifeq 0de
      // 0d7: bipush 74
      // 0d9: istore 10
      // 0db: goto 13c
      // 0de: bipush 68
      // 0e0: istore 10
      // 0e2: goto 13c
      // 0e5: iload 6
      // 0e7: tableswitch 45 0 7 85 45 52 59 66 73 85 80
      // 114: bipush 69
      // 116: istore 10
      // 118: goto 13c
      // 11b: bipush 70
      // 11d: istore 10
      // 11f: goto 13c
      // 122: bipush 71
      // 124: istore 10
      // 126: goto 13c
      // 129: bipush 72
      // 12b: istore 10
      // 12d: goto 13c
      // 130: bipush 73
      // 132: istore 10
      // 134: goto 13c
      // 137: sipush 152
      // 13a: istore 10
      // 13c: iload 10
      // 13e: ifeq 14b
      // 141: aload 9
      // 143: iload 10
      // 145: invokestatic net/rim/device/apps/internal/explorer/file/resource/ExplorerResources.getString (I)Ljava/lang/String;
      // 148: invokevirtual net/rim/device/api/ui/component/LabelField.setText (Ljava/lang/Object;)V
      // 14b: aconst_null
      // 14c: astore 12
      // 14e: aload 1
      // 14f: dup
      // 150: instanceof net/rim/device/apps/internal/explorer/file/FileItemField
      // 153: ifne 15a
      // 156: pop
      // 157: goto 165
      // 15a: checkcast net/rim/device/apps/internal/explorer/file/FileItemField
      // 15d: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getFileConnection ()Ljavax/microedition/io/file/FileConnection;
      // 160: astore 12
      // 162: goto 175
      // 165: aload 1
      // 166: instanceof net/rim/device/apps/internal/explorer/MediaLibrary/MediaInfo
      // 169: ifeq 175
      // 16c: aload 3
      // 16d: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 170: checkcast javax/microedition/io/file/FileConnection
      // 173: astore 12
      // 175: bipush -1
      // 177: i2l
      // 178: lstore 13
      // 17a: aload 12
      // 17c: ifnonnull 182
      // 17f: goto 349
      // 182: iload 2
      // 183: ifne 189
      // 186: goto 30b
      // 189: iload 11
      // 18b: ifne 19b
      // 18e: aload 12
      // 190: bipush 1
      // 191: invokeinterface javax/microedition/io/file/FileConnection.directorySize (Z)J 2
      // 196: lstore 13
      // 198: goto 349
      // 19b: aload 12
      // 19d: invokeinterface javax/microedition/io/file/FileConnection.usedSize ()J 1
      // 1a2: lstore 15
      // 1a4: aload 12
      // 1a6: invokeinterface javax/microedition/io/file/FileConnection.totalSize ()J 1
      // 1ab: lstore 17
      // 1ad: new net/rim/device/api/ui/container/VerticalFieldManager
      // 1b0: dup
      // 1b1: ldc2_w 299067162755072
      // 1b4: invokespecial net/rim/device/api/ui/container/VerticalFieldManager.<init> (J)V
      // 1b7: astore 19
      // 1b9: lload 15
      // 1bb: l2d
      // 1bc: lload 17
      // 1be: l2d
      // 1bf: ddiv
      // 1c0: nop
      // 1c1: ldc2_w 4636737291354636288
      // 1c4: dmul
      // 1c5: nop
      // 1c6: dstore 20
      // 1c8: nop
      // 1c9: dload 20
      // 1cb: invokestatic java/lang/Double.toString (D)Ljava/lang/String;
      // 1ce: astore 22
      // 1d0: aload 22
      // 1d2: bipush 46
      // 1d4: invokevirtual java/lang/String.indexOf (I)I
      // 1d7: istore 23
      // 1d9: iload 23
      // 1db: bipush -1
      // 1dd: if_icmpeq 1fa
      // 1e0: iload 23
      // 1e2: bipush 3
      // 1e4: iadd
      // 1e5: aload 22
      // 1e7: invokevirtual java/lang/String.length ()I
      // 1ea: if_icmpgt 1fa
      // 1ed: aload 22
      // 1ef: bipush 0
      // 1f0: iload 23
      // 1f2: bipush 3
      // 1f4: iadd
      // 1f5: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 1f8: astore 22
      // 1fa: aload 19
      // 1fc: new net/rim/device/api/ui/component/LabelField
      // 1ff: dup
      // 200: new java/lang/StringBuffer
      // 203: dup
      // 204: invokespecial java/lang/StringBuffer.<init> ()V
      // 207: aload 22
      // 209: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 20c: ldc_w "% "
      // 20f: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 212: bipush 75
      // 214: invokestatic net/rim/device/apps/internal/explorer/file/resource/ExplorerResources.getString (I)Ljava/lang/String;
      // 217: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 21a: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 21d: ldc2_w 8589934592
      // 220: invokespecial net/rim/device/api/ui/component/LabelField.<init> (Ljava/lang/Object;J)V
      // 223: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // 226: aload 19
      // 228: new net/rim/device/api/ui/component/LabelField
      // 22b: dup
      // 22c: new java/lang/StringBuffer
      // 22f: dup
      // 230: invokespecial java/lang/StringBuffer.<init> ()V
      // 233: lload 15
      // 235: invokestatic net/rim/device/internal/io/file/FileUtilities.sizeToString (J)Ljava/lang/String;
      // 238: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 23b: bipush 32
      // 23d: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 240: bipush 77
      // 242: invokestatic net/rim/device/apps/internal/explorer/file/resource/ExplorerResources.getString (I)Ljava/lang/String;
      // 245: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 248: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 24b: ldc2_w 8589934592
      // 24e: invokespecial net/rim/device/api/ui/component/LabelField.<init> (Ljava/lang/Object;J)V
      // 251: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // 254: aload 19
      // 256: new net/rim/device/api/ui/component/LabelField
      // 259: dup
      // 25a: new java/lang/StringBuffer
      // 25d: dup
      // 25e: invokespecial java/lang/StringBuffer.<init> ()V
      // 261: lload 17
      // 263: invokestatic net/rim/device/internal/io/file/FileUtilities.sizeToString (J)Ljava/lang/String;
      // 266: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 269: bipush 32
      // 26b: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 26e: bipush 76
      // 270: invokestatic net/rim/device/apps/internal/explorer/file/resource/ExplorerResources.getString (I)Ljava/lang/String;
      // 273: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 276: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 279: ldc2_w 8589934592
      // 27c: invokespecial net/rim/device/api/ui/component/LabelField.<init> (Ljava/lang/Object;J)V
      // 27f: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // 282: bipush 0
      // 283: nop
      // 284: dload 20
      // 286: d2i
      // 287: bipush 100
      // 289: invokestatic net/rim/device/api/util/MathUtilities.clamp (III)I
      // 28c: istore 24
      // 28e: new net/rim/device/api/ui/chart/ChartField
      // 291: dup
      // 292: invokespecial net/rim/device/api/ui/chart/ChartField.<init> ()V
      // 295: astore 25
      // 297: new net/rim/device/api/ui/chart/PieRenderer
      // 29a: dup
      // 29b: aload 25
      // 29d: invokespecial net/rim/device/api/ui/chart/PieRenderer.<init> (Lnet/rim/device/api/ui/chart/ChartField;)V
      // 2a0: astore 26
      // 2a2: new net/rim/device/api/ui/chart/XYDataset
      // 2a5: dup
      // 2a6: invokespecial net/rim/device/api/ui/chart/XYDataset.<init> ()V
      // 2a9: astore 27
      // 2ab: aload 27
      // 2ad: ldc_w 16766720
      // 2b0: iload 24
      // 2b2: invokevirtual net/rim/device/api/ui/chart/XYDataset.add (II)V
      // 2b5: aload 27
      // 2b7: ldc_w 32768
      // 2ba: bipush 100
      // 2bc: iload 24
      // 2be: isub
      // 2bf: invokevirtual net/rim/device/api/ui/chart/XYDataset.add (II)V
      // 2c2: aload 26
      // 2c4: aload 27
      // 2c6: invokevirtual net/rim/device/api/ui/chart/ChartRenderer.setDataset (Lnet/rim/device/api/ui/chart/XYDataset;)V
      // 2c9: aload 25
      // 2cb: aload 26
      // 2cd: invokevirtual net/rim/device/api/ui/chart/ChartField.addRenderer (Lnet/rim/device/api/ui/chart/ChartRenderer;)V
      // 2d0: aload 19
      // 2d2: invokevirtual net/rim/device/api/ui/container/VerticalFieldManager.getPreferredHeight ()I
      // 2d5: bipush 32
      // 2d7: invokestatic java/lang/Math.max (II)I
      // 2da: istore 28
      // 2dc: new net/rim/device/api/ui/container/HorizontalFieldManager
      // 2df: dup
      // 2e0: invokespecial net/rim/device/api/ui/container/HorizontalFieldManager.<init> ()V
      // 2e3: astore 29
      // 2e5: aload 26
      // 2e7: iload 28
      // 2e9: invokevirtual net/rim/device/api/ui/chart/ChartRenderer.setPreferredHeight (I)V
      // 2ec: aload 26
      // 2ee: iload 28
      // 2f0: invokevirtual net/rim/device/api/ui/chart/ChartRenderer.setPreferredWidth (I)V
      // 2f3: aload 29
      // 2f5: aload 25
      // 2f7: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // 2fa: aload 29
      // 2fc: aload 19
      // 2fe: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // 301: aload 7
      // 303: aload 29
      // 305: invokevirtual net/rim/device/api/ui/container/DialogFieldManager.addCustomField (Lnet/rim/device/api/ui/Field;)V
      // 308: goto 349
      // 30b: aload 12
      // 30d: dup
      // 30e: instanceof net/rim/device/api/io/file/ExtendedFileConnection
      // 311: ifne 318
      // 314: pop
      // 315: goto 325
      // 318: checkcast net/rim/device/api/io/file/ExtendedFileConnection
      // 31b: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.rawFileSize ()J 1
      // 320: lstore 13
      // 322: goto 32e
      // 325: aload 12
      // 327: invokeinterface javax/microedition/io/file/FileConnection.fileSize ()J 1
      // 32c: lstore 13
      // 32e: aload 7
      // 330: new net/rim/device/api/ui/component/DateField
      // 333: dup
      // 334: bipush 66
      // 336: invokestatic net/rim/device/apps/internal/explorer/file/resource/ExplorerResources.getString (I)Ljava/lang/String;
      // 339: aload 12
      // 33b: invokeinterface javax/microedition/io/file/FileConnection.lastModified ()J 1
      // 340: ldc2_w 36028797018964016
      // 343: invokespecial net/rim/device/api/ui/component/DateField.<init> (Ljava/lang/String;JJ)V
      // 346: invokevirtual net/rim/device/api/ui/container/DialogFieldManager.addCustomField (Lnet/rim/device/api/ui/Field;)V
      // 349: lload 13
      // 34b: bipush 0
      // 34c: i2l
      // 34d: lcmp
      // 34e: iflt 377
      // 351: aload 7
      // 353: new net/rim/device/apps/api/ui/LeftRightFieldManager
      // 356: dup
      // 357: new net/rim/device/api/ui/component/LabelField
      // 35a: dup
      // 35b: bipush 67
      // 35d: invokestatic net/rim/device/apps/internal/explorer/file/resource/ExplorerResources.getString (I)Ljava/lang/String;
      // 360: invokespecial net/rim/device/api/ui/component/LabelField.<init> (Ljava/lang/Object;)V
      // 363: new net/rim/device/api/ui/component/LabelField
      // 366: dup
      // 367: lload 13
      // 369: invokestatic net/rim/device/internal/io/file/FileUtilities.sizeToString (J)Ljava/lang/String;
      // 36c: invokespecial net/rim/device/api/ui/component/LabelField.<init> (Ljava/lang/Object;)V
      // 36f: invokespecial net/rim/device/apps/api/ui/LeftRightFieldManager.<init> (Lnet/rim/device/api/ui/Field;Lnet/rim/device/api/ui/Field;)V
      // 372: bipush 2
      // 374: invokevirtual net/rim/device/api/ui/container/DialogFieldManager.insertCustomField (Lnet/rim/device/api/ui/Field;I)V
      // 377: iload 6
      // 379: bipush 2
      // 37b: if_icmpeq 381
      // 37e: goto 3f2
      // 381: aload 4
      // 383: invokestatic net/rim/device/internal/io/file/FileUtilities.makeFileURL (Ljava/lang/String;)Ljava/lang/String;
      // 386: invokestatic net/rim/device/internal/io/file/MetaDataFile.getOrCreate (Ljava/lang/String;)Lnet/rim/device/internal/io/file/MetaDataFile;
      // 389: astore 15
      // 38b: getstatic net/rim/device/apps/internal/explorer/file/menu/PropertiesDialog.tagToLabel [[I
      // 38e: bipush 0
      // 38f: aaload
      // 390: arraylength
      // 391: istore 16
      // 393: iinc 16 -1
      // 396: iload 16
      // 398: iflt 3f2
      // 39b: aload 15
      // 39d: aload 5
      // 39f: getstatic net/rim/device/apps/internal/explorer/file/menu/PropertiesDialog.tagToLabel [[I
      // 3a2: bipush 0
      // 3a3: aaload
      // 3a4: iload 16
      // 3a6: iaload
      // 3a7: invokevirtual net/rim/device/internal/io/file/MetaDataFile.getMetadataFromCache (Ljava/lang/String;I)Ljava/lang/Object;
      // 3aa: astore 17
      // 3ac: aload 17
      // 3ae: instanceof java/lang/String
      // 3b1: ifeq 393
      // 3b4: new net/rim/device/api/ui/container/FlowFieldManager
      // 3b7: dup
      // 3b8: ldc2_w 1152921504606846984
      // 3bb: invokespecial net/rim/device/api/ui/container/FlowFieldManager.<init> (J)V
      // 3be: astore 18
      // 3c0: aload 18
      // 3c2: new net/rim/device/api/ui/component/LabelField
      // 3c5: dup
      // 3c6: getstatic net/rim/device/apps/internal/explorer/file/menu/PropertiesDialog.tagToLabel [[I
      // 3c9: bipush 1
      // 3ca: aaload
      // 3cb: iload 16
      // 3cd: iaload
      // 3ce: invokestatic net/rim/device/apps/internal/explorer/file/resource/ExplorerResources.getString (I)Ljava/lang/String;
      // 3d1: invokespecial net/rim/device/api/ui/component/LabelField.<init> (Ljava/lang/Object;)V
      // 3d4: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // 3d7: aload 18
      // 3d9: new net/rim/device/api/ui/component/LabelField
      // 3dc: dup
      // 3dd: aload 17
      // 3df: checkcast java/lang/String
      // 3e2: invokespecial net/rim/device/api/ui/component/LabelField.<init> (Ljava/lang/Object;)V
      // 3e5: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // 3e8: aload 7
      // 3ea: aload 18
      // 3ec: invokevirtual net/rim/device/api/ui/container/DialogFieldManager.addCustomField (Lnet/rim/device/api/ui/Field;)V
      // 3ef: goto 393
      // 3f2: aload 7
      // 3f4: new net/rim/device/api/ui/component/NullField
      // 3f7: dup
      // 3f8: invokespecial net/rim/device/api/ui/component/NullField.<init> ()V
      // 3fb: invokevirtual net/rim/device/api/ui/container/DialogFieldManager.addCustomField (Lnet/rim/device/api/ui/Field;)V
      // 3fe: aload 12
      // 400: ifnull 436
      // 403: aload 12
      // 405: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 40a: return
      // 40b: astore 13
      // 40d: return
      // 40e: astore 13
      // 410: aload 12
      // 412: ifnull 436
      // 415: aload 12
      // 417: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 41c: return
      // 41d: astore 13
      // 41f: return
      // 420: astore 30
      // 422: aload 12
      // 424: ifnull 433
      // 427: aload 12
      // 429: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 42e: goto 433
      // 431: astore 31
      // 433: aload 30
      // 435: athrow
      // 436: return
      // try (432 -> 434): 435 null
      // try (134 -> 430): 437 null
      // try (440 -> 442): 443 null
      // try (134 -> 430): 445 null
      // try (437 -> 438): 445 null
      // try (448 -> 450): 451 null
      // try (445 -> 446): 445 null
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      if (super.keyChar(c, status, time)) {
         return true;
      }

      UiApplication.getUiApplication().popScreen(this);
      return true;
   }
}
