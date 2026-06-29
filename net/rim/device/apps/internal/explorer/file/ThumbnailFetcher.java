package net.rim.device.apps.internal.explorer.file;

import net.rim.device.api.system.Application;
import net.rim.device.internal.io.file.MetaDataFile;

final class ThumbnailFetcher implements Runnable {
   private int MAX_THUMBNAILS_QUEUE = 30;
   private BoundedFileItemQueue _thumbsToLoad = new BoundedFileItemQueue(this.MAX_THUMBNAILS_QUEUE);
   private Object _gate = new Object();
   private int _mode;
   private FolderList _folderList;
   private MetaDataFile _db;
   private String _lastPath;
   private Thread _myThread;
   private boolean _running;
   private Runnable _task;
   private int _loadNextThumbRunnableId = -1;
   private Runnable _loadNextThumbRunnable = new ThumbnailFetcher$1(this);

   ThumbnailFetcher(FolderList folderList) {
      this._folderList = folderList;
      folderList.setThumbnailFetcher(this);
   }

   final void fetchNow() {
      if (this._loadNextThumbRunnableId >= 0) {
         Application.getApplication().cancelInvokeLater(this._loadNextThumbRunnableId);
         this._loadNextThumbRunnableId = -1;
      }

      synchronized (this._gate) {
         this._gate.notify();
      }
   }

   private final void fetchNext() {
      if (this._loadNextThumbRunnableId < 0) {
         this._loadNextThumbRunnableId = Application.getApplication().invokeLater(this._loadNextThumbRunnable, 1, false);
      }
   }

   final void addElement(FileItemField fileItem) {
      if (fileItem != null) {
         synchronized (this._thumbsToLoad) {
            if (!this._thumbsToLoad.contains(fileItem)) {
               this._thumbsToLoad.addElement(fileItem);
            }
         }
      }

      this.checkStartThread();
   }

   final void setTask(Runnable task) {
      this._task = task;
      this.invalidateThumbsToLoad();
      this.checkStartThread();
   }

   private final void checkStartThread() {
      if (this._myThread == null) {
         this._myThread = new Thread(this, "ThumbnailFetcher");
         this._myThread.setPriority(this._myThread.getPriority() - 1);
         this._myThread.start();
      }

      synchronized (this._gate) {
         this._gate.notify();
      }
   }

   final void invalidateThumbsToLoad() {
      this._thumbsToLoad.flush();
   }

   final void setMode(int mode) {
      this._mode = mode;
   }

   final void setSize(int size) {
      if (size != this._thumbsToLoad.size()) {
         this._thumbsToLoad.resize(size);
      }
   }

   final void shutdown() {
      if (this._db != null) {
         this._db = null;
      }

      this._lastPath = null;
      this._running = false;
      this.invalidateThumbsToLoad();
      if (this._myThread != null) {
         this._myThread.interrupt();
      }
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
      // 001: bipush 1
      // 002: putfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._running Z
      // 005: aload 0
      // 006: getfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._running Z
      // 009: ifne 00f
      // 00c: goto 16a
      // 00f: aconst_null
      // 010: astore 1
      // 011: aload 0
      // 012: getfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._task Ljava/lang/Runnable;
      // 015: astore 2
      // 016: aload 0
      // 017: aconst_null
      // 018: putfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._task Ljava/lang/Runnable;
      // 01b: aload 2
      // 01c: ifnull 037
      // 01f: aload 2
      // 020: invokeinterface java/lang/Runnable.run ()V 1
      // 025: aconst_null
      // 026: astore 2
      // 027: goto 005
      // 02a: astore 3
      // 02b: aconst_null
      // 02c: astore 2
      // 02d: goto 005
      // 030: astore 4
      // 032: aconst_null
      // 033: astore 2
      // 034: aload 4
      // 036: athrow
      // 037: aload 0
      // 038: getfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._thumbsToLoad Lnet/rim/device/apps/internal/explorer/file/BoundedFileItemQueue;
      // 03b: invokevirtual net/rim/device/apps/internal/explorer/file/BoundedFileItemQueue.isEmpty ()Z
      // 03e: ifeq 05f
      // 041: aload 0
      // 042: getfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._gate Ljava/lang/Object;
      // 045: dup
      // 046: astore 3
      // 047: monitorenter
      // 048: aload 0
      // 049: getfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._gate Ljava/lang/Object;
      // 04c: sipush 5000
      // 04f: i2l
      // 050: invokevirtual java/lang/Object.wait (J)V
      // 053: aload 3
      // 054: monitorexit
      // 055: goto 05f
      // 058: astore 5
      // 05a: aload 3
      // 05b: monitorexit
      // 05c: aload 5
      // 05e: athrow
      // 05f: aload 0
      // 060: getfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._task Ljava/lang/Runnable;
      // 063: ifnull 069
      // 066: goto 005
      // 069: aload 0
      // 06a: getfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._thumbsToLoad Lnet/rim/device/apps/internal/explorer/file/BoundedFileItemQueue;
      // 06d: dup
      // 06e: astore 3
      // 06f: monitorenter
      // 070: aload 0
      // 071: getfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._thumbsToLoad Lnet/rim/device/apps/internal/explorer/file/BoundedFileItemQueue;
      // 074: invokevirtual net/rim/device/apps/internal/explorer/file/BoundedFileItemQueue.isEmpty ()Z
      // 077: ifeq 086
      // 07a: aload 3
      // 07b: monitorexit
      // 07c: aload 0
      // 07d: aconst_null
      // 07e: putfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._myThread Ljava/lang/Thread;
      // 081: aload 0
      // 082: invokevirtual net/rim/device/apps/internal/explorer/file/ThumbnailFetcher.shutdown ()V
      // 085: return
      // 086: aload 0
      // 087: getfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._thumbsToLoad Lnet/rim/device/apps/internal/explorer/file/BoundedFileItemQueue;
      // 08a: invokevirtual net/rim/device/apps/internal/explorer/file/BoundedFileItemQueue.removeElement ()Lnet/rim/device/apps/internal/explorer/file/FileItemField;
      // 08d: astore 1
      // 08e: aload 3
      // 08f: monitorexit
      // 090: goto 0a5
      // 093: astore 6
      // 095: aload 3
      // 096: monitorexit
      // 097: aload 6
      // 099: athrow
      // 09a: astore 2
      // 09b: aload 0
      // 09c: aconst_null
      // 09d: putfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._myThread Ljava/lang/Thread;
      // 0a0: aload 0
      // 0a1: invokevirtual net/rim/device/apps/internal/explorer/file/ThumbnailFetcher.shutdown ()V
      // 0a4: return
      // 0a5: aload 0
      // 0a6: getfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._db Lnet/rim/device/internal/io/file/MetaDataFile;
      // 0a9: astore 2
      // 0aa: aload 1
      // 0ab: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getName ()Ljava/lang/String;
      // 0ae: astore 3
      // 0af: aload 1
      // 0b0: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getPath ()Ljava/lang/String;
      // 0b3: astore 4
      // 0b5: aload 0
      // 0b6: getfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._lastPath Ljava/lang/String;
      // 0b9: ifnull 0c8
      // 0bc: aload 4
      // 0be: aload 0
      // 0bf: getfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._lastPath Ljava/lang/String;
      // 0c2: invokevirtual java/lang/String.equalsIgnoreCase (Ljava/lang/String;)Z
      // 0c5: ifne 0dc
      // 0c8: aload 0
      // 0c9: aload 4
      // 0cb: invokestatic net/rim/device/internal/io/file/FileUtilities.makeFileURL (Ljava/lang/String;)Ljava/lang/String;
      // 0ce: invokestatic net/rim/device/internal/io/file/MetaDataFile.getOrCreate (Ljava/lang/String;)Lnet/rim/device/internal/io/file/MetaDataFile;
      // 0d1: dup_x1
      // 0d2: putfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._db Lnet/rim/device/internal/io/file/MetaDataFile;
      // 0d5: astore 2
      // 0d6: aload 0
      // 0d7: aload 4
      // 0d9: putfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._lastPath Ljava/lang/String;
      // 0dc: aload 0
      // 0dd: getfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._mode I
      // 0e0: lookupswitch 20 1 2 26
      // 0f4: bipush 0
      // 0f5: istore 5
      // 0f7: goto 0fd
      // 0fa: bipush 1
      // 0fb: istore 5
      // 0fd: aload 2
      // 0fe: aload 3
      // 0ff: iload 5
      // 101: invokevirtual net/rim/device/internal/io/file/MetaDataFile.getOrCreateMetadata (Ljava/lang/String;I)Ljava/lang/Object;
      // 104: astore 6
      // 106: aload 6
      // 108: ifnull 163
      // 10b: bipush 0
      // 10c: istore 7
      // 10e: iload 5
      // 110: tableswitch 32 -1 2 70 32 42 63
      // 130: aload 6
      // 132: instanceof net/rim/device/api/system/EncodedImage
      // 135: istore 7
      // 137: goto 156
      // 13a: aload 6
      // 13c: instanceof java/lang/String
      // 13f: istore 7
      // 141: iload 7
      // 143: ifne 156
      // 146: aload 2
      // 147: aload 3
      // 148: bipush 2
      // 14a: invokevirtual net/rim/device/internal/io/file/MetaDataFile.getMetadataFromCache (Ljava/lang/String;I)Ljava/lang/Object;
      // 14d: astore 6
      // 14f: aload 6
      // 151: instanceof java/lang/Long
      // 154: istore 7
      // 156: iload 7
      // 158: ifeq 163
      // 15b: aload 0
      // 15c: getfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._folderList Lnet/rim/device/apps/internal/explorer/file/FolderList;
      // 15f: aload 1
      // 160: invokevirtual net/rim/device/apps/internal/explorer/file/FolderList.completedFetch (Lnet/rim/device/apps/internal/explorer/file/FileItemField;)V
      // 163: aload 0
      // 164: invokespecial net/rim/device/apps/internal/explorer/file/ThumbnailFetcher.fetchNext ()V
      // 167: goto 005
      // 16a: aload 0
      // 16b: aconst_null
      // 16c: putfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._myThread Ljava/lang/Thread;
      // 16f: aload 0
      // 170: invokevirtual net/rim/device/apps/internal/explorer/file/ThumbnailFetcher.shutdown ()V
      // 173: return
      // 174: astore 1
      // 175: aload 0
      // 176: aconst_null
      // 177: putfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._myThread Ljava/lang/Thread;
      // 17a: aload 0
      // 17b: invokevirtual net/rim/device/apps/internal/explorer/file/ThumbnailFetcher.shutdown ()V
      // 17e: return
      // 17f: astore 8
      // 181: aload 0
      // 182: aconst_null
      // 183: putfield net/rim/device/apps/internal/explorer/file/ThumbnailFetcher._myThread Ljava/lang/Thread;
      // 186: aload 0
      // 187: invokevirtual net/rim/device/apps/internal/explorer/file/ThumbnailFetcher.shutdown ()V
      // 18a: aload 8
      // 18c: athrow
      // try (17 -> 19): 22 null
      // try (17 -> 19): 26 null
      // try (22 -> 23): 26 null
      // try (26 -> 27): 26 null
      // try (40 -> 47): 48 null
      // try (48 -> 51): 48 null
      // try (62 -> 68): 81 null
      // try (74 -> 80): 81 null
      // try (81 -> 84): 81 null
      // try (9 -> 56): 86 null
      // try (57 -> 68): 86 null
      // try (74 -> 86): 86 null
      // try (0 -> 68): 171 null
      // try (74 -> 87): 171 null
      // try (93 -> 165): 171 null
      // try (0 -> 68): 178 null
      // try (74 -> 87): 178 null
      // try (93 -> 165): 178 null
      // try (171 -> 172): 178 null
      // try (178 -> 179): 178 null
   }
}
