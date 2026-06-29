package net.rim.apps.internal.explorer.IndexService;

import java.util.Enumeration;
import javax.microedition.io.file.FileSystemListener;
import javax.microedition.io.file.FileSystemRegistry;
import net.rim.device.api.io.FileInfo;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.io.file.FileSystemJournal;
import net.rim.device.api.io.file.FileSystemJournalEntry;
import net.rim.device.api.io.file.FileSystemJournalListener;
import net.rim.device.api.system.Application;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringCaseInsensitiveHashtable;
import net.rim.device.api.util.StringToIntCaseInsensitiveHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.io.file.FileIndexService;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.io.file.MetaDataFile;
import net.rim.vm.DebugSupport;

public final class FileIndexServiceImpl extends FileIndexService implements FileSystemJournalListener, FileSystemListener {
   private StringCaseInsensitiveHashtable _mediaFolderList = new StringCaseInsensitiveHashtable();
   private long _myLastUSN;
   private FileScanner _scanner;
   private StringToIntCaseInsensitiveHashtable _preCreatedMediaFolders;
   private String[] _hiddenFolders = new String[0];
   private int _scanInProgress;
   private static final String[] ExcludedAudioMIMETypes = new String[]{"audio/x-gsm"};

   FileIndexServiceImpl() {
      this._scanner = new FileScanner(this);
      StringToIntCaseInsensitiveHashtable preCreatedMediaFolders = new StringToIntCaseInsensitiveHashtable(8);
      int mediaType = 4;

      while (--mediaType > 0) {
         preCreatedMediaFolders.put(FileUtilities.makeFileURL(FileIndexService.getDefaultMediaFolderForFileSystem(mediaType, 3, -1)), mediaType);
         preCreatedMediaFolders.put(FileUtilities.makeFileURL(FileIndexService.getDefaultMediaFolderForFileSystem(mediaType, 1, -1)), mediaType);
      }

      this._preCreatedMediaFolders = preCreatedMediaFolders;
   }

   final void init() {
      this._scanner.start();
      FileIndexService.setService(this);
      Application app = Application.getApplication();
      this._myLastUSN = FileSystemJournal.getNextUSN();
      if (DebugSupport.getenv("NoFileIndexing") == null) {
         app.addFileSystemListener(this);
         app.addFileSystemJournalListener(this);
      }
   }

   @Override
   public final void requestScan() {
      synchronized (this) {
         this._scanInProgress++;
      }

      if (DebugSupport.getenv("NoFileIndexing") == null) {
         Enumeration roots = FileSystemRegistry.listRoots();

         while (roots.hasMoreElements()) {
            String rootFolderName = (String)roots.nextElement();
            this._scanner.addFolderPath(FileUtilities.makeFileURL(FileUtilities.encodeString("/" + rootFolderName)));
         }
      }

      this._scanner.scanComplete();
   }

   private final Enumeration enumerateFiles(String param1) {
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
      // 00: aconst_null
      // 01: astore 2
      // 02: aload 1
      // 03: bipush 1
      // 04: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;I)Ljavax/microedition/io/Connection;
      // 07: checkcast javax/microedition/io/file/FileConnection
      // 0a: astore 2
      // 0b: aload 2
      // 0c: ifnull 65
      // 0f: aload 2
      // 10: invokeinterface javax/microedition/io/file/FileConnection.isHidden ()Z 1
      // 15: ifeq 20
      // 18: aload 0
      // 19: aload 1
      // 1a: invokespecial net/rim/apps/internal/explorer/IndexService/FileIndexServiceImpl.addHiddenFolder (Ljava/lang/String;)V
      // 1d: goto 65
      // 20: aload 0
      // 21: aload 1
      // 22: invokespecial net/rim/apps/internal/explorer/IndexService/FileIndexServiceImpl.removeHiddenFolder (Ljava/lang/String;)V
      // 25: aload 2
      // 26: dup
      // 27: instanceof net/rim/device/api/io/file/ExtendedFileConnection
      // 2a: ifne 31
      // 2d: pop
      // 2e: goto 4e
      // 31: checkcast net/rim/device/api/io/file/ExtendedFileConnection
      // 34: ldc_w "*"
      // 37: bipush 1
      // 38: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.listWithDetails (Ljava/lang/String;Z)Ljava/util/Enumeration; 3
      // 3d: astore 3
      // 3e: aload 2
      // 3f: ifnull 4c
      // 42: aload 2
      // 43: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 48: aload 3
      // 49: areturn
      // 4a: astore 4
      // 4c: aload 3
      // 4d: areturn
      // 4e: aload 2
      // 4f: invokeinterface javax/microedition/io/file/FileConnection.list ()Ljava/util/Enumeration; 1
      // 54: astore 3
      // 55: aload 2
      // 56: ifnull 63
      // 59: aload 2
      // 5a: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 5f: aload 3
      // 60: areturn
      // 61: astore 4
      // 63: aload 3
      // 64: areturn
      // 65: aload 2
      // 66: ifnull 98
      // 69: aload 2
      // 6a: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 6f: aconst_null
      // 70: areturn
      // 71: astore 3
      // 72: aconst_null
      // 73: areturn
      // 74: astore 3
      // 75: aload 2
      // 76: ifnull 98
      // 79: aload 2
      // 7a: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 7f: aconst_null
      // 80: areturn
      // 81: astore 3
      // 82: aconst_null
      // 83: areturn
      // 84: astore 5
      // 86: aload 2
      // 87: ifnull 95
      // 8a: aload 2
      // 8b: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 90: goto 95
      // 93: astore 6
      // 95: aload 5
      // 97: athrow
      // 98: aconst_null
      // 99: areturn
      // try (32 -> 34): 36 null
      // try (44 -> 46): 48 null
      // try (53 -> 55): 57 null
      // try (2 -> 30): 60 null
      // try (39 -> 42): 60 null
      // try (63 -> 65): 67 null
      // try (2 -> 30): 70 null
      // try (39 -> 42): 70 null
      // try (60 -> 61): 70 null
      // try (73 -> 75): 76 null
      // try (70 -> 71): 70 null
   }

   final synchronized void processScanComplete() {
      if (--this._scanInProgress < 0) {
         this._scanInProgress = 0;
      }
   }

   private final synchronized boolean isScanInProgress() {
      return this._scanInProgress > 0;
   }

   final void processAddFilePath(String fileURL) {
      String fileName = FileUtilities.getName(fileURL);
      String folderURL = fileURL.substring(0, fileURL.length() - fileName.length());
      this.processAddFilePath(folderURL, fileName);
   }

   final void processAddFilePath(String folderURL, String fileName) {
      int mediaType = MIMETypeAssociations.getMediaType(fileName);
      MetaDataFile db = MetaDataFile.getOrCreate(folderURL);
      if (db != null) {
         db.getOrCreateMetadata(fileName, true);
      }

      this.notifyMetaDataListeners(2, folderURL, fileName, null);
      if (this.haveFilteredFileListener(mediaType)) {
         this.notifyFilteredFileListeners(5, mediaType, folderURL + fileName);
      }
   }

   final void processFileAddedJournal(String param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aconst_null
      // 01: astore 2
      // 02: aload 1
      // 03: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 06: checkcast javax/microedition/io/file/FileConnection
      // 09: astore 2
      // 0a: aload 2
      // 0b: invokeinterface javax/microedition/io/file/FileConnection.isHidden ()Z 1
      // 10: ifne 40
      // 13: aload 0
      // 14: aload 1
      // 15: aload 0
      // 16: aload 1
      // 17: invokespecial net/rim/apps/internal/explorer/IndexService/FileIndexServiceImpl.indexOfFileName (Ljava/lang/String;)I
      // 1a: aload 1
      // 1b: invokestatic net/rim/device/api/io/MIMETypeAssociations.getMediaType (Ljava/lang/String;)I
      // 1e: bipush 1
      // 1f: aconst_null
      // 20: invokespecial net/rim/apps/internal/explorer/IndexService/FileIndexServiceImpl.incrementMediaCount (Ljava/lang/String;III[I)[I
      // 23: pop
      // 24: aload 2
      // 25: dup
      // 26: instanceof net/rim/device/api/io/file/ExtendedFileConnection
      // 29: ifne 30
      // 2c: pop
      // 2d: goto 40
      // 30: checkcast net/rim/device/api/io/file/ExtendedFileConnection
      // 33: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.isContentBuiltIn ()Z 1
      // 38: ifeq 40
      // 3b: aload 0
      // 3c: aload 1
      // 3d: invokevirtual net/rim/apps/internal/explorer/IndexService/FileIndexServiceImpl.processAddFilePath (Ljava/lang/String;)V
      // 40: aload 2
      // 41: ifnull 7d
      // 44: aload 2
      // 45: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 4a: return
      // 4b: astore 3
      // 4c: return
      // 4d: astore 3
      // 4e: aload 2
      // 4f: ifnull 7d
      // 52: aload 2
      // 53: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 58: return
      // 59: astore 3
      // 5a: return
      // 5b: astore 3
      // 5c: aload 2
      // 5d: ifnull 7d
      // 60: aload 2
      // 61: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 66: return
      // 67: astore 3
      // 68: return
      // 69: astore 4
      // 6b: aload 2
      // 6c: ifnull 7a
      // 6f: aload 2
      // 70: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 75: goto 7a
      // 78: astore 5
      // 7a: aload 4
      // 7c: athrow
      // 7d: return
      // try (34 -> 36): 37 null
      // try (2 -> 32): 39 null
      // try (42 -> 44): 45 null
      // try (2 -> 32): 47 null
      // try (50 -> 52): 53 null
      // try (2 -> 32): 55 null
      // try (39 -> 40): 55 null
      // try (47 -> 48): 55 null
      // try (58 -> 60): 61 null
      // try (55 -> 56): 55 null
   }

   final void processFileChangedJournal(String fileURL) {
      int mediaType = MIMETypeAssociations.getMediaType(fileURL);
      if (isMetaDataSupportedForMediaType(mediaType, fileURL)) {
         if (this.isViewable(fileURL)) {
            if (mediaType != 1) {
               this.processAddFilePath(fileURL);
               return;
            }

            this._scanner.addFilePath(mediaType, fileURL);
            return;
         }
      } else if (this.haveFilteredFileListener(mediaType) && this.isViewable(fileURL)) {
         this.notifyFilteredFileListeners(5, mediaType, fileURL);
      }
   }

   final void processAddFolderPath(String folderURL) {
      synchronized (this) {
         this._scanInProgress++;
      }

      Enumeration files = this.enumerateFiles(folderURL);
      int[] counts = null;
      if (files != null) {
         while (files.hasMoreElements()) {
            Object element = files.nextElement();
            String fileName;
            boolean isDirectory;
            if (!(element instanceof FileInfo)) {
               if (!(element instanceof String)) {
                  continue;
               }

               fileName = FileUtilities.encodeString((String)element);
               isDirectory = FileUtilities.isDirectory(fileName);
            } else {
               FileInfo info = (FileInfo)element;
               fileName = FileUtilities.encodeString(info.getFileName());
               int attributes = info.getAttributes();
               isDirectory = (attributes & 16) != 0;
               if (isDirectory && !fileName.endsWith("/")) {
                  fileName = fileName + '/';
               }

               if ((attributes & 8) != 0) {
                  if (isDirectory) {
                     this.addHiddenFolder(folderURL + fileName);
                  }
                  continue;
               }
            }

            if (isDirectory) {
               this._scanner.addFolderPath(folderURL + fileName);
            } else {
               int mediaType = MIMETypeAssociations.getMediaType(fileName);
               if (isMetaDataSupportedForMediaType(mediaType, fileName)) {
                  counts = this.incrementMediaCount(folderURL, folderURL.length(), mediaType, 1, counts);
                  this._scanner.addFilePath(mediaType, folderURL, fileName);
               } else if (this.haveFilteredFileListener(mediaType)) {
                  this.notifyFilteredFileListeners(5, mediaType, folderURL + fileName);
               }
            }
         }
      }

      this._scanner.scanComplete();
      if (counts == null) {
         int mediaType = this._preCreatedMediaFolders.get(folderURL);
         if (mediaType > 0) {
            this.incrementMediaCount(folderURL, folderURL.length(), mediaType, 0, counts);
         }
      }
   }

   final void processRescan(String folderURL) {
      Enumeration files = this.enumerateFiles(folderURL);
      int[] counts = null;
      if (files != null) {
         while (files.hasMoreElements()) {
            Object element = files.nextElement();
            String fileName;
            if (!(element instanceof FileInfo)) {
               if (!(element instanceof String)) {
                  continue;
               }

               fileName = FileUtilities.encodeString((String)element);
               if (FileUtilities.isDirectory(fileName)) {
                  continue;
               }
            } else {
               FileInfo info = (FileInfo)element;
               int attributes = info.getAttributes();
               if ((attributes & 24) != 0) {
                  continue;
               }

               fileName = FileUtilities.encodeString(info.getFileName());
            }

            int mediaType = MIMETypeAssociations.getMediaType(fileName);
            if (isMetaDataSupportedForMediaType(mediaType, fileName)) {
               if (counts == null) {
                  counts = new int[4];
               }

               counts = this.incrementMediaCount(folderURL, folderURL.length(), mediaType, 1, counts);
            }
         }
      }

      Object old = null;
      int[] oldCounts = null;
      if (counts == null) {
         int mediaType = this._preCreatedMediaFolders.get(folderURL);
         if (mediaType > 0) {
            counts = new int[4];
         } else {
            old = this._mediaFolderList.remove(folderURL);
         }
      }

      if (counts != null) {
         old = this._mediaFolderList.put(folderURL, counts);
      }

      if (old instanceof int[]) {
         oldCounts = (int[])old;
      }

      int mediaType = Math.max(counts == null ? 0 : counts.length, oldCounts == null ? 0 : oldCounts.length);

      while (--mediaType > 0) {
         if (this.haveFilteredFolderListener(mediaType)) {
            int diffMask = (counts != null && counts[mediaType] > 0 ? 2 : 0) + (oldCounts != null && oldCounts[mediaType] > 0 ? 1 : 0);
            switch (diffMask) {
               case 0:
                  break;
               case 1:
               default:
                  this.notifyFilteredFolderListeners(6, mediaType, folderURL);
                  break;
               case 2:
                  this.notifyFilteredFolderListeners(7, mediaType, folderURL);
            }
         }
      }
   }

   final void processRemoveFilePath(String pathURL, String fileName) {
      if (this._mediaFolderList.containsKey(pathURL)) {
         this.onFileDeleted(pathURL, fileName);
         this.decrementMediaCount(pathURL, pathURL.length(), MIMETypeAssociations.getMediaType(fileName));
      }
   }

   final void processRemoveFolderPath(String rootURL) {
      int rootURLlength = rootURL.length();
      Enumeration folders = this._mediaFolderList.keys();

      while (folders.hasMoreElements()) {
         String folderURL = (String)folders.nextElement();
         if (folderURL.length() >= rootURLlength && folderURL.regionMatches(true, 0, rootURL, 0, rootURLlength)) {
            Object o = this._mediaFolderList.remove(folderURL);
            if (o instanceof int[]) {
               int[] counts = (int[])o;
               int mediaType = counts.length;

               while (--mediaType > 0) {
                  if (this.haveFilteredFolderListener(mediaType)) {
                     this.notifyFilteredFolderListeners(6, mediaType, folderURL);
                  }
               }
            }
         }
      }

      this.notifyMetaDataListeners(0, rootURL, null, null);
   }

   private static final boolean isMetaDataSupportedForMediaType(int mediaType, String name) {
      switch (mediaType) {
         case 0:
            return false;
         case 1:
         case 3:
            return true;
         case 2:
         default:
            String type = MIMETypeAssociations.getMIMEType(name);
            if (type != null) {
               int i = ExcludedAudioMIMETypes.length;

               while (--i >= 0) {
                  if (StringUtilities.strEqualIgnoreCase(type, ExcludedAudioMIMETypes[i], 1701707776)) {
                     return false;
                  }
               }
            }

            return true;
      }
   }

   @Override
   protected final boolean mediaFolderExists(String folder) {
      String folderURL = FileUtilities.makeFileURL(folder);
      if (this._mediaFolderList.containsKey(folderURL)) {
         return true;
      } else {
         return this.isScanInProgress() ? FileUtilities.checkFileExists(folderURL) : false;
      }
   }

   @Override
   protected final boolean isPrecreatedFolder(String folder) {
      if (!folder.endsWith("/")) {
         folder = folder + "/";
      }

      String folderURL = FileUtilities.makeFileURL(folder);
      Enumeration preCreated = this._preCreatedMediaFolders.keys();

      while (preCreated.hasMoreElements()) {
         String element = (String)preCreated.nextElement();
         if (element.startsWith(folderURL)) {
            return true;
         }
      }

      return false;
   }

   private final void addHiddenFolder(String folderURL) {
      synchronized (this._hiddenFolders) {
         int i = this._hiddenFolders.length;

         while (true) {
            if (--i < 0) {
               i = 0;
               break;
            }

            int v = StringUtilities.compareToIgnoreCase(folderURL, this._hiddenFolders[i], 1701707776);
            if (v == 0) {
               return;
            }

            if (v > 0) {
               i++;
               break;
            }
         }

         Arrays.insertAt(this._hiddenFolders, folderURL, i);
      }

      this.processRemoveFolderPath(folderURL);
   }

   private final void removeHiddenFolder(String folderURL) {
      synchronized (this._hiddenFolders) {
         int i = this._hiddenFolders.length;

         while (--i >= 0) {
            int v = StringUtilities.compareToIgnoreCase(folderURL, this._hiddenFolders[i], 1701707776);
            if (v == 0) {
               Arrays.removeAt(this._hiddenFolders, i);
               break;
            }

            if (v > 0) {
               break;
            }
         }
      }
   }

   private final boolean isUnderAHiddenFolder(String fileURL) {
      synchronized (this._hiddenFolders) {
         int i = this._hiddenFolders.length;

         while (--i >= 0) {
            if (StringUtilities.startsWithIgnoreCase(fileURL, this._hiddenFolders[i], 1701707776)) {
               return true;
            }
         }

         return false;
      }
   }

   private final boolean isViewable(String param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: aload 1
      // 02: invokespecial net/rim/apps/internal/explorer/IndexService/FileIndexServiceImpl.isUnderAHiddenFolder (Ljava/lang/String;)Z
      // 05: ifeq 0a
      // 08: bipush 0
      // 09: ireturn
      // 0a: aconst_null
      // 0b: astore 2
      // 0c: aload 1
      // 0d: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 10: checkcast javax/microedition/io/file/FileConnection
      // 13: astore 2
      // 14: aload 2
      // 15: invokeinterface javax/microedition/io/file/FileConnection.isHidden ()Z 1
      // 1a: ifne 21
      // 1d: bipush 1
      // 1e: goto 22
      // 21: bipush 0
      // 22: istore 3
      // 23: aload 2
      // 24: ifnull 31
      // 27: aload 2
      // 28: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 2d: iload 3
      // 2e: ireturn
      // 2f: astore 4
      // 31: iload 3
      // 32: ireturn
      // 33: astore 3
      // 34: aload 2
      // 35: ifnull 67
      // 38: aload 2
      // 39: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 3e: bipush 0
      // 3f: ireturn
      // 40: astore 3
      // 41: bipush 0
      // 42: ireturn
      // 43: astore 3
      // 44: aload 2
      // 45: ifnull 67
      // 48: aload 2
      // 49: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 4e: bipush 0
      // 4f: ireturn
      // 50: astore 3
      // 51: bipush 0
      // 52: ireturn
      // 53: astore 5
      // 55: aload 2
      // 56: ifnull 64
      // 59: aload 2
      // 5a: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 5f: goto 64
      // 62: astore 6
      // 64: aload 5
      // 66: athrow
      // 67: bipush 0
      // 68: ireturn
      // try (21 -> 23): 25 null
      // try (8 -> 19): 28 null
      // try (31 -> 33): 35 null
      // try (8 -> 19): 38 null
      // try (41 -> 43): 45 null
      // try (8 -> 19): 48 null
      // try (28 -> 29): 48 null
      // try (38 -> 39): 48 null
      // try (51 -> 53): 54 null
      // try (48 -> 49): 48 null
   }

   @Override
   public final void fileJournalChanged() {
      long nextUSN = FileSystemJournal.getNextUSN();

      for (long lookUSN = nextUSN - 1; lookUSN >= this._myLastUSN; lookUSN -= 1) {
         FileSystemJournalEntry entry = FileSystemJournal.getEntry(lookUSN);
         if (entry == null) {
            break;
         }

         String pathURL = FileUtilities.makeFileURL(entry.getPath());
         switch (entry.getEvent()) {
            case -1:
               break;
            case 0:
               if (FileUtilities.isDirectory(pathURL)) {
                  this._scanner.addFolderPath(pathURL);
               } else {
                  int mediaType = MIMETypeAssociations.getMediaType(pathURL);
                  if (isMetaDataSupportedForMediaType(mediaType, pathURL) && !this.isUnderAHiddenFolder(pathURL)) {
                     this._scanner.addFileAddedJournal(pathURL);
                  }
               }
               break;
            case 1:
               if (FileUtilities.isDirectory(pathURL)) {
                  this._scanner.removeFolderPath(pathURL);
               } else {
                  int mediaType = MIMETypeAssociations.getMediaType(pathURL);
                  if (isMetaDataSupportedForMediaType(mediaType, pathURL)) {
                     this._scanner.removeFilePath(mediaType, pathURL);
                  } else if (this.haveFilteredFileListener(mediaType)) {
                     this.notifyFilteredFileListeners(4, mediaType, FileUtilities.makeFileURL(pathURL));
                  }
               }
               break;
            case 3:
            default:
               String oldPathURL = FileUtilities.makeFileURL(entry.getOldPath());
               if (FileUtilities.isDirectory(oldPathURL)) {
                  this._scanner.removeFolderPath(oldPathURL);
               } else {
                  int mediaType = MIMETypeAssociations.getMediaType(oldPathURL);
                  if (isMetaDataSupportedForMediaType(mediaType, oldPathURL)) {
                     this.decrementMediaCount(oldPathURL, this.indexOfFileName(oldPathURL), mediaType);
                     this._scanner.removeFilePath(mediaType, oldPathURL);
                  } else if (this.haveFilteredFileListener(mediaType)) {
                     this.notifyFilteredFileListeners(4, mediaType, oldPathURL);
                  }
               }
            case 2:
               if (FileUtilities.isDirectory(pathURL)) {
                  this._scanner.addFolderPath(pathURL);
               } else {
                  this._scanner.addFileChangedJournal(pathURL);
               }
         }
      }

      this._myLastUSN = nextUSN;
   }

   private final int indexOfFileName(String path) {
      int pathLength = path.length();
      if (pathLength > 1) {
         pathLength = path.lastIndexOf(47, pathLength - 2);
         if (pathLength < 0) {
            return path.length();
         }

         pathLength++;
      }

      return pathLength;
   }

   private final int[] incrementMediaCount(String path, int endIndexOfPath, int mediaType, int count, int[] mediaCounts) {
      String folderPath = null;
      if (mediaCounts == null) {
         Object o = this._mediaFolderList.get(path, 0, endIndexOfPath);
         if (!(o instanceof int[])) {
            mediaCounts = new int[4];
            folderPath = path.substring(0, endIndexOfPath);
            this._mediaFolderList.put(folderPath, mediaCounts);
         } else {
            mediaCounts = (int[])o;
         }
      }

      if (mediaType < mediaCounts.length) {
         if ((mediaCounts[mediaType] += count) == count && this.haveFilteredFolderListener(mediaType)) {
            if (folderPath == null) {
               folderPath = path.substring(0, endIndexOfPath);
            }

            this.notifyFilteredFolderListeners(7, mediaType, folderPath);
         }

         mediaCounts[0] += count;
      }

      return mediaCounts;
   }

   private final void decrementMediaCount(String path, int endIndexOfPath, int mediaType) {
      Object o = this._mediaFolderList.get(path, 0, endIndexOfPath);
      if (o instanceof int[]) {
         int[] mediaCounts = (int[])o;
         int count = mediaCounts[mediaType];
         if (count > 0) {
            if (--count <= 0) {
               this._scanner.rescan(path);
               return;
            }

            mediaCounts[mediaType] = count;
            count = mediaCounts[0];
            if (count > 0) {
               mediaCounts[0] = count - 1;
            }
         }
      }
   }

   @Override
   public final void rootChanged(int state, String rootName) {
      switch (state) {
         case 0:
         default:
            synchronized (this) {
               this._scanInProgress++;
            }

            this._scanner.addFolderPath("/" + rootName);
            this._scanner.scanComplete();
            return;
         case 1:
            this._scanner.removeFolderPath("/" + rootName);
         case -1:
      }
   }

   @Override
   public final Enumeration metaDataFolders() {
      return new FileIndexServiceImpl$MetaDataFolderEnumeration(this, this._mediaFolderList.keys(), 0);
   }

   @Override
   public final Enumeration metaDataFolders(int mediaType) {
      return new FileIndexServiceImpl$MetaDataFolderEnumeration(this, this._mediaFolderList.keys(), mediaType);
   }
}
