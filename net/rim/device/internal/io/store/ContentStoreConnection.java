package net.rim.device.internal.io.store;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import net.rim.device.api.io.ConnectionClosedException;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.Memory;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.utility.URIDecoder;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.io.FilenameValidator;
import net.rim.device.internal.io.file.BaseFileConnection;
import net.rim.device.internal.io.file.FileSystem;
import net.rim.device.internal.io.file.FileSystemOptions;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.vm.TraceBack;

public final class ContentStoreConnection implements BaseFileConnection {
   private boolean _opened;
   private String _path;
   private String _host;
   private ContentStoreInputStream _inputStream;
   private ContentStoreOutputStream _outputStream;
   private ContentStoreImpl _contentStore;
   private int _accessMode;
   private CodeSigningKey _controlledAccessOnCreate;
   private boolean _drmLockedOnCreate;
   private static String FILE_SYSTEM_NAME_STORE = "store";
   private static String SLASH = "/";
   private static String SLASH_SLASH_LOCALHOST_SLASH = "//localhost/";
   private static String SLASH_SLASH_SLASH = "///";
   private static final int MAX_PATH = 256;
   static Class class$net$rim$device$internal$io$store$ContentStoreConnection;

   @Override
   public final void close() {
      this._opened = false;
      this._inputStream = null;
      this._outputStream = null;
   }

   final void outputClosed() {
      this._outputStream = null;
   }

   final void inputClosed() {
      this._inputStream = null;
   }

   @Override
   public final void init(String name, int mode, boolean timeouts) {
      this._accessMode = mode;
      int rootPos = 0;
      if (name.startsWith(SLASH_SLASH_SLASH)) {
         rootPos = SLASH_SLASH_SLASH.length();
         this._host = "";
      } else {
         if (!StringUtilities.startsWithIgnoreCase(name, SLASH_SLASH_LOCALHOST_SLASH, 1701707776)) {
            throw new Object(1003);
         }

         rootPos = SLASH_SLASH_LOCALHOST_SLASH.length();
         this._host = name.substring(2, 11);
      }

      if (name.regionMatches(false, rootPos, FILE_SYSTEM_NAME_STORE, 0, FILE_SYSTEM_NAME_STORE.length())) {
         name = name.substring(rootPos + FILE_SYSTEM_NAME_STORE.length());
         if (name.length() == 0) {
            name = SLASH;
         }

         if (!FilenameValidator.validateFilenameAndPath(name)) {
            throw new Object("invalid character in name");
         }

         if (name.length() > 256) {
            throw new Object(13);
         }

         this._contentStore = ContentStoreImpl.getInstance();
         this._path = name;
         this._opened = true;
      } else {
         throw new Object(1003);
      }
   }

   @Override
   public final boolean isOpen() {
      return this._opened;
   }

   @Override
   public final InputStream openInputStream() {
      int caller = TraceBack.getCallingModule(0);
      return this.openInputStreamInternal(caller, false);
   }

   @Override
   public final DataInputStream openDataInputStream() {
      int caller = TraceBack.getCallingModule(0);
      return (DataInputStream)(new Object(this.openInputStreamInternal(caller, false)));
   }

   @Override
   public final OutputStream openOutputStream() {
      int caller = TraceBack.getCallingModule(0);
      return this.openOutputStreamInternal(0, caller);
   }

   @Override
   public final DataOutputStream openDataOutputStream() {
      int caller = TraceBack.getCallingModule(0);
      return (ContentStoreOutputStream)this.openOutputStreamInternal(0, caller);
   }

   @Override
   public final OutputStream openOutputStream(long offset) {
      int caller = TraceBack.getCallingModule(0);
      return this.openOutputStreamInternal(offset, caller);
   }

   @Override
   public final long totalSize() {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertReadPermission();
      return Math.max(FileSystemOptions.getContentStoreTotalSize(), this._contentStore.getQuotaUsed());
   }

   @Override
   public final long availableSize() {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertReadPermission();
      long available;
      if (this._path.startsWith("/home/user/pictures/")) {
         long picturesQuotaUsed = this._contentStore.getPicturesQuotaUsed();
         long picturesReservedSize = FileSystemOptions.getPicturesReservedSize();
         if (picturesQuotaUsed < picturesReservedSize) {
            available = FileSystemOptions.getContentStoreTotalSize() - this._contentStore.getQuotaUsed() - picturesReservedSize + picturesQuotaUsed;
            if (available < 0) {
               available = 0;
            }

            available += picturesReservedSize - picturesQuotaUsed;
         } else {
            available = Math.max(FileSystemOptions.getContentStoreTotalSize() - this._contentStore.getQuotaUsed(), 0);
         }
      } else {
         available = Math.max(0, this.totalSize() - this.usedSize());
      }

      return Math.min(available, Memory.getFlashFree());
   }

   @Override
   public final long usedSize() {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertReadPermission();
      if (this._path.startsWith("/home/user/pictures/")) {
         return this._contentStore.getQuotaUsed();
      }

      long picturesReservedSize = FileSystemOptions.getPicturesReservedSize();
      long picturesQuotaUsed = this._contentStore.getPicturesQuotaUsed();
      long usedSize = this._contentStore.getQuotaUsed();
      if (picturesQuotaUsed < picturesReservedSize) {
         usedSize += picturesReservedSize - picturesQuotaUsed;
      }

      return usedSize;
   }

   @Override
   public final long directorySize(boolean includeSubDirs) {
      if (!this.isOpen()) {
         throw new Object();
      } else {
         this.assertReadPermission();
         FSDescriptor fd = this.getFSDescriptor();
         if (fd == null) {
            return -1;
         } else if (!(fd instanceof FolderImpl)) {
            throw new Object(1002);
         } else {
            return this.directorySizeInternal((FolderImpl)fd, includeSubDirs);
         }
      }
   }

   @Override
   public final long fileSize() {
      return this.fileSizeInternal(false);
   }

   @Override
   public final long rawFileSize() {
      return this.fileSizeInternal(true);
   }

   @Override
   public final boolean canRead() {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertReadPermission();
      FSDescriptor fd = this.getFSDescriptor();
      if (fd == null) {
         return false;
      }

      try {
         return (fd.getAttributes() & 1) != 0;
      } finally {
         ;
      }
   }

   @Override
   public final boolean canWrite() {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertReadPermission();
      FSDescriptor fd = this.getFSDescriptor();
      if (fd == null) {
         return false;
      }

      try {
         return (fd.getAttributes() & 2) != 0;
      } finally {
         ;
      }
   }

   @Override
   public final boolean isHidden() {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertReadPermission();
      FSDescriptor fd = this.getFSDescriptor();
      if (fd == null) {
         return false;
      }

      try {
         return (fd.getAttributes() & 8) != 0;
      } finally {
         ;
      }
   }

   @Override
   public final void setReadable(boolean readable) {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertWritePermission();
      FSDescriptor fd = this.getFSDescriptor();
      if (fd == null) {
         throw new Object(8);
      }

      if ((fd.getAttributes() & 16) != 0) {
         throw new Object(12);
      }

      if (readable) {
         fd.setAttributes(1, 0);
      } else {
         fd.setAttributes(0, 1);
      }
   }

   @Override
   public final void setWritable(boolean writable) {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertWritePermission();
      FSDescriptor fd = this.getFSDescriptor();
      if (fd == null) {
         throw new Object(8);
      }

      if ((fd.getAttributes() & 16) != 0) {
         throw new Object(12);
      }

      if (writable) {
         fd.setAttributes(2, 0);
      } else {
         fd.setAttributes(0, 2);
      }
   }

   @Override
   public final void setHidden(boolean hidden) {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertWritePermission();
      FSDescriptor fd = this.getFSDescriptor();
      if (fd == null) {
         throw new Object(8);
      }

      if (hidden) {
         fd.setAttributes(8, 0);
      } else {
         fd.setAttributes(0, 8);
      }
   }

   @Override
   public final Enumeration list() {
      return this.list("*", false);
   }

   @Override
   public final Enumeration list(String filter, boolean includeHidden) {
      return this.listWithFilter(filter, includeHidden, false);
   }

   @Override
   public final Enumeration listWithDetails(String filter, boolean includeHidden) {
      return this.listWithFilter(filter, includeHidden, true);
   }

   @Override
   public final void create() {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertWritePermission();
      FSDescriptor fd = this.getFSDescriptor();
      if (fd != null) {
         throw new Object(7);
      }

      if (this._path.endsWith(SLASH)) {
         throw new Object(1006);
      }

      FileImpl file = this._contentStore.addFile(this._path, 1);
      if (file == null) {
         throw new Object("Failed to create file");
      }

      if (this._controlledAccessOnCreate != null) {
         file.setCodeSigningKey(this._controlledAccessOnCreate);
      }

      if (this._drmLockedOnCreate) {
         file.setDrmAttributes(1, 0);
      }
   }

   @Override
   public final void mkdir() {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertWritePermission();
      FSDescriptor fd = this.getFSDescriptor();
      if (fd != null) {
         throw new Object(15);
      }

      if (!this._path.endsWith(SLASH)) {
         throw new Object(1002);
      }

      fd = this.getFSDescriptor(this.getPlatformPath());
      if (fd == null) {
         throw new Object("Parent doesn't exist");
      }

      this._contentStore.addFolder(this._path);
   }

   @Override
   public final boolean exists() {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertReadPermission();
      return this.getFSDescriptor() != null;
   }

   @Override
   public final boolean isDirectory() {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertReadPermission();
      return this.getFSDescriptor() instanceof FolderImpl;
   }

   @Override
   public final void delete() {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertWritePermission();
      FSDescriptor fd = this.getFSDescriptor();
      if (fd == null) {
         throw new Object(8);
      }

      if (fd instanceof SymbolicLinkImpl) {
         ControlledAccess.assertRRISignatures(true);
      }

      this.closeStreams();
      if (fd instanceof FolderImpl && this.listInternal((FolderImpl)fd, null, true, false).hasMoreElements()) {
         throw new Object(17);
      }

      fd.remove();
   }

   @Override
   public final int getSupportFlags() {
      return 0;
   }

   @Override
   public final void renameEx(String newName) {
      throw new Object(12);
   }

   @Override
   public final void rename(String newName) {
      if (!this.isOpen()) {
         throw new Object();
      }

      if (newName == null) {
         throw new Object();
      }

      this.assertWritePermission();
      FSDescriptor fd = this.getFSDescriptor();
      if (fd == null) {
         throw new Object(8);
      }

      if (fd instanceof SymbolicLinkImpl) {
         throw new Object(1007);
      }

      newName = URIDecoder.decode(newName, "UTF-8", false);
      if (newName.indexOf(47) != -1) {
         throw new Object("new name contains path");
      }

      if (!FilenameValidator.validateFilenameAndPath(newName)) {
         throw new Object(1004);
      }

      this.closeStreams();
      String newPath = ((StringBuffer)(new Object())).append(this.getPlatformPath()).append(newName).toString();
      FSDescriptor otherFd = this.getFSDescriptor(newPath);
      if (otherFd != null && otherFd != fd) {
         throw new Object(7);
      }

      fd.setName(newName);
      this._path = newPath;
   }

   @Override
   public final void truncate(long byteOffset) {
      if (!this.isOpen()) {
         throw new Object();
      }

      if (byteOffset < 0) {
         throw new Object("offset is negative");
      }

      this.assertWritePermission();
      FSDescriptor fd = this.getFSDescriptor();
      if (fd == null) {
         throw new Object(8);
      }

      if (fd instanceof SymbolicLinkImpl) {
         throw new Object(1007);
      }

      if (!(fd instanceof FileImpl)) {
         throw new Object(1001);
      }

      if (this._outputStream != null) {
         label43:
         try {
            this._outputStream.flush();
         } finally {
            break label43;
         }
      }

      ((FileImpl)fd).truncate(byteOffset);
   }

   @Override
   public final void setFileConnection(String fileName) {
      if (!this.isOpen()) {
         throw new Object();
      }

      FSDescriptor fd = this.getFSDescriptor();
      if (fd == null) {
         throw new Object(16);
      }

      if (!(fd instanceof FolderImpl)) {
         throw new Object("setFileConnection called on a file");
      }

      if (!this._path.endsWith(SLASH)) {
         this._path = ((StringBuffer)(new Object())).append(this._path).append('/').toString();
      }

      if (fileName == null) {
         throw new Object();
      }

      if (!fileName.equals(".")) {
         fileName = URIDecoder.decode(fileName, "UTF-8", false);
         if (!FilenameValidator.validateFilenameAndPath(fileName)) {
            throw new Object(1004);
         }

         if (fileName.equals("..")) {
            if (SLASH.equals(this._path)) {
               throw new Object("cannot access parent of root folder");
            }

            this._path = this.getPlatformPath();
         } else {
            int indexOfSlash = fileName.indexOf(47);
            if (indexOfSlash != -1 && indexOfSlash != fileName.length() - 1) {
               throw new Object("path strucutre contained in fileName");
            }

            boolean isDir = indexOfSlash != -1;
            String newPath = ((StringBuffer)(new Object())).append(this._path).append(fileName).toString();
            FSDescriptor fsDescriptor = this.getFSDescriptor(newPath);
            if (fsDescriptor == null) {
               throw new Object("new path to file not found");
            }

            if (isDir && !(fsDescriptor instanceof FolderImpl)) {
               throw new Object("new path is not a directory");
            }

            if (newPath.length() > 256) {
               throw new Object(13);
            }

            this._path = newPath;
         }
      }
   }

   @Override
   public final String getName() {
      if (this._path.equals(SLASH)) {
         return "";
      }

      String path = this._path;
      boolean isDir = false;
      boolean endsWithSlash = path.endsWith(SLASH);
      FSDescriptor fd = this.getFSDescriptor();
      if (fd instanceof FolderImpl) {
         isDir = true;
      } else if (fd == null) {
         isDir = endsWithSlash;
      }

      int endPos = path.length();
      if (endsWithSlash) {
         endPos--;
      }

      int lastSlash = path.lastIndexOf(47, endPos - 1);
      if (lastSlash != -1) {
         if (isDir && endsWithSlash) {
            endPos++;
         }

         path = path.substring(lastSlash + 1, endPos);
      }

      if (isDir && !path.endsWith(SLASH)) {
         path = ((StringBuffer)(new Object())).append(path).append('/').toString();
      }

      return path;
   }

   @Override
   public final String getPath() {
      return ((StringBuffer)(new Object("/store"))).append(this.getPlatformPath()).toString();
   }

   @Override
   public final String getURL() {
      return ((StringBuffer)(new Object("file://")))
         .append(this._host)
         .append(FileUtilities.encodeString(((StringBuffer)(new Object())).append(this.getPath()).append(this.getName()).toString()))
         .toString();
   }

   @Override
   public final long lastModified() {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertReadPermission();
      FSDescriptor fd = this.getFSDescriptor();
      return !(fd instanceof FileImpl) ? 0 : ((FileImpl)fd).getTimeModify();
   }

   @Override
   public final boolean setControlledAccess(CodeSigningKey csk) {
      this.assertWritePermission();
      if (!this.isOpen()) {
         throw new ConnectionClosedException();
      } else {
         FSDescriptor fd = this.getFSDescriptor();
         if (fd != null) {
            throw new Object(7);
         } else if (this._path.endsWith(SLASH)) {
            throw new Object(1006);
         } else if (csk != null && this._controlledAccessOnCreate == null) {
            this._controlledAccessOnCreate = csk;
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public final CodeSigningKey getControlledAccess() {
      this.assertReadPermission();
      if (!this.isOpen()) {
         throw new ConnectionClosedException();
      }

      FSDescriptor fd = this.getFSDescriptor();
      if (!(fd instanceof FileImpl)) {
         if (fd instanceof FolderImpl) {
            throw new Object(1001);
         } else if (this._path.endsWith(SLASH)) {
            throw new Object(1006);
         } else {
            return this._controlledAccessOnCreate;
         }
      } else {
         return ((FileImpl)fd).getCodeSigningKey();
      }
   }

   @Override
   public final void enableDRMForwardLock() {
      this.assertWritePermission();
      if (!this.isOpen()) {
         throw new ConnectionClosedException();
      }

      FSDescriptor fd = this.getFSDescriptor();
      if (fd != null) {
         throw new Object(7);
      }

      this._drmLockedOnCreate = true;
   }

   @Override
   public final boolean isContentDRMForwardLocked() {
      this.assertReadPermission();
      if (!this.isOpen()) {
         throw new ConnectionClosedException();
      }

      FSDescriptor fd = this.getFSDescriptor();
      if (fd == null) {
         throw new Object(8);
      }

      if (!(fd instanceof FileImpl)) {
         throw new Object(1001);
      }

      FileImpl file = (FileImpl)fd;
      return (file.getDrmAttributes() & 1) != 0;
   }

   @Override
   public final boolean isFileEncrypted() {
      return this.isContentDRMForwardLocked();
   }

   @Override
   public final InputStream openRawInputStream() {
      int caller = TraceBack.getCallingModule(0);
      return this.openInputStreamInternal(caller, true);
   }

   @Override
   public final boolean isContentBuiltIn() {
      this.assertReadPermission();
      if (!this.isOpen()) {
         throw new ConnectionClosedException();
      } else {
         FSDescriptor fd = this.getFSDescriptor();
         if (fd == null) {
            throw new Object(8);
         } else if (fd instanceof FolderImpl) {
            throw new Object(1001);
         } else {
            return fd instanceof SymbolicLinkImpl;
         }
      }
   }

   @Override
   public final void setAutoEncryptionResolveMode(boolean mode) {
      if (!this.isOpen()) {
         throw new ConnectionClosedException();
      }
   }

   public static final void register() {
      FileSystem.mount(
         -1,
         "store/",
         class$net$rim$device$internal$io$store$ContentStoreConnection == null
            ? (class$net$rim$device$internal$io$store$ContentStoreConnection = class$("net.rim.device.internal.io.store.ContentStoreConnection"))
            : class$net$rim$device$internal$io$store$ContentStoreConnection
      );
   }

   private final OutputStream openOutputStreamInternal(long offset, int callerModule) {
      this.assertWritePermission();
      if (offset < 0) {
         throw new Object("offset is negative");
      }

      if (!this.isOpen()) {
         throw new ConnectionClosedException();
      }

      if (this._outputStream != null) {
         throw new Object(1005);
      }

      FSDescriptor fd = this.getFSDescriptor();
      if (fd == null) {
         throw new Object(8);
      }

      if (fd instanceof SymbolicLinkImpl) {
         throw new Object(1007);
      }

      if (!(fd instanceof FileImpl)) {
         throw new Object(1001);
      }

      FileImpl file = (FileImpl)fd;
      CodeSigningKey key = file.getCodeSigningKey();
      if (key != null && !ControlledAccess.verifyCodeModuleSignature(callerModule, key)) {
         throw new Object(key);
      }

      this._outputStream = new ContentStoreOutputStream(this, file.openOutputStream(offset));
      return this._outputStream;
   }

   private final long directorySizeInternal(FolderImpl fd, boolean includeDirs) {
      long totalSize = 0;
      Enumeration entries = this.listInternal(fd, null, true, false);
      if (entries instanceof ListEnumeration) {
         ListEnumeration listEntries = (ListEnumeration)entries;

         while (listEntries.hasMoreElements()) {
            FSDescriptor entry = listEntries.nextDescriptor();
            if (includeDirs && entry instanceof FolderImpl) {
               totalSize += this.directorySizeInternal((FolderImpl)entry, includeDirs);
            } else if (entry instanceof FileImpl) {
               totalSize += ((FileImpl)entry).getLength();
            }
         }
      }

      return totalSize;
   }

   private final void assertReadPermission() {
      ApplicationControl.assertFileApiAllowed(true);
      if ((this._accessMode & 1) == 0) {
         throw new Object("Access mode read required");
      }
   }

   private final void assertWritePermission() {
      ApplicationControl.assertFileApiAllowed(true);
      if ((this._accessMode & 2) == 0) {
         throw new Object("Access mode write required");
      }
   }

   private final FSDescriptor getFSDescriptor() {
      return this.getFSDescriptor(this._path);
   }

   private final FSDescriptor getFSDescriptor(String param1) {
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
      // 03: invokevirtual java/lang/String.length ()I
      // 06: bipush 1
      // 07: if_icmple 20
      // 0a: aload 1
      // 0b: getstatic net/rim/device/internal/io/store/ContentStoreConnection.SLASH Ljava/lang/String;
      // 0e: invokevirtual java/lang/String.endsWith (Ljava/lang/String;)Z
      // 11: ifeq 20
      // 14: aload 1
      // 15: bipush 0
      // 16: aload 1
      // 17: invokevirtual java/lang/String.length ()I
      // 1a: bipush 1
      // 1b: isub
      // 1c: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 1f: astore 1
      // 20: aload 0
      // 21: getfield net/rim/device/internal/io/store/ContentStoreConnection._contentStore Lnet/rim/device/internal/io/store/ContentStoreImpl;
      // 24: aload 1
      // 25: invokevirtual net/rim/device/internal/io/store/ContentStoreImpl.get (Ljava/lang/String;)Lnet/rim/device/internal/io/store/FileImpl;
      // 28: astore 2
      // 29: aload 2
      // 2a: ifnonnull 52
      // 2d: aload 1
      // 2e: invokevirtual java/lang/String.length ()I
      // 31: bipush 1
      // 32: if_icmple 49
      // 35: new java/lang/Object
      // 38: dup
      // 39: invokespecial java/lang/StringBuffer.<init> ()V
      // 3c: aload 1
      // 3d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 40: bipush 47
      // 42: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 45: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 48: astore 1
      // 49: aload 0
      // 4a: getfield net/rim/device/internal/io/store/ContentStoreConnection._contentStore Lnet/rim/device/internal/io/store/ContentStoreImpl;
      // 4d: aload 1
      // 4e: invokevirtual net/rim/device/internal/io/store/ContentStoreImpl.getFolder (Ljava/lang/String;)Lnet/rim/device/internal/io/store/FolderImpl;
      // 51: astore 2
      // 52: aload 2
      // 53: ifnull 67
      // 56: aload 2
      // 57: invokeinterface net/rim/device/internal/io/store/FSDescriptor.isAlive ()Z 1
      // 5c: ifne 67
      // 5f: aconst_null
      // 60: astore 2
      // 61: aload 2
      // 62: areturn
      // 63: astore 3
      // 64: aload 2
      // 65: areturn
      // 66: astore 3
      // 67: aload 2
      // 68: areturn
      // try (2 -> 50): 52 null
      // try (2 -> 50): 55 null
   }

   private final void closeStreams() {
      if (this._inputStream != null) {
         this._inputStream.close();
         this._inputStream = null;
      }

      if (this._outputStream != null) {
         this._outputStream.close();
         this._outputStream = null;
      }
   }

   public static final long totalSizeUsed() {
      return ContentStoreImpl.getInstance().getQuotaUsed();
   }

   public static final long picturesTotalSizeUsed() {
      return ContentStoreImpl.getInstance().getPicturesQuotaUsed();
   }

   private final long fileSizeInternal(boolean raw) {
      if (!this.isOpen()) {
         if (raw) {
            throw new ConnectionClosedException();
         } else {
            throw new Object();
         }
      } else {
         this.assertReadPermission();
         FSDescriptor fd = this.getFSDescriptor();
         if (fd == null) {
            return -1;
         }

         if (!(fd instanceof FileImpl)) {
            throw new Object(1001);
         }

         FileImpl file = (FileImpl)fd;
         return raw ? file.getRawLength() : file.getLength();
      }
   }

   private final Enumeration listWithFilter(String filter, boolean includeHidden, boolean returnFileInfo) {
      if (!this.isOpen()) {
         if (returnFileInfo) {
            throw new ConnectionClosedException();
         } else {
            throw new Object();
         }
      } else {
         this.assertReadPermission();
         if (filter == null) {
            throw new Object();
         } else {
            filter = URIDecoder.decode(filter, "UTF-8", false);
            if (filter.indexOf(47) != -1) {
               throw new Object("filter contains path");
            } else if (!FilenameValidator.validateFilenameAndPath(filter.replace('*', 'a'))) {
               throw new Object("invalid character in filter");
            } else {
               FSDescriptor fd = this.getFSDescriptor();
               if (fd == null) {
                  throw new Object(16);
               } else if (!(fd instanceof FolderImpl)) {
                  throw new Object(1002);
               } else {
                  return this.listInternal((FolderImpl)fd, filter, includeHidden, returnFileInfo);
               }
            }
         }
      }
   }

   private final Enumeration listInternal(FolderImpl fd, String filter, boolean includeHidden, boolean returnFileInfo) {
      FileListCriteria criteria = new FileListCriteria();
      criteria.setRootFolder(fd.getPath(), false);
      criteria.setContentType(null);
      criteria.setAttributes(32, includeHidden ? 0 : 8);
      FileListImpl fileList = this._contentStore.list(criteria);
      return new ListEnumeration(fileList, filter, returnFileInfo);
   }

   private final InputStream openInputStreamInternal(int codeModuleCaller, boolean raw) {
      this.assertReadPermission();
      if (!this.isOpen()) {
         throw new ConnectionClosedException();
      }

      if (this._inputStream != null) {
         throw new Object(1005);
      }

      FSDescriptor fd = this.getFSDescriptor();
      if (fd == null) {
         throw new Object(8);
      }

      if (!(fd instanceof FileImpl)) {
         throw new Object(1001);
      }

      FileImpl file = (FileImpl)fd;
      CodeSigningKey key = file.getCodeSigningKey();
      if (key != null && !ControlledAccess.verifyCodeModuleSignature(codeModuleCaller, key)) {
         throw new Object(key);
      }

      this._inputStream = new ContentStoreInputStream(this, raw ? file.openRawInputStream() : file.openInputStream());
      return this._inputStream;
   }

   private final String getPlatformPath() {
      int searchPos = this._path.length();
      if (this._path.endsWith(SLASH)) {
         searchPos--;
      }

      if (searchPos > 0) {
         int lastSlash = this._path.lastIndexOf(47, searchPos - 1);
         return this._path.substring(0, lastSlash + 1);
      } else {
         return SLASH;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
