package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.internal.explorer.file.FileItemField;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

public final class AddFolderMenuItem extends MenuItem {
   private FileItemField _currentFolder;
   private static final int MAX_FOLDERNAME_CHARS;

   public AddFolderMenuItem(FileItemField folder) {
      super(ExplorerResources.getResourceBundleFamily(), 14, 589904, 200);
      this._currentFolder = folder;
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
      // 000: aconst_null
      // 001: astore 1
      // 002: aload 0
      // 003: getfield net/rim/device/apps/internal/explorer/file/menu/AddFolderMenuItem._currentFolder Lnet/rim/device/apps/internal/explorer/file/FileItemField;
      // 006: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getFileConnection ()Ljavax/microedition/io/file/FileConnection;
      // 009: astore 1
      // 00a: aload 1
      // 00b: ifnonnull 021
      // 00e: bipush 50
      // 010: invokestatic net/rim/device/apps/internal/explorer/file/resource/ExplorerResources.getString (I)Ljava/lang/String;
      // 013: invokestatic net/rim/device/api/ui/component/Dialog.alert (Ljava/lang/String;)V
      // 016: return
      // 017: astore 2
      // 018: bipush 50
      // 01a: invokestatic net/rim/device/apps/internal/explorer/file/resource/ExplorerResources.getString (I)Ljava/lang/String;
      // 01d: invokestatic net/rim/device/api/ui/component/Dialog.alert (Ljava/lang/String;)V
      // 020: return
      // 021: new java/lang/Object
      // 024: dup
      // 025: aload 0
      // 026: getfield net/rim/device/apps/internal/explorer/file/menu/AddFolderMenuItem._currentFolder Lnet/rim/device/apps/internal/explorer/file/FileItemField;
      // 029: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getFullPath ()Ljava/lang/String;
      // 02c: ldc_w "/"
      // 02f: bipush 0
      // 030: bipush 15
      // 032: invokestatic net/rim/device/apps/internal/explorer/file/resource/ExplorerResources.getString (I)Ljava/lang/String;
      // 035: invokespecial net/rim/device/apps/api/framework/file/FileDialog.<init> (Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)V
      // 038: astore 2
      // 039: aload 2
      // 03a: invokevirtual net/rim/device/apps/api/framework/file/FileDialog.doModal ()I
      // 03d: istore 3
      // 03e: iload 3
      // 03f: bipush -1
      // 041: if_icmpne 045
      // 044: return
      // 045: aload 2
      // 046: invokevirtual net/rim/device/apps/api/framework/file/FileDialog.getFilename ()Ljava/lang/String;
      // 049: astore 4
      // 04b: aload 4
      // 04d: ifnull 05b
      // 050: aload 4
      // 052: invokevirtual java/lang/String.trim ()Ljava/lang/String;
      // 055: invokevirtual java/lang/String.length ()I
      // 058: ifne 05c
      // 05b: return
      // 05c: aconst_null
      // 05d: astore 5
      // 05f: aload 2
      // 060: invokevirtual net/rim/device/apps/api/framework/file/FileDialog.getURL ()Ljava/lang/String;
      // 063: astore 4
      // 065: aload 4
      // 067: invokestatic net/rim/device/internal/io/file/FileUtilities.isDirectory (Ljava/lang/String;)Z
      // 06a: ifne 083
      // 06d: new java/lang/Object
      // 070: dup
      // 071: invokespecial java/lang/StringBuffer.<init> ()V
      // 074: aload 4
      // 076: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 079: bipush 47
      // 07b: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 07e: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 081: astore 4
      // 083: aload 4
      // 085: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 088: checkcast java/lang/Object
      // 08b: astore 1
      // 08c: aload 1
      // 08d: ifnonnull 09a
      // 090: bipush 50
      // 092: invokestatic net/rim/device/apps/internal/explorer/file/resource/ExplorerResources.getString (I)Ljava/lang/String;
      // 095: astore 5
      // 097: goto 0a0
      // 09a: aload 1
      // 09b: invokeinterface javax/microedition/io/file/FileConnection.mkdir ()V 1
      // 0a0: aload 1
      // 0a1: ifnull 0af
      // 0a4: aload 1
      // 0a5: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0aa: goto 0af
      // 0ad: astore 6
      // 0af: aload 5
      // 0b1: ifnull 136
      // 0b4: aload 5
      // 0b6: invokestatic net/rim/device/api/ui/component/Dialog.alert (Ljava/lang/String;)V
      // 0b9: return
      // 0ba: astore 6
      // 0bc: aload 6
      // 0be: dup
      // 0bf: instanceof java/lang/Object
      // 0c2: ifne 0c9
      // 0c5: pop
      // 0c6: goto 0d4
      // 0c9: checkcast java/lang/Object
      // 0cc: invokevirtual net/rim/device/api/io/file/FileIOException.getMessage ()Ljava/lang/String;
      // 0cf: astore 5
      // 0d1: goto 0db
      // 0d4: bipush 50
      // 0d6: invokestatic net/rim/device/apps/internal/explorer/file/resource/ExplorerResources.getString (I)Ljava/lang/String;
      // 0d9: astore 5
      // 0db: aload 1
      // 0dc: ifnull 0ea
      // 0df: aload 1
      // 0e0: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0e5: goto 0ea
      // 0e8: astore 6
      // 0ea: aload 5
      // 0ec: ifnull 136
      // 0ef: aload 5
      // 0f1: invokestatic net/rim/device/api/ui/component/Dialog.alert (Ljava/lang/String;)V
      // 0f4: return
      // 0f5: astore 6
      // 0f7: bipush 44
      // 0f9: invokestatic net/rim/device/apps/internal/explorer/file/resource/ExplorerResources.getString (I)Ljava/lang/String;
      // 0fc: astore 5
      // 0fe: aload 1
      // 0ff: ifnull 10d
      // 102: aload 1
      // 103: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 108: goto 10d
      // 10b: astore 6
      // 10d: aload 5
      // 10f: ifnull 136
      // 112: aload 5
      // 114: invokestatic net/rim/device/api/ui/component/Dialog.alert (Ljava/lang/String;)V
      // 117: return
      // 118: astore 7
      // 11a: aload 1
      // 11b: ifnull 129
      // 11e: aload 1
      // 11f: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 124: goto 129
      // 127: astore 8
      // 129: aload 5
      // 12b: ifnull 133
      // 12e: aload 5
      // 130: invokestatic net/rim/device/api/ui/component/Dialog.alert (Ljava/lang/String;)V
      // 133: aload 7
      // 135: athrow
      // 136: return
      // try (2 -> 11): 12 null
      // try (76 -> 78): 79 null
      // try (47 -> 74): 85 null
      // try (101 -> 103): 104 null
      // try (47 -> 74): 110 null
      // try (116 -> 118): 119 null
      // try (47 -> 74): 125 null
      // try (85 -> 99): 125 null
      // try (110 -> 114): 125 null
      // try (128 -> 130): 131 null
      // try (125 -> 126): 125 null
   }
}
