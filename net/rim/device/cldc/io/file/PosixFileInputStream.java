package net.rim.device.cldc.io.file;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import net.rim.device.api.io.FileInfo;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.io.file.EncryptedFile;
import net.rim.device.internal.io.file.FileHandleProvider;
import net.rim.device.internal.io.file.FileSystem;
import net.rim.device.internal.io.file.FileSystemInfo;
import net.rim.vm.Process;
import net.rim.vm.WeakReference;

final class PosixFileInputStream extends InputStream implements FileHandleProvider {
   private int _handle;
   private int _rootId;
   private long _savePos;
   private byte[] _buffer;
   private int _offset;
   private int _available;
   private boolean _eof;
   private boolean _closeHandle;
   private Runnable _cleanupRunnable;
   private PosixFileConnection _connection;
   private EncryptedFile _encryptedFile;
   private FileInfo _fileInfo;
   private WeakReference _process;

   public PosixFileInputStream(int codeModuleCaller, int fs, String fileName, PosixFileConnection connection, boolean autoResolveEncrypted, boolean unwrapData) throws IOException {
      this._rootId = fs;
      this._offset = 0;
      this._available = 0;
      this._connection = connection;
      long handleAndStatus = -1;
      boolean encrypted = StringUtilities.endsWithIgnoreCase(fileName, PosixFileConnection.ENCRYPTED_MEDIA_EXTENSION, 1701707776);
      if (autoResolveEncrypted && !encrypted) {
         handleAndStatus = FileSystem.open(
            fs, ((StringBuffer)(new Object())).append(fileName).append(PosixFileConnection.ENCRYPTED_MEDIA_EXTENSION).toString(), 1
         );
         if ((int)handleAndStatus == 0) {
            encrypted = true;
         }
      }

      if ((int)handleAndStatus != 0) {
         handleAndStatus = FileSystem.open(fs, fileName, 1);
      }

      PosixFileConnection.checkStatus((int)handleAndStatus);
      this._handle = (int)(handleAndStatus >> 32);
      this._closeHandle = true;
      FileSystem.registerInputStream(this._rootId, this);
      this._cleanupRunnable = new PosixFileInputStream$FileInputStreamCleanupRunnable(this);
      ApplicationProcess process = (ApplicationProcess)Process.currentProcess();
      if (process != null) {
         this._process = (WeakReference)(new Object(process));
         process.addCleanupRunnable(this._cleanupRunnable);
      }

      if (encrypted && unwrapData) {
         this._buffer = new byte[8192];

         try {
            this._encryptedFile = EncryptedFile.readHeader(this._handle, (DataInputStream)(new Object(this)), FileSystemEncryption.getMasterKey(false));
            CodeSigningKey key = this._encryptedFile.getCodeSigningKey();
            if (codeModuleCaller != -1 && key != null && !ControlledAccess.verifyCodeModuleSignature(codeModuleCaller, key)) {
               throw new Object(key);
            }

            this._eof = false;
            this._offset = 0;
            this._available = 0;
         } finally {
            label91:
            try {
               this.close();
            } finally {
               break label91;
            }
         }
      } else {
         this._buffer = new byte[FileSystem.getMaxReadSize(this._rootId)];
      }
   }

   public PosixFileInputStream(int fs, int fileHandle) {
      FileSystemInfo fsInfo = (FileSystemInfo)(new Object());
      PosixFileConnection.checkStatus(FileSystem.getFileSystemInfo(fs, fsInfo));
      this._buffer = new byte[fsInfo.getMaxReadLength()];
      this._offset = 0;
      this._available = 0;
      this._handle = fileHandle;
      this._closeHandle = false;
   }

   @Override
   public final synchronized int available() {
      return this._available;
   }

   @Override
   public final synchronized int read() {
      if (this._handle == -1) {
         throw new Object(1000);
      }

      if (this._available == 0) {
         this.doRead();
      }

      if (this._available == 0) {
         return -1;
      }

      this._available--;
      return this._buffer[this._offset++] & 0xFF;
   }

   @Override
   public final synchronized int read(byte[] b, int off, int len) {
      if (off >= 0 && len >= 0 && b.length - len >= off) {
         if (this._handle == -1) {
            throw new Object(1000);
         }

         int bytesCopied = 0;

         while (bytesCopied < len) {
            if (this._available == 0) {
               this.doRead();
               if (this._available == 0) {
                  if (bytesCopied == 0) {
                     return -1;
                  }

                  return bytesCopied;
               }
            }

            int bytesToCopy = Math.min(this._available, len - bytesCopied);
            System.arraycopy(this._buffer, this._offset, b, off + bytesCopied, bytesToCopy);
            bytesCopied += bytesToCopy;
            this._available -= bytesToCopy;
            this._offset += bytesToCopy;
         }

         return bytesCopied;
      } else {
         throw new Object();
      }
   }

   final EncryptedFile getEncryptedFile() {
      return this._encryptedFile;
   }

   @Override
   public final synchronized void close() {
      this.closeInternal(true);
   }

   private final void closeInternal(boolean removeRunnable) {
      if (this._handle != -1) {
         if (this._closeHandle) {
            this._connection.inputClosed();
            FileSystem.deregisterInputStream(this._rootId, this);
            FileSystem.close(this._handle);
            if (removeRunnable) {
               ApplicationProcess process = this.getProcess();
               if (process != null) {
                  process.removeCleanupRunnable(this._cleanupRunnable);
               }

               this._cleanupRunnable = null;
            }
         }

         this._handle = -1;
      }
   }

   private final void doRead() {
      if (!this._eof) {
         long result;
         if (this._encryptedFile != null) {
            result = this._encryptedFile.readBlocks(this._buffer);
         } else {
            result = FileSystem.read(this._handle, this._buffer);
         }

         PosixFileConnection.checkStatus((int)result);
         this._offset = 0;
         this._available = (int)(result >> 32);
         if (this._available != this._buffer.length) {
            this._eof = true;
         }
      }
   }

   @Override
   public final boolean markSupported() {
      return true;
   }

   @Override
   public final synchronized void mark(int readLimit) {
      long additionalOffset = 0;
      if (this._encryptedFile != null) {
         additionalOffset = this._encryptedFile.getHeaderLength();
      }

      this._savePos = this.getPosition() - additionalOffset;
   }

   public final synchronized long getPosition() {
      return FileSystem.tell(this._handle) - this._available;
   }

   @Override
   public final synchronized void reset() {
      if (this._handle == -1) {
         throw new Object(1000);
      }

      long additionalOffset = 0;
      if (this._encryptedFile != null) {
         additionalOffset = this._encryptedFile.getHeaderLength();
      }

      PosixFileConnection.checkStatus(FileSystem.seek(this._handle, this._savePos + additionalOffset, 0));
      this._savePos = 0;
      this._eof = false;
      this._offset = 0;
      this._available = 0;
   }

   @Override
   public final synchronized long skip(long nBytes) {
      if (this._handle == -1) {
         throw new Object(1000);
      }

      if (nBytes <= 0) {
         return 0;
      }

      long skipped = 0;
      if (this._available > 0) {
         skipped = Math.min(nBytes, this._available);
         nBytes -= skipped;
         this._available = (int)(this._available - skipped);
         this._offset = (int)(this._offset + skipped);
      }

      if (nBytes > 0 && !this._eof) {
         if (this._fileInfo == null) {
            this._fileInfo = (FileInfo)(new Object());
         }

         PosixFileConnection.checkStatus(FileSystem.getFileInfo(this._handle, this._fileInfo));
         long bytesLeft = this._fileInfo.getFileSize() - this.getPosition();
         if (bytesLeft > 0) {
            if (nBytes >= bytesLeft) {
               nBytes = bytesLeft - 1;
               bytesLeft = 1;
            } else {
               bytesLeft = 0;
            }

            if (this._encryptedFile != null) {
               long blockBytes = nBytes / 16 * 16;
               if (blockBytes > 0) {
                  PosixFileConnection.checkStatus(FileSystem.seek(this._handle, blockBytes, 1));
               }

               return super.skip(nBytes - blockBytes + bytesLeft) + skipped + blockBytes;
            }

            if (nBytes > 0) {
               PosixFileConnection.checkStatus(FileSystem.seek(this._handle, nBytes, 1));
               skipped += nBytes;
            }

            if (bytesLeft == 1 && this.read() != -1) {
               skipped += 1;
            }
         }
      }

      return skipped;
   }

   @Override
   public final int getFileHandle() {
      return this._handle;
   }

   @Override
   public final boolean isInputCiphering() {
      return this._encryptedFile != null;
   }

   private final ApplicationProcess getProcess() {
      if (this._process != null) {
         Object process = this._process.get();
         if (process instanceof Object) {
            return (ApplicationProcess)process;
         }
      }

      return null;
   }
}
