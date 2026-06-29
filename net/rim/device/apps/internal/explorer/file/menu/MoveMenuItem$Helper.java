package net.rim.device.apps.internal.explorer.file.menu;

final class MoveMenuItem$Helper extends Thread {
   private boolean _overwrite;
   private boolean _moveconfirm;
   private final MoveMenuItem this$0;

   MoveMenuItem$Helper(MoveMenuItem _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper.this$0 Lnet/rim/device/apps/internal/explorer/file/menu/MoveMenuItem;
      // 004: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem._destPath Ljava/lang/String;
      // 007: invokestatic net/rim/device/internal/io/file/FileUtilities.isDirectory (Ljava/lang/String;)Z
      // 00a: ifne 01e
      // 00d: aload 0
      // 00e: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper.this$0 Lnet/rim/device/apps/internal/explorer/file/menu/MoveMenuItem;
      // 011: aload 0
      // 012: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper.this$0 Lnet/rim/device/apps/internal/explorer/file/menu/MoveMenuItem;
      // 015: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem._destPath Ljava/lang/String;
      // 018: invokestatic net/rim/device/internal/io/file/FileUtilities.getPathURL (Ljava/lang/String;)Ljava/lang/String;
      // 01b: putfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem._destPath Ljava/lang/String;
      // 01e: aload 0
      // 01f: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper.this$0 Lnet/rim/device/apps/internal/explorer/file/menu/MoveMenuItem;
      // 022: aload 0
      // 023: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper.this$0 Lnet/rim/device/apps/internal/explorer/file/menu/MoveMenuItem;
      // 026: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem._destPath Ljava/lang/String;
      // 029: ldc_w "file://"
      // 02c: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 02f: ifeq 03c
      // 032: aload 0
      // 033: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper.this$0 Lnet/rim/device/apps/internal/explorer/file/menu/MoveMenuItem;
      // 036: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem._destPath Ljava/lang/String;
      // 039: goto 053
      // 03c: new java/lang/Object
      // 03f: dup
      // 040: ldc_w "file://"
      // 043: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 046: aload 0
      // 047: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper.this$0 Lnet/rim/device/apps/internal/explorer/file/menu/MoveMenuItem;
      // 04a: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem._destPath Ljava/lang/String;
      // 04d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 050: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 053: putfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem._destPath Ljava/lang/String;
      // 056: new java/lang/Object
      // 059: dup
      // 05a: invokespecial java/lang/StringBuffer.<init> ()V
      // 05d: aload 0
      // 05e: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper.this$0 Lnet/rim/device/apps/internal/explorer/file/menu/MoveMenuItem;
      // 061: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem._destPath Ljava/lang/String;
      // 064: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 067: aload 0
      // 068: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper.this$0 Lnet/rim/device/apps/internal/explorer/file/menu/MoveMenuItem;
      // 06b: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem._sourceFile Lnet/rim/device/apps/internal/explorer/file/FileItemField;
      // 06e: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getName ()Ljava/lang/String;
      // 071: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 074: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 077: astore 1
      // 078: aconst_null
      // 079: astore 2
      // 07a: aload 0
      // 07b: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper.this$0 Lnet/rim/device/apps/internal/explorer/file/menu/MoveMenuItem;
      // 07e: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem._destPath Ljava/lang/String;
      // 081: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 084: checkcast java/lang/Object
      // 087: astore 3
      // 088: aload 3
      // 089: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 08e: ifne 09a
      // 091: bipush 49
      // 093: invokestatic net/rim/device/apps/internal/explorer/file/resource/ExplorerResources.getString (I)Ljava/lang/String;
      // 096: astore 2
      // 097: goto 170
      // 09a: aload 3
      // 09b: invokeinterface javax/microedition/io/file/FileConnection.canWrite ()Z 1
      // 0a0: ifne 0ac
      // 0a3: bipush 48
      // 0a5: invokestatic net/rim/device/apps/internal/explorer/file/resource/ExplorerResources.getString (I)Ljava/lang/String;
      // 0a8: astore 2
      // 0a9: goto 170
      // 0ac: aload 1
      // 0ad: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 0b0: checkcast java/lang/Object
      // 0b3: astore 3
      // 0b4: aload 3
      // 0b5: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 0ba: ifeq 0f4
      // 0bd: aload 1
      // 0be: aload 0
      // 0bf: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper.this$0 Lnet/rim/device/apps/internal/explorer/file/menu/MoveMenuItem;
      // 0c2: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem._sourceFile Lnet/rim/device/apps/internal/explorer/file/FileItemField;
      // 0c5: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getURL ()Ljava/lang/String;
      // 0c8: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 0cb: ifne 0f4
      // 0ce: invokestatic net/rim/device/api/system/Application.getApplication ()Lnet/rim/device/api/system/Application;
      // 0d1: new net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper$1
      // 0d4: dup
      // 0d5: aload 0
      // 0d6: invokespecial net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper$1.<init> (Lnet/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper;)V
      // 0d9: invokevirtual net/rim/device/api/system/Application.invokeAndWait (Ljava/lang/Runnable;)V
      // 0dc: aload 0
      // 0dd: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper._overwrite Z
      // 0e0: ifeq 0f3
      // 0e3: aload 3
      // 0e4: bipush 1
      // 0e5: invokeinterface javax/microedition/io/file/FileConnection.setWritable (Z)V 2
      // 0ea: aload 3
      // 0eb: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 0f0: goto 121
      // 0f3: return
      // 0f4: aload 0
      // 0f5: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper.this$0 Lnet/rim/device/apps/internal/explorer/file/menu/MoveMenuItem;
      // 0f8: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem._copy Z
      // 0fb: ifne 121
      // 0fe: aload 0
      // 0ff: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper.this$0 Lnet/rim/device/apps/internal/explorer/file/menu/MoveMenuItem;
      // 102: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem._sourceFile Lnet/rim/device/apps/internal/explorer/file/FileItemField;
      // 105: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.canWrite ()Z
      // 108: ifne 121
      // 10b: invokestatic net/rim/device/api/system/Application.getApplication ()Lnet/rim/device/api/system/Application;
      // 10e: new net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper$2
      // 111: dup
      // 112: aload 0
      // 113: invokespecial net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper$2.<init> (Lnet/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper;)V
      // 116: invokevirtual net/rim/device/api/system/Application.invokeAndWait (Ljava/lang/Runnable;)V
      // 119: aload 0
      // 11a: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper._moveconfirm Z
      // 11d: ifne 121
      // 120: return
      // 121: aload 0
      // 122: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper.this$0 Lnet/rim/device/apps/internal/explorer/file/menu/MoveMenuItem;
      // 125: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem._copy Z
      // 128: ifeq 141
      // 12b: aload 0
      // 12c: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper.this$0 Lnet/rim/device/apps/internal/explorer/file/menu/MoveMenuItem;
      // 12f: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem._sourceFile Lnet/rim/device/apps/internal/explorer/file/FileItemField;
      // 132: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getURL ()Ljava/lang/String;
      // 135: aload 1
      // 136: aload 0
      // 137: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper.this$0 Lnet/rim/device/apps/internal/explorer/file/menu/MoveMenuItem;
      // 13a: bipush 1
      // 13b: invokestatic net/rim/device/internal/io/file/FileUtilities.copyFile (Ljava/lang/String;Ljava/lang/String;Lnet/rim/device/internal/io/file/CopyProgressCallback;Z)V
      // 13e: goto 154
      // 141: aload 0
      // 142: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper.this$0 Lnet/rim/device/apps/internal/explorer/file/menu/MoveMenuItem;
      // 145: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem._sourceFile Lnet/rim/device/apps/internal/explorer/file/FileItemField;
      // 148: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getURL ()Ljava/lang/String;
      // 14b: aload 1
      // 14c: aload 0
      // 14d: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper.this$0 Lnet/rim/device/apps/internal/explorer/file/menu/MoveMenuItem;
      // 150: bipush 1
      // 151: invokestatic net/rim/device/internal/io/file/FileUtilities.moveFile (Ljava/lang/String;Ljava/lang/String;Lnet/rim/device/internal/io/file/CopyProgressCallback;Z)V
      // 154: aload 0
      // 155: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper.this$0 Lnet/rim/device/apps/internal/explorer/file/menu/MoveMenuItem;
      // 158: getfield net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem._destPath Ljava/lang/String;
      // 15b: invokestatic net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem.access$602 (Ljava/lang/String;)Ljava/lang/String;
      // 15e: pop
      // 15f: return
      // 160: astore 3
      // 161: aload 3
      // 162: invokevirtual net/rim/device/api/io/file/FileIOException.getMessage ()Ljava/lang/String;
      // 165: astore 2
      // 166: goto 170
      // 169: astore 3
      // 16a: bipush 50
      // 16c: invokestatic net/rim/device/apps/internal/explorer/file/resource/ExplorerResources.getString (I)Ljava/lang/String;
      // 16f: astore 2
      // 170: aload 2
      // 171: astore 3
      // 172: invokestatic net/rim/device/api/system/Application.getApplication ()Lnet/rim/device/api/system/Application;
      // 175: new net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper$3
      // 178: dup
      // 179: aload 0
      // 17a: aload 3
      // 17b: invokespecial net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper$3.<init> (Lnet/rim/device/apps/internal/explorer/file/menu/MoveMenuItem$Helper;Ljava/lang/String;)V
      // 17e: invokevirtual net/rim/device/api/system/Application.invokeAndWait (Ljava/lang/Runnable;)V
      // 181: return
      // try (50 -> 99): 148 null
      // try (100 -> 118): 148 null
      // try (119 -> 147): 148 null
      // try (50 -> 99): 153 null
      // try (100 -> 118): 153 null
      // try (119 -> 147): 153 null
   }
}
