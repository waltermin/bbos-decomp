package net.rim.device.cldc.io.file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.io.ConnectionClosedException;
import net.rim.device.api.io.FileInfo;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.utility.URIDecoder;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.io.FilenameValidator;
import net.rim.device.internal.io.file.BaseFileConnection;
import net.rim.device.internal.io.file.EncryptedFile;
import net.rim.device.internal.io.file.FileSystem;
import net.rim.device.internal.io.file.FileSystemInfo;
import net.rim.device.internal.io.file.FileSystemOptions;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.vm.TraceBack;

public final class PosixFileConnection implements BaseFileConnection {
   private int _rootId = -1;
   private boolean _opened;
   private String _path;
   private String _host;
   private InputStream _inputStream;
   private PosixFileOutputStream _outputStream;
   private FileSystemInfo _fileSystemInfo;
   private FileInfo _fileInfo;
   private int _accessMode;
   private boolean _autoResolveEncryptedFiles = true;
   private boolean _drmLockedOnCreate;
   private CodeSigningKey _controlledAccessOnCreate;
   public static String ENCRYPTED_MEDIA_EXTENSION = ".rem";
   private static String FILE_SYSTEM_NAME_SDCARD = "SDCard";
   private static String SLASH = "/";
   private static String SLASH_SLASH_LOCALHOST_SLASH = "//localhost/";
   private static String SLASH_SLASH_SLASH = "///";
   private static String DOT = ".";
   private static String DOT_DOT = "..";

   @Override
   public final void close() {
      this._opened = false;
      this._inputStream = null;
      this._outputStream = null;
   }

   final String getJSRPath() {
      return FileUtilities.encodeString(((StringBuffer)(new Object())).append(this.getPath()).append(this.getName()).toString());
   }

   final void inputClosed() {
      this._inputStream = null;
   }

   final void outputClosed() {
      this._outputStream = null;
   }

   @Override
   public final boolean isOpen() {
      return this._opened;
   }

   @Override
   public final InputStream openInputStream() {
      int caller = TraceBack.getCallingModule(0);
      return this.openInputStreamInternal(caller, true, true);
   }

   @Override
   public final DataInputStream openDataInputStream() {
      int caller = TraceBack.getCallingModule(0);
      return (DataInputStream)(new Object(this.openInputStreamInternal(caller, true, true)));
   }

   @Override
   public final OutputStream openOutputStream() {
      int caller = TraceBack.getCallingModule(0);
      return this.openOutputStreamInternal(0, caller);
   }

   @Override
   public final DataOutputStream openDataOutputStream() {
      int caller = TraceBack.getCallingModule(0);
      return (DataOutputStream)(new Object(this.openOutputStreamInternal(0, caller)));
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

      try {
         checkStatus(FileSystem.getFileSystemInfo(this._rootId, this._fileSystemInfo));
         return this._fileSystemInfo.getTotalSpace();
      } finally {
         return 0;
      }
   }

   @Override
   public final long availableSize() {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertReadPermission();

      try {
         checkStatus(FileSystem.getFileSystemInfo(this._rootId, this._fileSystemInfo));
         return this._fileSystemInfo.getFreeSpace();
      } finally {
         return 0;
      }
   }

   @Override
   public final long usedSize() {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertReadPermission();

      try {
         checkStatus(FileSystem.getFileSystemInfo(this._rootId, this._fileSystemInfo));
         return this._fileSystemInfo.getTotalSpace() - this._fileSystemInfo.getFreeSpace();
      } finally {
         return 0;
      }
   }

   @Override
   public final long directorySize(boolean includeSubDirs) {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertReadPermission();
      int attributes = this.getFileAttribNoThrow();
      if (attributes == -1) {
         return -1;
      }

      if ((attributes & 16) == 0) {
         throw new Object(1002);
      }

      if (!this._path.endsWith(SLASH)) {
         this._path = ((StringBuffer)(new Object())).append(this._path).append('/').toString();
      }

      return this.directorySizeInternal(this._path, includeSubDirs);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final long rawFileSize() {
      this.assertReadPermission();
      if (!this.isOpen()) {
         throw new ConnectionClosedException();
      }

      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         this.getFileAttrib();
         var3 = false;
      } finally {
         if (var3) {
            return -1;
         }
      }

      if ((this._fileInfo.getAttributes() & 16) != 0) {
         throw new Object(1001);
      } else {
         return this._fileInfo.getFileSize();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final long fileSize() {
      this.assertReadPermission();
      if (!this.isOpen()) {
         throw new Object();
      }

      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         this.getFileAttrib();
         var3 = false;
      } finally {
         if (var3) {
            return -1;
         }
      }

      if ((this._fileInfo.getAttributes() & 16) != 0) {
         throw new Object(1001);
      } else {
         return this.fileSizeInternal(this.isFileEncrypted());
      }
   }

   @Override
   public final boolean canRead() {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertReadPermission();
      int attributes = this.getFileAttribNoThrow();
      return attributes != -1;
   }

   @Override
   public final boolean canWrite() {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertReadPermission();
      int attributes = this.getFileAttribNoThrow();
      return attributes == -1 ? false : (attributes & 2) == 0;
   }

   @Override
   public final boolean isHidden() {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertReadPermission();
      int attributes = this.getFileAttribNoThrow();
      return attributes == -1 ? false : (attributes & 8) != 0;
   }

   @Override
   public final void setReadable(boolean readable) {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertWritePermission();
      this.getFileAttrib();
   }

   @Override
   public final void setWritable(boolean writable) {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertWritePermission();
      int attributes = this.getFileAttrib();
      if ((attributes & 2) == 0 != writable) {
         int newValue = writable ? attributes & -3 : attributes | 2;
         checkStatus(FileSystem.setFileAttrib(this._rootId, this._path, newValue));
      }
   }

   @Override
   public final void setHidden(boolean hidden) {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertWritePermission();
      int attributes = this.getFileAttrib();
      if ((attributes & 8) != 0 != hidden) {
         checkStatus(FileSystem.setFileAttrib(this._rootId, this._path, hidden ? attributes | 8 : attributes & -9));
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

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void create() {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertWritePermission();
      int attributes = this.getFileAttribNoThrow();
      if (attributes != -1) {
         throw new Object(7);
      }

      if (this._path.endsWith(SLASH)) {
         throw new Object(1006);
      }

      boolean encryptFile;
      switch (FileSystemOptions.getExternalEncryptionLevel()) {
         case -1:
         case 2:
         case 4:
            encryptFile = true;
            break;
         case 0:
         default:
            encryptFile = this._drmLockedOnCreate || this._controlledAccessOnCreate != null;
            break;
         case 1:
         case 3:
         case 5:
            if (FileSystemOptions.isMediaDirectory(this._path)) {
               encryptFile = this._drmLockedOnCreate;
            } else {
               encryptFile = true;
            }
      }

      if (StringUtilities.strEqualIgnoreCase(this._path, FileSystemEncryption.KEY_FILE, 1701707776)) {
         encryptFile = false;
      }

      boolean endsWith = StringUtilities.endsWithIgnoreCase(this._path, ENCRYPTED_MEDIA_EXTENSION, 1701707776);
      if (encryptFile && !endsWith) {
         this._path = ((StringBuffer)(new Object())).append(this._path).append(ENCRYPTED_MEDIA_EXTENSION).toString();
      } else if (!encryptFile && endsWith) {
         this._path = this._path.substring(0, this._path.length() - ENCRYPTED_MEDIA_EXTENSION.length());
      }

      PosixFileOutputStream fileOut = null;
      boolean createdFileSuccessfully = false;
      boolean var18 = false /* VF: Semaphore variable */;

      try {
         var18 = true;
         fileOut = new PosixFileOutputStream(-1, this._rootId, this._path, this, 0, false, true);
         if (encryptFile) {
            EncryptedFile.createFile(
               fileOut._handle,
               (DataOutputStream)(new Object(fileOut)),
               this._drmLockedOnCreate,
               FileSystemEncryption.getMasterKey(true),
               this._controlledAccessOnCreate
            );
         }

         createdFileSuccessfully = true;
         var18 = false;
      } finally {
         if (var18) {
            if (fileOut != null) {
               label209:
               try {
                  fileOut.close();
               } finally {
                  break label209;
               }
            }

            if (!createdFileSuccessfully) {
               FileSystem.delete(this._rootId, this._path);
            }
         }
      }

      if (fileOut != null) {
         label215:
         try {
            fileOut.close();
         } finally {
            break label215;
         }
      }

      if (!createdFileSuccessfully) {
         FileSystem.delete(this._rootId, this._path);
      }

      FileSystem.addFileJournalEntry(this.getJSRPath(), 0);
   }

   @Override
   public final void mkdir() {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertWritePermission();
      int attributes = this.getFileAttribNoThrow();
      if (attributes != -1) {
         throw new Object(15);
      }

      if (!this._path.endsWith(SLASH)) {
         throw new Object(1002);
      }

      checkStatus(FileSystem.mkdir(this._rootId, this._path));
      FileSystem.addFileJournalEntry(this.getJSRPath(), 0);
   }

   @Override
   public final boolean exists() {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertReadPermission();
      int attributes = this.getFileAttribNoThrow();
      return attributes != -1;
   }

   @Override
   public final boolean isDirectory() {
      if (!this.isOpen()) {
         throw new Object();
      }

      try {
         this.assertReadPermission();
         int attributes = this.getFileAttrib();
         return (attributes & 16) != 0;
      } finally {
         ;
      }
   }

   @Override
   public final void delete() {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertWritePermission();
      int attributes = this.getFileAttrib();
      if ((attributes & 16) != 0) {
         if (!this._path.endsWith(SLASH)) {
            this._path = ((StringBuffer)(new Object())).append(this._path).append('/').toString();
         }

         checkStatus(FileSystem.rmdir(this._rootId, this._path));
      } else {
         this.closeStreams();
         checkStatus(FileSystem.delete(this._rootId, this._path));
      }

      FileSystem.addFileJournalEntry(this.getJSRPath(), 1);
   }

   @Override
   public final int getSupportFlags() {
      return 1;
   }

   @Override
   public final void renameEx(String newName) {
      this.renameInternal(newName, true);
   }

   @Override
   public final void rename(String newName) {
      this.renameInternal(newName, false);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void truncate(long byteOffset) {
      if (!this.isOpen()) {
         throw new Object();
      }

      if (byteOffset < 0) {
         throw new Object("offset is negative");
      }

      this.assertWritePermission();
      if (this._outputStream != null) {
         label71:
         try {
            this._outputStream.flush();
         } finally {
            break label71;
         }
      }

      this.getFileAttrib();
      boolean encrypted = this.isFileEncrypted();
      byteOffset = Math.min(this.fileSizeInternal(encrypted), byteOffset);
      int result;
      if (this._outputStream != null) {
         result = this._outputStream.truncate(byteOffset);
      } else {
         long handleAndStatus = FileSystem.open(this._rootId, this._path, 3);
         checkStatus((int)handleAndStatus);
         int handle = (int)(handleAndStatus >> 32);
         boolean var13 = false /* VF: Semaphore variable */;

         try {
            var13 = true;
            if (encrypted) {
               DataInputStream in = (DataInputStream)(new Object(new PosixFileInputStream(this._rootId, handle)));
               EncryptedFile encryptedFile = EncryptedFile.readHeader(handle, in, FileSystemEncryption.getMasterKey(true));
               result = encryptedFile.truncate(byteOffset);
               in.close();
               var13 = false;
            } else {
               result = FileSystem.truncate(handle, byteOffset);
               var13 = false;
            }
         } finally {
            if (var13) {
               FileSystem.close(handle);
            }
         }

         FileSystem.close(handle);
      }

      checkStatus(result);
   }

   @Override
   public final void setFileConnection(String fileName) {
      if (!this.isOpen()) {
         throw new Object();
      }

      int attributes = this.getFileAttrib();
      if ((attributes & 16) == 0) {
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
            this._path = this.getPlatformPath();
         } else {
            int indexOfSlash = fileName.indexOf(47);
            if (indexOfSlash != -1 && indexOfSlash != fileName.length() - 1) {
               throw new Object("path strucutre contained in fileName");
            }

            boolean isDir = indexOfSlash != -1;
            String newPath = ((StringBuffer)(new Object())).append(this._path).append(fileName).toString();
            int newPathAttribs = -1;
            if (!isDir) {
               boolean encrypted = StringUtilities.endsWithIgnoreCase(newPath, ENCRYPTED_MEDIA_EXTENSION, 1701707776);
               if (this._autoResolveEncryptedFiles && !encrypted) {
                  String tempPath = ((StringBuffer)(new Object())).append(newPath).append(ENCRYPTED_MEDIA_EXTENSION).toString();
                  newPathAttribs = this.getFileAttrib(tempPath, false);
                  if (newPathAttribs != -1) {
                     newPath = tempPath;
                  }
               }
            }

            if (newPathAttribs == -1) {
               newPathAttribs = this.getFileAttrib(newPath, false);
               if (newPathAttribs == -1) {
                  throw new Object("file not found");
               }
            }

            if (isDir && (newPathAttribs & 16) == 0) {
               throw new Object("new path is not a directory");
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
      int attributes = this.getFileAttribNoThrow();
      if (attributes != -1) {
         String tempName = this._fileInfo.getFileName();
         if (tempName != null) {
            path = tempName;
         }

         if ((attributes & 16) != 0) {
            isDir = true;
         }
      }

      boolean endsWithSlash = path.endsWith(SLASH);
      if (attributes == -1) {
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
      StringBuffer str = (StringBuffer)(new Object("/"));
      if (this._rootId == 1) {
         str.append(FILE_SYSTEM_NAME_SDCARD);
      }

      str.append(this.getPlatformPath());
      return str.toString();
   }

   @Override
   public final String getURL() {
      return ((StringBuffer)(new Object("file://"))).append(this._host).append(this.getJSRPath()).toString();
   }

   @Override
   public final void init(String name, int mode, boolean timeouts) {
      int fileSystemId = -1;
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

      if (name.regionMatches(false, rootPos, FILE_SYSTEM_NAME_SDCARD, 0, FILE_SYSTEM_NAME_SDCARD.length())) {
         fileSystemId = 1;
         name = name.substring(rootPos + FILE_SYSTEM_NAME_SDCARD.length());
      }

      if (name.length() == 0) {
         name = SLASH;
      }

      if (fileSystemId == -1 || !FileSystem.isFileSystemSupported(fileSystemId)) {
         throw new Object(4);
      }

      if (!FilenameValidator.validateFilenameAndPath(name)) {
         throw new Object("invalid character in name");
      }

      this._rootId = fileSystemId;
      this._path = name;
      this._opened = true;
      this._accessMode = mode;
      this._fileSystemInfo = (FileSystemInfo)(new Object());
      this._fileInfo = (FileInfo)(new Object());
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final long lastModified() {
      if (!this.isOpen()) {
         throw new Object();
      }

      this.assertReadPermission();
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         this.getFileAttrib();
         var3 = false;
      } finally {
         if (var3) {
            return 0;
         }
      }

      return this._fileInfo.getLastModified();
   }

   @Override
   public final boolean setControlledAccess(CodeSigningKey csk) {
      this.assertWritePermission();
      if (!this.isOpen()) {
         throw new ConnectionClosedException();
      } else if (this.getFileAttribNoThrow() != -1) {
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

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final CodeSigningKey getControlledAccess() {
      this.assertReadPermission();
      if (!this.isOpen()) {
         throw new ConnectionClosedException();
      }

      int attributes = this.getFileAttribNoThrow();
      if (attributes != -1 && (attributes & 16) != 0) {
         throw new Object(1001);
      }

      if (attributes == -1 && this._path.endsWith(SLASH)) {
         throw new Object(1006);
      }

      try {
         if (this.isFileEncryptedInternal()) {
            int caller = TraceBack.getCallingModule(0);
            PosixFileInputStream in = null;
            boolean var34 = false /* VF: Semaphore variable */;

            CodeSigningKey var5;
            label309: {
               try {
                  var34 = true;
                  in = this.openInputStreamInternal(caller, true, false);
                  if (in != null) {
                     EncryptedFile ef = in.getEncryptedFile();
                     if (ef != null) {
                        var5 = ef.getCodeSigningKey();
                        var34 = false;
                        break label309;
                     }

                     var34 = false;
                  } else {
                     var34 = false;
                  }
               } finally {
                  if (var34) {
                     if (in != null) {
                        label261:
                        try {
                           in.close();
                        } finally {
                           break label261;
                        }
                     }
                  }
               }

               if (in != null) {
                  try {
                     in.close();
                  } finally {
                     return this._controlledAccessOnCreate;
                  }
               }

               return this._controlledAccessOnCreate;
            }

            if (in != null) {
               try {
                  in.close();
               } finally {
                  return var5;
               }
            }

            return var5;
         }
      } finally {
         return this._controlledAccessOnCreate;
      }

      return this._controlledAccessOnCreate;
   }

   @Override
   public final void enableDRMForwardLock() {
      this.assertWritePermission();
      if (!this.isOpen()) {
         throw new ConnectionClosedException();
      }

      if (this.getFileAttribNoThrow() != -1) {
         throw new Object(7);
      }

      this._drmLockedOnCreate = true;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final boolean isContentDRMForwardLocked() {
      this.assertReadPermission();
      if (!this.isOpen()) {
         throw new ConnectionClosedException();
      }

      int caller = TraceBack.getCallingModule(0);
      PosixFileInputStream in = null;
      boolean var38 = false /* VF: Semaphore variable */;
      boolean var45 = false /* VF: Semaphore variable */;

      label333: {
         boolean var4;
         label334: {
            try {
               try {
                  var45 = true;
                  var38 = true;
                  if (!this.isFileEncrypted()) {
                     var38 = false;
                     var45 = false;
                     break label333;
                  }

                  in = this.openInputStreamInternal(caller, true, false);
                  if (in == null) {
                     var38 = false;
                     var45 = false;
                     break label333;
                  }

                  EncryptedFile ef = in.getEncryptedFile();
                  if (ef == null) {
                     var38 = false;
                     var45 = false;
                     break label333;
                  }

                  var4 = ef.isDrmForwardLocked();
                  var38 = false;
                  var45 = false;
               } finally {
                  if (var45) {
                     var4 = true;
                     var38 = false;
                     break label334;
                  }
               }
            } finally {
               if (var38) {
                  if (in != null) {
                     label299:
                     try {
                        in.close();
                     } finally {
                        break label299;
                     }
                  }
               }
            }

            if (in != null) {
               try {
                  in.close();
               } finally {
                  return var4;
               }
            }

            return var4;
         }

         if (in != null) {
            try {
               in.close();
            } finally {
               return var4;
            }
         }

         return var4;
      }

      if (in != null) {
         try {
            in.close();
            return false;
         } finally {
            ;
         }
      } else {
         return false;
      }
   }

   @Override
   public final boolean isFileEncrypted() {
      this.assertReadPermission();
      if (!this.isOpen()) {
         throw new ConnectionClosedException();
      } else {
         int attributes = this.getFileAttrib();
         if ((attributes & 16) == 0 && !this._path.endsWith(SLASH)) {
            return this.isFileEncryptedInternal();
         } else {
            throw new Object(1001);
         }
      }
   }

   @Override
   public final InputStream openRawInputStream() {
      int caller = TraceBack.getCallingModule(0);
      return this.openInputStreamInternal(caller, false, true);
   }

   @Override
   public final boolean isContentBuiltIn() {
      this.assertReadPermission();
      if (!this.isOpen()) {
         throw new ConnectionClosedException();
      } else {
         int attributes = this.getFileAttrib();
         if ((attributes & 16) != 0) {
            throw new Object(1001);
         } else {
            return false;
         }
      }
   }

   @Override
   public final void setAutoEncryptionResolveMode(boolean mode) {
      if (!this.isOpen()) {
         throw new ConnectionClosedException();
      }

      this._autoResolveEncryptedFiles = mode;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final long fileSizeInternal(boolean encrypted) {
      if (encrypted) {
         int caller = TraceBack.getCallingModule(0);
         PosixFileInputStream in = null;
         boolean var26 = false /* VF: Semaphore variable */;

         long var5;
         label190: {
            try {
               var26 = true;
               in = this.openInputStreamInternal(caller, true, false);
               if (in != null) {
                  EncryptedFile ef = in.getEncryptedFile();
                  if (ef != null) {
                     var5 = ef.getFileSize();
                     var26 = false;
                     break label190;
                  }

                  var26 = false;
               } else {
                  var26 = false;
               }
            } finally {
               if (var26) {
                  if (in != null) {
                     label155:
                     try {
                        in.close();
                     } finally {
                        break label155;
                     }
                  }
               }
            }

            if (in != null) {
               try {
                  in.close();
               } finally {
                  return this._fileInfo.getFileSize();
               }
            }

            return this._fileInfo.getFileSize();
         }

         if (in != null) {
            try {
               in.close();
            } finally {
               return var5;
            }
         }

         return var5;
      } else {
         return this._fileInfo.getFileSize();
      }
   }

   static final void checkStatus(int status) {
      if (status != 0) {
         throw new Object(status);
      }
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

   private final int getFileAttribNoThrow() {
      try {
         return this.getFileAttrib(false);
      } finally {
         ;
      }
   }

   private final int getFileAttrib() {
      return this.getFileAttrib(true);
   }

   private final int getFileAttrib(boolean throwIO) {
      boolean encrypted = StringUtilities.endsWithIgnoreCase(this._path, ENCRYPTED_MEDIA_EXTENSION, 1701707776);
      if (this._autoResolveEncryptedFiles) {
         if (encrypted) {
            int result = this.getFileAttrib(this._path, false);
            if (result != -1) {
               return result;
            }

            String tempPath = this._path.substring(0, this._path.length() - ENCRYPTED_MEDIA_EXTENSION.length());
            result = this.getFileAttrib(tempPath, throwIO);
            if (result != -1) {
               this._path = tempPath;
            }

            return result;
         }

         String tempPath = ((StringBuffer)(new Object())).append(this._path).append(ENCRYPTED_MEDIA_EXTENSION).toString();
         int result = this.getFileAttrib(tempPath, false);
         if (result != -1) {
            this._path = tempPath;
            return result;
         }
      }

      return this.getFileAttrib(this._path, throwIO);
   }

   private final int getFileAttrib(String path, boolean throwIO) {
      if (path.equals(SLASH)) {
         return 16;
      }

      int status = FileSystem.getFileInfo(this._rootId, path, this._fileInfo);
      if (throwIO) {
         checkStatus(status);
      } else if (status != 0) {
         return -1;
      }

      return this._fileInfo.getAttributes();
   }

   private final void renameInternal(String newName, boolean isPath) {
      if (!this.isOpen()) {
         throw new Object();
      }

      if (newName == null) {
         throw new Object();
      }

      this.assertWritePermission();
      this.getFileAttrib();
      newName = URIDecoder.decode(newName, "UTF-8", false);
      if (!isPath && newName.indexOf(47) != -1) {
         throw new Object("new name contains path");
      }

      if (!FilenameValidator.validateFilenameAndPath(newName)) {
         throw new Object(1004);
      }

      if (!isPath) {
         newName = ((StringBuffer)(new Object())).append(this.getPlatformPath()).append(newName).toString();
      }

      if (StringUtilities.endsWithIgnoreCase(this._path, ENCRYPTED_MEDIA_EXTENSION, 1701707776)
         && !StringUtilities.endsWithIgnoreCase(newName, ENCRYPTED_MEDIA_EXTENSION, 1701707776)) {
         newName = ((StringBuffer)(new Object())).append(newName).append(ENCRYPTED_MEDIA_EXTENSION).toString();
      }

      this.closeStreams();
      checkStatus(FileSystem.rename(this._rootId, this._path, newName));
      String oldJSRPath = this.getJSRPath();
      this._path = newName;
      FileSystem.addFileJournalEntry(this.getJSRPath(), oldJSRPath, 3);
   }

   private final long directorySizeInternal(String path, boolean includeDirs) {
      long totalSize = FileSystem.directorySize(this._rootId, path);
      if (includeDirs) {
         Enumeration entries = this.listInternal(path, null, true, false);

         while (entries.hasMoreElements()) {
            String subPath = (String)entries.nextElement();
            if (subPath.endsWith(SLASH)) {
               totalSize += this.directorySizeInternal(((StringBuffer)(new Object())).append(path).append(subPath).toString(), includeDirs);
            }
         }
      }

      return totalSize;
   }

   private final PosixFileInputStream openInputStreamInternal(int callerModule, boolean unwrapData, boolean setInputStream) {
      this.assertReadPermission();
      if (!this.isOpen()) {
         throw new ConnectionClosedException();
      }

      if (setInputStream && this._inputStream != null) {
         throw new Object(1005);
      }

      int attributes = this.getFileAttrib();
      if ((attributes & 16) != 0) {
         throw new Object(1001);
      }

      PosixFileInputStream inputStream = new PosixFileInputStream(callerModule, this._rootId, this._path, this, this._autoResolveEncryptedFiles, unwrapData);
      if (setInputStream) {
         this._inputStream = inputStream;
      }

      return inputStream;
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

   private final boolean isFileEncryptedInternal() {
      return StringUtilities.endsWithIgnoreCase(this._path, ENCRYPTED_MEDIA_EXTENSION, 1701707776);
   }

   private final OutputStream openOutputStreamInternal(long offset, int callerModule) {
      this.assertWritePermission();
      if (offset < 0) {
         throw new Object("negative offset");
      }

      if (!this.isOpen()) {
         throw new ConnectionClosedException();
      }

      if (this._outputStream != null) {
         throw new Object(1005);
      }

      this.getFileAttrib();
      if ((this._fileInfo.getAttributes() & 16) != 0) {
         throw new Object(1001);
      }

      offset = Math.min(offset, this.fileSizeInternal(this.isFileEncryptedInternal()));
      this._outputStream = new PosixFileOutputStream(callerModule, this._rootId, this._path, this, offset, this._autoResolveEncryptedFiles, false);
      return this._outputStream;
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
               return this.listInternal(this._path, filter, includeHidden, returnFileInfo);
            }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final Enumeration listInternal(String path, String filter, boolean includeHidden, boolean returnFileInfo) {
      int attributes = this.getFileAttrib();
      if ((attributes & 16) == 0) {
         throw new Object(1002);
      }

      if (!path.endsWith(SLASH)) {
         path = ((StringBuffer)(new Object())).append(path).append('/').toString();
      }

      if (filter != null && filter.length() > 0) {
         filter = ((StringBuffer)(new Object())).append(path).append(filter).toString();
      } else {
         filter = ((StringBuffer)(new Object())).append(path).append('*').toString();
      }

      Vector elements = (Vector)(new Object());
      boolean first = true;
      int handle = -1;

      while (true) {
         boolean var14 = false /* VF: Semaphore variable */;

         try {
            var14 = true;
            FileInfo fileInfo;
            if (returnFileInfo) {
               fileInfo = (FileInfo)(new Object());
            } else {
               fileInfo = this._fileInfo;
            }

            if (first) {
               first = false;
               long result = FileSystem.findFirst(this._rootId, filter, fileInfo);
               if ((int)result != 0) {
                  var14 = false;
                  break;
               }

               handle = (int)(result >> 32);
            } else if (FileSystem.findNext(this._rootId, handle, fileInfo) != 0) {
               var14 = false;
               break;
            }

            int fileAttrib = fileInfo.getAttributes();
            if (includeHidden || (fileAttrib & 8) == 0) {
               String name = fileInfo.getFileName();
               if (name != null && !name.equals(DOT) && !name.equals(DOT_DOT)) {
                  if (returnFileInfo) {
                     elements.addElement(fileInfo);
                  } else if ((fileAttrib & 16) != 0) {
                     elements.addElement(((StringBuffer)(new Object())).append(fileInfo.getFileName()).append('/').toString());
                  } else {
                     elements.addElement(fileInfo.getFileName());
                  }
               }
            }
         } finally {
            if (var14) {
               if (handle != -1) {
                  FileSystem.findClose(handle);
               }
            }
         }
      }

      if (handle != -1) {
         FileSystem.findClose(handle);
      }

      return elements.elements();
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
}
