package net.rim.device.cldc.io.file;

import java.io.DataInputStream;
import java.io.OutputStream;
import net.rim.device.api.io.file.FileIOException;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.io.file.EncryptedFile;
import net.rim.device.internal.io.file.FileHandleProvider;
import net.rim.device.internal.io.file.FileSystem;
import net.rim.vm.Process;

final class PosixFileOutputStream extends OutputStream implements FileHandleProvider {
   int _handle;
   private int _rootId;
   private byte[] _buffer;
   private int _offset;
   private int _available;
   private Runnable _cleanupRunnable;
   private PosixFileConnection _connection;
   private EncryptedFile _encryptedFile;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public PosixFileOutputStream(
      int callerModule, int fs, String fileName, PosixFileConnection connection, long startPos, boolean autoResolveEncryptedFiles, boolean createFile
   ) throws FileIOException {
      this._rootId = fs;
      this._buffer = new byte[FileSystem.getMaxWriteSize(this._rootId)];
      this._offset = 0;
      this._available = this._buffer.length;
      this._connection = connection;
      int mode;
      if (createFile) {
         mode = 7;
      } else {
         mode = 3;
      }

      long handleAndStatus = FileSystem.open(fs, fileName, mode);
      PosixFileConnection.checkStatus((int)handleAndStatus);
      this._handle = (int)(handleAndStatus >> 32);
      FileSystem.registerOutputStream(this._rootId, this);
      this._cleanupRunnable = new PosixFileOutputStream$FileOutputStreamCleanupRunnable(this);
      ((ApplicationProcess)Process.currentProcess()).addCleanupRunnable(this._cleanupRunnable);
      if (autoResolveEncryptedFiles && StringUtilities.endsWithIgnoreCase(fileName, PosixFileConnection.ENCRYPTED_MEDIA_EXTENSION, 1701707776)) {
         DataInputStream in = new DataInputStream(new PosixFileInputStream(fs, this._handle));
         boolean var30 = false /* VF: Semaphore variable */;

         try {
            var30 = true;
            this._encryptedFile = EncryptedFile.readHeader(this._handle, in, FileSystemEncryption.getMasterKey(true));
            CodeSigningKey key = this._encryptedFile.getCodeSigningKey();
            if (callerModule != -1) {
               if (key != null) {
                  if (!ControlledAccess.verifyCodeModuleSignature(callerModule, key)) {
                     throw new ControlledAccessException(key);
                  }

                  var30 = false;
               } else {
                  var30 = false;
               }
            } else {
               var30 = false;
            }
         } finally {
            if (var30) {
               label164:
               try {
                  in.close();
               } finally {
                  break label164;
               }
            }
         }

         label170:
         try {
            in.close();
         } finally {
            break label170;
         }
      }

      if (startPos != 0) {
         if (this._encryptedFile == null) {
            PosixFileConnection.checkStatus(FileSystem.seek(this._handle, startPos, 1));
         } else {
            long blockBytes = startPos / 16 * 16;
            if (blockBytes > 0) {
               PosixFileConnection.checkStatus(FileSystem.seek(this._handle, blockBytes, 1));
            }

            int remaining = (int)(startPos - blockBytes);
            if (remaining > 0) {
               byte[] block = new byte[16];
               long filePos = FileSystem.tell(this._handle);
               if (filePos == -1) {
                  throw new FileIOException(6);
               }

               long status = this._encryptedFile.readBlocks(block);
               PosixFileConnection.checkStatus(FileSystem.seek(this._handle, filePos, 0));
               PosixFileConnection.checkStatus((int)status);
               System.arraycopy(block, 0, this._buffer, 0, block.length);
               int remainingBytes = (int)(status >> 32);
               if (remaining > remainingBytes) {
                  remaining = remainingBytes;
               }

               this._offset = remaining;
               this._available -= remaining;
               return;
            }
         }
      }
   }

   @Override
   public final synchronized void write(int b) throws FileIOException {
      if (this._handle == -1) {
         throw new FileIOException(1000);
      }

      if (this._available == 0) {
         this.flush(false);
      }

      this._available--;
      this._buffer[this._offset++] = (byte)b;
   }

   @Override
   public final synchronized void write(byte[] b, int off, int len) throws FileIOException {
      if (off >= 0 && len >= 0 && b.length - len >= off) {
         if (this._handle == -1) {
            throw new FileIOException(1000);
         }

         int bytesCopied = 0;

         while (bytesCopied < len) {
            if (this._available == 0) {
               this.flush(false);
            }

            int bytesToCopy = Math.min(this._available, len - bytesCopied);
            System.arraycopy(b, off + bytesCopied, this._buffer, this._offset, bytesToCopy);
            bytesCopied += bytesToCopy;
            this._available -= bytesToCopy;
            this._offset += bytesToCopy;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final synchronized void flush() {
      this.flush(true);
   }

   private final void flush(boolean commit) throws FileIOException {
      if (this._handle == -1) {
         throw new FileIOException(1000);
      }

      int bytesToWrite = this._buffer.length - this._available;
      if (bytesToWrite == 0) {
         if (commit) {
            PosixFileConnection.checkStatus(FileSystem.commit(this._rootId, this._handle));
         }
      } else if (this._encryptedFile == null) {
         PosixFileConnection.checkStatus(FileSystem.write(this._handle, this._buffer, bytesToWrite));
         if (commit) {
            PosixFileConnection.checkStatus(FileSystem.commit(this._rootId, this._handle));
         }

         this._offset = 0;
         this._available = this._buffer.length;
      } else {
         int offset = 0;
         if (bytesToWrite >= 16) {
            offset = bytesToWrite / 16 * 16;
            PosixFileConnection.checkStatus(this._encryptedFile.writeBlocks(this._buffer, offset, 16));
            bytesToWrite -= offset;
         }

         if (bytesToWrite > 0) {
            byte[] block = new byte[16];
            long filePos = FileSystem.tell(this._handle);
            if (filePos == -1) {
               throw new FileIOException(6);
            }

            long status = this._encryptedFile.readBlocks(block);
            PosixFileConnection.checkStatus(FileSystem.seek(this._handle, filePos, 0));
            PosixFileConnection.checkStatus((int)status);
            System.arraycopy(this._buffer, offset, block, 0, bytesToWrite);
            int bytesInLastBlock = Math.max((int)(status >> 32), bytesToWrite);
            PosixFileConnection.checkStatus(this._encryptedFile.writeBlocks(block, block.length, bytesInLastBlock));
            PosixFileConnection.checkStatus(FileSystem.seek(this._handle, filePos, 0));
         }

         System.arraycopy(this._buffer, offset, this._buffer, 0, bytesToWrite);
         if (commit) {
            PosixFileConnection.checkStatus(FileSystem.commit(this._rootId, this._handle));
         }

         this._offset = bytesToWrite;
         this._available = this._buffer.length - bytesToWrite;
      }
   }

   final int truncate(long byteOffset) {
      return this._encryptedFile != null ? this._encryptedFile.truncate(byteOffset) : FileSystem.truncate(this._handle, byteOffset);
   }

   @Override
   public final int getFileHandle() {
      return this._handle;
   }

   @Override
   public final boolean isInputCiphering() {
      return this._encryptedFile != null;
   }

   @Override
   public final synchronized void close() {
      this.closeInternal(true);
   }

   private final void closeInternal(boolean removeRunnable) {
      if (this._handle != -1) {
         label24:
         try {
            this.flush();
         } finally {
            break label24;
         }

         this._connection.outputClosed();
         FileSystem.deregisterOutputStream(this._rootId, this);
         FileSystem.close(this._handle);
         FileSystem.addFileJournalEntry(this._connection.getJSRPath(), 2);
         this._handle = -1;
         if (removeRunnable) {
            ((ApplicationProcess)Process.currentProcess()).removeCleanupRunnable(this._cleanupRunnable);
         }
      }
   }
}
