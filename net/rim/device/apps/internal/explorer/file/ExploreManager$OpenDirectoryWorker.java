package net.rim.device.apps.internal.explorer.file;

import java.util.Enumeration;
import net.rim.device.api.collection.util.SortedReadableList;
import net.rim.device.apps.api.framework.file.FileSelectionFilter;

final class ExploreManager$OpenDirectoryWorker implements Runnable {
   FileItemField _fileItem;
   String _directory;
   FileSelectionFilter _filter;
   SortedReadableList _shortcutList;
   SortedReadableList _fileList;
   Enumeration _files;
   boolean _hasDetails;
   boolean _completed;
   private final ExploreManager this$0;

   final void cancel() {
      this._completed = true;
   }

   final boolean isDone() {
      return this._completed;
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
      // 000: aconst_null
      // 001: astore 1
      // 002: aload 0
      // 003: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker._files Ljava/util/Enumeration;
      // 006: astore 2
      // 007: aload 0
      // 008: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker._hasDetails Z
      // 00b: istore 3
      // 00c: new java/util/Vector
      // 00f: dup
      // 010: invokespecial java/util/Vector.<init> ()V
      // 013: astore 4
      // 015: new java/util/Vector
      // 018: dup
      // 019: invokespecial java/util/Vector.<init> ()V
      // 01c: astore 5
      // 01e: aload 2
      // 01f: ifnonnull 06c
      // 022: aload 0
      // 023: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker._fileItem Lnet/rim/device/apps/internal/explorer/file/FileItemField;
      // 026: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getFileConnection ()Ljavax/microedition/io/file/FileConnection;
      // 029: astore 1
      // 02a: aload 1
      // 02b: instanceof net/rim/device/api/io/file/ExtendedFileConnection
      // 02e: ifeq 05c
      // 031: aload 0
      // 032: aload 1
      // 033: checkcast net/rim/device/api/io/file/ExtendedFileConnection
      // 036: ldc_w "*"
      // 039: aload 0
      // 03a: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker._filter Lnet/rim/device/apps/api/framework/file/FileSelectionFilter;
      // 03d: invokevirtual net/rim/device/apps/api/framework/file/FileSelectionFilter.isHideFilteredOn ()Z
      // 040: ifne 047
      // 043: bipush 1
      // 044: goto 048
      // 047: bipush 0
      // 048: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.listWithDetails (Ljava/lang/String;Z)Ljava/util/Enumeration; 3
      // 04d: dup
      // 04e: astore 2
      // 04f: putfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker._files Ljava/util/Enumeration;
      // 052: aload 0
      // 053: bipush 1
      // 054: dup
      // 055: istore 3
      // 056: putfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker._hasDetails Z
      // 059: goto 06c
      // 05c: aload 1
      // 05d: ifnull 06c
      // 060: aload 0
      // 061: aload 1
      // 062: invokeinterface javax/microedition/io/file/FileConnection.list ()Ljava/util/Enumeration; 1
      // 067: dup
      // 068: astore 2
      // 069: putfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker._files Ljava/util/Enumeration;
      // 06c: aload 2
      // 06d: ifnonnull 073
      // 070: goto 180
      // 073: bipush 100
      // 075: istore 6
      // 077: aload 2
      // 078: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 07d: ifne 083
      // 080: goto 180
      // 083: iinc 6 -1
      // 086: iload 6
      // 088: ifgt 08e
      // 08b: goto 180
      // 08e: aconst_null
      // 08f: astore 8
      // 091: bipush 0
      // 092: istore 9
      // 094: iload 3
      // 095: ifeq 0d1
      // 098: aload 2
      // 099: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 09e: checkcast net/rim/device/api/io/FileInfo
      // 0a1: astore 8
      // 0a3: aload 8
      // 0a5: invokevirtual net/rim/device/api/io/FileInfo.getFileName ()Ljava/lang/String;
      // 0a8: astore 7
      // 0aa: aload 8
      // 0ac: invokevirtual net/rim/device/api/io/FileInfo.getAttributes ()I
      // 0af: bipush 16
      // 0b1: iand
      // 0b2: ifeq 0dc
      // 0b5: new java/lang/StringBuffer
      // 0b8: dup
      // 0b9: invokespecial java/lang/StringBuffer.<init> ()V
      // 0bc: aload 7
      // 0be: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0c1: bipush 47
      // 0c3: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 0c6: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0c9: astore 7
      // 0cb: bipush 1
      // 0cc: istore 9
      // 0ce: goto 0dc
      // 0d1: aload 2
      // 0d2: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 0d7: checkcast java/lang/String
      // 0da: astore 7
      // 0dc: aconst_null
      // 0dd: astore 10
      // 0df: aload 7
      // 0e1: invokestatic net/rim/device/internal/io/file/FileUtilities.encodeString (Ljava/lang/String;)Ljava/lang/String;
      // 0e4: astore 7
      // 0e6: new net/rim/device/apps/internal/explorer/file/FileItemField
      // 0e9: dup
      // 0ea: aload 0
      // 0eb: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker._directory Ljava/lang/String;
      // 0ee: aload 7
      // 0f0: bipush 0
      // 0f1: aload 8
      // 0f3: aload 0
      // 0f4: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker._fileItem Lnet/rim/device/apps/internal/explorer/file/FileItemField;
      // 0f7: invokespecial net/rim/device/apps/internal/explorer/file/FileItemField.<init> (Ljava/lang/String;Ljava/lang/String;ZLnet/rim/device/api/io/FileInfo;Lnet/rim/device/apps/internal/explorer/file/FileItemField;)V
      // 0fa: astore 10
      // 0fc: goto 154
      // 0ff: astore 11
      // 101: aconst_null
      // 102: astore 12
      // 104: aload 11
      // 106: instanceof java/lang/IllegalArgumentException
      // 109: ifeq 116
      // 10c: bipush 44
      // 10e: invokestatic net/rim/device/apps/internal/explorer/file/resource/ExplorerResources.getString (I)Ljava/lang/String;
      // 111: astore 12
      // 113: goto 135
      // 116: aload 11
      // 118: dup
      // 119: instanceof net/rim/device/api/io/file/FileIOException
      // 11c: ifne 123
      // 11f: pop
      // 120: goto 12e
      // 123: checkcast net/rim/device/api/io/file/FileIOException
      // 126: invokevirtual net/rim/device/api/io/file/FileIOException.getMessage ()Ljava/lang/String;
      // 129: astore 12
      // 12b: goto 135
      // 12e: bipush 50
      // 130: invokestatic net/rim/device/apps/internal/explorer/file/resource/ExplorerResources.getString (I)Ljava/lang/String;
      // 133: astore 12
      // 135: new net/rim/device/apps/internal/explorer/file/AliasFileItemField
      // 138: dup
      // 139: new net/rim/device/apps/api/framework/file/AliasFileEntry
      // 13c: dup
      // 13d: aload 7
      // 13f: aload 0
      // 140: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker.this$0 Lnet/rim/device/apps/internal/explorer/file/ExploreManager;
      // 143: aload 12
      // 145: invokespecial net/rim/device/apps/internal/explorer/file/ExploreManager.getInvalidFileAlertVerb (Ljava/lang/String;)Lnet/rim/device/apps/api/framework/verb/Verb;
      // 148: bipush 0
      // 149: invokestatic net/rim/device/apps/api/icons/FileIcon.getFileIconImage (I)Lnet/rim/device/internal/ui/Image;
      // 14c: invokespecial net/rim/device/apps/api/framework/file/AliasFileEntry.<init> (Ljava/lang/String;Lnet/rim/device/apps/api/framework/verb/Verb;Lnet/rim/device/internal/ui/Image;)V
      // 14f: invokespecial net/rim/device/apps/internal/explorer/file/AliasFileItemField.<init> (Lnet/rim/device/apps/api/framework/file/AliasFileEntry;)V
      // 152: astore 10
      // 154: aload 0
      // 155: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker.this$0 Lnet/rim/device/apps/internal/explorer/file/ExploreManager;
      // 158: aload 0
      // 159: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker._filter Lnet/rim/device/apps/api/framework/file/FileSelectionFilter;
      // 15c: aload 10
      // 15e: invokespecial net/rim/device/apps/internal/explorer/file/ExploreManager.match (Lnet/rim/device/apps/api/framework/file/FileSelectionFilter;Lnet/rim/device/apps/internal/explorer/file/FileItemField;)Z
      // 161: ifne 167
      // 164: goto 077
      // 167: iload 9
      // 169: ifeq 176
      // 16c: aload 4
      // 16e: aload 10
      // 170: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 173: goto 077
      // 176: aload 5
      // 178: aload 10
      // 17a: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 17d: goto 077
      // 180: aload 1
      // 181: ifnull 18f
      // 184: aload 1
      // 185: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 18a: goto 18f
      // 18d: astore 15
      // 18f: aload 2
      // 190: ifnull 19c
      // 193: aload 2
      // 194: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 199: ifne 1a1
      // 19c: aload 0
      // 19d: bipush 1
      // 19e: putfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker._completed Z
      // 1a1: new net/rim/device/apps/internal/explorer/file/ExploreManager$AddElementsTask
      // 1a4: dup
      // 1a5: aload 0
      // 1a6: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker.this$0 Lnet/rim/device/apps/internal/explorer/file/ExploreManager;
      // 1a9: aload 4
      // 1ab: aload 5
      // 1ad: aload 0
      // 1ae: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker._shortcutList Lnet/rim/device/api/collection/util/SortedReadableList;
      // 1b1: aload 0
      // 1b2: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker._fileList Lnet/rim/device/api/collection/util/SortedReadableList;
      // 1b5: invokespecial net/rim/device/apps/internal/explorer/file/ExploreManager$AddElementsTask.<init> (Lnet/rim/device/apps/internal/explorer/file/ExploreManager;Ljava/util/Vector;Ljava/util/Vector;Lnet/rim/device/api/collection/util/SortedReadableList;Lnet/rim/device/api/collection/util/SortedReadableList;)V
      // 1b8: astore 15
      // 1ba: invokestatic net/rim/device/api/system/Application.isEventDispatchThread ()Z
      // 1bd: ifeq 1c8
      // 1c0: aload 15
      // 1c2: invokevirtual net/rim/device/apps/internal/explorer/file/ExploreManager$AddElementsTask.run ()V
      // 1c5: goto 1d4
      // 1c8: aload 0
      // 1c9: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker.this$0 Lnet/rim/device/apps/internal/explorer/file/ExploreManager;
      // 1cc: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._app Lnet/rim/device/api/ui/UiApplication;
      // 1cf: aload 15
      // 1d1: invokevirtual net/rim/device/api/system/Application.invokeLater (Ljava/lang/Runnable;)V
      // 1d4: aload 0
      // 1d5: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker._completed Z
      // 1d8: ifne 1de
      // 1db: goto 2d9
      // 1de: aload 0
      // 1df: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker.this$0 Lnet/rim/device/apps/internal/explorer/file/ExploreManager;
      // 1e2: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._updateLayoutInvokeLater Lnet/rim/device/apps/internal/explorer/file/ExploreManager$UpdateLayoutInvokeLater;
      // 1e5: invokevirtual net/rim/device/apps/internal/explorer/file/ExploreManager$UpdateLayoutInvokeLater.dirOpenComplete ()V
      // 1e8: aload 0
      // 1e9: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker.this$0 Lnet/rim/device/apps/internal/explorer/file/ExploreManager;
      // 1ec: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._updateLayoutInvokeLater Lnet/rim/device/apps/internal/explorer/file/ExploreManager$UpdateLayoutInvokeLater;
      // 1ef: invokevirtual net/rim/device/apps/internal/explorer/file/ExploreManager$UpdateLayoutInvokeLater.invokeLater ()V
      // 1f2: return
      // 1f3: astore 6
      // 1f5: aload 1
      // 1f6: ifnull 204
      // 1f9: aload 1
      // 1fa: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1ff: goto 204
      // 202: astore 15
      // 204: aload 2
      // 205: ifnull 211
      // 208: aload 2
      // 209: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 20e: ifne 216
      // 211: aload 0
      // 212: bipush 1
      // 213: putfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker._completed Z
      // 216: new net/rim/device/apps/internal/explorer/file/ExploreManager$AddElementsTask
      // 219: dup
      // 21a: aload 0
      // 21b: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker.this$0 Lnet/rim/device/apps/internal/explorer/file/ExploreManager;
      // 21e: aload 4
      // 220: aload 5
      // 222: aload 0
      // 223: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker._shortcutList Lnet/rim/device/api/collection/util/SortedReadableList;
      // 226: aload 0
      // 227: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker._fileList Lnet/rim/device/api/collection/util/SortedReadableList;
      // 22a: invokespecial net/rim/device/apps/internal/explorer/file/ExploreManager$AddElementsTask.<init> (Lnet/rim/device/apps/internal/explorer/file/ExploreManager;Ljava/util/Vector;Ljava/util/Vector;Lnet/rim/device/api/collection/util/SortedReadableList;Lnet/rim/device/api/collection/util/SortedReadableList;)V
      // 22d: astore 15
      // 22f: invokestatic net/rim/device/api/system/Application.isEventDispatchThread ()Z
      // 232: ifeq 23d
      // 235: aload 15
      // 237: invokevirtual net/rim/device/apps/internal/explorer/file/ExploreManager$AddElementsTask.run ()V
      // 23a: goto 249
      // 23d: aload 0
      // 23e: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker.this$0 Lnet/rim/device/apps/internal/explorer/file/ExploreManager;
      // 241: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._app Lnet/rim/device/api/ui/UiApplication;
      // 244: aload 15
      // 246: invokevirtual net/rim/device/api/system/Application.invokeLater (Ljava/lang/Runnable;)V
      // 249: aload 0
      // 24a: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker._completed Z
      // 24d: ifeq 2d9
      // 250: aload 0
      // 251: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker.this$0 Lnet/rim/device/apps/internal/explorer/file/ExploreManager;
      // 254: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._updateLayoutInvokeLater Lnet/rim/device/apps/internal/explorer/file/ExploreManager$UpdateLayoutInvokeLater;
      // 257: invokevirtual net/rim/device/apps/internal/explorer/file/ExploreManager$UpdateLayoutInvokeLater.dirOpenComplete ()V
      // 25a: aload 0
      // 25b: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker.this$0 Lnet/rim/device/apps/internal/explorer/file/ExploreManager;
      // 25e: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._updateLayoutInvokeLater Lnet/rim/device/apps/internal/explorer/file/ExploreManager$UpdateLayoutInvokeLater;
      // 261: invokevirtual net/rim/device/apps/internal/explorer/file/ExploreManager$UpdateLayoutInvokeLater.invokeLater ()V
      // 264: return
      // 265: astore 13
      // 267: aload 1
      // 268: ifnull 276
      // 26b: aload 1
      // 26c: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 271: goto 276
      // 274: astore 15
      // 276: aload 2
      // 277: ifnull 283
      // 27a: aload 2
      // 27b: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 280: ifne 288
      // 283: aload 0
      // 284: bipush 1
      // 285: putfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker._completed Z
      // 288: new net/rim/device/apps/internal/explorer/file/ExploreManager$AddElementsTask
      // 28b: dup
      // 28c: aload 0
      // 28d: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker.this$0 Lnet/rim/device/apps/internal/explorer/file/ExploreManager;
      // 290: aload 4
      // 292: aload 5
      // 294: aload 0
      // 295: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker._shortcutList Lnet/rim/device/api/collection/util/SortedReadableList;
      // 298: aload 0
      // 299: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker._fileList Lnet/rim/device/api/collection/util/SortedReadableList;
      // 29c: invokespecial net/rim/device/apps/internal/explorer/file/ExploreManager$AddElementsTask.<init> (Lnet/rim/device/apps/internal/explorer/file/ExploreManager;Ljava/util/Vector;Ljava/util/Vector;Lnet/rim/device/api/collection/util/SortedReadableList;Lnet/rim/device/api/collection/util/SortedReadableList;)V
      // 29f: astore 15
      // 2a1: invokestatic net/rim/device/api/system/Application.isEventDispatchThread ()Z
      // 2a4: ifeq 2af
      // 2a7: aload 15
      // 2a9: invokevirtual net/rim/device/apps/internal/explorer/file/ExploreManager$AddElementsTask.run ()V
      // 2ac: goto 2bb
      // 2af: aload 0
      // 2b0: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker.this$0 Lnet/rim/device/apps/internal/explorer/file/ExploreManager;
      // 2b3: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._app Lnet/rim/device/api/ui/UiApplication;
      // 2b6: aload 15
      // 2b8: invokevirtual net/rim/device/api/system/Application.invokeLater (Ljava/lang/Runnable;)V
      // 2bb: aload 0
      // 2bc: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker._completed Z
      // 2bf: ifeq 2d6
      // 2c2: aload 0
      // 2c3: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker.this$0 Lnet/rim/device/apps/internal/explorer/file/ExploreManager;
      // 2c6: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._updateLayoutInvokeLater Lnet/rim/device/apps/internal/explorer/file/ExploreManager$UpdateLayoutInvokeLater;
      // 2c9: invokevirtual net/rim/device/apps/internal/explorer/file/ExploreManager$UpdateLayoutInvokeLater.dirOpenComplete ()V
      // 2cc: aload 0
      // 2cd: getfield net/rim/device/apps/internal/explorer/file/ExploreManager$OpenDirectoryWorker.this$0 Lnet/rim/device/apps/internal/explorer/file/ExploreManager;
      // 2d0: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._updateLayoutInvokeLater Lnet/rim/device/apps/internal/explorer/file/ExploreManager$UpdateLayoutInvokeLater;
      // 2d3: invokevirtual net/rim/device/apps/internal/explorer/file/ExploreManager$UpdateLayoutInvokeLater.invokeLater ()V
      // 2d6: aload 13
      // 2d8: athrow
      // 2d9: return
      // try (103 -> 117): 118 null
      // try (16 -> 173): 222 null
      // try (16 -> 173): 271 null
      // try (222 -> 223): 271 null
      // try (271 -> 272): 271 null
      // try (274 -> 276): 277 null
      // try (225 -> 227): 228 null
      // try (175 -> 177): 178 null
   }

   ExploreManager$OpenDirectoryWorker(
      ExploreManager _1, FileItemField fileItem, FileSelectionFilter filter, SortedReadableList shortcutList, SortedReadableList fileList
   ) {
      this.this$0 = _1;
      this._directory = fileItem.getFullPath();
      this._fileItem = fileItem;
      this._filter = filter;
      this._shortcutList = shortcutList;
      this._fileList = fileList;
   }
}
